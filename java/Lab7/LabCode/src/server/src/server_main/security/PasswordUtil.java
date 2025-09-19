package server_main.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class PasswordUtil {
    private PasswordUtil() {
    }

    public static String sha224(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-224");
            byte[] hash = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("SHA-224 not available", e);
        }
    }
}
