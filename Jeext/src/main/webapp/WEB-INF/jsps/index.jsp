<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Jeext</title>
		<style>
			* {
			  height: 100%;
			  margin: 0;
			  padding: 0;
			  text-align: center;
			}
		</style>
	</head>
	<body>
	
		<div style="background-image: url('${pageContext.request.contextPath}/resources/night_sky.png');">
			<h1 style="color: beige; position: relative; top: 50%; height: auto">Hello ${name}!</h1>
		</div>
	
	</body>
</html>