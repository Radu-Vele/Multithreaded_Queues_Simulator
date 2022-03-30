package Logic;
import Model.*;
import View.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Random;
import java.util.random.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable {
    private int maxTime;
    private int maxServiceTime;
    private int minServiceTime;
    private int minArrivalTime;
    private int maxArrivalTime;
    private int nrOfQueues;
    private int nrOfClients;
    private File outputFile;
    private FileWriter outputWriter;
    private ArrayList<Client> generatedClients;
    private ArrayList<TQueue> availableQueues;
    private ArrayList<JLabel> labelsQueues;
    private JLabel timeLabel;
    private JLabel waitingLabel;
    private JLabel serviceTimeLabel;
    private JLabel peakHourLabel;
    private JLabel waitingTimeLabel;
    public static AtomicInteger globalSimulationTime;
    private final GUI simulationGUI;
    public SimulationManager(GUI simulationGUI) {
        this.simulationGUI = simulationGUI;
    }

    public boolean prepareSimulation () { //return true or false to validate the preparation
        try {
            this.maxTime = Integer.parseInt(this.simulationGUI.gettSim().getText());
            this.maxServiceTime = Integer.parseInt(this.simulationGUI.getMaxService().getText());
            this.minServiceTime = Integer.parseInt(this.simulationGUI.getMinService().getText());
            this.minArrivalTime = Integer.parseInt(this.simulationGUI.getMinArrival().getText());
            this.maxArrivalTime = Integer.parseInt(this.simulationGUI.getMaxArrival().getText());
            this.nrOfQueues = Integer.parseInt(this.simulationGUI.getNrQueues().getText());
            this.nrOfClients = Integer.parseInt(this.simulationGUI.getNrClients().getText());
        } catch (NumberFormatException e) {
            ErrorScreen errorScreen = new ErrorScreen("Error! The inserted numbers are not integers");
            return false;
        }

        globalSimulationTime = new AtomicInteger();
        if((generatedClients = this.generateRandomClients()) == null) {
            return false;
        }

        this.timeLabel = new JLabel("Current time: 0");
        this.waitingLabel = new JLabel("Waiting Clients: -");
        generatedClients = generateRandomClients();
        availableQueues = new ArrayList<TQueue>(nrOfQueues);
        simulationGUI.getQueuesPanel().removeAll();
        simulationGUI.getQueuesPanel().setLayout(new GridLayout(nrOfQueues + 6, 1));
        simulationGUI.getQueuesPanel().add(timeLabel);
        simulationGUI.getQueuesPanel().add(waitingLabel);

        this.labelsQueues = new ArrayList<JLabel>(nrOfQueues);
        for(int i = 0; i < nrOfQueues; i++) { //start threads
            TQueue newQueue = new TQueue();
            availableQueues.add(newQueue);
            Thread t = new Thread(newQueue); //create a thread for each queue
            String initLabel = "Queue " + Integer.toString(i + 1) + ": closed";
            this.labelsQueues.add(new JLabel(initLabel));
            simulationGUI.getQueuesPanel().add(this.labelsQueues.get(i));
            t.start();
        }

        serviceTimeLabel = new JLabel("Average Service Time: -");
        waitingTimeLabel = new JLabel("Average Waiting Time: -");
        peakHourLabel = new JLabel("Peak Hour: -");

        simulationGUI.getQueuesPanel().add(new Label(" "));
        simulationGUI.getQueuesPanel().add(waitingTimeLabel);
        simulationGUI.getQueuesPanel().add(serviceTimeLabel);
        simulationGUI.getQueuesPanel().add(peakHourLabel);

        simulationGUI.pack();
        return true;
    }

    @Override
    public void run() {
        //compute service time & other stats--
        int maxClientsInQueues = 0;
        int peakHour = 0;
        int currClientsInQueues = 0;
        double avgServiceTime = 0;

        for(Client client:generatedClients) {
            avgServiceTime += client.getTService();
        }
        avgServiceTime /= nrOfClients;
        serviceTimeLabel.setText("Average Waiting Time: " + Double.toString(avgServiceTime));
        //---
        //Delete previously existing files
        File toDel = new File("output.txt");
        toDel.delete();
        //---
        try {
            this.outputFile = new File("output.txt");
            if(!outputFile.createNewFile()) {
                System.out.println("Output file not created\n");
            }
            this.outputWriter = new FileWriter("output.txt");
            this.outputWriter.write(">>> LOG OF EVENTS <<<\n-------------------------\n");

        } catch (IOException e) {
            System.out.println("Error while creating output file\n");
        }

        while(globalSimulationTime.get() < maxTime) {
            globalSimulationTime.getAndIncrement(); //start from time = 1

            if(!generatedClients.isEmpty()) {
                boolean condition = true;
                while(condition) {
                    if(generatedClients.isEmpty()) {
                        condition = false;
                    }
                    else {
                        Client toSend = generatedClients.get(0);
                        if (toSend.getTArrival() <= globalSimulationTime.get()) {
                            TQueue chosen = getMinTimeQueue();
                            chosen.addClient(toSend);
                            generatedClients.remove(toSend);
                        } else {
                            condition = false;
                        }
                    }
                }
            }

            try {
                Thread.sleep(1000); //stop thread until client is served in seconds
            } catch (InterruptedException e) {
                System.out.println("\nInterrupted thread sleep!");
                return;
            }

            //printing to file and gui ------------------------------------------------------------------------
            String waiting = "Waiting clients: ";
            String timeText = new String("Current time: " + Integer.toString(globalSimulationTime.get()));
            currClientsInQueues = 0;
            try {
                this.outputWriter.write("Time" + " " + globalSimulationTime.get() + ":\n");
                waiting = new String("Waiting clients: ");
                for (Client currClient : generatedClients) {
                    waiting = waiting + currClient.printFriendly();
                }
                this.outputWriter.write(waiting);
                for (int i = 0; i < nrOfQueues; i++) {
                    String queueContent = new String("\nQueue " + Integer.toString(i + 1) + ": " + availableQueues.get(i).printContents());
                    outputWriter.write(queueContent);
                    labelsQueues.get(i).setText(queueContent);
                    currClientsInQueues += availableQueues.get(i).getQ().size();
                }

                timeLabel.setText(timeText);
                waitingLabel.setText(waiting);
                outputWriter.write("\n--------------------------------------------------------------------------\n");
            } catch(IOException e) {
                System.out.println("Error encountered while writing in file\n");
            }

            if(currClientsInQueues > maxClientsInQueues) {
                peakHour = globalSimulationTime.get();
                maxClientsInQueues = currClientsInQueues;
            }
        }

        try {
            outputWriter.close();
        } catch (IOException e) {
            System.out.println("Error encountered while closing the file writer\n");
        }
        //stop queues threads and compute avg waiting time
        int totalWaitingTime = 0;
        int totalClientsServed = 0;

        for(TQueue currQueue: availableQueues) {
            currQueue.setSimRunning(false);
            totalWaitingTime += currQueue.getTotalWaitingTime().get();
            totalClientsServed += currQueue.getTotalClientsServed().get();
        }
        double avgWaitingTime = (double) totalWaitingTime / (double) totalClientsServed;
        waitingTimeLabel.setText("Average Waiting Time: " + Double.toString(avgWaitingTime));
        peakHourLabel.setText("Peak Hour: " + Integer.toString(peakHour));
        System.out.println("Simulation ended!");
    }

    public ArrayList<Client> generateRandomClients(){
        ArrayList<Client> toReturn = new ArrayList<Client>(nrOfClients);

        try {
            Random randomGen = new Random();
            boolean[] validID = new boolean[nrOfClients + 1];
            for (int i = 0; i < nrOfClients + 1; i++) {
                validID[i] = true;
            }

            for (int i = 0; i < this.nrOfClients; i++) { //clients generation
                int randID;
                while (true) { //unique ID for each client
                    int index = randomGen.nextInt(1, nrOfClients + 1);
                    if (validID[index]) {
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
        } catch (IllegalArgumentException e) {
            ErrorScreen errorScreen = new ErrorScreen("Error! You inserted maximum values smaller than minimum values!");
            return null;
        }
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
}
