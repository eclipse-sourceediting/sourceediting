<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<TITLE>Login</TITLE>
<LINK href="../theme/Master.css" rel="stylesheet" type="text/css">
</HEAD>
<BODY bgcolor="#ffffcc">
	<SCRIPT>
      function submitForm(dest){
         document.myForm.action = dest
         document.myForm.submit()
      }
      </SCRIPT>
	<CENTER>
		<H1>Database Connectivity</H1>
		<HR>
	</CENTER>
	<P>
		Enter the username &amp; password to connect to the database.<BR>
		This username &amp; password should be the one used while creating the
		database.
	</P>
	<% if(session.getAttribute("error_message") != null) { %>
	<P>* Incorrect Login or Password</P>
	<%  session.removeAttribute("error_message");
      }  %>
	<FORM name="myForm" method="POST" action="../Login/LogonServlet">
		<INPUT type="hidden" name="command"
			value='<%=session.getAttribute("final_page") %>'> <INPUT
			type="hidden" name="previous_page" value="Login/Login.jsp"> <A
			href="javascript:submitForm('../Login/LogonServlet')">OK</A>
		<TABLE border="0">
			<TBODY>
				<TR>
					<TD><FONT color="#993333"> username: </FONT></TD>
					<TD><INPUT name="userID" type="text" size="30" maxlength="40"></TD>
				</TR>
				<TR>
					<TD><FONT color="#993333"> password: </FONT></TD>
					<TD><INPUT name="password" type="password" size="30"
						maxlength="40"></TD>
				</TR>
				<TR>
					<TD><FONT color="#993333"> drivername: </FONT></TD>
					<TD><INPUT name="driver" type="text" size="30" maxlength="40"
						value="COM.ibm.db2.jdbc.app.DB2Driver"></TD>
				</TR>
				<TR>
					<TD><FONT color="#993333"> url: </FONT></TD>
					<TD><INPUT name="url" type="text" size="30" maxlength="40"
						value="jdbc:db2:WSSAMPLE"></TD>
				</TR>
			</TBODY>
		</TABLE>
	</FORM>
</BODY>
</HTML>
