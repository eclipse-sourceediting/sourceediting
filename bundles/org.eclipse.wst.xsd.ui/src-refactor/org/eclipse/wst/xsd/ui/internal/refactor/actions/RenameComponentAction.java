/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor.actions;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.ui.actions.GlobalBuildAction;
import org.eclipse.wst.common.ui.internal.dialogs.SaveDirtyFilesDialog;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringComponent;
import org.eclipse.wst.xsd.ui.internal.refactor.XMLRefactoringComponent;
import org.eclipse.wst.xsd.ui.internal.refactor.handlers.RenameHandler;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.RenameComponentProcessor;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RefactoringWizardMessages;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RenameRefactoringWizard;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDTypeDefinition;
import org.w3c.dom.Node;

/**
 * @deprecated Use {@link RenameHandler} 
 */
public class RenameComponentAction extends XSDSelectionDispatchAction {

	private XSDNamedComponent selectedComponent;

	public RenameComponentAction(ISelection selection,
			Object aModel) {
		super(selection, aModel);
	
	}

	protected boolean canEnable(XSDConcreteComponent selectedObject) {

		selectedComponent = null;
		if (selectedObject instanceof XSDNamedComponent) {
			selectedComponent = (XSDNamedComponent) selectedObject;

			// if it's element reference, then this action is not appropriate
			if (selectedComponent instanceof XSDElementDeclaration) {
				XSDElementDeclaration element = (XSDElementDeclaration) selectedComponent;
				if (element.isElementDeclarationReference()) {
					selectedComponent = null;
				}
			}
			if(selectedComponent instanceof XSDTypeDefinition){
				XSDTypeDefinition type = (XSDTypeDefinition) selectedComponent;
				XSDConcreteComponent parent = type.getContainer();
				if (parent instanceof XSDElementDeclaration) {
					XSDElementDeclaration element = (XSDElementDeclaration) parent;
					if(element.getAnonymousTypeDefinition().equals(type)){
						selectedComponent = null;
					}
				}
				else if(parent instanceof XSDAttributeDeclaration) {
					XSDAttributeDeclaration element = (XSDAttributeDeclaration) parent;
					if(element.getAnonymousTypeDefinition().equals(type)){
						selectedComponent = null;
					}
				}
			}
		}

		return canRun();
	}

	protected boolean canEnable(Object selectedObject) {

		if (selectedObject instanceof XSDConcreteComponent) 
		{
			return canEnable((XSDConcreteComponent) selectedObject) && super.canEnable(selectedObject);
		} else if (selectedObject instanceof Node) 
		{
			Node node = (Node) selectedObject;
			if (getSchema() != null) 
			{
				XSDConcreteComponent concreteComponent = getSchema()
						.getCorrespondingComponent(node);
				return canEnable(concreteComponent) && super.canEnable(concreteComponent);
			}
		}
		return false;

	}

	public boolean canRun() {

		return selectedComponent != null;
	}

	public void run(ISelection selection) {
		if (selectedComponent.getName() == null) {
			selectedComponent.setName(new String());
		}
		if (selectedComponent.getSchema() == null) {
			if (getSchema() != null) {
				getSchema().updateElement(true);
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
		
		RenameComponentProcessor processor = new RenameComponentProcessor(
				component, selectedComponent.getName());
		RenameRefactoring refactoring = new RenameRefactoring(processor);
		try {
			RefactoringWizard wizard = new RenameRefactoringWizard(
					refactoring,
					RefactoringWizardMessages.RenameComponentWizard_defaultPageTitle, 
					RefactoringWizardMessages
							.RenameComponentWizard_inputPage_description, 
					null);
			RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(
					wizard);
			op.run(XSDEditorPlugin.getShell(), wizard
					.getDefaultPageTitle());
			
			// TODO (cs) I'm not sure why we need to do this.  See bug 145700
			//triggerBuild();
		} catch (InterruptedException e) {
			// do nothing. User action got cancelled
		}

	}

	public static void triggerBuild() {
		if (ResourcesPlugin.getWorkspace().getDescription().isAutoBuilding()) {
			new GlobalBuildAction(XSDEditorPlugin.getPlugin().getWorkbench()
					.getActiveWorkbenchWindow(),
					IncrementalProjectBuilder.INCREMENTAL_BUILD).run();
		}
	}	
}
