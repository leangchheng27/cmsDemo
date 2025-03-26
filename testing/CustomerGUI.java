package testing;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerGUI {
    private JFrame frame;
    private Customer customer;
    private JPanel contentPanel;

    public CustomerGUI(Customer customer) {
        this.customer = customer;

        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Customer Portal - Watch2Day");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(139, 0, 0)); // Body background color

        // Extract username from email
        String username = customer.getEmail().split("@")[0]; // Get the part before '@'

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(139, 0, 0)); // Match the body background color
        JLabel welcomeLabel = new JLabel("Welcome " + username, SwingConstants.CENTER); // Display username
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE); // White text color
        headerPanel.add(welcomeLabel);
        frame.add(headerPanel, BorderLayout.NORTH);

        // Sidebar Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 10)); // Updated to 6 rows for the new button
        buttonPanel.setBackground(Color.BLACK); // Black background for the button panel

        JButton viewMoviesBtn = createButton("View Movies");
        JButton viewShowsBtn = createButton("View All Shows");
        JButton myBookingsBtn = createButton("My Bookings");
        JButton viewBalanceBtn = createButton("View Balance");
        JButton topUpBalanceBtn = createButton("Top Up");
        JButton logoutBtn = createButton("Log Out");

        buttonPanel.add(viewMoviesBtn);
        buttonPanel.add(viewShowsBtn);
        buttonPanel.add(myBookingsBtn);
        buttonPanel.add(viewBalanceBtn);
        buttonPanel.add(topUpBalanceBtn);
        buttonPanel.add(logoutBtn);

        frame.add(buttonPanel, BorderLayout.WEST);

        // Content Panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(5, 2, 10, 10));
        displayMovies(); // Default content is movies

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Button Actions
        viewMoviesBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Refreshing movies...");
            displayMovies();
        });
        viewShowsBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Refreshing all shows...");
            displayShows(); // Reload all shows
        });
        myBookingsBtn.addActionListener(e -> showMyBookings());
        viewBalanceBtn.addActionListener(e -> viewBalance());
        topUpBalanceBtn.addActionListener(e -> topUpBalance());
        logoutBtn.addActionListener(e -> {
            frame.dispose();
            new GUI(); // Return to main login page
        });

        frame.setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(Color.BLACK); // Black background
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false); // Remove focus border
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE)); // Add a white border
        return button;
    }

    private void displayMovies() {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridLayout(5, 2, 10, 10)); // Add spacing between panels
    
        String query = "SELECT id, title, image_path FROM movies WHERE is_available = TRUE";
        try (PreparedStatement stmt = MySQLConnection.getConnection().prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int movieId = rs.getInt("id");
                String title = rs.getString("title");
                String imagePath = rs.getString("image_path");
    
                // Create a panel for each movie
                JPanel moviePanel = new JPanel(new BorderLayout());
                moviePanel.setBackground(new Color(139, 0, 0));
                moviePanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2)); // Add a border
    
                // Add the title at the top
                JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
                titleLabel.setForeground(Color.WHITE);
                titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Increased font size
                titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add padding
                moviePanel.add(titleLabel, BorderLayout.NORTH);
    
                // Add the movie image in the center
                ImageIcon icon;
                if (imagePath == null || imagePath.trim().isEmpty() || !new java.io.File(imagePath).exists()) {
                    // Use the absolute path for the default image
                    icon = new ImageIcon("/Users/macbookpro/Downloads/Test 2/testing/movies/default_image.png");
                } else {
                    icon = new ImageIcon(imagePath);
                }
    
                // Scale the image
                Image img = icon.getImage().getScaledInstance(370, 245, Image.SCALE_SMOOTH);
                JLabel movieLabel = new JLabel(new ImageIcon(img));
                moviePanel.add(movieLabel, BorderLayout.CENTER);
    
                // Add the "View Shows" button at the bottom
                JButton viewShowsButton = new JButton("View Shows");
                viewShowsButton.setFont(new Font("Arial", Font.BOLD, 16));
                viewShowsButton.setBackground(Color.WHITE);
                viewShowsButton.setForeground(Color.BLACK);
                viewShowsButton.setFocusPainted(false);
                viewShowsButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                viewShowsButton.addActionListener(e -> displayShowsForMovie(movieId, title));
                moviePanel.add(viewShowsButton, BorderLayout.SOUTH);
    
                // Add the movie panel to the contentPanel
                contentPanel.add(moviePanel);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading movies: " + e.getMessage());
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void displayShows() {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridLayout(5, 2, 10, 10));

        String query = "SELECT m.title, s.id, s.show_time, s.available_seats, s.ticket_price, s.hall_number " +
                       "FROM shows s " +
                       "JOIN movies m ON s.movie_id = m.id";
        try (PreparedStatement stmt = MySQLConnection.getConnection().prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String title = rs.getString("title");
                int showId = rs.getInt("id");
                String showTime = rs.getString("show_time");
                int availableSeats = rs.getInt("available_seats");
                double ticketPrice = rs.getDouble("ticket_price");
                int hallNumber = rs.getInt("hall_number");

                JPanel showPanel = new JPanel(new BorderLayout());
                showPanel.setBackground(new Color(139, 0, 0));
                showPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2)); // Add a border

                // Add the show details
                JLabel detailsLabel = new JLabel(
                        String.format("<html><center>Movie: %s<br>Time: %s<br>Seats: %d<br>Price: $%.2f<br>Hall: %d</center></html>",
                                title, showTime, availableSeats, ticketPrice, hallNumber),
                        SwingConstants.CENTER);
                detailsLabel.setForeground(Color.WHITE);
                detailsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                showPanel.add(detailsLabel, BorderLayout.CENTER);

                // Add the "Book" button at the bottom
                JButton bookButton = new JButton("Book");
                bookButton.setFont(new Font("Arial", Font.BOLD, 16));
                bookButton.setBackground(Color.WHITE);
                bookButton.setForeground(Color.BLACK);
                bookButton.setFocusPainted(false);
                bookButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                bookButton.addActionListener(e -> bookMovie(showId, title));
                showPanel.add(bookButton, BorderLayout.SOUTH);

                contentPanel.add(showPanel);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading shows: " + e.getMessage());
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void displayShowsForMovie(int movieId, String movieTitle) {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridLayout(5, 2, 10, 10));

        String query = "SELECT id, show_time, available_seats, ticket_price, hall_number " +
                       "FROM shows WHERE movie_id = ?";
        try (PreparedStatement stmt = MySQLConnection.getConnection().prepareStatement(query)) {
            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int showId = rs.getInt("id");
                String showTime = rs.getString("show_time");
                int availableSeats = rs.getInt("available_seats");
                double ticketPrice = rs.getDouble("ticket_price");
                int hallNumber = rs.getInt("hall_number");

                JPanel showPanel = new JPanel(new BorderLayout());
                showPanel.setBackground(new Color(139, 0, 0));
                showPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2)); // Add a border

                // Add the show details
                JLabel detailsLabel = new JLabel(
                        String.format("<html><center>Time: %s<br>Seats: %d<br>Price: $%.2f<br>Hall: %d</center></html>",
                                showTime, availableSeats, ticketPrice, hallNumber),
                        SwingConstants.CENTER);
                detailsLabel.setForeground(Color.WHITE);
                detailsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                showPanel.add(detailsLabel, BorderLayout.CENTER);

                // Add the "Book" button at the bottom
                JButton bookButton = new JButton("Book");
                bookButton.setFont(new Font("Arial", Font.BOLD, 16));
                bookButton.setBackground(Color.WHITE);
                bookButton.setForeground(Color.BLACK);
                bookButton.setFocusPainted(false);
                bookButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                bookButton.addActionListener(e -> bookMovie(showId, movieTitle));
                showPanel.add(bookButton, BorderLayout.SOUTH);

                contentPanel.add(showPanel);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading shows: " + e.getMessage());
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void bookMovie(int showId, String movieTitle) {
        String input = JOptionPane.showInputDialog(frame, "Enter the number of tickets to book for \"" + movieTitle + "\":");
        if (input != null) {
            try {
                int tickets = Integer.parseInt(input);
                if (tickets <= 0) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number of tickets.");
                    return;
                }
    
                // Check available seats
                String checkQuery = "SELECT available_seats, ticket_price FROM shows WHERE id = ? AND available_seats >= ?";
                try (Connection conn = MySQLConnection.getConnection();
                     PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                    checkStmt.setInt(1, showId);
                    checkStmt.setInt(2, tickets);
                    ResultSet rs = checkStmt.executeQuery();
    
                    if (rs.next()) {
                        double ticketPrice = rs.getDouble("ticket_price");
                        double totalCost = tickets * ticketPrice;
    
                        // Check if the customer has enough balance
                        if (customer.getBalance() < totalCost) {
                            JOptionPane.showMessageDialog(frame, "Insufficient balance. Please top up your account.");
                            return;
                        }
    
                        // Proceed with booking
                        String bookQuery = "INSERT INTO tickets (customer_id, show_id, number_of_tickets, total_cost) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement bookStmt = conn.prepareStatement(bookQuery)) {
                            System.out.println("Customer ID being used for booking: " + customer.getId()); // Debugging statement
                            bookStmt.setInt(1, customer.getId());
                            bookStmt.setInt(2, showId);
                            bookStmt.setInt(3, tickets);
                            bookStmt.setDouble(4, totalCost);
                            bookStmt.executeUpdate();
    
                            JOptionPane.showMessageDialog(frame, "Booking successful! Total cost: $" + totalCost);
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Not enough seats available.");
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid number of tickets entered.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame, "Error processing booking: " + e.getMessage());
            }
        }
    }

    private void showMyBookings() {
        StringBuilder bookings = new StringBuilder("Your Bookings:\n");
        String query = "SELECT t.id, m.title, t.number_of_tickets, t.total_cost " +
                       "FROM tickets t " +
                       "JOIN shows s ON t.show_id = s.id " +
                       "JOIN movies m ON s.movie_id = m.id " +
                       "WHERE t.customer_id = ?";
        try (PreparedStatement stmt = MySQLConnection.getConnection().prepareStatement(query)) {
            stmt.setInt(1, customer.getId());
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                int bookingId = rs.getInt("id");
                String title = rs.getString("title");
                int tickets = rs.getInt("number_of_tickets");
                double totalCost = rs.getDouble("total_cost");
    
                bookings.append(String.format("Booking ID: %d\nMovie: %s\nTickets: %d\nTotal Cost: $%.2f\n\n",
                        bookingId, title, tickets, totalCost));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading bookings: " + e.getMessage());
        }
    
        JOptionPane.showMessageDialog(frame, bookings.toString());
    }

    private void viewBalance() {
        JOptionPane.showMessageDialog(frame, "Your current balance is: $" + customer.getBalance());
    }

    private void topUpBalance() {
        String input = JOptionPane.showInputDialog(frame, "Enter amount to top up:");
        if (input != null) {
            try {
                double amount = Double.parseDouble(input);
                if (amount > 0) {
                    customer.setBalance(customer.getBalance() + amount);
    
                    String query = "UPDATE customers SET balance = ? WHERE id = ?";
                    try (Connection conn = MySQLConnection.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(query)) {
                        pstmt.setDouble(1, customer.getBalance());
                        pstmt.setInt(2, customer.getId());
                        pstmt.executeUpdate();
                    }
    
                    System.out.println("Updated balance in database: " + customer.getBalance());
                    JOptionPane.showMessageDialog(frame, "Balance successfully topped up! New balance: $" + customer.getBalance());
                } else {
                    JOptionPane.showMessageDialog(frame, "Please enter a positive amount.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid amount entered. Please enter a valid number.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame, "Error updating balance in the database: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Top-up canceled.");
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM customers WHERE id = ?")) {
                pstmt.setInt(1, 1); // Replace with the actual customer ID
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    double balance = rs.getDouble("balance");
                    String role = "Customer";
    
                    System.out.println("Fetched balance from database: " + balance);
                    new CustomerGUI(new Customer(id, email, password, balance, role));
                } else {
                    JOptionPane.showMessageDialog(null, "Customer not found in the database!");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error fetching customer data: " + e.getMessage());
            }
        });
    }
}
