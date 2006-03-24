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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMNamespaceInfoManager;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceInfo;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.w3c.dom.Element;

public class AddAppInfoAttributeCommand extends AddAppInfoCommand
{
  private static DOMNamespaceInfoManager manager = new DOMNamespaceInfoManager();
  private XSDAttributeDeclaration attribute;
  private boolean appInfoAttributeAdded = false;
  private boolean prefixAdded = false;
  private String attributeQName;
  private String namespacePrefix;
  
  XSDConcreteComponent component;


  public AddAppInfoAttributeCommand(String label, XSDConcreteComponent component,
      XSDAttributeDeclaration attribute)
  {
    super(label);
    this.component = component;
    this.attribute = attribute;
  }

  public void execute()
  {
    namespacePrefix = handleNamespacePrefices();
    
    attributeQName = namespacePrefix + ":" + attribute.getName();
    String value = component.getElement().getAttribute(attributeQName);
    if ( value == null) {
      appInfoAttributeAdded = true;
      component.getElement().setAttribute(attributeQName, "");
    }
  }

  public void undo()
  {
    super.undo();
    // TODO (allison) remove the namespace prefix when applicable as well
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
      prefixAdded = true;
      Map prefMapper = component.getSchema().getQNamePrefixToNamespaceMap();
      prefix = "sdo";
      if ( prefMapper.get(prefix) != null){
        int i = 1;
        while ( prefMapper.get(prefix + i) != null)
          i++;
        prefix += i;
      }
    }

    NamespaceInfo info = new NamespaceInfo(attribute.getTargetNamespace(), prefix, "");
    List infoList = new ArrayList(1);
    infoList.add(info);
    manager.addNamespaceInfo(schemaElement, infoList, false);
    return prefix;
  }
}
