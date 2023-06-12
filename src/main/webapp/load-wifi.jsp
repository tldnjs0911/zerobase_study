<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<div style="text-align: center;">
   <% if (request.getParameter("dataLoaded") != null && Boolean.parseBoolean(request.getParameter("dataLoaded"))) { %>
            <p>23304개의 WIFI 정보를 정상적으로 저장하였습니다.</p>
    <% } %>
</div>
<div style="text-align: center;">
    <a href="mainPage.jsp">홈으로 가기</a>
</div>
<script>
</script>
</body>
</html>