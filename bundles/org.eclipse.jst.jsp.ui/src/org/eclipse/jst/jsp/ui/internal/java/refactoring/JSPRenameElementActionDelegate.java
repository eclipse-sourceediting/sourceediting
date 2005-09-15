/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.jst.jsp.ui.internal.java.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.ui.internal.util.PlatformStatusLineUtil;

/**
 * An action delegate that launches JDT rename element wizard
 */
public class JSPRenameElementActionDelegate implements IEditorActionDelegate, IActionDelegate2, IViewActionDelegate {
	private IEditorPart fEditor;
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		fEditor = targetEditor;
	}

	public void dispose() {
		// nulling out just in case
		fEditor = null;
	}

	public void init(IAction action) {
		if (action != null) {
			action.setText(JSPUIMessages.RenameElement_label);
			action.setToolTipText(JSPUIMessages.RenameElement_label);
		}
	}

	public void runWithEvent(IAction action, Event event) {
		run(action);
	}
	
	public void run(IAction action) {
		IJavaElement element = getSelectedElement();
		if(element != null) {
			RenameSupport renameSupport = null;
			try {
				switch(element.getElementType()) {
					case IJavaElement.TYPE:
						renameSupport= RenameSupport.create((IType)element, element.getElementName(), RenameSupport.UPDATE_REFERENCES);
						break;
					case IJavaElement.METHOD:
						renameSupport= RenameSupport.create((IMethod)element, element.getElementName(), RenameSupport.UPDATE_REFERENCES);
						break;
					case IJavaElement.PACKAGE_FRAGMENT:
						renameSupport= RenameSupport.create((IPackageFragment)element, element.getElementName(), RenameSupport.UPDATE_REFERENCES);
						break;
				}
				if(renameSupport != null) {
					renameSupport.openDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
					PlatformStatusLineUtil.clearStatusLine();
				}
			}
			catch (CoreException e) {
				Logger.logException(e);
			}
		}
		else  {
			PlatformStatusLineUtil.displayErrorMessage(JSPUIMessages.JSPRenameElementAction_0); //$NON-NLS-1$
			PlatformStatusLineUtil.addOneTimeClearListener();
		}
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		PlatformStatusLineUtil.clearStatusLine();
	}

	public void init(IViewPart view) {
		// do nothing
	}

	private IJavaElement getSelectedElement() {
		IJavaElement element = null;
		if (fEditor instanceof ITextEditor) {
			IJavaElement[] elements = JSPJavaSelectionProvider.getSelection((ITextEditor)fEditor);
			if (elements.length == 1)
				element = elements[0];
		}
		return element;
	}
}
