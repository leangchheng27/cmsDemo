package testing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class CinemaApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Movie> movies = new ArrayList<>();
        ArrayList<Show> shows = new ArrayList<>();
        List<Admin> admins = initializeAdmins(movies, shows);
        List<Customer> customers = initializeCustomers(shows);

        while (true) {
        try {
            System.out.println("1. Admin Login");
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
                    System.out.println("1. Admin Register");
                    System.out.println("2. Customer Register");
                    System.out.print("Enter choice: ");
                    int registerChoice = scanner.nextInt();
                    scanner.nextLine();
                
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    System.out.print("Confirm password: ");
                    String confirmPassword = scanner.nextLine();
                
                    if (password.equals(confirmPassword)) {
                        if (registerChoice == 1) {
                            Admin newAdmin = new Admin(email, password, movies, shows);
                            admins.add(newAdmin);
                            saveAccount(newAdmin, "File_IO/accounts.txt");  // Save to file
                            System.out.println("Admin registered successfully.");
                        } else if (registerChoice == 2) {
                            Customer newCustomer = new Customer(email, password, 0.0, shows);
                            customers.add(newCustomer);
                            saveAccount(newCustomer, "File_IO/accounts.txt");  // Save to file
                            System.out.println("Customer registered successfully.");
                        } else {
                            System.out.println("Invalid choice for registration.");
                        }
                    } else {
                        System.out.println("Passwords do not match.");
                    }
                    break;                
                case 4:
                    // Exit
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
            
                default:
                    // Invalid choice
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

    public static void saveAccount(Account account, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            if (account instanceof Admin) {
                Admin admin = (Admin) account;
                writer.write("ADMIN," + admin.getEmail() + "," + admin.getPassword() + "\n");
            } else if (account instanceof Customer) {
                Customer customer = (Customer) account;
                writer.write("CUSTOMER," + customer.getEmail() + "," + customer.getPassword() + "," + customer.balance + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving account: " + e.getMessage());
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

    private static List<Customer> initializeCustomers(ArrayList<Show> shows) {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("hello@gmail.com", "12345", 1000.0, shows));
        customers.add(new Customer("john@gmail.com", "56789", 200.0, shows));
        customers.add(new Customer("emily@gmail.com", "98765", 150.0, shows));
        customers.add(new Customer("david@gmail.com", "abcd1234", 300.0, shows));
        return customers;
    }

    public Object selectMovie(int movieIndex) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selectMovie'");
    }

    public ArrayList<Movie> getMovies() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMovies'");
    }

    public ArrayList<Show> getShows() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getShows'");
    }
}