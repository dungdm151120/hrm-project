package controller.profile;

import model.User;
import service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = (int) session.getAttribute("userId");

        User user = userService.getProfile(userId);

        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/profile/profile.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = (int) session.getAttribute("userId");

        User user = new User();
        user.setId(userId);
        user.setPhone(trimToNull(request.getParameter("phone")));
        user.setGender(trimToNull(request.getParameter("gender")));
        user.setAddress(trimToNull(request.getParameter("address")));
        user.setAvatarUrl(trimToNull(request.getParameter("avatarUrl")));

        String dateOfBirth = trimToNull(request.getParameter("dateOfBirth"));
        try {
            if (dateOfBirth != null) {
                user.setDateOfBirth(LocalDate.parse(dateOfBirth).atStartOfDay());
            }
        } catch (Exception e) {
            User currentProfile = userService.getProfile(userId);
            request.setAttribute("user", currentProfile);
            request.setAttribute("profileError", "Date of birth is invalid.");
            request.getRequestDispatcher("/WEB-INF/views/profile/profile.jsp")
                    .forward(request, response);
            return;
        }

        String error = userService.updateProfile(user);
        if (error != null) {
            copyEditableFieldsToCurrentProfile(userService.getProfile(userId), user, request);
            request.setAttribute("profileError", error);
            request.getRequestDispatcher("/WEB-INF/views/profile/profile.jsp")
                    .forward(request, response);
            return;
        }

        User updatedProfile = userService.getProfile(userId);
        updateSessionProfile(session, updatedProfile);
        request.setAttribute("user", updatedProfile);
        request.setAttribute("profileSuccess", "Profile updated successfully.");
        request.getRequestDispatcher("/WEB-INF/views/profile/profile.jsp")
                .forward(request, response);
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private void copyEditableFieldsToCurrentProfile(User currentProfile, User submittedProfile,
                                                    HttpServletRequest request) {
        if (currentProfile == null) {
            currentProfile = submittedProfile;
        } else {
            currentProfile.setPhone(submittedProfile.getPhone());
            currentProfile.setGender(submittedProfile.getGender());
            currentProfile.setDateOfBirth(submittedProfile.getDateOfBirth());
            currentProfile.setAddress(submittedProfile.getAddress());
            currentProfile.setAvatarUrl(submittedProfile.getAvatarUrl());
        }
        request.setAttribute("user", currentProfile);
    }

    private void updateSessionProfile(HttpSession session, User updatedProfile) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || updatedProfile == null) {
            return;
        }

        currentUser.setPhone(updatedProfile.getPhone());
        currentUser.setGender(updatedProfile.getGender());
        currentUser.setDateOfBirth(updatedProfile.getDateOfBirth());
        currentUser.setAddress(updatedProfile.getAddress());
        currentUser.setAvatarUrl(updatedProfile.getAvatarUrl());
        session.setAttribute("currentUser", currentUser);
    }
}
