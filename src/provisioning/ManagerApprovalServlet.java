package provisioning;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import dbconnection.Dbconn;

@WebServlet("/ManagerApprovalServlet")
public class ManagerApprovalServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int requestId = Integer.parseInt(request.getParameter("request_id"));
        int approvedDuration = Integer.parseInt(request.getParameter("approved_duration"));
        String managerRole = request.getParameter("manager_role");
        String managerName = request.getParameter("manager_name");
        String resourceCode = request.getParameter("resource_code");

        boolean allowed = false;
        if (managerRole.equals("PRODUCTION MANAGER") &&
                (resourceCode.equals("A1") || resourceCode.equals("A2") || resourceCode.equals("A3") || resourceCode.equals("A4"))) {
            allowed = true;
        } else if (managerRole.equals("QA MANAGER") && resourceCode.equals("A7")) {
            allowed = true;
        } else if (managerRole.equals("GENERAL MANAGER") &&
                (resourceCode.equals("A5") || resourceCode.equals("A6") || resourceCode.equals("A8"))) {
            allowed = true;
        }

        if (!allowed) {
            response.getWriter().println("<script>alert('You are NOT allowed to approve this resource');history.back();</script>");
            return;
        }

        try (Connection con = Dbconn.getconnection()) {

            // Fetch request + employee details
            PreparedStatement ps1 = con.prepareStatement(
                    "SELECT ar.*, em.emp_name, em.emp_id, em.designation, em.email " +
                            "FROM access_requests ar " +
                            "JOIN user_credentials uc ON ar.app_user_id = uc.app_user_id " +
                            "JOIN employee_master em ON uc.emp_id = em.emp_id " +
                            "WHERE ar.request_id = ?"
            );
            ps1.setInt(1, requestId);
            ResultSet rs = ps1.executeQuery();

            if (!rs.next()) {
                response.getWriter().println("Invalid Request");
                return;
            }

            String appUserId = rs.getString("app_user_id");
            String empId = rs.getString("emp_id");
            String empName = rs.getString("emp_name");
            String designation = rs.getString("designation");
            String accessType = rs.getString("access_type");
            int requestedDuration = rs.getInt("duration_minutes");

            // Insert into manager_approval_log
            PreparedStatement ps2 = con.prepareStatement(
                    "INSERT INTO manager_approval_log " +
                            "(request_id, app_user_id, emp_id, emp_name, emp_designation, " +
                            "resource_code, access_type, requested_duration, approved_duration, " +
                            "manager_role, manager_name, approval_status) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"
            );
            ps2.setInt(1, requestId);
            ps2.setString(2, appUserId);
            ps2.setString(3, empId);
            ps2.setString(4, empName);
            ps2.setString(5, designation);
            ps2.setString(6, resourceCode);
            ps2.setString(7, accessType);
            ps2.setInt(8, requestedDuration);
            ps2.setInt(9, approvedDuration);
            ps2.setString(10, managerRole);
            ps2.setString(11, managerName);
            ps2.setString(12, "APPROVED");
            ps2.executeUpdate();

            // Update request status
            PreparedStatement ps3 = con.prepareStatement(
                    "UPDATE access_requests SET status='MANAGER_APPROVED' WHERE request_id=?"
            );
            ps3.setInt(1, requestId);
            ps3.executeUpdate();

            response.sendRedirect(
            	    "module3_ManagerApprovalEmail.jsp?app_user_id=" + appUserId
            	);


        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Approval Failed");
        }
    }
}
