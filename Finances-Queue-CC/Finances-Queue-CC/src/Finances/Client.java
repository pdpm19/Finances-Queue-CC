package Finances;

public class Client {
    int t_arrive;
    int t_sorting;
    int direct_treasury;
    int t_desk;
    char desk;
    int treasury;
    int t_treasury;
    int priority;
    int repeat;

    public Client(){

    }
    public Client(Client c,int t_arrive, int t_sorting, int priority){
        c.setArriveTime(t_arrive);
        c.setSortingTime(t_sorting);
        c.setPriority(priority);
    }

    // Sets
    public void setArriveTime(int t_arrive){
        this.t_arrive = t_arrive;
    }
    public void setSortingTime(int t_sorting){
        this.t_sorting = t_sorting;
    }
    public void setDirectTreasury(int direct_treasury){
        this.direct_treasury = direct_treasury;
    }
    public void setDeskTime(int t_desk){
        this.t_desk = t_desk;
    }
    public void setDesk(char desk){
        this.desk = desk;
    }
    public void setTreasury(int treasury){
        this.treasury = treasury;
    }
    public void setTreasuryTime(int t_treasury){
        this.t_treasury = t_treasury;
    }
    public void setPriority(int priority){
        this.priority = priority;
    }
    public void setRepeat(int repeat){
        this.repeat = repeat;
    }

    // Gets
    public int getArriveTime(){
        return t_arrive;
    }
    public int getSortingTime(){
        return t_sorting;
    }
    public int getDirectTreasury(){
        return direct_treasury;
    }
    public int getDeskTime(){
        return t_desk;
    }
    public char getDesk(){
        return desk;
    }
    public int getTreasury(){
        return treasury;
    }
    public int getTreasuryTime(){
        return t_treasury;
    }
    public int getPriority(){
        return priority;
    }
    public int getRepeat(){
        return repeat;
    }

    // ToString
    public String toString(){
        String s = "Arrive Time: " + t_arrive + ", Sorting Time: " + t_sorting + ", Direct Treasury: " + direct_treasury + ", Priority: " + priority + ", Desk: " + desk + ", Desk Time:" + t_desk + ", Treasury: " + treasury + "" +
                ", Treasury Time: " + t_treasury + ", Repeat: " + repeat + "\n";
        return s;
    }

    // Clone function


}
