<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%-- jsf:codeBehind language="java" location="/JavaSource/pagecode/test/Kaeru5.java" --%><%-- /jsf:codeBehind --%>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@taglib uri="http://www.ibm.com/jsf/html_extended" prefix="hx"%>
<HTML>
<HEAD>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page 
language="java"
contentType="text/html; charset=SHIFT_JIS"
pageEncoding="SHIFT_JIS"
%>
<META http-equiv="Content-Type" content="text/html; charset=SHIFT_JIS">
<META name="GENERATOR" content="IBM WebSphere Studio">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="../theme/Master.css" rel="stylesheet" type="text/css">
<LINK rel="stylesheet" type="text/css" href="../theme/stylesheet.css"
	title="Style">
<TITLE>kaeru.jsp</TITLE>
</HEAD>
<f:view>
	<BODY>
	<hx:scriptCollector id="scriptCollector1">
		<h:form styleClass="form" id="form1">
			承認
			承認
			承認
			<h:selectOneMenu id="Combo30" styleClass="Combo30" tabindex="26">
				<f:selectItem itemValue="tokyo" itemLabel="tokyo" />
				<f:selectItem itemValue="証券取引所" itemLabel="証券取引所" />
			</h:selectOneMenu>
			<h:selectOneMenu id="Combo31" styleClass="Combo31" tabindex="27">
				<f:selectItem itemValue="tokyo" itemLabel="tokyo" />
				<f:selectItem itemValue="kyoto" itemLabel="kyoto" />
			</h:selectOneMenu>
		</h:form>
	</hx:scriptCollector>
	</BODY>
</f:view>
</HTML>
