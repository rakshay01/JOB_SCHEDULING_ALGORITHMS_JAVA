import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

class RoundRobin extends JFrame implements ActionListener {
    JButton jb[] = new JButton[3];
    JTextField jt1[], jt2[], jt3;
    JLabel jl[], jl1, jl2, jl3;
    JPanel jp, jp1, jp2, readyQueuePanel, progressPanel;
    Container con;
    int k, quantum;
    String str[] = {"SUBMIT", "RESET", "EXIT"};
    String str1[] = {"Process", "AT", "BT", "WT", "TAT"};
    JProgressBar[] progressBars;
    DefaultTableModel model;

    public RoundRobin() {
        super("Round Robin Scheduling Algorithm");
        con = getContentPane();

        k = Integer.parseInt(JOptionPane.showInputDialog("Enter number of processes"));
        quantum = Integer.parseInt(JOptionPane.showInputDialog("Enter time quantum"));

        jl1 = new JLabel("Process");
        jl2 = new JLabel("Arrival Time");
        jl3 = new JLabel("Burst Time");

        jl = new JLabel[k];
        jt1 = new JTextField[k];
        jt2 = new JTextField[k];
        jt3 = new JTextField(10);

        progressBars = new JProgressBar[k];

        for (int i = 0; i < k; i++) {
            jl[i] = new JLabel("process" + (i + 1));
            jt1[i] = new JTextField(10);
            jt2[i] = new JTextField(10);
        }

        for (int i = 0; i < 3; i++) {
            jb[i] = new JButton(str[i]);
        }

        con.setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(k + 3, 3));
        inputPanel.add(jl1);
        inputPanel.add(jl2);
        inputPanel.add(jl3);

        int l = 0;
        for (int i = 0; i < k; i++) {
            inputPanel.add(jl[l]);
            inputPanel.add(jt1[l]);
            inputPanel.add(jt2[l]);
            l++;
        }
        inputPanel.add(new JLabel("Time Quantum"));
        inputPanel.add(jt3);

        l = 0;
        for (int i = 0; i < 3; i++) {
            inputPanel.add(jb[l]);
            jb[l].addActionListener(this);
            l++;
        }

        jp = new JPanel(new GridLayout(k + 1, 5));
        jp.add(new JLabel(str1[0]));
        jp.add(new JLabel(str1[1]));
        jp.add(new JLabel(str1[2]));
        jp.add(new JLabel(str1[3]));
        jp.add(new JLabel(str1[4]));

        readyQueuePanel = new JPanel(new GridLayout(1, k + 2));
        jp2 = new JPanel(new BorderLayout());
        jp2.add(jp, BorderLayout.NORTH);
        jp2.add(readyQueuePanel, BorderLayout.SOUTH);

        con.add(inputPanel, BorderLayout.NORTH);
        con.add(jp2, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == jb[2]) {
            System.exit(0);
        } else if (ae.getSource() == jb[0]) {
            int arrivalTime[] = new int[k];
            int burstTime[] = new int[k];
            int remainingTime[] = new int[k];
            int waitingTime[] = new int[k];
            int turnaroundTime[] = new int[k];

            model = new DefaultTableModel(0, 5);
            model.setColumnIdentifiers(str1);

            // Retrieve input data
            for (int i = 0; i < k; i++) {
                arrivalTime[i] = Integer.parseInt(jt1[i].getText());
                burstTime[i] = Integer.parseInt(jt2[i].getText());
                remainingTime[i] = burstTime[i];
            }

            int currentTime = 0;
            boolean done = false;

            while (!done) {
                done = true;
                for (int i = 0; i < k; i++) {
                    if (remainingTime[i] > 0) {
                        done = false;
                        if (remainingTime[i] > quantum) {
                            currentTime += quantum;
                            remainingTime[i] -= quantum;
                        } else {
                            currentTime += remainingTime[i];
                            waitingTime[i] = currentTime - arrivalTime[i] - burstTime[i];
                            remainingTime[i] = 0;
                        }
                    }
                }
            }

            // Calculate turnaround time
            for (int i = 0; i < k; i++) {
                turnaroundTime[i] = burstTime[i] + waitingTime[i];
            }

            // Add data to the table model
            for (int i = 0; i < k; i++) {
                model.addRow(new Object[]{i + 1, arrivalTime[i], burstTime[i], waitingTime[i], turnaroundTime[i]});
            }

            // Create progress bars panel
            progressPanel = new JPanel(new GridLayout(k, 1));

            // Add progress bars for each process with random colors
            Random rand = new Random();
            for (int i = 0; i < k; i++) {
                progressBars[i] = new JProgressBar(0, burstTime[i]);
                progressBars[i].setForeground(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
                progressBars[i].setStringPainted(true);
                progressPanel.add(progressBars[i]);
            }

            // Create output frame
            JFrame outputFrame = new JFrame("Output");
            outputFrame.setLayout(new BorderLayout());
            outputFrame.setSize(600, 400);

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            outputFrame.add(scrollPane, BorderLayout.CENTER);

            outputFrame.add(progressPanel, BorderLayout.SOUTH);

            outputFrame.setVisible(true);

            // Start a timer to update progress bars
            Timer timer = new Timer(1000, new ActionListener() {
                int timeElapsed = 0;
                public void actionPerformed(ActionEvent e) {
                    timeElapsed++;
                    for (int i = 0; i < k; i++) {
                        if (remainingTime[i] > 0 && remainingTime[i] >= timeElapsed) {
                            progressBars[i].setValue(burstTime[i] - remainingTime[i] + timeElapsed);
                        } else if (remainingTime[i] <= 0) {
                            progressBars[i].setValue(burstTime[i]);
                        }
                    }
                    if (timeElapsed >= quantum) {
                        timeElapsed = 0;
                    }
                }
            });
            timer.start();
        } else if (ae.getSource() == jb[1]) {
            setVisible(false);
            RoundRobin window = new RoundRobin();
            window.setSize(800, 600);
            window.setVisible(true);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }

    public static void main(String[] args) {
        RoundRobin window = new RoundRobin();
        window.setSize(800, 600);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}