<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>위치 히스토리 목록</title>
</head>
<body>
    <h1>위치 히스토리 목록</h1>
    <div>
        <a href="mainPage.jsp">홈</a>
        <a href="history.jsp">위치 히스토리 목록</a>
        <a href="#" onclick="loadData()">Open API 와이파이 정보 가져오기</a>
    </div>

    <style>
        table {
            border-collapse: collapse;
            width: 100%;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            font-size: 12px;
        }

        th {
            background-color: green;
            color: white;
        }

        #loadingMessage {
            font-weight: bold;
            text-align: center;
        }
    </style>

    <table id="tableBody">
        <thead>
            <tr>
                <th>ID</th>
                <th>X좌표</th>
                <th>Y좌표</th>
                <th>조회일자</th>
                <th>비고</th>
            </tr>
        </thead>
        <tbody>
            <% 
                Connection connection = null;
                Statement statement = null;
                ResultSet resultSet = null;
                try {
                    
                    String url = "jdbc:sqlite:C:/Users/Administrator/Desktop/history.db";

                   
                    Class.forName("org.sqlite.JDBC");

                    
                    connection = DriverManager.getConnection(url);

                    
                    String sql = "SELECT * FROM location";
                    statement = connection.createStatement();

                   
                    resultSet = statement.executeQuery(sql);

                    
                    int rowCount = 1;
                    while (resultSet.next()) {
                        String latitude = resultSet.getString("latitude");
                        String longitude = resultSet.getString("longitude");
                        String dateTime = resultSet.getString("datetime");

                        out.println("<tr>");
                        out.println("<td>" + rowCount++ + "</td>");
                        out.println("<td>" + latitude + "</td>");
                        out.println("<td>" + longitude + "</td>");
                        out.println("<td>" + dateTime + "</td>");
                        out.println("<td><button onclick=\"deleteRow(this)\">Delete</button></td>");
                        out.println("</tr>");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    
                    if (resultSet != null) {
                        try {
                            resultSet.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            %>
        </tbody>
    </table>

   <script>
    function deleteRow(button) {
        var row = button.parentNode.parentNode;
        var index = row.rowIndex; // Get the index of the row

        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                
                row.parentNode.removeChild(row);
            }
        };
        xhr.open("POST", "deleteLocation.jsp", true);
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xhr.send("index=" + index); // Pass the index to deleteLocation.jsp
    }
</script>
</body>
</html>