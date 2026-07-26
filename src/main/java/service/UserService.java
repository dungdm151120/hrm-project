package service;

import dao.UserDAO;
import model.User;
import util.PasswordUtil;

import java.net.URI;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class UserService {
    private static final int MAX_PASSWORD_LENGTH = 72;
    private static final Set<String> VALID_GENDERS = Set.of("Male", "Female", "Other");

    private final UserDAO userDAO = new UserDAO();

    public User getProfile(int userId) {
        return userDAO.findProfileById(userId);
    }

    public Map<String, String> updateProfile(User user) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (user.getPhone() == null || !user.getPhone().matches("^\\d{10,15}$")) {
            errors.put("phone", "Phone number must be 10-15 digits.");
        } else if (userDAO.isPhoneUsedByAnotherUser(user.getPhone(), user.getId())) {
            errors.put("phone", "Phone number is already in use.");
        }

        if (user.getGender() == null || !VALID_GENDERS.contains(user.getGender())) {
            errors.put("gender", "Please select a valid gender.");
        }

        if (user.getDateOfBirth() == null) {
            errors.put("dateOfBirth", "Date of birth is required.");
        } else {
            LocalDate dateOfBirth = user.getDateOfBirth().toLocalDate();
            LocalDate today = LocalDate.now();
            if (!dateOfBirth.isBefore(today)) {
                errors.put("dateOfBirth", "Date of birth must be in the past.");
            } else if (dateOfBirth.plusYears(18).isAfter(today)) {
                errors.put("dateOfBirth", "You must be at least 18 years old.");
            }
        }

        if (user.getAddress() != null && user.getAddress().length() > 255) {
            errors.put("address", "Address must be 255 characters or fewer.");
        }

        if (user.getAvatarUrl() != null) {
            if (user.getAvatarUrl().length() > 1000) {
                errors.put("avatarUrl", "Avatar URL must be 1000 characters or fewer.");
            } else if (isNewAvatarUrl(user) && !isValidAvatarUrl(user.getAvatarUrl())) {
                errors.put("avatarUrl", "Avatar URL must be a valid JPG, JPEG, PNG, or WEBP image URL.");
            }
        }

        if (!errors.isEmpty()) {
            return errors;
        }

        if (!userDAO.updateProfile(user)) {
            errors.put("global", "Update profile failed.");
        }
        return errors;
    }

    public Map<String, String> changePassword(int userId, String oldPassword, String newPassword,
                                               String confirmPassword) {
        Map<String, String> errors = new LinkedHashMap<>();

        validateOldPassword(oldPassword, errors);
        validateNewPassword(newPassword, errors);
        validateConfirmation(newPassword, confirmPassword, errors);

        if (!errors.containsKey("oldPassword") && !isCurrentPassword(userId, oldPassword)) {
            errors.put("oldPassword", "Current password is incorrect.");
        }

        if (!errors.isEmpty()) {
            return errors;
        }

        if (!userDAO.updatePassword(userId, PasswordUtil.hashPassword(newPassword))) {
            errors.put("global", "Password could not be changed. Please try again.");
        }
        return errors;
    }

    private boolean isNewAvatarUrl(User user) {
        User currentProfile = userDAO.findProfileById(user.getId());
        return currentProfile == null || !user.getAvatarUrl().equals(currentProfile.getAvatarUrl());
    }

    private boolean isValidAvatarUrl(String value) {
        try {
            URI uri = URI.create(value);
            String scheme = uri.getScheme();
            String path = uri.getPath();
            if (uri.getHost() == null
                    || !("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                    || path == null) {
                return false;
            }

            String lowerPath = path.toLowerCase(Locale.ROOT);
            return lowerPath.endsWith(".jpg")
                    || lowerPath.endsWith(".jpeg")
                    || lowerPath.endsWith(".png")
                    || lowerPath.endsWith(".webp");
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void validateOldPassword(String oldPassword, Map<String, String> errors) {
        if (oldPassword == null || oldPassword.isEmpty()) {
            errors.put("oldPassword", "Current password is required.");
        } else if (oldPassword.length() > MAX_PASSWORD_LENGTH) {
            errors.put("oldPassword", "Current password cannot exceed 72 characters.");
        }
    }

    private void validateNewPassword(String newPassword, Map<String, String> errors) {
        if (newPassword == null || newPassword.isEmpty()) {
            errors.put("newPassword", "New password is required.");
        } else if (newPassword.length() < 6) {
            errors.put("newPassword", "New password must be at least 6 characters.");
        } else if (newPassword.length() > MAX_PASSWORD_LENGTH) {
            errors.put("newPassword", "New password cannot exceed 72 characters.");
        }
    }

    private void validateConfirmation(String newPassword, String confirmPassword, Map<String, String> errors) {
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            errors.put("confirmPassword", "Password confirmation is required.");
        } else if (confirmPassword.length() > MAX_PASSWORD_LENGTH) {
            errors.put("confirmPassword", "Password confirmation cannot exceed 72 characters.");
        } else if (newPassword != null && !newPassword.equals(confirmPassword)) {
            errors.put("confirmPassword", "Password confirmation does not match.");
        }
    }

    private boolean isCurrentPassword(int userId, String oldPassword) {
        User user = userDAO.findById(userId);
        return user != null && PasswordUtil.verifyPassword(oldPassword, user.getPassword());
    }
}
