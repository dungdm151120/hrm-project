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

        boolean terminated = contractDAO.terminate(contractId, request.getParameter("terminationReason"), terminatedBy);
        if (terminated) {
            response.sendRedirect(request.getContextPath() + "/contracts/detail?id=" + contractId);
            return;
        }

        String error = URLEncoder.encode("Only active contracts can be terminated.", StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/contracts/detail?id=" + contractId + "&error=" + error);
    }
}
