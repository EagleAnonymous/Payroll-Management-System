package com.via.vix.payroll.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A simple calculator frame.
 */
public class CalculatorFrame extends JFrame implements ActionListener {

    // iPhone-like color scheme
    private static final Color BG_DARK = new Color(28, 28, 28);
    private static final Color BTN_DARK_GRAY = new Color(80, 80, 80);
    private static final Color BTN_LIGHT_GRAY = new Color(165, 165, 165);
    private static final Color BTN_ORANGE = new Color(255, 149, 0);
    private static final Color BTN_YELLOW = new Color(255, 214, 10); // A nice, rich yellow
    private static final Color TEXT_WHITE = Color.WHITE;

    private JTextField displayField;
    private String operator;
    private double firstOperand;
    private boolean isNewCalculation;
    // For repeating last operation with '='
    private String lastOperator;
    private double lastSecondOperand;
    private JButton activeOperatorButton;

    public CalculatorFrame() {
        setTitle("Calculator");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close to not exit the main app
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_DARK);

        // --- Display ---
        displayField = new JTextField();
        displayField.setEditable(false);
        displayField.setFont(new Font("SansSerif", Font.PLAIN, 48));
        displayField.setHorizontalAlignment(SwingConstants.RIGHT);
        displayField.setText("0");
        displayField.setBackground(BG_DARK);
        displayField.setForeground(TEXT_WHITE);
        displayField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(displayField, BorderLayout.NORTH);

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(BG_DARK);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        String[] buttonLabels = {
                "C", "+/-", "%", "/",
                "7", "8", "9", "×",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "0", ".", "="
        };

        int row = 0;
        int col = 0;
        for (String label : buttonLabels) {
            gbc.gridx = col;
            gbc.gridy = row;
            gbc.gridwidth = 1;

            if (label.equals("0")) {
                gbc.gridwidth = 2; // Make '0' button span two columns
            }

            JButton button;
            if (label.equals("0")) {
                // Use a rounded rectangle for the '0' button
                button = new RoundedButton(label);
            } else {
                // Use circular buttons for all others
                button = new CircularButton(label);
            }

            button.setFont(new Font("SansSerif", Font.BOLD, 22));
            button.addActionListener(this);

            // Apply colors
            if ("+/-%C".contains(label)) {
                button.setBackground(BTN_LIGHT_GRAY);
                button.setForeground(Color.BLACK);
            } else if ("/×-+".contains(label)) { // Operators
                button.setBackground(BTN_YELLOW);
                button.setForeground(Color.BLACK);
            } else if (label.equals("=")) { // Equals button remains orange
                button.setBackground(BTN_ORANGE);
                button.setForeground(Color.WHITE);
            } else {
                button.setBackground(BTN_DARK_GRAY);
                button.setForeground(TEXT_WHITE);
            }

            buttonPanel.add(button, gbc);

            // Logic to advance column and row
            if (label.equals("0")) {
                col += 2; // The '0' button spans two columns
            } else {
                col++;
            }

            if (col > 3) {
                col = 0;
                row++;
            }
        }

        add(buttonPanel, BorderLayout.CENTER);

        // Initialize state
        operator = "";
        firstOperand = 0;
        isNewCalculation = true;
        lastOperator = "";
        lastSecondOperand = 0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        Object source = e.getSource();
        String currentText = displayField.getText();

        // Handle number and decimal input
        switch (command) {
            case "0": case "1": case "2": case "3": case "4":
            case "5": case "6": case "7": case "8": case "9":
                if (currentText.equals("Error")) {
                    currentText = ""; // Clear error before proceeding
                }
                if (isNewCalculation || currentText.equals("0")) {
                    displayField.setText(command);
                    isNewCalculation = false;
                } else {
                    displayField.setText(currentText + command);
                }
                resetActiveOperator();
                break;
            

            case ".":
                if (!currentText.contains(".")) {
                    displayField.setText(currentText + ".");
                }
                break;

            // Handle operators (+, -, *, /)
            case "+": case "-": case "×": case "/":
                // If an operator is pressed right after another, just switch the operator
                if (!isNewCalculation) {
                    performPendingCalculation();
                    firstOperand = Double.parseDouble(displayField.getText());
                }
                operator = command;
                isNewCalculation = true;
                // Update firstOperand to the current display value when switching operators
                firstOperand = Double.parseDouble(displayField.getText());
                lastOperator = ""; // Clear last operation for '=' repetition
                if (source instanceof JButton) {
                    activeOperatorButton = (JButton) source;
                    // Indicate active operator by changing text color, not background
                    activeOperatorButton.setForeground(TEXT_WHITE);
                }
                break;
            
            case "+/-":
                if (!currentText.equals("0") && !currentText.equals("Error")) {
                    if (currentText.startsWith("-")) {
                        displayField.setText(currentText.substring(1));
                    } else {
                        displayField.setText("-" + currentText);
                    }
                }
                break;

            case "%":
                if (currentText.equals("Error")) {
                    return; // Do nothing on error
                }
                double percentValue = Double.parseDouble(currentText) / 100.0;
                displayField.setText(String.valueOf(percentValue));
                resetActiveOperator();
                isNewCalculation = true;
                break;

            case "=":
                performPendingCalculation();
                lastOperator = ""; // An explicit '=' press resets the repeat chain
                break;

            case "C":
                displayField.setText("0");
                operator = "";
                firstOperand = 0;
                lastOperator = "";
                lastSecondOperand = 0;
                resetActiveOperator();
                isNewCalculation = true;
                break;
        }
        adjustFontSize();
    }

    private void performPendingCalculation() {
        if (operator.isEmpty()) {
            return;
        }
        double secondOperand = Double.parseDouble(displayField.getText());
        calculate(firstOperand, operator, secondOperand);
        firstOperand = Double.parseDouble(displayField.getText()); // The result becomes the new first operand
    }

    private void resetActiveOperator() {
        if (activeOperatorButton != null) {
            // Revert text color to normal operator color
            activeOperatorButton.setForeground(Color.BLACK);
            activeOperatorButton = null;
        }
    }

    private void calculate(double num1, String op, double num2) {
        double result = 0;
        switch (op) {
            case "+": result = num1 + num2; break;
            case "-": result = num1 - num2; break;
            case "×": result = num1 * num2; break;
            case "/":
                if (num2 != 0) {
                    result = num1 / num2;
                } else {
                    displayField.setText("Error");
                    operator = "";
                    isNewCalculation = true;
                    return;
                }
                break;
        }
        // Format to avoid long decimals like .0
        if (result == (long) result) {
            displayField.setText(String.format("%d", (long) result));
        } else {
            displayField.setText(String.format("%s", result));
        }
        resetActiveOperator();
        operator = "";
        isNewCalculation = true;
    }

    private void adjustFontSize() {
        Font labelFont = displayField.getFont();
        String text = displayField.getText();
        int stringWidth = displayField.getFontMetrics(labelFont).stringWidth(text);
        int componentWidth = displayField.getWidth() - 20; // Subtract padding

        if (stringWidth > componentWidth) {
            // Shrink font size
            double widthRatio = (double) componentWidth / (double) stringWidth;
            int newFontSize = (int) (labelFont.getSize() * widthRatio);
            int finalFontSize = Math.max(12, newFontSize); // Don't let it get too small
            displayField.setFont(new Font(labelFont.getName(), labelFont.getStyle(), finalFontSize));
        } else {
            // Reset to default size if it fits
            displayField.setFont(new Font(labelFont.getName(), labelFont.getStyle(), 48));
        }
    }
}