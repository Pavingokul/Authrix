package validation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dbconnection.Dbconn;

@WebServlet("/employeeUpload")
public class EmployeeDatasetUpload extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String CSV_PATH =
    		"C:\\Users\\Pavingokul\\Downloads\\Authrix source code\\Authrix\\WebContent\\datasheet\\employees_list.csv";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {

        response.setContentType("text/html");

        try (Connection con = Dbconn.getconnection()) {

            // 1️⃣ CHECK TABLE EMPTY
            PreparedStatement checkStmt =
                con.prepareStatement("SELECT COUNT(*) FROM employee_master");
            ResultSet rs = checkStmt.executeQuery();
            rs.next();

            if (rs.getInt(1) > 0) {
                response.getWriter().println(
                    "<script>alert('Employee dataset already uploaded!');" +
                    "window.location='admin_home_page.html';</script>"
                );
                return;
            }

            // 2️⃣ INSERT QUERY
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO employee_master " +
                "(emp_id, emp_name, email, department, designation, status, join_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)"
            );

            BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(CSV_PATH),
                    StandardCharsets.UTF_8
                )
            );

            String line;
            boolean isHeader = true;

            // ✅ DATE FORMAT OF CSV
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            while ((line = br.readLine()) != null) {

                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] data = line.split(",", -1);

                if (data.length < 7) continue;

                ps.setString(1, data[0].trim());
                ps.setString(2, data[1].trim());
                ps.setString(3, data[2].trim());
                ps.setString(4, data[3].trim());
                ps.setString(5, data[4].trim());
                ps.setString(6, data[5].trim());

                // ✅ CORRECT DATE CONVERSION
                java.util.Date utilDate = sdf.parse(data[6].trim());
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                ps.setDate(7, sqlDate);

                ps.addBatch();
            }

            ps.executeBatch();
            br.close();

            response.getWriter().println(
                "<script>alert('Employee dataset uploaded successfully!');" +
                "window.location='admin_home_page.html';</script>"
            );

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println(
                "<script>alert('Upload Failed: " + e.getMessage() + "');</script>"
            );
        }
    }
}
