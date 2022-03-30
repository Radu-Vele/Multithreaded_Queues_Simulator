package Model;

import Logic.SimulationManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TQueue implements Runnable    {
    private BlockingQueue<Client> q = new LinkedBlockingQueue<Client>(); //thread-safe data structure
    private AtomicInteger waitingPeriod;
    private AtomicInteger totalWaitingTime;
    private AtomicInteger totalClientsServed;
    private boolean simRunning;

    public TQueue() {
        this.waitingPeriod = new AtomicInteger(0);
        this.totalWaitingTime = new AtomicInteger(0);
        this.totalClientsServed = new AtomicInteger(0);
        simRunning = true;
    }

    @Override
    public void run() { //run method implemented for a queue
        while(this.simRunning) {
            if (this.q.isEmpty()) {
                try {
                    Thread.sleep(1000); //stop thread until client is served in seconds
                } catch (InterruptedException e) {
                    System.out.println("\nInterrupted thread sleep!");
                    return;
                }
                continue;
            }
            Client currClient = q.peek();
            try {
                Thread.sleep(currClient.getTService() * 1000); //stop thread until client is served in seconds
            } catch (InterruptedException e) {
                System.out.println("\nInterrupted thread sleep!");
                return;
            }
            this.waitingPeriod.getAndAdd(- currClient.getTService());

            //for total waiting time computation
            int toAdd = SimulationManager.globalSimulationTime.get() - currClient.getTArrival();
            totalWaitingTime.getAndAdd(toAdd); //the total waiting time of the clients in the queue;
            totalClientsServed.getAndIncrement();

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

    public void setSimRunning(boolean simRunning) {
        this.simRunning = simRunning;
    }

    public String printContents() {
        String toReturn = new String();
        if(this.q.isEmpty()) {
            toReturn = "closed";
        }
        else {
            for (Client currClient : q) {
                toReturn = toReturn + currClient.printFriendly();
            }
        }

        return toReturn;
    }

    public AtomicInteger getTotalClientsServed() {
        return totalClientsServed;
    }

    public AtomicInteger getTotalWaitingTime() {
        return totalWaitingTime;
    }
}