<?xml version="1.0" encoding="UTF-8"?>
<!--
 ******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - bug 243578 - initial API and implementation
 *******************************************************************************  -->
<xsl:stylesheet version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xhtml="http://www.w3c.org/1999/xhtml"
   exclude-result-prefixes="xhtml">
   
   <xsl:variable name="globalVariable">
      This is a global variable
   </xsl:variable>
   When xsl:i
   <xsl:param name="selectParam" select="'1'"/>
   <xsl:param name="contentParam">Test</xsl:param>

   <xsl:template name="func1">
      <xsl:param name="p1" select="1"/>
      <xsl:param name="p2"/>
      <literal>Hello World</literal>
   </xsl:template>
   
</xsl:stylesheet>