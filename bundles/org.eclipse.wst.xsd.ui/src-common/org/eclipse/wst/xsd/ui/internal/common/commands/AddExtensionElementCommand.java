/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.commands;

import java.util.List;
import java.util.Map;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceTable;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.SpecificationForExtensionsSchema;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AddExtensionElementCommand extends AddExtensionCommand
{
  XSDElementDeclaration element;
  Element appInfo;
  Element newElement;

  public AddExtensionElementCommand(String label, XSDConcreteComponent input, XSDElementDeclaration element)
  {
    super(label);
    this.component = input;
    this.element = element;
  }

  public void execute()
  {
    try
    {
      beginRecording(component.getElement());
      super.execute();
      addAnnotationSet(component.getSchema(), extensionsSchemaSpec);
      formatChild(component.getElement());
    }
    finally
    {
      endRecording();
    }
  }

  public void undo()
  {
    super.undo();
    XSDAnnotation xsdAnnotation = XSDCommonUIUtils.getInputXSDAnnotation(component, false);
    xsdAnnotation.getElement().removeChild(appInfo);
    List appInfos = xsdAnnotation.getApplicationInformation();
    appInfos.remove(appInfo);
    xsdAnnotation.updateElement();

  }

  public void setSchemaProperties(SpecificationForExtensionsSchema spec)
  {
    this.extensionsSchemaSpec = spec;
  }

  public void addAnnotationSet(XSDSchema xsdSchema, SpecificationForExtensionsSchema spec)
  {
    XSDAnnotation xsdAnnotation = XSDCommonUIUtils.getInputXSDAnnotation(component, true);
    addAnnotationSet(spec, xsdAnnotation);
  }

  private void addAnnotationSet(SpecificationForExtensionsSchema spec, XSDAnnotation xsdAnnotation)
  {     
    XSDSchema schema= xsdAnnotation.getSchema();
    Element schemaElement = schema.getElement();

    if (xsdAnnotation.getApplicationInformation().size() == 0)
    {
      appInfo = xsdAnnotation.createApplicationInformation(null);
      xsdAnnotation.getElement().appendChild(appInfo);
      List appInfos = xsdAnnotation.getApplicationInformation();
      appInfos.add(appInfo);
    }
    else
    {
      // use the first appInfo
      appInfo = (Element)xsdAnnotation.getApplicationInformation().get(0);
    }

    String prefix = addNamespaceDeclarationIfRequired(schemaElement, "p", spec.getNamespaceURI());

    if (appInfo != null)
    {
      Document doc = appInfo.getOwnerDocument();
     
      newElement = doc.createElementNS(spec.getNamespaceURI(), element.getName());
      newElement.setPrefix(prefix);   
      appInfo.appendChild(newElement);

      xsdAnnotation.updateElement();
    }
  }

  public Object getNewObject()
  {
    return newElement;
  }
  
  /**
   * @deprecated
   */
  protected String createUniquePrefix(XSDConcreteComponent component)
  {
    String prefix = "p"; //$NON-NLS-1$
    Map prefMapper = component.getSchema().getQNamePrefixToNamespaceMap();
    if ( prefMapper.get(prefix) != null){
      int i = 1;
      while ( prefMapper.get(prefix + i) != null)
        i++;
      prefix += i;
    }
    return prefix;
  }  
  
  // TODO... common this up with wsdl.ui
  private String addNamespaceDeclarationIfRequired(Element schemaElement, String prefixHint, String namespace)
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
      while (schemaElement.hasAttribute(attributeName))
      {
        count++;
        prefix = basePrefix + count;
        attributeName = xmlnsColon + prefix;
      }      
      schemaElement.setAttribute(attributeName, namespace);  
    }    
    return prefix;
  }
}
