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

import java.util.List;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.GlobalBuildAction;
import org.eclipse.wst.xsd.ui.internal.XSDEditor;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.RenameComponentProcessor;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.RenameRefactoringWizard;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.w3c.dom.Node;


public class RenameComponentAction extends SelectionDispatchAction {

	private XSDEditor fEditor;
	private XSDNamedComponent fSelectedComponent;
	
	public RenameComponentAction(IWorkbenchSite site) {
		super(site);
		IEditorPart editorPart = site.getPage().getActiveEditor();
		if(editorPart instanceof XSDEditor){
			fEditor = (XSDEditor)editorPart;
			setEnabled(canOperateOn(fEditor));
		}
	}
	
	public static boolean canOperateOn(XSDEditor editor) {
		if (editor == null)
			return false;
		return editor.getEditorInput() != null;
		
	}

	public void selectionChanged(ITextSelection selection) {

		List elements = fEditor.getSelectedNodes();
		if (elements.size() == 1) {
			Object object = elements.get(0);
			setEnabled(canEnable(object));
		}

	}

	protected boolean canEnable(Object selectedObject) {

		if (selectedObject instanceof XSDNamedComponent) {
			fSelectedComponent = (XSDNamedComponent) selectedObject;
		} else if (selectedObject instanceof Node) {
			Node node = (Node) selectedObject;
			XSDConcreteComponent concreteComponent = fEditor.getXSDSchema()
					.getCorrespondingComponent(node);
			if (concreteComponent instanceof XSDNamedComponent) {
				fSelectedComponent = (XSDNamedComponent) concreteComponent;
			}
		}
		if (fSelectedComponent instanceof XSDElementDeclaration) {
			XSDElementDeclaration element = (XSDElementDeclaration) fSelectedComponent;
			if (element.isElementDeclarationReference()) {
				fSelectedComponent = null;
			}
		} 
		return canRun();
	}
	
	public boolean canRun() {

		return fSelectedComponent != null;
	}

	public void run(ISelection selection) {
		if (fSelectedComponent.getName() == null){
			fSelectedComponent.setName(new String());
		}
		if(fSelectedComponent.getSchema() == null){
			fEditor.getXSDSchema().updateElement(true);
		}
		RenameComponentProcessor processor = new RenameComponentProcessor(fSelectedComponent, fSelectedComponent.getName());
		RenameRefactoring refactoring = new RenameRefactoring(processor);
		try {
			RefactoringWizard wizard = new RenameRefactoringWizard(
					refactoring,
					RefactoringMessages.getString("RenameComponentWizard.defaultPageTitle"), //$NON-NLS-1$ TODO: provide correct strings
					RefactoringMessages.getString("RenameComponentWizard.inputPage.description"), //$NON-NLS-1$
					null);
			RefactoringWizardOpenOperation op= new RefactoringWizardOpenOperation(wizard);
			int result= op.run(XSDEditorPlugin.getShell(), wizard.getDefaultPageTitle());
			op.getInitialConditionCheckingStatus();
			triggerBuild();
		} catch (InterruptedException e) {
			// do nothing. User action got cancelled
		}
		
	}

	
	public static void triggerBuild() {
		if (ResourcesPlugin.getWorkspace().getDescription().isAutoBuilding()) {
			new GlobalBuildAction(XSDEditorPlugin.getPlugin().getWorkbench().getActiveWorkbenchWindow(), IncrementalProjectBuilder.INCREMENTAL_BUILD).run();
		}
	}
	
	
	  
}
