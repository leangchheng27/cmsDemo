package testing;

import java.sql.*;
import java.util.*;

public class MySQLConnection {

    private static Connection connection = null;
    private static final String URL = "jdbc:mysql://localhost:3306/cinema";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "@Mryb012345";

    // Establish the connection
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {  // Check if closed
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Connected to MySQL successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
    

    // Register function (Admins & Customers)
    public static void registerUser(List<Customer> customers, ArrayList<Show> shows) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Select registration type:");
            System.out.println("1. Admin");
            System.out.println("2. Customer");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter email: ");
            String email = scanner.nextLine();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            System.out.print("Confirm password: ");
            String confirmPassword = scanner.nextLine();

            if (!password.equals(confirmPassword)) {
                System.out.println("Passwords do not match! Registration failed.");
                return;
            }

            String insertSQL = (choice == 1)
                ? "INSERT INTO admins (email, password) VALUES (?, ?)"
                : "INSERT INTO customers (email, password, balance) VALUES (?, ?, 0.0)";

            try (PreparedStatement pstmt = getConnection().prepareStatement(insertSQL)) {
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                pstmt.executeUpdate();
                System.out.println((choice == 1 ? "Admin" : "Customer") + " registered successfully!");

                if (choice == 2) {
                    customers.clear();
                    customers.addAll(initializeCustomers(shows));
                }
            } catch (SQLException e) {
                System.out.println("Registration failed: " + e.getMessage());
            }
        }
    }

    // Retrieve customers from MySQL
    private static List<Customer> initializeCustomers(ArrayList<Show> shows) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT email, password, balance FROM customers";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                customers.add(new Customer(
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getDouble("balance"),
                    shows
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving customers from database: " + e.getMessage());
        }
        return customers;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Movie> movies = new ArrayList<>();
        ArrayList<Show> shows = new ArrayList<>();
        List<Admin> admins = initializeAdmins(movies, shows);
        List<Customer> customers = initializeCustomers(shows);

        while (true) {
            try {
                System.out.println("\n1. Admin Login");
                System.out.println("2. Customer Login");
                System.out.println("3. Register");
                System.out.println("4. Exit");
                System.out.print("Enter choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        // Admin Login
                        System.out.print("Enter email: ");
                        String adminEmail = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String adminPassword = scanner.nextLine();

                        boolean adminFound = false;
                        for (Admin admin : admins) {
                            if (admin.getEmail().equals(adminEmail) && admin.getPassword().equals(adminPassword)) {
                                System.out.println("Admin login successful.");
                                admin.menu(); // Admin menu
                                adminFound = true;
                                break;
                            }
                        }
                        if (!adminFound) {
                            System.out.println("Incorrect email or password for admin.");
                        }
                        break;

                    case 2:
                        // Customer Login
                        System.out.print("Enter email: ");
                        String customerEmail = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String customerPassword = scanner.nextLine();

                        boolean customerFound = false;
                        for (Customer customer : customers) {
                            if (customer.getEmail().equals(customerEmail) && customer.getPassword().equals(customerPassword)) {
                                System.out.println("Customer login successful.");
                                customer.menu(); // Customer menu
                                customerFound = true;
                                break;
                            }
                        }
                        if (!customerFound) {
                            System.out.println("Incorrect email or password for customer.");
                        }
                        break;

                    case 3:
                        // Register
                        registerUser(customers, shows);
                        break;

                        case 4:
                        System.out.println("Exiting...");
                        try {
                            if (connection != null && !connection.isClosed()) {
                                connection.close();  // Close connection properly
                                System.out.println("Database connection closed.");
                            }
                        } catch (SQLException e) {
                            System.out.println("Error closing connection: " + e.getMessage());
                        }
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice, try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume the newline character
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private static List<Admin> initializeAdmins(ArrayList<Movie> movies, ArrayList<Show> shows) {
        List<Admin> admins = new ArrayList<>();
        admins.add(new Admin("chheng@gmail.com", "12345", movies, shows));
        admins.add(new Admin("hak@gmail.com", "12345", movies, shows));
        admins.add(new Admin("nak@gmail.com", "12345", movies, shows));
        admins.add(new Admin("hong@gmail.com", "12345", movies, shows));
        return admins;
    }
}