/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.gef.commands.Command;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.adt.edit.ComponentReferenceEditManager;
import org.eclipse.wst.xsd.ui.internal.adt.edit.IComponentDialog;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDAttributeDeclarationCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateAttributeReferenceAndManagerDirectivesCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateAttributeReferenceCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDDirectivesManager;
import org.eclipse.wst.xsd.ui.internal.dialogs.NewAttributeDialog;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDSearchListDialogDelegate;
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;

public class XSDAttributeReferenceEditManager extends XSDElementReferenceEditManager implements ComponentReferenceEditManager
{
  public XSDAttributeReferenceEditManager(IFile currentFile, XSDSchema[] schemas)
  {
    super(currentFile, schemas);
  }

  public IComponentDialog getBrowseDialog()
  {
    XSDSearchListDialogDelegate dialogDelegate = 
      new XSDSearchListDialogDelegate(XSDSearchListDialogDelegate.ATTRIBUTE_META_NAME, currentFile, schemas);
    return dialogDelegate;
  }

  public IComponentDialog getNewDialog()
  {
    if (schemas.length > 0) {
      return new NewAttributeDialog(schemas[0]);
    }
    else {
      return new NewAttributeDialog();
    }
  }

  public void modifyComponentReference(Object referencingObject, ComponentSpecification referencedComponent)
  {
    XSDAttributeDeclaration concreteComponent = null;
    if (referencingObject instanceof Adapter)
    {
      Adapter adapter = (Adapter)referencingObject;
      if (adapter.getTarget() instanceof XSDAttributeDeclaration)
      {
        concreteComponent = (XSDAttributeDeclaration)adapter.getTarget();
      }
    }
    else if (referencingObject instanceof XSDConcreteComponent)
    {
      concreteComponent = (XSDAttributeDeclaration) referencingObject;
    }
    if (concreteComponent != null)
    {
        if (referencedComponent.isNew())
        {  
          XSDAttributeDeclaration attributeDec = null;
          if (referencedComponent.getMetaName() == IXSDSearchConstants.ATTRIBUTE_META_NAME)
          {  
            AddXSDAttributeDeclarationCommand command = new AddXSDAttributeDeclarationCommand(Messages._UI_ACTION_ADD_ATTRIBUTE, concreteComponent.getSchema());
            command.setNameToAdd(referencedComponent.getName());
            command.execute();
            attributeDec = (XSDAttributeDeclaration) command.getAddedComponent();
          }
          if (attributeDec != null)
          {
            Command command = new UpdateAttributeReferenceCommand(Messages._UI_ACTION_UPDATE_ATTRIBUTE_REFERENCE, concreteComponent, attributeDec);
            command.execute();
          }
          XSDDirectivesManager.removeUnusedXSDImports(concreteComponent.getSchema());
        }  
        else
        {
          Command command = new UpdateAttributeReferenceAndManagerDirectivesCommand(concreteComponent, referencedComponent.getName(), referencedComponent.getQualifier(), referencedComponent.getFile());
          command.setLabel(Messages._UI_ACTION_UPDATE_ATTRIBUTE_REFERENCE);
          command.execute();
        }  
      }
  }
}
