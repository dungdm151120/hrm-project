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
import java.util.List;

@WebServlet("/my-contract")
public class MyContractServlet extends HttpServlet {
    private final LaborContractDAO contractDAO = new LaborContractDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = ContractRequestHelper.currentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        LaborContract contract = findCurrentUserContract(currentUser);
        if (contract == null) {
            request.setAttribute("message", "No contract found.");
            request.getRequestDispatcher("/WEB-INF/views/contract/my_contract_empty.jsp").forward(request, response);
            return;
        }

        request.setAttribute("contract", contract);
        request.setAttribute("canUpdateContract", false);
        request.setAttribute("canTerminateContract", false);
        request.setAttribute("backUrl", request.getContextPath() + "/home");
        request.getRequestDispatcher("/WEB-INF/views/contract/contract_detail.jsp").forward(request, response);
    }

    private LaborContract findCurrentUserContract(User currentUser) {
        List<LaborContract> contracts = contractDAO.findByUserId(currentUser.getId());
        if (contracts.isEmpty()) {
            return null;
        }
        return contracts.get(0);
    }
}
