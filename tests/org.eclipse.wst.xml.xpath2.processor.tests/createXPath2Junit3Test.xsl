<?xml version="1.0" encoding="UTF-8"?>
<!-- 
/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver - STAR - Stylesheet for testing purposes.
 *******************************************************************************/
 
 This stylesheet is used to generate a Test per test-group for the XPath 2.0
 testing suite.  It uses as input the XQTSCatalog.xml file.
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xqts="http://www.w3.org/2005/02/query-test-XQTSCatalog">
   <xsl:output indent="yes" method="text" encoding="UTF-8"/>
   
   <xsl:variable name="location">/Queries/XQuery</xsl:variable>
   <xsl:param name="group">Catalog</xsl:param>
   
   <xsl:template match="/">
      <xsl:text>
/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver - STAR - initial api and implementation bug 262765 
 *******************************************************************************/
package org.eclipse.wst.xml.xpath2.processor.testsuite;

import java.net.URL;

import org.apache.xerces.xs.XSModel;
import org.eclipse.wst.xml.xpath2.processor.*;
import org.eclipse.wst.xml.xpath2.processor.ast.XPath;
import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyType;
import org.eclipse.wst.xml.xpath2.processor.test.AbstractPsychoPathTest;
      
      </xsl:text>
      <xsl:apply-templates/>
   </xsl:template>
   
   <xsl:template match="xqts:test-suite">
      <xsl:text>
public class </xsl:text>
      <xsl:value-of select="$group"/>
      <xsl:text>Test extends AbstractPsychoPathTest {
</xsl:text>
      <xsl:apply-templates select="descendant::xqts:test-group[@name = $group]"/>
      <xsl:text>
}
      </xsl:text>
   </xsl:template>
   
   <xsl:template match="xqts:test-group">
       <xsl:apply-templates select="xqts:test-group | xqts:test-case"/>
   </xsl:template>
   
   <xsl:template match="xqts:test-case">
      <xsl:if test="@is-XPath2 = 'true'">
        <xsl:variable name="testName" select="@name"/>
        <xsl:variable name="FilePath" select="@FilePath"/>
        <xsl:variable name="queryName" select="./xqts:query/@name"/>
        <xsl:variable name="inputFile"><xsl:value-of select="xqts:input-file"/>.xml</xsl:variable>
        <xsl:variable name="variable" select="./xqts:input-file/@variable"/>
        <xsl:text>
   //</xsl:text>
        <xsl:value-of select="./xqts:description"/>
        <xsl:text>
   public void test_</xsl:text>
        <xsl:variable name="methodName">
            <xsl:call-template name="replace-string">
               <xsl:with-param name="text" select="$testName"/>
               <xsl:with-param name="replace">-</xsl:with-param>
               <xsl:with-param name="with">_</xsl:with-param>
            </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="$methodName"/>
        <xsl:text>() throws Exception {
</xsl:text>
        <xsl:call-template name="SetVariables"/>
<xsl:text>
      String expectedResult = getExpectedResult(resultFile);
      URL fileURL = bundle.getEntry(inputFile);
      loadDOMDocument(fileURL);
      
      // Get XML Schema Information for the Document
      XSModel schema = getGrammar();

      DynamicContext dc = setupDynamicContext(schema);

      String xpath = extractXPathExpression(xqFile, inputFile);
      String actual = null;
      try {
	   	  XPath path = compileXPath(dc, xpath);
	
	      Evaluator eval = new DefaultEvaluator(dc, domDoc);
	      ResultSequence rs = eval.evaluate(path);
         
          actual = buildResultString(rs);
	
      } catch (XPathParserException ex) {
    	 actual = ex.code();
      } catch (StaticError ex) {
         actual = ex.code();
      } catch (DynamicError ex) {
         actual = ex.code();
      }

      assertEquals("XPath Result Error " + xqFile + ":", expectedResult, actual);
        
</xsl:text>
        <xsl:text>
   }
</xsl:text>
      </xsl:if>
   </xsl:template>
   
   <xsl:template name="SetVariables">
      <xsl:text>      String inputFile = "/TestSources/</xsl:text>
      <xsl:value-of select="xqts:input-file"/>
      <xsl:text>.xml";</xsl:text>
      <xsl:text>
      String xqFile = "/Queries/XQuery/</xsl:text>
      <xsl:value-of select="@FilePath"/>
      <xsl:value-of select="xqts:query/@name"/>
      <xsl:text>.xq";</xsl:text>
      <xsl:text>
      String resultFile = "/ExpectedTestResults/</xsl:text>
      <xsl:value-of select="@FilePath"/>
      <xsl:value-of select="xqts:output-file"/>
      <xsl:text>";</xsl:text>
   </xsl:template>
   
   <xsl:template name="replace-string">
      <xsl:param name="text"/>
      <xsl:param name="replace"/>
      <xsl:param name="with"/>
      <xsl:choose>
         <xsl:when test="contains($text,$replace)">
            <xsl:value-of select="substring-before($text,$replace)"/>
            <xsl:value-of select="$with"/>
            <xsl:call-template name="replace-string">
               <xsl:with-param name="text" select="substring-after($text,$replace)"/>
               <xsl:with-param name="replace" select="$replace"/>
               <xsl:with-param name="with" select="$with"/>
            </xsl:call-template>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$text"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   
   <xsl:template match="* | node()">
   
   </xsl:template>
   
</xsl:stylesheet>