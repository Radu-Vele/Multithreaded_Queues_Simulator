package Logic;
import Model.*;
import View.*;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable {
    public int maxTime;
    public int maxServiceTime;
    public int minServiceTime;
    public int nrOfQueues;
    public int nrOfClients;
    private ArrayList<Client> generatedClients;
    private ArrayList<TQueue> availableQueues;
    private GUI simulationGUI;

    public SimulationManager(GUI simulationGUI) {
        this.simulationGUI = simulationGUI; //link to view
        generatedClients = generateRandomClients();
        availableQueues = new ArrayList<TQueue>(nrOfQueues);
        //TODO: Start the threads
        //TODO: Get information from the GUI
    }

    public AtomicInteger globalSimulationTime;
    @Override
    public void run() {
        globalSimulationTime.getAndIncrement();
        for (Client currClient:
             generatedClients) {
            if(currClient.getTArrival() >= globalSimulationTime.get()) {
                //select the TQueue with the smallest waitingTime
                TQueue selectedQueue = getMinTimeQueue();
                selectedQueue.addClient(currClient);
            }
        }
    }

    public ArrayList<Client> generateRandomClients() {
        //TODO:generate nrOfClients clients randomly with their times within the given range
        //sort based on arrival time such that the lowest arrival time is taken at each point
        return null;
    }

    public TQueue getMinTimeQueue() {
        TQueue minQueue = availableQueues.get(0);
        for (TQueue current:
             availableQueues) {
            if (current.getWaitingPeriod().get() < current.getWaitingPeriod().get()) {
                minQueue = current;
            }
        }
        return  minQueue;
    }
}
