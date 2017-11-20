<?xml version="1.0" encoding="UTF-8"?>
<!-- /******************************************************************************* 
   * Copyright (c) 2008 Standards for Technology in Automotive Retail and others. 
   * All rights reserved. This program and the accompanying materials * are 
   made available under the terms of the Eclipse Public License v1.0 * which 
   accompanies this distribution, and is available at * http://www.eclipse.org/legal/epl-v10.html 
   * * Contributors: * David Carver - STAR - Stylesheet for testing purposes. 
   *******************************************************************************/ -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   version="1.0">
   <xsl:param name="date" />
   <xsl:include href="utils.xsl" />
   <xsl:template match="StarTeam">
      <html>
         <head>
            <title>Change Request by Responsibility</title>
         </head>
         <body>
            <p>
               <table border="1">
                  <caption>
                     <b>Draft/Milestone Completed Change Requests</b>
                  </caption>
                  <xsl:call-template name="tableHeader" />
                  <xsl:for-each
                     select="ChangeRequest[(Status = 'Fixed' or Status = 'In Progress') and (Category = 'XML' or Category = 'DTS')]">
                     <xsl:sort select="ModifiedDate" order="descending" />
                     <xsl:sort select="AssignedTo" order="descending" />
                     <xsl:call-template name="ChangeRequest" />
                  </xsl:for-each>
               </table>
            </p>
         </body>
      </html>
   </xsl:template>
   <xsl:template name="tableHeader">
      <tr bgcolor="silver">
         <td>
            <strong>Number</strong>
         </td>
         <td>
            <strong>Creation Date</strong>
         </td>
         <td>
            <strong>Modified Date</strong>
         </td>
         <td>
            <strong>Category</strong>
         </td>
         <td>
            <strong>Component</strong>
         </td>
         <td>
            <strong>Synopsis</strong>
         </td>
         <td>
            <strong>Assigned To</strong>
         </td>
         <td>
            <strong>Status</strong>
         </td>
      </tr>
   </xsl:template>
   <xsl:template name="ChangeRequest">
      <tr>
         <td>
            <xsl:value-of select="CRNumber" />
         </td>
         <td>
            <xsl:call-template name="long_date">
               <xsl:with-param name="date" select="CreatedOnDate" />
            </xsl:call-template>
         </td>
         <td>
            <xsl:call-template name="long_date">
               <xsl:with-param name="date" select="ModifiedDate" />
            </xsl:call-template>
         </td>
         <td>
            <xsl:value-of select="Category" />
         </td>
         <td>
            <xsl:value-of select="Component" />
         </td>
         <td>
            <xsl:value-of select="Synopsis" />
         </td>
         <td>
            <xsl:value-of select="AssignedTo" />
         </td>
         <td>
            <xsl:value-of select="Status" />
         </td>
      </tr>
   </xsl:template>
</xsl:stylesheet>
