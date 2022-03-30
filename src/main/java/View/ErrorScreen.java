package View;

import javax.swing.*;

public class ErrorScreen extends JFrame{
    private JPanel mainPanel;
    private JLabel errorLabel;

    public ErrorScreen(String errorMessage) {
        setTitle("Invalid input");
        setSize(700, 80);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(mainPanel);
        errorLabel.setText(errorMessage);
    }
}
