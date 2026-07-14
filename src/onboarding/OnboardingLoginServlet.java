package onboarding;

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

@WebServlet("/OnboardingLoginServlet")
public class OnboardingLoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        String empId = request.getParameter("employeeId");
        String email = request.getParameter("email");

        try (Connection con = Dbconn.getconnection()) {

            String sql =
                "SELECT emp_id, emp_name, department, designation, status " +
                "FROM employee_master " +
                "WHERE emp_id = ? AND email = ? AND status = 'ACTIVE'";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, empId);
            ps.setString(2, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // ? Employee exists in master table

                HttpSession session = request.getSession();
                session.setAttribute("emp_id", rs.getString("emp_id"));
                session.setAttribute("emp_name", rs.getString("emp_name"));
                session.setAttribute("department", rs.getString("department"));
                session.setAttribute("designation", rs.getString("designation"));

                response.sendRedirect("module2_home_page.html");

            } else {
                // ? Employee not found
                response.getWriter().println(
                    "<script>alert('You are not in the employee list. Please contact Admin.');" +
                    "window.location='index.html';</script>"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println(
                "<script>alert('Login failed. Please try again later.');</script>"
            );
        }
    }
}
