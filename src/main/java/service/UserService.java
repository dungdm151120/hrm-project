package service;

import dao.UserDAO;
import model.User;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public User getProfile(int userId) {
        return userDAO.findById(userId);
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

        boolean oldPasswordCorrect = userDAO.checkOldPassword(userId, oldPassword);

        if (!oldPasswordCorrect) {
            return "Mật khẩu cũ không đúng";
        }

        boolean updated = userDAO.updatePassword(userId, newPassword);

        if (!updated) {
            return "Đổi mật khẩu thất bại";
        }

        return null;
    }
}