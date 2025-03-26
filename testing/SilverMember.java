package testing;

import java.util.ArrayList;

public class SilverMember extends Customer {
    public SilverMember(String email, String password, double balance, ArrayList<Show> shows) {
        super(email, password, balance, shows);
    }

    @Override
    public void bookTicket() {
        super.bookTicket(); // Call the original bookTicket method

        // Apply 15% discount
        if (!bookedTickets.isEmpty()) {
            Ticket lastBookedTicket = bookedTickets.get(bookedTickets.size() - 1);
            double totalCost = lastBookedTicket.getNumberOfTickets() * lastBookedTicket.getShow().getTicketPrice();
            double discount = totalCost * 0.15;
            balance += discount; // Refund the discount amount to the balance
            System.out.println("As a Silver Member, you received a 15% discount of $" + discount);
        }
    }

    @Override
    public String getMembership() {
        return "Silver";
    }

    @Override
    public String toString() {
        return "Silver Member";
    }
}