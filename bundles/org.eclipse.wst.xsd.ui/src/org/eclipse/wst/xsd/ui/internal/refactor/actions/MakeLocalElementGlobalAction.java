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
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xsd.ui.internal.commands.MakeLocalElementGlobalCommand;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Node;

public class MakeLocalElementGlobalAction extends SelectionDispatchAction {

	XSDElementDeclaration fSelectedComponent;

	public MakeLocalElementGlobalAction(ISelectionProvider selectionProvider, XSDSchema schema) {
		super(selectionProvider);
		setText(RefactoringMessages.getString("MakeLocalElementGlobalAction.text")); //$NON-NLS-1$
		setSchema(schema);
	}
	
	public boolean canRun() {

		return fSelectedComponent != null;
	}

	protected boolean canEnable(XSDConcreteComponent selectedObject) {

		fSelectedComponent = null;
		if (selectedObject instanceof XSDElementDeclaration) {
			XSDElementDeclaration element = (XSDElementDeclaration) selectedObject;
			if (!element.isElementDeclarationReference() && !element.isGlobal()) {
				fSelectedComponent = element;
			}
		} 
		return canRun();
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


	public void run() {
		DocumentImpl doc = (DocumentImpl) fSelectedComponent.getElement()
				.getOwnerDocument();
		doc.getModel().beginRecording(this, getText());
		MakeLocalElementGlobalCommand command = new MakeLocalElementGlobalCommand(
				fSelectedComponent);
		command.run();
		doc.getModel().endRecording(this);
	}

}
