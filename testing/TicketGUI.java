package testing;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketGUI {
    private JFrame frame;
    private Customer customer;

    public TicketGUI(Customer customer) {
        this.customer = customer;
        frame = new JFrame("Your Tickets");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("Your Tickets", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(headerLabel, BorderLayout.NORTH);

        // Ticket List Panel
        JPanel ticketPanel = new JPanel();
        ticketPanel.setLayout(new BoxLayout(ticketPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(ticketPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Fetch and Display Tickets
        List<Ticket> tickets = fetchTicketsForCustomer(customer.getId());
        if (tickets.isEmpty()) {
            JLabel noTicketsLabel = new JLabel("No tickets found.", SwingConstants.CENTER);
            noTicketsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            ticketPanel.add(noTicketsLabel);
        } else {
            for (Ticket ticket : tickets) {
                JPanel ticketCard = createTicketCard(ticket);
                ticketPanel.add(ticketCard);
            }
        }

        frame.setVisible(true);
    }

    private JPanel createTicketCard(Ticket ticket) {
        JPanel card = new JPanel();
        card.setLayout(new GridLayout(6, 1));
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        card.setBackground(Color.LIGHT_GRAY);

        JLabel movieLabel = new JLabel("Movie: " + ticket.getShow().getMovie().getTitle());
        JLabel ticketsLabel = new JLabel("Number of Tickets: " + ticket.getNumberOfTickets());
        JLabel totalCostLabel = new JLabel("Total Cost: $" + ticket.getNumberOfTickets() * ticket.getShow().getTicketPrice());
        JLabel discountLabel = new JLabel("Discount: " + (customer.calculateDiscount() * 100) + "%");
        JLabel discountedCostLabel = new JLabel("Discounted Cost: $" + (ticket.getNumberOfTickets() * ticket.getShow().getTicketPrice() * (1 - customer.calculateDiscount())));

        JButton viewInvoiceButton = new JButton("View Invoice");
        viewInvoiceButton.addActionListener(e -> viewInvoice(ticket));

        card.add(movieLabel);
        card.add(ticketsLabel);
        card.add(totalCostLabel);
        card.add(discountLabel);
        card.add(discountedCostLabel);
        card.add(viewInvoiceButton);

        return card;
    }

    private void viewInvoice(Ticket ticket) {
        double discount = customer.calculateDiscount();
        double totalCost = ticket.getNumberOfTickets() * ticket.getShow().getTicketPrice();
        double discountedCost = totalCost * (1 - discount);

        // Display invoice in a dialog box
        String invoice = "****************************\n" +
                         "********* INVOICE **********\n" +
                         "****************************\n" +
                         "Customer: " + customer.getEmail() + "\n" +
                         "Movie: " + ticket.getShow().getMovie().getTitle() + "\n" +
                         "Number of Tickets: " + ticket.getNumberOfTickets() + "\n" +
                         "Total Cost: $" + totalCost + "\n" +
                         "Discount: " + (discount * 100) + "%\n" +
                         "Discounted Cost: $" + discountedCost + "\n" +
                         "Membership: " + customer.getMembership() + "\n" +
                         "****************************\n";

        JOptionPane.showMessageDialog(frame, invoice, "Invoice", JOptionPane.INFORMATION_MESSAGE);

        // Write invoice to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("File_IO/invoices.txt", true))) {
            writer.write(invoice);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error writing invoice to file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Ticket> fetchTicketsForCustomer(int customerId) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT t.id, t.number_of_tickets, s.id AS show_id, s.ticket_price, m.title " +
                     "FROM tickets t " +
                     "JOIN shows s ON t.show_id = s.id " +
                     "JOIN movies m ON s.movie_id = m.id " +
                     "WHERE t.user_id = ?";
        try (PreparedStatement pstmt = MySQLConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Show show = new Show(rs.getInt("show_id"), new Movie(rs.getString("title")), rs.getDouble("ticket_price"));
                Ticket ticket = new Ticket(show, rs.getInt("number_of_tickets"));
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error fetching tickets: " + e.getMessage());
        }
        return tickets;
    }
}