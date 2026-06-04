package controller.contract;

import dao.LaborContractDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.LaborContract;

import java.io.IOException;

@WebServlet("/contracts/update")
public class UpdateContractServlet extends HttpServlet {
    private final LaborContractDAO contractDAO = new LaborContractDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LaborContract contract = findRequestedContract(request, response);
        if (contract == null) {
            return;
        }
        if (isTerminated(contract)) {
            response.sendRedirect(request.getContextPath() + "/contracts/detail?id=" + contract.getId());
            return;
        }

        forwardForm(request, response, contract, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LaborContract current = findRequestedContract(request, response);
        if (current == null) {
            return;
        }
        if (isTerminated(current)) {
            response.sendRedirect(request.getContextPath() + "/contracts/detail?id=" + current.getId());
            return;
        }

        LaborContract updatedContract;
        try {
            updatedContract = ContractFormMapper.fromRequest(request);
        } catch (IllegalArgumentException e) {
            forwardForm(request, response, current, e.getMessage());
            return;
        }
        applyImmutableFields(current, updatedContract);

        String validationError = validateUpdate(current, updatedContract);
        if (validationError != null) {
            forwardForm(request, response, updatedContract, validationError);
            return;
        }

        if (contractDAO.update(updatedContract)) {
            response.sendRedirect(request.getContextPath() + "/contracts/detail?id=" + updatedContract.getId());
        } else {
            forwardForm(request, response, updatedContract, "Update contract failed.");
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

    private void forwardForm(HttpServletRequest request, HttpServletResponse response,
                             LaborContract contract, String error)
            throws ServletException, IOException {
        request.setAttribute("contract", contract);
        request.setAttribute("error", error);
        request.getRequestDispatcher("/WEB-INF/views/contract/update_contract.jsp").forward(request, response);
    }

    private void applyImmutableFields(LaborContract current, LaborContract contract) {
        contract.setId(current.getId());
        contract.setUserId(current.getUserId());
        contract.setEmployeeCode(current.getEmployeeCode());
        contract.setEmployeeName(current.getEmployeeName());
        contract.setEmployeeEmail(current.getEmployeeEmail());
        contract.setContractCode(current.getContractCode());
    }

    private String validateUpdate(LaborContract current, LaborContract contract) {
        if (!"TERMINATED".equals(current.getStatus()) && "TERMINATED".equals(contract.getStatus())) {
            return "Use the Terminate Contract action to terminate a contract.";
        }

        boolean activeDateOverlap = "ACTIVE".equals(contract.getStatus())
                && contractDAO.existsOverlappingActiveContract(
                        contract.getUserId(),
                        contract.getStartDate(),
                        contract.getEndDate(),
                        contract.getId()
                );
        if (activeDateOverlap) {
            return "This employee already has an active contract in the selected date range.";
        }

        return null;
    }

    private boolean isTerminated(LaborContract contract) {
        return contract != null && "TERMINATED".equals(contract.getStatus());
    }
}
