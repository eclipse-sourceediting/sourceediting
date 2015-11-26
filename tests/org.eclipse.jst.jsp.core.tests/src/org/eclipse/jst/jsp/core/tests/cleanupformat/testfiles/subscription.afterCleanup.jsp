<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="/WEB-INF/app.tld" prefix="app"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<app:checkLogon />

<%-- In real life, these would be loaded from a database --%>
<%
  java.util.ArrayList list = new java.util.ArrayList();
  list.add(new org.apache.struts.util.LabelValueBean("IMAP Protocol", "imap"));
  list.add(new org.apache.struts.util.LabelValueBean("POP3 Protocol", "pop3"));
  pageContext.setAttribute("serverTypes", list);
%>

<html:html>
<HEAD>
<logic:equal name="subscriptionForm" property="action" scope="request"
	value="Create">
	<TITLE><bean:message key="subscription.title.create" /></TITLE>
</logic:equal>
<logic:equal name="subscriptionForm" property="action" scope="request"
	value="Delete">
	<TITLE><bean:message key="subscription.title.delete" /></TITLE>
</logic:equal>
<logic:equal name="subscriptionForm" property="action" scope="request"
	value="Edit">
	<TITLE><bean:message key="subscription.title.edit" /></TITLE>
</logic:equal>
<html:base />
</HEAD>
<BODY bgcolor="white">

	<html:errors />

	<html:form action="/saveSubscription" focus="host">
		<html:hidden property="action" />
		<TABLE border="0" width="100%">

			<TR>
				<TH align="right"><bean:message key="prompt.username" />:</TH>
				<TD align="left"><bean:write name="user" property="username"
						filter="true" /></TD>
			</TR>

			<TR>
				<TH align="right"><bean:message key="prompt.mailHostname" />:</TH>
				<TD align="left"><logic:equal name="subscriptionForm"
						property="action" scope="request" value="Create">
						<html:text property="host" size="50" />
					</logic:equal> <logic:notEqual name="subscriptionForm" property="action"
						scope="request" value="Create">
						<html:hidden property="host" write="true" />
					</logic:notEqual></TD>
			</TR>

			<TR>
				<TH align="right"><bean:message key="prompt.mailUsername" />:</TH>
				<TD align="left"><html:text property="username" size="50" /></TD>
			</TR>

			<TR>
				<TH align="right"><bean:message key="prompt.mailPassword" />:</TH>
				<TD align="left"><html:password property="password" size="50" />
				</TD>
			</TR>

			<TR>
				<TH align="right"><bean:message key="prompt.mailServerType" />:
				</TH>
				<TD align="left"><html:select property="type">
						<html:options collection="serverTypes" property="value"
							labelProperty="label" />
					</html:select></TD>
			</TR>

			<TR>
				<TH align="right"><bean:message key="prompt.autoConnect" />:</TH>
				<TD align="left"><html:checkbox property="autoConnect" /></TD>
			</TR>

			<TR>
				<TD align="right"><logic:equal name="subscriptionForm"
						property="action" scope="request" value="Create">
						<html:submit>
							<bean:message key="button.save" />
						</html:submit>
					</logic:equal> <logic:equal name="subscriptionForm" property="action"
						scope="request" value="Delete">
						<html:submit>
							<bean:message key="button.confirm" />
						</html:submit>
					</logic:equal> <logic:equal name="subscriptionForm" property="action"
						scope="request" value="Edit">
						<html:submit>
							<bean:message key="button.save" />
						</html:submit>
					</logic:equal></TD>
				<TD align="left"><logic:notEqual name="subscriptionForm"
						property="action" scope="request" value="Delete">
						<html:reset>
							<bean:message key="button.reset" />
						</html:reset>
					</logic:notEqual> &nbsp; <html:cancel>
						<bean:message key="button.cancel" />
					</html:cancel></TD>
			</TR>

		</TABLE>

	</html:form>

</BODY>
</html:html>
