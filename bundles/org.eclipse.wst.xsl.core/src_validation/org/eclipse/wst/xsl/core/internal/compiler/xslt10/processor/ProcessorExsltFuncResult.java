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
 * $Id: ProcessorExsltFuncResult.java,v 1.1 2008/03/27 01:08:55 dacarver Exp $
 */
package org.eclipse.wst.xsl.core.internal.compiler.xslt10.processor;

import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemExsltFuncResult;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemExsltFunction;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemParam;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemTemplateElement;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.ElemVariable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class processes parse events for an exslt func:result element.
 * @xsl.usage internal
 */
public class ProcessorExsltFuncResult extends ProcessorTemplateElem
{
    static final long serialVersionUID = 6451230911473482423L;
  
  /**
   * Verify that the func:result element does not appear within a variable,
   * parameter, or another func:result, and that it belongs to a func:function 
   * element.
   */
  public void startElement(
          StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes)
            throws SAXException
  {
    String msg = "";

    super.startElement(handler, uri, localName, rawName, attributes);
    ElemTemplateElement ancestor = handler.getElemTemplateElement().getParentElem();
    while (ancestor != null && !(ancestor instanceof ElemExsltFunction))
    {
      if (ancestor instanceof ElemVariable 
          || ancestor instanceof ElemParam
          || ancestor instanceof ElemExsltFuncResult)
      {
        msg = "func:result cannot appear within a variable, parameter, or another func:result.";
        handler.error(msg, new SAXException(msg));
      }
      ancestor = ancestor.getParentElem();
    }
    if (ancestor == null)
    {
      msg = "func:result must appear in a func:function element";
      handler.error(msg, new SAXException(msg));
    }
  }
}
