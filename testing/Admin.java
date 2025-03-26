package testing;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Admin extends Account implements Authentication {

    private Scanner scanner = new Scanner(System.in);
    private ArrayList<Movie> movies;
    private ArrayList<Show> shows;

    private static final int ADD_MOVIE = 1;
    private static final int LIST_SHOWS = 2;
    private static final int LOG_OUT = 3;

    public Admin(String email, String password, ArrayList<Movie> movies, ArrayList<Show> shows) {
        super(email, password);
        this.movies = (movies != null) ? movies : new ArrayList<>();
        this.shows = (shows != null) ? shows : new ArrayList<>();
    }

    @Override
    public boolean login() {
        System.out.println("Admin login is not implemented yet.");
        return false; // Placeholder return value
    }

    @Override
    public boolean register() {
        System.out.println("Admin registration is not supported.");
        return false; // Placeholder return value
    }

    void addMovie() {
        try {
            System.out.print("Enter movie title: ");
            String title = scanner.nextLine();
            System.out.print("Enter genre: ");
            String genre = scanner.nextLine();
            if (!isValidGenre(genre)) {
                System.out.println("Invalid genre. Please enter a valid genre.");
                return;
            }
            System.out.print("Enter duration (minutes): ");
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a valid number for duration.");
                scanner.nextLine();
                return;
            }
            int duration = scanner.nextInt();
            scanner.nextLine();

            Movie newMovie = new Movie(title, genre, duration, true);
            movies.add(newMovie);
            System.out.println("Movie added successfully!");

            System.out.print("Enter show time (HH:MM): ");
            String time = scanner.nextLine();
            if (!isValidTime(time)) {
                System.out.println("Invalid show time. Please enter a valid time in HH:MM format.");
                return;
            }
            System.out.print("Enter available seats: ");
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a valid number for seats.");
                scanner.nextLine();
                return;
            }
            int seats = scanner.nextInt();
            System.out.print("Enter ticket price ($): ");
            if (!scanner.hasNextDouble()) {
                System.out.println("Invalid input. Please enter a valid number for ticket price.");
                scanner.nextLine();
                return;
            }
            double price = scanner.nextDouble();
            System.out.print("Enter hall number: ");
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a valid number for hall number.");
                scanner.nextLine();
                return;
            }
            int hallNumber = scanner.nextInt();
            scanner.nextLine();

            shows.add(new Show(newMovie, time, seats, price, hallNumber, LocalDate.now().toString()));
            System.out.println("Show added successfully!");

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter valid numbers for duration, seats, price, and hall number.");
            scanner.nextLine(); // Consume the invalid input
        }
    }
    private boolean isValidGenre(String genre) {
        return genre != null && genre.matches("^[a-zA-Z0-9\\s\\-/]+$");
    }

    void listMovies() {
        if (movies.isEmpty()) {
            System.out.println("No movies available.");
            return;
        }
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }

    private boolean isValidTime(String time) {
        if (time == null || !time.matches("^[0-2][0-9]:[0-5][0-9]$")) {
            return false;
        }
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours >= 0 && hours < 24 && minutes >= 0 && minutes < 60;
    }

    void listShows() {
        if (shows.isEmpty()) {
            System.out.println("No shows available.");
            return;
        }
        for (Show show : shows) {
            System.out.println(show);
        }
    }

    public void menu() {
        while (true) {
            try {
                System.out.println(ADD_MOVIE + ". Add Movie");
                System.out.println(LIST_SHOWS + ". List Shows");
                System.out.println(LOG_OUT + ". Log out");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case ADD_MOVIE:
                        addMovie();
                        break;
                    case LIST_SHOWS:
                        listShows();
                        break;
                    case LOG_OUT:
                        System.out.println("Logging out");
                        return;
                    default:
                        System.out.println("Invalid choice, try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume the invalid input
            }
        }
    }

    public boolean isAdmin() {
        return true;
    }
}