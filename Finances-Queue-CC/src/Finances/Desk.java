package Finances;

public class Desk extends Client {
    int state;
    int t_busy;
    Client c;

    public Desk(){
        state = 0;
        t_busy = 10000000;
        c = new Client();
        c.setRepeat(0);
    }

    // Gets
    public int getState() {
        return state;
    }

    // Sets
    public void setState(int state) {
        this.state = state;
    }

    public int getBusyTime() {
        return t_busy;
    }

    public void setBusyTime(int t_busy) {
        this.t_busy = t_busy;
    }

    public Client getClient() {
        return c;
    }

    public void setClient(Client c) {
        this.c = c;
    }
}
