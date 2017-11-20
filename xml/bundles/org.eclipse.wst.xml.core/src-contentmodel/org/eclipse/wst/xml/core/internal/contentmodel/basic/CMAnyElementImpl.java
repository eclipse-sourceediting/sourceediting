/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.contentmodel.basic;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAnyElement;


public class CMAnyElementImpl extends CMContentImpl implements CMAnyElement
{
  protected String namespaceURI;  

  public CMAnyElementImpl(String namespaceURI)
  {
    this.namespaceURI = namespaceURI;
    minOccur = -1;
  }             

  public static String computeNodeName(String uri)
  {
    return uri != null ? ("any#" + uri) : "any"; //$NON-NLS-1$ //$NON-NLS-2$
  }

  // implements CMNode
  //
  public String getNodeName()
  {
    return computeNodeName(namespaceURI);
  }
 
  public int getNodeType()
  {
    return ANY_ELEMENT;
  } 

  // implements CMAnyElement
  //
  public String getNamespaceURI()
  {
    return namespaceURI;
  }                     
}
