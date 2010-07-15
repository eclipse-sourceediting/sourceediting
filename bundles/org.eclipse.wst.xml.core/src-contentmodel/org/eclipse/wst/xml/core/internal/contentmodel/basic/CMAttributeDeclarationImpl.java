/*******************************************************************************
 * Copyright (c) 2002, 2010 IBM Corporation and others.
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

import java.util.Enumeration;
import java.util.List;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;


public class CMAttributeDeclarationImpl extends CMNodeImpl implements CMAttributeDeclaration
{
  protected CMDataType dataType;
  protected String nodeName;    
  protected int usage;  
  protected CMDocument cmDocument;
  protected boolean prefixQualification;
  protected List xsiTypes;
                
  public CMAttributeDeclarationImpl(String nodeName, int usage)
  {
    this(nodeName, usage, new CMDataTypeImpl(CMDataType.CDATA, ""));
  }     

  public CMAttributeDeclarationImpl(String nodeName, int usage, CMDataType dataType)
  {        
    this.nodeName = nodeName;
    this.usage = usage;
    this.dataType = dataType; 
  }    

  public int getNodeType()
  {
    return CMNode.ATTRIBUTE_DECLARATION;
  }

  public Object getProperty(String propertyName)
  {
    Object result = null;   
    if (propertyName.equals("CMDocument")) //$NON-NLS-1$
    {
      result = cmDocument;
    } 
    else if (propertyName.equals("XSITypes")) //$NON-NLS-1$
    {                                      
      result = xsiTypes;
    }
    else if (propertyName.equals("http://org.eclipse.wst/cm/properties/nsPrefixQualification")) //$NON-NLS-1$
    {
      result = prefixQualification ? "qualified" : "unqualified"; //$NON-NLS-1$ //$NON-NLS-2$
    }  
    else
    {
      result = super.getProperty(propertyName);
    }
    return result;
  }

  public void setPrefixQualification(boolean qualified)
  {
    prefixQualification = qualified;
  }              

  public void setXSITypes(List list)
  {
    xsiTypes = list;
  }
     
  public void setCMDocument(CMDocument cmDocument)
  {
    this.cmDocument = cmDocument;
  }

  public String getNodeName()
  {
    return nodeName;
  }

  public String getAttrName()
  {
    return nodeName;
  }

  public void setAttrType(CMDataType dataType)
  {
    this.dataType = dataType;
  }

  public CMDataType getAttrType()
  {
    return dataType;
  }

  public int getUsage()
  {
    return usage;
  }
           
  /** @deprecated */
  public String getDefaultValue()
  {
    return ""; //$NON-NLS-1$
  }                 

  /** @deprecated */
  public Enumeration getEnumAttr()
  {
    return null;
  }
}
