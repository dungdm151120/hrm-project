package controller.contract;

import jakarta.servlet.http.HttpServletRequest;
import model.LaborContract;

import java.math.BigDecimal;
import java.time.LocalDate;

final class ContractFormMapper {
    private ContractFormMapper() {
    }

    static LaborContract fromRequest(HttpServletRequest request) {
        LaborContract contract = new LaborContract();
        contract.setUserId(parseInt(request.getParameter("userId"), "Employee is required."));
        contract.setContractCode(required(request.getParameter("contractCode"), "Contract code is required."));
        contract.setContractType(required(request.getParameter("contractType"), "Contract type is required."));
        contract.setStartDate(parseDate(request.getParameter("startDate"), "Start date is required."));
        contract.setEndDate(parseOptionalDate(request.getParameter("endDate")));
        contract.setBaseSalary(parseOptionalSalary(request.getParameter("baseSalary")));
        contract.setWorkingTime(trimToNull(request.getParameter("workingTime")));
        contract.setWorkLocation(trimToNull(request.getParameter("workLocation")));
        contract.setStatus(required(request.getParameter("status"), "Status is required."));
        contract.setFileUrl(trimToNull(request.getParameter("fileUrl")));
        contract.setNote(trimToNull(request.getParameter("note")));

        if (contract.getEndDate() != null && contract.getEndDate().isBefore(contract.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date.");
        }

        return contract;
    }

    private static String required(String value, String message) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException(message);
        }
        return trimmed;
    }

    private static String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private static int parseInt(String value, String message) {
        try {
            return Integer.parseInt(required(value, message));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(message);
        }
    }

    private static LocalDate parseDate(String value, String message) {
        try {
            return LocalDate.parse(required(value, message));
        } catch (Exception e) {
            throw new IllegalArgumentException(message);
        }
    }

    private static LocalDate parseOptionalDate(String value) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return null;
        }
        try {
            return LocalDate.parse(trimmed);
        } catch (Exception e) {
            throw new IllegalArgumentException("End date is invalid.");
        }
    }

    private static BigDecimal parseOptionalSalary(String value) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return null;
        }
        try {
            BigDecimal salary = new BigDecimal(trimmed);
            if (salary.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Base salary must be non-negative.");
            }
            return salary;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Base salary is invalid.");
        }
    }
}
