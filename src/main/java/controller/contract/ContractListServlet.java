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

        boolean canViewAllContracts = ContractAccessUtil.canViewAllContracts(request);
        List<LaborContract> contracts = canViewAllContracts
                ? contractDAO.findAll()
                : contractDAO.findByUserId(currentUser.getId());

        request.setAttribute("contracts", contracts);
        request.setAttribute("canCreateContract", ContractAccessUtil.canCreateContract(request));
        request.setAttribute("canUpdateContract", ContractAccessUtil.canUpdateContract(request));
        request.getRequestDispatcher("/WEB-INF/views/contract/contract_list.jsp").forward(request, response);
    }
}
