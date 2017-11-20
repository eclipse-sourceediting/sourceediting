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
package org.eclipse.wst.xml.core.internal.contentmodel.internal.modelqueryimpl;
        
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
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

  public void filterAvailableElementContent(List cmnodes, Element element, CMElementDeclaration ed)
  {
	  filterAvailableElementContent(cmnodes, element, ed, ModelQuery.INCLUDE_CHILD_NODES);
  }

  public void filterAvailableElementContent(List cmnodes, Element element, CMElementDeclaration ed, int includeOptions)
  {
    String contentTypeId = getContentTypeId(element);
    String parentNamespace = element.getNamespaceURI();

	List modelQueryExtensions = modelQueryExtensionRegistry.getApplicableExtensions(contentTypeId, parentNamespace);
	if((includeOptions & ModelQuery.INCLUDE_CHILD_NODES) > 0)
	{
      for (Iterator j = cmnodes.iterator(); j.hasNext(); )
      {
        CMNode cmNode = (CMNode)j.next();  
        String namespace = getNamespace(cmNode);
        String name = cmNode.getNodeName();
      
        boolean include = true;
        for(int k = 0; k < modelQueryExtensions.size() && include; k++) {
        {
            ModelQueryExtension extension = (ModelQueryExtension)modelQueryExtensions.get(k);
            include = extension.isApplicableChildElement(element, namespace, name);
            if (!include)
            {
              // remove the cmNode from the list
              j.remove();
            }
          }
        }
      }
    }
    // add MQE-provided content
    for(int k = 0; k < modelQueryExtensions.size(); k++)
    {
        ModelQueryExtension extension = (ModelQueryExtension)modelQueryExtensions.get(k);
        cmnodes.addAll(Arrays.asList(extension.getAvailableElementContent(element, parentNamespace, includeOptions)));
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
    String contentTypeId = "org.eclipse.core.runtime.xml"; //$NON-NLS-1$
    if (node instanceof IDOMNode)
    {
      IDOMNode domNode = (IDOMNode) node;
      contentTypeId = domNode.getModel().getContentTypeIdentifier();
    }   
    return contentTypeId;
  } 
}
