/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor.handlers;


import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.refactor.structure.MakeAnonymousTypeGlobalCommand;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RefactoringWizardMessages;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.w3c.dom.Node;


public class MakeAnonymousTypeGobalHandler extends RefactoringHandler
{
  private String parentName;
  private boolean isComplexType = true;

  public Object doExecute(ISelection selection, XSDSchema schema)
  {
    if (selection != null)
    {
      Object selectedObject = ((StructuredSelection)selection).getFirstElement();
      
      if (selectedObject instanceof XSDBaseAdapter)
      {
        selectedObject = ((XSDBaseAdapter)selectedObject).getTarget();
      }
      
      XSDConcreteComponent concreteComp = null;

      if (selectedObject instanceof Node)
      {
        Node node = (Node)selectedObject;
        concreteComp = schema.getCorrespondingComponent(node);
      }
      else if (selectedObject instanceof XSDConcreteComponent)
      {
        concreteComp = ((XSDConcreteComponent)selectedObject);
      }

      if (concreteComp != null)
      {
        if (concreteComp instanceof XSDComplexTypeDefinition)
        {

          isComplexType = true;
          XSDComplexTypeDefinition typeDef = (XSDComplexTypeDefinition)concreteComp;
          XSDConcreteComponent parent = typeDef.getContainer();
          if (parent instanceof XSDElementDeclaration)
          {
            parentName = ((XSDElementDeclaration)parent).getName();
            run(selection, schema, typeDef);
          }
          else if (concreteComp instanceof XSDSimpleTypeDefinition)
          {
            isComplexType = false;
            XSDSimpleTypeDefinition simpleTypeDef = (XSDSimpleTypeDefinition)concreteComp;
            XSDConcreteComponent parentComp = simpleTypeDef.getContainer();
            if (parentComp instanceof XSDElementDeclaration)
            {
              parentName = ((XSDElementDeclaration)parent).getName();

            }
            else if (parent instanceof XSDAttributeDeclaration)
            {
              parentName = ((XSDAttributeDeclaration)parent).getName();

            }
            run(selection, schema, simpleTypeDef);
          }
        }
      }
    }

    return null;
  }

  private String getNewDefaultName()
  {
    if (parentName != null && !"".equals(parentName)) { //$NON-NLS-1$
      if (isComplexType)
      {
        return parentName + "ComplexType"; //$NON-NLS-1$
      }
      else
      {
        return parentName + "SimpleType"; //$NON-NLS-1$
      }
    }
    else
    {
      if (isComplexType)
      {
        return "NewComplexType"; //$NON-NLS-1$
      }
      else
      {
        return "NewSimpleType"; //$NON-NLS-1$
      }
    }

  }

  public void run(ISelection selection, XSDSchema schema, XSDTypeDefinition selectedComponent)
  {
    if (selectedComponent == null)
    {
      return;
    }

    if (selectedComponent.getSchema() == null)
    {
      schema.updateElement(true);
    }
    DocumentImpl doc = (DocumentImpl)selectedComponent.getElement().getOwnerDocument();
    doc.getModel().beginRecording(this, RefactoringWizardMessages.MakeAnonymousTypeGlobalAction_text);
    MakeAnonymousTypeGlobalCommand command = new MakeAnonymousTypeGlobalCommand(selectedComponent, getNewDefaultName());
    command.run();
    doc.getModel().endRecording(this);
  }
}