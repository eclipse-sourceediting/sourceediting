<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
I found this change

<%@ page
import="SelColBeanRow12ViewBean"
 pageEncoding="SHIFT_JIS"
 contentType="text/html; charset=JUNK"
%>
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=METAJUNK">
<META name="GENERATOR" content="IBM WebSphere Studio">
<TITLE>Results page</TITLE>

<!--Styles-->
<STYLE TYPE="text/css">
<!--
BODY {
	background-color: #ffffff !important;
}
H1 {
	color: #0000ff !important;
	text-align: center !important;
}
TH {
	text-align:left !important;
	color: #000000 !important;
	vertical-align: top !important;
}
TD {
	text-align:left !important;
	vertical-align: top !important;
}
TH.result {
	background-color: #999999 !important;
}
TD.result {
	background-color: #cccccc;
	vertical-align: top !important;
}
-->
</STYLE>


<!--Style Sheet-->
<LINK href="/t/theme/Master.css" rel="stylesheet" type="text/css">
</head>
<body>

<jsp:useBean id="selColBeanRow12Bean" scope="session" class="SelColBeanRow12ViewBean" type="SelColBeanRow12ViewBean"/>
<%















%>

<%
//Execute Bean Methods


%>

<!--Banner-->
<H1>Results page</H1>

<BR><BR>

<!-- Result Table -->
<TABLE border="0">
<TBODY>
			<TR>
			<TH>TEST2_‹‹‹</TH>
			<TD>

	<%=selColBeanRow12Bean.getTEST2_‹‹‹()== null ? "NULL" : selColBeanRow12Bean.getTEST2_‹‹‹().toString()%>
			</TD>
		</TR>
		<TR>
			<TH>TEST2_Š‰</TH>
			<TD>

	<%=selColBeanRow12Bean.getTEST2_Š‰()== null ? "NULL" : selColBeanRow12Bean.getTEST2_Š‰().toString()%>
			</TD>
		</TR>
		<TR>
			<TH>TEST2_‰‰‰</TH>
			<TD>

	<%=selColBeanRow12Bean.getTEST2_‰‰‰()== null ? "NULL" : selColBeanRow12Bean.getTEST2_‰‰‰().toString()%>
			</TD>
		</TR>
		<TR>
			<TH>TEST2_\Z_</TH>
			<TD>

	<%=selColBeanRow12Bean.getTEST2_\Z_()== null ? "NULL" : selColBeanRow12Bean.getTEST2_\Z_().toString()%>
			</TD>
		</TR>
		<TR>
			<TH>TEST2_</TH>
			<TD>

	<%=selColBeanRow12Bean.getTEST2_()== null ? "NULL" : selColBeanRow12Bean.getTEST2_().toString()%>
			</TD>
		</TR>

</TBODY>
</TABLE >
</body>
</html>
