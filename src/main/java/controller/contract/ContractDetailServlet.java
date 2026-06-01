package controller.contract;

import dao.LaborContractDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.LaborContract;

import java.io.IOException;

@WebServlet("/contracts/detail")
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

        request.setAttribute("contract", contract);
        request.setAttribute("canUpdateContract", ContractRequestHelper.hasPermission(request, "CONTRACT_UPDATE"));
        request.setAttribute("canTerminateContract", ContractRequestHelper.hasPermission(request, "CONTRACT_TERMINATE"));
        request.setAttribute("backUrl", request.getContextPath() + "/contracts");
        request.getRequestDispatcher("/WEB-INF/views/contract/contract_detail.jsp").forward(request, response);
    }
}
