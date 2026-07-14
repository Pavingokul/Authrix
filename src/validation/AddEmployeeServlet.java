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

@WebServlet("/AddEmployeeServlet")
public class AddEmployeeServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String empId = req.getParameter("emp_id");
        String empName = req.getParameter("emp_name");
        String email = req.getParameter("email");
        String department = req.getParameter("department");
        String designation = req.getParameter("designation");
        String status = req.getParameter("status");
        String joinDate = req.getParameter("join_date");

        try {
            Connection con = Dbconn.getconnection();

            String sql =
                "INSERT INTO employee_master " +
                "(emp_id, emp_name, email, department, designation, status, join_date) " +
                "VALUES (?,?,?,?,?,?,?)";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, empId);
            ps.setString(2, empName);
            ps.setString(3, email);
            ps.setString(4, department);
            ps.setString(5, designation);
            ps.setString(6, status);
            ps.setString(7, joinDate); // yyyy-mm-dd

            ps.executeUpdate();
            con.close();

            res.sendRedirect("admin_add_newemployee_success.jsp?msg=success");

        } catch (Exception e) {
            e.printStackTrace();
            res.sendRedirect("admin_add_newemployee_error.jsp?msg=error");
        }
    }
}
