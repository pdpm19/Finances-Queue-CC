package Finances;

import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*
    MAX CLOCK = 28.800
    INFINITY = 10.000.000
*/

public class Finances extends Functions {

    // Main function
    public static void main(String[] args) throws InterruptedException {
        int clock = 0, n_clients = 0, aux = 0, max_tte = 0, min_tte = 1000000,
                tte_q1 = 0, tte_q2 = 0, tte_q3 = 0, tte_q4 = 0,
                real_n_clients_q1 = 0, real_n_clients_q2 = 0, real_n_clients_q3 = 0, real_n_clients_q4 = 0,     // Real number of clients attended
                a_clients = 0, b_clients = 0, c_clients = 0, direct = 0,               // Total number of clients for every desk
                a1_clients = 0, a2_clients = 0, b1_clients = 0, b2_clients = 0, treasury_clients = 0, cc_clients = 0,
                a_q1 = 0, a_q2 = 0, a_q3 = 0, a_q4 = 0,                           // A clients in every quarter
                b_q1 = 0, b_q2 = 0, b_q3 = 0, b_q4 = 0,
                c_q1 = 0, c_q2 = 0, c_q3 = 0, c_q4 = 0,
                direct_q1 = 0, direct_q2 = 0, direct_q3 = 0, direct_q4 = 0,
                q_waiting = 0, p2_waiting = 0, r2_waiting = 0, g2_waiting = 0,      // Waiting time on every queue
                p3_waiting = 0, r3_waiting = 0, g3_waiting = 0,
                n_q_waiting = 0, n_p2_waiting = 0, n_r2_waiting = 0, n_g2_waiting = 0,      // Number of clients on Waiting time on every queue
                n_p3_waiting = 0, n_r3_waiting = 0, n_g3_waiting = 0;

        // Output file of the simulation
        try {
            File myObj = new File("simulation.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        // Clients and Queues
        ArrayList<Client> raw_clients = new ArrayList<>();          // Clients in raw mode
        ArrayList<Client> clients = new ArrayList<>();              // Clients ordered by t_arrive
        ArrayList<Client> queue_1 = new ArrayList<>();              // Clients Sorting (stage 1)
        ArrayList<Client> p_queue_2 = new ArrayList<>();            // Priority Clients stage 2
        ArrayList<Client> g_queue_2 = new ArrayList<>();            // General Clients stage 2
        ArrayList<Client> p_queue_3 = new ArrayList<>();            // Priority Clients stage 3
        ArrayList<Client> g_queue_3 = new ArrayList<>();            // Priority Clients stage 3
        ArrayList<Client> r_queue_2 = new ArrayList<>();            // Non-Priority Clients stage 2
        ArrayList<Client> r_queue_3 = new ArrayList<>();            // Non-Priority Clients stage 3

        // Desks
        Desk sorting = new Desk();
        Desk a1 = new Desk();
        Desk a2 = new Desk();
        Desk b1 = new Desk();
        Desk b2 = new Desk();
        Desk c = new Desk();
        Desk treasury = new Desk();


        // Generate the number of clients, [120-150]
        while (n_clients < 120 || n_clients > 150)
            n_clients = (int) (Math.random() * 1000);

        // Generate the clients data
        clientGenerator(raw_clients, n_clients);

        // Order the clients by Arrive Time
        while (aux <= 28800) {
            for (int i = 0; i < n_clients; i++) {
                if (aux == raw_clients.get(i).getArriveTime())
                    clients.add(raw_clients.get(i));
            }
            aux += 1;
        }
        System.out.println(clients);

        // Total number of A, B, C and Direct clients in clients list
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getDesk() == 'A')
                a_clients++;
            else if (clients.get(i).getDesk() == 'B')
                b_clients++;
            else if (clients.get(i).getDesk() == 'C')
                c_clients++;
            else
                direct++;
        }

        int nextArrive = clients.get(0).getArriveTime();


        // Functional Finances
        while (clock < 28800 && clock >= 0) {

            /*
                SORTING PHASE
            */
            // Client goes to the next phase
            //   System.out.println("clock:"+clock);

            if (sorting.getBusyTime() == clock) {
                if (clock - (sorting.getClient().getArriveTime() + sorting.getClient().getSortingTime()) > 0) {
                    q_waiting += clock - (sorting.getClient().getArriveTime() + sorting.getClient().getSortingTime());
                    sorting.getClient().setWaiting(clock - (sorting.getClient().getArriveTime() + sorting.getClient().getSortingTime()));
                    n_q_waiting++;
                }

                if (sorting.getClient().getPriority() == 1 && sorting.getClient().getDirectTreasury() != 1)
                    p_queue_2.add(sorting.getClient());
                else if (sorting.getClient().getPriority() == 0 && sorting.getClient().getDirectTreasury() != 1)
                    g_queue_2.add(sorting.getClient());
                else if (sorting.getClient().getPriority() == 1 && sorting.getClient().getDirectTreasury() == 1)
                    p_queue_3.add(sorting.getClient());
                else
                    g_queue_3.add(sorting.getClient());


                // Check if there is someone in the first queue
                if (!queue_1.isEmpty()) {
                    sorting.setState(1);
                    sorting.setBusyTime(clock + queue_1.get(0).getSortingTime());
                    sorting.setClient(queue_1.get(0));
                    queue_1.remove(0);
                }
                // Sets Sorting to IDLE
                else {
                    sorting.setBusyTime(10000000);
                    sorting.setState(0);
                }
            }

            // Client arrives
            if (nextArrive == clock) {
                if (sorting.getState() == 0) {
                    sorting.setState(1);
                    sorting.setBusyTime(clock + clients.get(0).getSortingTime());
                    sorting.setClient(clients.get(0));
                    clients.remove(0);
                } else {
                    queue_1.add(clients.get(0));
                    clients.remove(0);
                }

            }
            // Next Arrive calculation
            if (clients.isEmpty())
                nextArrive = 10000000;
            else
                nextArrive = clients.get(0).getArriveTime();

            /*
                DESK PHASE
             */
            // A1
            // Client exits the A1
            if (a1.getBusyTime() == clock) {
                a1.setState(0);
                a1.setBusyTime(10000000);
                a1_clients++;
                // Waiting time on queues
                if (clock - (a1.getClient().getArriveTime() + a1.getClient().getSortingTime() + a1.getClient().getWaiting() + a1.getClient().getDeskTime()) > 0) {
                    if (a1.getClient().getPriority() == 1 && a1.getClient().getRepeat() != -1) {
                        p2_waiting += (clock - (a1.getClient().getArriveTime() + a1.getClient().getSortingTime() + a1.getClient().getWaiting() + a1.getClient().getDeskTime()));
                        n_p2_waiting++;
                    } else if (a1.getClient().getPriority() == 0 && a1.getClient().getRepeat() != -1) {
                        g2_waiting += (clock - (a1.getClient().getArriveTime() + a1.getClient().getSortingTime() + a1.getClient().getWaiting() + a1.getClient().getDeskTime()));
                        n_g2_waiting++;
                    } else {
                        r2_waiting += (clock - (a1.getClient().getArriveTime() + a1.getClient().getSortingTime() + a1.getClient().getWaiting() + a1.getClient().getDeskTime()));
                        n_r2_waiting++;
                    }

                    a1.getClient().setWaiting(a1.getClient().getWaiting() + (clock - (a1.getClient().getArriveTime() + a1.getClient().getSortingTime() + a1.getClient().getWaiting() + a1.getClient().getDeskTime())));
                }
                if (a1.getClient().getTreasury() == 1) {         // Client needs to pass through Treasury

                    if (a1.getClient().getPriority() == 1 && a1.getClient().getRepeat() == 0) {
                        p_queue_3.add(a1.getClient());
                    } else if (a1.getClient().getPriority() == 0 && a1.getClient().getRepeat() == 0) {
                        g_queue_3.add(a1.getClient());
                    } else {
                        r_queue_3.add(a1.getClient());
                        a1_clients--;
                    }

                } else {                                            // Clients exits the system
                    System.out.println("Client exits the system " + a1.getClient());
                    if (a1.getClient().getArriveTime() <= 7200) {
                        a_q1++;
                        real_n_clients_q1++;
                        tte_q1 += a1.getClient().getWaiting();
                    } else if (a1.getClient().getArriveTime() > 7200 && a1.getClient().getArriveTime() <= 14400) {
                        a_q2++;
                        real_n_clients_q2++;
                        tte_q2 += a1.getClient().getWaiting();
                    } else if (a1.getClient().getArriveTime() > 14400 && a1.getClient().getArriveTime() <= 21600) {
                        a_q3++;
                        real_n_clients_q3++;
                        tte_q3 += a1.getClient().getWaiting();
                    } else {
                        a_q4++;
                        real_n_clients_q4++;
                        tte_q4 += a1.getClient().getWaiting();
                    }
                    max_tte = maxTTE(max_tte, a1.getClient().getWaiting());
                    min_tte = minTTE(min_tte, a1.getClient().getWaiting());}
            }
            // A1 is IDLE
            if (a1.getState() == 0) {
                if (!p_queue_2.isEmpty()) {
                    for (int i = 0; i < p_queue_2.size(); i++) {
                        if (p_queue_2.get(i).getDesk() == 'A') {
                            if (p_queue_2.get(i).getRepeat() == 1) {
                                a1.setState(1);
                                int old_time = p_queue_2.get(i).getDeskTime();
                                p_queue_2.get(i).setDeskTime(generator(0, p_queue_2.get(i).getDeskTime()));
                                a1.setBusyTime(clock + p_queue_2.get(i).getDeskTime() - old_time);
                                a1.setClient(p_queue_2.get(i));
                                p_queue_2.remove(i);
                            } else {
                                a1.setState(1);
                                a1.setBusyTime(clock + p_queue_2.get(i).getDeskTime());
                                a1.setClient(p_queue_2.get(i));
                                p_queue_2.remove(i);
                            }
                            break;
                        }
                    }
                } else if (!r_queue_2.isEmpty()) {
                    for (int i = 0; i < r_queue_2.size(); i++) {
                        if (r_queue_2.get(i).getDesk() == 'A') {
                            a1.setState(1);
                            a1.setBusyTime(clock + r_queue_2.get(i).getDeskTime());
                            a1.setClient(r_queue_2.get(i));
                            r_queue_2.remove(i);
                            break;
                        }
                    }
                } else if (!g_queue_2.isEmpty()) {
                    for (int i = 0; i < g_queue_2.size(); i++) {
                        if (g_queue_2.get(i).getDesk() == 'A') {
                            if (g_queue_2.get(i).getRepeat() == 1) {
                                a1.setState(1);
                                int old_time = g_queue_2.get(i).getDeskTime();
                                g_queue_2.get(i).setDeskTime(generator(0, g_queue_2.get(i).getDeskTime()));
                                a1.setBusyTime(clock + g_queue_2.get(i).getDeskTime() - old_time);
                                a1.setClient(g_queue_2.get(i));
                                g_queue_2.remove(i);
                            } else {
                                a1.setState(1);
                                a1.setBusyTime(clock + g_queue_2.get(i).getDeskTime());
                                a1.setClient(g_queue_2.get(i));
                                g_queue_2.remove(i);
                            }
                            break;
                        }
                    }
                }
            }

            // A2
            // Client exits the A2
            if (a2.getBusyTime() == clock) {
                a2.setState(0);
                a2.setBusyTime(10000000);
                a2_clients++;

                if (clock - (a2.getClient().getArriveTime() + a2.getClient().getSortingTime() + a2.getClient().getWaiting() + a2.getClient().getDeskTime()) > 0) {
                    if (a2.getClient().getPriority() == 1 && a2.getClient().getRepeat() != -1) {
                        p2_waiting += (clock - (a2.getClient().getArriveTime() + a2.getClient().getSortingTime() + a2.getClient().getWaiting() + a2.getClient().getDeskTime()));
                        n_p2_waiting++;
                    } else if (a2.getClient().getPriority() == 0 && a2.getClient().getRepeat() != -1) {
                        g2_waiting += (clock - (a2.getClient().getArriveTime() + a2.getClient().getSortingTime() + a2.getClient().getWaiting() + a2.getClient().getDeskTime()));
                        n_g2_waiting++;
                    } else {
                        r2_waiting += (clock - (a2.getClient().getArriveTime() + a2.getClient().getSortingTime() + a2.getClient().getWaiting() + a2.getClient().getDeskTime()));
                        n_r2_waiting++;
                    }
                    a2.getClient().setWaiting(a2.getClient().getWaiting() + (clock - (a2.getClient().getArriveTime() + a2.getClient().getSortingTime() + a2.getClient().getWaiting() + a2.getClient().getDeskTime())));
                }

                if (a2.getClient().getTreasury() == 1) {            // Client needs to pass through Treasury

                    if (a2.getClient().getPriority() == 1 && a2.getClient().getRepeat() == 0) {
                        p_queue_3.add(a2.getClient());
                    } else if (a2.getClient().getPriority() == 0 && a2.getClient().getRepeat() == 0) {
                        g_queue_3.add(a2.getClient());
                    } else {
                        r_queue_3.add(a2.getClient());
                        a2_clients--;
                    }

                } else {                                            // Clients exits the system
                    System.out.println("Client exits the system " + a2.getClient());
                    if (a2.getClient().getArriveTime() <= 7200) {
                        a_q1++;
                        real_n_clients_q1++;
                        tte_q1 += a2.getClient().getWaiting();
                    } else if (a2.getClient().getArriveTime() > 7200 && a2.getClient().getArriveTime() <= 14400) {
                        a_q2++;
                        real_n_clients_q2++;
                        tte_q2 += a2.getClient().getWaiting();
                    } else if (a2.getClient().getArriveTime() > 14400 && a2.getClient().getArriveTime() <= 21600) {
                        a_q3++;
                        real_n_clients_q3++;
                        tte_q3 += a2.getClient().getWaiting();
                    } else {
                        a_q4++;
                        real_n_clients_q4++;
                        tte_q4 += a2.getClient().getWaiting();
                    }
                    max_tte = maxTTE(max_tte, a2.getClient().getWaiting());
                    min_tte = minTTE(min_tte, a2.getClient().getWaiting());}
            }
            // A2 is IDLE
            if (a2.getState() == 0) {
                if (!p_queue_2.isEmpty()) {
                    for (int i = 0; i < p_queue_2.size(); i++) {
                        if (p_queue_2.get(i).getDesk() == 'A') {
                            if (p_queue_2.get(i).getRepeat() == 1) {
                                a2.setState(1);
                                int old_time = p_queue_2.get(i).getDeskTime();
                                p_queue_2.get(i).setDeskTime(generator(0, p_queue_2.get(i).getDeskTime()));
                                a2.setBusyTime(clock + p_queue_2.get(i).getDeskTime() - old_time);
                                a2.setClient(p_queue_2.get(i));
                                p_queue_2.remove(i);
                            } else {
                                a2.setState(1);
                                a2.setBusyTime(clock + p_queue_2.get(i).getDeskTime());
                                a2.setClient(p_queue_2.get(i));
                                p_queue_2.remove(i);
                            }
                            break;
                        }
                    }
                } else if (!r_queue_2.isEmpty()) {
                    for (int i = 0; i < r_queue_2.size(); i++) {
                        if (r_queue_2.get(i).getDesk() == 'A') {
                            a2.setState(1);
                            a2.setBusyTime(clock + r_queue_2.get(i).getDeskTime());
                            a2.setClient(r_queue_2.get(i));
                            r_queue_2.remove(i);
                            break;
                        }
                    }
                } else if (!g_queue_2.isEmpty()) {
                    for (int i = 0; i < g_queue_2.size(); i++) {
                        if (g_queue_2.get(i).getDesk() == 'A') {
                            if (g_queue_2.get(i).getRepeat() == 1) {
                                a2.setState(1);
                                int old_time = g_queue_2.get(i).getDeskTime();
                                g_queue_2.get(i).setDeskTime(generator(0, g_queue_2.get(i).getDeskTime()));
                                a2.setBusyTime(clock + g_queue_2.get(i).getDeskTime() - old_time);
                                a2.setClient(g_queue_2.get(i));
                                g_queue_2.remove(i);
                            } else {
                                a2.setState(1);
                                a2.setBusyTime(clock + g_queue_2.get(i).getDeskTime());
                                a2.setClient(g_queue_2.get(i));
                                g_queue_2.remove(i);
                            }
                            break;
                        }
                    }
                }
            }
            // B1
            // Client exits the B1
            if (b1.getBusyTime() == clock) {

                if (clock - (b1.getClient().getArriveTime() + b1.getClient().getSortingTime() + b1.getClient().getWaiting() + b1.getClient().getDeskTime()) > 0) {
                    if (b1.getClient().getPriority() == 1 && b1.getClient().getRepeat() != -1) {
                        p2_waiting += (clock - (b1.getClient().getArriveTime() + b1.getClient().getSortingTime() + b1.getClient().getWaiting() + b1.getClient().getDeskTime()));
                        n_p2_waiting++;
                    } else if (b1.getClient().getPriority() == 0 && b1.getClient().getRepeat() != -1) {
                        g2_waiting += (clock - (b1.getClient().getArriveTime() + b1.getClient().getSortingTime() + b1.getClient().getWaiting() + b1.getClient().getDeskTime()));
                        n_g2_waiting++;
                    } else {
                        r2_waiting += (clock - (b1.getClient().getArriveTime() + b1.getClient().getSortingTime() + b1.getClient().getWaiting() + b1.getClient().getDeskTime()));
                        n_r2_waiting++;
                    }
                    b1.getClient().setWaiting(b1.getClient().getWaiting() + (clock - (b1.getClient().getArriveTime() + b1.getClient().getSortingTime() + b1.getClient().getWaiting() + b1.getClient().getDeskTime())));
                }

                if (b1.getClient().getTreasury() == 1) {         // Client needs to pass through Treasury

                    if (b1.getClient().getPriority() == 1 && b1.getClient().getRepeat() == 0)
                        p_queue_3.add(b1.getClient());
                    else if (b1.getClient().getPriority() == 0 && b1.getClient().getRepeat() == 0) {
                        g_queue_3.add(b1.getClient());
                    } else {
                        r_queue_3.add(b1.getClient());
                        b1_clients--;

                    }
                } else {                                            // Clients exits the system
                    System.out.println("Client exits the system " + b1.getClient());
                    if (b1.getClient().getArriveTime() <= 7200) {
                        b_q1++;
                        real_n_clients_q1++;
                        tte_q1 += b1.getClient().getWaiting();
                    } else if (b1.getClient().getArriveTime() > 7200 && b1.getClient().getArriveTime() <= 14400) {
                        b_q2++;
                        real_n_clients_q2++;
                        tte_q2 += b1.getClient().getWaiting();
                    } else if (b1.getClient().getArriveTime() > 14400 && b1.getClient().getArriveTime() <= 21600) {
                        b_q3++;
                        real_n_clients_q3++;
                        tte_q3 += b1.getClient().getWaiting();
                    } else {
                        b_q4++;
                        real_n_clients_q4++;
                        tte_q4 += b1.getClient().getWaiting();
                    }
                    max_tte = maxTTE(max_tte, b1.getClient().getWaiting());
                    min_tte = minTTE(min_tte, b1.getClient().getWaiting());}
                b1.setState(0);
                b1.setBusyTime(10000000);
                b1_clients++;

            }
            // B1 is IDLE
            if (b1.getState() == 0) {
                if (!p_queue_2.isEmpty()) {
                    for (int i = 0; i < p_queue_2.size(); i++) {
                        if (p_queue_2.get(i).getDesk() == 'B') {
                            if (p_queue_2.get(i).getRepeat() == 1) {
                                b1.setState(1);
                                int old_time = p_queue_2.get(i).getDeskTime();
                                p_queue_2.get(i).setDeskTime(generator(0, p_queue_2.get(i).getDeskTime()));
                                b1.setBusyTime(clock + p_queue_2.get(i).getDeskTime() - old_time);
                                b1.setClient(p_queue_2.get(i));
                                p_queue_2.remove(i);
                            } else {
                                b1.setState(1);
                                b1.setBusyTime(clock + p_queue_2.get(i).getDeskTime());
                                b1.setClient(p_queue_2.get(i));
                                p_queue_2.remove(i);
                            }
                            break;
                        }
                    }
                } else if (!r_queue_2.isEmpty()) {
                    for (int i = 0; i < r_queue_2.size(); i++) {
                        if (r_queue_2.get(i).getDesk() == 'B') {
                            b1.setState(1);
                            b1.setBusyTime(clock + r_queue_2.get(i).getDeskTime());
                            b1.setClient(r_queue_2.get(i));
                            r_queue_2.remove(i);
                            break;
                        }
                    }
                } else if (!g_queue_2.isEmpty()) {
                    for (int i = 0; i < g_queue_2.size(); i++) {
                        if (g_queue_2.get(i).getDesk() == 'B') {
                            if (g_queue_2.get(i).getRepeat() == 1) {
                                b1.setState(1);
                                int old_time = g_queue_2.get(i).getDeskTime();
                                g_queue_2.get(i).setDeskTime(generator(0, g_queue_2.get(i).getDeskTime()));
                                b1.setBusyTime(clock + g_queue_2.get(i).getDeskTime() - old_time);
                                b1.setClient(g_queue_2.get(i));
                                g_queue_2.remove(i);
                            } else {
                                b1.setState(1);
                                b1.setBusyTime(clock + g_queue_2.get(i).getDeskTime());
                                b1.setClient(g_queue_2.get(i));
                                g_queue_2.remove(i);
                            }
                            break;
                        }
                    }
                }
            }
            // B2
            // Client exits the B2
            if (b2.getBusyTime() == clock) {
                b2.setState(0);
                b2.setBusyTime(10000000);
                b2_clients++;

                if (clock - (b2.getClient().getArriveTime() + b2.getClient().getSortingTime() + b2.getClient().getWaiting() + b2.getClient().getDeskTime()) > 0) {
                    if (b2.getClient().getPriority() == 1 && b2.getClient().getRepeat() != -1) {
                        p2_waiting += (clock - (b2.getClient().getArriveTime() + b2.getClient().getSortingTime() + b2.getClient().getWaiting() + b2.getClient().getDeskTime()));
                        n_p2_waiting++;
                    } else if (b2.getClient().getPriority() == 0 && b2.getClient().getRepeat() != -1) {
                        g2_waiting += (clock - (b2.getClient().getArriveTime() + b2.getClient().getSortingTime() + b2.getClient().getWaiting() + b2.getClient().getDeskTime()));
                        n_g2_waiting++;
                    } else {
                        r2_waiting += (clock - (b2.getClient().getArriveTime() + b2.getClient().getSortingTime() + b2.getClient().getWaiting() + b2.getClient().getDeskTime()));
                        n_r2_waiting++;
                    }
                    b2.getClient().setWaiting(b2.getClient().getWaiting() + (clock - (b2.getClient().getArriveTime() + b2.getClient().getSortingTime() + b2.getClient().getWaiting() + b2.getClient().getDeskTime())));
                }


                if (b2.getClient().getTreasury() == 1) {         // Client needs to pass through Treasury

                    if (b2.getClient().getPriority() == 1 && b2.getClient().getRepeat() == 0) {
                        p_queue_3.add(b2.getClient());
                    } else if (b2.getClient().getPriority() == 0 && b2.getClient().getRepeat() == 0) {
                        g_queue_3.add(b2.getClient());
                    } else {
                        r_queue_3.add(b2.getClient());
                        b2_clients--;

                    }
                } else {                                            // Clients exits the system
                    System.out.println("Client exits the system " + b2.getClient());
                    if (b2.getClient().getArriveTime() <= 7200) {
                        b_q1++;
                        real_n_clients_q1++;
                        tte_q1 += b2.getClient().getWaiting();
                    } else if (b2.getClient().getArriveTime() > 7200 && b2.getClient().getArriveTime() <= 14400) {
                        b_q2++;
                        real_n_clients_q2++;
                        tte_q2 += b2.getClient().getWaiting();
                    } else if (b2.getClient().getArriveTime() > 14400 && b2.getClient().getArriveTime() <= 21600) {
                        b_q3++;
                        real_n_clients_q3++;
                        tte_q3 += b2.getClient().getWaiting();
                    } else {
                        b_q4++;
                        real_n_clients_q4++;
                        tte_q4 += b2.getClient().getWaiting();
                    }
                    max_tte = maxTTE(max_tte, b2.getClient().getWaiting());
                    min_tte = minTTE(min_tte, b2.getClient().getWaiting());}

            }

            // B2 is IDLE
            if (b2.getState() == 0) {
                if (!p_queue_2.isEmpty()) {
                    for (int i = 0; i < p_queue_2.size(); i++) {
                        if (p_queue_2.get(i).getDesk() == 'B') {
                            if (p_queue_2.get(i).getRepeat() == 1) {
                                b2.setState(1);
                                int old_time = p_queue_2.get(i).getDeskTime();
                                p_queue_2.get(i).setDeskTime(generator(0, p_queue_2.get(i).getDeskTime()));
                                b2.setBusyTime(clock + p_queue_2.get(i).getDeskTime() - old_time);
                                b2.setClient(p_queue_2.get(i));
                                p_queue_2.remove(i);
                            } else {
                                b2.setState(1);
                                b2.setBusyTime(clock + p_queue_2.get(i).getDeskTime());
                                b2.setClient(p_queue_2.get(i));
                                p_queue_2.remove(i);
                            }
                            break;
                        }
                    }
                } else if (!r_queue_2.isEmpty()) {
                    for (int i = 0; i < r_queue_2.size(); i++) {
                        if (r_queue_2.get(i).getDesk() == 'B') {
                            b2.setState(1);
                            b2.setBusyTime(clock + r_queue_2.get(i).getDeskTime());
                            b2.setClient(r_queue_2.get(i));
                            r_queue_2.remove(i);
                            break;
                        }
                    }
                } else if (!g_queue_2.isEmpty()) {
                    for (int i = 0; i < g_queue_2.size(); i++) {
                        if (g_queue_2.get(i).getDesk() == 'B') {
                            if (g_queue_2.get(i).getRepeat() == 1) {
                                b2.setState(1);
                                int old_time = g_queue_2.get(i).getDeskTime();
                                g_queue_2.get(i).setDeskTime(generator(0, g_queue_2.get(i).getDeskTime()));
                                b2.setBusyTime(clock + g_queue_2.get(i).getDeskTime() - old_time);
                                b2.setClient(g_queue_2.get(i));
                                g_queue_2.remove(i);
                            } else {
                                b2.setState(1);
                                b2.setBusyTime(clock + g_queue_2.get(i).getDeskTime());
                                b2.setClient(g_queue_2.get(i));
                                g_queue_2.remove(i);
                            }
                            break;
                        }
                    }
                }
            }
            // C
            // Client exits the C
            if (c.getBusyTime() == clock) {
                cc_clients++;
                c.setState(0);
                c.setBusyTime(10000000);

                if (clock - (c.getClient().getArriveTime() + c.getClient().getSortingTime() + c.getClient().getWaiting() + c.getClient().getDeskTime()) > 0) {
                    if (c.getClient().getPriority() == 1 && c.getClient().getRepeat() != -1) {
                        p2_waiting += (clock - (c.getClient().getArriveTime() + c.getClient().getSortingTime() + c.getClient().getWaiting() + c.getClient().getDeskTime()));
                        n_p2_waiting++;
                    } else if (c.getClient().getPriority() == 0 && c.getClient().getRepeat() != -1) {
                        g2_waiting += (clock - (c.getClient().getArriveTime() + c.getClient().getSortingTime() + c.getClient().getWaiting() + c.getClient().getDeskTime()));
                        n_g2_waiting++;
                    } else {
                        r2_waiting += (clock - (c.getClient().getArriveTime() + c.getClient().getSortingTime() + c.getClient().getWaiting() + c.getClient().getDeskTime()));
                        n_r2_waiting++;
                    }
                    c.getClient().setWaiting(c.getClient().getWaiting() + (clock - (c.getClient().getArriveTime() + c.getClient().getSortingTime() + c.getClient().getWaiting() + c.getClient().getDeskTime())));
                }

                if (c.getClient().getTreasury() == 1) {         // Client needs to pass through Treasury

                    if (c.getClient().getPriority() == 1 && c.getClient().getRepeat() == 0)
                        p_queue_3.add(c.getClient());
                    else if (c.getClient().getPriority() == 0 && c.getClient().getRepeat() == 0) {
                        g_queue_3.add(c.getClient());
                    } else {
                        r_queue_3.add(c.getClient());
                        cc_clients--;

                    }
                } else {                                            // Clients exits the system
                    System.out.println("Client exits the system " + c.getClient());
                    if (c.getClient().getArriveTime() <= 7200) {
                        c_q1++;
                        real_n_clients_q1++;
                        tte_q1 += c.getClient().getWaiting();
                    } else if (c.getClient().getArriveTime() > 7200 && c.getClient().getArriveTime() <= 14400) {
                        c_q2++;
                        real_n_clients_q2++;
                        tte_q2 += c.getClient().getWaiting();
                    } else if (c.getClient().getArriveTime() > 14400 && c.getClient().getArriveTime() <= 21600) {
                        c_q3++;
                        real_n_clients_q3++;
                        tte_q3 += c.getClient().getWaiting();
                    } else {
                        c_q4++;
                        real_n_clients_q4++;
                        tte_q4 += c.getClient().getWaiting();
                    }
                    max_tte = maxTTE(max_tte, c.getClient().getWaiting());
                    min_tte = minTTE(min_tte, c.getClient().getWaiting());
                }
            }
            // C is IDLE
            if (c.getState() == 0) {
                if (!p_queue_2.isEmpty()) {
                    for (int i = 0; i < p_queue_2.size(); i++) {
                        if (p_queue_2.get(i).getDesk() == 'C') {
                            if (p_queue_2.get(i).getRepeat() == 1) {
                                c.setState(1);
                                int old_time = p_queue_2.get(i).getDeskTime();
                                p_queue_2.get(i).setDeskTime(generator(0, p_queue_2.get(i).getDeskTime()));
                                c.setBusyTime(clock + p_queue_2.get(i).getDeskTime() - old_time);
                                c.setClient(p_queue_2.get(i));
                                p_queue_2.remove(i);
                            } else {
                                c.setState(1);
                                c.setBusyTime(clock + p_queue_2.get(i).getDeskTime());
                                c.setClient(p_queue_2.get(i));
                                p_queue_2.remove(i);
                            }
                            break;
                        }
                    }
                } else if (!r_queue_2.isEmpty()) {
                    for (int i = 0; i < r_queue_2.size(); i++) {
                        if (r_queue_2.get(i).getDesk() == 'C') {
                            c.setState(1);
                            c.setBusyTime(clock + r_queue_2.get(i).getDeskTime());
                            c.setClient(r_queue_2.get(i));
                            r_queue_2.remove(i);
                            break;
                        }
                    }
                } else if (!g_queue_2.isEmpty()) {
                    for (int i = 0; i < g_queue_2.size(); i++) {
                        if (g_queue_2.get(i).getDesk() == 'C') {
                            if (g_queue_2.get(i).getRepeat() == 1) {
                                c.setState(1);
                                int old_time = g_queue_2.get(i).getDeskTime();
                                g_queue_2.get(i).setDeskTime(generator(0, g_queue_2.get(i).getDeskTime()));
                                c.setBusyTime(clock + g_queue_2.get(i).getDeskTime() - old_time);
                                c.setClient(g_queue_2.get(i));
                                g_queue_2.remove(i);
                            } else {
                                c.setState(1);
                                c.setBusyTime(clock + g_queue_2.get(i).getDeskTime());
                                c.setClient(g_queue_2.get(i));
                                g_queue_2.remove(i);
                            }
                            break;
                        }
                    }
                }
            }

            /*
                TREASURY PHASE
             */
            // Client exits
            // System.out.println("tesouraria p3:" + p_queue_3);
            // System.out.println("tesouraria g3:" + g_queue_3);

            if (treasury.getBusyTime() == clock) {
                treasury_clients++;

                if (clock - (treasury.getClient().getArriveTime() + treasury.getClient().getSortingTime() + treasury.getClient().getWaiting() + treasury.getClient().getDeskTime()) > 0) {
                    if (treasury.getClient().getPriority() == 1 && treasury.getClient().getRepeat() == 0) {
                        p3_waiting += (clock - (treasury.getClient().getArriveTime() + treasury.getClient().getSortingTime() + treasury.getClient().getWaiting() + treasury.getClient().getDeskTime()));
                        n_p3_waiting++;
                    } else if (treasury.getClient().getPriority() == 0 && treasury.getClient().getRepeat() == 0) {
                        g3_waiting += (clock - (treasury.getClient().getArriveTime() + treasury.getClient().getSortingTime() + treasury.getClient().getWaiting() + treasury.getClient().getDeskTime()));
                        n_g3_waiting++;
                    } else {
                        r3_waiting += (clock - (treasury.getClient().getArriveTime() + treasury.getClient().getSortingTime() + treasury.getClient().getWaiting() + treasury.getClient().getDeskTime()));
                        n_r3_waiting++;
                    }
                    treasury.getClient().setWaiting(treasury.getClient().getWaiting() + (clock - (treasury.getClient().getArriveTime() + treasury.getClient().getSortingTime() + treasury.getClient().getWaiting() + treasury.getClient().getDeskTime() + treasury.getClient().getTreasuryTime())));
                }

                if (treasury.getClient().getRepeat() == 1) {      // if client returns to desk A,B or C after treasury
                    // clone of the client to r_queue_2
                    Client y = treasury.getClient().clone();
                    y.setRepeat(-1);
                    y.setTreasury(0);
                    y.setArriveTime(clock);
                    r_queue_2.add(y);
                } else {
                    System.out.println("Client exits the system " + treasury.getClient());    //client exits the system

                    if (treasury.getClient().getArriveTime() <= 7200) {
                        if (treasury.getClient().getDesk() == 'A')
                            a_q1++;
                        else if (treasury.getClient().getDesk() == 'B')
                            b_q1++;
                        else if (treasury.getClient().getDesk() == 'C')
                            c_q1++;
                        else
                            direct_q1++;
                        real_n_clients_q1++;
                        tte_q1 += treasury.getClient().getWaiting();
                    } else if (treasury.getClient().getArriveTime() > 7200 && treasury.getClient().getArriveTime() <= 14400) {
                        if (treasury.getClient().getDesk() == 'A')
                            a_q2++;
                        else if (treasury.getClient().getDesk() == 'B')
                            b_q2++;
                        else if (treasury.getClient().getDesk() == 'C')
                            c_q2++;
                        else
                            direct_q2++;
                        real_n_clients_q2++;
                        tte_q2 += treasury.getClient().getWaiting();
                    } else if (treasury.getClient().getArriveTime() > 14400 && treasury.getClient().getArriveTime() <= 21600) {
                        if (treasury.getClient().getDesk() == 'A')
                            a_q3++;
                        else if (treasury.getClient().getDesk() == 'B')
                            b_q3++;
                        else if (treasury.getClient().getDesk() == 'C')
                            c_q3++;
                        else
                            direct_q3++;
                        real_n_clients_q3++;
                        tte_q3 += treasury.getClient().getWaiting();
                    } else {
                        if (treasury.getClient().getDesk() == 'A')
                            a_q4++;
                        else if (treasury.getClient().getDesk() == 'B')
                            b_q4++;
                        else if (treasury.getClient().getDesk() == 'C')
                            c_q4++;
                        else
                            direct_q4++;
                        real_n_clients_q4++;
                        tte_q4 += treasury.getClient().getWaiting();
                    }
                    max_tte = maxTTE(max_tte, treasury.getClient().getWaiting());
                    min_tte = minTTE(min_tte, treasury.getClient().getWaiting());
                }

                treasury.setState(0);
                treasury.setBusyTime(10000000);
            }

            // It's idle
            if (treasury.getState() == 0) {
                if (!p_queue_3.isEmpty()) {
                    treasury.setState(1);
                    treasury.setBusyTime(clock + p_queue_3.get(0).getTreasuryTime());
                    treasury.setClient(p_queue_3.get(0));
                    p_queue_3.remove(0);
                } else if (!r_queue_3.isEmpty()) {
                    treasury.setState(1);
                    treasury.setBusyTime(clock + r_queue_3.get(0).getTreasuryTime());
                    treasury.setClient(r_queue_3.get(0));
                    r_queue_3.remove(0);
                } else if (!g_queue_3.isEmpty()) {
                    treasury.setState(1);
                    treasury.setBusyTime(clock + g_queue_3.get(0).getTreasuryTime());
                    treasury.setClient(g_queue_3.get(0));
                    g_queue_3.remove(0);
                }
            }

            // Clock
            System.out.println("1: " + nextArrive + " 2: " + sorting.getBusyTime() + " 3:" + a1.getBusyTime() + " 4:" + a2.getBusyTime() + " 5:" + b1.getBusyTime() + " 6:" + b2.getBusyTime() + " 7:" + c.getBusyTime() + " 8:" + treasury.getBusyTime());
            clock = min(nextArrive, sorting.getBusyTime(), a1.getBusyTime(), a2.getBusyTime(), b1.getBusyTime(), b2.getBusyTime(), c.getBusyTime(), treasury.getBusyTime());
            System.out.println("Clock: " + clock);
            System.out.println();            //Thread.sleep(2000);

        }

        int real_clients = real_n_clients_q1 + real_n_clients_q2 + real_n_clients_q3 + real_n_clients_q4;
        System.out.println("**************************************************************INICIO SIMULAÇÃO**************************************************************");
        System.out.println();
        System.out.println("Nº de clientes gerados: " + n_clients);
        System.out.println("Nº de clientes atendidos (saiem do sistema): " + real_clients + " -> " + (float) real_clients / n_clients * 100 + "% dos clientes gerados.");
        System.out.println("-----------------------Balcões-----------------------");
        System.out.println("Nº de clientes gerados para o Balcão A: " + a_clients + " -> " + (float) a_clients / n_clients * 100 + "%.");
        System.out.println("Nº de clientes gerados para o Balcão B: " + b_clients + " -> " + (float) b_clients / n_clients * 100 + "%.");
        System.out.println("Nº de clientes gerados para o Balcão C: " + c_clients + " -> " + (float) c_clients / n_clients * 100 + "%.");
        System.out.println("Nº de clientes que vão diretos à Tesouraria: " + direct + " -> " + (float) direct / n_clients * 100 + "%.");
        System.out.println();
        System.out.println("Nº clientes atendidos no Balcão A1: " + a1_clients + " -> " + (float) a1_clients / a_clients * 100 + "% dos clientes A" + " -> "
                + (float) a1_clients / real_clients * 100 + "% dos clientes totais atendidos" + " -> " + (float) a1_clients / n_clients * 100 + "% dos clientes gerados.");
        System.out.println("Nº clientes atendidos no Balcão A2: " + a2_clients + " -> " + (float) a2_clients / a_clients * 100 + "% dos clientes A" + " -> "
                + (float) a2_clients / real_clients * 100 + "% dos clientes totais atendidos" + " -> " + (float) a2_clients / n_clients * 100 + "% dos clientes gerados.");
        System.out.println("Nº clientes atendidos no Balcão B1: " + b1_clients + " -> " + (float) b1_clients / b_clients * 100 + "% dos clientes B" + " -> "
                + (float) b1_clients / real_clients * 100 + "% dos clientes totais atendidos" + " -> " + (float) b1_clients / n_clients * 100 + "% dos clientes gerados.");
        System.out.println("Nº clientes atendidos no Balcão B2: " + b2_clients + " -> " + (float) b2_clients / b_clients * 100 + "% dos clientes B" + " -> "
                + (float) b2_clients / real_clients * 100 + "% dos clientes totais atendidos" + " -> " + (float) b2_clients / n_clients * 100 + "% dos clientes gerados.");
        System.out.println("Nº clientes atendidos no Balcão C: " + cc_clients + " -> " + (float) cc_clients / c_clients * 100 + "% dos clientes C" + " -> "
                + (float) cc_clients / real_clients * 100 + "% dos clientes totais atendidos" + " -> " + (float) cc_clients / n_clients * 100 + "% dos clientes gerados.");
        System.out.println("Nº clientes atendidos na Tesouraria: " + treasury_clients + " -> " + (float) treasury_clients / treasury_clients * 100 + "% dos clientes que passam na Tesouraria"
                + " -> " + (float) treasury_clients / real_clients * 100 + "% dos clientes totais atendidos" + " -> " + (float) treasury_clients / n_clients * 100 + "% dos clientes gerados.");
        System.out.println();
        System.out.println("-----------------------Quadrantes-----------------------");
        System.out.println("Nº total de clientes atendidos Q1 (9h-11h): " + (real_n_clients_q1) + " -> " + ((float) real_n_clients_q1) / real_clients * 100 + "% de clientes atendidos.");
        System.out.println("Nº total de clientes atendidos Q2 (11h-13h): " + (real_n_clients_q2) + " -> " + ((float) real_n_clients_q2) / real_clients * 100 + "% de clientes atendidos.");
        System.out.println("Nº total de clientes atendidos Q3 (13h-15h): " + (real_n_clients_q3) + " -> " + ((float) real_n_clients_q3) / real_clients * 100 + "% de clientes atendidos.");
        System.out.println("Nº total de clientes atendidos Q4 (15h-17h): " + (real_n_clients_q4) + " -> " + ((float) real_n_clients_q4) / real_clients * 100 + "% de clientes atendidos.");
        System.out.println("Quadrante 1");
        System.out.println("Nº clientes antendidos A: " + a_q1 + " -> " + (float) a_q1 / real_n_clients_q1 * 100 + "% dos clientes atendidos neste quadrante.");
        System.out.println("Nº clientes antendidos B: " + b_q1 + " -> " + (float) b_q1 / real_n_clients_q1 * 100 + "% dos clientes atendidos neste quadrante.");
        System.out.println("Nº clientes antendidos C: " + c_q1 + " -> " + (float) c_q1 / real_n_clients_q1 * 100 + "% dos clientes atendidos neste quadrante.");
        System.out.println("Nº clientes atendidos que vão diretos para a Tesouraria: " + direct_q1 + " -> " + (float) direct_q1 / real_n_clients_q1 * 100 + "%");
        System.out.println("Quadrante 2");
        System.out.println("Nº clientes antendidos A: " + a_q2 + " -> " + (float) a_q2 / real_n_clients_q2 * 100 + "% dos clientes atendidos neste quadrante.");
        System.out.println("Nº clientes antendidos B: " + b_q2 + " -> " + (float) b_q2 / real_n_clients_q2 * 100 + "% dos clientes atendidos neste quadrante.");
        System.out.println("Nº clientes antendidos C: " + c_q2 + " -> " + (float) c_q2 / real_n_clients_q2 * 100 + "% dos clientes atendidos neste quadrante.");
        System.out.println("Nº clientes atendidos que vão diretos para a Tesouraria: " + direct_q2 + " -> " + (float) direct_q2 / real_n_clients_q2 * 100 + "%");
        System.out.println("Quadrante 3");
        System.out.println("Nº clientes antendidos A: " + a_q3 + " -> " + (float) a_q3 / real_n_clients_q3 * 100 + "% dos clientes atendidos neste quadrante.");
        System.out.println("Nº clientes antendidos B: " + b_q3 + " -> " + (float) b_q3 / real_n_clients_q3 * 100 + "% dos clientes atendidos neste quadrante.");
        System.out.println("Nº clientes antendidos C: " + c_q3 + " -> " + (float) c_q3 / real_n_clients_q3 * 100 + "% dos clientes atendidos neste quadrante.");
        System.out.println("Nº clientes atendidos que vão diretos para a Tesouraria: " + direct_q3 + " -> " + (float) direct_q3 / real_n_clients_q3 * 100 + "%");
        System.out.println("Quadrante 4");
        System.out.println("Nº clientes antendidos A: " + a_q4 + " -> " + (float) a_q4 / real_n_clients_q4 * 100 + "% dos clientes atendidos neste quadrante.");
        System.out.println("Nº clientes antendidos B: " + b_q4 + " -> " + (float) b_q4 / real_n_clients_q4 * 100 + "% dos clientes atendidos neste quadrante.");
        System.out.println("Nº clientes antendidos C: " + c_q4 + " -> " + (float) c_q4 / real_n_clients_q4 * 100 + "% dos clientes atendidos neste quadrante.");
        System.out.println("Nº clientes atendidos que vão diretos para a Tesouraria: " + direct_q4 + " -> " + (float) direct_q4 / real_n_clients_q4 * 100 + "% dos clientes atendidos neste quadrante.");
        System.out.println();
        System.out.println("-----------------------Variáveis Estatísticas-----------------------");
        System.out.println("TTE: " + (tte_q1 + tte_q2 + tte_q3 + tte_q4) + " -> " + "TTE Min: " + min_tte + " TTE Max: " + max_tte);
        System.out.println("TTE do Q1: " + tte_q1);
        System.out.println("TTE do Q2: " + tte_q2);
        System.out.println("TTE do Q3: " + tte_q3);
        System.out.println("TTE do Q4: " + tte_q4);
        System.out.println("TME: " + (float) (tte_q1 / real_n_clients_q1 + tte_q2 / real_n_clients_q2 + tte_q3 / real_n_clients_q3 + tte_q4 / real_n_clients_q4));
        System.out.println("TME do Q1: " + (float) tte_q1 / real_n_clients_q1);
        System.out.println("TME do Q2: " + (float) tte_q2 / real_n_clients_q2);
        System.out.println("TME do Q3: " + (float) tte_q3 / real_n_clients_q3);
        System.out.println("TME do Q4: " + (float) tte_q4 / real_n_clients_q4);
        System.out.println();
        System.out.println("-----------------------Filas de Espera-----------------------");
        System.out.println("Triagem: " + queue_1);
        System.out.println("Prioritários Balcões: " + p_queue_2);
        System.out.println("Repetentes Balcões: " + r_queue_2);
        System.out.println("Gerais Balcões: " + g_queue_2);
        System.out.println("Prioritários Tesouraria: " + p_queue_3);
        System.out.println("Repetentes Tesouraria: " + r_queue_3);
        System.out.println("Gerais Tesouraria: " + g_queue_3);
        System.out.println("TME da fila da Triagem: " + (float)q_waiting/n_q_waiting);
        System.out.println("TME da fila Prioritários Balcões: " + (float)p2_waiting/n_p2_waiting);
        System.out.println("TME da fila Repetentes Balcões: " + (float)r2_waiting/n_r2_waiting);
        System.out.println("TME da fila Gerais Balcões: " + (float)g2_waiting/n_g2_waiting);
        System.out.println("TME da fila Prioritários Tesouraria: " + (float)p3_waiting/n_p3_waiting);
        System.out.println("TME da fila Repetentes Tesouraria: " + (float)r3_waiting/n_r3_waiting);
        System.out.println("TME da flia Gerais Tesouraria: " + (float)g3_waiting/n_g3_waiting);
        System.out.println();
        System.out.println("**************************************************************FIM SIMULAÇÃO**************************************************************");



    }
}