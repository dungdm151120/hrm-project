package controller.request;

import dao.RequestDAO;
import dao.UserDAO;
import model.Request;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/create_request")
public class CreateRequestServlet extends HttpServlet {
    private final RequestDAO requestDAO = new RequestDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int currentUserId = (int) session.getAttribute("userId");

        List<User> businessAdminList = userDAO.getBusinessAdminsByRole("Business Admin");
        User deptManager = userDAO.getDeptManagerByEmployeeId(currentUserId);

        request.setAttribute("requestType", model.Request.getAllType());
        request.setAttribute("businessAdminList", businessAdminList);
        request.setAttribute("deptManager", deptManager);

        request.getRequestDispatcher("WEB-INF/views/request/create_request.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            Integer userId = (Integer) session.getAttribute("userId");
            Integer deptId = (Integer) session.getAttribute("departmentId");

            Request req = new Request();
            req.setUserId(userId);
            req.setDepartmentId(deptId);
            req.setType(request.getParameter("type"));
            req.setReason(request.getParameter("reason"));
            req.setApproverId(Integer.parseInt(request.getParameter("approverId")));

            String obsIdStr = request.getParameter("observerId");
            if (obsIdStr != null && !obsIdStr.isEmpty()) {
                req.setObserverId(Integer.parseInt(obsIdStr));
            }

            requestDAO.createRequest(req);

            session.setAttribute("message", "Request created successfully.");
            response.sendRedirect("view_my_request");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Failed to create request: " + e.getMessage());
            response.sendRedirect("create_request");
        }
    }
}