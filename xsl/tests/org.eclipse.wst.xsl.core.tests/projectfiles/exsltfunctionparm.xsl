<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<func:function name="my:count-elements">
	   <xsl:param name="param"/>
	   <xsl:for-each select="(//*)[1]">
	      <func:result select="count(//*)" />
	   </xsl:for-each>
	</func:function>
</xsl:stylesheet>