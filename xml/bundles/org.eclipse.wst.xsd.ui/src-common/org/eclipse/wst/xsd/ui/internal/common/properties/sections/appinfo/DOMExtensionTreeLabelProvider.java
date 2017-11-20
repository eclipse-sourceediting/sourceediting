/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.custom.NodeCustomizationRegistry;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class DOMExtensionTreeLabelProvider extends LabelProvider
{
  protected static final Image DEFAULT_ELEMENT_ICON = XSDEditorPlugin.getXSDImage("icons/XSDElement.gif"); //$NON-NLS-1$
  protected static final Image DEFAULT_ATTR_ICON = XSDEditorPlugin.getXSDImage("icons/XSDAttribute.gif"); //$NON-NLS-1$
    
  public DOMExtensionTreeLabelProvider()
  {    
  }
  
  public Image getImage(Object element)
  {
    NodeCustomizationRegistry registry = XSDEditorPlugin.getDefault().getNodeCustomizationRegistry();
    if (element instanceof Element)
    {
      Element domElement = (Element) element;
      String namespace = domElement.getNamespaceURI();
      if (namespace != null)
      {  
        ILabelProvider lp = registry.getLabelProvider(namespace);
        if (lp != null)
        {
          Image img = lp.getImage(domElement);
          if (img != null)
            return img;
        }  
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
      String textVal = "";

      if (domElement.hasChildNodes())
      {
        Node node = domElement.getChildNodes().item(0);
        if (node instanceof Text)
        {
          Text textNode = (Text) node;
          try
          {
            if (!textNode.getNodeValue().contains("\n"))
              textVal = " [" + textNode.getNodeValue() + "]";
          }
          catch (DOMException e)
          {
            textVal = "";
          }
        }
      }
      return domElement.getLocalName() + textVal;
    }
    if ( input instanceof Attr){
      return ((Attr) input).getLocalName();
    }
    return ""; //$NON-NLS-1$
  }
}
