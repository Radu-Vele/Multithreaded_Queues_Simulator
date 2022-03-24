package Logic;
import Model.*;
import View.*;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.random.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable {
    private int maxTime = 20;
    private int maxServiceTime = 5;
    private int minServiceTime = 3;
    private int minArrivalTime = 2;
    private int maxArrivalTime = 15;
    private int nrOfQueues = 2;
    private int nrOfClients = 10;

    private ArrayList<Client> generatedClients;
    private ArrayList<TQueue> availableQueues;
    public AtomicInteger globalSimulationTime;
    //private GUI simulationGUI; TODO: later

    public SimulationManager() {
        generatedClients = generateRandomClients();
        availableQueues = new ArrayList<TQueue>(nrOfQueues);

        for(int i = 0; i < nrOfQueues; i++) { //start threads
            TQueue newQueue = new TQueue();
            availableQueues.add(newQueue);
            Thread t = new Thread(newQueue); //create a thread for each queue
            t.start();
        }

        globalSimulationTime = new AtomicInteger();
        generatedClients = this.generateRandomClients();
    }

    @Override
    public void run() {
        while(globalSimulationTime.get() < maxTime) {
            globalSimulationTime.getAndIncrement(); //start from time = 1

            if(!generatedClients.isEmpty()) {
                Client toSend = generatedClients.get(0);
                if (toSend.getTArrival() <= globalSimulationTime.get()) { //TODO: if more arrive at the same time?
                    TQueue chosen = getMinTimeQueue();
                    chosen.addClient(toSend);
                    generatedClients.remove(toSend);
                }
            }

            try {
                Thread.sleep(1000); //stop thread until client is served in seconds
            } catch (InterruptedException e) {
                System.out.println("\nInterrupted thread sleep!");
                return;
            }
            //TODO: generate txt file
            System.out.println("Time" + " " + globalSimulationTime.get() + ":");
            String waiting = new String("Waiting clients: ");
            for (Client currClient: generatedClients) {
                waiting = waiting + currClient.printFriendly();
            }
            System.out.println(waiting);

            for(int i = 0; i < nrOfQueues; i++) {
                System.out.println("Queue " + Integer.toString(i + 1) + ":" + availableQueues.get(i).printContents());
            }

            //TODO later: update UI Frame
        }

        for(TQueue currQueue: availableQueues) {
            currQueue.setSimRunning(false);
        }
    }

    public ArrayList<Client> generateRandomClients() {
        ArrayList<Client> toReturn = new ArrayList<Client>(nrOfClients);
        Random randomGen = new Random();
        boolean [] validID = new boolean[nrOfClients + 1];

        for(int i = 0; i < nrOfClients + 1; i++) {
            validID[i] = true;
        }

        for(int i = 0; i < this.nrOfClients; i++) { //clients generation
            int randID;
            while(true) { //unique ID for each client
                int index = randomGen.nextInt(1, nrOfClients + 1);
                if(validID[index]) {
                    randID = index;
                    validID[index] = false;
                    break;
                }
            }

            int randArrival = randomGen.nextInt(this.minArrivalTime, this.maxArrivalTime + 1);
            int randService = randomGen.nextInt(minServiceTime, maxServiceTime + 1);
            Client toAdd = new Client(randID, randArrival, randService);
            toReturn.add(toAdd);
        }

        Collections.sort(toReturn);
        return toReturn;
    }

    public TQueue getMinTimeQueue() {
        TQueue minQueue = availableQueues.get(0);

        for (TQueue current: availableQueues) {
            if (current.getWaitingPeriod().get() < minQueue.getWaitingPeriod().get()) {
                minQueue = current;
            }
        }
        return  minQueue;
    }

    public static void main(String[] args) {
        SimulationManager sim = new SimulationManager();
        Thread tMain = new Thread(sim);
        tMain.start();
    }
}
