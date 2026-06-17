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
import model.Message;
import model.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/chat/messages")
public class ChatMessagesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String conversationIdParam = request.getParameter("conversationId");
        String offsetParam = request.getParameter("offset");
        String limitParam = request.getParameter("limit");
        if (conversationIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int conversationId = Integer.parseInt(conversationIdParam);
        int offset = offsetParam != null ? Integer.parseInt(offsetParam) : 0;
        int limit = limitParam != null ? Integer.parseInt(limitParam) : 50;

        ChatDAO chatDAO = new ChatDAO();
        try {
            List<Message> messages = chatDAO.getMessagesByConversation(conversationId, offset, limit);
            JsonArray array = new JsonArray();
            for (Message msg : messages) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", msg.getId());
                obj.addProperty("senderId", msg.getSenderId());
                obj.addProperty("content", msg.getContent());
                obj.addProperty("sentAt", msg.getSentAt() != null ? msg.getSentAt().toString() : null);
                obj.addProperty("read", msg.isRead());
                obj.addProperty("senderName", msg.getSenderName());
                obj.addProperty("senderAvatar", msg.getSenderAvatar());
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