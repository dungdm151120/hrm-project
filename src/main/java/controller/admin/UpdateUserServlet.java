package controller.admin;

import dao.LaborContractDAO;
import dao.RoleDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Role;
import model.User;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet("/users/update")
public class UpdateUserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/user_list");
            return;
        }
        try {
            int userId = Integer.parseInt(idParam);
            UserDAO dao = new UserDAO();
            User user = dao.findById(userId);

            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/user_list");
                return;
            }

            RoleDAO daoR = new RoleDAO();
            List<Role> roles = daoR.getAllRoles();

            req.setAttribute("roles", roles);
            req.setAttribute("userToUpdate", user);
            req.getRequestDispatcher("/WEB-INF/views/admin/update_user.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/user_list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User updatedUser = new User();

        try {
            int id = Integer.parseInt(req.getParameter("id"));
            updatedUser.setId(id);

            UserDAO dao = new UserDAO();
            User existingUser = dao.findById(id);

            if (existingUser == null) {
                resp.sendRedirect(req.getContextPath() + "/user_list");
                return;
            }

            String fullName = req.getParameter("fullName") != null ? req.getParameter("fullName").trim() : "";
            String email = req.getParameter("email") != null ? req.getParameter("email").trim() : "";
            String phone = req.getParameter("phone") != null ? req.getParameter("phone").trim() : "";
            String gender = req.getParameter("gender");
            String address = req.getParameter("address") != null ? req.getParameter("address").trim() : "";
            String avatarUrl = req.getParameter("avatarUrl") != null ? req.getParameter("avatarUrl").trim() : "";

            int roleId = 0;
            String roleIdParam = req.getParameter("roleId");
            if (roleIdParam != null && !roleIdParam.trim().isEmpty()) {
                roleId = Integer.parseInt(roleIdParam);
            } else {
                roleId = existingUser.getRoleId();
            }

            boolean active = Boolean.parseBoolean(req.getParameter("active"));

            updatedUser.setFullName(fullName);
            updatedUser.setEmail(email);
            updatedUser.setPhone(phone);
            updatedUser.setGender(gender);
            updatedUser.setAddress(address);
            updatedUser.setAvatarUrl(avatarUrl);
            updatedUser.setRoleId(roleId);
            updatedUser.setActive(active);

            StringBuilder errorMsg = new StringBuilder();

            if (fullName.isEmpty() || fullName.length() < 2 || fullName.length() > 100) {
                errorMsg.append("Full Name must be between 2 and 100 characters.<br/>");
            }

            if (email.isEmpty() || email.length() > 100 || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                errorMsg.append("Invalid email format or email length exceeds 100 characters.<br/>");
            } else if (dao.isEmailExists(email)) {
                errorMsg.append("Email is already registered in the system.<br/>");
            }

            if (!phone.isEmpty()) {
                if (!phone.matches("\\d{9,11}")) {
                    errorMsg.append("Phone number must contain only digits and be between 9 and 11 numbers.<br/>");
                } else if (dao.isPhoneExistsForUpdate(phone, id)) {
                    errorMsg.append("Phone number is already registered in the system.<br/>");
                }
            }

            String dobParam = req.getParameter("dateOfBirth");
            LocalDateTime dateOfBirth = null;
            if (dobParam != null && !dobParam.trim().isEmpty()) {
                try {
                    LocalDate dob = LocalDate.parse(dobParam);
                    if (dob.isAfter(LocalDate.now())) {
                        errorMsg.append("Date of Birth cannot be a future date.<br/>");
                    } else {
                        dateOfBirth = dob.atStartOfDay();
                        updatedUser.setDateOfBirth(dateOfBirth);
                    }
                } catch (DateTimeParseException e) {
                    errorMsg.append("Invalid Date of Birth format.<br/>");
                }
            }

            if (address.length() > 255) errorMsg.append("Address cannot exceed 255 characters.<br/>");
            if (avatarUrl.length() > 255) errorMsg.append("Avatar URL cannot exceed 255 characters.<br/>");

            if (existingUser.isActive() && !active) {
                LaborContractDAO contractDAO = new LaborContractDAO();
                if (!contractDAO.canDeactivateUser(id)) {
                    errorMsg.append("User cannot be deactivated while they still have an active contract.<br/>");
                }
            }

            if (errorMsg.length() > 0) {
                sendErrorBack(req, resp, errorMsg.toString(), updatedUser);
                return;
            }

            boolean isSuccess = dao.updateUser(updatedUser);

            if (isSuccess) {
                resp.sendRedirect(req.getContextPath() + "/user_list");
            } else {
                sendErrorBack(req, resp, "Update user failed in database execution!", updatedUser);
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendErrorBack(req, resp, "Invalid data input format!", updatedUser);
        }
    }

    private void sendErrorBack(HttpServletRequest req, HttpServletResponse resp, String errorMessage, User userToUpdate)
            throws ServletException, IOException {
        RoleDAO daoR = new RoleDAO();
        req.setAttribute("roles", daoR.getAllRoles());
        req.setAttribute("error", errorMessage);
        req.setAttribute("userToUpdate", userToUpdate);
        req.getRequestDispatcher("/WEB-INF/views/admin/update_user.jsp").forward(req, resp);
    }
}
