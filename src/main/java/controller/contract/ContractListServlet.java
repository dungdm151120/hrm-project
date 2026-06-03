package controller.contract;

import dao.LaborContractDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.LaborContract;

import java.io.IOException;
import java.util.List;

@WebServlet("/contracts")
public class ContractListServlet extends HttpServlet {
    private final LaborContractDAO contractDAO = new LaborContractDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = trimToNull(request.getParameter("search"));
        String contractType = trimToNull(request.getParameter("contractType"));
        String status = trimToNull(request.getParameter("status"));
        int pageSize = 10;
        int currentPage = parsePositiveInt(request.getParameter("page"), 1);

        int totalRecords = contractDAO.count(null, search, contractType, status);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / pageSize));

        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        int offset = (currentPage - 1) * pageSize;
        List<LaborContract> contracts = contractDAO.search(null, search, contractType, status, offset, pageSize);

        request.setAttribute("contracts", contracts);
        request.setAttribute("search", search);
        request.setAttribute("contractType", contractType);
        request.setAttribute("status", status);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("canCreateContract", ContractRequestHelper.hasPermission(request, "CONTRACT_CREATE"));
        request.setAttribute("canUpdateContract", ContractRequestHelper.hasPermission(request, "CONTRACT_UPDATE"));
        request.getRequestDispatcher("/WEB-INF/views/contract/contract_list.jsp").forward(request, response);
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty() || "all".equalsIgnoreCase(value.trim())) {
            return null;
        }
        return value.trim();
    }

    private int parsePositiveInt(String value, int defaultValue) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
