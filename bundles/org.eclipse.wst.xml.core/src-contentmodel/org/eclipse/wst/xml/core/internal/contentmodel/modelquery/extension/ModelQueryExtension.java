/*******************************************************************************
 * Copyright (c) 2002, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension;

import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class ModelQueryExtension
{  
  protected static final String[] EMPTY_STRING_ARRAY = {};
  protected static final CMNode[] EMPTY_CMNODE_ARRAY = {};
  
  /**
 * @param ownerElement - the owner element
 * @param namespace - the active namespace
 * @param name - the name of an attribute node
 * @return valid values for the given attribute
 */
public String[] getAttributeValues(Element ownerElement, String namespace, String name)
  {
    return EMPTY_STRING_ARRAY;
  }
  
  public String[] getElementValues(Node parentNode, String namespace, String name)
  {
    return EMPTY_STRING_ARRAY;
  }
  
  /**
 * @param parentNode
 * @param namespace
 * @param name
 * @return whether a child element of the given parentNode is valid given the active namespace
 */
public boolean isApplicableChildElement(Node parentNode, String namespace, String name)
  {
    return true;
  }
  
  /**
 * @param parentElement - the parent Element when asking for children, the owner Element when asking for attributes 
 * @param namespace - the active namespace
 * @param includeOptions - ModelQuery.INCLUDE_ATTRIBUTES or ModelQuery.INCLUDE_CHILD_NODES
 * @return additional valid CMAttributeDeclarations or CMElementDeclarations 
 */
public CMNode[] getAvailableElementContent(Element parentElement, String namespace, int includeOptions) 
  {
	  return EMPTY_CMNODE_ARRAY;
  }
}
