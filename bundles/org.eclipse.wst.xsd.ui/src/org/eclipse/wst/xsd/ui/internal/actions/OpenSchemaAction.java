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
package org.eclipse.wst.xsd.ui.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.wst.xml.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xsd.ui.internal.util.OpenOnSelectionHelper;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.impl.XSDImportImpl;
import org.w3c.dom.Node;


public class OpenSchemaAction extends Action
{
  XSDConcreteComponent component;
  public OpenSchemaAction(String label, XSDConcreteComponent component)
  {
    super(label);
    this.component = component;
  }

  public void run()
  {
    if (component != null)
    {
      revealObject();
    }
  }

  boolean lastResult = false;
  protected boolean revealObject()
  {
    Node element = component.getElement();
    String schemaLocation = "";
    XSDSchemaDirective dir;
    if (component instanceof XSDSchemaDirective)
    {
      dir = (XSDSchemaDirective)component;
      // force load of imported schema
      if (dir instanceof XSDImportImpl)
      {
        ((XSDImportImpl)dir).importSchema();
      }
      if (dir.getResolvedSchema() != null)
      {
        schemaLocation = URIHelper.removePlatformResourceProtocol(dir.getResolvedSchema().getSchemaLocation());
        if (schemaLocation != null)
        {
          OpenOnSelectionHelper.openXSDEditor(schemaLocation);
        }
      }
    }
    return lastResult;
  }
}
