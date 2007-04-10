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

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamespace;


public class CMDocumentImpl extends CMNodeImpl implements CMDocument
{
  protected String targetNamespace;
  protected CMNamedNodeMapImpl elements = new CMNamedNodeMapImpl();
  protected CMNamedNodeMapImpl localElementPool;
  protected CMNamedNodeMapImpl anyElements;

  public CMDocumentImpl(String targetNamespace)
  {
    this.targetNamespace = targetNamespace;
  }

  public int getNodeType()
  {
    return DOCUMENT;
  }

  public String getNodeName()
  {
    return ""; //$NON-NLS-1$
  }
 
  public Object getProperty(String propertyName)
  {
    Object result = null;
    if (propertyName.equals("http://org.eclipse.wst/cm/properties/targetNamespaceURI")) //$NON-NLS-1$
    {
      result = targetNamespace;
    }                
    else
    {
      result = super.getProperty(propertyName);
    }
    return result;
  }    

  public CMNamedNodeMap getElements()
  {
    return elements;
  }

  public CMNamedNodeMapImpl getAnyElements()
  { 
    if (anyElements == null)
    {
      anyElements = new CMNamedNodeMapImpl();
    }
    return anyElements;
  }
  
  public CMNamedNodeMapImpl getLocalElementPool()
  { 
    if (localElementPool == null)
    {
      localElementPool = new CMNamedNodeMapImpl();
    }                 
    return localElementPool;
  }

  public CMNamedNodeMap getEntities()
  {
    return new CMNamedNodeMapImpl();
  }

  public CMNamespace getNamespace()
  {
    return null;
  }
}
