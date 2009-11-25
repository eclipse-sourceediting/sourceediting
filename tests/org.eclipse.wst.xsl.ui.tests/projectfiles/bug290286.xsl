<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:abc="http://test.com/abc">

	<xsl:template name="abc:test">
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:function name="abc:test2">
		<xsl:param name="value" />
		<xsl:value-of select="$value" />
	</xsl:function>

	<xsl:function name="abc:test3">
		<xsl:param name="value" />
		<xsl:value-of select="$value" />
	</xsl:function>

</xsl:stylesheet>