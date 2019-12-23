package Finances;

import java.util.ArrayList;

/*
    MAX CLOCK = 28.800
    INFINITY = 10.000.000
*/

public class Finances extends Functions {

    // Main function
    public static void main(String[] args) throws InterruptedException {
        int clock = 0, tme = 0, tte = 0, n_clients = 0, aux = 0;

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
        do {
            n_clients = (int) (Math.random() * 1000);
        } while (n_clients < 2 || n_clients > 5);

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
        int nextArrive = clients.get(0).getArriveTime();

        // Functional Finances
        while (clock < 28800 && clock >= 0) {
            /*
                SORTING PHASE
            */
            // Client goes to the next phase
            if (sorting.getBusyTime() == clock) {
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
                    if (clients.get(0).getPriority() == 1) {
                        queue_1.add(clients.get(0));
                        clients.remove(0);
                    } else {
                        queue_1.add(clients.get(0));
                        clients.remove(0);
                    }

                }

            }
            // Next Arrive calculation
            if (clients.isEmpty())
                nextArrive = 10000000;
            else
                nextArrive = clients.get(0).getArriveTime();

            /*
                A DESK PHASE
             */
            // A1
            // Client exits the A1
            if (a1.getBusyTime() == clock) {
                a1.setState(0);
                a1.setBusyTime(10000000);
                if (a1.getClient().getTreasury() == 1) {         // Client needs to pass through Treasury

                    if (a1.getClient().getPriority() == 1){
                        p_queue_3.add(a1.getClient());}
                    else if (a2.getClient().getPriority() == 0){
                        g_queue_3.add(a1.getClient());}
                    else{
                        r_queue_3.add(a1.getClient());}

                } else {                                            // Clients exists the system
                    System.out.println("Client exists the system");
                    tte += (clock - (a1.getClient().getArriveTime() + a1.getClient().getSortingTime() + a1.getClient().getDeskTime()));
                    System.out.println(tte);
                }
            }
            // A1 is IDLE
            if (a1.getState() == 0) {
                if (!p_queue_2.isEmpty()) {
                    for (int i = 0; i < p_queue_2.size(); i++) {
                        if (p_queue_2.get(i).getDesk() == 'A') {
                            a1.setState(1);
                            a1.setBusyTime(clock + p_queue_2.get(i).getDeskTime());
                            a1.setClient(p_queue_2.get(i));
                            p_queue_2.remove(i);
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
                            a1.setState(1);
                            a1.setBusyTime(clock + g_queue_2.get(i).getDeskTime());
                            a1.setClient(g_queue_2.get(i));
                            g_queue_2.remove(i);
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

                if (a2.getClient().getTreasury() == 1) {
                    // Client needs to pass through Treasury

                    if (a2.getClient().getPriority() == 1){
                        p_queue_3.add(a2.getClient());}
                    else if (a2.getClient().getPriority() == 0){
                        g_queue_3.add(a2.getClient());}
                    else{
                        r_queue_3.add(a2.getClient());}

                } else {                                            // Clients exists the system
                    System.out.println("Client exists the system");
                    tte += (clock - (a2.getClient().getArriveTime() + a2.getClient().getSortingTime() + a2.getClient().getDeskTime()));
                    System.out.println(tte);
                }
            }
            // A2 is IDLE
            if (a2.getState() == 0) {
                if (!p_queue_2.isEmpty()) {
                    for (int i = 0; i < p_queue_2.size(); i++) {
                        if (p_queue_2.get(i).getDesk() == 'A') {
                            a2.setState(1);
                            a2.setBusyTime(clock + p_queue_2.get(i).getDeskTime());
                            a2.setClient(p_queue_2.get(i));
                            p_queue_2.remove(i);
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
                            a2.setState(1);
                            a2.setBusyTime(clock + g_queue_2.get(i).getDeskTime());
                            a2.setClient(g_queue_2.get(i));
                            g_queue_2.remove(i);
                            break;
                        }
                    }
                }
            }
            // B1
            // Client exits the B1
            if (b1.getBusyTime() == clock) {
                b1.setState(0);
                b1.setBusyTime(10000000);
                if (b1.getClient().getTreasury() == 1) {         // Client needs to pass through Treasury

                    if (b1.getClient().getPriority() == 1)
                        p_queue_3.add(b1.getClient());
                    else if (b1.getClient().getPriority() == 0){
                        g_queue_3.add(b1.getClient());}
                    else{
                        r_queue_3.add(b1.getClient());}
                } else {                                            // Clients exists the system
                    System.out.println("Client exists the system");
                    tte += (clock - (b1.getClient().getArriveTime() + b1.getClient().getSortingTime() + b1.getClient().getDeskTime()));
                    System.out.println(tte);
                }
            }
            // B1 is IDLE
            if (b1.getState() == 0) {
                if (!p_queue_2.isEmpty()) {
                    for (int i = 0; i < p_queue_2.size(); i++) {
                        if (p_queue_2.get(i).getDesk() == 'B') {
                            b1.setState(1);
                            b1.setBusyTime(clock + p_queue_2.get(i).getDeskTime());
                            b1.setClient(p_queue_2.get(i));
                            p_queue_2.remove(i);
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
                            b1.setState(1);
                            b1.setBusyTime(clock + g_queue_2.get(i).getDeskTime());
                            b1.setClient(g_queue_2.get(i));
                            g_queue_2.remove(i);
                            break;
                        }
                    }
                }
            }
            // B2
            // Client exits the B1
            if (b2.getBusyTime() == clock) {
                b2.setState(0);
                b2.setBusyTime(10000000);

                    if (b2.getClient().getTreasury() == 1) {         // Client needs to pass through Treasury

                        if (b2.getClient().getPriority() == 1) {
                            p_queue_3.add(b2.getClient());
                        } else if (b2.getClient().getPriority() == 0) {
                            g_queue_3.add(b2.getClient());
                        } else {
                            r_queue_3.add(b2.getClient());
                        }
                    } else {                                            // Clients exists the system
                        System.out.println("Client exists the system");
                        tte += (clock - (b2.getClient().getArriveTime() + b2.getClient().getSortingTime() + b2.getClient().getDeskTime()));
                        System.out.println(tte);
                    }

            }

            // B2 is IDLE
            if (b2.getState() == 0) {
                if (!p_queue_2.isEmpty()) {
                    for (int i = 0; i < p_queue_2.size(); i++) {
                        if (p_queue_2.get(i).getDesk() == 'B') {
                            b2.setState(1);
                            b2.setBusyTime(clock + p_queue_2.get(i).getDeskTime());
                            b2.setClient(p_queue_2.get(i));
                            p_queue_2.remove(i);
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
                            b2.setState(1);
                            b2.setBusyTime(clock + g_queue_2.get(i).getDeskTime());
                            b2.setClient(g_queue_2.get(i));
                            g_queue_2.remove(i);
                            break;
                        }
                    }
                }
            }
            // C
            // Client exits the C
            if (c.getBusyTime() == clock) {
                c.setState(0);
                c.setBusyTime(10000000);
                if (c.getClient().getTreasury() == 1) {         // Client needs to pass through Treasury

                    if (c.getClient().getPriority() == 1)
                        p_queue_3.add(c.getClient());
                    else if (c.getClient().getPriority() == 0){
                        g_queue_3.add(c.getClient());}
                    else{
                        r_queue_3.add(c.getClient());}
                } else {                                            // Clients exists the system
                    System.out.println("Client exits the system");
                    tte += (clock - (c.getClient().getArriveTime() + c.getClient().getSortingTime() + c.getClient().getDeskTime()));
                    System.out.println(tte);
                }
            }
            // C is IDLE
            if (c.getState() == 0) {
                if (!p_queue_2.isEmpty()) {
                    for (int i = 0; i < p_queue_2.size(); i++) {
                        if (p_queue_2.get(i).getDesk() == 'C') {
                            c.setState(1);
                            c.setBusyTime(clock + p_queue_2.get(i).getDeskTime());
                            c.setClient(p_queue_2.get(i));
                            p_queue_2.remove(i);
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
                            c.setState(1);
                            c.setBusyTime(clock + g_queue_2.get(i).getDeskTime());
                            c.setClient(g_queue_2.get(i));
                            g_queue_2.remove(i);
                            break;
                        }
                    }
                }
            }
            // Clock
            System.out.println("1: " + nextArrive + " 2: " + sorting.getBusyTime() + " 3:" + a1.getBusyTime() + " 4:" + a2.getBusyTime() + " 5:" + b1.getBusyTime() + " 6:" + b2.getBusyTime() + " 7:" + c.getBusyTime() + " 8:" + treasury.getBusyTime());
            clock = min(nextArrive, sorting.getBusyTime(), a1.getBusyTime(), a2.getBusyTime(), b1.getBusyTime(), b2.getBusyTime(), c.getBusyTime(), treasury.getBusyTime());
            System.out.println(clock);
            //Thread.sleep(2000);

        }
        System.out.println("Desks");
        System.out.println("Queue 2 P: " + p_queue_2);
        System.out.println("Queue 2 G: " + g_queue_2);
        System.out.println("RAW 1:" + r_queue_2);
        System.out.println("Treasury");
        System.out.println("Queue 3 P: " + p_queue_3);
        System.out.println("Queue 3 G: " + g_queue_3);
        System.out.println("RAW 2:" + r_queue_3);
        System.out.println("TTE: " + tte);
    }
}
