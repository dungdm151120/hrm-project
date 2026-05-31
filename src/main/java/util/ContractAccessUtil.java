package util;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.LaborContract;
import model.User;

import java.io.IOException;
import java.util.Set;

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

    public static boolean hasPermission(HttpServletRequest request, String permissionCode) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        @SuppressWarnings("unchecked")
        Set<String> userPermissions = (Set<String>) session.getAttribute("userPermissions");
        return userPermissions != null && userPermissions.contains(permissionCode);
    }

    public static boolean canViewAllContracts(HttpServletRequest request) {
        return hasPermission(request, "CONTRACT_VIEW_LIST");
    }

    public static boolean canCreateContract(HttpServletRequest request) {
        return hasPermission(request, "CONTRACT_CREATE");
    }

    public static boolean canUpdateContract(HttpServletRequest request) {
        return hasPermission(request, "CONTRACT_UPDATE");
    }

    public static boolean canTerminateContract(HttpServletRequest request) {
        return hasPermission(request, "CONTRACT_TERMINATE");
    }

    public static boolean canViewContract(HttpServletRequest request, User user, LaborContract contract) {
        if (user == null || contract == null) {
            return false;
        }
        return hasPermission(request, "CONTRACT_VIEW_DETAIL")
                || (hasPermission(request, "CONTRACT_VIEW_OWN") && contract.getUserId() == user.getId());
    }

    public static void forwardForbidden(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        request.getRequestDispatcher("/WEB-INF/views/common/403.jsp").forward(request, response);
    }
}
