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
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:redirect="http://xml.apache.org/xalan/redirect"
                extension-element-prefixes="redirect">
    <xsl:output encoding="UTF-8" method="html" indent="no" omit-xml-declaration="yes"/>
    
    <!-- Create the HTML structure -->
	<xsl:template match="/">
   		<xsl:apply-templates select="//jelclass"/>
	</xsl:template>
   
    <!-- This is the main work horse that does the general layout for the class page -->
    <xsl:template match="jelclass">
      <xsl:variable name="outputFile">
        <xsl:value-of select="@fulltype"/>
        <xsl:text>.html</xsl:text>
      </xsl:variable>
      <xsl:message terminate="no">
         <xsl:text>Generating JavaDoc for </xsl:text>
         <xsl:value-of select="@fulltype"/>
         <xsl:text>.</xsl:text>
      </xsl:message>
      <redirect:write select="$outputFile">
       <html>
         <body>
            <p>
               <font size="+1"><strong>Package <xsl:value-of select="@package"/></strong></font>
            </p>
            <hr/>
            <h2>
               <font size="-1"><strong><xsl:value-of select="@package"/></strong></font><br/>
               Class <xsl:value-of select="@type"/>
            </h2>
            <br/>
            <hr/>
            <xsl:call-template name="format-header"/>
            <xsl:apply-templates select="comment/description"/>
            <xsl:apply-templates select="comment/attribute"/>
            <hr/>
            <xsl:if test="descendant::fields">
               <table border="1" width="100%">
                  <tr bgcolor="#CCCCFF" cols="2">
                     <td width="100%" colspan="2"><h2>Field Summary</h2></td>
                  </tr>
                  <xsl:apply-templates select="fields/field" mode="summary">
                     <xsl:sort select="@name" order="ascending"/>
                  </xsl:apply-templates>
               </table>
               <br/>
            </xsl:if>
            <xsl:if test="descendant::constructor">
               <table border="1" width="100%">
                  <tr bgcolor="#CCCCFF">
                     <td width="100%"><h2>Constructor Summary</h2></td>
                  </tr>
                  <xsl:apply-templates select="descendant::constructor" mode="summary">
                     <xsl:sort select="@name" order="ascending"/>
                  </xsl:apply-templates>
               </table>
               <br/>
            </xsl:if>
            <xsl:if test="descendant::methods">
               <table border="1" width="100%">
                  <tr bgcolor="#CCCCFF" cols="2">
                     <td width="100%" colspan="2"><h2>Method Summary</h2></td>
                  </tr>
                  <xsl:apply-templates select="methods/method" mode="summary">
                     <xsl:sort select="@name" order="ascending"/>
                  </xsl:apply-templates>
               </table>
            </xsl:if>
            <hr/>
            <xsl:if test="fields">
               <xsl:call-template name="fieldDetail"/>
            </xsl:if>
            <xsl:if test="methods/constructor">
               <xsl:call-template name="constructorDetail"/>
            </xsl:if>
            <xsl:if test="methods/method">
               <xsl:call-template name="methodsDetail"/>
            </xsl:if>
           </body>
         </html>
      </redirect:write>
    </xsl:template>
    
    <!-- Output any description that may be there for comments. -->
    <xsl:template match="comment/description">
            <xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    
    <!-- Output the various attribute information for the class -->
    <xsl:template match="comment/attribute">
         <p>
            <xsl:choose>
               <xsl:when test="@author">
                  <strong>Author:</strong><br/>
                  <xsl:value-of select="description" disable-output-escaping="yes"/> 
               </xsl:when>
               <xsl:when test="@see">
                  <strong>See Also:</strong><br/>
                     <xsl:value-of select="description" disable-output-escaping="yes"/>
               </xsl:when>
            </xsl:choose>
         </p>
    </xsl:template>


        <!-- Create the field summary rows -->
    <xsl:template match="field" mode="summary">
         <tr valign="top">
            <td align="right" width="20%">
               <code>
                  <xsl:value-of select="@visibility"/>
                  <xsl:if test="@static = 'true'">
                     <xsl:text> static </xsl:text>
                  </xsl:if>
                  <xsl:text> </xsl:text>
                  <xsl:value-of select="@fulltype"/>                     
               </code>
            </td>
            <td align="left" width="80%">
               <code>
                  <a href="#field_{@name}">
                  <xsl:value-of select="@name"/>
                  </a>
               </code>
               <br/>
               &#160;&#160;&#160;&#160;
               <xsl:value-of select="comment/description"/>
            </td>
         </tr>
    </xsl:template>
    
    
    <!-- Create the constructors rows -->
    <xsl:template match="constructor" mode="summary">
         <tr>
            <td>
               <code>
                  <strong>
                    <a href="#con_{@name}">
                     <xsl:value-of select="@name"/>
                    </a>
                  </strong>                     
                  <xsl:text>(</xsl:text>
                  <xsl:if test="params">
                     <xsl:apply-templates select="params/param"/>
                  </xsl:if>
                  <xsl:text>)</xsl:text>
               </code>
            </td>
         </tr>
    </xsl:template>
    
    
    <xsl:template match="param">
      <xsl:value-of select="@fulltype"/><xsl:text> </xsl:text><xsl:value-of select="@name"/>
      <xsl:if test="position() != last()">
         ,
      </xsl:if>
    </xsl:template>
    
    <!-- Create the method summary rows -->
    <xsl:template match="method" mode="summary">
         <tr valign="top">
            <td align="right" width="20%">
               <code>
                  <xsl:value-of select="@visibility"/>
                  <xsl:if test="@static = 'true'">
                     <xsl:text> static</xsl:text>
                  </xsl:if>
                  <xsl:text> </xsl:text>
                  <xsl:value-of select="@fulltype"/>                     
               </code>
            </td>
            <td align="left" width="">
               <code>
                  <a href="#method_{@name}">
                  <xsl:value-of select="@name"/>
                  </a>
                  <xsl:text>(</xsl:text>
                  <xsl:if test="params">
                     <xsl:apply-templates select="params/param"/>
                  </xsl:if>
                  <xsl:text>)</xsl:text>
               </code>
               <br/>
               &#160;&#160;&#160;&#160;
               <xsl:apply-templates select="comment/description"/>
            </td>
         </tr>
    </xsl:template>
    
    
    <xsl:template name="format-header">
         <pre>
<xsl:value-of select="@visibility"/> class <strong><xsl:value-of select="@type"/></strong>
extends <xsl:value-of select="@superclassfulltype"/>
         </pre>
    </xsl:template>
    
    <xsl:template name="fieldDetail">
      <table border="1" width="100%" cols="2">
         <tr align="left" bgcolor="#CCCCFF" width="100%" colspan="2">
            <td>
               <h2>
                  Field Detail
               </h2>
            </td>
         </tr>
      </table>
      <xsl:apply-templates select="fields/field" mode="detail">
         <xsl:sort select="@name" order="ascending"/>
      </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="fields/field" mode="detail">
      <h2>
         <a name="field_{@name}">
            <xsl:value-of select="@name"/>
         </a>
      </h2>
      <p>
         <code>
            <xsl:value-of select="@visibility"/>
            <xsl:if test="@static = 'true'">
               <xsl:text> static </xsl:text>
            </xsl:if>
            <xsl:text> </xsl:text>
            <xsl:value-of select="@fulltype"/>
            <xsl:text> </xsl:text>
            <strong>
               <xsl:value-of select="@name"/>
            </strong>                   
         </code>
      </p>
      <xsl:if test="comment">
         <table>
            <tr>
              <td>
                  <xsl:apply-templates select="comment/description"/>
              </td>
            </tr>
         </table>
      </xsl:if>
      <hr/>
      
    </xsl:template>
    
    <xsl:template name="constructorDetail">
      <table border="1" width="100%" cols="2">
         <tr align="left" bgcolor="#CCCCFF" width="100%" colspan="2">
            <td>
               <h2>
                  Constructor Detail
               </h2>
            </td>
         </tr>
      </table>
      <xsl:apply-templates select="methods/constructor" mode="detail">
         <xsl:sort select="@name" order="ascending"/>
      </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="methods/constructor" mode="detail">
      <h2>
         <a name="con_{@name}"><xsl:value-of select="@name"/></a>
      </h2>
      <p>
         <code>
            <xsl:value-of select="@visibility"/>
            <xsl:text> </xsl:text>
            <strong>
               <xsl:value-of select="@name"/>
            </strong>
            <xsl:text>(</xsl:text>
            <xsl:if test="params">
               <xsl:apply-templates select="params/param"/>
            </xsl:if>
            <xsl:text>)</xsl:text>
         </code>
      </p>
      <p>
         <xsl:apply-templates select="comment/description"/>
      </p>
      <hr/>
      
    </xsl:template>
    
    <xsl:template name="methodsDetail">
      <table border="1" width="100%" cols="2">
         <tr align="left" bgcolor="#CCCCFF" width="100%" colspan="2">
            <td>
               <h2>
                  Methods Detail
               </h2>
            </td>
         </tr>
      </table>
      <xsl:apply-templates select="methods/method" mode="detail">
         <xsl:sort select="@name" order="ascending"/>
      </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="methods/method" mode="detail">
      <h2>
         <a name="method_{@name}">
            <xsl:value-of select="@name"/>
         </a>
      </h2>
      <p>
         <code>
            <xsl:value-of select="@visibility"/>
            <xsl:text> </xsl:text>
            <xsl:value-of select="@fulltype"/>
            <xsl:text> </xsl:text>
            <strong>
               <xsl:value-of select="@name"/>
            </strong>
            <xsl:text>(</xsl:text>
            <xsl:if test="params">
               <xsl:apply-templates select="params/param"/>
            </xsl:if>
            <xsl:text>)</xsl:text>
         </code>
      </p>
      <p>
         <xsl:apply-templates select="comment/description"/>
      </p>
      <xsl:if test="params">
         <h3>Parameters</h3>
         <xsl:apply-templates select="params/param" mode="detail"/>
      </xsl:if>
      <xsl:if test="@type != 'void'">
         <h3>Returns</h3>
            <p>
               <code>
                  <xsl:value-of select="@fulltype"/>
               </code>
               <xsl:if test="string-length(@returncomment) > 0">
                  <xsl:text> - </xsl:text>
                  <xsl:value-of select="@returncomment" disable-output-escaping="yes"/>
               </xsl:if>
            </p>
      </xsl:if>
      <xsl:if test="exceptions">
         <h3>Throws:</h3>
         <xsl:apply-templates select="exceptions/exception" mode="detail"/>
      </xsl:if>
      <hr/>
      
    </xsl:template>
    
    <xsl:template match="param" mode="detail">
         <p>
            <code>
               <xsl:value-of select="@name"/>
            </code>
            <xsl:if test="string-length(@comment) > 0">
               <xsl:text> - </xsl:text>
               <xsl:value-of select="@comment" disable-output-escaping="yes"/>
            </xsl:if>
         </p>
    </xsl:template>
    
    <xsl:template match="exception" mode="detail">
      <p>
         <code>
            <xsl:value-of select="@fulltype"/>
            <xsl:if test="string-length(@comment) > 0">
               <xsl:text> - </xsl:text>
               <xsl:value-of select="@comment" disable-output-escaping="yes"/>
            </xsl:if>
         </code>
      </p>
    </xsl:template>
       
</xsl:stylesheet>