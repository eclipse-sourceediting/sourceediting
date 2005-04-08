/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.java.refactoring;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.wst.sse.ui.util.PlatformStatusLineUtil;

/**
 * A TextEditorAction that launches JDT rename element wizard
 * 
 * @author pavery
 */
public class JSPRenameElementAction extends TextEditorAction {

	public JSPRenameElementAction(ResourceBundle bundle, String prefix, ITextEditor editor) {

		super(bundle, prefix, editor);
	}
	
	public boolean isEnabled() {
		// always enabled, just print appropriate status to window
		// if for some reason the action can't run (like multiple java elements selected)
		return true;
	}
	
	/**
	 * @see org.eclipse.ui.texteditor.TextEditorAction#update()
	 */
	public void update() {
		super.update();
		PlatformStatusLineUtil.clearStatusLine();
	}
	
	private IJavaElement getSelectedElement() {
		IJavaElement element = null;
		if (getTextEditor() != null) {
			IJavaElement[] elements = JSPJavaSelectionProvider.getSelection(getTextEditor());
			if (elements.length == 1)
				element = elements[0];
		}
		return element;
	}
	
	public void run() {
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
}