<%@ page language = "java" contentType = "text/html; charset=UTF-8" pageEncoding = "UTF-8" isErrorPage = "true" %>

<html>
	<head>
		<meta http-equiv = "Content-Type" content = "text/html; charset=UTF-8">
		<title>error</title>
		<style>
			<%@include file="errors-css/error.css"%>
		</style>
	</head>
	<body>
		<section class="main">
			<div class="t3d">
				<div class="chatWindow">
					<div class="box" id="messageBox"></div>
					<div class="box" id="userBox"></div>
					<div class="box" id="textBox" contenteditable="true"></div>
					<div class="loginBox">
						<button class="button" id="loginButton" type="button"></button>
					</div>
					<div class="buttonBox">
						<button class="button" id="sendButton" type="button"></button> 
					</div>
				</div>
				<div class="message">
					<span>
						<%
							String error = request.getParameter("error");
							if (error.equals("400")) {
						%>
						<%=400%>
						<%
							} else if (error.equals("404")) {
						%>
						<%=404%>
						<%
							} else if (error.equals("500")) {
						%>
						<%=500%>
						<%
							}
						%>
					</span>
				</div>
			</div>
		</section>
	</body>
</html>