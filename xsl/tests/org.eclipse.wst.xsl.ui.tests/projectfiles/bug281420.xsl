<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:variable name="test">
      0
   </xsl:variable>
   <xsl:template match="/">
      <xsl:value-of select="$" />
   </xsl:template>
</xsl:stylesheet>
<!-- /******************************************************************************* 
   *Copyright (c) 2009 Standards for Technology in Automotive Retail and others. 
   *All rights reserved. This program and the accompanying materials *are made 
   available under the terms of the Eclipse Public License v1.0 *which accompanies 
   this distribution, and is available at *http://www.eclipse.org/legal/epl-v10.html 
   * *Contributors: * David Carver (STAR) - bug 281420 - initial API and implementation 
   *******************************************************************************/ -->