/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceTable;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class ExtensibleAddExtensionCommand extends AddExtensionCommand
{
  protected XSDElementDeclaration element;
  protected Element appInfo;
  protected Element newElement;

  public ExtensibleAddExtensionCommand(String label)
  {
    super(label);
  }

  public XSDElementDeclaration getXSDElementDeclarationElement()
  {
    return element;
  }
  
  public Element getNewElement()
  {
    return newElement;
  }

  public Element getAppInfo()
  {
    return appInfo;
  }
  
  public XSDSchema getXSDSchema()
  {
    return component.getSchema();    
  }

  public void setInputs(XSDConcreteComponent input, XSDElementDeclaration element)
  {
    this.component = input;
    this.element = element;
  }

  public void execute()
  {
    try
    {
      beginRecording(component.getElement());
      super.execute();
      
      addCustomizedActions();
      
      formatChild(component.getElement());
    }
    finally
    {
      endRecording();
    }
  }
  
  public void addCustomizedActions()
  {
   
  }

  public void undo()
  {
    super.undo();
//    XSDAnnotation xsdAnnotation = XSDCommonUIUtils.getInputXSDAnnotation(component, false);
//    xsdAnnotation.getElement().removeChild(appInfo);
//    List appInfos = xsdAnnotation.getApplicationInformation();
//    appInfos.remove(appInfo);
//    xsdAnnotation.updateElement();
  }

  public Object getNewObject()
  {
    return newElement;
  }
  
  // TODO... common this up with wsdl.ui
  protected String addNamespaceDeclarationIfRequired(Element schemaElement, String prefixHint, String namespace)
  {
    String prefix = null;      
    NamespaceTable namespaceTable = new NamespaceTable(schemaElement.getOwnerDocument());
    namespaceTable.addElement(schemaElement);
    prefix = namespaceTable.getPrefixForURI(namespace);
    if (prefix == null)
    { 
      String basePrefix = prefixHint;
      prefix = basePrefix;
      String xmlnsColon = "xmlns:"; //$NON-NLS-1$
      String attributeName = xmlnsColon + prefix;
      int count = 0;
      while (schemaElement.getAttribute(attributeName) != null)
      {
        count++;
        prefix = basePrefix + count;
        attributeName = xmlnsColon + prefix;
      }      
      schemaElement.setAttribute(attributeName, namespace);  
    }    
    return prefix;
  }
  
  protected void addSchemaAttribute(Element schemaElement, String namespace, String attributeName, String attributeValue)
  {
    String prefix = null;
    NamespaceTable namespaceTable = new NamespaceTable(schemaElement.getOwnerDocument());
    namespaceTable.addElement(schemaElement);
    prefix = namespaceTable.getPrefixForURI(namespace);
    try
    {
      if (prefix != null)
      {
        schemaElement.setAttribute(prefix + ":" + attributeName, attributeValue);  
      }
      else
      {
        schemaElement.setAttribute(attributeName, attributeValue);
      }
    }
    catch (Exception e)
    {
      
    }
  }

  protected void addSourceAttributeToAppInfo(String namespace)
  {
    if (appInfo.getAttributeNode(XSDConstants.SOURCE_ATTRIBUTE) == null)
      appInfo.setAttribute(XSDConstants.SOURCE_ATTRIBUTE, namespace);
  }
  
  /**
   * 
   * @param element
   * @param attributeName
   * @param attributeValue
   */
  protected void addAttribute(Element element, String attributeName, String attributeValue)
  {
    element.setAttribute(attributeName, attributeValue);
  }
}
