package testing;

import java.util.ArrayList;



public class BronzeMember extends Customer {

    public BronzeMember(String email, String password, double balance, ArrayList<Show> shows) {
        super(email, password, balance, shows);
    }

    @Override
    public void bookTicket() {
        super.bookTicket(); // Call the original bookTicket method

        // Apply 10% discount
        if (!bookedTickets.isEmpty()) {
            Ticket lastBookedTicket = bookedTickets.get(bookedTickets.size() - 1);
            double totalCost = lastBookedTicket.getNumberOfTickets() * lastBookedTicket.getShow().getTicketPrice();
            double discount = totalCost * 0.10;
            balance += discount; // Refund the discount amount to the balance
            System.out.println("As a Bronze Member, you received a 10% discount of $" + discount);
        }
    }

    @Override
    public String getMembership() {
        return "Bronze";
    }

    @Override
    public String toString() {
        return "Bronze Member";
    }
}