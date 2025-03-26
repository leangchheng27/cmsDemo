package testing;

import java.security.MessageDigest;

public class Account {
    private String email;
    private String password;
    private String role;

    public Account(String email, String password) throws IllegalArgumentException {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 5 characters long.");
        }
        setEmail(email);
        setPassword(password);
    }

    public Account(String email) throws IllegalArgumentException {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        setEmail(email);
        this.password = null; // Default value for password
    }

    public Account(String email, String password, String role) throws IllegalArgumentException {
        this(email, password);
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 5;
    }

    public boolean login(String email, String password) {
        if (email.equals(this.email) && password.equals(this.password)) {
            System.out.println("Login successful");
            return true;
        } else {
            System.out.println("Incorrect email or password.");
            return false;
        }
    }

    public boolean register(String email, String password, String confirmPassword) {
        if (!isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return false;
        }
        if (!isValidPassword(password)) {
            System.out.println("Password must be at least 5 characters long.");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return false;
        }
        setEmail(email);
        setPassword(password);
        System.out.println("Customer registered successfully");
        return true;
    }

    protected void setEmail(String email) {
        this.email = email;
    }

    protected void setPassword(String password) {
        this.password = hashPassword(password);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}