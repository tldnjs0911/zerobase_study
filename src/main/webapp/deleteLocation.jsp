<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%
    String index = request.getParameter("index");

    
    Connection connection = null;
    PreparedStatement statement = null;
    try {
        
        String url = "jdbc:sqlite:C:/Users/Administrator/Desktop/history.db";
        
        
        Class.forName("org.sqlite.JDBC");

        
        connection = DriverManager.getConnection(url);

        
        String sql = "DELETE FROM location WHERE id IN (SELECT id FROM location ORDER BY id LIMIT 1 OFFSET ?)";
        statement = connection.prepareStatement(sql);
        statement.setString(1, index);

        
        statement.executeUpdate();

        
        statement.close();
        connection.close();

        
        response.setStatus(HttpServletResponse.SC_OK);
    } catch (Exception e) {
        e.printStackTrace();
        
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } finally {
        
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