package service;

import dao.UserDAO;
import model.User;
import util.PasswordUtil;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            return null;
        }

        String trimmedPassword = password.trim();
        User user = userDAO.findActiveUserByEmail(email.trim());

        if (user == null || !PasswordUtil.verifyPassword(trimmedPassword, user.getPassword())) {
            return null;
        }

        if (PasswordUtil.needsHashUpgrade(user.getPassword())) {
            userDAO.updatePassword(user.getId(), PasswordUtil.hashPassword(trimmedPassword));
        }

        return user;
    }
}
