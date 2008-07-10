<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://nothing.com/libtags" prefix="libtags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Insert title here</title>
</head>
<body>
<libtags:emptyme></libtags:emptyme>
<jsp:attribute name="named"></jsp:attribute>
<libtags:ireqattrs><jsp:attribute name="name"></jsp:attribute></libtags:ireqattrs>
<libtags:ireqattrs name="<%= "named"%>" scope="<%= "expression" %>"></libtags:ireqattrs>
<libtags:ihaveattrs name="boo"></libtags:ihaveattrs>
</body>
</html>