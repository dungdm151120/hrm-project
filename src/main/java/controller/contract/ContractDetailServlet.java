package controller.contract;

import dao.LaborContractDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.LaborContract;
import model.User;
import util.ContractAccessUtil;

import java.io.IOException;

@WebServlet("/contracts/detail")
public class ContractDetailServlet extends HttpServlet {
    private final LaborContractDAO contractDAO = new LaborContractDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = ContractAccessUtil.currentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

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

        if (!ContractAccessUtil.canViewContract(currentUser, contract)) {
            ContractAccessUtil.forwardForbidden(request, response);
            return;
        }

        request.setAttribute("contract", contract);
        request.setAttribute("canManageContracts", ContractAccessUtil.canManageContracts(currentUser));
        request.getRequestDispatcher("/WEB-INF/views/contract/contract_detail.jsp").forward(request, response);
    }
}
