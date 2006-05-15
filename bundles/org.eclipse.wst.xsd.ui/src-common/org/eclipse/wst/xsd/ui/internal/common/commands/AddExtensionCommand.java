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

import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.SpecificationForExtensionsSchema;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;

public class AddExtensionCommand extends BaseCommand
{
  protected SpecificationForExtensionsSchema extensionsSchemaSpec;
  protected XSDConcreteComponent component;

  protected AddExtensionCommand(String label)
  {
    super(label);
  }

  public void setSchemaProperties(SpecificationForExtensionsSchema appInfoSchemaSpec)
  {
    this.extensionsSchemaSpec = appInfoSchemaSpec;
  }
  
  public Object getNewObject()
  {
    return null;
  }

  public void execute()
  {
    if (component instanceof XSDSchema)
    {
      ensureSchemaElement((XSDSchema)component);
    }
    
    super.execute();
  }
  
  
}
