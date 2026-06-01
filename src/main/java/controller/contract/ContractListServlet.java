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
import java.util.List;

@WebServlet("/contracts")
public class ContractListServlet extends HttpServlet {
    private final LaborContractDAO contractDAO = new LaborContractDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = ContractAccessUtil.currentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String search = trimToNull(request.getParameter("search"));
        String contractType = trimToNull(request.getParameter("contractType"));
        String status = trimToNull(request.getParameter("status"));
        boolean canManageContracts = ContractAccessUtil.canManageContracts(currentUser);
        Integer userId = canManageContracts ? null : currentUser.getId();
        List<LaborContract> contracts = contractDAO.search(userId, search, contractType, status);

        request.setAttribute("contracts", contracts);
        request.setAttribute("search", search);
        request.setAttribute("contractType", contractType);
        request.setAttribute("status", status);
        request.setAttribute("canManageContracts", canManageContracts);
        request.getRequestDispatcher("/WEB-INF/views/contract/contract_list.jsp").forward(request, response);
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty() || "all".equalsIgnoreCase(value.trim())) {
            return null;
        }
        return value.trim();
    }
}
