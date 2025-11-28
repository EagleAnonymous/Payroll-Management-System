package com.via.vix.payroll.ui;

import com.via.vix.payroll.db.DB;
import com.via.vix.payroll.util.DateUtil;
import com.via.vix.payroll.util.DeductionUtil;
import com.via.vix.payroll.util.PDFUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField tfName = new JTextField(15);
    private JTextField tfDob = new JTextField(15);
    private JTextField tfEmail = new JTextField(15);
    private JTextField tfContact = new JTextField(15);
    private JTextField tfAge = new JTextField(15);
    private JComboBox<String> cbGender = new JComboBox<>(new String[]{"MALE", "FEMALE", "OTHER"});
    private JComboBox<String> cbPosition = new JComboBox<>(new String[]{
            "BUSINESS ANALYST", "PROJECT MANAGER", "UI/UX DESIGNER",
            "WEB DEVELOPER", "QA ENGINEER", "DEVOPS ENGINEER"
    });
            private JTextField search;
    private JButton addBtn, updateBtn, deleteBtn, calculatorBtn;
    private JLabel loadingLabel; // Declare loadingLabel here

    private JTextField tfDaysWorked, tfRatePerDay, tfSss, tfPagibig, tfPhilhealth;
    private JTextField tfGrossSalary, tfTotalDeductions, tfNetSalary;
    private JButton calculateBtn;
    private JPanel payslipPanel;
    private JTextArea payslipArea; // Added payslipArea
    private JButton printBtn, printToPdfBtn;
    private Timer searchTimer;

    public MainPanel() {
        setLayout(new BorderLayout());
        setOpaque(false); // Make transparent to show dashboard gradient
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Employee List");
        title.setForeground(new Color(30, 89, 185)); // Dark Blue Text
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID","NAME","AGE","DOB","GENDER","POSITION","EMAIL","CONTACT"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        JScrollPane tableScrollPane = new JScrollPane(table);

        search = new JTextField();
        addPlaceholder(search, "Search...");
        
        JPanel searchAndLoadingPanel = new JPanel(new BorderLayout());
        searchAndLoadingPanel.add(search, BorderLayout.CENTER);

        loadingLabel = new JLabel("Loading...");
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loadingLabel.setVisible(false); // Initially hidden
        searchAndLoadingPanel.add(loadingLabel, BorderLayout.EAST);

        JPanel employeeListPanel = new JPanel(new BorderLayout());
        employeeListPanel.add(searchAndLoadingPanel, BorderLayout.NORTH);
        employeeListPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel employeeForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0; // Reset weightx for labels
        gbc.weighty = 0.0; // Reset weighty for labels
        employeeForm.add(new JLabel("Name"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0; // Allow text field to expand horizontally
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally
        employeeForm.add(tfName, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        employeeForm.add(new JLabel("DOB (yyyy-MM-dd)"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally
        employeeForm.add(tfDob, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        employeeForm.add(new JLabel("Age"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally
        employeeForm.add(tfAge, gbc);
        tfAge.setEditable(false); // Make age field non-editable

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        employeeForm.add(new JLabel("Gender"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally
        employeeForm.add(cbGender, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        employeeForm.add(new JLabel("Position"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally
        employeeForm.add(cbPosition, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        employeeForm.add(new JLabel("Email"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        employeeForm.add(tfEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        employeeForm.add(new JLabel("Contact"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        employeeForm.add(tfContact, gbc);

        JPanel btns = new JPanel();
        addBtn = new JButton("ADD");
        updateBtn = new JButton("UPDATE");
        deleteBtn = new JButton("DELETE");
        calculatorBtn = new JButton("CALCULATOR");
        btns.add(addBtn);
        btns.add(updateBtn);
        btns.add(deleteBtn);
        btns.add(calculatorBtn);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0; // Buttons don't need to expand horizontally
        gbc.weighty = 0.0; // Reset weighty
        employeeForm.add(btns, gbc);

        JPanel payrollForm = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0; // Reset weightx for labels
        gbc.weighty = 0.0; // Reset weighty for labels
        payrollForm.add(new JLabel("No. of Days"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0; // Allow text field to expand horizontally
        tfDaysWorked = new JTextField(15);
        payrollForm.add(tfDaysWorked, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        payrollForm.add(new JLabel("Rate/Day"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        tfRatePerDay = new JTextField(15);
        payrollForm.add(tfRatePerDay, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        payrollForm.add(new JLabel("SSS"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        tfSss = new JTextField(15);
        tfSss.setEditable(false);
        payrollForm.add(tfSss, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        payrollForm.add(new JLabel("Pag-ibig"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        tfPagibig = new JTextField(15);
        tfPagibig.setEditable(false);
        payrollForm.add(tfPagibig, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        payrollForm.add(new JLabel("Philhealth"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        tfPhilhealth = new JTextField(15);
        tfPhilhealth.setEditable(false);
        payrollForm.add(tfPhilhealth, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        payrollForm.add(new JLabel("Gross Salary"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        tfGrossSalary = new JTextField(15);
        tfGrossSalary.setEditable(false);
        payrollForm.add(tfGrossSalary, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        payrollForm.add(new JLabel("Total Deductions"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        tfTotalDeductions = new JTextField(15);
        tfTotalDeductions.setEditable(false);
        payrollForm.add(tfTotalDeductions, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        payrollForm.add(new JLabel("Net Salary"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        tfNetSalary = new JTextField(15);
        tfNetSalary.setEditable(false);
        payrollForm.add(tfNetSalary, gbc);

        // Calculate button
        JPanel calculateBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center the button
        calculateBtn = new JButton("CALCULATE");
        calculateBtnPanel.add(calculateBtn);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0; // No extra vertical space for this row
        payrollForm.add(calculateBtnPanel, gbc);

        // Print buttons
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weighty = 1.0; // Allow this row to take the remaining vertical space
        gbc.anchor = GridBagConstraints.NORTH; // Anchor buttons to the top of their cell
        printBtn = new JButton("PRINT");
        printToPdfBtn = new JButton("PRINT TO PDF");
        JPanel printButtonsGroup = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center the buttons
        printButtonsGroup.add(printBtn);
        printButtonsGroup.add(printToPdfBtn);
        payrollForm.add(printButtonsGroup, gbc); // Add the group to payrollForm
        
        // A single panel to hold both forms, allowing them to scroll together
        JPanel formsPanel = new JPanel();
        formsPanel.setLayout(new BoxLayout(formsPanel, BoxLayout.Y_AXIS));
        formsPanel.add(employeeForm);
        formsPanel.add(new JSeparator());
        formsPanel.add(payrollForm);

        // Put the forms panel in a scroll pane to handle smaller screen sizes
        JScrollPane middlePanel = new JScrollPane(formsPanel);
        middlePanel.setBorder(BorderFactory.createEmptyBorder()); // Remove scrollpane border

        // Main split pane for table, middlePanel, and payslipPanel
        JSplitPane mainContentSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, employeeListPanel, middlePanel);
        mainContentSplitPane.setResizeWeight(0.66); // Adjust as needed

        add(mainContentSplitPane, BorderLayout.CENTER);

        loadData("");

        // listeners
        tfDob.getDocument().addDocumentListener(new DocumentListener() {
            void upd() {
                try {
                    LocalDate d = LocalDate.parse(tfDob.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    tfAge.setText(String.valueOf(DateUtil.ageFromDob(d)));
                } catch (Exception ex) {
                    tfAge.setText("");
                }
            }
            public void insertUpdate(DocumentEvent e) { upd(); }
            public void removeUpdate(DocumentEvent e) { upd(); }
            public void changedUpdate(DocumentEvent e) { upd(); }
        });

        searchTimer = new Timer(300, e -> { // 300ms delay
            String text = search.getText().trim();
            if (text.equals("Search...")) {
                loadData("");
            } else {
                loadData(text);
            }
        });
        searchTimer.setRepeats(false); // Only fire once

        search.getDocument().addDocumentListener(new DocumentListener() {
            void restartTimer() {
                if (searchTimer.isRunning()) {
                    searchTimer.restart();
                } else {
                    searchTimer.start();
                }
            }
            public void insertUpdate(DocumentEvent e) { restartTimer(); }
            public void removeUpdate(DocumentEvent e) { restartTimer(); }
            public void changedUpdate(DocumentEvent e) { restartTimer(); }
        });

        addBtn.addActionListener(e -> addEmployee());
        updateBtn.addActionListener(e -> updateEmployee());
        deleteBtn.addActionListener(e -> deleteEmployee());
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelected());
        calculateBtn.addActionListener(e -> calculatePayroll());
        calculatorBtn.addActionListener(e -> new CalculatorFrame().setVisible(true));

        printBtn.addActionListener(e -> onPrint());
        printToPdfBtn.addActionListener(e -> onPrintToPdf());
    }

    /**
     * Performs the core payroll calculation, updates UI fields, and saves the record to the database.
     * This method contains all validation and logic.
     *
     * @return true if calculation and saving were successful, false otherwise.
     */
    private boolean calculateAndSavePayroll() {
        int selectedRow = table.getSelectedRow();
        try {
            // Input validation
            if (tfDaysWorked.getText().isEmpty() || tfRatePerDay.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter values for Days Worked and Rate/Day.");
                return false;
            }

            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select an employee to calculate payroll for.");
                return false;
            }

            double daysWorked = Double.parseDouble(tfDaysWorked.getText());
            double ratePerDay = Double.parseDouble(tfRatePerDay.getText());

            if (daysWorked < 0 || ratePerDay < 0) {
                JOptionPane.showMessageDialog(this, "Days Worked and Rate/Day cannot be negative.");
                return false;
            }

            double grossSalary = daysWorked * ratePerDay;
            double sss = DeductionUtil.calculateSss(grossSalary);
            double pagibig = DeductionUtil.calculatePagibig(grossSalary);
            double philhealth = DeductionUtil.calculatePhilhealth(grossSalary);
            double totalDeductions = sss + pagibig + philhealth;
            double netSalary = grossSalary - totalDeductions;

            tfGrossSalary.setText(String.format("%.2f", grossSalary));
            tfSss.setText(String.format("%.2f", sss));
            tfPagibig.setText(String.format("%.2f", pagibig));
            tfPhilhealth.setText(String.format("%.2f", philhealth));
            tfTotalDeductions.setText(String.format("%.2f", totalDeductions));
            tfNetSalary.setText(String.format("%.2f", netSalary));

            savePayrollToDB(selectedRow, daysWorked, ratePerDay, grossSalary);
            return true; // Success
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format in payroll fields. Please enter numeric values.");
            return false;
        }
    }

    private void calculatePayroll() {
        int selectedRow = table.getSelectedRow();
        try {
            // Input validation
            if (tfDaysWorked.getText().isEmpty() || tfRatePerDay.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter values for Days Worked and Rate/Day.");
                return;
            }

            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select an employee to calculate payroll for.");
                return;
            }

            double daysWorked = Double.parseDouble(tfDaysWorked.getText());
            double ratePerDay = Double.parseDouble(tfRatePerDay.getText());

            if (daysWorked < 0 || ratePerDay < 0) {
                JOptionPane.showMessageDialog(this, "Days Worked and Rate/Day cannot be negative.");
                return;
            }

            double grossSalary = daysWorked * ratePerDay;

            double sss = DeductionUtil.calculateSss(grossSalary);
            double pagibig = DeductionUtil.calculatePagibig(grossSalary);
            double philhealth = DeductionUtil.calculatePhilhealth(grossSalary);
            double totalDeductions = sss + pagibig + philhealth;
            double netSalary = grossSalary - totalDeductions;

            tfGrossSalary.setText(String.format("%.2f", grossSalary));
            tfSss.setText(String.format("%.2f", sss));
            tfPagibig.setText(String.format("%.2f", pagibig));
            tfPhilhealth.setText(String.format("%.2f", philhealth));
            tfTotalDeductions.setText(String.format("%.2f", totalDeductions));
            tfNetSalary.setText(String.format("%.2f", netSalary));

            // Display payslip in a popup
            Map<String, String> payslipData = getPayslipData();
            StringBuilder payslipText = new StringBuilder();
            for (Map.Entry<String, String> entry : payslipData.entrySet()) {
                payslipText.append(String.format("% -20s: %s\n", entry.getKey(), entry.getValue()));
            }
            payslipText.append("-------------------\n");
            showPayslipPreview(payslipText.toString());
            savePayrollToDB(selectedRow, daysWorked, ratePerDay, grossSalary); // This is now redundant if called from calculateAndSavePayroll

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format in payroll fields. Please enter numeric values.");
        }
    }

    private void savePayrollToDB(int selectedRow, double daysWorked, double ratePerDay, double grossSalary) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                int employeeId = (int) model.getValueAt(selectedRow, 0);
                String name = (String) model.getValueAt(selectedRow, 1);
                String position = (String) model.getValueAt(selectedRow, 5);

                String sql = "INSERT INTO payroll (employee_id, name, position, days_worked, rate_per_day, salary) VALUES (?, ?, ?, ?, ?, ?)";
                try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setInt(1, employeeId);
                    ps.setString(2, name);
                    ps.setString(3, position);
                    ps.setDouble(4, daysWorked);
                    ps.setDouble(5, ratePerDay);
                    ps.setDouble(6, grossSalary);
                    ps.executeUpdate();
                }
                return null;
            }

            @Override
            protected void done() {
                // This runs on the EDT. We can show a confirmation or handle errors here.
                // For now, we'll just log errors to the console to keep the UI clean.
                try { get(); } catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }

    

    private void showPayslipPreview(String payslipContent) {
        JDialog payslipDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Payslip Preview", true);
        payslipDialog.setSize(400, 600);
        payslipDialog.setLocationRelativeTo(this); // Center relative to MainPanel

        JTextArea previewArea = new JTextArea(payslipContent);
        previewArea.setEditable(false);
        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Monospaced for better alignment
        JScrollPane scrollPane = new JScrollPane(previewArea);
        payslipDialog.add(scrollPane, BorderLayout.CENTER);

        // Add a close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> payslipDialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        payslipDialog.add(buttonPanel, BorderLayout.SOUTH);

        payslipDialog.setVisible(true);
    }

    private void loadData(String filter) {
        setButtonsEnabled(false);
        model.setRowCount(0); // Clear existing data

        new SwingWorker<Void, Object[]>() {
            private String currentFilter = filter;

            @Override
            protected void done() {
                loadingLabel.setVisible(false); // Hide loading indicator
                setButtonsEnabled(true); // Re-enable buttons
                try {
                    get(); // Check for exceptions from doInBackground
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    JOptionPane.showMessageDialog(MainPanel.this, "Loading interrupted: " + e.getMessage(), "Load Data Error", JOptionPane.ERROR_MESSAGE);
                } catch (java.util.concurrent.ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof SQLException) {
                        JOptionPane.showMessageDialog(MainPanel.this, "Database error loading data: " + cause.getMessage(), "Load Data Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(MainPanel.this, "An unexpected error occurred loading data: " + cause.getMessage(), "Load Data Error", JOptionPane.ERROR_MESSAGE);
                    }
                    cause.printStackTrace();
                }
            }

            @Override
            protected void process(java.util.List<Object[]> chunks) {
                // Update the table model on the EDT
                for (Object[] row : chunks) {
                    model.addRow(row);
                }
            }
            
            @Override
            protected Void doInBackground() throws Exception {
                String sql;
                boolean isIdSearch = false;
                int employeeId = -1;

                if (currentFilter.isEmpty()) {
                    sql = "SELECT * FROM employees ORDER BY name ASC";
                } else {
                    try {
                        employeeId = Integer.parseInt(currentFilter);
                        sql = "SELECT * FROM employees WHERE id = ? ORDER BY name ASC";
                        isIdSearch = true;
                    } catch (NumberFormatException e) {
                        sql = "SELECT * FROM employees WHERE " +
                              "LOWER(name) LIKE ? OR " +
                              "LOWER(gender) LIKE ? OR " +
                              "LOWER(email) LIKE ? OR " +
                              "LOWER(contact) LIKE ? OR " +
                              "LOWER(position) LIKE ? OR " +
                              "LOWER(dob) LIKE ? " +
                              "ORDER BY name ASC";
                    }
                }

                try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
                    if (isIdSearch) {
                        ps.setInt(1, employeeId);
                    } else if (!currentFilter.isEmpty()) {
                        String like = "%" + currentFilter.toLowerCase() + "%";
                        int i = 1;
                        ps.setString(i++, like);
                        ps.setString(i++, like);
                        ps.setString(i++, like);
                        ps.setString(i++, like);
                        ps.setString(i++, like);
                        ps.setString(i++, like);
                    }
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            // Publish rows as they are fetched
                            publish(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getInt("age"),
                                    rs.getDate("dob").toString(), rs.getString("gender"), rs.getString("position"),
                                    rs.getString("email"), rs.getString("contact")});
                        }
                    }
                }
                return null;
            }
        }.execute();
    }

    private void addEmployee() {
        if (!validateEmployeeInput()) return;

        setButtonsEnabled(false); // Disable buttons during operation
        loadingLabel.setVisible(true); // Show loading indicator

        new SwingWorker<Integer, Void>() {
            private String name = tfName.getText().trim();
            private String dobText = tfDob.getText().trim();
            private String gender = cbGender.getSelectedItem().toString();
            private String position = cbPosition.getSelectedItem().toString();
            private String email = tfEmail.getText().trim();
            private String contact = tfContact.getText().trim();

            @Override
            protected Integer doInBackground() throws Exception {
                try (Connection c = DB.get();
                     PreparedStatement ps = c.prepareStatement(
                             "INSERT INTO employees(name, age, dob, gender, position, email, contact) VALUES (?,?,?,?,?,?,?)",
                             Statement.RETURN_GENERATED_KEYS)) {

                    LocalDate dob = LocalDate.parse(dobText);
                    int age = DateUtil.ageFromDob(dob);

                    ps.setString(1, name);
                    ps.setInt(2, age);
                    ps.setDate(3, java.sql.Date.valueOf(dob));
                    ps.setString(4, gender);
                    ps.setString(5, position);
                    ps.setString(6, email);
                    ps.setString(7, contact);

                    int affectedRows = ps.executeUpdate();
                    if (affectedRows > 0) {
                        try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                return generatedKeys.getInt(1);
                            }
                        }
                    }
                }
                return -1; // Indicate failure
            }

            @Override
            protected void done() {
                loadingLabel.setVisible(false); // Hide loading indicator
                setButtonsEnabled(true); // Re-enable buttons
                try {
                    int newId = get();
                    if (newId != -1) {
                        JOptionPane.showMessageDialog(MainPanel.this, "Employee added successfully! ID: " + newId);
                        if (!search.getText().equals("Search...")) {
                            search.setText(""); // Clear search only if it's not the placeholder
                        }
                        loadData(""); // Reload all data
                        clearForm();
                    } else {
                        JOptionPane.showMessageDialog(MainPanel.this, "Failed to add employee.", "Add Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    JOptionPane.showMessageDialog(MainPanel.this, "Add operation interrupted: " + e.getMessage(), "Add Error", JOptionPane.ERROR_MESSAGE);
                } catch (java.util.concurrent.ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof DateTimeParseException) {
                        JOptionPane.showMessageDialog(MainPanel.this, "Invalid Date of Birth format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    } else if (cause instanceof SQLException) {
                        JOptionPane.showMessageDialog(MainPanel.this, "Database error: " + cause.getMessage(), "Add Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(MainPanel.this, "An unexpected error occurred: " + cause.getMessage(), "Add Error", JOptionPane.ERROR_MESSAGE);
                    }
                    cause.printStackTrace();
                }
            }
        }.execute();
    }

    private void updateEmployee() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row to update.", "Update Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateEmployeeInput()) return;

        setButtonsEnabled(false); // Disable buttons during operation
        loadingLabel.setVisible(true); // Show loading indicator

        int id = (int) model.getValueAt(row, 0);
        new SwingWorker<Boolean, Void>() {
            private String name = tfName.getText().trim();
            private String dobText = tfDob.getText().trim();
            private String gender = cbGender.getSelectedItem().toString();
            private String position = cbPosition.getSelectedItem().toString();
            private String email = tfEmail.getText().trim();
            private String contact = tfContact.getText().trim();
            private int employeeId = id;

            @Override
            protected Boolean doInBackground() throws Exception {
                try (Connection c = DB.get();
                     PreparedStatement ps = c.prepareStatement(
                             "UPDATE employees SET name=?, age=?, dob=?, gender=?, position=?, email=?, contact=? WHERE id=?")) {

                    LocalDate dob = LocalDate.parse(dobText);
                    int age = DateUtil.ageFromDob(dob);

                    ps.setString(1, name);
                    ps.setInt(2, age);
                    ps.setDate(3, java.sql.Date.valueOf(dob));
                    ps.setString(4, gender);
                    ps.setString(5, position);
                    ps.setString(6, email);
                    ps.setString(7, contact);
                    ps.setInt(8, employeeId);

                    int affectedRows = ps.executeUpdate();
                    return affectedRows > 0;
                }
            }

            @Override
            protected void done() {
                loadingLabel.setVisible(false); // Hide loading indicator
                setButtonsEnabled(true); // Re-enable buttons
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(MainPanel.this, "Employee updated successfully!");
                        String searchText = search.getText().trim();
                        if (searchText.equals("Search...")) {
                            loadData("");
                        } else {
                            loadData(searchText);
                        }
                        clearForm();
                    } else {
                        JOptionPane.showMessageDialog(MainPanel.this, "Employee not found or no changes made.", "Update Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    JOptionPane.showMessageDialog(MainPanel.this, "Update operation interrupted: " + e.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
                } catch (java.util.concurrent.ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof DateTimeParseException) {
                        JOptionPane.showMessageDialog(MainPanel.this, "Invalid Date of Birth format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    } else if (cause instanceof SQLException) {
                        JOptionPane.showMessageDialog(MainPanel.this, "Database error: " + cause.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(MainPanel.this, "An unexpected error occurred: " + cause.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
                    }
                    cause.printStackTrace();
                }
            }
        }.execute();
    }

    private void deleteEmployee() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Select one or more rows to delete.", "Delete Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + selectedRows.length + " selected employee(s)?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        setButtonsEnabled(false); // Disable buttons during operation
        loadingLabel.setVisible(true); // Show loading indicator

        new SwingWorker<Integer, Void>() {
            private int[] employeeIdsToDelete = new int[selectedRows.length];

            { // Initialize the array with IDs from selected rows
                for (int i = 0; i < selectedRows.length; i++) {
                    employeeIdsToDelete[i] = (int) model.getValueAt(selectedRows[i], 0);
                }
            }

            @Override
            protected Integer doInBackground() throws Exception {
                int totalAffectedRows = 0;
                try (Connection c = DB.get();
                     PreparedStatement ps = c.prepareStatement("DELETE FROM employees WHERE id=?")) {
                    for (int id : employeeIdsToDelete) {
                        ps.setInt(1, id);
                        ps.addBatch();
                    }
                    int[] affectedRows = ps.executeBatch();
                    for (int count : affectedRows) {
                        totalAffectedRows += count;
                    }
                }
                return totalAffectedRows;
            }

            @Override
            protected void done() {
                loadingLabel.setVisible(false); // Hide loading indicator
                setButtonsEnabled(true); // Re-enable buttons
                try {
                    int deletedCount = get();
                    if (deletedCount > 0) {
                        JOptionPane.showMessageDialog(MainPanel.this, deletedCount + " employee(s) deleted successfully!");
                        String searchText = search.getText().trim();
                        if (searchText.equals("Search...")) {
                            loadData(""); // Refresh the full list
                        } else {
                            loadData(searchText); // Re-apply the current filter
                        }
                        clearForm();
                    } else {
                        JOptionPane.showMessageDialog(MainPanel.this, "No employees were deleted.", "Delete Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    JOptionPane.showMessageDialog(MainPanel.this, "Delete operation interrupted: " + e.getMessage(), "Delete Error", JOptionPane.ERROR_MESSAGE);
                } catch (java.util.concurrent.ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof SQLException) {
                        JOptionPane.showMessageDialog(MainPanel.this, "Database error: " + cause.getMessage(), "Delete Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(MainPanel.this, "An unexpected error occurred: " + cause.getMessage(), "Delete Error", JOptionPane.ERROR_MESSAGE);
                    }
                    cause.printStackTrace();
                }
            }
        }.execute();
    }

    private boolean validateEmployeeInput() {
        String name = tfName.getText().trim();
        String dobText = tfDob.getText().trim();
        String email = tfEmail.getText().trim();
        String contact = tfContact.getText().trim();

        if (name.isEmpty() || dobText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Date of Birth are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            LocalDate.parse(dobText);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid Date of Birth format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Basic email validation (can be improved with regex)
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Basic contact validation (e.g., only digits, can be improved)
        if (!contact.isEmpty() && !contact.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Contact should contain only digits.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void fillFormFromSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        tfName.setText(String.valueOf(model.getValueAt(row,1)));
        tfAge.setText(String.valueOf(model.getValueAt(row,2)));
        tfDob.setText(String.valueOf(model.getValueAt(row,3)));
        cbGender.setSelectedItem(String.valueOf(model.getValueAt(row,4)));
        cbPosition.setSelectedItem(String.valueOf(model.getValueAt(row,5)));
        tfEmail.setText(String.valueOf(model.getValueAt(row,6)));
        tfContact.setText(String.valueOf(model.getValueAt(row,7)));
    }

    private void clearForm() {
        tfName.setText(""); tfDob.setText(""); tfAge.setText(""); tfEmail.setText(""); tfContact.setText("");
        cbGender.setSelectedIndex(0); cbPosition.setSelectedIndex(0);
    }

    private void addPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
    }

    private Map<String, String> getPayslipData() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("EMPLOYEE", tfName.getText());
        data.put("POSITION", cbPosition.getSelectedItem().toString());
        data.put("DAYS WORK", tfDaysWorked.getText());
        data.put("RATE/DAY", tfRatePerDay.getText());
        data.put("GROSS SALARY", tfGrossSalary.getText());
        data.put("SSS", tfSss.getText());
        data.put("PAG-IBIG", tfPagibig.getText());
        data.put("PHILHEALTH", tfPhilhealth.getText());
        data.put("TOTAL DEDUCTIONS", tfTotalDeductions.getText());
        data.put("NET SALARY", tfNetSalary.getText());
        return data;
    }

    private void onPrint() {
        // First, ensure the payroll is calculated and saved.
        if (calculateAndSavePayroll()) {
            try {
                Map<String, String> data = getPayslipData();
                PDFUtil.printPayslip(data);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Print failed: " + ex.getMessage());
            }
        } else {
            // Error message is shown by calculateAndSavePayroll()
            // so we don't need another one here.
        }
    }

    private void onPrintToPdf() {
        // First, ensure the payroll is calculated and saved.
        if (calculateAndSavePayroll()) {
            try {
                Map<String, String> data = getPayslipData();
                Path out = PDFUtil.savePayslip(data);
                JOptionPane.showMessageDialog(this, "Payslip saved: " + out.toString());
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Print failed: " + ex.getMessage());
            }
        } else {
            // Error message is shown by calculateAndSavePayroll()
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        addBtn.setEnabled(enabled);
        updateBtn.setEnabled(enabled);
        deleteBtn.setEnabled(enabled);
    }
}