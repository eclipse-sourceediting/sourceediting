 <%@page import="java.util.Calendar"%>
<%@ taglib uri="/WEB-INF/input.tld" prefix="input" %>
<HTML>

<HEAD>    

</HEAD>



<BODY>

<p> 
 The problem in both cases is a missing equals sign after the percent.  However the include file=is
 highlighed instead of the actual error



  <%@ include file="commonEventHandlers.jspf"%> 
  </p>
  
 <p> 
  

 <input:text name="<% Calendar.DAY_OF_MONTH  %>" />
 </p>
 <p>
 <input:text name="<% Calendar.DAY_OF_MONTH  %>" />


  
</p>

</BODY>
</HTML>

