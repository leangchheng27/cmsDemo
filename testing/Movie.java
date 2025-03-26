package testing;

public class Movie {
    private String title;
    private String genre;
    private int duration; 
    private boolean isAvailable;

    // Full constructor
    public Movie(String title, String genre, int duration, boolean isAvailable) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.isAvailable = isAvailable;
    }

    // Constructor with only title
    public Movie(String title) {
        this.title = title;
        this.genre = ""; // Default value
        this.duration = 0; // Default value
        this.isAvailable = true; // Default value
    }

    // Another constructor
    public Movie(int movieId, String movieTitle, String genre2, int duration2, String imagePath) {
        // TODO Auto-generated constructor stub
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailability(boolean availability) {
        this.isAvailable = availability;
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Genre: " + genre + ", Duration: " + duration + " mins";
    }
}