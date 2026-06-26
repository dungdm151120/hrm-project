package controller.task;

import dao.TaskDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/tasks/all")
public class TaskViewAllServlet extends HttpServlet {
    private static final int PAGE_SIZE = 10;
    private final TaskDAO taskDAO = new TaskDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        int page = parseInt(request.getParameter("page"), 1);
        int totalRecords = taskDAO.countTasks(keyword, status, 0, false);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / PAGE_SIZE));
        if (page > totalPages) {
            page = totalPages;
        }

        request.setAttribute("tasks", taskDAO.getTasks(keyword, status, page, PAGE_SIZE, 0, false));
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        request.setAttribute("activeView", "all");
        request.setAttribute("listAction", "/tasks/all");
        request.setAttribute("canViewAllTasks", true);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.getRequestDispatcher("/WEB-INF/views/task/task-list.jsp").forward(request, response);
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return value == null || value.isBlank() ? defaultValue : Math.max(1, Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
