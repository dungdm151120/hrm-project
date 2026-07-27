package service;

import dao.PasswordResetRequestDAO;
import dao.UserDAO;
import util.DBConnection;

import java.sql.Connection;

public class PasswordResetService {
    private final UserDAO userDAO = new UserDAO();
    private final PasswordResetRequestDAO requestDAO = new PasswordResetRequestDAO();

    public boolean completeReset(
            int requestId,
            int userId,
            String passwordHash,
            int handledBy,
            String adminNote
    ) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                boolean passwordUpdated = userDAO.updatePassword(conn, userId, passwordHash);
                boolean requestApproved = requestDAO.approve(conn, requestId, handledBy, adminNote);

                if (!passwordUpdated || !requestApproved) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (Exception ignored) {
                    // Connection is closing; there is nothing else to recover.
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

