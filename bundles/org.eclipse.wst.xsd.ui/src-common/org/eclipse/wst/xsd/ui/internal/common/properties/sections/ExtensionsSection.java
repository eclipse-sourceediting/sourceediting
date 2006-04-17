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
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import org.eclipse.gef.commands.Command;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddExtensionAttributeCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddExtensionCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddExtensionElementCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.RemoveExtensionAttributerCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.RemoveExtensionElementCommand;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.DOMExtensionTreeLabelProvider;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.ExtensionsSchemasRegistry;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.XSDExtensionTreeContentProvider;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ExtensionsSection extends AbstractExtensionsSection
{
  public ExtensionsSection()
  {
    super();
    setExtensionTreeLabelProvider(new DOMExtensionTreeLabelProvider());
    setExtensionTreeContentProvider(new XSDExtensionTreeContentProvider());
  }

  protected AddExtensionCommand getAddExtensionCommand(Object o)
  {
    AddExtensionCommand addExtensionCommand = null;
    if (o instanceof XSDElementDeclaration)
    {
      XSDElementDeclaration element = (XSDElementDeclaration) o;
      addExtensionCommand = new AddExtensionElementCommand("Add AppInfo Element", (XSDConcreteComponent) input, element);
    }
    else if (o instanceof XSDAttributeDeclaration)
    {
      XSDAttributeDeclaration attribute = (XSDAttributeDeclaration) o;
      addExtensionCommand = new AddExtensionAttributeCommand("Add AppInfo Attribute", (XSDConcreteComponent) input, attribute);
    }
    return addExtensionCommand;
  }

  protected Command getRemoveExtensionCommand(Object o)
  {
    Command command = null;
    if (o instanceof Element)
    {
      XSDAnnotation xsdAnnotation = (XSDAnnotation) extensionTreeViewer.getInput();
      Node appInfoElement = ((Element) o).getParentNode();
      command = new RemoveExtensionElementCommand("Remove AppInfo Element", xsdAnnotation, appInfoElement);
    }
    else if (o instanceof Attr)
    {
      Element hostElement = ((Attr) o).getOwnerElement();
      command = new RemoveExtensionAttributerCommand("Remove AppInfo Attribute", hostElement, (Attr) o);
    }
    return command;
  }  
  
  protected ExtensionsSchemasRegistry getExtensionsSchemasRegistry()
  {
    return XSDEditorPlugin.getDefault().getExtensionsSchemasRegistry();
  }
}