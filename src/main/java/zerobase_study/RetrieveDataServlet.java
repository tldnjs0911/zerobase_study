package zerobase_study;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@WebServlet("/retrieveDataFromDB")
public class RetrieveDataServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:sqlite:C:/Users/Administrator/Desktop/wifiInfo.db";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Connection conn = null;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);

            
            double lat = Double.parseDouble(request.getParameter("lat"));
            double lng = Double.parseDouble(request.getParameter("lng"));

            
            JSONArray wifiInfo = retrieveDataFromDatabase(conn, lat, lng);

            
            response.getWriter().println(wifiInfo.toJSONString());
        } catch (ClassNotFoundException | SQLException | IOException e) {
            Logger.getLogger(RetrieveDataServlet.class.getName()).log(Level.SEVERE, null, e);
            JSONObject errorObject = new JSONObject();
            errorObject.put("error", "Error retrieving data from the database.");
            response.getWriter().println(errorObject.toJSONString());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    Logger.getLogger(RetrieveDataServlet.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }


    private JSONArray retrieveDataFromDatabase(Connection conn, double lat, double lng) throws SQLException {
        JSONArray wifiInfo = new JSONArray();

        String selectQuery = "SELECT *, (LAT - ?) * (LAT - ?) + (LNT - ?) * (LNT - ?) AS distance " +
                             "FROM wifiInfo " +
                             "ORDER BY distance " +
                             "LIMIT 20";
        try (PreparedStatement statement = conn.prepareStatement(selectQuery)) {
            statement.setDouble(1, lat);
            statement.setDouble(2, lat);
            statement.setDouble(3, lng);
            statement.setDouble(4, lng);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    JSONObject wifi = new JSONObject();
                    wifi.put("관리번호", resultSet.getString("X_SWIFI_MGR_NO"));
                    wifi.put("자치구", resultSet.getString("X_SWIFI_WRDOFC"));
                    wifi.put("와이파이명", resultSet.getString("X_SWIFI_MAIN_NM"));
                    wifi.put("도로명주소", resultSet.getString("X_SWIFI_ADRES1"));
                    wifi.put("상세주소", resultSet.getString("X_SWIFI_ADRES2"));
                    wifi.put("설치위치", resultSet.getString("X_SWIFI_INSTL_FLOOR"));
                    wifi.put("설치유형", resultSet.getString("X_SWIFI_INSTL_TY"));
                    wifi.put("설치기관", resultSet.getString("X_SWIFI_INSTL_MBY"));
                    wifi.put("서비스구분", resultSet.getString("X_SWIFI_SVC_SE"));
                    wifi.put("망종류", resultSet.getString("X_SWIFI_CMCWR"));
                    wifi.put("설치년도", resultSet.getString("X_SWIFI_CNSTC_YEAR"));
                    wifi.put("실내외구분", resultSet.getString("X_SWIFI_INOUT_DOOR"));
                    wifi.put("WIFI접속환경", resultSet.getString("X_SWIFI_REMARS3"));
                    wifi.put("X좌표", resultSet.getString("LAT"));
                    wifi.put("Y좌표", resultSet.getString("LNT"));
                    wifi.put("작업일자", resultSet.getString("WORK_DTTM"));

                    wifiInfo.add(wifi);
                }
            }
        }

        return wifiInfo;
    }
}