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
        ArrayList<Client> p_queue_1 = new ArrayList<>();            // Priority Clients Sorting (stage 1)
        ArrayList<Client> g_queue_1 = new ArrayList<>();            // General Clients Sorting (stage 1)
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
        } while (n_clients < 120 || n_clients > 150);

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
        while (clock < 28800) {
            /*
                SORTING PHASE
            */
            // Client goes to the next phase
            if (sorting.getBusyTime() == clock) {
                if (sorting.getClient().getPriority() == 1)
                    p_queue_2.add(sorting.getClient());
                else
                    g_queue_2.add(sorting.getClient());

                // There is someone in 1 of the queues
                if(!p_queue_1.isEmpty()){
                    sorting.setState(1);
                    sorting.setBusyTime(clock + p_queue_1.get(0).getSortingTime());
                    sorting.setClient(p_queue_1.get(0));
                    p_queue_1.remove(0);
                }
                else if(!g_queue_1.isEmpty()){
                    sorting.setState(1);
                    sorting.setBusyTime(clock + g_queue_1.get(0).getSortingTime());
                    sorting.setClient(g_queue_1.get(0));
                    g_queue_1.remove(0);
                }
                // Sets Sorting to IDLE
                else {
                    sorting.setBusyTime(10000000);
                    sorting.setState(0);
                }
            }
            // Client arrives
            if (nextArrive == clock) {
                if(sorting.getState() == 0){
                    sorting.setState(1);
                    sorting.setBusyTime(clock + clients.get(0).getSortingTime());
                    sorting.setClient(clients.get(0));
                    clients.remove(0);
                }
                else{
                    if(clients.get(0).getPriority() == 1){
                        p_queue_1.add(clients.get(0));
                        clients.remove(0);
                    }else{
                        g_queue_1.add(clients.get(0));
                        clients.remove(0);
                    }

                }

            }
            //System.out.println("ATENDENDO: " + sorting.getClient());
            // Next Arrive calculation
            if(clients.isEmpty())
                nextArrive = 10000000;
            else
                nextArrive = clients.get(0).getArriveTime();

            // Clock
            clock = min(nextArrive, sorting.getBusyTime(), a1.getBusyTime(), a2.getBusyTime(), b1.getBusyTime(), b2.getBusyTime(), c.getBusyTime(), treasury.getBusyTime());
            System.out.println(clock);
            //Thread.sleep(2000);

        }

        System.out.println("Queue P: " + p_queue_2);
        System.out.println("Queue G: " + g_queue_2);
    }
}
