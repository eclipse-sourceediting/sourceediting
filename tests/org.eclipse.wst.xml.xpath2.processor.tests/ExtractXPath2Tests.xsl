<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xqts="http://www.w3.org/2005/02/query-test-XQTSCatalog">
   <xsl:output indent="yes" method="xml" encoding="UTF-8"/>
   <xsl:template match="/">
      <xsl:copy>
         <xsl:apply-templates/>
      </xsl:copy>
   </xsl:template>
   <xsl:template match="xqts:test-group">
      <xsl:if test="not(@is-XPath2 = 'false')">
         <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
         </xsl:copy>
      </xsl:if>
   </xsl:template>
   
   <xsl:template match="xqts:test-case">
      <xsl:if test="@is-XPath2 = 'true'">
         <xsl:choose>
            <xsl:when test="@name = 'static-context-1'"/>
            <xsl:otherwise>
               <xsl:copy>
                  <xsl:copy-of select="@*"/>
                  <xsl:apply-templates/>
               </xsl:copy>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:if>
   </xsl:template>
   <xsl:template match="*">
      <xsl:copy>
         <xsl:copy-of select="@*"/>
         <xsl:apply-templates/>
      </xsl:copy>
   </xsl:template>
</xsl:stylesheet>