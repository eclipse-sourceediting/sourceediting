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

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xsd.ui.internal.XSDEditor;
import org.eclipse.wst.xsd.ui.internal.commands.MakeLocalElementGlobalCommand;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.w3c.dom.Node;

public class MakeLocalElementGlobalAction extends SelectionDispatchAction {

	private XSDEditor fEditor;

	XSDElementDeclaration fSelectedComponent;

	public MakeLocalElementGlobalAction(IWorkbenchSite site) {
		super(site);
		setText(RefactoringMessages
				.getString("MakeLocalElementGlobalAction.text")); //$NON-NLS-1$

		IEditorPart editorPart = site.getPage().getActiveEditor();
		if (editorPart instanceof XSDEditor) {
			fEditor = (XSDEditor) editorPart;
		}
		setEnabled(canOperateOn(fEditor));
	}

	public MakeLocalElementGlobalAction(XSDEditor editor) {
		this(editor.getEditorSite());
		fEditor = editor;
		setEnabled(canOperateOn(fEditor));
	}

	public static boolean canOperateOn(XSDEditor editor) {
		if (editor == null)
			return false;
		return editor.getEditorInput() != null;

	}

	public void selectionChanged(ITextSelection selection) {

		if(fEditor == null){
			setEnabled(false);
			return;
		}
		List elements = fEditor.getSelectedNodes();
		if (elements.size() == 1) {
			Object object = elements.get(0);
			setEnabled(canEnable(object));
		}

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
		
		if(fEditor == null) return false;
		if (selectedObject instanceof XSDConcreteComponent) {
			return canEnable((XSDConcreteComponent)selectedObject);
		}
		else if (selectedObject instanceof Node) {
			Node node = (Node) selectedObject;
			XSDConcreteComponent concreteComponent = fEditor.getXSDSchema()
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
