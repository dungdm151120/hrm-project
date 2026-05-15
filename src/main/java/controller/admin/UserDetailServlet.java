package controller.admin;

import dao.UserDAO;
import jakarta.servlet.annotation.WebServlet;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "UserDetailServlet", value = "/user_detail")
public class UserDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Lấy id từ tham số 'id' trên URL
        String idString = request.getParameter("id");

        if (idString != null && !idString.isEmpty()) {
            try {
                int id = Integer.parseInt(idString);

                // 2. Gọi DAO để lấy dữ liệu của RIÊNG user đó
                UserDAO dao = new UserDAO();
                User user = dao.findById(id);

                if (user != null) {
                    // 3. Đưa đối tượng user vào request để JSP hiển thị
                    request.setAttribute("user", user);

                    // 4. Forward sang trang detail
                    request.getRequestDispatcher("/WEB-INF/views/admin/user_detail.jsp").forward(request, response);
                } else {
                    // Nếu không tìm thấy user, quay về danh sách
                    response.sendRedirect("user_list");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect("user_list");
            }
        } else {
            response.sendRedirect("user_list");
        }
    }
}
