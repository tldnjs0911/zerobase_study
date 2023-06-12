import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@WebServlet("/loadDataFromAPI")
public class LoadDataServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:sqlite:wifiInfo.db";
    private static final String API_URL = "http://openapi.seoul.go.kr:8088/";
    private static final String API_KEY = "725443484c746c64353747757a5256";
    private static final int MAX_INDEX = 1000;
    private static final int TOTAL_COUNT = 23304;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        Connection conn = null;

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:wifiInfo.db");
            deleteAllDataFromTable(conn);
            saveDataFromAPI(conn);

            
            response.sendRedirect("load-wifi.jsp?dataLoaded=true");
        } catch (ClassNotFoundException | SQLException | IOException e) {
            Logger.getLogger(LoadDataServlet.class.getName()).log(Level.SEVERE, null, e);
            response.getWriter().println("Error loading data from API.");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    Logger.getLogger(LoadDataServlet.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }

    private void deleteAllDataFromTable(Connection conn) throws SQLException {
        String deleteQuery = "DELETE FROM wifiInfo";
        try (PreparedStatement statement = conn.prepareStatement(deleteQuery)) {
            statement.executeUpdate();
        }
    }

    private void saveDataFromAPI(Connection conn) throws SQLException {
        int numCalls = (int) Math.ceil((double) TOTAL_COUNT / MAX_INDEX);

        
        String insertQuery = "INSERT OR IGNORE INTO wifiInfo VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(insertQuery)) {
            for (int i = 0; i < numCalls; i++) {
                int startIndex = i * MAX_INDEX + 1;
                int endIndex = (i + 1) * MAX_INDEX;

                String apiUrl = API_URL + API_KEY + "/json/TbPublicWifiInfo/" + startIndex + "/" + endIndex + "/";
                try {
                    URL url = new URL(apiUrl);
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
                        StringBuilder jsonBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            jsonBuilder.append(line);
                        }

                        JSONParser jsonParser = new JSONParser();
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonBuilder.toString());
                        JSONObject wifiInfoResult = (JSONObject) jsonObject.get("TbPublicWifiInfo");
                        JSONArray wifiInfo = (JSONArray) wifiInfoResult.get("row");

                        for (int j = 0; j < wifiInfo.size(); j++) {
                            JSONObject json = (JSONObject) wifiInfo.get(j);
                            statement.setString(1, (String) json.get("X_SWIFI_MGR_NO"));
                            statement.setString(2, (String) json.get("X_SWIFI_WRDOFC"));
                            statement.setString(3, (String) json.get("X_SWIFI_MAIN_NM"));
                            statement.setString(4, (String) json.get("X_SWIFI_ADRES1"));
                            statement.setString(5, (String) json.get("X_SWIFI_ADRES2"));
                            statement.setString(6, (String) json.get("X_SWIFI_INSTL_FLOOR"));
                            statement.setString(7, (String) json.get("X_SWIFI_INSTL_TY"));
                            statement.setString(8, (String) json.get("X_SWIFI_INSTL_MBY"));
                            statement.setString(9, (String) json.get("X_SWIFI_SVC_SE"));
                            statement.setString(10, (String) json.get("X_SWIFI_CMCWR"));
                            statement.setString(11, (String) json.get("X_SWIFI_CNSTC_YEAR"));
                            statement.setString(12, (String) json.get("X_SWIFI_INOUT_DOOR"));
                            statement.setString(13, (String) json.get("X_SWIFI_REMARS3"));
                            statement.setString(14, (String) json.get("LAT"));
                            statement.setString(15, (String) json.get("LNT"));
                            statement.setString(16, (String) json.get("WORK_DTTM"));
                            statement.addBatch();
                        }
                    }
                } catch (IOException | ParseException e) {
                    Logger.getLogger(LoadDataServlet.class.getName()).log(Level.SEVERE, null, e);
                    throw new SQLException("Error parsing JSON data.");
                }
            }

            
            statement.executeBatch();
        }
    }
}