package controller.request;

import dao.RequestDAO;
import dao.UserDAO;
import model.Request;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;

import java.io.IOException;
import java.util.*;

@WebServlet("/create_request")
public class CreateRequestServlet extends HttpServlet {
    private final RequestDAO requestDAO = new RequestDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("currentUser");

        // Lấy chức vụ chính xác của user từ DB
        String position = userDAO.getPositionNameByUserId(user.getId());
        boolean isManager = (position != null && position.contains("Manager"));
        boolean isSysAdmin = (position != null && position.contains("Admin"));

        // Lấy toàn bộ danh sách loại request từ Model
        Map<String, String> allTypes = Request.getAllType();
        Map<String, String> filteredTypes = new LinkedHashMap<>();

        for (var entry : allTypes.entrySet()) {
            if ("POSITION_HANDOVER".equals(entry.getKey())) {
                if (isManager || isSysAdmin) {
                    filteredTypes.put(entry.getKey(), entry.getValue());
                }
            } else {
                filteredTypes.put(entry.getKey(), entry.getValue());
            }
        }

        // Truyền danh sách đã lọc xuống trang JSP chính
        request.setAttribute("requestTypes", filteredTypes);

        // --- Các logic chuẩn bị dữ liệu bên dưới giữ nguyên ---
        int deptId = (user.getDepartmentId() != null) ? user.getDepartmentId() : 0;
        request.setAttribute("deptEmployees", userDAO.getAllEmployeesByDepartment(deptId));
        request.setAttribute("businessAdminList", userDAO.getUserByRole("BUSINESS ADMIN"));
        // ...
        request.getRequestDispatcher("WEB-INF/views/request/create_request.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");

        try {
            Request req = new Request();
            req.setUserId(currentUser.getId());
            req.setDepartmentId(currentUser.getDepartmentId());

            req.setType(request.getParameter("type"));
            req.setReason(request.getParameter("reason"));

            // Kiểm tra an toàn cho trường approverId
            String approverIdParam = request.getParameter("approverId");
            if (approverIdParam == null || approverIdParam.trim().isEmpty()) {
                response.sendRedirect("create_request?error=missing_approver");
                return;
            }
            req.setApproverId(Integer.parseInt(approverIdParam));

            // Kiểm tra an toàn cho trường handlerId
            String handlerIdParam = request.getParameter("handlerId");
            if (handlerIdParam != null && !handlerIdParam.trim().isEmpty()) {
                req.setHandlerId(Integer.parseInt(handlerIdParam));
            }

            // Xử lý mảng Observers
            String[] observerIds = request.getParameterValues("observerIds");
            Set<Integer> uniqueObsIds = new LinkedHashSet<>();
            if (observerIds != null) {
                for (String id : observerIds) {
                    if (id != null && !id.trim().isEmpty()) {
                        uniqueObsIds.add(Integer.parseInt(id));
                    }
                }
            }

            requestDAO.createRequest(req, new ArrayList<>(uniqueObsIds));
            response.sendRedirect("view_my_request?success=true");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("create_request?error=system_error");
        }
    }
}