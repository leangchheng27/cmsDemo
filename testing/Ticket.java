package testing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;



public class Ticket {
    private Show show;
    private int numberOfTickets;

    public Ticket(Show show, int numberOfTickets) {
        this.show = show;
        this.numberOfTickets = numberOfTickets;
    }

    public Show getShow() {
        return show;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    public void setNumberOfTickets(int numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    public void printInvoice(Customer customer) {
        double discount = customer.calculateDiscount();
        double totalCost = this.getNumberOfTickets() * this.getShow().getTicketPrice();
        double discountedCost = totalCost * (1 - discount);

        // Print invoice to console
        System.out.println("****************************");
        System.out.println("********* INVOICE **********");
        System.out.println("****************************");
        System.out.println("Movie: " + this.getShow().getMovie().getTitle());
        System.out.println("Number of Tickets: " + this.getNumberOfTickets());
        System.out.println("Total Cost: $" + totalCost);
        System.out.println("Discount: " + (discount * 100) + "%");
        System.out.println("Discounted Cost: $" + discountedCost);
        System.out.println("Membership: " + customer.getMembership());
        System.out.println("****************************\n");

        // Write invoice to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("File_IO/invoices.txt", true))) {
            writer.write("****************************\n");
            writer.write("********* INVOICE **********\n");
            writer.write("****************************\n");
            writer.write("Customer: " + customer.getEmail() + "\n");
            writer.write("Movie: " + this.getShow().getMovie().getTitle() + "\n");
            writer.write("Number of Tickets: " + this.getNumberOfTickets() + "\n");
            writer.write("Total Cost: $" + totalCost + "\n");
            writer.write("Discount: " + (discount * 100) + "%\n");
            writer.write("Discounted Cost: $" + discountedCost + "\n");
            writer.write("Membership: " + customer.getMembership() + "\n");
            writer.write("****************************\n\n");
        } catch (IOException e) {
            System.out.println("Error writing invoice: " + e.getMessage());
        }
    }
}
