package controller.contract;

import dao.LaborContractDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.LaborContract;
import model.User;

import java.io.IOException;

@WebServlet({"/contracts/detail", "/my-contract/detail"})
public class ContractDetailServlet extends HttpServlet {
    private final LaborContractDAO contractDAO = new LaborContractDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int contractId;
        try {
            contractId = Integer.parseInt(request.getParameter("id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/contracts");
            return;
        }

        LaborContract contract = contractDAO.findById(contractId);
        if (contract == null) {
            response.sendRedirect(request.getContextPath() + "/contracts");
            return;
        }

        boolean ownContractView = "/my-contract/detail".equals(request.getServletPath());
        if (ownContractView && !isCurrentUserContract(request, contract)) {
            response.sendRedirect(request.getContextPath() + "/my-contract");
            return;
        }

        request.setAttribute("contract", contract);
        request.setAttribute("canUpdateContract", !ownContractView && ContractRequestHelper.hasPermission(request, "CONTRACT_UPDATE"));
        request.setAttribute("canTerminateContract", !ownContractView && ContractRequestHelper.hasPermission(request, "CONTRACT_TERMINATE"));
        request.setAttribute("backUrl", request.getContextPath() + (ownContractView ? "/my-contract" : "/contracts"));
        request.getRequestDispatcher("/WEB-INF/views/contract/contract_detail.jsp").forward(request, response);
    }

    private boolean isCurrentUserContract(HttpServletRequest request, LaborContract contract) {
        User currentUser = ContractRequestHelper.currentUser(request);
        return currentUser != null && currentUser.getId() == contract.getUserId();
    }
}
