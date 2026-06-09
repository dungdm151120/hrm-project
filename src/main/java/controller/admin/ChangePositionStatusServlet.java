package controller.admin;

import dao.PositionDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Position;

import java.io.IOException;

@WebServlet("/position/toggle-status")
public class ChangePositionStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idParam = request.getParameter("id");
        String actionParam = request.getParameter("action");

        if (idParam == null || actionParam == null) {
            response.sendRedirect(request.getContextPath() + "/position/list");
            return;
        }

        try {
            int positionId = Integer.parseInt(idParam);
            boolean newStatus = "activate".equalsIgnoreCase(actionParam);

            PositionDAO positionDAO = new PositionDAO();
            UserDAO userDAO = new UserDAO();

            if (!newStatus) {

                boolean isAssigned = userDAO.isPositionAssigned(positionId);
                if (isAssigned) {
                    session.setAttribute("error", "Không thể vô hiệu hóa vì đang có nhân viên đảm nhận vị trí này!");
                    response.sendRedirect(request.getContextPath() + "/position/list");
                    return;
                }

                Position position = positionDAO.findById(positionId);
                if (position != null) {
                    String posName = position.getName();
                    if ("System Administrator".equalsIgnoreCase(posName) ||
                            "HR Manager".equalsIgnoreCase(posName) ||
                            "Department Manager".equalsIgnoreCase(posName)) {
                        session.setAttribute("error", "Không thể vô hiệu hóa vị trí quản lý hệ thống: " + posName);
                        response.sendRedirect(request.getContextPath() + "/position/list");
                        return;
                    }
                }
            }

            boolean isUpdated = positionDAO.updatePositionStatus(positionId, newStatus);

            if (isUpdated) {
                if (!newStatus) {
                    userDAO.clearPositionForUsers(positionId);
                    session.setAttribute("message", "Đã vô hiệu hóa vị trí thành công.");
                } else {
                    session.setAttribute("message", "Đã kích hoạt vị trí thành công.");
                }
            } else {
                session.setAttribute("error", "Thay đổi trạng thái thất bại.");
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("error", "Dữ liệu không hợp lệ.");
        }

        response.sendRedirect(request.getContextPath() + "/position/list");
    }
}