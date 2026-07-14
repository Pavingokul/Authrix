package Enforcement;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbconnection.Dbconn;

@WebServlet("/UserAccessServlet")
public class UserAccessServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String appUserId = req.getParameter("app_user_id");

        try {
            Connection con = Dbconn.getconnection();

            PreparedStatement psCheck = con.prepareStatement(
                "SELECT status FROM manager_approval_log WHERE app_user_id=?"
            );
            psCheck.setString(1, appUserId);
            ResultSet rs = psCheck.executeQuery();

            if (!rs.next() || !"READY".equals(rs.getString("status"))) {
                res.sendRedirect("module4_privilleged_access_user_entry.jsp?error=notready");
                return;
            }

            PreparedStatement psUpdate = con.prepareStatement(
                "UPDATE manager_approval_log " +
                "SET status='ACTIVE', access_start_time=NOW() " +
                "WHERE app_user_id=?"
            );
            psUpdate.setString(1, appUserId);
            psUpdate.executeUpdate();

            req.getSession().setAttribute("app_user_id", appUserId);
            res.sendRedirect("module4_demo_dashboard.jsp");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
