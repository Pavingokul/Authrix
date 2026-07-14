package Enforcement;

import java.io.IOException;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import dbconnection.Dbconn;

@WebServlet("/ReadyAccessServlet")
public class ReadyAccessServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String appUserId = request.getParameter("app_user_id");

        try {
            Connection con = Dbconn.getconnection();

            PreparedStatement ps1 = con.prepareStatement(
                "UPDATE manager_approval_log SET status='READY' WHERE app_user_id=?"
            );
            ps1.setString(1, appUserId);
            ps1.executeUpdate();

            PreparedStatement ps2 = con.prepareStatement(
                "SELECT emp_id, emp_name, emp_designation, resource_code, access_type " +
                "FROM manager_approval_log WHERE app_user_id=?"
            );
            ps2.setString(1, appUserId);

            ResultSet rs = ps2.executeQuery();

            if (rs.next()) {
                request.setAttribute("appUserId", appUserId);
                request.setAttribute("empId", rs.getString("emp_id"));
                request.setAttribute("empName", rs.getString("emp_name"));
                request.setAttribute("designation", rs.getString("emp_designation"));
                request.setAttribute("resource", rs.getString("resource_code"));
                request.setAttribute("accessType", rs.getString("access_type"));
            }

            request.getRequestDispatcher("module4_success.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
