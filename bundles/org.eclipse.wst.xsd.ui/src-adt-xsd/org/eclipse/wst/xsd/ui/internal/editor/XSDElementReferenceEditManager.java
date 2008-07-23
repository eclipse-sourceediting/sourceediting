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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.gef.commands.Command;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentDescriptionProvider;
import org.eclipse.wst.xsd.ui.internal.adt.edit.ComponentReferenceEditManager;
import org.eclipse.wst.xsd.ui.internal.adt.edit.IComponentDialog;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDElementCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateElementReferenceAndManageDirectivesCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateElementReferenceCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDDirectivesManager;
import org.eclipse.wst.xsd.ui.internal.dialogs.NewElementDialog;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDSearchListDialogDelegate;
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;

public class XSDElementReferenceEditManager implements ComponentReferenceEditManager
{  
  protected IFile currentFile;
  protected XSDSchema[] schemas;
  
  public XSDElementReferenceEditManager(IFile currentFile, XSDSchema[] schemas)
  {
    this.currentFile = currentFile;
    this.schemas = schemas;
  }
  
  public void addToHistory(ComponentSpecification component)
  {
    // TODO (cs) implement me!    
  }

  public IComponentDialog getBrowseDialog()
  {
    //XSDSetExistingTypeDialog dialog = new XSDSetExistingTypeDialog(currentFile, schemas);
    //return dialog;
    XSDSearchListDialogDelegate dialogDelegate = 
    	new XSDSearchListDialogDelegate(XSDSearchListDialogDelegate.ELEMENT_META_NAME, currentFile, schemas);
    return dialogDelegate;
  }

  public IComponentDescriptionProvider getComponentDescriptionProvider()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public ComponentSpecification[] getHistory()
  {
    // TODO (cs) implement this properly - should this history be global or local to each editor?
    // This is something we should play around with ourselves to see what feels right.
    //
    List list = new ArrayList();
    ComponentSpecification result[] = new ComponentSpecification[list.size()];
    list.toArray(result);
    return result;
  }

  public IComponentDialog getNewDialog()
  {
	  if (schemas.length > 0) {
		  return new NewElementDialog(schemas[0]);
	  }
	  else {
		  return new NewElementDialog();
	  }
  }

  public ComponentSpecification[] getQuickPicks()
  {
    // TODO (cs) implement this properly - we should be providing a list of the 
    // most 'common' built in schema types here
    // I believe Trung will be working on a perference page to give us this list
    // for now let's hard code some values
    //
    List list = new ArrayList();
    
    ComponentSpecification result[] = new ComponentSpecification[list.size()];
    list.toArray(result);
    return result;
  }
  
//TODO not changed yet
  public void modifyComponentReference(Object referencingObject, ComponentSpecification component)
  {
    XSDElementDeclaration concreteComponent = null;
    if (referencingObject instanceof Adapter)
    {
      Adapter adapter = (Adapter)referencingObject;
      if (adapter.getTarget() instanceof XSDElementDeclaration)
      {
        concreteComponent = (XSDElementDeclaration)adapter.getTarget();
      }
    }
    else if (referencingObject instanceof XSDConcreteComponent)
    {
      concreteComponent = (XSDElementDeclaration) referencingObject;
    }
    if (concreteComponent != null)
    {
        if (component.isNew())
        {  
          XSDElementDeclaration elementDec = null;
          if (component.getMetaName() == IXSDSearchConstants.ELEMENT_META_NAME)
          {  
            AddXSDElementCommand command = new AddXSDElementCommand(Messages._UI_ACTION_ADD_ELEMENT, concreteComponent.getSchema());
            command.setNameToAdd(component.getName());
            command.execute();
            elementDec = (XSDElementDeclaration) command.getAddedComponent();
          }
          if (elementDec != null)
          {
            Command command = new UpdateElementReferenceCommand(Messages._UI_ACTION_UPDATE_ELEMENT_REFERENCE, concreteComponent, elementDec);
            command.execute();
          }
          XSDDirectivesManager.removeUnusedXSDImports(concreteComponent.getSchema());
        }  
        else
        {
          Command command = new UpdateElementReferenceAndManageDirectivesCommand(concreteComponent, component.getName(), component.getQualifier(), component.getFile());
          command.setLabel(Messages._UI_ACTION_UPDATE_ELEMENT_REFERENCE);
          command.execute();
        }  
      }
  }
}
