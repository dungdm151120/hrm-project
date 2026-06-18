package controller.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dao.ChatDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/chat/search")
public class ChatSearchServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String keyword = request.getParameter("q");
        if (keyword == null || keyword.trim().isEmpty()) {
            response.setContentType("application/json");
            response.getWriter().print("[]");
            return;
        }

        ChatDAO chatDAO = new ChatDAO();
        try {
            List<User> users = chatDAO.searchUsers(keyword.trim(), currentUser.getId());
            JsonArray array = new JsonArray();
            for (User user : users) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", user.getId());
                obj.addProperty("fullName", user.getFullName());
                obj.addProperty("email", user.getEmail());
                obj.addProperty("avatarUrl", user.getAvatarUrl());
                array.add(obj);
            }
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(array.toString());
            out.flush();
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}