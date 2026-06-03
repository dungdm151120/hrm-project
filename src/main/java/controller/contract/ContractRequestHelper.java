package controller.contract;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.util.Set;

final class ContractRequestHelper {
    private ContractRequestHelper() {
    }

    static User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute("currentUser");
    }

    @SuppressWarnings("unchecked")
    static boolean hasPermission(HttpServletRequest request, String permissionCode) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Set<String> userPermissions = (Set<String>) session.getAttribute("userPermissions");
        return userPermissions != null && userPermissions.contains(permissionCode);
    }
}
