package View;
import Logic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {

    JPanel mainPanel;
    private JPanel inputPanel;
    private JTextField nrClients;
    private JTextField nrQueues;
    private JTextField tSim;
    private JTextField minArrival;
    private JTextField maxArrival;
    private JTextField minService;
    private JTextField maxService;
    private JButton STARTButton;
    private JPanel inputPanel2;
    private JLabel nrClientsLabel;
    private JLabel nrQueuesLabel;
    private JLabel tSimLabel;
    private JLabel minArrLabel;
    private JLabel maxArrLabel;
    private JLabel minSerLabel;
    private JLabel maxSerLabel;
    private JLabel startLabel;
    private JPanel queuesPanel;

    SimulationManager simulationManager;

    public GUI() throws HeadlessException {
        setTitle("Queue Management Simulator");
        setSize(680, 300);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(mainPanel);
        simulationManager = new SimulationManager(this);

        STARTButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // when button is pressed -> fetch data, validate, start simulation
                simulationManager.prepareSimulation();
                Thread tMain = new Thread(simulationManager);
                tMain.start();
                System.out.println("Simulation in progress...");
            }
        });
    }

    public JTextField getNrClients() {
        return nrClients;
    }

    public JTextField getNrQueues() {
        return nrQueues;
    }

    public JTextField gettSim() {
        return tSim;
    }

    public JTextField getMinArrival() {
        return minArrival;
    }

    public JTextField getMaxArrival() {
        return maxArrival;
    }

    public JTextField getMinService() {
        return minService;
    }

    public JTextField getMaxService() {
        return maxService;
    }

    public JPanel getQueuesPanel() {
        return queuesPanel;
    }
}
