package controller.chat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dao.ChatDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Conversation;
import model.Message;
import model.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/chat/conversations")
public class ChatConversationsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        ChatDAO chatDAO = new ChatDAO();
        try {
            List<Conversation> conversations = chatDAO.getConversationsByUserId(currentUser.getId());
            JsonArray array = new JsonArray();
            for (Conversation conv : conversations) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", conv.getId());
                obj.addProperty("isGroup", conv.isGroup());
                obj.addProperty("name", conv.getName());
                obj.addProperty("createdAt", conv.getCreatedAt() != null ? conv.getCreatedAt().toString() : null);
                if (conv.getLastMessage() != null) {
                    JsonObject lastMsg = new JsonObject();
                    lastMsg.addProperty("id", conv.getLastMessage().getId());
                    lastMsg.addProperty("content", conv.getLastMessage().getContent());
                    lastMsg.addProperty("senderName", conv.getLastMessage().getSenderName());
                    lastMsg.addProperty("sentAt", conv.getLastMessage().getSentAt() != null ? conv.getLastMessage().getSentAt().toString() : null);
                    obj.add("lastMessage", lastMsg);
                }
                JsonArray participants = new JsonArray();
                for (User p : conv.getParticipants()) {
                    JsonObject pObj = new JsonObject();
                    pObj.addProperty("id", p.getId());
                    pObj.addProperty("fullName", p.getFullName());
                    pObj.addProperty("avatarUrl", p.getAvatarUrl());
                    participants.add(pObj);
                }
                obj.add("participants", participants);
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