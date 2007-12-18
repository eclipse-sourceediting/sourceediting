/*******************************************************************************
 * Copyright (c) 2002, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     David Carver - STAR - bug 198807 - attribute order dependancy.
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contentmodel.util;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.ibm.icu.util.StringTokenizer;


public class NamespaceAttributeVisitor
{                                      
  public static final String XML_SCHEMA_INSTANCE_URI = "http://www.w3.org/2001/XMLSchema-instance"; //$NON-NLS-1$
  public String xsiPrefix = "xsi"; //$NON-NLS-1$

  public void visitXMLNamespaceAttribute(Attr attr, String namespacePrefix, String namespaceURI)
  {   
    if (namespaceURI.equals(XML_SCHEMA_INSTANCE_URI))
    {
      xsiPrefix = namespacePrefix;
    }
  } 

  public void visitXSINoNamespaceSchemaLocationAttribute(Attr attr, String value)
  {
  }

  public void visitXSISchemaLocationAttribute(Attr attr, String value)
  {
    StringTokenizer st = new StringTokenizer(value);          
    while (true)
    {
      String nsURI = st.hasMoreTokens() ? st.nextToken() : null;
      String locationHint = st.hasMoreTokens() ? st.nextToken() : null;            
      if (nsURI != null && locationHint != null)
      {    
        visitXSISchemaLocationValuePair(nsURI, locationHint);          
      }
      else
      {
        break;
      }
    }
  }   

  public void visitXSISchemaLocationValuePair(String uri, String locationHint)
  {
  }

  public void visitElement(Element element)
  {
    NamedNodeMap map = element.getAttributes();
    int mapLength = map.getLength();
    
    // First retrieve all the namespaces so that they are loaded before
    // doing any special prefix handling.  This allows the attributes to be
    // defined in any order, but the namespaces have to be retrieved first.
    
    for (int i = 0; i < mapLength; i++)
    {
      Attr attr = (Attr)map.item(i);
      String prefix = DOMNamespaceHelper.getPrefix(attr.getName());
      String unprefixedName = DOMNamespaceHelper.getUnprefixedName(attr.getName());
      if (prefix != null && unprefixedName != null)
      {
        if (prefix.equals("xmlns")) //$NON-NLS-1$
        {
          visitXMLNamespaceAttribute(attr, unprefixedName, attr.getValue());
        }
      }
      else if (unprefixedName != null)
      {
        if (unprefixedName.equals("xmlns")) //$NON-NLS-1$
        {
          visitXMLNamespaceAttribute(attr, "", attr.getValue()); //$NON-NLS-1$
        }
      }      

    }

    for (int i = 0; i < mapLength; i++)
    {
      Attr attr = (Attr)map.item(i);
      String prefix = DOMNamespaceHelper.getPrefix(attr.getName());
      String unprefixedName = DOMNamespaceHelper.getUnprefixedName(attr.getName());
      if (prefix != null && unprefixedName != null)
      {
       	if (prefix.equals(xsiPrefix) && unprefixedName.equals("schemaLocation")) //$NON-NLS-1$
        {
          visitXSISchemaLocationAttribute(attr, attr.getValue());
        }
        else if (prefix.equals(xsiPrefix) && unprefixedName.equals("noNamespaceSchemaLocation")) //$NON-NLS-1$
        {
          visitXSINoNamespaceSchemaLocationAttribute(attr, attr.getValue());
        }
      }
    }
  }      
}
