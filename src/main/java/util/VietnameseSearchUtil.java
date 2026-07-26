package util;

import java.text.Normalizer;
import java.util.Locale;

public final class VietnameseSearchUtil {
    private VietnameseSearchUtil() {
    }

    public static boolean matchesPerson(String fullName, String email, String employeeCode, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }

        String emailQuery = query.trim().toLowerCase(Locale.ROOT);
        if (containsIgnoreCase(email, emailQuery) || containsIgnoreCase(employeeCode, emailQuery)) {
            return true;
        }

        boolean queryHasToneMarks = hasToneMarks(query);
        String normalizedName = queryHasToneMarks
                ? normalizePreservingToneMarks(fullName)
                : normalize(fullName);
        String normalizedQuery = queryHasToneMarks
                ? normalizePreservingToneMarks(query)
                : normalize(query);
        if (normalizedName.isEmpty() || normalizedQuery.isEmpty()) {
            return false;
        }

        String[] nameTokens = normalizedName.split(" ");
        for (String queryToken : normalizedQuery.split(" ")) {
            boolean tokenMatched = false;
            for (String nameToken : nameTokens) {
                if (nameToken.startsWith(queryToken)) {
                    tokenMatched = true;
                    break;
                }
            }
            if (!tokenMatched) {
                return false;
            }
        }
        return true;
    }

    public static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("\\s+", " ");
    }

    private static String normalizePreservingToneMarks(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return Normalizer.normalize(value, Normalizer.Form.NFC)
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("\\s+", " ");
    }

    private static boolean hasToneMarks(String value) {
        return value != null
                && Normalizer.normalize(value, Normalizer.Form.NFD).matches(".*\\p{M}.*");
    }

    private static boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }
}
