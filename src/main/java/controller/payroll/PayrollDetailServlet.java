package controller.payroll;

import dao.AttendanceDAO;
import dao.PayrollDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AttendanceConfirmedSummary;
import model.Payroll;
import model.PayrollSetting;
import model.User;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;

@WebServlet("/payroll/detail")
public class PayrollDetailServlet extends HttpServlet {

    private final PayrollDAO payrollDAO = new PayrollDAO();
    private final UserDAO userDAO = new UserDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String idParam = request.getParameter("id");
            String month = request.getParameter("month");
            String year = request.getParameter("year");
            String departmentId = request.getParameter("departmentId");

            if (idParam == null || idParam.trim().isEmpty()) {
                request.getSession().setAttribute("error", "Invalid payroll request id.");
                response.sendRedirect(request.getContextPath() + "/payroll/department");
                return;
            }

            int payrollId = Integer.parseInt(idParam);
            Payroll payroll = payrollDAO.findById(payrollId);

            if (payroll == null) {
                request.getSession().setAttribute("error", "Payroll record not found.");
                response.sendRedirect(request.getContextPath() + "/payroll/department");
                return;
            }

            User employeeInfo = userDAO.findByIdWithEmployeeCode(payroll.getUserId());
            if (employeeInfo == null) {
                employeeInfo = new User();
                employeeInfo.setId(payroll.getUserId());
                employeeInfo.setFullName("Unknown Employee");
                employeeInfo.setDepartmentName("N/A");
                employeeInfo.setPositionName("N/A");
            }

            int parsedMonth = Integer.parseInt(month);
            int parsedYear = Integer.parseInt(year);
            LocalDate payrollPeriodDate = LocalDate.of(parsedYear, parsedMonth, 1);

            int sickLeaveDays = attendanceDAO.countSickLeaveByUserId(payroll.getUserId(), parsedMonth, parsedYear);

            PayrollSetting setting = payrollDAO.getPayrollSettingByDate(payrollPeriodDate);
            int numberOfDependents = payrollDAO.countDependentByUserId(
                    payroll.getUserId(),
                    payroll.getMonth(),
                    payroll.getYear()
            );

            YearMonth yearMonth = YearMonth.of(parsedYear, parsedMonth);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();
            AttendanceConfirmedSummary summary = attendanceDAO.getSummaryConfirmedAttendanceByUser(payroll.getUserId(), startDate, endDate);
            double overtimeHours = summary.getOvertimeHours();

            request.setAttribute("overtimeHours", overtimeHours);
            request.setAttribute("sickLeaveDays", sickLeaveDays);
            request.setAttribute("month", month);
            request.setAttribute("year", year);
            request.setAttribute("departmentId", departmentId);
            request.setAttribute("numberOfDependents", numberOfDependents);
            request.setAttribute("setting", setting);
            request.setAttribute("payroll", payroll);
            request.setAttribute("employee", employeeInfo);

            request.getRequestDispatcher("/WEB-INF/views/payroll/payroll_detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "Payroll ID must be a valid number.");
            response.sendRedirect(request.getContextPath() + "/payroll/list");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "An error occurred while loading details.");
            response.sendRedirect(request.getContextPath() + "/payroll/list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
