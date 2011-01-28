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
 *     David Carver - STAR - bug 335273 - initial API and implementation
 *******************************************************************************  -->
<xsl:stylesheet
  version='1.0'
  xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
  xmlns:redirect='http://xml.apache.org/xalan/redirect'
  extension-element-prefixes='redirect'
  exclude-result-prefixes='redirect'
>
  <xsl:output method="text" />

  <xsl:strip-space elements="*" />

  <xsl:template match="*" />

  <xsl:param name="outputDirectory" />
  <xsl:param name="parameterPackage"/>

  <xsl:template name="toConstant">
    <xsl:param name="camelCase" />
    <xsl:value-of select="translate(substring($camelCase, 1, 1),'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')" /><xsl:call-template name="toConstantInternal"><xsl:with-param name="camelCase" select="substring($camelCase,2)" /></xsl:call-template>
  </xsl:template>

  <xsl:template name="toConstantChar">
    <xsl:param name="character" />
    <xsl:choose>
      <xsl:when test="contains('ABCDEFGHIJKLMNOPQRSTUVWXYZ', $character)">_<xsl:value-of select="$character" /></xsl:when>
      <xsl:otherwise><xsl:value-of select="translate($character, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')" /></xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="toConstantInternal">
    <xsl:param name="camelCase" />

    <xsl:choose>
      <xsl:when test="$camelCase=''"></xsl:when>
      <xsl:otherwise><xsl:call-template name="toConstantChar"><xsl:with-param name="character" select="substring($camelCase, 1, 1)" /></xsl:call-template><xsl:call-template name="toConstantInternal"><xsl:with-param name="camelCase" select="substring($camelCase, 2)" /></xsl:call-template></xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="/">/*
 * (C) Copyright 2007-2008 Redwood Technology B.V., Houten, The Netherlands
 *
 * This file is generated from redwood/scheduler/model/src/xml/ps-parameters.xml
 * Using $Id: npeparam.xsl,v 1.1 2011/01/28 15:06:19 dacarver Exp $
 */
package <xsl:value-of select="$parameterPackage" />;

public class ParameterConstants
{<xsl:text />
  /* Parameter names */
  <xsl:for-each select="ProcessServerParameters/ProcessServerParameter"><xsl:text />
  <xsl:variable name="constantName">
    <xsl:call-template name="toConstant">
      <xsl:with-param name="camelCase" select="@name" />
    </xsl:call-template>
  </xsl:variable>
  /** <xsl:value-of select="description" /> */
  public static final String <xsl:value-of select="$constantName" /> = "<xsl:value-of select="@name" />";<xsl:text/>
  </xsl:for-each>

  /* Variable names for Java */
  <xsl:for-each select="ProcessServerParameters/Variables/variable"><xsl:text />
    <xsl:variable name="constantName">
      <xsl:call-template name="toConstant">
        <xsl:with-param name="camelCase" select="substring-after(@name, 'var')" />
      </xsl:call-template>
    </xsl:variable>
  /** <xsl:value-of select="description" /> */
  public static final String VAR_<xsl:value-of select="$constantName" /> = "<xsl:value-of select="substring-after(@name, 'var')" />";
  </xsl:for-each>

  /* Platform specific default constants for Java */
  <xsl:for-each select="ProcessServerParameters/ProcessServerParameter/platform[@language='java']"><xsl:text />
  /** <xsl:value-of select="description" /> */
  public static final String PAR_DEF_<xsl:value-of select="@constant" /> = "<xsl:value-of select="@value" />";
  </xsl:for-each>
}<xsl:text />
  </xsl:template>
</xsl:stylesheet>
