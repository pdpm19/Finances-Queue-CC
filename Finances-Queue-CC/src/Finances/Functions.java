package Finances;

import java.util.ArrayList;

public class Functions {

    // Min
    public static int min(int a, int b, int c, int d, int e, int f, int g, int h) {
        ArrayList<Integer> lista = new ArrayList<>();
        lista.add(a);lista.add(b);lista.add(c);lista.add(d);lista.add(e);lista.add(f);lista.add(g);lista.add(h);
        int min = lista.get(0);
        for (int i = 0; i < lista.size(); i++) {
            if(lista.get(i) < min)
                min = lista.get(i);
        }

        return min;
    }

    // Client's data generator
    public static ArrayList<Client> clientGenerator(ArrayList<Client> e, int n_clients){
        for(int i = 0; i < n_clients; i++){
            Client client = new Client();
            client.setPriority(priorityGenerator());
            client.setArriveTime(arriveGenerator());
            client.setSortingTime(sortingGenerator());
            client.setDirectTreasury(directTreasuryGeneratorta());
            if (client.getDirectTreasury() == 0) {                              // Not going right to Treasury
                client.setDesk(deskGenerator());
                client.setDeskTime(deskTimeGenerator(client.getDesk()));
                client.setTreasury(treasuryPassGenerator(client.getDesk()));
                if (client.getTreasury() == 1) {                                // Needs to pass in the Treasury
                    client.setTreasuryTime(treasuryTimeGenerator());
                    client.setRepeat(repeatGenerator(client));                  // Needs to go back to queue 2
                }

            }
            else{
                client.setTreasury(1);
                client.setTreasuryTime(treasuryTimeGenerator());
            }
            e.add(client);
        }
        return e;
    }

    // Priority Generator
    public static int priorityGenerator() {
        if (Math.random() <= 0.20)
            return 1;
        return 0;
    }

    // Arrive Time
    public static int arriveGenerator() {
        double p_entrada = Math.random();
        if (p_entrada <= 0.10)
            return (generator(0, 7200));
        else if (p_entrada <= 0.20)
            return (generator(21600, 28800));
        else if (p_entrada <= 0.25)
            return (generator(7200, 14400));
        else
            return (generator(14400, 21600));
    }

    // Sorting Time
    public static int sortingGenerator() {
        double p_triagem = Math.random();
        if (p_triagem <= 0.10)
            return (generator(120, 180));
        else if (p_triagem <= 0.35)
            return (generator(60, 120));
        else
            return (generator(0, 60));
    }

    // Direct Treasury
    public static int directTreasuryGeneratorta() {
        if (Math.random() <= 0.10)
            return 1;
        else
            return 0;
    }

    // Which desk
    public static char deskGenerator() {
        double p_balcao = Math.random();
        if (p_balcao <= 0.15)
            return 'C';
        else if (p_balcao <= 0.30)
            return 'A';
        else
            return 'B';
    }

    // Desk Time
    public static int deskTimeGenerator(char a) {
        double p_tempo = Math.random();
        if (a == 'A') {
            if (p_tempo <= 0.10)
                return (generator(1500, 1800));
            else if (p_tempo <= 0.25)
                return (generator(0, 300));
            else if (p_tempo <= 0.30)
                return (generator(900, 1500));
            else
                return (generator(300, 900));
        } else if (a == 'B') {
            if (p_tempo <= 0.05)
                return (generator(900, 1200));
            else if (p_tempo <= 0.25)
                return (generator(600, 900));
            else if (p_tempo <= 0.30)
                return (generator(0, 300));
            else
                return (generator(300, 600));
        } else {
            if (p_tempo <= 0.05)
                return (generator(0, 300));
            else if (p_tempo <= 0.15)
                return (generator(900, 1200));
            else if (p_tempo <= 0.35)
                return (generator(300, 600));
            else
                return (generator(600, 900));
        }
    }

    // Treasury pass
    public static int treasuryPassGenerator(char a) {
        double passagem = Math.random();
        if (a == 'A') {
            if (passagem <= 0.20)
                return 1;
            else
                return 0;
        }
        if (a == 'B') {
            if (passagem <= 0.30)
                return 1;
            else
                return 0;
        } else if (passagem <= 0.75)
            return 1;
        else
            return 0;
    }

    // Treasury Time
    public static int treasuryTimeGenerator() {
        double p_tesouraria = Math.random();
        if (p_tesouraria <= 0.05)
            return (generator(120, 180));
        else if (p_tesouraria <= 0.40)
            return (generator(0, 60));
        else
            return (generator(60, 120));
    }

    // Repeat
    public static int repeatGenerator(Client client){
        if(client.getDesk() == 'A') {
            if (Math.random() <= 0.30)
                return 1;
            else
                return 0;
        }
        else if(client.getDesk() == 'B'){
            if(Math.random() <= 0.20)
                return 1;
            else
                return 0;
        }
        else{
            if(Math.random() <= 0.40)
                return 1;
            else
                return 0;
        }
    }

    // Generator of ]min, max]
    public static int generator(int min, int max) {
        int valor = 0;
        do {
            valor = (int) (Math.random() * max);
        } while (valor <= min || valor > max);
        return valor;
    }

    //minimum value of tte
    public static int minTTE (int min, int aux_tte_min) {
        if (min < aux_tte_min)
            return min;
        return aux_tte_min;
    }

    //max value of tte
    public static int maxTTE (int max, int aux_tte_max){
        if (max > aux_tte_max)
            return max;
        return aux_tte_max;
    }
}
