package validation;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbconnection.Dbconn;

@WebServlet("/AdminUserActionServlet")
public class AdminUserActionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userId = request.getParameter("user_id");
        String action = request.getParameter("action");

        try (Connection con = Dbconn.getconnection()) {

            if ("approve".equals(action)) {

                PreparedStatement ps = con.prepareStatement(
                    "UPDATE user_credentials SET status='admin_approved' WHERE user_id=?"
                );
                ps.setInt(1, Integer.parseInt(userId));
                ps.executeUpdate();

                response.sendRedirect("admin_approval_user.jsp?msg=approved");

            } else if ("reject".equals(action)) {

                PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM user_credentials WHERE user_id=?"
                );
                ps.setInt(1, Integer.parseInt(userId));
                ps.executeUpdate();

                response.sendRedirect("admin_approval_user.jsp?msg=rejected");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println(
                "<script>alert('Action failed');window.history.back();</script>"
            );
        }
    }
}
