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
package org.eclipse.wst.xml.core.internal.contentmodel.internal.modelqueryimpl;
        
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension.DataTypeValueExtension;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension.ElementContentFilterExtension;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension.ModelQueryExtension;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension.ModelQueryExtensionManager;
import org.w3c.dom.Element;



public class ModelQueryExtensionManagerImpl implements ModelQueryExtensionManager
{           
  protected Hashtable dataTypeValueExtensionTable = new Hashtable();
  protected List elementContentFilterList = new ArrayList();

  public List getDataTypeValues(Element element, CMNode cmNode)
  {
    List list = new Vector();                                         
    for (Iterator i = dataTypeValueExtensionTable.values().iterator(); i.hasNext();)
    {
      DataTypeValueExtension extension = (DataTypeValueExtension)i.next();
      list.addAll(extension.getDataTypeValues(element, cmNode));
    }  
    return list;  
  }                               

  public void filterAvailableElementContent(List list, Element element, CMElementDeclaration ed)
  {
    for (Iterator i = elementContentFilterList.iterator(); i.hasNext();)
    {
      ElementContentFilterExtension extension = (ElementContentFilterExtension)i.next();
      extension.filterAvailableElementContent(list, element, ed);
    }  
  }

  public void addExtension(ModelQueryExtension extension)
  {  
    if (extension.getType() == ModelQueryExtension.DATA_TYPE_VALUE_EXTENSION)
    {              
      dataTypeValueExtensionTable.put(extension.getId(), extension);
    }
    else if (extension.getType() == ModelQueryExtension.ELEMENT_CONTENT_FILTER)
    {
      elementContentFilterList.add(extension);
    }
  }

  public void removeExtension(ModelQueryExtension extension)
  {
    if (extension.getType() == ModelQueryExtension.DATA_TYPE_VALUE_EXTENSION)
    {  
      dataTypeValueExtensionTable.remove(extension.getId());
    }  
    else if (extension.getType() == ModelQueryExtension.ELEMENT_CONTENT_FILTER)
    {
      elementContentFilterList.remove(extension);
    }
  }
}
