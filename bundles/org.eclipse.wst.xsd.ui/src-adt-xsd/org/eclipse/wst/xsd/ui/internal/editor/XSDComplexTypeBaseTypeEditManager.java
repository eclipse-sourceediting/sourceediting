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
package org.eclipse.wst.xsd.ui.internal.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.gef.commands.Command;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.adt.edit.IComponentDialog;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDComplexTypeDefinitionCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDSimpleTypeDefinitionCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.SetBaseTypeAndManagerDirectivesCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.SetBaseTypeCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDDirectivesManager;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDSearchListDialogDelegate;
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

public class XSDComplexTypeBaseTypeEditManager extends XSDTypeReferenceEditManager
{
  public XSDComplexTypeBaseTypeEditManager(IFile currentFile, XSDSchema[] schemas)
  {
    super(currentFile, schemas);
  }

  public ComponentSpecification[] getQuickPicks()
  {
    return super.getQuickPicks();
  }
  
  public IComponentDialog getBrowseDialog()
  {
    XSDSearchListDialogDelegate dialogDelegate = new XSDSearchListDialogDelegate(XSDSearchListDialogDelegate.TYPE_META_NAME, currentFile, schemas);
    return dialogDelegate;
  }

  // TODO common this up
  public void modifyComponentReference(Object referencingObject, ComponentSpecification component)
  {
    XSDConcreteComponent concreteComponent = null;
    if (referencingObject instanceof Adapter)
    {
      Adapter adpater = (Adapter)referencingObject;
      if (adpater.getTarget() instanceof XSDConcreteComponent)
      {
        concreteComponent = (XSDConcreteComponent)adpater.getTarget();
      }
    }
    else if (referencingObject instanceof XSDConcreteComponent)
    {
      concreteComponent = (XSDConcreteComponent) referencingObject;
    }
    
    if (concreteComponent instanceof XSDComplexTypeDefinition)
    {
      if (component.isNew())
      {  
        XSDTypeDefinition td = null;
        if (component.getMetaName() == IXSDSearchConstants.COMPLEX_TYPE_META_NAME)
        {  
          AddXSDComplexTypeDefinitionCommand command = new AddXSDComplexTypeDefinitionCommand(Messages._UI_ACTION_ADD_COMPLEX_TYPE, concreteComponent.getSchema());
          command.setNameToAdd(component.getName());
          command.execute();
          td = command.getCreatedComplexType();
        }
        else
        {
          AddXSDSimpleTypeDefinitionCommand command = new AddXSDSimpleTypeDefinitionCommand(Messages._UI_ACTION_ADD_SIMPLE_TYPE, concreteComponent.getSchema());
          command.setNameToAdd(component.getName());
          command.execute();
          td = command.getCreatedSimpleType();
        }  
        if (td != null)
        {
          Command command = new SetBaseTypeCommand(concreteComponent, td);
          command.execute();
        }
        XSDDirectivesManager.removeUnusedXSDImports(concreteComponent.getSchema());
      }  
      else
      {  
        Command command = new SetBaseTypeAndManagerDirectivesCommand(concreteComponent, component.getName(), component.getQualifier(), component.getFile());
        command.setLabel(Messages._UI_ACTION_SET_BASE_TYPE);
        command.execute();
      }
    }
    else if (concreteComponent instanceof XSDSimpleTypeDefinition)
    {
      if (component.isNew())
      {  
        XSDTypeDefinition td = null;
        if (component.getMetaName() == IXSDSearchConstants.SIMPLE_TYPE_META_NAME)
        {  
          AddXSDSimpleTypeDefinitionCommand command = new AddXSDSimpleTypeDefinitionCommand(Messages._UI_ACTION_ADD_SIMPLE_TYPE, concreteComponent.getSchema());
          command.setNameToAdd(component.getName());
          command.execute();
          td = command.getCreatedSimpleType();
        }
        if (td != null)
        {
          Command command = new SetBaseTypeCommand(concreteComponent, td);
          command.execute();
        }
        XSDDirectivesManager.removeUnusedXSDImports(concreteComponent.getSchema());
      }  
      else
      {  
        Command command = new SetBaseTypeAndManagerDirectivesCommand(concreteComponent, component.getName(), component.getQualifier(), component.getFile());
        command.execute();
      }
    }

  }

}
