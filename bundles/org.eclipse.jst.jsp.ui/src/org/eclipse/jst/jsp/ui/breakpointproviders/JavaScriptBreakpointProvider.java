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
package org.eclipse.jst.jsp.ui.breakpointproviders;



import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.nls.ResourceHandler;
import org.eclipse.ui.IEditorInput;
import org.eclipse.wst.sse.ui.extensions.breakpoint.SourceEditingTextTools;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A IBreakpointProvider supporting server-side JavaScript as a JSP language
 *
 */
public class JavaScriptBreakpointProvider extends AbstractBreakpointProvider {
	public boolean canAddBreakpoint(Document doc, IDocument idoc, IEditorInput input, Node node, int lineNumber, int offset) {
		IResource res = getEditorInputResource(input);
		return res != null && !isBreakpointExist(res, lineNumber) && isValidPosition(doc, idoc, lineNumber) && (getPageLanguage(doc) != JAVA);
	}

	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.extensions.breakpoint.IBreakpointProvider#addBreakpoint(org.w3c.dom.Document, org.eclipse.jface.text.IDocument, org.eclipse.ui.IEditorInput, org.w3c.dom.Node, int, int)
	 */
	public IStatus addBreakpoint(Document doc, IDocument idoc, IEditorInput input, Node node, int lineNumber, int offset) {
		int pos = getValidPosition(doc, idoc, lineNumber);
		if (pos != NO_VALID_CONTENT && canAddBreakpoint(doc, idoc, input, node, lineNumber, offset)) {
			IResource res = getEditorInputResource(input);
			if (res != null) {
//				try {
				new JavascriptLineBreakpoint(res, lineNumber, pos, pos);
//				}
//				catch(CoreException e) {
//					return new Status(IStatus.ERROR, JSPUIPlugin.ID, IStatus.ERROR, "Problem adding Java breakpoint", e);
//				}
			}
		}
		return new Status(IStatus.OK, JSPUIPlugin.ID, IStatus.OK, ResourceHandler.getString("OK"), null); //$NON-NLS-1$
	}

	/*
	 * @param res
	 * @param lineNumber
	 * @return boolean
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
	 * @param doc
	 * @param idoc
	 * @param lineNumber
	 * @return boolean
	 */
	private boolean isValidPosition(Document doc, IDocument idoc, int lineNumber) {
		return getValidPosition(doc, idoc, lineNumber) != NO_VALID_CONTENT;
	}

	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.extensions.IBreakpointProvider#setSourceEditingTextTools(com.ibm.sse.editor.extensions.SourceEditingTextTools)
	 */
	public void setSourceEditingTextTools(SourceEditingTextTools util) {
	}

	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.extensions.IBreakpointProvider#getResource(org.eclipse.ui.IStorageEditorInput)
	 */
	public IResource getResource(IEditorInput input) {
		return getEditorInputResource(input);
	}
}