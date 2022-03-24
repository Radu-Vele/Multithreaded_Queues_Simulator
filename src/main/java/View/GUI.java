package View;
import Logic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {
    private JPanel mainPanel;
    private JPanel statePanel;
    private JPanel inputsPanel;
    private JPanel numberInsert;
    private JTextField textField1;
    private JTextField textField2;
    private JLabel queuesLabel;
    private JLabel clientsLabel;
    private JPanel simulationInsert;
    private JLabel intervalLabel;
    private JTextField textField3;
    private JLabel arrivalLabel;
    private JLabel minArrivalLabel;
    private JLabel maxArrivalLabel;
    private JTextField minArrival;
    private JTextField maxArrival;
    private JLabel serviceLabel;
    private JLabel minServiceLabel;
    private JLabel maxServiceLabel;
    private JTextField minService;
    private JTextField maxService;
    private JButton simulateButton;
    //TODO: set-up GUI output

    SimulationManager simulationManager = new SimulationManager();

    public GUI() throws HeadlessException {
        setTitle("Queue Management Simulator");
        setSize(500, 800);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(mainPanel);
    }

}
