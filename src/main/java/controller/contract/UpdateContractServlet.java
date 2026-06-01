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
import util.ContractAccessUtil;

import java.io.IOException;

@WebServlet("/contracts/update")
public class UpdateContractServlet extends HttpServlet {
    private final LaborContractDAO contractDAO = new LaborContractDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ensureManager(request, response)) {
            return;
        }

        LaborContract contract = findRequestedContract(request, response);
        if (contract == null) {
            return;
        }

        forwardForm(request, response, contract, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ensureManager(request, response)) {
            return;
        }

        LaborContract contract;
        try {
            contract = ContractFormMapper.fromRequest(request);
            contract.setId(Integer.parseInt(request.getParameter("id")));
        } catch (Exception e) {
            forwardForm(request, response, null, "Invalid contract data.");
            return;
        }

        if (contractDAO.existsByContractCode(contract.getContractCode(), contract.getId())) {
            forwardForm(request, response, contract, "Contract code already exists.");
            return;
        }

        if (contractDAO.update(contract)) {
            response.sendRedirect(request.getContextPath() + "/contracts/detail?id=" + contract.getId());
        } else {
            forwardForm(request, response, contract, "Update contract failed.");
        }
    }

    private LaborContract findRequestedContract(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int contractId;
        try {
            contractId = Integer.parseInt(request.getParameter("id"));
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/contracts");
            return null;
        }

        LaborContract contract = contractDAO.findById(contractId);
        if (contract == null) {
            response.sendRedirect(request.getContextPath() + "/contracts");
        }
        return contract;
    }

    private boolean ensureManager(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = ContractAccessUtil.currentUser(request);
        if (!ContractAccessUtil.canManageContracts(currentUser)) {
            ContractAccessUtil.forwardForbidden(request, response);
            return false;
        }
        return true;
    }

    private void forwardForm(HttpServletRequest request, HttpServletResponse response,
                             LaborContract contract, String error)
            throws ServletException, IOException {
        request.setAttribute("contract", contract);
        request.setAttribute("users", userDAO.findAllUsers());
        request.setAttribute("formAction", request.getContextPath() + "/contracts/update");
        request.setAttribute("error", error);
        request.getRequestDispatcher("/WEB-INF/views/contract/contract_form.jsp").forward(request, response);
    }
}
