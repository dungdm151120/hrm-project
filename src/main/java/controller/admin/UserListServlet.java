package controller.admin;

import dao.UserDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "UserListServlet", value = "/user_list")
public class UserListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Khởi tạo DAO để lấy dữ liệu từ Database
        UserDAO dao = new UserDAO();

        // 2. Lấy danh sách User
        List<User> list = dao.findAllUsers();

        // 3. Đẩy danh sách vào request attribute để JSP có thể lấy được
        // Tên "userList" phải khớp với items="${userList}" trong thẻ c:forEach ở JSP
        request.setAttribute("userList", list);

        // 4. Chuyển hướng sang trang JSP để hiển thị giao diện
        // Đảm bảo đường dẫn file jsp của bạn chính xác
        request.getRequestDispatcher("/WEB-INF/views/admin/user_list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
