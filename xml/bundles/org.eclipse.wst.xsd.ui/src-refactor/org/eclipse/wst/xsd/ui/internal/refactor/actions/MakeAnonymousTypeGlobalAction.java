/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.refactor.handlers.MakeAnonymousTypeGobalHandler;
import org.eclipse.wst.xsd.ui.internal.refactor.structure.MakeAnonymousTypeGlobalCommand;
import org.eclipse.wst.xsd.ui.internal.refactor.structure.MakeTypeGlobalProcessor;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RefactoringWizardMessages;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RenameRefactoringWizard;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.w3c.dom.Node;

/**
 * @deprecated Use {@link MakeAnonymousTypeGobalHandler}
 */
public class MakeAnonymousTypeGlobalAction extends XSDSelectionDispatchAction {

	private String fParentName;
	private boolean isComplexType = true;
	private XSDTypeDefinition fSelectedComponent;
	
	public MakeAnonymousTypeGlobalAction(ISelection selection, XSDSchema schema) {
		super(selection, schema);
		setText(RefactoringWizardMessages.MakeAnonymousTypeGlobalAction_text); //$NON-NLS-1$
	}
	
	public boolean canRun() {

		return fSelectedComponent != null;
	}
	

	private String getNewDefaultName(){
		if(fParentName != null && !"".equals(fParentName)){ //$NON-NLS-1$
			if(isComplexType){
				return fParentName + "ComplexType"; //$NON-NLS-1$
			}
			else{
				return fParentName + "SimpleType"; //$NON-NLS-1$
			}
		}
		else{
			if(isComplexType){
				return "NewComplexType"; //$NON-NLS-1$
			}
			else{
				return "NewSimpleType"; //$NON-NLS-1$
			}
		}
		
	}
	private boolean canEnable(XSDConcreteComponent xsdComponent){
		if (xsdComponent instanceof XSDComplexTypeDefinition) {
			fSelectedComponent = (XSDComplexTypeDefinition)xsdComponent;
			isComplexType = true;
			XSDComplexTypeDefinition typeDef = (XSDComplexTypeDefinition) xsdComponent;
			XSDConcreteComponent parent = typeDef.getContainer();
			if(parent instanceof XSDElementDeclaration){
				fParentName = ((XSDElementDeclaration)parent).getName();
				return true;
			}
		} 
		else if (xsdComponent instanceof XSDSimpleTypeDefinition){
			fSelectedComponent = (XSDSimpleTypeDefinition)xsdComponent;
			isComplexType = false;
			XSDSimpleTypeDefinition typeDef = (XSDSimpleTypeDefinition) xsdComponent;
			XSDConcreteComponent parent = typeDef.getContainer();
			if(parent instanceof XSDElementDeclaration){
				fParentName = ((XSDElementDeclaration)parent).getName();
				return true;
			}
			else if(parent instanceof XSDAttributeDeclaration){
				fParentName = ((XSDAttributeDeclaration)parent).getName();
				return true;
			}
			
		}
		return false;
	}

	protected boolean canEnable(Object selectedObject) {
		
		if (selectedObject instanceof XSDConcreteComponent) {
			return canEnable((XSDConcreteComponent)selectedObject) && super.canEnable(selectedObject);
		}
		else if (selectedObject instanceof Node) {
			Node node = (Node) selectedObject;
			XSDConcreteComponent concreteComponent = getSchema().getCorrespondingComponent(node);
			return canEnable(concreteComponent) && super.canEnable(concreteComponent);
		
		}
		return false;
		
	}

	public void run1() {
		
		if(fSelectedComponent == null){
			return;
		}
		
		if(fSelectedComponent.getSchema() == null){
			getSchema().updateElement(true);
		}
		MakeTypeGlobalProcessor processor = new MakeTypeGlobalProcessor(fSelectedComponent, getNewDefaultName());
		RenameRefactoring refactoring = new RenameRefactoring(processor);
		try {
			RefactoringWizard wizard = new RenameRefactoringWizard(
					refactoring,
					RefactoringWizardMessages.RenameComponentWizard_defaultPageTitle, // TODO: provide correct strings
					RefactoringWizardMessages.RenameComponentWizard_inputPage_description, null);
			RefactoringWizardOpenOperation op= new RefactoringWizardOpenOperation(wizard);
			op.run(XSDEditorPlugin.getShell(), wizard.getDefaultPageTitle());
			//triggerBuild();
		} catch (InterruptedException e) {
			// do nothing. User action got cancelled
		}
		
	}
	
	public void run(){
		if(fSelectedComponent == null){
			return;
		}
		
		if(fSelectedComponent.getSchema() == null){
			getSchema().updateElement(true);
		}
		DocumentImpl doc = (DocumentImpl) fSelectedComponent.getElement().getOwnerDocument();
		doc.getModel().beginRecording(
						this,
						RefactoringWizardMessages.MakeAnonymousTypeGlobalAction_text);
		MakeAnonymousTypeGlobalCommand command = new MakeAnonymousTypeGlobalCommand(
				fSelectedComponent, getNewDefaultName());
		command.run();
		doc.getModel().endRecording(this);
	}
	
	

}
