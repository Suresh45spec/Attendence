package attendance.service;

import java.io.IOException;
import java.util.ArrayList;

import attendance.model.User;

/**
 * Handles registration, login and a simple forgot-password reset.
 */
public class AuthService {

    private final FileStorage storage;
    private ArrayList<User> users;

    public AuthService(FileStorage storage) {
        this.storage = storage;
        this.users = new ArrayList<User>();
        loadUsers();
        createDemoAccountsIfEmpty();
    }

    private void loadUsers() {
        try {
            users = storage.loadUsers();
        } catch (IOException e) {
            users = new ArrayList<User>();
        }
    }

    private void createDemoAccountsIfEmpty() {
        if (!users.isEmpty()) {
            return;
        }
        users.add(new User("teacher", "teacher123", User.ROLE_TEACHER, "Demo Teacher", "teacher@college.edu"));
        users.add(new User("101", "student123", User.ROLE_STUDENT, "Aarav Sharma", "aarav@college.edu"));
        saveUsersQuietly();
    }

    private void saveUsersQuietly() {
        try {
            storage.saveUsers(users);
        } catch (IOException e) {
            System.out.println("Could not save users: " + e.getMessage());
        }
    }

    public User login(String username, String password, String role) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)
                    && user.getPassword().equals(password)
                    && user.getRole().equalsIgnoreCase(role)) {
                return user;
            }
        }
        return null;
    }

    public String register(String fullName, String email, String username, String password, String confirmPassword, String role) {
        if (isBlank(fullName) || isBlank(email) || isBlank(username) || isBlank(password) || isBlank(role)) {
            return "Please fill all fields.";
        }
        if (!email.contains("@") || !email.contains(".")) {
            return "Please enter a valid email ID.";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters.";
        }
        if (!password.equals(confirmPassword)) {
            return "Password and Confirm Password do not match.";
        }
        if (findByUsername(username) != null) {
            return "This username / roll number is already registered.";
        }

        users.add(new User(username.trim(), password, role, fullName.trim(), email.trim()));
        try {
            storage.saveUsers(users);
        } catch (IOException e) {
            return "Could not save account: " + e.getMessage();
        }
        return null;
    }

    public String resetPassword(String username, String email, String newPassword, String confirmPassword) {
        User user = findByUsername(username);
        if (user == null) {
            return "No account found for this username / roll number.";
        }
        if (!user.getEmail().equalsIgnoreCase(email.trim())) {
            return "Email ID does not match this account.";
        }
        if (newPassword.length() < 6) {
            return "Password must be at least 6 characters.";
        }
        if (!newPassword.equals(confirmPassword)) {
            return "Password and Confirm Password do not match.";
        }
        user.setPassword(newPassword);
        try {
            storage.saveUsers(users);
        } catch (IOException e) {
            return "Could not update password: " + e.getMessage();
        }
        return null;
    }

    public User findByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
