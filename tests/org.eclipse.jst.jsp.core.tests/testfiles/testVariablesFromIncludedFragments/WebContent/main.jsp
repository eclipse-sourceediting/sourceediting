<!doctype html public "-//w3c//dtd html 3.2//en">

<%
String variableFromMainJSP = "initialized in main";
%>

<%@ include file="test_header1.jspf" %>

<%
variableFromMainJSP = "reassigned in main";
variableFromHeader1 = "reassigned in main";
%>
Value of variableFromMainJSP: "<%=variableFromMainJSP%>"
<br>
Value of variableFromHeader1: "<%=variableFromHeader1%>"
