package testing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GUI {
    private JFrame frame;

    public GUI() {
        frame = new JFrame("Watch2Day");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(139, 0, 0));

        JLabel titleLabel = new JLabel("Welcome to Watch2Day", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(new Color(139, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton loginButton = createStyledButton("LOGIN");
        JButton registerButton = createStyledButton("REGISTER");
        JButton exitButton = createStyledButton("EXIT");

        loginButton.addActionListener(new LoginHandler());
        registerButton.addActionListener(new RegisterHandler());
        exitButton.addActionListener(e -> System.exit(0));

        gbc.gridy = 0;
        buttonPanel.add(loginButton, gbc);
        gbc.gridy = 1;
        buttonPanel.add(registerButton, gbc);
        gbc.gridy = 2;
        buttonPanel.add(exitButton, gbc);

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(400, 100));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        return button;
    }

    private class LoginHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = JOptionPane.showInputDialog("Enter Email:");
            String password = JOptionPane.showInputDialog("Enter Password:");

            if (email != null && password != null) {
                try (Connection conn = MySQLConnection.getConnection()) {
                    // Check if the user is an admin
                    String adminQuery = "SELECT * FROM admins WHERE email=? AND password=?";
                    try (PreparedStatement pstmt = conn.prepareStatement(adminQuery)) {
                        pstmt.setString(1, email);
                        pstmt.setString(2, password);
                        ResultSet rs = pstmt.executeQuery();

                        if (rs.next()) {
                            JOptionPane.showMessageDialog(frame, "Admin Login Successful!");
                            frame.dispose();
                            new AdminGUI(email);
                            return;
                        }
                    }

                    // Check if the user is a customer
                    String customerQuery = "SELECT * FROM customers WHERE email=? AND password=?";
                    try (PreparedStatement pstmt = conn.prepareStatement(customerQuery)) {
                        pstmt.setString(1, email);
                        pstmt.setString(2, password);
                        ResultSet rs = pstmt.executeQuery();

                        if (rs.next()) {
                            int customerId = rs.getInt("id");
                            System.out.println("Customer ID fetched during login: " + customerId); // Debugging statement
                            JOptionPane.showMessageDialog(frame, "Customer Login Successful!");
                            frame.dispose();
                            new CustomerGUI(new Customer(
                                customerId,
                                rs.getString("email"),
                                rs.getString("password"),
                                rs.getDouble("balance"),
                                "Customer"
                            ));
                            return;
                        }
                    }

                    JOptionPane.showMessageDialog(frame, "Invalid Email or Password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class RegisterHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = JOptionPane.showInputDialog("Enter Email:");
            String password = JOptionPane.showInputDialog("Enter Password:");
            String confirmPassword = JOptionPane.showInputDialog("Confirm Password:");
            String[] roles = {"admin", "customer"};
            String role = (String) JOptionPane.showInputDialog(frame, "Select Role:", "Role Selection",
                    JOptionPane.QUESTION_MESSAGE, null, roles, roles[1]);

            if (email != null && password != null && password.equals(confirmPassword) && role != null) {
                try (Connection conn = MySQLConnection.getConnection()) {
                    if ("admin".equalsIgnoreCase(role)) {
                        // Insert into admins table
                        String query = "INSERT INTO admins (email, password) VALUES (?, ?)";
                        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                            pstmt.setString(1, email);
                            pstmt.setString(2, password);
                            pstmt.executeUpdate();
                        }
                    } else if ("customer".equalsIgnoreCase(role)) {
                        // Insert into customers table
                        String query = "INSERT INTO customers (email, password, balance, total_purchases) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                            pstmt.setString(1, email);
                            pstmt.setString(2, password);
                            pstmt.setDouble(3, 0.00); // Initial balance for customers
                            pstmt.setDouble(4, 0.00); // Initial total purchases for customers
                            pstmt.executeUpdate();
                        }
                    }
                    JOptionPane.showMessageDialog(frame, "Registration Successful!");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Registration Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Passwords do not match or invalid input!", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}
