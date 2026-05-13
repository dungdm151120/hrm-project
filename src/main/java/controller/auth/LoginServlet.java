package controller.auth;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/common/login.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Tạm thời test trước, chưa cần database
        if ("admin@gmail.com".equals(email) && "123".equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("email", email);

            response.sendRedirect(request.getContextPath() + "/home");
        } else {
            request.setAttribute("error", "Email hoặc mật khẩu không đúng");
            request.getRequestDispatcher("/WEB-INF/views/common/login.jsp")
                    .forward(request, response);
        }
    }
}