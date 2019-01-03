/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.commands;

import java.util.Iterator;

import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;

public abstract class AddXSDSchemaDirectiveCommand extends BaseCommand
{
  protected XSDSchema xsdSchema;
  
  public AddXSDSchemaDirectiveCommand(String label)
  {
    super(label);
  }

  public void undo()
  {
    super.undo();
  }

  protected boolean adopt(XSDConcreteComponent model)
  {
    return false;
  }
  
  protected int findNextPositionToInsert()
  {
    int index = 0;
    for (Iterator i = xsdSchema.getContents().iterator(); i.hasNext(); )
    {
      Object o = i.next();
      if (o instanceof XSDSchemaDirective)
      {
        index ++;
      }
      else
      {
        break;
      }
    }
    return index;
  }

  public void execute()
  {
    ensureSchemaElement(xsdSchema);
 }
}
