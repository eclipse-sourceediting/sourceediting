<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title><%!int addOne(int before) {
          return before +1;	
}
	%>
</head>
<body><p>
<%

	  String a = 
                                             "hello, world!";
	int b = 5;
	
	if (a != null && addOne(b) > 4) {
out.print(a);
	  }

%></p>
</body>
</html>