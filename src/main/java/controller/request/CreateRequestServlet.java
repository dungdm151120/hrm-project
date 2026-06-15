package controller.request;

import dao.RequestDAO;
import dao.UserDAO;
import model.Request;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.*;

@WebServlet("/create_request")
public class CreateRequestServlet extends HttpServlet {
    private final RequestDAO requestDAO = new RequestDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int deptId = user.getDepartmentId();
        String position = user.getPositionName();

        List<User> deptEmployees = userDAO.getAllEmployeesByDepartment(deptId);
        List<User> deptEmployeesFiltered = new ArrayList<>();
        for (User emp : deptEmployees) {
            if (emp.getId() != user.getId()) {
                deptEmployeesFiltered.add(emp);
            }
        }

        Map<String, String> allTypes = Request.getAllType();
        Map<String, String> filteredTypes = new LinkedHashMap<>();

        for (var entry : allTypes.entrySet()) {
            if ("POSITION_HANDOVER".equals(entry.getKey())) {
                boolean isManager = (position != null && position.contains("Manager"));
                boolean isSysAdmin = "System Administrator".equals(position);

                if (isManager || isSysAdmin) {
                    filteredTypes.put(entry.getKey(), entry.getValue());
                }
            } else {
                filteredTypes.put(entry.getKey(), entry.getValue());
            }
        }

        List<User> sysAdmins = userDAO.getUserByPosition("System Administrator");
        List<User> hrManagers = userDAO.getUserByPosition("HR Manager");
        List<User> payrollManagers = userDAO.getUserByPosition("Payroll Manager");
        List<User> deptManagers = userDAO.getAllDeptManager();

        List<User> allObservers = new ArrayList<>();
        if (sysAdmins != null) allObservers.addAll(sysAdmins);
        if (hrManagers != null) allObservers.addAll(hrManagers);
        if (payrollManagers != null) allObservers.addAll(payrollManagers);
        if (deptManagers != null) allObservers.addAll(deptManagers);

        request.setAttribute("deptEmployees", deptEmployeesFiltered);
        request.setAttribute("allObservers", allObservers);
        request.setAttribute("requestType", filteredTypes);
        request.setAttribute("businessAdminList", userDAO.getUserByRole("BUSINESS ADMIN"));

        request.getRequestDispatcher("WEB-INF/views/request/create_request.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String type = request.getParameter("type");
        String[] observerIds = request.getParameterValues("observerIds");

        List<Integer> obsIds = new ArrayList<>();
        if (observerIds != null) {
            for (String id : observerIds) {
                if (id != null && !id.isEmpty()) {
                    obsIds.add(Integer.parseInt(id));
                }
            }
        }

        try {
            Request req = new Request();
            req.setUserId(userId);
            req.setDepartmentId((Integer) session.getAttribute("departmentId"));
            req.setType(request.getParameter("type"));
            req.setReason(request.getParameter("reason"));
            req.setApproverId(Integer.parseInt(request.getParameter("approverId")));

            requestDAO.createRequest(req, obsIds);
            response.sendRedirect("view_my_request");
        } catch (Exception e) {
            session.setAttribute("error", "Lỗi: " + e.getMessage());
            response.sendRedirect("create_request");
        }
    }
}