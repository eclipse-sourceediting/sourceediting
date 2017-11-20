<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:variable name="somveValue">1234</xsl:variable>
	<xsl:template match="/">
         <xsl:if test="$someValue">
            <xsl:apply-templates />
         </xsl:if>
	</xsl:template>
</xsl:stylesheet>