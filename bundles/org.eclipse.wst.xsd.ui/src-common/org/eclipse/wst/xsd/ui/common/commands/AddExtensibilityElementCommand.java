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

import org.eclipse.gef.commands.Command;
import org.eclipse.wst.xsd.ui.common.properties.sections.appinfo.SpecificationForAppinfoSchema;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AddExtensibilityElementCommand extends Command
{
  Element input, elementToAdd;
  SpecificationForAppinfoSchema appInfoSchemaSpec;

  public AddExtensibilityElementCommand(String label, Element input, Element elementToAdd)
  {
    super(label);
    this.input = input;
    this.elementToAdd = elementToAdd;
  }

  public void execute()
  {
    super.execute();
    addElement();
  }

  public void undo()
  {
    super.undo();
    // TODO
  }

  public void setSchemaProperties(SpecificationForAppinfoSchema appInfoSchemaSpec)
  {
    this.appInfoSchemaSpec = appInfoSchemaSpec;
  }

  private void addElement()
  {
    if (input != null)
    {
      Document doc = input.getOwnerDocument();
      String name = elementToAdd.getAttribute("name");
      try
      {
        Element rootElement = doc.createElementNS(appInfoSchemaSpec.getNamespaceURI(), name);
        String prefix = input.getPrefix();
        rootElement.setPrefix(prefix);
        String xmlns = (prefix == null || prefix.equals("")) ? "xmlns" : "xmlns:" + prefix;
        Attr nsURIAttribute = doc.createAttribute(xmlns);
        nsURIAttribute.setValue(appInfoSchemaSpec.getNamespaceURI());
        rootElement.setAttributeNode(nsURIAttribute);
        input.appendChild(rootElement);

      }
      catch (Exception e)
      {

      }

    }
  }

}
