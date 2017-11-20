<?xml version="1.0" encoding="UTF-8"?>
<!-- /******************************************************************************* 
   * Copyright (c) 2008 Standards for Technology in Automotive Retail and others. 
   * All rights reserved. This program and the accompanying materials * are 
   made available under the terms of the Eclipse Public License v1.0 * which 
   accompanies this distribution, and is available at * http://www.eclipse.org/legal/epl-v10.html 
   * * Contributors: * David Carver - STAR - Stylesheet for testing purposes. 
   *******************************************************************************/ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   version="1.0">
   <xsl:template name="long_date">
      <xsl:param name="date" />
      <!-- Month -->
      <xsl:variable name="month" select="number(substring($date, 6, 2))" />
      <xsl:choose>
         <xsl:when test="$month=1">
            January
         </xsl:when>
         <xsl:when test="$month=2">
            February
         </xsl:when>
         <xsl:when test="$month=3">
            March
         </xsl:when>
         <xsl:when test="$month=4">
            April
         </xsl:when>
         <xsl:when test="$month=5">
            May
         </xsl:when>
         <xsl:when test="$month=6">
            June
         </xsl:when>
         <xsl:when test="$month=7">
            July
         </xsl:when>
         <xsl:when test="$month=8">
            August
         </xsl:when>
         <xsl:when test="$month=9">
            September
         </xsl:when>
         <xsl:when test="$month=10">
            October
         </xsl:when>
         <xsl:when test="$month=11">
            November
         </xsl:when>
         <xsl:when test="$month=12">
            December
         </xsl:when>
         <xsl:otherwise>
            INVALID MONTH
         </xsl:otherwise>
      </xsl:choose>
      <xsl:text />
      <!-- Day -->
      <xsl:value-of select="number(substring($date, 9, 2))" />
      <xsl:text>, </xsl:text>
      <!-- Year -->
      <xsl:value-of select="substring($date, 1, 4)" />
   </xsl:template>
</xsl:stylesheet>