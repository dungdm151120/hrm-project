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

        if (password == null || password.isEmpty()) {
            return null;
        }

        String normalizedEmail = email.trim().toLowerCase();
        User user = userDAO.findActiveUserWithPositionByEmail(normalizedEmail);

        if (user == null || !PasswordUtil.verifyPassword(password, user.getPassword())) {
            return null;
        }

        if (PasswordUtil.needsHashUpgrade(user.getPassword())) {
            userDAO.updatePassword(user.getId(), PasswordUtil.hashPassword(password));
        }

        return user;
    }
}
