import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Comparator;

class SJN extends JFrame implements ActionListener {
    JButton jb[] = new JButton[3];
    JTextField jt1[], jt2[];
    JLabel jl[], jl1, jl2, jl3;
    JPanel jp, jp1, jp2, readyQueuePanel;
    Container con;
    int k, p;
    String str[] = {"SUBMIT", "RESET", "EXIT"};
    String str1[] = {"Process", "AT", "ST", "WT", "FT", "TAT", "NTAT"};
    JProgressBar[] progressBars;
    DefaultTableModel model;

    public SJN() {
        super("SJN Scheduling Algorithm");
        con = getContentPane();

        k = Integer.parseInt(JOptionPane.showInputDialog("Enter number of process"));

        jl1 = new JLabel("Process");
        jl2 = new JLabel("Arrival Time");
        jl3 = new JLabel("Service Time");

        jl = new JLabel[k];
        jt1 = new JTextField[k];
        jt2 = new JTextField[k];

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
        JPanel inputPanel = new JPanel(new GridLayout(k + 2, 3));
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
        l = 0;
        for (int i = 0; i < 3; i++) {
            inputPanel.add(jb[l]);
            jb[l].addActionListener(this);
            l++;
        }

        jp = new JPanel(new GridLayout(k + 1, 7));
        jp.add(new JLabel(str1[0]));
        jp.add(new JLabel(str1[1]));
        jp.add(new JLabel(str1[2]));
        jp.add(new JLabel(str1[3]));
        jp.add(new JLabel(str1[4]));
        jp.add(new JLabel(str1[5]));
        jp.add(new JLabel(str1[6]));

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
            int FT[] = new int[k];
            int WT[] = new int[k];
            int TAT[] = new int[k];
            float NTAT[] = new float[k];
            float sum = 0;

            model = new DefaultTableModel(0, 7);
            model.setColumnIdentifiers(str1);

            // Create an array to hold the processes
            MyProcess[] processes = new MyProcess[k];
            for (int i = 0; i < k; i++) {
                processes[i] = new MyProcess(Integer.parseInt(jt1[i].getText()), Integer.parseInt(jt2[i].getText()), i + 1);
            }

            // Sort processes based on arrival time and service time
            Arrays.sort(processes, Comparator.comparingInt(p -> p.serviceTime));

            // Initialize currentTime
            int currentTime = 0;

            // Process the sorted processes
            for (int i = 0; i < k; i++) {
                MyProcess currentProcess = processes[i];
                int AT = currentProcess.arrivalTime;
                int ST = currentProcess.serviceTime;

                // If the current time is less than arrival time, wait until the arrival time
                if (currentTime < AT) {
                    currentTime = AT;
                }

                // Calculate finish time, waiting time, turnaround time, and normalized turnaround time
                FT[i] = currentTime + ST;
                WT[i] = currentTime - AT;
                TAT[i] = WT[i] + ST;
                NTAT[i] = (float) TAT[i] / ST;
                sum += WT[i];

                // Add the process details to the table model
                model.addRow(new Object[]{currentProcess.processId, AT, ST, WT[i], FT[i], TAT[i], NTAT[i]});

                // Update currentTime
                currentTime += ST;
            }

            // Create and update progress bars sequentially
            JPanel progressPanel = new JPanel(new GridLayout(k, 1));

            // Ensure that the number of processes matches the number of progress bars
            if (processes.length != k) {
                System.out.println("Number of processes does not match the number of progress bars.");
                return;
            }

            // Sort processes based on service time
            Arrays.sort(processes, Comparator.comparingInt(p -> p.serviceTime));

            for (int i = 0; i < k; i++) {
                progressBars[i] = new JProgressBar(0, Integer.parseInt(jt2[i].getText()));
                progressBars[i].setForeground(Color.getHSBColor((float) Math.random(), 1.0f, 1.0f));

                // Set the name of the progress bar to the process ID
                progressBars[i].setName(String.valueOf(processes[i].processId));

                JPanel processPanel = new JPanel(new BorderLayout());
                processPanel.add(new JLabel("Process " + processes[i].processId + ": "), BorderLayout.WEST);
                processPanel.add(progressBars[i], BorderLayout.CENTER);

                progressPanel.add(processPanel);
            }

            // Start the timer after adding progress bars
            Timer timer = new Timer(1000, new ActionListener() {
                int currentIndex = 0;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (currentIndex >= k) {
                        ((Timer) e.getSource()).stop();
                        return;
                    }

                    progressBars[currentIndex].setValue(progressBars[currentIndex].getValue() + 1);
                    if (progressBars[currentIndex].getValue() == progressBars[currentIndex].getMaximum()) {
                        currentIndex++;
                    }
                }
            });
            timer.start();

            // Create the output frame and display results
            JFrame outputFrame = new JFrame("Output");
            outputFrame.setLayout(new BorderLayout());
            outputFrame.setSize(600, 400);

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            outputFrame.add(scrollPane, BorderLayout.CENTER);

            JLabel avgLabel = new JLabel("Average Waiting Time: " + (sum / k));
            outputFrame.add(avgLabel, BorderLayout.SOUTH);

            outputFrame.add(progressPanel, BorderLayout.NORTH);

            outputFrame.setVisible(true);
        } else if (ae.getSource() == jb[1]) {
            setVisible(false);
            SJN window = new SJN();
            window.setSize(800, 600); // Increased size
            window.setVisible(true);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }

    public static void main(String[] args) {
        SJN window = new SJN();
        window.setSize(800, 600); // Increased size
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Helper class to represent a process
    class MyProcess {
        int arrivalTime;
        int serviceTime;
        int processId;

        public MyProcess(int arrivalTime, int serviceTime, int processId) {
            this.arrivalTime = arrivalTime;
            this.serviceTime = serviceTime;
            this.processId = processId;
        }
    }
}