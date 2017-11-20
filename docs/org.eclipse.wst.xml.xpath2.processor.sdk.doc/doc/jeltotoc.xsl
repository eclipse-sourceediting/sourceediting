<?xml version="1.0" encoding="UTF-8"?>
<!-- 
/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - bug 231472 - initial API and implementation
 *******************************************************************************/
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:set="http://exslt.org/sets"
   xmlns:xalan="http://xml.apache.org/xalan"
    exclude-result-prefixes="set xalan"
>
   <xsl:param name="sdkname">Some Name</xsl:param>
   <xsl:param name="dir">doc/html</xsl:param>   
   <xsl:output indent="yes" encoding="UTF-8" xalan:indent-amount="3"/>
   <xsl:key name="allPackages" match="jelclass" use="@package"/>
     
   <xsl:template match="/jel">
      <toc label="{$sdkname}">
         <topic label="Reference">
            <xsl:for-each select="set:distinct(jelclass/@package)">
               <topic label="{.}">
                  <xsl:apply-templates select="key('allPackages', .)"/>
               </topic>
            </xsl:for-each>
         </topic>
      </toc>
   </xsl:template>
   
   <xsl:template match="jelclass">
      <topic label="{@fulltype}" href="{$dir}/{@fulltype}.html"/>
   </xsl:template>
   
</xsl:stylesheet>