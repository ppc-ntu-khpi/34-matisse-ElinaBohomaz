package com.mybank.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {
    private JPanel mainPanel;
    private JComboBox<String> clientsComboBox;
    private JTextArea textArea;
    private JButton showBtn;
    private JButton reportBtn;
    private JButton aboutBtn;

    private List<Client> clients;
    private static final String DATA_FILE = "C:\\Users\\Еля\\IdeaProjects\\Matisse\\data\\test.dat";

    public Main() {
        clients = new ArrayList<>();
        initializeUI();
        setupComponents();
        setupListeners();
        loadClientsFromFile();
    }

    private void initializeUI() {
        setTitle("MyBank Clients");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void setupComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("MyBank Clients");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        JPanel topControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientsComboBox = new JComboBox<>();
        clientsComboBox.setPreferredSize(new Dimension(200, 25));
        topControlsPanel.add(clientsComboBox);
        centerPanel.add(topControlsPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createLoweredBevelBorder());
        textArea.setBackground(Color.WHITE);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 250));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(new EmptyBorder(0, 10, 0, 0));

        showBtn = new JButton("Show");
        showBtn.setPreferredSize(new Dimension(80, 30));
        showBtn.setMaximumSize(new Dimension(80, 30));

        reportBtn = new JButton("Report");
        reportBtn.setPreferredSize(new Dimension(80, 30));
        reportBtn.setMaximumSize(new Dimension(80, 30));

        aboutBtn = new JButton("About");
        aboutBtn.setPreferredSize(new Dimension(80, 30));
        aboutBtn.setMaximumSize(new Dimension(80, 30));

        buttonPanel.add(showBtn);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(reportBtn);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(aboutBtn);
        buttonPanel.add(Box.createVerticalGlue());

        contentPanel.add(buttonPanel, BorderLayout.EAST);

        centerPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void setupListeners() {
        showBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showClientDetails();
            }
        });

        reportBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });

        aboutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAbout();
            }
        });
    }

    private void loadClientsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line = reader.readLine();
            int numClients = Integer.parseInt(line.trim());

            for (int i = 0; i < numClients; i++) {
                line = reader.readLine();
                if (line != null) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length >= 3) {
                        String firstName = parts[0];
                        String lastName = parts[1];
                        int numAccounts = Integer.parseInt(parts[2]);

                        Client client = new Client(firstName, lastName);

                        for (int j = 0; j < numAccounts; j++) {
                            line = reader.readLine();
                            if (line != null) {
                                String[] accountParts = line.trim().split("\\s+");
                                if (accountParts.length >= 2) {
                                    String accountType = accountParts[0];
                                    double balance = Double.parseDouble(accountParts[1]);

                                    if (accountType.equals("S")) {
                                        double interestRate = accountParts.length > 2 ?
                                                Double.parseDouble(accountParts[2]) : 0.0;
                                        client.addAccount(new SavingsAccount(balance, interestRate));
                                    } else if (accountType.equals("C")) {
                                        double overdraftLimit = accountParts.length > 2 ?
                                                Double.parseDouble(accountParts[2]) : 0.0;
                                        client.addAccount(new CheckingAccount(balance, overdraftLimit));
                                    }
                                }
                            }
                        }
                        clients.add(client);
                        clientsComboBox.addItem(client.getFullName());
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
            loadDefaultClients();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error parsing file data: " + e.getMessage(),
                    "Data Error", JOptionPane.ERROR_MESSAGE);
            loadDefaultClients();
        }
    }

    private void loadDefaultClients() {
        clientsComboBox.addItem("John Doe");
        clientsComboBox.addItem("Jane Smith");
        clientsComboBox.addItem("Robert Johnson");
    }

    private void showClientDetails() {
        int selectedIndex = clientsComboBox.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < clients.size()) {
            Client client = clients.get(selectedIndex);
            StringBuilder details = new StringBuilder();
            DecimalFormat df = new DecimalFormat("#0.00");

            details.append("Client Details:\n\n");
            details.append("Name: ").append(client.getFullName()).append("\n");
            details.append("Number of Accounts: ").append(client.getAccounts().size()).append("\n\n");

            for (int i = 0; i < client.getAccounts().size(); i++) {
                Account account = client.getAccounts().get(i);
                details.append("Account ").append(i + 1).append(":\n");
                details.append("  Type: ").append(account.getAccountType()).append("\n");
                details.append("  Balance: $").append(df.format(account.getBalance())).append("\n");

                if (account instanceof SavingsAccount) {
                    SavingsAccount savings = (SavingsAccount) account;
                    details.append("  Interest Rate: ").append((savings.getInterestRate() * 100)).append("%\n");
                } else if (account instanceof CheckingAccount) {
                    CheckingAccount checking = (CheckingAccount) account;
                    details.append("  Overdraft Limit: $").append(df.format(checking.getOverdraftLimit())).append("\n");
                }
                details.append("\n");
            }

            textArea.setText(details.toString());
        }
    }

    private void generateReport() {
        StringBuilder report = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#0.00");

        report.append("Bank Report - All Clients:\n");
        report.append("=".repeat(50)).append("\n\n");

        double totalAssets = 0;
        int totalAccounts = 0;

        for (int clientIndex = 0; clientIndex < clients.size(); clientIndex++) {
            Client client = clients.get(clientIndex);

            report.append("Client ").append(clientIndex + 1).append(":\n");
            report.append("Name: ").append(client.getFullName()).append("\n");
            report.append("Number of Accounts: ").append(client.getAccounts().size()).append("\n\n");

            for (int i = 0; i < client.getAccounts().size(); i++) {
                Account account = client.getAccounts().get(i);
                report.append("  Account ").append(i + 1).append(":\n");
                report.append("    Type: ").append(account.getAccountType()).append("\n");
                report.append("    Balance: $").append(df.format(account.getBalance())).append("\n");

                if (account instanceof SavingsAccount) {
                    SavingsAccount savings = (SavingsAccount) account;
                    report.append("    Interest Rate: ").append((savings.getInterestRate() * 100)).append("%\n");
                } else if (account instanceof CheckingAccount) {
                    CheckingAccount checking = (CheckingAccount) account;
                    report.append("    Overdraft Limit: $").append(df.format(checking.getOverdraftLimit())).append("\n");
                }

                totalAssets += account.getBalance();
                totalAccounts++;
            }
            report.append("\n");
        }

        report.append("=".repeat(50)).append("\n");
        report.append("SUMMARY:\n");
        report.append("Total Clients: ").append(clients.size()).append("\n");
        report.append("Total Accounts: ").append(totalAccounts).append("\n");
        report.append("Total Assets: $").append(df.format(totalAssets)).append("\n");
        report.append("Last Updated: ").append(new java.util.Date());

        textArea.setText(report.toString());
    }

    private void showAbout() {
        textArea.setText("Практична робота Виконана, Еліна");
    }

    class Client {
        private String firstName;
        private String lastName;
        private List<Account> accounts;

        public Client(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.accounts = new ArrayList<>();
        }

        public String getFullName() {
            return firstName + " " + lastName;
        }

        public void addAccount(Account account) {
            accounts.add(account);
        }

        public List<Account> getAccounts() {
            return accounts;
        }
    }

    abstract class Account {
        protected double balance;

        public Account(double balance) {
            this.balance = balance;
        }

        public double getBalance() {
            return balance;
        }

        public abstract String getAccountType();
    }

    class SavingsAccount extends Account {
        private double interestRate;

        public SavingsAccount(double balance, double interestRate) {
            super(balance);
            this.interestRate = interestRate;
        }

        public double getInterestRate() {
            return interestRate;
        }

        @Override
        public String getAccountType() {
            return "Savings";
        }
    }

    class CheckingAccount extends Account {
        private double overdraftLimit;

        public CheckingAccount(double balance, double overdraftLimit) {
            super(balance);
            this.overdraftLimit = overdraftLimit;
        }

        public double getOverdraftLimit() {
            return overdraftLimit;
        }

        @Override
        public String getAccountType() {
            return "Checking";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}