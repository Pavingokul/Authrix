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
import javax.servlet.http.HttpSession;

import dbconnection.Dbconn;

@WebServlet("/Module4LoginServlet")
public class Module4LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try (Connection con = Dbconn.getconnection()) {

            String sql =
                "SELECT mal.emp_name, mal.emp_designation, mal.resource_code, " +
                "mal.access_type, mal.approved_duration, mal.manager_name, mal.approved_at " +
                "FROM user_credentials uc " +
                "JOIN manager_approval_log mal ON uc.emp_id = mal.emp_id " +
                "WHERE uc.username = ? " +
                "AND uc.password_hash = ? " +
                "AND uc.status = 'admin_approved' " +
                "AND mal.approval_status = 'APPROVED'";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                HttpSession session = req.getSession();

                // 🔐 STORE VALUES (NOT ResultSet)
                session.setAttribute("empName", rs.getString("emp_name"));
                session.setAttribute("designation", rs.getString("emp_designation"));
                session.setAttribute("resource", rs.getString("resource_code"));
                session.setAttribute("accessType", rs.getString("access_type"));
                session.setAttribute("duration", rs.getInt("approved_duration"));
                session.setAttribute("manager", rs.getString("manager_name"));
                session.setAttribute("approvedAt", rs.getTimestamp("approved_at"));

                req.getRequestDispatcher("module4_user_details.jsp")
                   .forward(req, res);

            } else {

                req.setAttribute("errorMsg",
                        "Access denied. Manager approval required.");

                req.getRequestDispatcher("module4_login.jsp")
                   .forward(req, res);
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("errorMsg", "Internal server error");
            req.getRequestDispatcher("module4_login.jsp")
               .forward(req, res);
        }
    }
}