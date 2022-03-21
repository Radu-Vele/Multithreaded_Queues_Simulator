package Model;

public class Client {
    private String ID;
    private int tService;
    private int tArrival;

    public Client(String ID, int tArrival, int tService) {
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
}
