package KTGK.de03.controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import KTGK.de03.dao.AdminDashboardDao_24133056;

@WebServlet({"/admin", "/admin/dashboard"})
public class AdminDashboardController_24133056 extends BaseController_24133056 {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Keep existing edit and pagination bookmarks usable.
            if (request.getParameter("edit") != null || request.getParameter("page") != null) {
                request.getRequestDispatcher("/admin/videos").forward(request, response);
                return;
            }
            request.setAttribute("dashboard", new AdminDashboardDao_24133056().load());
            request.setAttribute("adminSection", "dashboard");
            view(request, response, "admin-dashboard");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
