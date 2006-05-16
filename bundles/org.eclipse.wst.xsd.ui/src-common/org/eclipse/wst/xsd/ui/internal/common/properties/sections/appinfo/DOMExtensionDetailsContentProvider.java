package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
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
  private static Object[] EMPTY_ARRAY = {};
  private static String[] EMPTY_STRING_ARRAY = {};

  public Object[] getItems(Object input)
  {
    HashMap resultMap = new HashMap();
    if (input instanceof Element)
    {
      Element element = (Element) input;
      NamedNodeMap attributes = element.getAttributes();
      for (int i = 0; i < attributes.getLength(); i++)
      {
        Attr attr = (Attr) attributes.item(i);
        if (!"xmlns".equals(attr.getName()) && !"xmlns".equals(attr.getPrefix())) //$NON-NLS-1$ //$NON-NLS-2$
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
          if ((contentType == CMElementDeclaration.PCDATA || contentType == CMElementDeclaration.PCDATA) && ed.getDataType() != null)
          {
            resultMap.put("text()", new DOMExtensionItem(element, ed)); //$NON-NLS-1$
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
      return items;
    }
    else if (input instanceof Attr)
    {
      // TODO (cs) do we actually utilize this case?
      Attr attr = (Attr) input;
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
