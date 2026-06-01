package controller.contract;

import dao.LaborContractDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.LaborContract;

import java.io.IOException;

@WebServlet("/contracts/add")
public class AddContractServlet extends HttpServlet {
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
        } catch (IllegalArgumentException e) {
            forwardForm(request, response, null, e.getMessage());
            return;
        }

        if (contractDAO.existsByContractCode(contract.getContractCode(), null)) {
            forwardForm(request, response, contract, "Contract code already exists.");
            return;
        }

        if (contractDAO.add(contract)) {
            response.sendRedirect(request.getContextPath() + "/contracts");
        } else {
            forwardForm(request, response, contract, "Add contract failed.");
        }
    }

    private void forwardForm(HttpServletRequest request, HttpServletResponse response,
                             LaborContract contract, String error)
            throws ServletException, IOException {
        request.setAttribute("contract", contract);
        request.setAttribute("users", userDAO.findAllUsers());
        request.setAttribute("formAction", request.getContextPath() + "/contracts/add");
        request.setAttribute("error", error);
        request.getRequestDispatcher("/WEB-INF/views/contract/contract_form.jsp").forward(request, response);
    }
}
