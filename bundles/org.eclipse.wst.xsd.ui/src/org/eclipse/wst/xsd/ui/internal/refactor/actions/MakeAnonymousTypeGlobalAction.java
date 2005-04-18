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

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.commands.MakeAnonymousTypeGlobalCommand;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.RenameRefactoringWizard;
import org.eclipse.wst.xsd.ui.internal.refactor.structure.MakeTypeGlobalProcessor;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.w3c.dom.Node;

public class MakeAnonymousTypeGlobalAction extends SelectionDispatchAction {

	private String fParentName;
	private boolean isComplexType = true;
	private XSDTypeDefinition fSelectedComponent;
	
	public MakeAnonymousTypeGlobalAction(ISelectionProvider selectionProvider, XSDSchema schema) {
		super(selectionProvider);
		setText(XSDEditorPlugin.getXSDString("_UI_ACTION_MAKE_ANONYMOUS_TYPE_GLOBAL")); //$NON-NLS-1$
		setSchema(schema);
	}
	
	public boolean canRun() {

		return fSelectedComponent != null;
	}
	

	private String getNewDefaultName(){
		if(fParentName != null && !"".equals(fParentName)){
			if(isComplexType){
				return fParentName + "ComplexType";
			}
			else{
				return fParentName + "SimpleType";
			}
		}
		else{
			if(isComplexType){
				return "NewComplexType";
			}
			else{
				return "NewSimpleType";
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
			return canEnable((XSDConcreteComponent)selectedObject);
		}
		else if (selectedObject instanceof Node) {
			Node node = (Node) selectedObject;
			XSDConcreteComponent concreteComponent = getSchema()
					.getCorrespondingComponent(node);
			return canEnable(concreteComponent);
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
					RefactoringMessages.getString("RenameComponentWizard.defaultPageTitle"), //$NON-NLS-1$ TODO: provide correct strings
					RefactoringMessages.getString("RenameComponentWizard.inputPage.description"), //$NON-NLS-1$
					null);
			RefactoringWizardOpenOperation op= new RefactoringWizardOpenOperation(wizard);
			int result= op.run(XSDEditorPlugin.getShell(), wizard.getDefaultPageTitle());
			op.getInitialConditionCheckingStatus();
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
						XSDEditorPlugin
								.getXSDString("_UI_ACTION_MAKE_ANONYMOUS_TYPE_GLOBAL"));
		MakeAnonymousTypeGlobalCommand command = new MakeAnonymousTypeGlobalCommand(
				fSelectedComponent, getNewDefaultName());
		command.run();
		doc.getModel().endRecording(this);
	}

}
