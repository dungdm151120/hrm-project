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

@WebServlet("/admin/users/add")
public class AddUserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/admin/add_user.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String fullName = req.getParameter("fullName");
            String email = req.getParameter("email");
            String password = req.getParameter("password");
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

            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setPhone(phone);
            newUser.setGender(gender);
            newUser.setDateOfBirth(dateOfBirth);
            newUser.setAddress(address);
            newUser.setAvatarUrl(avatarUrl);
            newUser.setRoleId(roleId);
            newUser.setActive(active);

            UserDAO dao = new UserDAO();
            boolean isSuccess = dao.addUser(newUser);

            if (isSuccess) {
                resp.sendRedirect(req.getContextPath() + "/admin/users");
            } else {
                req.setAttribute("error", "Add new user failed in database!");
                req.setAttribute("newUser", newUser);
                req.getRequestDispatcher("/WEB-INF/views/admin/add_user.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Invalid data input format! " + e.getMessage());
            doGet(req, resp);
        }
    }
}
