package service;

import dao.UserDAO;
import model.User;
import util.PasswordUtil;

import java.time.LocalDate;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public User getProfile(int userId) {
        return userDAO.findProfileById(userId);
    }

    public String updateProfile(User user) {
        if (user.getPhone() != null && user.getPhone().length() > 20) {
            return "Phone must be 20 characters or fewer.";
        }

        if (user.getAvatarUrl() != null && user.getAvatarUrl().length() > 500) {
            return "Avatar URL must be 500 characters or fewer.";
        }

        if (user.getDateOfBirth() != null
                && user.getDateOfBirth().toLocalDate().isAfter(LocalDate.now())) {
            return "Date of birth cannot be in the future.";
        }

        boolean updated = userDAO.updateProfile(user);
        return updated ? null : "Update profile failed.";
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
