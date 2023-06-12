<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="zerobase_study.DistanceCalculator" %>
<%@ page import="java.util.*" %>
<%@ page import="zerobase_study.CustomELFunctions" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<h1>와이파이 정보 구하기</h1>

<div>
    <a href="mainPage.jsp">홈</a>
    <a href="history.jsp">위치 히스토리 목록</a>
    <a href="#" onclick="loadData()">Open API 와이파이 정보 가져오기</a>
</div>

<div>
    <span id="locationLabel">LAT: </span>
    <span id="latitude"></span>
    <span id="longitudeLabel">LNT: </span>
    <span id="longitude"></span>
</div>
<button onclick="getLocation(event)">내 위치 가져오기</button>
<button onclick="displayNearestWifiInfo(event)">근접 WIFI 정보 보기</button>

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

<table id="wifiTable">
    <thead>
        <tr>
            <th>관리번호</th>
            <th>자치구</th>
            <th>와이파이명</th>
            <th>도로명주소</th>
            <th>상세주소</th>
            <th>설치위치</th>
            <th>설치유형</th>
            <th>설치기관</th>
            <th>서비스구분</th>
            <th>망종류</th>
            <th>설치년도</th>
            <th>실내외구분</th>
            <th>WIFI접속환경</th>
            <th>X좌표</th>
            <th>Y좌표</th>
            <th>작업일자</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td colspan="16" id="loadingMessage">현재 위치를 가져온 후에 조회해 주세요.</td>
        </tr>
    </tbody>
</table>

<script>
    function loadData() {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", "loadDataFromAPI", true);
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                var response = JSON.parse(xhr.responseText);
                if (response.length > 0) {
                    displayData(response);
                    window.location.href = "load-wifi.jsp?dataLoaded=true"; // Redirect to load-wifi.jsp
                } else {
                    document.getElementById("loadingMessage").textContent = "데이터를 가져오지 못했습니다.";
                }
            }
        };
        xhr.send();
    }

    function displayData(data) {
        var tableBody = document.getElementById("wifiTable").getElementsByTagName("tbody")[0];
        tableBody.innerHTML = ""; // Clear the existing table body content

        for (var i = 0; i < data.length; i++) {
            var row = document.createElement("tr");

            var 관리번호Cell = document.createElement("td");
            관리번호Cell.textContent = data[i].관리번호;
            row.appendChild(관리번호Cell);

            var 자치구Cell = document.createElement("td");
            자치구Cell.textContent = data[i].자치구;
            row.appendChild(자치구Cell);

            var 와이파이명Cell = document.createElement("td");
            와이파이명Cell.textContent = data[i].와이파이명;
            row.appendChild(와이파이명Cell);

            var 도로명주소Cell = document.createElement("td");
            도로명주소Cell.textContent = data[i].도로명주소;
            row.appendChild(도로명주소Cell);

            var 상세주소Cell = document.createElement("td");
            상세주소Cell.textContent = data[i].상세주소;
            row.appendChild(상세주소Cell);

            var 설치위치Cell = document.createElement("td");
            설치위치Cell.textContent = data[i].설치위치;
            row.appendChild(설치위치Cell);

            var 설치유형Cell = document.createElement("td");
            설치유형Cell.textContent = data[i].설치유형;
            row.appendChild(설치유형Cell);

            var 설치기관Cell = document.createElement("td");
            설치기관Cell.textContent = data[i].설치기관;
            row.appendChild(설치기관Cell);

            var 서비스구분Cell = document.createElement("td");
            서비스구분Cell.textContent = data[i].서비스구분;
            row.appendChild(서비스구분Cell);

            var 망종류Cell = document.createElement("td");
            망종류Cell.textContent = data[i].망종류;
            row.appendChild(망종류Cell);

            var 설치년도Cell = document.createElement("td");
            설치년도Cell.textContent = data[i].설치년도;
            row.appendChild(설치년도Cell);

            var 실내외구분Cell = document.createElement("td");
            실내외구분Cell.textContent = data[i].실내외구분;
            row.appendChild(실내외구분Cell);

            var WIFI접속환경Cell = document.createElement("td");
            WIFI접속환경Cell.textContent = data[i].WIFI접속환경;
            row.appendChild(WIFI접속환경Cell);

            var X좌표Cell = document.createElement("td");
            X좌표Cell.textContent = data[i].X좌표;
            row.appendChild(X좌표Cell);

            var Y좌표Cell = document.createElement("td");
            Y좌표Cell.textContent = data[i].Y좌표;
            row.appendChild(Y좌표Cell);

            var 작업일자Cell = document.createElement("td");
            작업일자Cell.textContent = data[i].작업일자;
            row.appendChild(작업일자Cell);

            tableBody.appendChild(row);
        }
    }

    function getLocation(event) {
        event.preventDefault();

        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(saveAndDisplayLocation);
        } else {
            alert("Geolocation is not supported by this browser.");
        }
    }

    function saveAndDisplayLocation(position) {
        var latitude = position.coords.latitude;
        var longitude = position.coords.longitude;

        var url = "mainPage.jsp?lat=" + latitude + "&lng=" + longitude;

        document.getElementById("latitude").textContent = latitude;
        document.getElementById("longitude").textContent = longitude;

        window.history.replaceState({}, "", url);

        var xhr = new XMLHttpRequest();
        xhr.open("POST", "saveLocation.jsp", true);
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                console.log("Location saved successfully.");
            }
        };
        xhr.send("lat=" + latitude + "&lng=" + longitude);
    }


    function displayNearestWifiInfo(event) {
        event.preventDefault();
        navigator.geolocation.getCurrentPosition(function (position) {
            var lat = position.coords.latitude;
            var lng = position.coords.longitude;

            var tableBody = document.getElementById("wifiTable").getElementsByTagName("tbody")[0];
            var loadingMessageRow = tableBody.querySelector("#loadingMessage");
            if (loadingMessageRow && loadingMessageRow.parentNode === tableBody) {
                tableBody.removeChild(loadingMessageRow);
            }

            var xhr = new XMLHttpRequest();
            xhr.open("GET", "retrieveDataFromDB?lat=" + lat + "&lng=" + lng, true); // Replace "retrieveData" with your servlet or JSP page name
            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    var response = JSON.parse(xhr.responseText);
                    if (response.length > 0) {
                        var nearestData = getNearestData(response, lat, lng, 20);
                        displayData(nearestData);
                    } else {
                        console.log("No nearest WiFi data found.");
                    }
                }
            };
            xhr.send();
        }, function (error) {
            console.log(error);
        });
    }

    function getNearestData(data, lat, lng, count) {

        var distances = [];
        for (var i = 0; i < data.length; i++) {
            var distance = calculateDistance(lat, lng, data[i].latitude, data[i].longitude);
            distances.push({ index: i, distance: distance });
        }


        distances.sort(function (a, b) {
            return a.distance - b.distance;
        });

        var nearestData = [];
        for (var i = 0; i < count && i < distances.length; i++) {
            var index = distances[i].index;
            nearestData.push(data[index]);
        }

        return nearestData;
    }

    function calculateDistance(lat1, lon1, lat2, lon2) {
        var toRadians = Math.PI / 180;
        var earthRadius = 6371;

        var dLat = toRadians * (lat2 - lat1);
        var dLon = toRadians * (lon2 - lon1);
        var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(toRadians * lat1) * Math.cos(toRadians * lat2) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        var distance = earthRadius * c;

        return distance;
    }
</script>
</body>
</html>
