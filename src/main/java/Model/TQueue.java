package Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TQueue implements Runnable    {
    private BlockingQueue<Client> q = new LinkedBlockingQueue<Client>(); //thread-safe data structure
    private AtomicInteger waitingPeriod;
    private boolean simRunning;

    public TQueue() {
        this.waitingPeriod.set(0);
        simRunning = false;
    }

    @Override
    public void run() { //run method implemented for a queue
        while(this.simRunning) {
            Client currClient = q.peek();
            try {
                Thread.sleep(currClient.getTService() * 1000); //stop thread until client is served in seconds
            } catch (InterruptedException e) {
                System.out.println("\nInterrupted thread sleep!");
                return;
            }
            this.waitingPeriod.getAndAdd(- currClient.getTService());
            q.remove();
        }
    }

    public BlockingQueue<Client> getQ() {
        return q;
    }

    public void addClient(Client toAdd) {
        q.add(toAdd);
        this.waitingPeriod.getAndAdd(toAdd.getTService());
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }
}