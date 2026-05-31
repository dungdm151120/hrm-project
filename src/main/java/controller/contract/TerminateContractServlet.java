package controller.contract;

import dao.LaborContractDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import util.ContractAccessUtil;

import java.io.IOException;

@WebServlet("/contracts/terminate")
public class TerminateContractServlet extends HttpServlet {
    private final LaborContractDAO contractDAO = new LaborContractDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = ContractAccessUtil.currentUser(request);
        if (currentUser == null || !ContractAccessUtil.canTerminateContract(request)) {
            ContractAccessUtil.forwardForbidden(request, response);
            return;
        }

        int contractId;
        try {
            contractId = Integer.parseInt(request.getParameter("id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/contracts");
            return;
        }

        contractDAO.terminate(contractId, request.getParameter("terminationReason"));
        response.sendRedirect(request.getContextPath() + "/contracts/detail?id=" + contractId);
    }
}
