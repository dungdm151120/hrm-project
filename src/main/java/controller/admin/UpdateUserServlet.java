package controller.admin;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/users/update")
public class UpdateUserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }

        int userId = Integer.parseInt(idParam);
        UserDAO dao = new UserDAO();
        User user = dao.findById(userId);

        req.setAttribute("userToUpdate", user);
        req.getRequestDispatcher("/WEB-INF/views/admin/update_user.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            String fullName = req.getParameter("fullName");
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");
            String gender = req.getParameter("gender");
            String address = req.getParameter("address");
            String avatarUrl = req.getParameter("avatarUrl");
            int roleId = Integer.parseInt(req.getParameter("roleId"));
            boolean active = Boolean.parseBoolean(req.getParameter("active"));

            String dobParam = req.getParameter("dateOfBirth");
            LocalDateTime dateOfBirth = null;
            if (dobParam != null && !dobParam.trim().isEmpty()) {
                dateOfBirth = java.time.LocalDate.parse(dobParam).atStartOfDay();
            }

            User updatedUser = new User();
            updatedUser.setId(id);
            updatedUser.setFullName(fullName);
            updatedUser.setEmail(email);
            updatedUser.setPhone(phone);
            updatedUser.setGender(gender);
            updatedUser.setDateOfBirth(dateOfBirth);
            updatedUser.setAddress(address);
            updatedUser.setAvatarUrl(avatarUrl);
            updatedUser.setRoleId(roleId);
            updatedUser.setActive(active);

            UserDAO dao = new UserDAO();
            boolean isSuccess = dao.updateUser(updatedUser);

            if (isSuccess) {
                resp.sendRedirect(req.getContextPath() + "/admin/users");
            } else {
                req.setAttribute("error", "Update failed in database!");
                req.setAttribute("userToUpdate", updatedUser);
                req.getRequestDispatcher("/WEB-INF/views/admin/update_user.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Invalid data input format! " + e.getMessage());
            doGet(req, resp);
        }
    }
}
