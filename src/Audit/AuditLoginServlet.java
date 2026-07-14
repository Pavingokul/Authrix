package Audit;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

@WebServlet("/AuditLoginServlet")
public class AuditLoginServlet extends HttpServlet {

    private static final String AUDIT_EMAIL = "audit@gmail.com";
    private static final String AUDIT_PASSWORD = "Audit@123";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (AUDIT_EMAIL.equals(email) && AUDIT_PASSWORD.equals(password)) {

            HttpSession session = request.getSession();
            session.setAttribute("auditUser", email);

            response.sendRedirect("module5_home_page.html");

        } else {
            request.setAttribute("errorMsg",
                "Invalid audit credentials. Access denied.");
            request.getRequestDispatcher("module5_audit_login.jsp")
                   .forward(request, response);
        }
    }
}