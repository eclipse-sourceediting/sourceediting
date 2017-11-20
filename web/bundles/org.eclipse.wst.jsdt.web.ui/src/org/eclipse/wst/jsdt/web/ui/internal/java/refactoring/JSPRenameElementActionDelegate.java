/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.java.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.IFunction;
import org.eclipse.wst.jsdt.core.IPackageFragment;
import org.eclipse.wst.jsdt.core.IType;
import org.eclipse.wst.jsdt.ui.refactoring.RenameSupport;
import org.eclipse.wst.jsdt.web.ui.internal.JsUIMessages;
import org.eclipse.wst.jsdt.web.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.util.PlatformStatusLineUtil;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class JSPRenameElementActionDelegate implements IEditorActionDelegate, IActionDelegate2, IViewActionDelegate {
	private IEditorPart fEditor;
	
	public void dispose() {
		// nulling out just in case
		fEditor = null;
	}
	
	private IJavaScriptElement getSelectedElement() {
		IJavaScriptElement element = null;
		if (fEditor instanceof ITextEditor) {
			IJavaScriptElement[] elements = JSPJavaSelectionProvider.getSelection((ITextEditor) fEditor);
			if (elements.length == 1) {
				element = elements[0];
			}
		}
		return element;
	}
	
	public void init(IAction action) {
		if (action != null) {
			action.setText(JsUIMessages.RenameElement_label);
			action.setToolTipText(JsUIMessages.RenameElement_label);
		}
	}
	
	public void init(IViewPart view) {
	// do nothing
	}
	
	public void run(IAction action) {
		IJavaScriptElement element = getSelectedElement();
		if (element != null) {
			RenameSupport renameSupport = null;
			try {
				switch (element.getElementType()) {
					case IJavaScriptElement.TYPE:
						renameSupport = RenameSupport.create((IType) element, element.getElementName(), RenameSupport.UPDATE_REFERENCES);
					break;
					case IJavaScriptElement.METHOD:
						renameSupport = RenameSupport.create((IFunction) element, element.getElementName(), RenameSupport.UPDATE_REFERENCES);
					break;
					case IJavaScriptElement.PACKAGE_FRAGMENT:
						renameSupport = RenameSupport.create((IPackageFragment) element, element.getElementName(), RenameSupport.UPDATE_REFERENCES);
					break;
				}
				if (renameSupport != null) {
					renameSupport.openDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
					PlatformStatusLineUtil.clearStatusLine();
				}
			} catch (CoreException e) {
				Logger.logException(e);
			}
		} else {
			PlatformStatusLineUtil.displayErrorMessage(JsUIMessages.JSPRenameElementAction_0);
			PlatformStatusLineUtil.addOneTimeClearListener();
		}
	}
	
	public void runWithEvent(IAction action, Event event) {
		run(action);
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		PlatformStatusLineUtil.clearStatusLine();
	}
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		fEditor = targetEditor;
	}
}
