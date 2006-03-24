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
package org.eclipse.wst.xsd.ui.common.commands;

import java.util.List;

import org.eclipse.wst.xsd.ui.common.properties.sections.appinfo.SpecificationForAppinfoSchema;
import org.eclipse.wst.xsd.ui.common.util.XSDCommonUIUtils;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AddAppInfoElementCommand extends AddAppInfoCommand
{
  XSDConcreteComponent input;
  XSDElementDeclaration element;
  Element appInfo;

  public AddAppInfoElementCommand(String label, XSDConcreteComponent input, XSDElementDeclaration element)
  {
    super(label);
    this.input = input;
    this.element = element;
  }

  public void execute()
  {
    super.execute();
    addAnnotationSet(input.getSchema(), appInfoSchemaSpec);
  }

  public void undo()
  {
    super.undo();
    XSDAnnotation xsdAnnotation = XSDCommonUIUtils.getInputXSDAnnotation(input, false);
    xsdAnnotation.getElement().removeChild(appInfo);
    List appInfos = xsdAnnotation.getApplicationInformation();
    appInfos.remove(appInfo);
    xsdAnnotation.updateElement();

  }

  public void setSchemaProperties(SpecificationForAppinfoSchema appInfoSchemaSpec)
  {
    this.appInfoSchemaSpec = appInfoSchemaSpec;
  }

  public void addAnnotationSet(XSDSchema xsdSchema, SpecificationForAppinfoSchema asiSpec)
  {
    XSDAnnotation xsdAnnotation = XSDCommonUIUtils.getInputXSDAnnotation(input, true);
    addAnnotationSet(asiSpec, xsdAnnotation);
  }

  private void addAnnotationSet(SpecificationForAppinfoSchema asiProperties, XSDAnnotation xsdAnnotation)
  {
    appInfo = xsdAnnotation.createApplicationInformation(asiProperties.getNamespaceURI());

    if (appInfo != null)
    {
      Document doc = appInfo.getOwnerDocument();

      Element rootElement = doc.createElementNS(asiProperties.getNamespaceURI(), element.getName());
      //TODO: create unique prefix
      rootElement.setPrefix("asi");

      Attr nsURIAttribute = doc.createAttribute("xmlns:asi");
      nsURIAttribute.setValue(asiProperties.getNamespaceURI());
      rootElement.setAttributeNode(nsURIAttribute);
      appInfo.appendChild(rootElement);

      xsdAnnotation.getElement().appendChild(appInfo);
      List appInfos = xsdAnnotation.getApplicationInformation();
      appInfos.add(appInfo);
      xsdAnnotation.updateElement();
    }
  }
}
