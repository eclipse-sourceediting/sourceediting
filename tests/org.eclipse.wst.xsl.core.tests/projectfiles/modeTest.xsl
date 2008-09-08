<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:include href="modeTest2.xsl"/>
	<xsl:template match="/">
   
	</xsl:template>
    <xsl:template match="something" mode="mode1">
    
    </xsl:template>
    <xsl:template match="something" mode="mode2">
    
    
    </xsl:template>
    <xsl:template name="something" mode="mode1">
    
    </xsl:template>
    
</xsl:stylesheet>