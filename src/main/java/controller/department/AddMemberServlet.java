package controller.department;

import dao.DepartmentDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AddMemberServlet", value = "/add_member")
public class AddMemberServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    // Hiển thị dsach các emp chưa có dept
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int deptId = Integer.parseInt(request.getParameter("deptId"));

        // Nếu phòng ban inactive, đá về trang danh sách kèm lỗi
        if (!departmentDAO.isDepartmentActive(deptId)) {
            response.sendRedirect(request.getContextPath() + "/admin/departments?error=Inactive department!");
            return;
        }

        request.setAttribute("deptId", deptId);
        request.setAttribute("unassignedUsers", userDAO.getUnassignedUsers());
        request.getRequestDispatcher("/WEB-INF/views/department/add_member.jsp").forward(request, response);
    }

    // Xử lý add
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int deptId = Integer.parseInt(request.getParameter("deptId"));

        // Nếu phòng ban inactive, đá về trang danh sách kèm lỗi
        if (!departmentDAO.isDepartmentActive(deptId)) {
            response.sendRedirect(request.getContextPath() + "/admin/departments?error=Inactive department!");
            return;
        }

        String[] userIdStrings = request.getParameterValues("userIds");
        if (userIdStrings != null && userIdStrings.length > 0) {
            int[] userIds = new int[userIdStrings.length];
            for (int i = 0; i < userIdStrings.length; i++) {
                userIds[i] = Integer.parseInt(userIdStrings[i]);
            }

            if (userDAO.addMembersToDept2(userIds, deptId)) {
                response.sendRedirect(request.getContextPath() + "/admin/departments/employees?id=" + deptId + "&msg=add_success");
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/departments/employees?id=" + deptId + "&error=add_failed");
    }
}