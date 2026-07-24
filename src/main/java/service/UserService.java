package service;

import dao.UserDAO;
import model.User;
import util.PasswordUtil;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

public class UserService {
    private static final int MAX_PASSWORD_LENGTH = 72;
    private static final Set<String> VALID_GENDERS = Set.of("Male", "Female", "Other");

    private final UserDAO userDAO = new UserDAO();

    public User getProfile(int userId) {
        return userDAO.findProfileById(userId);
    }

    public String updateProfile(User user) {
        if (user.getPhone() != null) {
            if (user.getPhone().length() > 20) {
                return "Phone must be 20 characters or fewer.";
            }
            if (!user.getPhone().matches("^[+]?[0-9][0-9 .()\\-]{7,19}$")) {
                return "Phone format is invalid.";
            }
        }

        if (user.getGender() != null && !VALID_GENDERS.contains(user.getGender())) {
            return "Gender is invalid.";
        }

        if (user.getAddress() != null && user.getAddress().length() > 255) {
            return "Address must be 255 characters or fewer.";
        }

        if (user.getAvatarUrl() != null) {
            if (user.getAvatarUrl().length() > 255) {
                return "Avatar URL must be 255 characters or fewer.";
            }
            if (!isValidAvatarUrl(user.getAvatarUrl())) {
                return "Avatar URL must be a valid HTTP or HTTPS URL.";
            }
        }

        if (user.getDateOfBirth() != null
                && user.getDateOfBirth().toLocalDate().isAfter(LocalDate.now())) {
            return "Date of birth cannot be in the future.";
        }

        boolean updated = userDAO.updateProfile(user);
        return updated ? null : "Update profile failed.";
    }

    private boolean isValidAvatarUrl(String value) {
        try {
            URI uri = URI.create(value);
            String scheme = uri.getScheme();
            return uri.getHost() != null
                    && ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String changePassword(int userId, String oldPassword, String newPassword, String confirmPassword) {
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            return "Vui lòng nhập mật khẩu cũ";
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return "Vui lòng nhập mật khẩu mới";
        }

        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            return "Vui lòng xác nhận mật khẩu mới";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "Mật khẩu mới và xác nhận mật khẩu không khớp";
        }

        if (oldPassword.length() > MAX_PASSWORD_LENGTH
                || newPassword.length() > MAX_PASSWORD_LENGTH
                || confirmPassword.length() > MAX_PASSWORD_LENGTH) {
            return "Mật khẩu không được vượt quá 72 ký tự";
        }

        if (newPassword.length() < 6) {
            return "Mật khẩu mới phải có ít nhất 6 ký tự";
        }

        User user = userDAO.findById(userId);
        if (user == null || !PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
            return "Mật khẩu cũ không đúng";
        }

        boolean updated = userDAO.updatePassword(userId, PasswordUtil.hashPassword(newPassword));

        if (!updated) {
            return "Đổi mật khẩu thất bại";
        }

        return null;
    }
}
