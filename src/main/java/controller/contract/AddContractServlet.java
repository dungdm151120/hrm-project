package controller.contract;

import dao.LaborContractDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.LaborContract;
import model.User;

import java.io.IOException;

@WebServlet("/contracts/add")
public class AddContractServlet extends HttpServlet {
    private static final String DEFAULT_WORKING_TIME = "Monday to Friday, 08:00 - 17:00";
    private final LaborContractDAO contractDAO = new LaborContractDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forwardForm(request, response, null, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LaborContract contract;
        try {
            contract = ContractFormMapper.fromRequest(request);
            contract.setWorkingTime(DEFAULT_WORKING_TIME);
        } catch (IllegalArgumentException e) {
            forwardForm(request, response, null, e.getMessage());
            return;
        }

        if (!"ACTIVE".equals(contract.getStatus())) {
            forwardForm(request, response, contract, "New contract status must be ACTIVE.");
            return;
        }

        if (!contractDAO.isActiveUser(contract.getUserId())) {
            forwardForm(request, response, contract, "Contract can only be created for an active employee.");
            return;
        }

        if (contractDAO.existsByContractCode(contract.getContractCode(), null)) {
            forwardForm(request, response, contract, "Contract code already exists.");
            return;
        }

        if (contractDAO.existsOverlappingActiveContract(
                contract.getUserId(), contract.getStartDate(), contract.getEndDate(), null)) {
            forwardForm(request, response, contract, "This employee already has an active contract in the selected date range.");
            return;
        }

        User currentUser = ContractRequestHelper.currentUser(request);
        Integer changedBy = currentUser == null ? null : currentUser.getId();

        if (contractDAO.add(contract, changedBy)) {
            response.sendRedirect(request.getContextPath() + "/contracts");
        } else {
            forwardForm(request, response, contract, "Add contract failed.");
        }
    }

    private void forwardForm(HttpServletRequest request, HttpServletResponse response,
                             LaborContract contract, String error)
            throws ServletException, IOException {
        request.setAttribute("contract", contract);
        request.setAttribute("users", userDAO.getAllActiveUsers());
        request.setAttribute("error", error);
        request.getRequestDispatcher("/WEB-INF/views/contract/add_contract.jsp").forward(request, response);
    }
}
