package testing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Customer extends Account implements Authentication {

    protected Scanner scanner = new Scanner(System.in);
    protected double balance;
    protected ArrayList<Show> shows;
    protected ArrayList<Ticket> bookedTickets;
    protected double totalPurchases;
    private int id;


    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Customer(String email, String password, double balance, ArrayList<Show> shows) {
        super(email, password);
        this.balance = balance;
        this.shows = shows;
        this.bookedTickets = new ArrayList<>();
        this.totalPurchases = 0;
    }

    public Customer(int id, String email, String password, double balance, String role) {
        super(email, password, role);
        this.id = id; // Set the id field
        this.balance = balance;
        this.shows = new ArrayList<>();
        this.bookedTickets = new ArrayList<>();
        this.totalPurchases = 0;
    }

    @Override
    public boolean register() {
        try {
            System.out.print("Enter email: ");
            String email = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            System.out.print("Confirm password: ");
            String confirmPassword = scanner.nextLine();

            if (!password.equals(confirmPassword)) {
                System.out.println("Passwords do not match.");
                return false;
            }

            // Simulate registration logic
            System.out.println("Customer registered successfully with email: " + email);
            return true;
        } catch (Exception e) {
            System.out.println("An error occurred during registration: " + e.getMessage());
            return false;
        }
    }

    public void menu() {
        while (true) {
            try {
                System.out.println("1. View Shows");
                System.out.println("2. Book Ticket");
                System.out.println("3. View Balance");
                System.out.println("4. Show My Booking");
                System.out.println("5. Top Up");
                System.out.println("6. Logout");
                System.out.print("Enter choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();
    
                switch (choice) {
                    case 1:
                        viewShows();
                        break;
                    case 2:
                        bookTicket();
                        break;
                    case 3:
                        System.out.println("Your balance: $" + balance);
                        break;
                    case 4:
                        showMyBooking();
                        break;
                    case 5:
                        topUp();
                        break;
                    case 6:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    void viewShows() {
        if (shows.isEmpty()) {
            System.out.println("No shows available.");
            return;
        }
        for (int i = 0; i < shows.size(); i++) {
            System.out.println(shows.get(i));
        }
    }

    public void bookTicket() {
        try {
            System.out.print("Enter movie title: ");
            String title = scanner.nextLine().trim();

            boolean showFound = false;
            for (int i = 0; i < shows.size(); i++) {
                Show s = shows.get(i);
                if (s.getMovie() != null && s.getMovie().getTitle().equalsIgnoreCase(title)) {
                    showFound = true;
                    System.out.print("Enter number of tickets: ");
                    int tickets = scanner.nextInt();
                    scanner.nextLine();

                    if (tickets <= 0) {
                        System.out.println("Invalid number of tickets.");
                        return;
                    }

                    double totalCost = tickets * s.getTicketPrice();
                    if (tickets <= s.getAvailableSeats() && totalCost <= balance) {
                        s.bookSeats(tickets);
                        balance -= totalCost;
                        totalPurchases += totalCost;
                        Ticket ticket = new Ticket(s, tickets);
                        bookedTickets.add(ticket);
                        System.out.println("\n\nBooking successful!");

                        upgradeMembership();
                        ticket.printInvoice(this);

                    } else {
                        if (tickets > s.getAvailableSeats()) {
                            System.out.println("Not enough seats available.");
                        } else {
                            System.out.println("Not enough balance.");
                        }
                    }
                    return;
                }
            }

            if (!showFound) {
                throw new NoSuchElementException("Show not found.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public void showMyBooking() {
        if (bookedTickets.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        System.out.println("\nYour Bookings:");
        for (Ticket ticket : bookedTickets) {
            System.out.println("Movie: " + ticket.getShow().getMovie().getTitle() + ", Tickets: " + ticket.getNumberOfTickets() + ", Cost: $" + (ticket.getNumberOfTickets() * ticket.getShow().getTicketPrice()));
        }
    }

    public void topUp() {
        try {
            System.out.println("Choose top-up method:");
            System.out.println("1. Cash");
            System.out.println("2. Credit Card");
            System.out.print("Enter choice: ");
            int method = scanner.nextInt();
            scanner.nextLine();

            if (method != 1 && method != 2) {
                System.out.println("Invalid choice.");
                return;
            }

            System.out.print("Enter amount to top up: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();

            if (amount <= 0) {
                System.out.println("Invalid amount.");
                return;
            }

            if (method == 1) {
                System.out.println("Cash payment received.");
            } else {
                System.out.println("Credit Card payment processed successfully.");
            }

            balance += amount;
            System.out.println("Top-up successful! New balance: $" + balance);

            try (PrintWriter writer = new PrintWriter(new FileWriter("FIle_IO/topup_records.txt", true))) {
                writer.println("Account " + this.getEmail() + " has topped up $" + amount);
            }

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public String getMembership() {
        if (totalPurchases > 300) {
            return "Gold Member";
        } else if (totalPurchases > 200) {
            return "Silver Member";
        } else if (totalPurchases > 100) {
            return "Bronze Member";
        } else {
            return "No Membership";
        }
    }

    public void upgradeMembership() {
        if (totalPurchases > 300) {
            System.out.println("You have been upgraded to Gold Member.");
        } else if (totalPurchases > 200) {
            System.out.println("You have been upgraded to Silver Member.");
        } else if (totalPurchases > 100) {
            System.out.println("You have been upgraded to Bronze Member.");
        }
    }

    public double calculateDiscount() {
        if (totalPurchases >= 200) {
            return 0.20;
        } else if (totalPurchases >= 150) {
            return 0.15;
        } else if (totalPurchases >= 100) {
            return 0.10;
        } else {
            return 0.0;
        }
    }

    public double getBalance() {
        return this.balance;
    }

    public int getId() {
        return this.id;
    }

    public void readInvoices() {
        try (BufferedReader reader = new BufferedReader(new FileReader("invoices.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading invoices: " + e.getMessage());
        }
    }

    @Override
    public boolean login() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }
}