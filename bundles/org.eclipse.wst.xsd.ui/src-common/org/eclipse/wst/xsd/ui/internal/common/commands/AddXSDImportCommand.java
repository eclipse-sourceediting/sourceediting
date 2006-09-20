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
package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchema;

public class AddXSDImportCommand extends AddXSDSchemaDirectiveCommand
{
  public AddXSDImportCommand(String label, XSDSchema schema)
  {
    super(label);
    this.xsdSchema = schema;
  }

  public void execute()
  {
    try
    {
      super.execute();
      // Add this after if we don't have a DOM Node yet
      beginRecording(xsdSchema.getElement());
      XSDImport xsdImport = XSDFactory.eINSTANCE.createXSDImport();
      xsdSchema.getContents().add(findNextPositionToInsert(), xsdImport);
      addedXSDConcreteComponent = xsdImport;
      formatChild(xsdSchema.getElement());
    }
    finally
    {
      endRecording();
    }
  }
}
