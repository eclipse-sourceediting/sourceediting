<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!--
 Copyright (C) 2000-2003 by The Web Services-Interoperability Organization (WS-I) and Certain of its Members. All Rights Reserved.

 Copyright (C) 2000-2003 International Business Machines Corporation

 License Information

 Use of this  WS-I Material is governed by the WS-I Sample Application License at http://www.ws-i.org/licenses/sample_license.htm.  
 By downloading these files, you agree to the terms of this license.
-->
<HTML>
<HEAD>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=WINDOWS-1252" 
    pageEncoding="WINDOWS-1252" %>
<%@ page import="shopper.Role" %>
<%@ page import="Configurator.org.ws_i.www.*" %>
<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.jsp.JspException" %>
<%@ page import="javax.xml.rpc.ServiceException" %>
<%@ page import="java.net.ConnectException" %>

<%@ page errorPage="error.jsp" %>

<META http-equiv="Content-Type" content="text/html; charset=WINDOWS-1252">
<META name="GENERATOR" content="IBM WebSphere Studio">
<META http-equiv="Content-Style-Type" content="text/css">
<LINK href="theme/Master.css" rel="stylesheet" type="text/css">
<TITLE>Configure WS-I Showcase</TITLE>
<TABLE border="0">
   <TBODY>
     <TR>
       <TD height="43" width="112">
         <IMG width="109" height="79" src="clip_image001.gif"
           v:shapes="_x0000_i1025">
       </TD>
       <TD height="43" width="638">
         <H1 align="center"><FONT COLOR="6074B0">WS-I Sample Application Showcase</FONT></H1>
       </TD>
     </TR>
   </TBODY>
</TABLE>
</HEAD>
<BODY>
<HR>
<FONT size="5" color="#6074B0" face="Arial Black">Configuration</FONT>
<P>
<FONT face="Arial">Choose an implementation for each role.  Then Place New Order.</FONT>
<P>
<jsp:useBean id="client" class="shopper.Shopper" type="shopper.Shopper"
   scope="session"></jsp:useBean>
<jsp:setProperty name="client" property="custName" param="custName" />
<jsp:setProperty name="client" property="custNumber" param="custNumber" />
<jsp:setProperty name="client" property="custAddress" param="custAddress" />
<%
    /*
     * generateNewDemoID will be set to true once the proper order
     * has been placed in the orderstatus.jsp.
     */
    String aUUID = (new java.rmi.server.UID()).toString();	// ALWAYS GEN UUID WHEN RETURN TO THIS PAGE
    client.setCustDemoID(aUUID);
    if (client.generateNewDemoID) {
        client.generateNewDemoID = false;
    }

    try {
        ConfiguratorPortType port = null;
        String selectedConfigurator = request.getParameter("selectedConfigurator");
        if ( selectedConfigurator != null) {                
            /*
             * We are here as we are in WSDK Version.
             * Need to check the previous Configurator selection. If it's been changed then
             * we need to clear all the Endpoints
             */ 
            if (client.previousConfigurator.compareTo(selectedConfigurator) != 0) {
                for (int i = Role.LoggingFacility; i < Role.totalRoles; i += Role.nextRole) {
                    client.allEndPoints[i].clear();
                    client.custSpecifiedEndPoints[i] = null;
                }
            }
            client.previousConfigurator = selectedConfigurator;

            if (selectedConfigurator.compareTo("useLocalConfigurator") == 0) {
                port = client.config.getConfigurator(new java.net.URL("http://localhost:9080/Configurator/services/Configurator"));
            } else if (selectedConfigurator.compareTo("useExternalConfigurator") == 0) {
                  port = client.config.getConfigurator(new java.net.URL("http://wsi.alphaworks.ibm.com:80/Configurator/services/Configurator"));
            } else if (selectedConfigurator.compareTo("useLocalEndPointsOnly") == 0) {
                port = null;
            } else {
                /*
                 * We should never reach here
                 */
                 Exception ex = new Exception("Unexpected selection for Configurator service");
                 throw ex;
            }
        } else {
            port = client.config.getConfigurator(new java.net.URL("http://wsi.alphaworks.ibm.com/Configurator/services/Configurator"));
        }    

        if (port != null) {
            ConfigOptionsType ret = port.getConfigurationOptions(false);
            ConfigOptionType[] configOption = ret.getConfigOption();
            /*
             * Fill up the Endpoints details, i.e EndPoint name and it's URL in
             * the TreeMaps
             */
            for (int i = 0; i < configOption.length; i++) {
                if (configOption[i].getConfigurationEndpoint().getRole().getValue().equals( Configurator.org.ws_i.www.ConfigurationEndpointRole._LoggingFacility)) {
                   client.allEndPoints[Role.LoggingFacility].put(configOption[i].getName(), configOption[i].getConfigurationEndpoint().toString());
                } else if (configOption[i].getConfigurationEndpoint().getRole().getValue().equals(Configurator.org.ws_i.www.ConfigurationEndpointRole._Retailer)) {
                   client.allEndPoints[Role.Retailer].put(configOption[i].getName(), configOption[i].getConfigurationEndpoint().toString());
                } else if (configOption[i].getConfigurationEndpoint().getRole().getValue().equals(Configurator.org.ws_i.www.ConfigurationEndpointRole._WarehouseA)) {
                   client.allEndPoints[Role.WarehouseA].put(configOption[i].getName(), configOption[i].getConfigurationEndpoint().toString());
                } else if (configOption[i].getConfigurationEndpoint().getRole().getValue().equals(Configurator.org.ws_i.www.ConfigurationEndpointRole._WarehouseB)) {
                   client.allEndPoints[Role.WarehouseB].put(configOption[i].getName(), configOption[i].getConfigurationEndpoint().toString());
                } else if (configOption[i].getConfigurationEndpoint().getRole().getValue().equals(Configurator.org.ws_i.www.ConfigurationEndpointRole._WarehouseC)) {
                   client.allEndPoints[Role.WarehouseC].put(configOption[i].getName(), configOption[i].getConfigurationEndpoint().toString());
                } else if (configOption[i].getConfigurationEndpoint().getRole().getValue().equals(Configurator.org.ws_i.www.ConfigurationEndpointRole._ManufacturerA)) {
                   client.allEndPoints[Role.ManufacturerA].put(configOption[i].getName(), configOption[i].getConfigurationEndpoint().toString());
                } else if (configOption[i].getConfigurationEndpoint().getRole().getValue().equals(Configurator.org.ws_i.www.ConfigurationEndpointRole._ManufacturerB)) {
                   client.allEndPoints[Role.ManufacturerB].put(configOption[i].getName(), configOption[i].getConfigurationEndpoint().toString());
                } else if (configOption[i].getConfigurationEndpoint().getRole().getValue().equals(Configurator.org.ws_i.www.ConfigurationEndpointRole._ManufacturerC)) {
                   client.allEndPoints[Role.ManufacturerC].put(configOption[i].getName(), configOption[i].getConfigurationEndpoint().toString());
                } 
            }
        } else {
            client.allEndPoints[Role.LoggingFacility].put("Local LoggingFacility Implementation", "http://localhost:9080/LoggingFacility/services/LoggingFacility");
            client.allEndPoints[Role.Retailer].put("Local Retailer Implementation", "http://localhost:9080/Retailer/services/Retailer");
            client.allEndPoints[Role.WarehouseA].put("Local WarehouseA Implementation", "http://localhost:9080/Warehouse/services/Warehouse");
            client.allEndPoints[Role.WarehouseB].put("Local WarehouseB Implementation", "http://localhost:9080/Warehouse/services/WarehouseB");
            client.allEndPoints[Role.WarehouseC].put("Local WarehouseC Implementation", "http://localhost:9080/Warehouse/services/WarehouseC");
            client.allEndPoints[Role.ManufacturerA].put("Local ManufacturerA Implementation", "http://localhost:9080/Manufacturer/services/Manufacturer");
            client.allEndPoints[Role.ManufacturerB].put("Local ManufacturerB Implementation", "http://localhost:9080/Manufacturer/services/ManufacturerB");
            client.allEndPoints[Role.ManufacturerC].put("Local ManufacturerC Implementation", "http://localhost:9080/Manufacturer/services/ManufacturerC");
        }
    } catch (Exception ex) {
    	if ( ex instanceof ConfiguratorFailedFault ) {
            %>
                <FONT size="4" color="#6074B0" face="Arial Black">Problem invoking Configurator Service: <%= ((ConfiguratorFailedFault)ex).getConfigError() %>.</FONT>    
            <%
    	}
        if ( ex instanceof ServiceException) {
            %>
                <FONT size="4" color="#6074B0" face="Arial Black">Problem invoking Configurator Service.</FONT>    
            <%
            throw ex;
        } else { 
            throw ex;
        }
    }
        
    
%>

<FORM action="order.jsp" method="post">
<CENTER>
<TABLE border="0" width="548" height="364">
   <TBODY>
      <TR align="left" bgcolor="#6074B0">
         <TD><FONT color="#ffffff"><b>Role</b></FONT></TD>
         <TD width="301"><FONT color="#ffffff"><b>Endpoint</b></FONT></TD>
      </TR>
      <TR align="left" bgcolor="#99CCFF">
         <TD><P><b>Retailer</b></P></TD>
         <TD width="301"><SELECT tabindex="1" name="Retailer"
            style="width: 300px">
            <%
               {
                   Set s = client.allEndPoints[Role.Retailer].keySet();
                   Iterator keys = s.iterator();
                   while (keys.hasNext()) {
                       String serviceName = (String) keys.next();
                       String serviceEndPoint = (String) client.allEndPoints[Role.Retailer].get(serviceName);
                       /*
                        * Highlight the IBM endpoint , if it is the first time, else
                        * highlight the previous selection
                        */
                       if (((client.custSpecifiedEndPoints[Role.Retailer] == null) && (serviceName.toUpperCase().indexOf("IBM") != -1))
                           ||((client.custSpecifiedEndPoints[Role.Retailer] != null) && (serviceEndPoint.compareTo(client.custSpecifiedEndPoints[Role.Retailer].toString()) == 0))) {
            %>
            <OPTION Selected><%= serviceName %></OPTION>
            <%              
                       } else {
            %>
               <OPTION><%= serviceName %></OPTION>
            <%       
                       }
                   }
               }
            %>
			</SELECT>
			</TD>
      </TR>
      <TR align="left" bgcolor="#99CCFF">
         <TD><P><b>Warehouse A (America)</b></P></TD>
         <TD width="301"><SELECT tabindex="2" name="WarehouseA"
            style="width: 300px">
            <%
               {
                   Set s = client.allEndPoints[Role.WarehouseA].keySet();
                   Iterator keys = s.iterator();
                   while (keys.hasNext()) {
                       String serviceName = (String) keys.next();
                       String serviceEndPoint = (String) client.allEndPoints[Role.WarehouseA].get(serviceName);
                       /*
                        * Highlight the IBM endpoint , if it is the first time, else
                        * highlight the previous selection
                        */

                       if (((client.custSpecifiedEndPoints[Role.WarehouseA] == null) && (serviceName.toUpperCase().indexOf("IBM") != -1))
                           ||((client.custSpecifiedEndPoints[Role.WarehouseA] != null) && (serviceEndPoint.compareTo(client.custSpecifiedEndPoints[Role.WarehouseA].toString()) == 0))) {
            %>
            <OPTION Selected><%= serviceName %></OPTION>
            <%              
                       } else {
            %>
               <OPTION><%= serviceName %></OPTION>
            <%       
                       }
                   }
               }
            %>
            </SELECT>
         </TD>
      </TR>
      <TR align="left" bgcolor="#99CCFF">
         <TD><P><b>Warehouse B (Europe)</b></P></TD>
         <TD width="301"><SELECT tabindex="3" name="WarehouseB"
            style="width: 300px">
            <%
               {
                   Set s = client.allEndPoints[Role.WarehouseB].keySet();
                   Iterator keys = s.iterator();
                   while (keys.hasNext()) {
                       String serviceName = (String) keys.next();
                       String serviceEndPoint = (String) client.allEndPoints[Role.WarehouseB].get(serviceName);
                       /*
                        * Highlight the IBM endpoint , if it is the first time, else
                        * highlight the previous selection
                        */
                       if (((client.custSpecifiedEndPoints[Role.WarehouseB] == null) && (serviceName.toUpperCase().indexOf("IBM") != -1))
                           ||((client.custSpecifiedEndPoints[Role.WarehouseB] != null) && (serviceEndPoint.compareTo(client.custSpecifiedEndPoints[Role.WarehouseB].toString()) == 0))) {
            %>
            <OPTION Selected><%= serviceName %></OPTION>
            <%              
                       } else {
            %>
               <OPTION><%= serviceName %></OPTION>
            <%       
                       }
                   }
               }
            %>
            </SELECT>
         </TD>
      </TR>
      <TR align="left" bgcolor="#99CCFF">
         <TD><P><b>Warehouse C (Asia Pacific)</b></P></TD>
         <TD width="301"><SELECT tabindex="4" name="WarehouseC"
            style="width: 300px">
            <%
               {
                   Set s = client.allEndPoints[Role.WarehouseC].keySet();
                   Iterator keys = s.iterator();
                   while (keys.hasNext()) {
                       String serviceName = (String) keys.next();
                       String serviceEndPoint = (String) client.allEndPoints[Role.WarehouseC].get(serviceName);
                       /*
                        * Highlight the IBM endpoint , if it is the first time, else
                        * highlight the previous selection
                        */
           
                       if (((client.custSpecifiedEndPoints[Role.WarehouseC] == null) && (serviceName.toUpperCase().indexOf("IBM") != -1))
                           ||((client.custSpecifiedEndPoints[Role.WarehouseC] != null) && (serviceEndPoint.compareTo(client.custSpecifiedEndPoints[Role.WarehouseC].toString()) == 0))) {
            %>
            <OPTION Selected><%= serviceName %></OPTION>
            <%              
                       } else {
            %>
               <OPTION><%= serviceName %></OPTION>
            <%       
                       }
                   }
               }
            %>
            </SELECT>
         </TD>
      </TR>
      <TR align="left" bgcolor="#99CCFF">
         <TD><P><b>Manufacturer (Brand A)</b></P></TD>
         <TD width="301"><SELECT tabindex="5" name="ManufacturerA"
            style="width: 300px">
            <%
               {
                   Set s = client.allEndPoints[Role.ManufacturerA].keySet();
                   Iterator keys = s.iterator();
                   while (keys.hasNext()) {
                       String serviceName = (String) keys.next();
                       String serviceEndPoint = (String) client.allEndPoints[Role.ManufacturerA].get(serviceName);
                       /*
                        * Highlight the IBM endpoint , if it is the first time, else
                        * highlight the previous selection
                        */
                       if (((client.custSpecifiedEndPoints[Role.ManufacturerA] == null) && (serviceName.toUpperCase().indexOf("IBM") != -1))
                           ||((client.custSpecifiedEndPoints[Role.ManufacturerA] != null) && (serviceEndPoint.compareTo(client.custSpecifiedEndPoints[Role.ManufacturerA].toString()) == 0))) {
            %>
            <OPTION Selected><%= serviceName %></OPTION>
            <%              
                       } else {
            %>
               <OPTION><%= serviceName %></OPTION>
            <%       
                       }
                   }
               }
            %>
            </SELECT>
         </TD>
      </TR>
      <TR align="left" bgcolor="#99CCFF">
         <TD><P><b>Manufacturer (Brand B)</b></P></TD>
         <TD width="301"><SELECT tabindex="6" name="ManufacturerB"
            style="width: 300px">
            <%
               {
                   Set s = client.allEndPoints[Role.ManufacturerB].keySet();
                   Iterator keys = s.iterator();
                   while (keys.hasNext()) {
                       String serviceName = (String) keys.next();
                       String serviceEndPoint = (String) client.allEndPoints[Role.ManufacturerB].get(serviceName);
                       /*
                        * Highlight the IBM endpoint , if it is the first time, else
                        * highlight the previous selection
                        */
                       if (((client.custSpecifiedEndPoints[Role.ManufacturerB] == null) && (serviceName.toUpperCase().indexOf("IBM") != -1))
                           ||((client.custSpecifiedEndPoints[Role.ManufacturerB] != null) && (serviceEndPoint.compareTo(client.custSpecifiedEndPoints[Role.ManufacturerB].toString()) == 0))) {
            %>
            <OPTION Selected><%= serviceName %></OPTION>
            <%              
                       } else {
            %>
               <OPTION><%= serviceName %></OPTION>
            <%       
                       }
                   }
               }
            %>
            </SELECT>
         </TD>
      </TR>
      <TR align="left" bgcolor="#99CCFF">
         <TD><P><b>Manufacturer (Brand C)</b></P></TD>
         <TD width="301"><SELECT tabindex="7" name="ManufacturerC"
            style="width: 300px">
            <%
               {
                   Set s = client.allEndPoints[Role.ManufacturerC].keySet();
                   Iterator keys = s.iterator();
                   while (keys.hasNext()) {
                       String serviceName = (String) keys.next();
                       String serviceEndPoint = (String) client.allEndPoints[Role.ManufacturerC].get(serviceName);
                       /*
                        * Highlight the IBM endpoint , if it is the first time, else
                        * highlight the previous selection
                        */
                       if (((client.custSpecifiedEndPoints[Role.ManufacturerC] == null) && (serviceName.toUpperCase().indexOf("IBM") != -1))
                           ||((client.custSpecifiedEndPoints[Role.ManufacturerC] != null) && (serviceEndPoint.compareTo(client.custSpecifiedEndPoints[Role.ManufacturerC].toString()) == 0))) {
            %>
            <OPTION Selected><%= serviceName %></OPTION>
            <%              
                       } else {
            %>
               <OPTION><%= serviceName %></OPTION>
            <%       
                       }
                   }
               }
            %>
            </SELECT>
         </TD>
      </TR>
      <TR align="left" bgcolor="#99CCFF">
         <TD><P><b>Logging Facility</b></P></TD>
         <TD width="301"><SELECT tabindex="8" name="LoggingFacility"
            style="width: 300px">
            <%
               {
                   Set s = client.allEndPoints[Role.LoggingFacility].keySet();
                   Iterator keys = s.iterator();
                   while (keys.hasNext()) {
                       String serviceName = (String) keys.next();
                       String serviceEndPoint = (String) client.allEndPoints[Role.LoggingFacility].get(serviceName);
                       /*
                        * Highlight the IBM endpoint , if it is the first time, else
                        * highlight the previous selection
                        */
                       if (((client.custSpecifiedEndPoints[Role.LoggingFacility] == null) && (serviceName.toUpperCase().indexOf("IBM") != -1))
                           ||((client.custSpecifiedEndPoints[Role.LoggingFacility] != null) && (serviceEndPoint.compareTo(client.custSpecifiedEndPoints[Role.LoggingFacility].toString()) == 0))) {
            %>
            <OPTION Selected><%= serviceName %></OPTION>
            <%
                       } else {
            %>
               <OPTION><%= serviceName %></OPTION>
            <%
                       }
                   }
               }
            %>
            </SELECT>
         </TD>
      </TR>
   </TBODY>
</TABLE>
<BR>
<TABLE border="0">
   <TBODY>
      <TR>
         <TD align="center"><INPUT type="image" src="NewOrderButton.jpg"></TD>
      </TR>
   </TBODY>
</TABLE>
<FONT face="Arial"> <IMG border="0" src="PlaceOrder.jpg" width="526"
	height="160"> </FONT>
<CENTER>
</FORM>

<TABLE border="0" width="100%">
   <TBODY>
      <TR>
         <TD align="right">
			<FONT face="Arial"> <IMG border="0" src="pbw.gif"> </FONT>
         </TD>
      </TR>
   </TBODY>
</TABLE>
</BODY>

</HTML>
