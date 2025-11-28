package com.via.vix.payroll.ui;

import com.via.vix.payroll.db.DB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PayrollPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JButton refreshButton;

    public PayrollPanel() {
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Payroll History");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(30, 89, 185)); // Dark Blue Text
        title.setOpaque(true);
        title.setBackground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add some padding
        headerPanel.add(title, BorderLayout.WEST);

        refreshButton = new JButton("Refresh");
        headerPanel.add(refreshButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "Employee ID", "Name", "Position", "Days Worked", "Rate/Day", "Gross Salary", "Date"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Listeners
        refreshButton.addActionListener(e -> loadPayrollData());

        // Load data on component shown
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadPayrollData();
            }
        });
    }

    private void loadPayrollData() {
        model.setRowCount(0); // Clear existing data

        new SwingWorker<Void, Object[]>() {
            @Override
            protected Void doInBackground() throws Exception {
                String sql = "SELECT id, employee_id, name, position, days_worked, rate_per_day, salary, created_at FROM payroll ORDER BY created_at DESC";
                try (Connection c = DB.get();
                     PreparedStatement ps = c.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        publish(new Object[]{
                                rs.getInt("id"),
                                rs.getInt("employee_id"),
                                rs.getString("name"),
                                rs.getString("position"),
                                rs.getBigDecimal("days_worked"),
                                rs.getBigDecimal("rate_per_day"),
                                rs.getBigDecimal("salary"),
                                rs.getTimestamp("created_at").toLocalDateTime().toLocalDate()
                        });
                    }
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Object[]> chunks) {
                for (Object[] row : chunks) {
                    model.addRow(row);
                }
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(PayrollPanel.this, "Failed to load payroll data: " + e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}