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
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDElementCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateAttributeValueCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDDirectivesManager;
import org.eclipse.wst.xsd.ui.internal.search.IXSDSearchConstants;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;

public class XSDSubstitutionGroupEditManager extends XSDElementReferenceEditManager
{

  public XSDSubstitutionGroupEditManager(IFile currentFile, XSDSchema[] schemas)
  {
    super(currentFile, schemas);
  }

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
            Command command = new UpdateAttributeValueCommand(concreteComponent.getElement(), XSDConstants.SUBSTITUTIONGROUP_ATTRIBUTE, elementDec.getQName(concreteComponent.getSchema()), org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_SUBSTITUTION_GROUP);
            command.execute();
          }  
        }  
        else
        {
          Command command = new UpdateAttributeValueCommand(concreteComponent.getElement(), XSDConstants.SUBSTITUTIONGROUP_ATTRIBUTE, ((XSDElementDeclaration)component.getObject()).getQName(concreteComponent.getSchema()), org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_SUBSTITUTION_GROUP);
          command.execute();
        }
        XSDDirectivesManager.removeUnusedXSDImports(concreteComponent.getSchema());
      }
  }
}
