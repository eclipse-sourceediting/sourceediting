package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class DOMExtensionTreeLabelProvider extends LabelProvider
{
  protected static final Image DEFAULT_ELEMENT_ICON = XSDEditorPlugin.getXSDImage("icons/XSDElement.gif");
  protected static final Image DEFAULT_ATTR_ICON = XSDEditorPlugin.getXSDImage("icons/XSDAttribute.gif");
    
  public DOMExtensionTreeLabelProvider()
  {    
  }
  
  public Image getImage(Object element)
  {
    ExtensionsSchemasRegistry registry = XSDEditorPlugin.getDefault().getExtensionsSchemasRegistry();
    if (element instanceof Element)
    {
      Element domElement = (Element) element;
      ILabelProvider lp = registry.getLabelProvider(domElement);
      if (lp != null)
      {
        Image img = lp.getImage(domElement);
        if (img != null)
          return img;
      }
      return DEFAULT_ELEMENT_ICON;
    }
    if (element instanceof Attr)
    return DEFAULT_ATTR_ICON;
    return null;
  }

  public String getText(Object input)
  {
    if (input instanceof Element)
    {
      Element domElement = (Element) input;
      return domElement.getLocalName();
    }
    if ( input instanceof Attr){
      return ((Attr) input).getLocalName();
    }
    return "";
  }
}
