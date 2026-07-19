package controller.admin;

import dao.RoleDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Role;
import model.User;
import util.PasswordUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/admin/users/add")
public class AddUserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        RoleDAO dao = new RoleDAO();
        List<Role> roles = dao.getAllRoles();
        if (roles != null) {
            roles.removeIf(r -> "SYSTEM ADMIN".equalsIgnoreCase(r.getName())
                    || "BUSINESS ADMIN".equalsIgnoreCase(r.getName()));
        }
        req.setAttribute("roles", roles);
        req.getRequestDispatcher("/WEB-INF/views/admin/add_user.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User newUser = new User();
        try {
            String fullName = req.getParameter("fullName") != null ? req.getParameter("fullName").trim() : "";
            String email = req.getParameter("email") != null ? req.getParameter("email").trim() : "";
            String password = req.getParameter("password");
            String phone = req.getParameter("phone") != null ? req.getParameter("phone").trim() : "";
            String gender = req.getParameter("gender");
            String address = req.getParameter("address") != null ? req.getParameter("address").trim() : "";
            String avatarUrl = req.getParameter("avatarUrl") != null ? req.getParameter("avatarUrl").trim() : "";
            int roleId = 0;
            if (req.getParameter("roleId") != null && !req.getParameter("roleId").isEmpty()) {
                roleId = Integer.parseInt(req.getParameter("roleId"));
            }
            boolean active = Boolean.parseBoolean(req.getParameter("active"));
            String dobParam = req.getParameter("dateOfBirth");

            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setGender(gender);
            newUser.setAddress(address);
            newUser.setAvatarUrl(avatarUrl);
            newUser.setRoleId(roleId);
            newUser.setActive(active);

            StringBuilder errorMsg = new StringBuilder();

            if (fullName.isEmpty() || fullName.length() < 2 || fullName.length() > 100) {
                errorMsg.append("Full Name must be between 2 and 100 characters.<br/>");
            }

            if (email.isEmpty() || email.length() > 100 || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                errorMsg.append("Invalid email format or email length exceeds 100 characters.<br/>");
            }

            if (password == null || password.length() < 6 || password.length() > 32) {
                errorMsg.append("Password must be between 6 and 32 characters.<br/>");
            }

            if (!phone.isEmpty()) {
                if (!phone.matches("\\d{9,11}")) {
                    errorMsg.append("Phone number must contain only digits and be between 9 and 11 numbers.<br/>");
                }
            }

            LocalDateTime dateOfBirth = null;
            if (dobParam != null && !dobParam.trim().isEmpty()) {
                LocalDate dob = LocalDate.parse(dobParam);
                if (dob.isAfter(LocalDate.now())) {
                    errorMsg.append("Date of Birth cannot be a future date.<br/>");
                } else {
                    dateOfBirth = dob.atStartOfDay();
                    newUser.setDateOfBirth(dateOfBirth);
                }
            }

            if (address.length() > 255) errorMsg.append("Address cannot exceed 255 characters.<br/>");
            if (avatarUrl.length() > 255) errorMsg.append("Avatar URL cannot exceed 255 characters.<br/>");

            if (errorMsg.length() > 0) {
                sendErrorBack(req, resp, errorMsg.toString(), newUser);
                return;
            }

            newUser.setPassword(PasswordUtil.hashPassword(password));

            UserDAO dao = new UserDAO();
            boolean isSuccess = dao.addUser(newUser);

            if (isSuccess) {
                resp.sendRedirect(req.getContextPath() + "/user_list");
            } else {
                sendErrorBack(req, resp, "Add new user failed in database execution!", newUser);
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorBack(req, resp, "Invalid data input format! " + e.getMessage(), newUser);
        }
    }

    private void sendErrorBack(HttpServletRequest req, HttpServletResponse resp, String errorMessage, User newUser)
            throws ServletException, IOException {
        RoleDAO dao = new RoleDAO();
        List<Role> roles = dao.getAllRoles();
        if (roles != null) {
            roles.removeIf(r -> "SYSTEM ADMIN".equalsIgnoreCase(r.getName())
                    || "BUSINESS ADMIN".equalsIgnoreCase(r.getName()));
        }
        req.setAttribute("roles", roles);
        req.setAttribute("error", errorMessage);
        req.setAttribute("newUser", newUser);
        req.getRequestDispatcher("/WEB-INF/views/admin/add_user.jsp").forward(req, resp);
    }
}
