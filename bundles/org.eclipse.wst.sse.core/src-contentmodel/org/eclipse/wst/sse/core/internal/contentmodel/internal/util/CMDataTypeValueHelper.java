/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.core.internal.contentmodel.internal.util;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.wst.sse.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.sse.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.sse.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.sse.core.internal.contentmodel.util.DOMNamespaceHelper;
import org.eclipse.wst.sse.core.internal.contentmodel.util.NamespaceTable;


public class CMDataTypeValueHelper
{
  protected int idCount = 0;


  public String getValue(CMAttributeDeclaration ad, NamespaceTable namespaceTable)
  {
    String value = null;
       
    if (isXSIType(ad))         
    {       
      List list = getQualifiedXSITypes(ad, namespaceTable);
      value = list.size() > 0 ? (String)list.get(0) : null;
    } 

    // shameless hack
    //
    if (value == null)
    {
      if (ad.getAttrName().equals("xml:lang"))    
      {
        value = "EN";
      }
    }

    if (value == null)
    {
      CMDataType dataType = ad.getAttrType();
      if (dataType != null)
      {
        value = getValue(dataType);
      }
    }
    return value;
  }


  public String getValue(CMDataType dataType)
  {
    String value = null;
    value = dataType.getImpliedValue();

    if (value == null)
    {
      String[] values = dataType.getEnumeratedValues();
      if (values != null && values.length > 0)
      {
        value = values[0];
      }
    }

    if (value == null)
    {
      value = dataType.generateInstanceValue();
    }               

    // Here is a special case where we handle DTD's ID related datatypes.
    // These values must be generated/validate by considering the entire xml file
    // so we can't rely on the 'generateInstanceValue' method.
    //
    // todo... find a nicer way to handle this    
    if (value == null)
    {
      String dataTypeName = dataType.getDataTypeName();
      if (dataTypeName != null)
      {
        if (dataTypeName.equals("ID"))
        {
          value = "idvalue" + idCount++;
        }
        else if (dataTypeName.equals("IDREF") || dataTypeName.equals("IDREFS"))
        {
          value = "idvalue0";
        }
      }
    }     
    return value;
  } 
      

  public boolean isValidEmptyValue(CMAttributeDeclaration ad)
  {    
    boolean result = true;
    CMDataType dataType = ad.getAttrType();
    if (dataType != null)
    {                                                          
      String propertyValue = (String)dataType.getProperty("isValidEmptyValue");
      if (propertyValue != null && propertyValue.equals("false"))
      {
        result = false;
      }
    }   
    return result;
  }


  public boolean isXSIType(CMAttributeDeclaration ad)
  {         
    boolean result = false;
    if (ad.getNodeName().equals("type")) 
    {
      CMDocument cmDocument = (CMDocument)ad.getProperty("CMDocument");
      if (cmDocument != null)
      {
        String namespaceName = (String)cmDocument.getProperty("http://org.eclipse.wst/cm/properties/targetNamespaceURI");
        if (namespaceName != null && namespaceName.equals("http://www.w3.org/2001/XMLSchema-instance"))
        {           
          result = true;
        }
      }
    }                   
    return result;
  }
   

  public List getQualifiedXSITypes(CMAttributeDeclaration ad, NamespaceTable table)
  {          
    List list = new Vector();                                                 
                                       
    List xsiTypes = (List)ad.getProperty("XSITypes");
    if (xsiTypes != null && xsiTypes.size() > 0)
    {
      for (Iterator i = xsiTypes.iterator(); i.hasNext(); )
      { 
        String uriQualifiedName = (String)i.next();
        String[] components = DOMNamespaceHelper.getURIQualifiedNameComponents(uriQualifiedName);  
        String prefix = table.getPrefixForURI(components[0] != null ? components[0] : "");
        String typeName = (prefix != null && prefix.length() > 0) ? 
                           prefix + ":" + components[1] : 
                           components[1];
        list.add(typeName);
      }
    }           
    return list;
  }
}
