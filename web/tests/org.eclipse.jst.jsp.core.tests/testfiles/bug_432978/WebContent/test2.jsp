<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://eclipse.org/testbug_432978" prefix="test" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<c:choose>
  <c:when test="true">
    <test:atbegin>
    </test:atbegin>
  </c:when>
  <c:otherwise>
    <test:atbegin>
    </test:atbegin>
  </c:otherwise>
</c:choose>

</body>
</html>