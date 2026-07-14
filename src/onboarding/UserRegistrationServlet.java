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

@WebServlet("/UserRegistrationServlet")
public class UserRegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        String username = request.getParameter("username") != null
                ? request.getParameter("username").trim()
                : "";

        String password = request.getParameter("password") != null
                ? request.getParameter("password").trim()
                : "";

        String confirmPassword = request.getParameter("confirmPassword") != null
                ? request.getParameter("confirmPassword").trim()
                : "";

        // 🔹 Get existing session only (do NOT create new)
        HttpSession session = request.getSession(false);
        String empId = null;

        if (session != null) {
            empId = (String) session.getAttribute("emp_id");
        }

        // 🔴 emp_id must exist
        if (empId == null) {
            response.getWriter().println(
                "<script>alert('Employee not logged in or session expired. Please login again');" +
                "window.location='index.html';</script>"
            );
            return;
        }

        try (Connection con = Dbconn.getconnection()) {

            // 1️⃣ Password match check
            if (!password.equals(confirmPassword)) {
                response.getWriter().println(
                    "<script>alert('Passwords do not match');window.history.back();</script>"
                );
                return;
            }

            // 2️⃣ Password validation
            String pattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{6,20}$";
            if (!password.matches(pattern)) {
                response.getWriter().println(
                    "<script>alert('Password must contain uppercase, number & special character');" +
                    "window.history.back();</script>"
                );
                return;
            }

            // 🔴 NEW PART: Check emp_id already has credentials
            try (PreparedStatement checkEmp =
                    con.prepareStatement(
                        "SELECT 1 FROM user_credentials WHERE emp_id = ?")) {

                checkEmp.setString(1, empId);
                ResultSet rsEmp = checkEmp.executeQuery();

                if (rsEmp.next()) {
                    response.getWriter().println(
                        "<script>alert('This employee already created username and password');" +
                        "window.location='module2_initial_login.jsp';</script>"
                    );
                    return;
                }
            }

            // 3️⃣ Check username already exists
            try (PreparedStatement checkUser =
                    con.prepareStatement(
                        "SELECT 1 FROM user_credentials WHERE username = ?")) {

                checkUser.setString(1, username);
                ResultSet rs = checkUser.executeQuery();

                if (rs.next()) {
                    response.getWriter().println(
                        "<script>alert('Username already exists');" +
                        "window.history.back();</script>"
                    );
                    return;
                }
            }

            // 4️⃣ Insert user credentials
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO user_credentials (emp_id, username, password_hash) " +
                    "VALUES (?, ?, ?)")) {

                ps.setString(1, empId);
                ps.setString(2, username);
                ps.setString(3, password); // plain password (learning phase)

                ps.executeUpdate();
            }

            // 5️⃣ Success
            response.getWriter().println(
                "<script>alert('User Registered Successfully');" +
                "window.location='module2_home_page.html';</script>"
            );

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println(
                "<script>alert('Error: " + e.getMessage() + "');" +
                "window.history.back();</script>"
            );
        }
    }
}
