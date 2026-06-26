package controller.task;

import dao.TaskDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Task;

import java.io.IOException;

@WebServlet("/tasks/delete")
public class TaskDeleteServlet extends HttpServlet {
    private final TaskDAO taskDAO = new TaskDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            long id = parseLong(request.getParameter("id"), 0);
            Task task = taskDAO.getTaskById(id);
            if (task == null) {
                response.sendRedirect(request.getContextPath() + "/tasks?error=not_found");
                return;
            }
            taskDAO.deleteTask(id);
            request.getSession().setAttribute("message", "Task deleted successfully.");
            response.sendRedirect(request.getContextPath() + "/tasks");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private long parseLong(String value, long defaultValue) {
        try {
            return value == null || value.isBlank() ? defaultValue : Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
