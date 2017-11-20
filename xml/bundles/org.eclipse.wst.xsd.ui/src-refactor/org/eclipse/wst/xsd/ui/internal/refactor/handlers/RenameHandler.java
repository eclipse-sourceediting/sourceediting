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
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.wst.common.ui.internal.dialogs.SaveDirtyFilesDialog;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringComponent;
import org.eclipse.wst.xsd.ui.internal.refactor.XMLRefactoringComponent;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.RenameComponentProcessor;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RefactoringWizardMessages;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RenameRefactoringWizard;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Node;


public class RenameHandler extends RefactoringHandler
{

  protected Object doExecute(ISelection selection, XSDSchema schema)
  {
    return execute(selection, schema);
  }

  public Object execute(ISelection selection, XSDSchema schema)
  {
    if (selection != null)
    {

      Object selectedObject = ((StructuredSelection)selection).getFirstElement();
      if (selectedObject instanceof XSDBaseAdapter)
      {
        selectedObject = ((XSDBaseAdapter)selectedObject).getTarget();
      }

      if (selectedObject instanceof XSDNamedComponent)
      {
        run(selection, schema, (XSDNamedComponent)selectedObject);
      }
      else if (selectedObject instanceof Node)
      {
        Node node = (Node)selectedObject;
        if (schema != null)
        {
          XSDConcreteComponent concreteComponent = schema.getCorrespondingComponent(node);
          if (concreteComponent instanceof XSDNamedComponent)
          {
            run(selection, schema, (XSDNamedComponent)concreteComponent);
          }
        }
      }
    }
    return null;

  }

  public void run(ISelection selection, XSDSchema schema, XSDNamedComponent selectedComponent)
  {
    if (selectedComponent.getName() == null)
    {
      selectedComponent.setName(new String());
    }
    if (selectedComponent.getSchema() == null)
    {
      if (schema != null)
      {
        schema.updateElement(true);
      }

    }

    boolean rc = SaveDirtyFilesDialog.saveDirtyFiles();
    if (!rc)
    {
      return;
    }
    RefactoringComponent component = new XMLRefactoringComponent(
      selectedComponent,
      (IDOMElement)selectedComponent.getElement(),
      selectedComponent.getName(),
      selectedComponent.getTargetNamespace());

    RenameComponentProcessor processor = new RenameComponentProcessor(component, selectedComponent.getName());
    RenameRefactoring refactoring = new RenameRefactoring(processor);
    try
    {
      RefactoringWizard wizard = new RenameRefactoringWizard(
        refactoring,
        RefactoringWizardMessages.RenameComponentWizard_defaultPageTitle,
        RefactoringWizardMessages.RenameComponentWizard_inputPage_description,
        null);
      RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
      op.run(XSDEditorPlugin.getShell(), wizard.getDefaultPageTitle());

    }
    catch (InterruptedException e)
    {
      // do nothing. User action got canceled
    }
  }
}
