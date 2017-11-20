<%@ taglib prefix="logic" uri="http://jakarta.apache.org/struts/tags-logic"%>
<%! int alpha = 5; %>
<%@ taglib prefix="bean" uri="http://jakarta.apache.org/struts/tags-bean"%>
<%  %>

<bean:define id="beta"/>
 
	<%= alpha + beta %>
