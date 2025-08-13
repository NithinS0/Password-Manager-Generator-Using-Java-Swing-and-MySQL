import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.security.SecureRandom;
import java.sql.*;
import javax.swing.*;

public class PasswordGeneratorSwing extends JFrame {
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = "ABCDEFGHIJKLMNOPQURSTUVWXYZ";
    private static final String DIGIT = "0123456789";
    private static final String SPECIAL_CHAR = "!@#$%^&*?";
    private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + DIGIT + SPECIAL_CHAR;
    private static final SecureRandom random = new SecureRandom();

    private JTextField lengthField;
    private JTextField usernameField;
    private JPasswordField userPasswordField;
    private JButton generateButton;
    private JButton saveButton;
    private JButton viewSavedButton;
    private JButton deleteButton;
    private JTextArea passwordArea;
    private Timer passwordTimer;

    private final Color primaryColor = new Color(53, 59, 72);
    private final Color secondaryColor = new Color(233, 236, 239);
    private final Color accentColor = new Color(47, 54, 64);
    private final Color buttonColor = new Color(0, 184, 148);

    private int loggedInUserId = -1;
    private String loggedInUserName = "";

    // Database connection constants
    private static final String DB_URL = "jdbc:mysql://localhost:3306/password_manager";
    private static final String DB_USER = "YOUR_DB_USERNAME"; // <-- Replace in local
    private static final String DB_PASS = "YOUR_DB_PASSWORD"; // <-- Replace in local

    public PasswordGeneratorSwing() {
        if (!showLoginDialog()) {
            System.exit(0);
        }

        this.setTitle("Password Generator - Welcome " + loggedInUserName);
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.getContentPane().setBackground(this.primaryColor);

        JLabel titleLabel = new JLabel("Password Generator");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(this.secondaryColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        this.add(titleLabel, gbc);

        JLabel lengthLabel = new JLabel("Enter Password Length:");
        lengthLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lengthLabel.setForeground(this.secondaryColor);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        this.add(lengthLabel, gbc);

        this.lengthField = new JTextField(10);
        this.lengthField.setBackground(this.accentColor);
        this.lengthField.setForeground(this.secondaryColor);
        this.lengthField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 1;
        this.add(this.lengthField, gbc);

        JLabel passwordLabel = new JLabel("Generated Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        passwordLabel.setForeground(this.secondaryColor);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        this.add(passwordLabel, gbc);

        this.passwordArea = new JTextArea(2, 30);
        this.passwordArea.setEditable(false);
        this.passwordArea.setBackground(this.accentColor);
        this.passwordArea.setForeground(this.secondaryColor);
        this.passwordArea.setFont(new Font("Monospaced", Font.BOLD, 18));
        this.passwordArea.setMargin(new Insets(10, 10, 10, 10));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        this.add(this.passwordArea, gbc);

        this.generateButton = new JButton("Generate Password");
        styleButton(this.generateButton);
        this.generateButton.addActionListener((e) -> {
            this.generatePasswordAction();
            if (passwordTimer != null && passwordTimer.isRunning()) {
                passwordTimer.stop();
            }
            passwordTimer = new Timer(30000, evt -> this.generatePasswordAction());
            passwordTimer.start();
        });
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        this.add(this.generateButton, gbc);

        JLabel usernameLabel = new JLabel("Enter Username to Save:");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        usernameLabel.setForeground(this.secondaryColor);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        this.add(usernameLabel, gbc);

        this.usernameField = new JTextField(15);
        this.usernameField.setBackground(this.accentColor);
        this.usernameField.setForeground(this.secondaryColor);
        this.usernameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 5;
        this.add(this.usernameField, gbc);

        JLabel userPasswordLabel = new JLabel("Enter Password:");
        userPasswordLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        userPasswordLabel.setForeground(this.secondaryColor);
        gbc.gridx = 0;
        gbc.gridy = 6;
        this.add(userPasswordLabel, gbc);

        this.userPasswordField = new JPasswordField(15);
        this.userPasswordField.setBackground(this.accentColor);
        this.userPasswordField.setForeground(this.secondaryColor);
        this.userPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 6;
        this.add(this.userPasswordField, gbc);

        this.saveButton = new JButton("Save Username & Password");
        styleButton(this.saveButton);
        this.saveButton.addActionListener((e) -> this.savePasswordAction());
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        this.add(this.saveButton, gbc);

        this.viewSavedButton = new JButton("View My Saved Passwords");
        styleButton(this.viewSavedButton);
        this.viewSavedButton.addActionListener((e) -> this.viewSavedPasswordsAction());
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        this.add(this.viewSavedButton, gbc);

        this.deleteButton = new JButton("Delete Saved Password");
        styleButton(this.deleteButton);
        this.deleteButton.addActionListener((e) -> this.deletePasswordAction());
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        this.add(this.deleteButton, gbc);

        this.setVisible(true);
    }

    private boolean showLoginDialog() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String[] options = {"Login", "Register"};
            int choice = JOptionPane.showOptionDialog(this, "Choose an option", "Login / Register",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (choice == 1) { // Register
                String name = JOptionPane.showInputDialog(this, "Enter your name:");
                String email = JOptionPane.showInputDialog(this, "Enter your email:");
                String password = JOptionPane.showInputDialog(this, "Enter a new password:");
                if (name == null || email == null || password == null || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM users WHERE email = ?");
                checkStmt.setString(1, email);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Email already registered. Please login.");
                    return false;
                }
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (name, email, password) VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, password);
                stmt.executeUpdate();
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    loggedInUserId = keys.getInt(1);
                    loggedInUserName = name;
                    JOptionPane.showMessageDialog(this, "Registration successful! Welcome, " + name);
                    return true;
                }
            } else if (choice == 0) { // Login
                String email = JOptionPane.showInputDialog(this, "Enter your email:");
                String password = JOptionPane.showInputDialog(this, "Enter your password:");
                PreparedStatement stmt = conn.prepareStatement("SELECT id, name FROM users WHERE email = ? AND password = ?");
                stmt.setString(1, email);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    loggedInUserId = rs.getInt("id");
                    loggedInUserName = rs.getString("name");
                    JOptionPane.showMessageDialog(this, "Login successful! Welcome back, " + loggedInUserName);
                    return true;
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private void generatePasswordAction() {
        String lengthText = this.lengthField.getText();
        try {
            int length = Integer.parseInt(lengthText);
            if (length < 8) {
                this.passwordArea.setText("Length must be at least 8.");
            } else {
                String password = generatePassword(length);
                this.passwordArea.setText(password);
            }
        } catch (NumberFormatException e) {
            this.passwordArea.setText("Invalid input. Please enter a number.");
        }
    }

    private void savePasswordAction() {
        if (loggedInUserId == -1) {
            JOptionPane.showMessageDialog(this, "You must be logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String username = this.usernameField.getText();
        String password = new String(this.userPasswordField.getPassword());
        if (password.isEmpty()) {
            password = this.passwordArea.getText();
        }
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "INSERT INTO passwords (user_id, username, password) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, loggedInUserId);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Password saved successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewSavedPasswordsAction() {
        if (loggedInUserId == -1) {
            JOptionPane.showMessageDialog(this, "You must be logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT username, password FROM passwords WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, loggedInUserId);
            ResultSet rs = stmt.executeQuery();
            StringBuilder savedPasswords = new StringBuilder("Your Saved Passwords:\n");
            while (rs.next()) {
                savedPasswords.append("Username: ").append(rs.getString("username"))
                        .append(", Password: ").append(rs.getString("password")).append("\n");
            }
            this.passwordArea.setText(savedPasswords.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePasswordAction() {
        if (loggedInUserId == -1) {
            JOptionPane.showMessageDialog(this, "You must be logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String username = JOptionPane.showInputDialog(this, "Enter username to delete:");
        if (username != null && !username.isEmpty()) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String query = "DELETE FROM passwords WHERE user_id = ? AND username = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, loggedInUserId);
                stmt.setString(2, username);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Password deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "No password found for that username.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database connection error.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String generatePassword(int length) {
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(PASSWORD_ALLOW_BASE.length());
            password.append(PASSWORD_ALLOW_BASE.charAt(index));
        }
        return password.toString();
    }

    private void styleButton(JButton button) {
        button.setBackground(this.buttonColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PasswordGeneratorSwing::new);
    }
}
