package testing;

import java.util.ArrayList;



public class GoldMember extends Customer {
    public GoldMember(String email, String password, double balance, ArrayList<Show> shows) {
        super(email, password, balance, shows);
    }

    @Override
    public void bookTicket() {
        super.bookTicket(); // Call the original bookTicket method

        // Apply 20% discount
        if (!bookedTickets.isEmpty()) {
            Ticket lastBookedTicket = bookedTickets.get(bookedTickets.size() - 1);
            double totalCost = lastBookedTicket.getNumberOfTickets() * lastBookedTicket.getShow().getTicketPrice();
            double discount = totalCost * 0.20;
            balance += discount; // Refund the discount amount to the balance
            System.out.println("As a Gold Member, you received a 20% discount of $" + discount);
        }
    }

    @Override
    public String getMembership() {
        return "Gold";
    }

    @Override
    public String toString() {
        return "Gold Member";
    }
}