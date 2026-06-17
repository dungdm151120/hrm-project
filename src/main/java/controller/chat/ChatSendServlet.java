package controller.chat;

import com.google.gson.JsonObject;
import dao.ChatDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Message;
import model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/chat/send")
public class ChatSendServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        String body = sb.toString();

        com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
        com.google.gson.JsonObject json = parser.parse(body).getAsJsonObject();
        int conversationId = json.get("conversationId").getAsInt();
        String content = json.get("content").getAsString();

        ChatDAO chatDAO = new ChatDAO();
        try {
            Message message = chatDAO.sendMessage(conversationId, currentUser.getId(), content);
            JsonObject obj = new JsonObject();
            obj.addProperty("id", message.getId());
            obj.addProperty("senderId", message.getSenderId());
            obj.addProperty("content", message.getContent());
            obj.addProperty("sentAt", message.getSentAt().toString());
            obj.addProperty("read", message.isRead());
            obj.addProperty("senderName", message.getSenderName());
            obj.addProperty("senderAvatar", message.getSenderAvatar());
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(obj.toString());
            out.flush();
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}