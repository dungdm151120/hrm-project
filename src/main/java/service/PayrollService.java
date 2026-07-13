package service;

import dao.*;
import model.*;
import util.PayrollCalculator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class PayrollService {

    private LaborContractDAO laborContractDAO = new LaborContractDAO();
    private PayrollCalculator calculator = new PayrollCalculator();
    private AttendanceDAO attendanceDAO = new AttendanceDAO();
    private PayrollDAO payrollDAO = new PayrollDAO();
    private UserDAO userDAO = new UserDAO();

    public Payroll generateMonthlyPayroll(User user, int month, int year, long basicSalary) {

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        LocalDate payrollPeriodDate = LocalDate.of(year, month, 1);

        AttendanceConfirmedSummary summary = attendanceDAO.getSummaryConfirmedAttendanceByUser(user.getId(), startDate, endDate);
        PayrollSetting setting = payrollDAO.getPayrollSettingByDate(payrollPeriodDate);
        List<PitBracket> brackets = payrollDAO.getPitBracketsByDate(payrollPeriodDate);

        String positionName = (user.getPositionName() != null) ? user.getPositionName().toLowerCase() : "";
        int numberOfDependents = payrollDAO.countDependentByUserId(user.getId(), month, year);
        double expectedHours = calculateExpectedHours(month, year);
        boolean isUnionMember = payrollDAO.isUnionMember(user.getId());
        int sickLeaveDays = attendanceDAO.countSickLeaveByUserId(user.getId(), month, year);

        if (summary == null || setting == null || brackets == null || brackets.isEmpty()) {
            return null;
        }

        return calculator.calculate(user, basicSalary, expectedHours, summary, isUnionMember, setting, positionName, numberOfDependents, brackets, sickLeaveDays, month, year);
    }

    public int generateBulkPayroll(List<User> users, int month, int year, Integer departmentId) throws Exception{

        Integer queryDeptId = (departmentId != null && departmentId == 0) ? null : departmentId;

        boolean hasSnapshot = attendanceDAO.hasAttendanceSnapshot(month, year, queryDeptId);

        if (!hasSnapshot) {
            throw new Exception("There is no confirmed attendance for this period in this department");
        }

        int totalUsersInDept = users.size();

        if (totalUsersInDept == 0) {
            throw new Exception("This department has no active employees to generate payroll.");
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        int employeesWithAttendance = attendanceDAO.countEmployeesWithAttendance(queryDeptId, startDate, endDate);

        if (employeesWithAttendance < totalUsersInDept) {
            int missingCount = totalUsersInDept - employeesWithAttendance;
            throw new Exception("Cannot generate payroll! There are " + missingCount
                    + " employee(s) in this department who do not have attendance data for " + month + "/" + year + ".");
        }

        int successCount = 0;
        for (User user : users) {
            int userId = user.getId();

            User detailedUser = userDAO.findByIdWithEmployeeCode(userId);
            if (detailedUser == null) {
                continue;
            }

            BigDecimal activeSalary = laborContractDAO.findActiveSalaryByUserId(userId);
            long basicSalary = activeSalary.longValue();

            Payroll payroll = this.generateMonthlyPayroll(detailedUser, month, year, basicSalary);
            if (payroll != null && payrollDAO.savePayroll(payroll)) {
                successCount++;
            }
        }
        return successCount;
    }

    private double calculateExpectedHours(int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        int weekdays = 0;
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            if (date.getDayOfWeek().getValue() < 6) {
                weekdays++;
            }
        }
        return weekdays * 8.0;
    }
}
