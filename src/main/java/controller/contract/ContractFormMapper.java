package controller.contract;

import jakarta.servlet.http.HttpServletRequest;
import model.LaborContract;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

final class ContractFormMapper {
    private static final Set<String> VALID_CONTRACT_TYPES = Set.of(
            "FIXED_TERM", "INDEFINITE_TERM", "PROBATION", "PART_TIME"
    );
    private static final Set<String> VALID_STATUSES = Set.of(
            "ACTIVE"
    );

    private ContractFormMapper() {
    }

    static LaborContract fromRequest(HttpServletRequest request) {
        LaborContract contract = new LaborContract();
        contract.setUserId(parseInt(request.getParameter("userId"), "Employee is required."));
        contract.setContractCode(required(request.getParameter("contractCode"), "Contract code is required."));
        contract.setContractType(validContractType(request.getParameter("contractType")));
        contract.setStartDate(parseDate(request.getParameter("startDate"), "Start date is required."));
        contract.setEndDate(parseOptionalDate(request.getParameter("endDate")));
        contract.setBaseSalary(parseOptionalSalary(request.getParameter("baseSalary")));
        contract.setWorkingTime(trimToNull(request.getParameter("workingTime")));
        contract.setWorkLocation(trimToNull(request.getParameter("workLocation")));
        contract.setStatus(validStatus(request.getParameter("status")));
        contract.setFileUrl(trimToNull(request.getParameter("fileUrl")));
        contract.setNote(trimToNull(request.getParameter("note")));

        if ("INDEFINITE_TERM".equals(contract.getContractType())) {
            contract.setEndDate(null);
        }

        if (contract.getEndDate() != null && !contract.getEndDate().isAfter(contract.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date.");
        }

        if (contract.getEndDate() != null && contract.getEndDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("End date cannot be in the past.");
        }

        return contract;
    }

    private static String validContractType(String value) {
        String contractType = required(value, "Contract type is required.").toUpperCase();
        if (!VALID_CONTRACT_TYPES.contains(contractType)) {
            throw new IllegalArgumentException("Invalid contract type.");
        }
        return contractType;
    }

    private static String validStatus(String value) {
        String status = required(value, "Status is required.").toUpperCase();
        if (!VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Contract status is managed automatically.");
        }
        return status;
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
