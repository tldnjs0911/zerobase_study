package zerobase_study;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class nearestWIFI extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

       
        double lat = Double.parseDouble(request.getParameter("lat"));
        double lng = Double.parseDouble(request.getParameter("lng"));

        
        response.setContentType("application/json");

       
        String jdbcUrl = "jdbc:sqlite:wifiInfo.db";
        try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
            
            String sql = "SELECT * FROM wifi_info ORDER BY ((latitude - ?) * (latitude - ?) + (longitude - ?) * (longitude - ?)) ASC LIMIT 20";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, lat);
            stmt.setDouble(2, lat);
            stmt.setDouble(3, lng);
            stmt.setDouble(4, lng);

            
            ResultSet rs = stmt.executeQuery();
            PrintWriter out = response.getWriter();
            while (rs.next()) {
                
                String jsonData = "{\"id\":\"" + rs.getString("id") + "\", \"name\":\"" + rs.getString("name") + "\", ...}";

                
                out.println(jsonData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}