package zerobase_study;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class jsonParse {
    public static void main(String[] args) {
        String key = "725443484c746c64353747757a5256";
        String result = "";
        
        

        try {
            String apiURL = "http://openapi.seoul.go.kr:8088/" + key + "/json/TbPublicWifiInfo/1/20/";
            URL url = new URL(apiURL);
            BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            result = bf.readLine();

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
            JSONObject wifiInfoResult = (JSONObject) jsonObject.get("TbPublicWifiInfo");
            JSONArray wifiInfo = (JSONArray) wifiInfoResult.get("row");

            Connection connection = null;
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:wifiInfo.db");
                System.out.println("DB 연결 성공");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String createTableQuery = "CREATE TABLE IF NOT EXISTS wifiInfo (" +
                    "mgr_no TEXT, wrdofc TEXT, main_nm TEXT, adres1 TEXT, adres2 TEXT, " +
                    "instl_floor TEXT, instl_ty TEXT, instl_mby TEXT, svc_se TEXT, " +
                    "cmcwr TEXT, cnstc_year TEXT, inout_door TEXT, remars3 TEXT, " +
                    "lat TEXT, lnt TEXT, work_dttm TEXT)";

            try {
                connection.createStatement().execute(createTableQuery);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String insertQuery = "INSERT INTO wifiInfo VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                for (int i = 0; i < wifiInfo.size(); i++) {
                    JSONObject json = (JSONObject) wifiInfo.get(i);
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
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < wifiInfo.size(); i++) {
                JSONObject tmp = (JSONObject) wifiInfo.get(i);
                sb.append("관리번호 : ").append(tmp.get("X_SWIFI_MGR_NO")).append("\n");
                sb.append("자치구 : ").append(tmp.get("X_SWIFI_WRDOFC")).append("\n");
                sb.append("와이파이명 : ").append(tmp.get("X_SWIFI_MAIN_NM")).append("\n");
                sb.append("도로명주소 : ").append(tmp.get("X_SWIFI_ADRES1")).append("\n");
                sb.append("상세주소 : ").append(tmp.get("X_SWIFI_ADRES2")).append("\n");
                sb.append("설치위치(층) : ").append(tmp.get("X_SWIFI_INSTL_FLOOR")).append("\n");
                sb.append("설치유형 : ").append(tmp.get("X_SWIFI_INSTL_TY")).append("\n");
                sb.append("설치기관 : ").append(tmp.get("X_SWIFI_INSTL_MBY")).append("\n");
                sb.append("서비스구분 : ").append(tmp.get("X_SWIFI_SVC_SE")).append("\n");
                sb.append("망종류 : ").append(tmp.get("X_SWIFI_CMCWR")).append("\n");
                sb.append("설치년도 : ").append(tmp.get("X_SWIFI_CNSTC_YEAR")).append("\n");
                sb.append("실내외구분 : ").append(tmp.get("X_SWIFI_INOUT_DOOR")).append("\n");
                sb.append("wifi접속환경 : ").append(tmp.get("X_SWIFI_REMARS3")).append("\n");
                sb.append("Y좌표 : ").append(tmp.get("LAT")).append("\n");
                sb.append("X좌표 : ").append(tmp.get("LNT")).append("\n");
                sb.append("작업일자 : ").append(tmp.get("WORK_DTTM")).append("\n");
            }
            System.out.println(sb.toString());

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
