<?xml version="1.0" encoding="UTF-8"?>
<!-- /******************************************************************************* 
   * Copyright (c) 2008 Standards for Technology in Automotive Retail and others. 
   * All rights reserved. This program and the accompanying materials * are 
   made available under the terms of the Eclipse Public License v1.0 * which 
   accompanies this distribution, and is available at * http://www.eclipse.org/legal/epl-v10.html 
   * * Contributors: * David Carver - STAR - Stylesheet for testing purposes. 
   *******************************************************************************/ -->
<xsl:stylesheet version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:import href="utils.xsl" />
   <xsl:template match="/">
      <xsl:call-template name="">
         <xsl:with-param name="date">
            20080930
         </xsl:with-param>
      </xsl:call-template>
   </xsl:template>
</xsl:stylesheet>
