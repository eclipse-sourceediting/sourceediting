/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.properties;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xml.core.internal.provisional.NameValidator;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public abstract class BasePropertySource implements IPropertySource
{
	protected Element element;
	protected Viewer viewer;
	protected IPropertyDescriptor[] propertyDescriptors;
	protected XSDSchema xsdSchema;
	protected String [] trueFalseComboValues = {
		"",
		"false",
		"true"
	};

  public BasePropertySource()
  {
    
  }

  public DocumentImpl getDocument(Element element)
  {
    return (DocumentImpl) element.getOwnerDocument();
  }
  
  public void beginRecording(String description, Element element)
  {
    getDocument(element).getModel().beginRecording(this, description);
  }
  
  public void endRecording(Element element)
  {
    DocumentImpl doc = (DocumentImpl) getDocument(element);
    
    doc.getModel().endRecording(this);    
  }
  
  
  public BasePropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    this.viewer = viewer;
    this.xsdSchema = xsdSchema;
  }
  
  public BasePropertySource(XSDSchema xsdSchema)
  {
    this.xsdSchema = xsdSchema;
  }
  
  public void setViewer(Viewer viewer)
  {
    this.viewer = viewer;
  }

  public void setInput(Element element)
  {
    this.element = element;
  }

  protected XSDDOMHelper domHelper = new XSDDOMHelper();
  /**
   * Gets the domHelper.
   * @return Returns a XSDDomHelper
   */
  public XSDDOMHelper getDomHelper()
  {
    return domHelper;
  }
  
	protected boolean hasElementChildren(Node parentNode)
	{
		boolean hasChildrenElements = false;
		if (parentNode != null && parentNode.hasChildNodes())
		{
			NodeList nodes = parentNode.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++)
			{
				if (nodes.item(i) instanceof Element)
				{
					hasChildrenElements = true;
					break;
				}
			}
		}
		return hasChildrenElements;
	}
  
  protected boolean validateName(String name)
  {
    return NameValidator.isValid(name);
  }

  // TODO
  protected boolean validateLanguage(String lang)
  {
    return true;
  }
  
  // TODO  
  protected boolean validatePrefix(String prefix)
  {
    return true;
  }
  
}
