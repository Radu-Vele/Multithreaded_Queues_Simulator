package Model;

public class Client implements Comparable<Client>{
    private int ID;
    private int tService;
    private int tArrival;

    public Client(int ID, int tArrival, int tService) {
        this.ID = ID;
        this.tService = tService;
        this.tArrival = tArrival;
    }

    public int getTArrival() {
        return tArrival;
    }

    public int getTService() {
        return tService;
    }

    public void decreaseServiceTime() {
        this.tService--;
    }

    @Override
    public int compareTo(Client o) {
        return this.getTArrival() - o.getTArrival();
    }

    public int getID() {
        return ID;
    }

    public String printFriendly() {
        String toReturn = new String("(");
        toReturn = toReturn + Integer.toString(this.getID()) + ",";
        toReturn = toReturn + Integer.toString(this.getTArrival()) + ",";
        toReturn = toReturn + Integer.toString(this.getTService()) + "); ";
        return  toReturn;
    }
}
