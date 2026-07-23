package controller.contract;

import jakarta.servlet.http.HttpServletRequest;
import model.LaborContract;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

final class ContractFormMapper {
    private static final int MAX_CONTRACT_CODE_LENGTH = 50;
    private static final int MAX_WORKING_TIME_LENGTH = 100;
    private static final int MAX_WORK_LOCATION_LENGTH = 255;
    private static final int MAX_NOTE_LENGTH = 1000;
    private static final BigDecimal MAX_BASE_SALARY = new BigDecimal("9999999999999.99");
    private static final String CONTRACT_CODE_PATTERN = "^[A-Za-z0-9/_-]+$";

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
        contract.setContractCode(validContractCode(request.getParameter("contractCode")));
        contract.setContractType(validContractType(request.getParameter("contractType")));
        contract.setStartDate(parseDate(request.getParameter("startDate"), "Start date is required."));
        contract.setEndDate(parseOptionalDate(request.getParameter("endDate")));
        contract.setBaseSalary(parseRequiredSalary(request.getParameter("baseSalary")));
        contract.setWorkingTime(requiredMaxLength(
                request.getParameter("workingTime"), "Working time", MAX_WORKING_TIME_LENGTH));
        contract.setWorkLocation(requiredMaxLength(
                request.getParameter("workLocation"), "Work location", MAX_WORK_LOCATION_LENGTH));
        contract.setStatus(validStatus(request.getParameter("status")));
        contract.setNote(optionalMaxLength(request.getParameter("note"), "Note", MAX_NOTE_LENGTH));

        if ("INDEFINITE_TERM".equals(contract.getContractType())) {
            contract.setEndDate(null);
        } else if (contract.getEndDate() == null) {
            throw new IllegalArgumentException("End date is required for this contract type.");
        }

        if (contract.getEndDate() != null && !contract.getEndDate().isAfter(contract.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date.");
        }

        if (contract.getEndDate() != null && contract.getEndDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("End date cannot be in the past.");
        }

        if ("FIXED_TERM".equals(contract.getContractType())) {
            LocalDate minimumEndDate = contract.getStartDate().plusMonths(1);
            LocalDate maximumEndDate = contract.getStartDate().plusMonths(36);

            if (contract.getEndDate().isBefore(minimumEndDate)) {
                throw new IllegalArgumentException("Fixed-term contract must be at least 1 month.");
            }
            if (contract.getEndDate().isAfter(maximumEndDate)) {
                throw new IllegalArgumentException("Fixed-term contract must not exceed 36 months.");
            }
        }

        return contract;
    }

    private static String validContractCode(String value) {
        String contractCode = requiredMaxLength(value, "Contract code", MAX_CONTRACT_CODE_LENGTH);
        if (!contractCode.matches(CONTRACT_CODE_PATTERN)) {
            throw new IllegalArgumentException(
                    "Contract code may contain only letters, numbers, hyphens, underscores, and slashes."
            );
        }
        return contractCode;
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

    private static String requiredMaxLength(String value, String fieldName, int maxLength) {
        String trimmed = required(value, fieldName + " is required.");
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " must not exceed " + maxLength + " characters.");
        }
        return trimmed;
    }

    private static String optionalMaxLength(String value, String fieldName, int maxLength) {
        String trimmed = trimToNull(value);
        if (trimmed != null && trimmed.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " must not exceed " + maxLength + " characters.");
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

    private static BigDecimal parseRequiredSalary(String value) {
        String trimmed = required(value, "Base salary is required.");
        try {
            BigDecimal salary = new BigDecimal(trimmed);
            if (salary.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Base salary must be greater than 0.");
            }
            if (salary.scale() > 2) {
                throw new IllegalArgumentException("Base salary must not have more than 2 decimal places.");
            }
            if (salary.compareTo(MAX_BASE_SALARY) > 0) {
                throw new IllegalArgumentException("Base salary exceeds the maximum allowed value.");
            }
            return salary;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Base salary is invalid.");
        }
    }
}
