/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - bug 224197 - initial API and implementation
 *                    based on work from Apache Xalan 2.7.0
 *******************************************************************************/

/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: ProcessorExsltFunction.java,v 1.1 2008/03/27 01:08:55 dacarver Exp $
 */
package org.eclipse.wst.xsl.core.internal.compiler.xslt10.processor;

import javax.xml.transform.SourceLocator;

import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemApplyImport;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemApplyTemplates;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemAttribute;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemCallTemplate;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemComment;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemCopy;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemCopyOf;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemElement;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemExsltFuncResult;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemExsltFunction;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemFallback;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemLiteralResult;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemNumber;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemPI;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemParam;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemTemplate;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemTemplateElement;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemText;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemTextLiteral;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemValueOf;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemVariable;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemMessage;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.Stylesheet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * This class processes parse events for an exslt func:function element.
 * @xsl.usage internal
 */
public class ProcessorExsltFunction extends ProcessorTemplateElem
{
    static final long serialVersionUID = 2411427965578315332L;
  /**
   * Start an ElemExsltFunction. Verify that it is top level and that it has a name attribute with a
   * namespace.
   */
  public void startElement(
          StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes)
            throws SAXException
  {
    //System.out.println("ProcessorFunction.startElement()");
    String msg = "";
    if (!(handler.getElemTemplateElement() instanceof Stylesheet))
    {
      msg = "func:function element must be top level.";
      handler.error(msg, new SAXException(msg));
    }
    super.startElement(handler, uri, localName, rawName, attributes);
       
    String val = attributes.getValue("name");
    int indexOfColon = val.indexOf(":");
    if (indexOfColon > 0)
    {
      String prefix = val.substring(0, indexOfColon);
      String localVal = val.substring(indexOfColon + 1);
      String ns = handler.getNamespaceSupport().getURI(prefix);
      //if (ns.length() > 0)
      //  System.out.println("fullfuncname " + ns + localVal);
    }
    else
    {
      msg = "func:function name must have namespace";
      handler.error(msg, new SAXException(msg));
    }
  }
  
  /**
   * Must include; super doesn't suffice!
   */
  protected void appendAndPush(
          StylesheetHandler handler, ElemTemplateElement elem)
            throws SAXException
  {
    //System.out.println("ProcessorFunction appendAndPush()" + elem);
    super.appendAndPush(handler, elem);
    //System.out.println("originating node " + handler.getOriginatingNode());
    elem.setDOMBackPointer(handler.getOriginatingNode());
    handler.getStylesheet().setTemplate((ElemTemplate) elem);
  }
    
  /**
   * End an ElemExsltFunction, and verify its validity.
   */
  public void endElement(
          StylesheetHandler handler, String uri, String localName, String rawName)
            throws SAXException
  {
   ElemTemplateElement function = handler.getElemTemplateElement();
   SourceLocator locator = handler.getLocator();
   validate(function, handler); // may throw exception
   super.endElement(handler, uri, localName, rawName);   
  }
  
  /**
   * Non-recursive traversal of FunctionElement tree based on TreeWalker to verify that
   * there are no literal result elements except within a func:result element and that
   * the func:result element does not contain any following siblings except xsl:fallback.
   */
  public void validate(ElemTemplateElement elem, StylesheetHandler handler)
    throws SAXException
  {
    String msg = "";
    while (elem != null)
    { 
      //System.out.println("elem " + elem);
      if (elem instanceof ElemExsltFuncResult 
          && elem.getNextSiblingElem() != null 
          && !(elem.getNextSiblingElem() instanceof ElemFallback))
      {
        msg = "func:result has an illegal following sibling (only xsl:fallback allowed)";
        handler.error(msg, new SAXException(msg));
      }
      
      if((elem instanceof ElemApplyImport
	 || elem instanceof ElemApplyTemplates
	 || elem instanceof ElemAttribute
	 || elem instanceof ElemCallTemplate
	 || elem instanceof ElemComment
	 || elem instanceof ElemCopy
	 || elem instanceof ElemCopyOf
	 || elem instanceof ElemElement
	 || elem instanceof ElemLiteralResult
	 || elem instanceof ElemNumber
	 || elem instanceof ElemPI
	 || elem instanceof ElemText
	 || elem instanceof ElemTextLiteral
	 || elem instanceof ElemValueOf)
	&& !(ancestorIsOk(elem)))
      {
        msg ="misplaced literal result in a func:function container.";
        handler.error(msg, new SAXException(msg));
      }
      ElemTemplateElement nextElem = elem.getFirstChildElem();
      while (nextElem == null)
      {
        nextElem = elem.getNextSiblingElem();
        if (nextElem == null)
          elem = elem.getParentElem();
        if (elem == null || elem instanceof ElemExsltFunction)
          return; // ok
      }  
      elem = nextElem;
    }
  }
  
  /**
   * Verify that a literal result belongs to a result element, a variable, 
   * or a parameter.
   */
  
  protected boolean ancestorIsOk(ElemTemplateElement child)
  {
    while (child.getParentElem() != null && !(child.getParentElem() instanceof ElemExsltFunction))
    {
      ElemTemplateElement parent = child.getParentElem();
      if (parent instanceof ElemExsltFuncResult 
          || parent instanceof ElemVariable
          || parent instanceof ElemParam
          || parent instanceof ElemMessage)
        return true;
      child = parent;      
    }
    return false;
  }
  
}
