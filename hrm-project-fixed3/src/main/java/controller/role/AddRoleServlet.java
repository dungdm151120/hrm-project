package controller.role;

import dao.RoleDAO;
import model.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/roles/add")
public class AddRoleServlet extends HttpServlet {

    private final RoleDAO roleDAO = new RoleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hiển thị form thêm role
        request.getRequestDispatcher("/WEB-INF/views/role/add_role.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // Lấy dữ liệu từ form
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String activeParam = request.getParameter("active");
        boolean active = activeParam != null && activeParam.equals("true");

        // Validate
        StringBuilder errors = new StringBuilder();
        if (name == null || name.trim().isEmpty()) {
            errors.append("Tên role không được để trống.<br/>");
        }
        if (description == null || description.trim().isEmpty()) {
            errors.append("Mô tả không được để trống.<br/>");
        }

        if (errors.length() > 0) {
            request.setAttribute("error", errors.toString());
            request.setAttribute("name", name);
            request.setAttribute("description", description);
            request.setAttribute("active", active);
            doGet(request, response); // quay lại form
            return;
        }

        // Tạo đối tượng Role
        Role newRole = new Role(0, name.trim(), description.trim(), active);

        // Gọi DAO để thêm
        int newId = roleDAO.addRole(newRole);

        if (newId != -1) {
            // Thành công -> redirect về danh sách kèm thông báo
            HttpSession session = request.getSession();
            session.setAttribute("successMessage", "Thêm role thành công (ID: " + newId + ")");
            response.sendRedirect(request.getContextPath() + "/admin/roles");
        } else {
            // Thất bại (có thể trùng tên, lỗi DB...)
            request.setAttribute("error", "Không thể thêm role. Tên role có thể đã tồn tại hoặc lỗi hệ thống.");
            request.setAttribute("name", name);
            request.setAttribute("description", description);
            request.setAttribute("active", active);
            doGet(request, response);
        }
    }
}