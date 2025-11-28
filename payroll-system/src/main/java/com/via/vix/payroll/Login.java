package com.via.vix.payroll;

import com.via.vix.payroll.db.DB;
import com.via.vix.payroll.util.UIConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.ResultSet;

/**
 * VIA VIX Payroll System Login Screen
 * Design styled to match the reference layout EXACTLY, with updated logo size and button visibility.
 */
public class Login extends JFrame {

    // --- Color Palette (Analyzed directly from the image) ---
    private static final Color PRIMARY_TEAL = new Color(0, 150, 168);       // Background Teal (Top Bar & Frame BG)
    private static final Color SECONDARY_LIGHT_BLUE = new Color(193, 214, 239); // Login Box BG (Specific Light Blue)
    private static final Color BUTTON_DARK_BLUE = new Color(30, 89, 185);     // Login Button (Darker Blue)
    private static final Color TEXT_WHITE = Color.WHITE; // Used for button text
    private static final Color TEXT_DARK = new Color(33, 33, 33); // Used for Login title and labels

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    // --- Login attempt tracking ---
    private int loginAttempts = 0;
    private static final int MAX_ATTEMPTS = 3;
    private long lockoutEndTime = 0;
    private static final long LOCKOUT_DURATION_SECONDS = 30;

    public Login() {
        // --- Frame setup ---
        setTitle("VIA VIX Payroll System Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set size to maintain the image's aspect ratio and general proportions (e.g., 700x500 or 800x600)
        setSize(700, 600); 
        setLocationRelativeTo(null);
        setResizable(false);

        // Set the frame icon to the system logo
        try {
            ImageIcon frameIcon = new ImageIcon(UIConfig.LOGO_PATH);
            setIconImage(frameIcon.getImage());
        } catch (Exception e) {
            System.err.println("Could not set frame icon: " + e.getMessage());
        }

        // Set system look and feel for a more modern appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        // --- Main container with Gradient Background ---
        JPanel mainPanel = new JPanel(new GridBagLayout()) { // Changed to GridBagLayout
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                // Define gradient colors: dark blue to sky blue
                Color color1 = BUTTON_DARK_BLUE;
                Color color2 = new Color(135, 206, 250); // Sky Blue
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();

        // --- Header section (logo + title) ---
        JPanel headerPanel = createHeaderPanel();
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 0, 0); // Top padding for the header
        gbc.anchor = GridBagConstraints.SOUTH; // Anchor to the bottom of its cell
        mainPanel.add(headerPanel, gbc);

        // --- Login box ---
        JPanel loginBox = createLoginBox();
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0); // Space between header and login box
        gbc.anchor = GridBagConstraints.NORTH; // Anchor to the top of its cell
        mainPanel.add(loginBox, gbc);

        // Add everything
        setContentPane(mainPanel);
        getRootPane().setDefaultButton(loginButton);
    }

    /**
     * Header section: logo + VIA VIX text
     */
    private JPanel createHeaderPanel() {
        // Use a wrapper panel to add a vertical gap between the header and the login box
        JPanel headerBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); 
        headerBar.setOpaque(false); // Make transparent to show gradient background
        // No border needed with GridBagLayout controlling spacing

        // Logo + VIA VIX text on one line
        JPanel titleLine = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        titleLine.setOpaque(false); // Make transparent

        // Logo - IMPORTANT: Update this path!
        JLabel logoLabel = new JLabel();
        try {
            // Placeholder: Assume the provided image path or an equivalent path for the logo
            ImageIcon originalIcon = new ImageIcon(UIConfig.LOGO_PATH); 
            // Scale logo to a smaller size
            Image scaledImage = originalIcon.getImage().getScaledInstance(90,90,Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            // Fallback for missing image - Using a large, clear gear emoji/character
            logoLabel.setText("⚙️");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 60)); 
            logoLabel.setForeground(TEXT_WHITE);
        }
        titleLine.add(logoLabel);

        // VIA VIX PAYROLL title
        JLabel titleLabel = new JLabel("VIA VIX PAYROLL");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32)); // Smaller, bold font
        titleLabel.setForeground(TEXT_WHITE);
        titleLine.add(titleLabel);
        
        headerBar.add(titleLine);
        return headerBar;
    }

    /**
     * Login box UI
     */
    private JPanel createLoginBox() {
        // Use GridBagLayout for flexible, aligned form elements
        JPanel box = new JPanel(new GridBagLayout());
        box.setBackground(SECONDARY_LIGHT_BLUE);
        // Set fixed size based on visual proportions in the image (e.g., 450x300 looks right)
        box.setPreferredSize(new Dimension(450, 300)); 
        box.setMinimumSize(new Dimension(450, 300)); // Ensure it maintains size
        
        // Use EmptyBorder for the internal padding of the light blue box
        box.setBorder(new EmptyBorder(15, 20, 15, 20)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5); // Default insets for field/label rows

        // --- 1. LOGIN title ---
        JLabel loginTitle = new JLabel("LOGIN", SwingConstants.CENTER);
        loginTitle.setFont(new Font("Arial", Font.BOLD, 24)); // Bold and larger font
        loginTitle.setForeground(TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 5, 20, 5); // Reduced bottom margin for title
        box.add(loginTitle, gbc);

        // --- 2. Username Label ---
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 18)); // Slightly larger label font
        userLabel.setForeground(TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 5, 10, 5);
        box.add(userLabel, gbc);

        // --- 3. Username Field ---
        usernameField = new JTextField(15); // Width set by preferred size below
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(200, 35)); // Fixed height/width to match image
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        box.add(usernameField, gbc);

        // --- 4. Password Label ---
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        passLabel.setForeground(TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        box.add(passLabel, gbc);

        // --- 5. Password Field ---
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBorder(null); // Remove border to blend into the wrapper

        JToggleButton showHideButton = new JToggleButton("\uD83D\uDC41"); // Unicode for eye icon
        showHideButton.setFont(new Font("Arial", Font.BOLD, 18)); // Larger font for the icon
        showHideButton.setPreferredSize(new Dimension(45, 35)); // Adjusted size for an icon
        showHideButton.setFocusPainted(false);
        showHideButton.setForeground(TEXT_DARK);
        showHideButton.setBackground(Color.WHITE);
        showHideButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel passwordWrapper = new JPanel(new BorderLayout());
        passwordWrapper.setPreferredSize(new Dimension(200, 35));
        passwordWrapper.setBackground(Color.WHITE);
        passwordWrapper.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        passwordWrapper.add(passwordField, BorderLayout.CENTER);
        passwordWrapper.add(showHideButton, BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        box.add(passwordWrapper, gbc);

        // --- 6. Login button ---
        loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        loginButton.setBackground(BUTTON_DARK_BLUE);
        loginButton.setForeground(TEXT_WHITE); 
        loginButton.setContentAreaFilled(false); // Required to show custom background color
        loginButton.setOpaque(true);             // Makes the background visible
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(180, 45)); // Button size to match visual aspect
        loginButton.addActionListener(e -> performLogin());

        // Add action listener for the show/hide button
        showHideButton.addActionListener(e -> {
            if (showHideButton.isSelected()) {
                passwordField.setEchoChar((char) 0); // Show password text
                showHideButton.setText("\u2715 "); // Set to 'X' icon
            } else {
                passwordField.setEchoChar('•'); // Hide password with a bullet
                showHideButton.setText("\uD83D\uDC41"); // Set back to eye icon
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 5, 5, 5); // Further reduced top margin
        box.add(loginButton, gbc);

        return box;
    }

    /**
     * Handles the login logic (Kept same as original for functionality)
     */
    private void performLogin() {
        // Check if the account is currently in a lockout period
        if (System.currentTimeMillis() < lockoutEndTime) {
            long remainingSeconds = (lockoutEndTime - System.currentTimeMillis()) / 1000;
            JOptionPane.showMessageDialog(this,
                    "Too many failed attempts. Please wait " + remainingSeconds + " more seconds.",
                    "Account Locked",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // If we are past a previous lockout time, reset the attempt counter.
        if (loginAttempts >= MAX_ATTEMPTS) loginAttempts = 0;

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username and password cannot be empty.",
                    "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("LOGGING IN...");

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Database logic from original code
                // NOTE: The 'DB' and 'Dashboard' classes are assumed to exist and work correctly 
                // within the 'com.via.vix.payroll' package structure.
                try (var c = DB.get();
                     var ps = c.prepareStatement(
                             "SELECT * FROM users WHERE username = ? AND password = ?")) {
                    ps.setString(1, username);
                    ps.setString(2, password);
                    try (ResultSet rs = ps.executeQuery()) {
                        return rs.next();
                    }
                }
            }

            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        // On successful login, reset attempts and clear any lockout.
                        loginAttempts = 0;
                        dispose();
                        // Dashboard class is required for this to compile and run successfully
                        new Dashboard(username).setVisible(true); 
                    } else {
                        JOptionPane.showMessageDialog(Login.this,
                                "Invalid username or password.",
                                "Login Failed", JOptionPane.ERROR_MESSAGE);
                        passwordField.setText("");
                        loginAttempts++;

                        if (loginAttempts >= MAX_ATTEMPTS) {
                            lockoutEndTime = System.currentTimeMillis() + (LOCKOUT_DURATION_SECONDS * 1000);
                            JOptionPane.showMessageDialog(Login.this,
                                    "You have exceeded the maximum number of login attempts.\n" +
                                    "Your account is locked for " + LOCKOUT_DURATION_SECONDS + " seconds.",
                                    "Account Locked",
                                    JOptionPane.WARNING_MESSAGE);
                        } else {
                            int remaining = MAX_ATTEMPTS - loginAttempts;
                            JOptionPane.showMessageDialog(Login.this,
                                    "Invalid credentials. You have " + remaining + " attempt(s) remaining.",
                                    "Login Failed",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(Login.this,
                            "Database error: " + e.getMessage(),
                            "Login Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("LOGIN");
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        // Ensure all GUI updates are done on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}