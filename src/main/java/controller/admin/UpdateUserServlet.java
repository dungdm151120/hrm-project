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
import java.time.LocalDateTime;
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

        int userId = Integer.parseInt(idParam);
        UserDAO dao = new UserDAO();
        User user = dao.findById(userId);

        RoleDAO daoR = new RoleDAO();
        List<Role> roles = daoR.getAllRoles();

        req.setAttribute("roles", roles);
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
            String roleIdParam = req.getParameter("roleId");
            int roleId = 0;

            if (roleIdParam != null && !roleIdParam.trim().isEmpty()) {
                roleId = Integer.parseInt(roleIdParam);
            } else {
                UserDAO dao = new UserDAO();
                User existingUser = dao.findById(id);
                if (existingUser != null) {
                    roleId = existingUser.getRoleId();
                }
            }

            boolean active = Boolean.parseBoolean(req.getParameter("active"));
            UserDAO dao = new UserDAO();
            User existingUser = dao.findById(id);

            if (existingUser == null) {
                resp.sendRedirect(req.getContextPath() + "/user_list");
                return;
            }

            if (existingUser.isActive() && !active) {
                LaborContractDAO contractDAO = new LaborContractDAO();
                if (!contractDAO.canDeactivateUser(id)) {
                    req.setAttribute("error", "User can only be deactivated when their contract is expired or terminated.");
                    req.setAttribute("userToUpdate", existingUser);
                    req.setAttribute("roles", new RoleDAO().getAllRoles());
                    req.getRequestDispatcher("/WEB-INF/views/admin/update_user.jsp").forward(req, resp);
                    return;
                }
            }

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

            boolean isSuccess = dao.updateUser(updatedUser);

            if (isSuccess) {
                resp.sendRedirect(req.getContextPath() + "/user_list");
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
