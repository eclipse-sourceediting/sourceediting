<?xml version="1.0" encoding="UTF-8"?>
<!-- ******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************  -->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:template name="existsInlib1Only">
		<xsl:param name="p1"></xsl:param>
		<literal>Hello World</literal>
	</xsl:template>
	
	<xsl:template name="existsInStyle1AndLib1">
		<xsl:param name="p1"></xsl:param>
		<literal>Hello World</literal>
	</xsl:template>
	
	<xsl:template name="existsInLib2">
		<xsl:param name="p1"></xsl:param>
		<literal>Hello World</literal>
	</xsl:template>
	
</xsl:stylesheet>