/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.breakpointproviders;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.ui.IEditorInput;
import org.w3c.dom.Document;

/**
 * A IBreakpointProvider supporting server-side JavaScript as a JSP language
 * 
 */
public class JavaScriptBreakpointProvider extends AbstractBreakpointProvider {
	public boolean canAddBreakpoint(IDocument document, IEditorInput input, int lineNumber, int offset) {
		IResource res = getEditorInputResource(input);
		Document doc = null;
		return res != null && !isBreakpointExist(res, lineNumber) && isValidPosition(document, lineNumber) && (getPageLanguage(doc) != JAVA);
	}


	public IStatus addBreakpoint(IDocument document, IEditorInput input, int lineNumber, int offset) {
		int pos = getValidPosition(document, lineNumber);
		if (pos != NO_VALID_CONTENT && canAddBreakpoint(document, input, lineNumber, offset)) {
			IResource res = getEditorInputResource(input);
			if (res != null) {
				new JavascriptLineBreakpoint(res, lineNumber, pos, pos);
			}
		}
		return new Status(IStatus.OK, JSPUIPlugin.ID, IStatus.OK, JSPUIMessages.OK, null); //$NON-NLS-1$
	}

	/*
	 * @param res @param lineNumber @return boolean
	 */
	private boolean isBreakpointExist(IResource res, int lineNumber) {
		IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
		IBreakpoint[] breakpoints = manager.getBreakpoints();
		for (int i = 0; i < breakpoints.length; i++) {
			if (!(breakpoints[i] instanceof JavascriptLineBreakpoint))
				continue;
			JavascriptLineBreakpoint breakpoint = (JavascriptLineBreakpoint) breakpoints[i];
			try {
				if (breakpoint.getResource().equals(res) && breakpoint.getLineNumber() == lineNumber) {
					return true;
				}
			}
			catch (CoreException e) {
				return true;
			}
		}
		return false;
	}

	/*
	 * @param doc @param idoc @param lineNumber @return boolean
	 */
	private boolean isValidPosition(IDocument idoc, int lineNumber) {
		return getValidPosition(idoc, lineNumber) != NO_VALID_CONTENT;
	}

	public IResource getResource(IEditorInput input) {
		return getEditorInputResource(input);
	}
}
