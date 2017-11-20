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
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.common.ui.internal.search.dialogs.IComponentDescriptionProvider;
import org.eclipse.wst.xsd.ui.internal.adt.edit.ComponentReferenceEditManager;
import org.eclipse.wst.xsd.ui.internal.adt.edit.ComponentReferenceEditManager2;
import org.eclipse.wst.xsd.ui.internal.adt.edit.IComponentDialog;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDComplexTypeDefinitionCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDSimpleTypeDefinitionCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateTypeReferenceAndManageDirectivesCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateTypeReferenceCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDDirectivesManager;
import org.eclipse.wst.xsd.ui.internal.dialogs.NewTypeDialog;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDSearchListDialogDelegate;
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

public class XSDTypeReferenceEditManager implements ComponentReferenceEditManager, ComponentReferenceEditManager2
{  
  protected IFile currentFile;
  protected XSDSchema[] schemas;
  private static ComponentSpecification result[];
  private IADTObject referencer;
  
  public XSDTypeReferenceEditManager(IFile currentFile, XSDSchema[] schemas)
  {
    this.currentFile = currentFile;
    this.schemas = schemas;
  }
  
  public void addToHistory(ComponentSpecification component)
  {
    // TODO (cs) implement me!    
  }
  
  public IADTObject getReferencer()
  {
    return referencer;
  }
  
  public void setReferencer(IADTObject referencer)
  {
    this.referencer = referencer;
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
    NewTypeDialog result = null;
    if (schemas.length > 0) 
    {
      result = new NewTypeDialog(schemas[0]);
    }
    else 
    {
      result = new NewTypeDialog();
    }
    if (referencer instanceof IField)
    {
      IField field = (IField)referencer;
      if (XSDConstants.ATTRIBUTE_ELEMENT_TAG.equals(field.getKind()))
      {
        result.allowComplexType(false);
      }
      String fieldName = field.getName();
      if (fieldName != null)
      {  
        fieldName = fieldName.trim();
        if (fieldName.length() > 0)
        {  
          result.setDefaultName(NLS.bind(Messages._UI_VALUE_NEW_TYPE, fieldName));
        }  
      }  
    }
    return result;
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
        if (component.getName() == null // This means we set to anonymous type
        		&& concreteComponent instanceof XSDElementDeclaration)
        {
          XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
          XSDElementDeclaration elementDeclaration = (XSDElementDeclaration) concreteComponent;
          // TODO (cs) we should use actions here so that UNDO/REDO can work nicely
          // with the proper undo descriptions
          if (component.getMetaName() == IXSDSearchConstants.COMPLEX_TYPE_META_NAME)
          {
            XSDComplexTypeDefinition complexType = factory.createXSDComplexTypeDefinition();            
	  	      elementDeclaration.setAnonymousTypeDefinition(complexType);
          }
          else
          {
        	  XSDSimpleTypeDefinition simpleType = factory.createXSDSimpleTypeDefinition();
        	  simpleType.setBaseTypeDefinition(
        	    elementDeclaration.getSchema().getSchemaForSchema().resolveSimpleTypeDefinition(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "string") ); //$NON-NLS-1$
  	  	    elementDeclaration.setAnonymousTypeDefinition(simpleType);
          }
		      elementDeclaration.getElement().removeAttribute("type"); //TODO use external literal string
        }
        else if (component.getMetaName() == IXSDSearchConstants.COMPLEX_TYPE_META_NAME)
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
          command.setLabel(Messages._UI_ACTION_SET_TYPE);
          command.execute();
        }
        XSDDirectivesManager.removeUnusedXSDImports(concreteComponent.getSchema());
      }  
      else
      {  
        Command command = new UpdateTypeReferenceAndManageDirectivesCommand(concreteComponent, component.getName(), component.getQualifier(), component.getFile());
        command.setLabel(Messages._UI_ACTION_SET_TYPE);
        command.execute();
      }  
    }  
  }
}
