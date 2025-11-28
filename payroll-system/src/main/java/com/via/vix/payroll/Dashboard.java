package com.via.vix.payroll;

import com.via.vix.payroll.ui.MainPanel;
import com.via.vix.payroll.ui.PayrollPanel;
import com.via.vix.payroll.ui.ReportPanel;
import com.via.vix.payroll.util.UIConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.io.File; // Needed for file loading
import java.util.LinkedHashMap;

/**
 * Represents the main dashboard window of the Payroll System.
 * This frame serves as the primary user interface after login, providing navigation
 * to different modules like Employee Management, Payroll Processing, and Reports.
 * It features a modern UI with a header, a side navigation bar, and a central content area.
 */
public class Dashboard extends JFrame {
    private CardLayout card = new CardLayout();
    private JPanel content;
    private JLabel clockLabel, dateLabel, userLabel;
    private final java.util.List<JButton> navButtons = new ArrayList<>();    
    private JButton activeButton;

    // Define a modern color palette
    // A modern color palette for a consistent and professional look.
    /** Primary color for headers and major UI elements. */
    private static final Color PRIMARY_BLUE = new Color(63, 81, 181);   // Material Design Indigo 500
    /** A lighter shade used for highlighting or backgrounds. */
    private static final Color LIGHT_BLUE = new Color(197, 202, 233); // Material Design Indigo 100
    /** Accent color for special elements, though not currently used in this class. */
    private static final Color ACCENT_PINK = new Color(255, 64, 129);  // Material Design Pink A200
    /** Background color for the navigation panel. */
    private static final Color NAV_BG_COLOR = new Color(48, 63, 159); // Darker Indigo for Nav    
    /** Standard text color for light backgrounds. */
    private static final Color TEXT_WHITE = Color.WHITE;
    /** A pleasant blue used for active/hover states in the navigation. */
    private static final Color SKY_BLUE = new Color(135, 206, 250);
    /** Standard text color for dark backgrounds. */
    private static final Color TEXT_DARK_GRAY = new Color(50, 50, 50);

    public Dashboard(String username) {
        setTitle("Payroll System - Dashboard");
        // Changed to DISPOSE_ON_CLOSE to allow returning to the login screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        // Set the frame to be maximized by default
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Set the frame icon to match the login logo
        try {
            ImageIcon frameIcon = new ImageIcon(UIConfig.LOGO_PATH);
            setIconImage(frameIcon.getImage());
        } catch (Exception e) {
            System.err.println("Could not set frame icon: " + e.getMessage());
        }

    // ===== HEADER PANEL =====
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = PRIMARY_BLUE.darker(); // Start darker
                Color color2 = PRIMARY_BLUE; // End with primary blue
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        header.setOpaque(false); // Ensure custom painting is visible
        header.setMinimumSize(new Dimension(0, 70));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        header.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15)); // Add some padding

        // --- Logo + Title (left) START MODIFICATION ---
        
        // Panel to hold both the icon and the text label
        JPanel logoAndTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); 
        logoAndTitlePanel.setOpaque(false);
        
        // 1. Load and scale the new logo
        JLabel mainLogo = new JLabel();
        int logoSize = UIConfig.DASHBOARD_HEADER_LOGO_SIZE; // Desired size for the logo
        
        // Try to load the logo from the classpath (preferred method)
        java.net.URL logoUrl = getClass().getClassLoader().getResource("payroll_logo.png");
        if (logoUrl == null) {
            // Fallback: try loading from the file system (less recommended for deployment)
            try {
                logoUrl = new File(UIConfig.LOGO_PATH).toURI().toURL();
            } catch (MalformedURLException e) {
                System.err.println("Error creating URL for payroll_logo.png from file system: " + e.getMessage());
            }
        }
        
        if (logoUrl != null) {
            try {
                ImageIcon originalIcon = new ImageIcon(logoUrl);
                Image scaledImage = originalIcon.getImage().getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH);
                mainLogo.setIcon(new ImageIcon(scaledImage));
                // Center the logo vertically in the header
                mainLogo.setVerticalAlignment(SwingConstants.CENTER);
            } catch (Exception e) {
                System.err.println("Error loading payroll_logo.png: " + e.getMessage());
                // Use a default text if the image fails to load
                mainLogo.setText("[Logo]"); 
                mainLogo.setForeground(TEXT_WHITE);
            }
        } else {
            mainLogo.setText("[Logo]"); 
            mainLogo.setForeground(TEXT_WHITE);
        }

        // 2. Text label
        JLabel logoText = new JLabel("VIA VIX PAYROLL");
        logoText.setForeground(TEXT_WHITE);
        logoText.setFont(new Font("SansSerif", Font.BOLD, 28)); // Slightly larger font

        // Add components to the new panel
        logoAndTitlePanel.add(mainLogo);
        logoAndTitlePanel.add(logoText);
        
        // Add the new panel to the header
        header.add(logoAndTitlePanel, BorderLayout.WEST);
        
        // Remove the old logo label
        // JLabel logo = new JLabel("VIA VIX PAYROLL"); // REMOVED
        // header.add(logo, BorderLayout.WEST); // REMOVED
        
        // --- Logo + Title (left) END MODIFICATION ---

        // Clock & Date (center)
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        clockLabel = new JLabel("00:00:00");
        clockLabel.setForeground(TEXT_WHITE);
        clockLabel.setFont(new Font("Monospaced", Font.BOLD, 22)); // Slightly larger font
        clockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        dateLabel = new JLabel("YYYY-MM-DD");
        dateLabel.setForeground(TEXT_WHITE);
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Changed to PLAIN for date
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(Box.createVerticalStrut(10)); // Reduced strut
        center.add(clockLabel);
        center.add(dateLabel);

        header.add(center, BorderLayout.CENTER);

        // User info (right)
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0)); // Reduced horizontal gap
        userInfoPanel.setOpaque(false);

        JLabel userIcon;
        java.net.URL userIconUrl = getClass().getClassLoader().getResource("user.png");
        if (userIconUrl != null) {
            ImageIcon originalIcon = new ImageIcon(userIconUrl);
            Image scaledImage = originalIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH); // Scale icon
            userIcon = new JLabel(new ImageIcon(scaledImage));
        } else {
            userIcon = new JLabel("User Icon Missing");
        }
        userLabel = new JLabel(username);
        userLabel.setForeground(TEXT_WHITE);
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 16)); // Slightly larger font

        userInfoPanel.add(userIcon);
        userInfoPanel.add(userLabel);

        // Wrapper panel to vertically center the user info
        JPanel userPanelWrapper = new JPanel(new GridBagLayout());
        userPanelWrapper.setOpaque(false);
        userPanelWrapper.add(userInfoPanel); // GridBagLayout centers by default

        header.add(userPanelWrapper, BorderLayout.EAST);
        
        add(header, BorderLayout.NORTH);

        // ===== NAVIGATION PANEL =====
        add(createNavigationPanel(), BorderLayout.WEST);

        // ===== CONTENT PANELS =====
        // Apply a gradient background from dark blue to sky blue for the content area
        content = new JPanel(card) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, NAV_BG_COLOR, 0, h, SKY_BLUE);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };

        content.add(new MainPanel(), "Employee");
        content.add(new PayrollPanel(), "Payroll");
        content.add(new ReportPanel(), "Report");

        // The method createCompoundBorder() in the type BorderFactory is not applicable for the arguments (Border)
        // The original code was attempting to pass a single Border object to createCompoundBorder, which expects two.
        // To fix this, we can simply set the empty border directly if only one border is intended.
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Outer padding
        add(content, BorderLayout.CENTER);
        card.show(content, "Employee"); // Show Employee panel by default

        // ===== EVENT BINDINGS =====

        JPopupMenu logoutMenu = new JPopupMenu();
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutMenu.add(logoutItem);

        logoutItem.addActionListener(e -> {
            dispose();
            // Re-open the login screen
            new Login().setVisible(true);
        });

        userLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                logoutMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        // Start clock/date
        new javax.swing.Timer(1000, e -> updateClock()).start();
        updateClock();
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(NAV_BG_COLOR);
        navPanel.setPreferredSize(new Dimension(180, 0)); // Reduced width for a smaller nav bar
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Use a map to associate button text with icon filenames
        LinkedHashMap<String, String> navItems = new LinkedHashMap<>();
        navItems.put("Employee", "employee_icon.png"); // Assumes employee_icon.png is in resources
        navItems.put("Payroll", "payroll_icon.png");   // Assumes payroll_icon.png is in resources
        navItems.put("Report", "report_icon.png");     // Assumes report_icon.png is in resources

        for (var entry : navItems.entrySet()) {
            String text = entry.getKey();
            String iconName = entry.getValue();
            JButton navButton = createNavButton(text, iconName);
            navButton.addActionListener(e -> {
                card.show(content, text);
                setActiveButton(navButton);
            });
            navPanel.add(navButton);
            navPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
            navButtons.add(navButton);
        }

        // Set the first button as active by default
        if (!navButtons.isEmpty()) {
            activeButton = navButtons.get(0);
            setActiveButton(activeButton);
        }

        return navPanel;
    }

    /**
     * Creates a styled navigation button with an icon and text.
     * @param text The text to display on the button.
     * @param iconName The filename of the icon to load from the resources.
     * @return A configured JButton for the navigation panel.
     */
    private JButton createNavButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setForeground(TEXT_WHITE);
        button.setBackground(NAV_BG_COLOR);
        button.setFont(new Font("SansSerif", Font.BOLD, 14)); // Smaller font
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(null, BorderFactory.createEmptyBorder(12, 20, 12, 20))); // Reduced padding
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        button.setOpaque(true); // Ensure the background color is painted

        // Load and set the icon
        java.net.URL iconUrl = getClass().getClassLoader().getResource(iconName);
        if (iconUrl != null) {
            ImageIcon originalIcon = new ImageIcon(iconUrl);
            Image scaledImage = originalIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH); // Smaller icon
            button.setIcon(new ImageIcon(scaledImage));
            button.setIconTextGap(15); // Reduced space between icon and text
        } else {
            System.err.println("Could not find icon: " + iconName);
        }

        // Add hover animation
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SKY_BLUE);
                button.setForeground(TEXT_DARK_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button != activeButton) {
                    resetButtonStyles(button); // Reset style, active button is handled in setActiveButton
                }
            }
        });
        return button;
    }

    /**
     * Updates the clock and date labels with the current time and date.
     */
    private void updateClock() {
        clockLabel.setText(java.time.LocalTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm:ss a")));
        dateLabel.setText(java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    /**
     * Sets the visual state of a navigation button to "active".
     * This involves changing its background and foreground colors and ensuring
     * all other navigation buttons are in their default "inactive" state.
     *
     * @param activeButton The button to be marked as active.
     */
    private void setActiveButton(JButton activeButton) {
        this.activeButton = activeButton;
        for (JButton button : navButtons) {
            resetButtonStyles(button);
        }
    }

    /**
     * Resets the style of a button based on whether it is the currently active button.
     * @param button The button to apply styles to.
     */
    private void resetButtonStyles(JButton button) {
        if (button == activeButton) {
            button.setBackground(SKY_BLUE);
            button.setForeground(TEXT_DARK_GRAY);
        } else {
            button.setBackground(NAV_BG_COLOR);
            button.setForeground(TEXT_WHITE);
        }
    }
}