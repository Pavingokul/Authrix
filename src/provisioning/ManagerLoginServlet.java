package provisioning;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/ManagerLoginServlet")
public class ManagerLoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        String role = null;
        String name = null;

        if ("manager@gmail.com".equals(email) && "man@123".equals(password)) {
            role = "MANAGER";
            name = "Ramesh Kumar, Mahesh Kumar, Suresh Kumar ";
        }
        

        if (role != null) {
            HttpSession session = req.getSession();
            session.setAttribute("managerRole", role);
            session.setAttribute("managerName", name);
            session.setAttribute("managerEmail", email);

            res.sendRedirect("module3_home_page.html");
        } 
        	else {

        	    res.setContentType("text/html");

        	    res.getWriter().println("<script>");
        	    res.getWriter().println("alert('Invalid Email or Password');");
        	    res.getWriter().println("window.location='module3_manager_login.jsp';");
        	    res.getWriter().println("</script>");
        	}

        }
    }

