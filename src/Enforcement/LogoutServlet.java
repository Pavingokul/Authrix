package Enforcement;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dbconnection.Dbconn;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        String appUserId = null;

        if (session != null) {
            appUserId = (String) session.getAttribute("app_user_id");
        }

        try {
            if (appUserId != null) {
                Connection con = Dbconn.getconnection();

                PreparedStatement ps = con.prepareStatement(
                    "UPDATE manager_approval_log " +
                    "SET status='EXPIRED', access_end_time=NOW() " +
                    "WHERE app_user_id=?"
                );
                ps.setString(1, appUserId);
                ps.executeUpdate();
            }

            if (session != null) {
                session.invalidate();
            }

            res.sendRedirect("module4_access_expired.jsp");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
