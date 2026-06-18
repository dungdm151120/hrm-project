package controller.department;

import dao.DepartmentDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Department;
import model.OrganizationNode;
import model.User;
import util.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@WebServlet("/admin/company-structure")
public class CompanyStructureServlet extends HttpServlet {

    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        @SuppressWarnings("unchecked")
        Set<String> permissions = (Set<String>) session.getAttribute("userPermissions");
        if (permissions == null || !permissions.contains("DEPARTMENT_VIEW_LIST")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        OrganizationNode root = new OrganizationNode(0, "HRM", "company");

        // Gán avatar và title của Business Admin cho node gốc
        List<User> businessAdmins = userDAO.getUserByRole("BUSINESS ADMIN");
        if (!businessAdmins.isEmpty()) {
            User businessAdmin = businessAdmins.get(0);
            root.setAvatarUrl(getAvatarUrl(businessAdmin.getId()));
            root.setTitle(businessAdmin.getFullName());
        } else {
            root.setTitle("Our Company");
        }

        // Duyệt các phòng ban
        List<Department> departments = departmentDAO.getAllDepartments().stream()
                .filter(Department::isActive)
                .toList();

        for (Department dept : departments) {
            OrganizationNode deptNode = new OrganizationNode(dept.getId(), dept.getName(), "department");

            if (dept.getManagerUserId() != null) {
                User manager = userDAO.findById(dept.getManagerUserId());
                if (manager != null) {
                    deptNode.setTitle("Manager: " + manager.getFullName());
                    deptNode.setAvatarUrl(getAvatarUrl(manager.getId()));
                }
            }

            List<User> members = userDAO.findByDepartmentId(dept.getId());
            List<User> activeMembers = new ArrayList<>();
            for (User m : members) {
                if (m.isActive() && (dept.getManagerUserId() == null || m.getId() != dept.getManagerUserId())) {
                    activeMembers.add(m);
                }
            }

            for (User member : activeMembers) {
                OrganizationNode memberNode = new OrganizationNode(member.getId(), member.getFullName(), "member");
                memberNode.setTitle(member.getPositionName());
                memberNode.setAvatarUrl(getAvatarUrl(member.getId()));
                deptNode.addChild(memberNode);
            }

            root.addChild(deptNode);
        }

        request.setAttribute("orgRoot", root);
        request.getRequestDispatcher("/WEB-INF/views/department/company_structure.jsp").forward(request, response);
    }

    private String getAvatarUrl(int userId) {
        String sql = "SELECT avatar_url FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("avatar_url");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
