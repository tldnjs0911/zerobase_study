<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%
    String lat = request.getParameter("lat");
    String lng = request.getParameter("lng");

   
    Connection connection = null;
    PreparedStatement statement = null;
    try {
        
        String url = "jdbc:sqlite:C:/Users/Administrator/Desktop/history.db";
        
        
        Class.forName("org.sqlite.JDBC");

        
        connection = DriverManager.getConnection(url);

        
        String sql = "INSERT INTO location (latitude, longitude, datetime) VALUES (?, ?, datetime('now'))";
        statement = connection.prepareStatement(sql);
        statement.setString(1, lat);
        statement.setString(2, lng);

        
        statement.executeUpdate();

        
        statement.close();
        connection.close();

        
        response.sendRedirect("history.jsp");
    } catch (Exception e) {
        e.printStackTrace();
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