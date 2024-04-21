import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Comparator;

class PriorityScheduling extends JFrame implements ActionListener {
    JButton jb[] = new JButton[3];
    JTextField jt1[], jt2[], jt3[];
    JLabel jl[], jl1, jl2, jl3, jl4;
    JPanel jp, jp1, jp2, readyQueuePanel;
    Container con;
    int k, p;
    String str[] = {"SUBMIT", "RESET", "EXIT"};
    String str1[] = {"Process", "Arrival Time", "Burst Time", "Priority", "Waiting Time", "Turnaround Time", "Normalized Turnaround Time"};
    DefaultTableModel model;
    JProgressBar[] progressBars;

    public PriorityScheduling() {
        super("Priority Scheduling Algorithm");
        con = getContentPane();

        k = Integer.parseInt(JOptionPane.showInputDialog("Enter number of processes"));

        jl1 = new JLabel("Process");
        jl2 = new JLabel("Arrival Time");
        jl3 = new JLabel("Burst Time");
        jl4 = new JLabel("Priority");

        jl = new JLabel[k];
        jt1 = new JTextField[k];
        jt2 = new JTextField[k];
        jt3 = new JTextField[k];

        progressBars = new JProgressBar[k];

        for (int i = 0; i < k; i++) {
            jl[i] = new JLabel("process" + (i + 1));
            jt1[i] = new JTextField(10);
            jt2[i] = new JTextField(10);
            jt3[i] = new JTextField(10);
        }

        for (int i = 0; i < 3; i++) {
            jb[i] = new JButton(str[i]);
        }

        con.setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(k + 2, 4));
        inputPanel.add(jl1);
        inputPanel.add(jl2);
        inputPanel.add(jl3);
        inputPanel.add(jl4);

        int l = 0;
        for (int i = 0; i < k; i++) {
            inputPanel.add(jl[l]);
            inputPanel.add(jt1[l]);
            inputPanel.add(jt2[l]);
            inputPanel.add(jt3[l]);
            l++;
        }
        l = 0;
        for (int i = 0; i < 3; i++) {
            inputPanel.add(jb[l]);
            jb[l].addActionListener(this);
            l++;
        }

        jp = new JPanel(new GridLayout(k + 1, 7));
        for (int i = 0; i < str1.length; i++) {
            jp.add(new JLabel(str1[i]));
        }

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
            int WT[] = new int[k];
            int TAT[] = new int[k];
            float NTAT[] = new float[k];
            float sum = 0;

            model = new DefaultTableModel(0, 7);
            model.setColumnIdentifiers(str1);

            // Create an array to hold the processes
            MyProcess[] processes = new MyProcess[k];
            for (int i = 0; i < k; i++) {
                int arrivalTime = parseTextField(jt1[i]);
                int burstTime = parseTextField(jt2[i]);
                int priority = parseTextField(jt3[i]);
                processes[i] = new MyProcess(arrivalTime, burstTime, priority, i + 1);
            }

            // Sort processes based on arrival time and priority (higher priority first)
            Arrays.sort(processes, Comparator.comparingInt((MyProcess p) -> p.arrivalTime)
                    .thenComparingInt((MyProcess p) -> -p.priority));

            // Initialize currentTime
            int currentTime = 0;

            // Process the sorted processes
            for (int i = 0; i < k; i++) {
                MyProcess currentProcess = processes[i];
                int AT = currentProcess.arrivalTime;
                int BT = currentProcess.burstTime;

                // If the current time is less than arrival time, wait until the arrival time
                if (currentTime < AT) {
                    currentTime = AT;
                }

                // Calculate waiting time, turnaround time, and normalized turnaround time
                WT[i] = currentTime - AT;
                TAT[i] = WT[i] + BT;
                NTAT[i] = (float) TAT[i] / BT;
                sum += WT[i];

                // Add the process details to the table model
                model.addRow(new Object[]{currentProcess.processId, AT, BT, currentProcess.priority, WT[i], TAT[i], NTAT[i]});

                // Update currentTime
                currentTime += BT;
            }

            // Create the output frame and display results
            JFrame outputFrame = new JFrame("Output");
            outputFrame.setLayout(new BorderLayout());
            outputFrame.setSize(600, 400);

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            outputFrame.add(scrollPane, BorderLayout.CENTER);

            JLabel avgLabel = new JLabel("Average Waiting Time: " + (sum / k));
            outputFrame.add(avgLabel, BorderLayout.SOUTH);

            // Adding progress bars to visualize execution
            JPanel progressPanel = new JPanel(new GridLayout(k, 1));
            JLabel[] processLabels = new JLabel[k];
            for (int i = 0; i < k; i++) {
                progressBars[i] = new JProgressBar(0, processes[i].burstTime);
                processLabels[i] = new JLabel("Process " + processes[i].processId);
                progressPanel.add(processLabels[i]);
                progressPanel.add(progressBars[i]);
            }
            outputFrame.add(progressPanel, BorderLayout.NORTH);

            // Start the timer for progress bars
            Timer timer = new Timer(1000, new ActionListener() {
                int processIndex = 0;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (processIndex < k) {
                        // Update progress bar value
                        progressBars[processIndex].setValue(progressBars[processIndex].getValue() + 1);
                        // Set the label of the progress bar to the process number
                        progressBars[processIndex].setString("Process " + processes[processIndex].processId);
                        if (progressBars[processIndex].getValue() == progressBars[processIndex].getMaximum()) {
                            processIndex++;
                        }
                    }
                }
            });
            timer.start();

            outputFrame.setVisible(true);
        }
        else if (ae.getSource() == jb[1]) {
            setVisible(false);
            PriorityScheduling window = new PriorityScheduling();
            window.setSize(800, 600); // Increased size
            window.setVisible(true);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }

    // Parse text field input to integer, handling empty strings
    // Parse text field input to integer, handling empty strings
    private int parseTextField(JTextField textField) {
        String text = textField.getText();
        if (!text.isEmpty()) {
            return Integer.parseInt(text);
        } else {
            return 0; // Return default value if text field is empty
        }
    }

    public static void main(String[] args) {
        PriorityScheduling window = new PriorityScheduling();
        window.setSize(800, 600); // Increased size
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Helper class to represent a process
    class MyProcess {
        int arrivalTime;
        int burstTime;
        int priority;
        int processId;

        public MyProcess(int arrivalTime, int burstTime, int priority, int processId) {
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.priority = priority;
            this.processId = processId;
        }
    }
}