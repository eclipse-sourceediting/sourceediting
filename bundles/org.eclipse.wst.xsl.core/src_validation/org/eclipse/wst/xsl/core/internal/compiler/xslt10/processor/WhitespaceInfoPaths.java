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
 * $Id: WhitespaceInfoPaths.java,v 1.1 2008/03/27 01:08:55 dacarver Exp $
 */
package org.eclipse.wst.xsl.core.internal.compiler.xslt10.processor;

import java.util.Vector;

import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.Stylesheet;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.templates.WhiteSpaceInfo;

public class WhitespaceInfoPaths extends WhiteSpaceInfo
{
    static final long serialVersionUID = 5954766719577516723L;
	
  /**
   * Bean property to allow setPropertiesFromAttributes to
   * get the elements attribute.
   */
  private Vector m_elements;

  /**
   * Set from the elements attribute.  This is a list of 
   * whitespace delimited element qualified names that specify
   * preservation of whitespace.
   *
   * @param elems Should be a non-null reference to a list 
   *              of {@link org.apache.xpath.XPath} objects.
   */
  public void setElements(Vector elems)
  {
    m_elements = elems;
  }

  /**
   * Get the property set by setElements().  This is a list of 
   * whitespace delimited element qualified names that specify
   * preservation of whitespace.
   *
   * @return A reference to a list of {@link org.apache.xpath.XPath} objects, 
   *         or null.
   */
  public Vector getElements()
  {
    return m_elements;
  }
  
  public void clearElements()
  {
  	m_elements = null;
  }

 /**
   * Constructor WhitespaceInfoPaths
   *
   * @param thisSheet The current stylesheet
   */
  public WhitespaceInfoPaths(Stylesheet thisSheet)
  {
  	super(thisSheet);
  	setStylesheet(thisSheet);
  }


}

