package controller.department;

import dao.DepartmentDAO;
import dao.PositionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Department;
import model.Position;

import java.io.IOException;

@WebServlet("/admin/departments/unassign-manager")
public class UnassignManagerServlet extends HttpServlet {

    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final PositionDAO positionDAO = new PositionDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String departmentIdParam = request.getParameter("departmentId");
        String userIdParam = request.getParameter("userId");

        int departmentId;
        int managerUserId;
        try {
            departmentId = Integer.parseInt(departmentIdParam);
            managerUserId = Integer.parseInt(userIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/departments?error=invalid_data");
            return;
        }

        String employeeListUrl = request.getContextPath() + "/admin/departments/employees?id=" + departmentId;

        Department department = departmentDAO.getDepartmentById(departmentId);
        if (department == null) {
            response.sendRedirect(request.getContextPath() + "/admin/departments?error=department_not_found");
            return;
        }

        Integer currentManagerId = department.getManagerUserId();
        if (currentManagerId == null) {
            response.sendRedirect(employeeListUrl + "&error=no_manager_assigned");
            return;
        }

        if (currentManagerId != managerUserId) {
            response.sendRedirect(employeeListUrl + "&error=not_current_manager");
            return;
        }
        boolean isHR = "Human Resources".equalsIgnoreCase(department.getName());
        boolean isIT = "Information Technology".equalsIgnoreCase(department.getName());
        boolean isFI =  "Finance".equalsIgnoreCase(department.getName());

        Integer fallbackPositionId = null;
        String fallbackPositionName = null;
        if(isHR){
            fallbackPositionName = "HR Staff";
        }else if(isFI){
            fallbackPositionName = "Payroll Staff";
        }else{
            fallbackPositionName = "Employee";
        }
        Position fallbackPosition = positionDAO.findByName(fallbackPositionName);
        if (fallbackPosition != null && fallbackPosition.isActive()) {
            fallbackPositionId = fallbackPosition.getId();
        }

        boolean unassigned = departmentDAO.unassignManager(departmentId, managerUserId, fallbackPositionId);
        if (unassigned) {
            response.sendRedirect(employeeListUrl + "&msg=unassign_manager_success");
        } else {
            response.sendRedirect(employeeListUrl + "&error=unassign_manager_failed");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/admin/departments?error=invalid_request_method");
    }
}
