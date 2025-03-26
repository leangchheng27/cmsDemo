package testing;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminGUI {
    private JFrame frame;
    private JPanel moviesPanel;

    public AdminGUI(String adminEmail) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Admin Portal - Watch2Day");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(139, 0, 0)); // Body background color

        // Extract username from email
        String username = adminEmail.split("@")[0]; // Get the part before '@'

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(139, 0, 0)); // Match the body background color
        JLabel welcomeLabel = new JLabel("Welcome " + username, SwingConstants.CENTER); // Display username
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE); // White text color
        headerPanel.add(welcomeLabel);
        frame.add(headerPanel, BorderLayout.NORTH);

        // Sidebar Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(7, 1, 10, 10)); // Updated to 7 rows for the new button
        buttonPanel.setBackground(Color.BLACK); // Black background for the button panel

        JButton addMovieBtn = createButton("Add Movie");
        JButton removeMovieBtn = createButton("Remove Movie"); // New button
        JButton addShowBtn = createButton("Add Show");
        JButton listMoviesBtn = createButton("List Movies");
        JButton listShowsBtn = createButton("List Shows");
        JButton refreshMoviesBtn = createButton("Refresh Movies");
        JButton logoutBtn = createButton("Log Out");

        buttonPanel.add(addMovieBtn);
        buttonPanel.add(removeMovieBtn); // Add the new button
        buttonPanel.add(addShowBtn);
        buttonPanel.add(listMoviesBtn);
        buttonPanel.add(listShowsBtn);
        buttonPanel.add(refreshMoviesBtn);
        buttonPanel.add(logoutBtn);

        frame.add(buttonPanel, BorderLayout.WEST);

        // Movies Grid Panel
        moviesPanel = new JPanel();
        moviesPanel.setLayout(new GridLayout(5, 2, 10, 10));
        displayMovies();

        JScrollPane scrollPane = new JScrollPane(moviesPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Button Actions
        addMovieBtn.addActionListener(e -> addMovie());
        removeMovieBtn.addActionListener(e -> removeMovie()); // Link the new button to removeMovie()
        addShowBtn.addActionListener(e -> addShow());
        listMoviesBtn.addActionListener(e -> listMovies());
        listShowsBtn.addActionListener(e -> listShows());
        refreshMoviesBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Refreshing movies...");
            displayMovies(); // Reload the movies
        });
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
        moviesPanel.removeAll();
        moviesPanel.setLayout(new GridLayout(5, 2, 10, 10));
    
        String query = "SELECT title, image_path FROM movies";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String title = rs.getString("title");
                String imagePath = rs.getString("image_path");
    
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
    
                moviesPanel.add(moviePanel);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading movies: " + e.getMessage());
        }
        moviesPanel.revalidate();
        moviesPanel.repaint();
    }

    private void addMovie() {
        String title = JOptionPane.showInputDialog("Enter movie title:");
        if (title == null || title.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Movie title cannot be empty!");
            return;
        }
    
        String genre = JOptionPane.showInputDialog("Enter genre:");
        if (genre == null || genre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Genre cannot be empty!");
            return;
        }
    
        String durationStr = JOptionPane.showInputDialog("Enter duration (mins):");
        if (durationStr == null || durationStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Duration cannot be empty!");
            return;
        }
    
        String imagePath = JOptionPane.showInputDialog("Enter image path (optional):");
        if (imagePath == null || imagePath.trim().isEmpty()) {
            imagePath = "default_image.png"; // Use a default image if none is provided
        }
    
        try {
            int duration = Integer.parseInt(durationStr);
    
            // Validate the image path (optional)
            if (!imagePath.equals("default_image.png")) {
                if (!new java.io.File(imagePath).exists()) {
                    JOptionPane.showMessageDialog(null, "Invalid image path. Using default image.");
                    imagePath = "default_image.png";
                }
            }
    
            String query = "INSERT INTO movies (title, genre, duration, is_available, image_path) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, title);
                stmt.setString(2, genre);
                stmt.setInt(3, duration);
                stmt.setBoolean(4, true);
                stmt.setString(5, imagePath);
                stmt.executeUpdate();
            }
    
            JOptionPane.showMessageDialog(null, "Movie added successfully!");
            displayMovies(); // Refresh the movie list
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid duration. Please enter a valid number.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding movie: " + e.getMessage());
        }
    }

    private void removeMovie() {
        String movieTitle = JOptionPane.showInputDialog("Enter the title of the movie to remove:");
        if (movieTitle == null || movieTitle.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Movie title cannot be empty!");
            return;
        }

        try (Connection conn = MySQLConnection.getConnection()) {
            // Check if the movie exists
            String checkQuery = "SELECT id FROM movies WHERE title = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, movieTitle);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(null, "Movie not found!");
                    return;
                }

                // Delete the movie
                String deleteQuery = "DELETE FROM movies WHERE title = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setString(1, movieTitle);
                    int rowsAffected = deleteStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Movie removed successfully!");
                        displayMovies(); // Refresh the movie list
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to remove the movie. Please try again.");
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error removing movie: " + e.getMessage());
        }
    }

    private void addShow() {
        String movieTitle = JOptionPane.showInputDialog("Enter the movie title for the show:");
        if (movieTitle == null || movieTitle.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Movie title cannot be empty!");
            return;
        }
    
        try {
            // Check if the movie exists
            String movieQuery = "SELECT id FROM movies WHERE title = ?";
            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement movieStmt = conn.prepareStatement(movieQuery)) {
                movieStmt.setString(1, movieTitle);
                ResultSet rs = movieStmt.executeQuery();
    
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(null, "Movie not found!");
                    return;
                }
    
                int movieId = rs.getInt("id");
    
                // Collect and validate show time
                String time;
                while (true) {
                    time = JOptionPane.showInputDialog("Enter show time (HH:MM):");
                    if (time == null || time.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Show time cannot be empty!");
                        return;
                    }
                    if (time.matches("([01]\\d|2[0-3]):[0-5]\\d")) { // Validate HH:MM format
                        break;
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid time format! Please enter time in HH:MM format.");
                    }
                }
    
                // Collect other show details
                String seatsStr = JOptionPane.showInputDialog("Enter available seats:");
                String priceStr = JOptionPane.showInputDialog("Enter ticket price:");
                String hallStr = JOptionPane.showInputDialog("Enter hall number:");
    
                int availableSeats = Integer.parseInt(seatsStr);
                double ticketPrice = Double.parseDouble(priceStr);
                int hallNumber = Integer.parseInt(hallStr);
    
                // Insert the new show into the database
                String showSQL = "INSERT INTO shows (movie_id, show_time, available_seats, ticket_price, hall_number, duration) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement showStmt = conn.prepareStatement(showSQL)) {
                    showStmt.setInt(1, movieId);
                    showStmt.setString(2, time); // Use the validated time directly
                    showStmt.setInt(3, availableSeats);
                    showStmt.setDouble(4, ticketPrice);
                    showStmt.setInt(5, hallNumber);
                    
                    // Prompt for duration and set it
                    String durationStr = JOptionPane.showInputDialog("Enter show duration (mins):");
                    int duration = Integer.parseInt(durationStr);
                    showStmt.setInt(6, duration);

                    showStmt.executeUpdate();
                }
    
                JOptionPane.showMessageDialog(null, "Show added successfully!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numbers for seats, price, and hall.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding show: " + e.getMessage());
        }
    }

    private void listMovies() {
        StringBuilder movieList = new StringBuilder("Available Movies:\n");
        String query = "SELECT title, genre, duration FROM movies";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String title = rs.getString("title");
                String genre = rs.getString("genre");
                int duration = rs.getInt("duration");
                movieList.append(String.format("Title: %s, Genre: %s, Duration: %d mins\n", title, genre, duration));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading movies: " + e.getMessage());
        }
        JOptionPane.showMessageDialog(null, movieList.toString());
    }

    private void listShows() {
        StringBuilder showList = new StringBuilder("Available Shows:\n");
        String query = "SELECT s.id, m.title, s.show_time, s.available_seats, s.ticket_price, s.hall_number " +
                       "FROM shows s JOIN movies m ON s.movie_id = m.id";
        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String title = rs.getString("title");
                String showTime = rs.getString("show_time");
                int availableSeats = rs.getInt("available_seats");
                double ticketPrice = rs.getDouble("ticket_price");
                int hallNumber = rs.getInt("hall_number");
                showList.append(String.format("Movie: %s, Time: %s, Seats: %d, Price: $%.2f, Hall: %d\n",
                        title, showTime, availableSeats, ticketPrice, hallNumber));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading shows: " + e.getMessage());
        }
        JOptionPane.showMessageDialog(null, showList.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminGUI("admin@gmail.com"));
    }
}