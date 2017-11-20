<?xml version=\"1.0\" encoding=\"UTF-8\"?>
\n" +
<!-- Copyright (c) IBM Corporation and others 2009. This page is made available 
   under license. For full details see the LEGAL in the documentation book that 
   contains this page. All Platform Debug contexts, those for org.eclipse.debug.ui, 
   are located in this file All contexts are grouped by their relation, with 
   all relations grouped alphabetically. -->
<xsl:stylesheet version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="html" encoding="iso-8859-1"
      doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
      doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" />
   n" +
   <xsl:template match="/">
      <html xmlns="http://www.w3.org/1999/xhtml">
         <head>
            <title>Compare Details</title>
         </head>
         <body>
            <div align="left" class="main">
               <xsl:apply-templates select="deltas" />
            </div>
            <p>
               <a href="http://validator.w3.org/check?uri=referer">
                  <img src="http://www.w3.org/Icons/valid-xhtml10-blue"
                     alt="Valid XHTML 1.0 Strict" height="31" width="88" />
               </a>
            </p>
         </body>
      </html>
   </xsl:template>
   <xsl:template match="deltas">
      <table border="1" width="90%">
         <tr bgcolor="#CC9933">
            <td>
               <h3>
                  <a href="javascript:void(0)" class="typeslnk"
                     onclick="expand(this)">
                     // faulty source line follows
                     <b>
                        List of
                        <xsl:value-of select=>
                           Details
                     </b>
                  </a>
               </h3>
            </td>
         </tr>
         <xsl:for-each select="deltas/delta[@compatible='true']">
            <xsl:sort select="@compatible" />
            <tr>
               <td>
                  <xsl:value-of disable-output-escaping="yes"
                     select="@message" />
               </td>
            </tr>
         </xsl:for-each>
      </table>
   </xsl:template>
</xsl:stylesheet>
