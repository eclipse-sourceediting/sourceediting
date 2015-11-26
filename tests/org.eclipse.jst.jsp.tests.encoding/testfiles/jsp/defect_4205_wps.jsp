<%@ page session="false" buffer="none" %>
<%@ page language="java" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="/WEB-INF/tld/engine.tld" prefix="wps" %>
<wps:constants/>

<table>
<tr><td><img alt="" title="" src='<%= wpsBaseURL %>/images/welcome_5.gif' border="0" align="left" /></td></tr>
<tr><td>
<span class="wpsTinyText"><b><%= Version.SERVER_NAME %> <%= Version.SERVER_VERSION %></b><br/>
建置層次：<%= Version.BUILD_NUMBER %> <%= Version.BUILD_DATE %> <%= Version.BUILD_TIME %><br/>
<br/>
Licensed Materials - Property of IBM<br/>
<%= Version.PRODUCT_NUMBER %><br/>
(C) Copyright IBM Corp. <%= Copyright.YEARS %> All Rights Reserved.</span></td></tr>
</table>
