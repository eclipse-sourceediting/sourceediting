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
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDComplexTypeDefinitionCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDSimpleTypeDefinitionCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateTypeReferenceAndManageDirectivesCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateTypeReferenceCommand;
import org.eclipse.wst.xsd.ui.internal.dialogs.NewTypeDialog;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDSearchListDialogDelegate;
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;

public class XSDTypeReferenceEditManager implements ComponentReferenceEditManager
{  
  protected IFile currentFile;
  protected XSDSchema[] schemas;
  
  private static ComponentSpecification result[];
  
  public XSDTypeReferenceEditManager(IFile currentFile, XSDSchema[] schemas)
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
    XSDSearchListDialogDelegate dialogDelegate = new XSDSearchListDialogDelegate(XSDSearchListDialogDelegate.TYPE_META_NAME, currentFile, schemas);
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
		  return new NewTypeDialog(schemas[0]);
	  }
	  else {
		  return new NewTypeDialog();
	  }
  }

  public ComponentSpecification[] getQuickPicks()
  {
    if (result != null)
      return result;

    // TODO (cs) implement this properly - we should be providing a list of the 
    // most 'common' built in schema types here
    // I believe Trung will be working on a perference page to give us this list
    // for now let's hard code some values
    //
    List list = new ArrayList();
    list.add(new ComponentSpecification(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "boolean", null)); //$NON-NLS-1$
    list.add(new ComponentSpecification(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "date", null)); //$NON-NLS-1$
    list.add(new ComponentSpecification(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "dateTime", null));     //$NON-NLS-1$
    list.add(new ComponentSpecification(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "double", null)); //$NON-NLS-1$
    list.add(new ComponentSpecification(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "float", null));  //$NON-NLS-1$
    list.add(new ComponentSpecification(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "hexBinary", null)); //$NON-NLS-1$
    list.add(new ComponentSpecification(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "int", null));     //$NON-NLS-1$
    list.add(new ComponentSpecification(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "string", null)); //$NON-NLS-1$
    list.add(new ComponentSpecification(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "time", null));        //$NON-NLS-1$
    result = new ComponentSpecification[list.size()];
    list.toArray(result);
    return result;
  }
  

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
    
    if (concreteComponent != null)
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
          Command command = new UpdateTypeReferenceCommand(concreteComponent, td);
          command.execute();
        }  
      }  
      else
      {  
        Command command = new UpdateTypeReferenceAndManageDirectivesCommand(concreteComponent, component.getName(), component.getQualifier(), component.getFile());
        command.execute();
      }  
    }  
  }
}
