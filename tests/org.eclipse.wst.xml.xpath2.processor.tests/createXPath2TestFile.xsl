<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xqts="http://www.w3.org/2005/02/query-test-XQTSCatalog">
   <xsl:output indent="yes" method="xml" encoding="UTF-8"/>
   <xsl:variable name="location">/Queries/XQuery</xsl:variable>
   <xsl:template match="/">
      <testcases-xpath2-report>
         <xsl:apply-templates select="*/xqts:test-group"/>
      </testcases-xpath2-report>
   </xsl:template>
   
   <xsl:template match="xqts:test-group">
       <xsl:apply-templates select="xqts:test-group | xqts:test-case"/>
   </xsl:template>
   
   <xsl:template match="xqts:test-case">
      <xsl:if test="@is-XPath2 = 'true'">
         <test-case>
            <xq-file>
              <xsl:value-of select="$location"/>
              <xsl:text>/</xsl:text>
              <xsl:value-of select="@FilePath"/>
              <xsl:value-of select="@name"/>
              <xsl:text>.xq</xsl:text>
            </xq-file>
            <context-files>
               <input-context>/TestSources/<xsl:value-of select="xqts:input-file"/>.xml</input-context>
            </context-files>
            <xsl:choose>
               <xsl:when test="xqts:expected-error">
                  <expected-result><xsl:value-of select="xqts:expected-error[1]"/></expected-result>
               </xsl:when>
               <xsl:otherwise>
                  <expected-result>
                     <xsl:text>/ExpectedTestResults/</xsl:text>
                     <xsl:value-of select="@FilePath"/>
                     <xsl:value-of select="xqts:output-file"/>
                  </expected-result>
               </xsl:otherwise>
            </xsl:choose>
         </test-case>
      </xsl:if>
   </xsl:template>
</xsl:stylesheet>