package attendance.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Salted SHA-256 password hashing.
 * Stored format: sha256$<salt>$<hash>  (salt and hash are lowercase hex).
 */
public final class PasswordHasher {

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordHasher() {
    }

    public static String hash(String password) {
        String salt = randomSalt();
        return "sha256$" + salt + "$" + sha256Hex(salt + password);
    }

    public static boolean matches(String password, String stored) {
        if (password == null || stored == null) {
            return false;
        }
        if (stored.startsWith("sha256$")) {
            String[] parts = stored.split("\\$", -1);
            if (parts.length != 3) {
                return false;
            }
            String candidate = sha256Hex(parts[1] + password);
            return constantTimeEquals(candidate, parts[2]);
        }
        // Legacy rows may still hold plain text.
        return stored.equals(password);
    }

    private static String randomSalt() {
        byte[] salt = new byte[8];
        RANDOM.nextBytes(salt);
        StringBuilder sb = new StringBuilder();
        for (byte b : salt) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            diff |= a.charAt(i) ^ b.charAt(i);
        }
        return diff == 0;
    }
}
