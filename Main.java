import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Process Scheduling Algorithm Selector");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(Color.WHITE); // Set background color
        JLabel headerLabel = new JLabel("Select the process scheduling algorithm to execute:");
        headerPanel.add(headerLabel);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        buttonPanel.setBackground(Color.WHITE); // Set background color

        // Add buttons with colorful backgrounds and changed text color
        addButton("First-Come, First-Served (FCFS)", Color.BLUE, Color.WHITE, buttonPanel, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to execute FCFS scheduling algorithm
                FCFS.main(null);
            }
        });

        addButton("Round Robin (RR)", Color.RED, Color.WHITE, buttonPanel, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to execute RR scheduling algorithm
                RoundRobin.main(null);
            }
        });

        addButton("Shortest Job Next (SJN)", Color.GREEN, Color.WHITE, buttonPanel, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to execute SJN scheduling algorithm
                SJN.main(null);
            }
        });

        addButton("Highest Response Ratio Next (HRRN)", Color.ORANGE, Color.WHITE, buttonPanel, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to execute HRRN scheduling algorithm
                HRRN.main(null);
            }
        });

        addButton("Priority Scheduling", Color.MAGENTA, Color.WHITE, buttonPanel, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Code to execute Priority Scheduling algorithm
                PriorityScheduling.main(null);
            }
        });

        // Add header and button panel to frame
        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);

        // Center the frame on the screen
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }

    // Helper method to create and add a colorful button with changed text color
    private static void addButton(String label, Color bgColor, Color textColor, JPanel panel, ActionListener listener) {
        JButton button = new JButton(label);
        button.setBackground(bgColor);
        button.setForeground(textColor); // Set text color
        button.addActionListener(listener);
        panel.add(button);
    }
}
