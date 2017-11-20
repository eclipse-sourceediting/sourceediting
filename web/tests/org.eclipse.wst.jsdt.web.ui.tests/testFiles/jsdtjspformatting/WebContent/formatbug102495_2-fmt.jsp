<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%!String pal = "friend";%>
<%!String message = "friend";%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<%-- jsp expression must be followed directly by ! because no whitespace exists between --%>
	Welcome to WTP,
	<%=message%>!
	<p>
		Welcome to WTP,
		<%=pal%>!
	</p>
</body>
</html>