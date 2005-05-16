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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension.ModelQueryExtension;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension.ModelQueryExtensionManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;



public class ModelQueryExtensionManagerImpl implements ModelQueryExtensionManager
{           
  protected ModelQueryExtensionRegistry modelQueryExtensionRegistry = new ModelQueryExtensionRegistry();

  public List getDataTypeValues(Element element, CMNode cmNode)
  {    
    List list = new ArrayList();      
    String contentTypeId = getContentTypeId(element);
    String namespace = getNamespace(cmNode);
    String name = cmNode.getNodeName();
    
    for (Iterator i = modelQueryExtensionRegistry.getApplicableExtensions(contentTypeId, namespace).iterator(); i.hasNext();)
    {
      ModelQueryExtension extension = (ModelQueryExtension)i.next();
      String[] values = null;
      if (cmNode.getNodeType() == CMNode.ATTRIBUTE_DECLARATION)
      {  
        values = extension.getAttributeValues(element, namespace, name);
      }
      else
      {
        values = extension.getElementValues(element, namespace, name);        
      }
      if (values != null)
      {
        list.addAll(Arrays.asList(values));
      }  
    }  
    return list;  
  }                               

  public void filterAvailableElementContent(List list, Element element, CMElementDeclaration ed)
  {
    String contentTypeId = getContentTypeId(element);
    String parentNamespace = element.getNamespaceURI();

    for (Iterator j = list.iterator(); j.hasNext(); )
    {
      CMNode cmNode = (CMNode)j.next();  
      String namespace = getNamespace(cmNode);
      String name = cmNode.getNodeName();
      
      boolean include = true;
      for (Iterator k = modelQueryExtensionRegistry.getApplicableExtensions(contentTypeId, parentNamespace).iterator(); k.hasNext();)
      {
        ModelQueryExtension extension = (ModelQueryExtension)k.next();
        include = extension.isApplicableChildElement(element, namespace, name);
        if (!include)
        {
          break;
        }  
      }  
      if (!include)
      {
        // remove the cmNode form the list
        j.remove();
      }       
    }   
  }
  
  private String getNamespace(CMNode cmNode)
  {
    String namespace = null;
    CMDocument cmDocument = (CMDocument)cmNode.getProperty("CMDocument"); //$NON-NLS-1$
    if (cmDocument != null)          
    {     
      namespace = (String)cmDocument.getProperty("http://org.eclipse.wst/cm/properties/targetNamespaceURI");    //$NON-NLS-1$
    }
    return namespace;
  }
  
  private String getContentTypeId(Node node)
  {
    String contentTypeId = "org.eclipse.core.runtime.xml";
    if (node instanceof IDOMNode)
    {
      try
      {
        IDOMNode domNode = (IDOMNode)node;
        contentTypeId = domNode.getModel().getContentTypeIdentifier();
      }
      catch (Exception e)
      {        
      }
    }   
    return contentTypeId;
  } 
}
