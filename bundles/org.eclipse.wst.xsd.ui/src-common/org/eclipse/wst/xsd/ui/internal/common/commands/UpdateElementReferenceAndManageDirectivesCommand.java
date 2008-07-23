/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.commands;
import org.eclipse.core.resources.IFile;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDDirectivesManager;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;

public class UpdateElementReferenceAndManageDirectivesCommand extends UpdateComponentReferenceAndManageDirectivesCommand
{
  public UpdateElementReferenceAndManageDirectivesCommand(XSDConcreteComponent concreteComponent, String componentName, String componentNamespace, IFile file)
  {
    super(concreteComponent, componentName, componentNamespace, file);
  }

  protected XSDComponent getDefinedComponent(XSDSchema schema, String componentName, String componentNamespace)
  {
    XSDElementDeclaration result = schema.resolveElementDeclaration(componentNamespace, componentName);
    if (result.eContainer() == null)
    {
      result = null;
    }
    return result;
  }

  public void execute()
  {
    try
    {
      beginRecording(concreteComponent.getElement());
      XSDComponent elementDef = computeComponent();
      if (elementDef != null)
      {
        UpdateElementReferenceCommand command = new UpdateElementReferenceCommand(Messages._UI_ACTION_UPDATE_ELEMENT_REFERENCE, (XSDElementDeclaration) concreteComponent,
            (XSDElementDeclaration) elementDef);
        command.execute();
        XSDDirectivesManager.removeUnusedXSDImports(concreteComponent.getSchema());
      }
    }
    catch (Exception e)
    {
    }
    finally
    {
      endRecording();
    }
  }
}
