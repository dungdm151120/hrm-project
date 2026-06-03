package service;

import dao.UserDAO;
import model.User;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            return null;
        }

        return userDAO.findByEmailAndPassword(email.trim(), password.trim());
    }
}