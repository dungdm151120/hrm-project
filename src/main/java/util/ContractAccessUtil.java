package util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.LaborContract;
import model.User;

import java.io.IOException;

public final class ContractAccessUtil {
    private ContractAccessUtil() {
    }

    public static User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute("currentUser");
    }

    public static boolean canManageContracts(User user) {
        if (user == null || user.getRoleName() == null) {
            return false;
        }
        String roleName = user.getRoleName();
        return "HR_STAFF".equalsIgnoreCase(roleName)
                || "HR_MANAGER".equalsIgnoreCase(roleName);
    }

    public static boolean canViewContract(User user, LaborContract contract) {
        if (user == null || contract == null) {
            return false;
        }
        return canManageContracts(user) || contract.getUserId() == user.getId();
    }

    public static void forwardForbidden(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/WEB-INF/views/common/403.jsp").forward(request, response);
    }
}
