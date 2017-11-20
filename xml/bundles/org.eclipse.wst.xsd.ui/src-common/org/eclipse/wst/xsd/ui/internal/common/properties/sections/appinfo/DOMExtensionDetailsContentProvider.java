/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import java.text.Collator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.ui.internal.tabletree.TreeContentHelper;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.custom.DefaultListNodeEditorConfiguration;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.custom.NodeCustomizationRegistry;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.custom.NodeEditorConfiguration;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.custom.NodeEditorProvider;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class DOMExtensionDetailsContentProvider implements ExtensionDetailsContentProvider
{
  private static final Object[] EMPTY_ARRAY = {};
  private static final String[] EMPTY_STRING_ARRAY = {};
  private static final String XMLNS = "xmlns"; //$NON-NLS
  private static final String TEXT_NODE_KEY = "text()"; //$NON-NLS

  public Object[] getItems(Object input)
  {
    HashMap resultMap = new HashMap();
    if (input instanceof Element)
    {
      Element element = (Element) input;
      
      // here we compute items for the attributes that physically in the document 
      //
      NamedNodeMap attributes = element.getAttributes();
      for (int i = 0; i < attributes.getLength(); i++)
      {
        Attr attr = (Attr) attributes.item(i);
        if (!XMLNS.equals(attr.getName()) && !XMLNS.equals(attr.getPrefix())) //$NON-NLS-1$ //$NON-NLS-2$
        {
          resultMap.put(attr.getName(), DOMExtensionItem.createItemForElementAttribute(element, attr));
        }
      }
     
      // here we compute an item for the text node that is physically in the document 
      //      
      String textNodeValue = new TreeContentHelper().getNodeValue(element);
      if (textNodeValue != null)
      {  
        resultMap.put(TEXT_NODE_KEY, DOMExtensionItem.createItemForElementText(element));
      }  
      
      ModelQuery modelQuery = ModelQueryUtil.getModelQuery(element.getOwnerDocument());
      if (modelQuery != null)
      {
        CMElementDeclaration ed = modelQuery.getCMElementDeclaration(element);
        if (ed != null)
        {
          // here we compute items for the attributes that may be added to the document according to the grammar 
          //           
          List list = modelQuery.getAvailableContent(element, ed, ModelQuery.INCLUDE_ATTRIBUTES);
          for (Iterator i = list.iterator(); i.hasNext(); )
          {  
              CMAttributeDeclaration ad = (CMAttributeDeclaration)i.next();
              if (ad != null && resultMap.get(ad.getNodeName()) == null)
              {
                resultMap.put(ad.getNodeName(), DOMExtensionItem.createItemForElementAttribute(element, ad));
              }            
          }
          if (resultMap.get(TEXT_NODE_KEY) == null)
          {
            // here we compute an item for the text node that may be added to the document according to the grammar 
            //                  
            int contentType = ed.getContentType();
            if (contentType == CMElementDeclaration.PCDATA || contentType == CMElementDeclaration.MIXED)
            {
              resultMap.put(TEXT_NODE_KEY, DOMExtensionItem.createItemForElementText(element));
            }
          }  
        }
      }
      Collection collection = resultMap.values();
      // initialize the editor information for each item
      //
      for (Iterator i = collection.iterator(); i.hasNext();)
      {
        initPropertyEditorConfiguration((DOMExtensionItem) i.next());
      }
      DOMExtensionItem[] items = new DOMExtensionItem[collection.size()];
      resultMap.values().toArray(items);
      
      // here we sort the list alphabetically
      //
      if (items.length > 0)
      {  
        Comparator comparator = new Comparator()
        {
          public int compare(Object arg0, Object arg1)
          {         
            DOMExtensionItem a = (DOMExtensionItem)arg0;
            DOMExtensionItem b = (DOMExtensionItem)arg1;
            
            // begin special case to ensure 'text nodes' come last
            if (a.isTextValue() && !b.isTextValue())
            {
              return 1;   
            }
            else if (b.isTextValue() && !a.isTextValue())
            {
              return -1;
            }  
            // end special case
            else
            {          
              return Collator.getInstance().compare(a.getName(), b.getName());
            }  
          }
        };
        Arrays.sort(items, comparator);
      }  
      return items;
    }
    else if (input instanceof Attr)
    {
      Attr attr = (Attr) input;
      DOMExtensionItem item = DOMExtensionItem.createItemForAttributeText(attr.getOwnerElement(), attr);
      DOMExtensionItem[] items = {item};
      return items;
    }
    return EMPTY_ARRAY;
  }

  public String getName(Object item)
  {
    if (item instanceof DOMExtensionItem)
    {
      return ((DOMExtensionItem) item).getName();
    }
    return ""; //$NON-NLS-1$
  }

  public String getValue(Object item)
  {
    if (item instanceof DOMExtensionItem)
    {
      return ((DOMExtensionItem) item).getValue();
    }
    return ""; //$NON-NLS-1$
  }

  public String[] getPossibleValues(Object item)
  {
    if (item instanceof DOMExtensionItem)
    {
      return ((DOMExtensionItem) item).getPossibleValues();
    }
    return EMPTY_STRING_ARRAY;
  }

  protected void initPropertyEditorConfiguration(DOMExtensionItem item)
  {
    String namespace = item.getNamespace();
    String name = item.getName();
    String parentName = item.getParentName();
    NodeEditorConfiguration configuration = null;
    if (namespace != null)
    {
      // TODO (cs) remove reference to XSDEditorPlugin... make generic
      // perhaps push down the xml.ui ?
      //
      NodeCustomizationRegistry registry = XSDEditorPlugin.getDefault().getNodeCustomizationRegistry();
      NodeEditorProvider provider= registry.getNodeEditorProvider(namespace);      
      if (provider != null)
      {
        configuration = provider.getNodeEditorConfiguration(parentName, name);
        if (configuration != null)
        {  
          configuration.setParentNode(item.getParentNode());
          if (item.getNode() != null)
          {
            configuration.setNode(item.getNode());
          }
        }  
      }
    }
    String[] values = item.getPossibleValues();
    if (values != null && values.length > 1)
    {
      configuration = new DefaultListNodeEditorConfiguration(values);
    }
    
    // Note that it IS expected that the configaration may be null
    //
    item.setPropertyEditorConfiguration(configuration);
  }
}
