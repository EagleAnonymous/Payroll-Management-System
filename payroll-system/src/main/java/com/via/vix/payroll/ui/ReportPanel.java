package com.via.vix.payroll.ui;

import com.via.vix.payroll.db.DB;
import com.via.vix.payroll.util.PDFUtil;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportPanel extends JPanel {

    private JTextField titleField;
    private JTextArea contentArea;
    private JButton saveButton;
    private JCheckBox includePayrollCheckbox;

    public ReportPanel() {
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JLabel titleLabel = new JLabel("Generate Report");
        titleLabel.setForeground(new Color(30, 89, 185)); // Dark Blue Text
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add some padding
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Report Title:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        titleField = new JTextField();
        formPanel.add(titleField, gbc);

        // Content
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Content:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        contentArea = new JTextArea(10, 30);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        formPanel.add(scrollPane, gbc);

        // Checkbox for including payroll history
        gbc.gridx = 1; // Align with the content text area column
        gbc.gridy = 2; // Next row
        gbc.gridwidth = 1; // Only span one column
        gbc.fill = GridBagConstraints.NONE; // Don't stretch the component
        gbc.anchor = GridBagConstraints.WEST; // Align to the left
        gbc.weightx = 0; // Don't take up extra horizontal space
        gbc.weighty = 0; // Don't take up extra vertical space
        includePayrollCheckbox = new JCheckBox("Include Payroll History Table in Report");
        includePayrollCheckbox.setOpaque(false); // Make checkbox transparent to show wrapper's background
        includePayrollCheckbox.setFont(new Font("SansSerif", Font.PLAIN, 12)); // Set a smaller font

        // Wrap the checkbox in a panel to give it a small, contained background
        JPanel checkboxWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkboxWrapper.setBackground(Color.WHITE);
        checkboxWrapper.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4)); // Small padding
        checkboxWrapper.add(includePayrollCheckbox);

        formPanel.add(checkboxWrapper, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Save Button
        saveButton = new JButton("Save Report as PDF");        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> saveReport());
    }
    
    private void saveReport() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and Content cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        saveButton.setEnabled(false);
        new SwingWorker<Path, Void>() {
            private final boolean includePayroll = includePayrollCheckbox.isSelected();

            @Override
            protected Path doInBackground() throws Exception {
                List<Object[]> payrollData = null;
                if (includePayroll) {
                    payrollData = fetchPayrollData();
                }

                // Save to DB
                try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("INSERT INTO reports (title, notes) VALUES (?, ?)")) {
                    ps.setString(1, title);
                    ps.setString(2, content);
                    ps.executeUpdate();
                }
                // Generate PDF
                return PDFUtil.saveReport(title, content, payrollData);
            }

            @Override
            protected void done() {
                saveButton.setEnabled(true);
                try {
                    Path path = get();
                    JOptionPane.showMessageDialog(ReportPanel.this, "Report saved successfully to database and as PDF:\n" + path, "Success", JOptionPane.INFORMATION_MESSAGE);
                    titleField.setText("");
                    contentArea.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ReportPanel.this, "Failed to save report: " + e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Fetches all payroll history from the database.
     * This logic is similar to the one in PayrollPanel.
     * @return A list of object arrays, where each array represents a row of payroll data.
     * @throws SQLException if a database access error occurs.
     */
    private List<Object[]> fetchPayrollData() throws SQLException {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT id, employee_id, name, position, days_worked, rate_per_day, salary, created_at FROM payroll ORDER BY created_at DESC";
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.add(new Object[]{
                        rs.getInt("id"), rs.getInt("employee_id"), rs.getString("name"),
                        rs.getString("position"), rs.getBigDecimal("days_worked"), rs.getBigDecimal("rate_per_day"),
                        rs.getBigDecimal("salary"), rs.getTimestamp("created_at").toLocalDateTime().toLocalDate()
                });
            }
        }
        return data;
    }
}