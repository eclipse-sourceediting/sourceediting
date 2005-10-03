/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor.actions;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.ui.actions.GlobalBuildAction;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.RenameComponentProcessor;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.RenameRefactoringWizard;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.w3c.dom.Node;

public class RenameComponentAction extends SelectionDispatchAction {

	private XSDNamedComponent fSelectedComponent;

	public RenameComponentAction(ISelectionProvider selectionProvider,
			XSDSchema schema) {
		super(selectionProvider);
		setSchema(schema);
	}

	protected boolean canEnable(XSDConcreteComponent selectedObject) {

		fSelectedComponent = null;
		if (selectedObject instanceof XSDNamedComponent) {
			fSelectedComponent = (XSDNamedComponent) selectedObject;

			// if it's element reference, then this action is not appropriate
			if (fSelectedComponent instanceof XSDElementDeclaration) {
				XSDElementDeclaration element = (XSDElementDeclaration) fSelectedComponent;
				if (element.isElementDeclarationReference()) {
					fSelectedComponent = null;
				}
			}
			if(fSelectedComponent instanceof XSDTypeDefinition){
				XSDTypeDefinition type = (XSDTypeDefinition) fSelectedComponent;
				XSDConcreteComponent parent = type.getContainer();
				if (parent instanceof XSDElementDeclaration) {
					XSDElementDeclaration element = (XSDElementDeclaration) parent;
					if(element.getAnonymousTypeDefinition().equals(type)){
						fSelectedComponent = null;
					}
				}
				else if(parent instanceof XSDAttributeDeclaration) {
					XSDAttributeDeclaration element = (XSDAttributeDeclaration) parent;
					if(element.getAnonymousTypeDefinition().equals(type)){
						fSelectedComponent = null;
					}
				}
			}
		}

		return canRun();
	}

	protected boolean canEnable(Object selectedObject) {

		if (selectedObject instanceof XSDConcreteComponent) {
			return canEnable((XSDConcreteComponent) selectedObject);
		} else if (selectedObject instanceof Node) {
			Node node = (Node) selectedObject;
			if (getSchema() != null) {
				XSDConcreteComponent concreteComponent = getSchema()
						.getCorrespondingComponent(node);
				return canEnable(concreteComponent);
			}
		}
		return false;

	}

	public boolean canRun() {

		return fSelectedComponent != null;
	}

	public void run(ISelection selection) {
		if (fSelectedComponent.getName() == null) {
			fSelectedComponent.setName(new String());
		}
		if (fSelectedComponent.getSchema() == null) {
			if (getSchema() != null) {
				getSchema().updateElement(true);
			}

		}
		RenameComponentProcessor processor = new RenameComponentProcessor(
				fSelectedComponent, fSelectedComponent.getName());
		RenameRefactoring refactoring = new RenameRefactoring(processor);
		try {
			RefactoringWizard wizard = new RenameRefactoringWizard(
					refactoring,
					RefactoringMessages
							.getString("RenameComponentWizard.defaultPageTitle"), //$NON-NLS-1$ TODO: provide correct strings
					RefactoringMessages
							.getString("RenameComponentWizard.inputPage.description"), //$NON-NLS-1$
					null);
			RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(
					wizard);
			op.run(XSDEditorPlugin.getShell(), wizard
					.getDefaultPageTitle());
			op.getInitialConditionCheckingStatus();
			triggerBuild();
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
