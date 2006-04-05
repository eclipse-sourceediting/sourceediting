/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.validation.errorcustomization;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple class to store information about an XML element.
 */
public class ElementInformation
{
  protected String localName;
  protected String namespace;
  protected List children = new ArrayList();

  /**
   * Constructor.
   * 
   * @param uri
   * 		The namespace URI of the element.
   * @param localName
   * 		The local name of the element.
   */
  public ElementInformation(String uri, String localName)
  {
    this.localName = localName;
    this.namespace = uri;
  }
  
  /**
   * Get the namespace of this element.
   * 
   * @return
   * 		The namespace of this element.
   */
  public String getNamespace()
  {
	return namespace;
  }
  
  /**
   * Get the local name of this element.
   * 
   * @return
   * 		The local name of this element.
   */
  public String getLocalname()
  {
	return localName;
  }
  
  /**
   * Get the list of children of this element. The list contains
   * ElementInformation objects representing the children of this element.
   * 
   * @return
   * 		The list of children of this element.
   */
  public List getChildren()
  {
	return children;
  }
}