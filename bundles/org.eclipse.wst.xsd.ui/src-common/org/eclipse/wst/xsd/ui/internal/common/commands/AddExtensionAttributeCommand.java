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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMNamespaceInfoManager;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceInfo;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.w3c.dom.Element;

public class AddExtensionAttributeCommand extends AddExtensionCommand
{
  private static DOMNamespaceInfoManager manager = new DOMNamespaceInfoManager();
  private XSDAttributeDeclaration attribute;
  private boolean appInfoAttributeAdded = false;
  private String attributeQName;
  private String namespacePrefix;

  public AddExtensionAttributeCommand(String label, XSDConcreteComponent component,
      XSDAttributeDeclaration attribute)
  {
    super(label);
    this.component = component;
    this.attribute = attribute;
  }

  public void execute()
  {
    try
    {
      beginRecording(component.getElement());
      super.execute();
      namespacePrefix = handleNamespacePrefices();

      attributeQName = namespacePrefix + ":" + attribute.getName(); //$NON-NLS-1$
     
      if (!(component.getElement().hasAttribute(attributeQName)))
      {
        appInfoAttributeAdded = true;
        component.getElement().setAttribute(attributeQName, ""); //$NON-NLS-1$
      }
    }
    finally
    {
      endRecording();
    }
  }

  public void undo()
  {
    super.undo();
    if (appInfoAttributeAdded){
      component.getElement().removeAttribute(attributeQName);
    }
  }

  /** Create a namespace prefix if needed, other wise retrieve 
   * a predefined namespace prefix
   * @return   */
  private String handleNamespacePrefices()
  {
    Element schemaElement = component.getSchema().getElement();
    String prefix = null;
    
    // If target namespace of the attribute already exists
    List namespacePrefices = manager.getNamespaceInfoList(schemaElement);
    for (int i = 0; i < namespacePrefices.size(); i++){
      NamespaceInfo info = (NamespaceInfo) namespacePrefices.get(i);
      if ( info.uri.equals(attribute.getTargetNamespace())) {
        prefix = info.prefix;
      }
    }
    
    // Create unquie namespace prefix
    if ( prefix == null){
      prefix = createUniquePrefix(component);
    }

    NamespaceInfo info = new NamespaceInfo(attribute.getTargetNamespace(), prefix, ""); //$NON-NLS-1$
    List infoList = new ArrayList(1);
    infoList.add(info);
    manager.addNamespaceInfo(schemaElement, infoList, false);
    return prefix;
  }
  
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
}
