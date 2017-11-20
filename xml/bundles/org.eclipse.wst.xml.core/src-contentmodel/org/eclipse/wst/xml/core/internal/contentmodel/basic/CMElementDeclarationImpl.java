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

import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;


public class CMElementDeclarationImpl extends CMContentImpl implements CMElementDeclaration
{
  protected String name;          
  protected CMDocument cmDocument;
  protected CMNamedNodeMapImpl attributeMap = new CMNamedNodeMapImpl();
  protected CMNamedNodeMapImpl localElementMap = new CMNamedNodeMapImpl();
  protected CMContent content;
  protected CMDataType dataType;
  protected boolean isLocal;

  public CMElementDeclarationImpl(CMDocument cmDocument, String name)
  {                              
    this.cmDocument = cmDocument;
    this.name = name;
  }     

  // implements CMNode
  //
  public String getNodeName()
  {
    return name;
  }
 
  public int getNodeType()
  {
    return ELEMENT_DECLARATION;
  }

  public Object getProperty(String propertyName)
  { 
    Object result = null;               
    if (propertyName.equals("http://org.eclipse.wst/cm/properties/nsPrefixQualification")) //$NON-NLS-1$
    {
      result = isLocal ? "unqualified" : "qualified"; //$NON-NLS-1$ //$NON-NLS-2$
    }    
    else if (propertyName.equals("CMDocument")) //$NON-NLS-1$
    {
      result = cmDocument;       
    }    
    else
    {
      result = super.getProperty(propertyName);
    }
    return result;
  }
                
  // implements CMElementDeclaration
  //
  public CMNamedNodeMap getAttributes()
  {
    return attributeMap;
  }                     

  public CMContent getContent()
  {
    return content;
  }

  public int getContentType()
  {
    return MIXED;
  }            

  public String getElementName()
  {
    return name;
  }

  public CMDataType getDataType()
  { 
    return dataType;
  }

  public CMNamedNodeMap getLocalElements()
  { 
    return localElementMap;
  } 
    
  //
  //
  public void setContent(CMContent cmContent)
  {
    content = cmContent;
  }                 

  public void setDataType(CMDataType cmDataType)
  {
    dataType = cmDataType;
  }

  public CMNamedNodeMapImpl getAttributeMap()
  {
    return attributeMap;
  }

  public void setLocal(boolean isLocal)
  {
    this.isLocal = isLocal;
  }
}
