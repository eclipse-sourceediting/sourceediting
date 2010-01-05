/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.java.refactoring;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.ui.internal.util.PlatformStatusLineUtil;

public class RenameElementHandler extends AbstractHandler {
	private IEditorPart fEditor;
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		fEditor = HandlerUtil.getActiveEditor(event);
		
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
		
		return null;
	}
	
	private IJavaElement getSelectedElement() {
		IJavaElement element = null;
		if (fEditor != null) {
			ITextEditor editor = (ITextEditor) ((fEditor instanceof ITextEditor) ? fEditor : fEditor.getAdapter(ITextEditor.class));
			if (editor != null) {
				IJavaElement[] elements = JSPJavaSelectionProvider.getSelection(editor);
				if (elements.length == 1)
					element = elements[0];
			}
		}
		return element;
	}

}
