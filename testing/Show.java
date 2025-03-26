package testing;

public class Show {
    private int id;
    private String showTime;
    private int availableSeats;
    private int totalSeats; // New field to track original capacity
    private double ticketPrice;
    private int hallNumber;
    private Movie movie;

    // Constructor with Movie object
    // Existing constructor
    public Show(Movie movie, String showTime, int availableSeats, double ticketPrice, int hallNumber, String date) {
        this.id = 0; // Default ID, or generate it dynamically if needed
        this.movie = movie;
        this.showTime = showTime;
        this.availableSeats = availableSeats;
        this.totalSeats = availableSeats; // Initialize totalSeats
        this.ticketPrice = ticketPrice;
        this.hallNumber = hallNumber;
    }

    // New constructor for TicketGUI
    public Show(int id, Movie movie, double ticketPrice) {
        this.id = id;
        this.movie = movie;
        this.ticketPrice = ticketPrice;
        // Initialize other fields with default values
        this.showTime = ""; // Default value
        this.availableSeats = 0; // Default value
        this.totalSeats = 0; // Default value
        this.hallNumber = 0; // Default value
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getShowTime() {
        return showTime;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public int getHallNumber() {
        return hallNumber;
    }

    public Movie getMovie() {
        return movie;
    }

    // Setters
    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public void setHallNumber(int hallNumber) {
        this.hallNumber = hallNumber;
    }

    // Book seats
    public boolean bookSeats(int seats) {
        if (seats > 0 && seats <= availableSeats) {
            availableSeats -= seats;
            return true;
        }
        return false;
    }

    // Cancel seats
    private void cancelSeats(int seats) {
        if (seats > 0 && availableSeats + seats <= totalSeats) { // Ensure it doesn't exceed totalSeats
            availableSeats += seats;
        }
    }

    // toString method
    @Override
    public String toString() {
        return "Movie: " + movie.getTitle() + 
               " | Show Time: " + showTime + 
               " | Seats: " + availableSeats + 
               " | Price: $" + ticketPrice + 
               " | Hall: " + hallNumber;
    }
}