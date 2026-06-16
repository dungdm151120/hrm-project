package controller.payroll;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Payroll;
import model.User;
import model.Role; // Import lớp Role của bạn
import dao.PayrollDAO;
import dao.UserDAO;
import dao.RoleDAO; // Khởi tạo RoleDAO riêng của bạn

import java.io.IOException;
import java.util.List;

@WebServlet({"/payroll/list", "/payroll/detail", "/payroll/confirm"})
public class PayrollListServlet extends HttpServlet {

    private final PayrollDAO payrollDAO = new PayrollDAO();
    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO(); // Khai báo RoleDAO

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Lấy thông tin Role Name từ DB dựa trên roleId của User hiện tại
        Role userRole = roleDAO.getRoleById(currentUser.getRoleId());
        String roleName = (userRole != null) ? userRole.getName() : "EMPLOYEE";

        // Đẩy thẳng tên Role sang JSP để check điều kiện ẩn hiện nút bấm
        request.setAttribute("currentUserRole", roleName);

        // ---- 1. VIEW PAYROLL DETAIL ----
        if ("/payroll/detail".equals(servletPath)) {
            int payrollId = Integer.parseInt(request.getParameter("id"));
            Payroll payroll = payrollDAO.findById(payrollId);

            // Nếu bảng lương không tồn tại trong DB, quay về danh sách và báo lỗi
            if (payroll == null) {
                request.setAttribute("error", "Error: Payroll record not found.");
                // Lưu ý: Dùng forward thay vì sendRedirect để hiển thị được thanh báo lỗi ${error} trên giao diện list
                request.getRequestDispatcher("/payroll/list").forward(request, response);
                return;
            }

            User employeeInfo = userDAO.findById(payroll.getUserId());

            // Kiểm tra an toàn bảo vệ giao diện nếu không tìm thấy User tương ứng trong Database
            if (employeeInfo == null) {
                employeeInfo = new User();
                employeeInfo.setId(payroll.getUserId());
                employeeInfo.setFullName("Unknown Employee");
                employeeInfo.setDepartmentName("N/A");
                employeeInfo.setPositionName("N/A");
            }

            request.setAttribute("payroll", payroll);
            request.setAttribute("employee", employeeInfo);
            request.getRequestDispatcher("/WEB-INF/views/payroll/payroll_detail.jsp").forward(request, response);
            return;
        }

        // ---- 2. CONFIRM PAYROLL ACTIONS (SINGLE OR ALL) ----
        if ("/payroll/confirm".equals(servletPath)) {
            String type = request.getParameter("type");

            if ("single".equals(type)) {
                int payrollId = Integer.parseInt(request.getParameter("id"));
                payrollDAO.updateStatus(payrollId, "CONFIRMED");
                response.sendRedirect(request.getContextPath() + "/payroll/detail?id=" + payrollId);
            } else if ("all".equals(type)) {
                int m = Integer.parseInt(request.getParameter("month"));
                int y = Integer.parseInt(request.getParameter("year"));

                List<Payroll> all = payrollDAO.findAllPayrolls();
                for (Payroll p : all) {
                    if (p.getMonth() == m && p.getYear() == y && "DRAFT".equals(p.getStatus())) {
                        payrollDAO.updateStatus(p.getId(), "CONFIRMED");
                    }
                }
                response.sendRedirect(request.getContextPath() + "/payroll/list");
            }
            return;
        }

        // ---- 3. VIEW PAYROLL LIST (DEFAULT WITH PAGING) ----
        int currentPage = 1;
        int pageSize = 10; // Đặt cố định 10 bản ghi một trang theo yêu cầu của bạn

        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException ignored) {}
        }

// Đếm tổng số dòng dữ liệu để tính số lượng trang
        int totalRows = payrollDAO.countPayrolls(roleName, currentUser.getId());
        int totalPages = (int) Math.ceil((double) totalRows / pageSize);

        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }

        int offset = (currentPage - 1) * pageSize;

        // Lấy danh sách phân trang phối hợp đầy đủ thông tin Tên, Phòng ban, Chức vụ
        List<Payroll> payrollList = payrollDAO.findPayrollsWithPaging(roleName, currentUser.getId(), offset, pageSize);

        // Đẩy dữ liệu tính toán phân trang sang trang JSP hiển thị
        request.setAttribute("payrollList", payrollList);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPage", totalPages);

        request.getRequestDispatcher("/WEB-INF/views/payroll/payroll_list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}