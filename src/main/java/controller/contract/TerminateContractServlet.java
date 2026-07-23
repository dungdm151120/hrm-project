package controller.contract;

import dao.LaborContractDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/contracts/terminate")
public class TerminateContractServlet extends HttpServlet {
    private static final int MAX_TERMINATION_REASON_LENGTH = 1000;
    private final LaborContractDAO contractDAO = new LaborContractDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int contractId;
        try {
            contractId = Integer.parseInt(request.getParameter("id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/contracts");
            return;
        }

        User currentUser = ContractRequestHelper.currentUser(request);
        Integer terminatedBy = currentUser == null ? null : currentUser.getId();

        String terminationReason = trimToNull(request.getParameter("terminationReason"));
        if (terminationReason == null) {
            redirectWithError(request, response, contractId, "Termination reason is required.");
            return;
        }
        if (terminationReason.length() > MAX_TERMINATION_REASON_LENGTH) {
            redirectWithError(request, response, contractId,
                    "Termination reason must not exceed 1000 characters.");
            return;
        }

        boolean terminated = contractDAO.terminate(contractId, terminationReason, terminatedBy);
        if (terminated) {
            response.sendRedirect(request.getContextPath() + "/contracts/detail?id=" + contractId);
            return;
        }

        redirectWithError(request, response, contractId, "Only active contracts can be terminated.");
    }

    private void redirectWithError(HttpServletRequest request, HttpServletResponse response,
                                   int contractId, String message) throws IOException {
        String error = URLEncoder.encode(message, StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath()
                + "/contracts/detail?id=" + contractId + "&error=" + error);
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
