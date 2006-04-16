package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import java.util.Collection;
import java.util.HashMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;


public class DOMExtensionDetailsContentProvider implements ExtensionDetailsContentProvider
{
  private static Object[] EMPTY_ARRAY = {};
  private static String[] EMPTY_STRING_ARRAY = {};  
  public Object[] getItems(Object input)
  {  
    HashMap resultMap = new HashMap();
    if (input instanceof Element)
    {  
      Element element = (Element)input;
      NamedNodeMap attributes = element.getAttributes();
      for (int i = 0; i < attributes.getLength(); i++)
      {
        Attr attr = (Attr)attributes.item(i);    
        if (!"xmlns".equals(attr.getName()) && !"xmlns".equals(attr.getPrefix()))
        {    
          resultMap.put(attr.getName(), new DOMExtensionItem(attr));
        }
      }  
      ModelQuery modelQuery = ModelQueryUtil.getModelQuery(element.getOwnerDocument());
      if (modelQuery != null)
      {  
        CMElementDeclaration ed = modelQuery.getCMElementDeclaration(element);
        if (ed != null)
        {
          CMNamedNodeMap attrMap = ed.getAttributes();
          if (attrMap != null)
          {
            int attrMapLength = attrMap.getLength();
            for (int i = 0; i < attrMapLength; i++) 
            {
              CMAttributeDeclaration ad = (CMAttributeDeclaration) attrMap.item(i);
              if (resultMap.get(ad.getNodeName()) == null)
              {  
                resultMap.put(ad.getNodeName(), new DOMExtensionItem(element, ad));
              }  
            }  
          }
          //
          int contentType = ed.getContentType(); 
          if ((contentType == CMElementDeclaration.PCDATA || 
              contentType == CMElementDeclaration.PCDATA) &&
              ed.getDataType() != null)              
          {
            resultMap.put("text()", new DOMExtensionItem(element, ed));
          }  
        }
      }      
      Collection collection = resultMap.values();
      DOMExtensionItem[] items = new DOMExtensionItem[collection.size()];
      resultMap.values().toArray(items);
      return items;      
    }
    else if (input instanceof Attr)
    {
      Attr attr = (Attr)input;
      DOMExtensionItem item = new DOMExtensionItem(attr);
      DOMExtensionItem[] items = {item};
      return items;
    }  
    return EMPTY_ARRAY;
  }
  

  public String getName(Object item)
  {
    if (item instanceof DOMExtensionItem)
    {
      return ((DOMExtensionItem)item).getName();
    }  
    return "";
  }

  public String getValue(Object item)
  {
    if (item instanceof DOMExtensionItem)
    {
      return ((DOMExtensionItem)item).getValue();
    }  
    return "";
  }
  
  public String[] getPossibleValues(Object item)
  {
    if (item instanceof DOMExtensionItem)
    {
      return ((DOMExtensionItem)item).getPossibleValues();
    }  
    return EMPTY_STRING_ARRAY;    
  }
}
