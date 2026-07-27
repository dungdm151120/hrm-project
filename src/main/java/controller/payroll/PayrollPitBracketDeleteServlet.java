package controller.payroll;

import dao.PayrollDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.PitBracketVersion;

import java.io.IOException;

@WebServlet("/payroll/pit/delete")
public class PayrollPitBracketDeleteServlet extends HttpServlet {

    private final PayrollDAO payrollDAO = new PayrollDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idStr = request.getParameter("id");

            if (idStr != null && !idStr.trim().isEmpty()) {
                int id = Integer.parseInt(idStr.trim());

                PitBracketVersion activePit = payrollDAO.getCurrentlyActivePitVersion();

                if (activePit != null && activePit.getId() == id) {
                    request.getSession().setAttribute("error", "Cannot delete the currently active PIT bracket version!");
                } else {
                    boolean deleted = payrollDAO.deletePitVersion(id);
                    if (deleted) {
                        request.getSession().setAttribute("message", "PIT bracket version deleted successfully!");
                    } else {
                        request.getSession().setAttribute("error", "Failed to delete PIT bracket version.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "An error occurred while deleting PIT version: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/payroll/pit/list");
    }
}