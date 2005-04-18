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
package org.eclipse.jst.jsp.ui.internal.breakpointproviders;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.breakpoint.SourceEditingTextTools;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A BreakpointProvider supporting server-side Java as a JSP language
 */
public class JavaBreakpointProvider extends AbstractBreakpointProvider {

	/*
	 * @param res
	 * @return String
	 */
	private static final String getTypeName(IResource res) {
		IPath path = res.getFullPath();
		// Assume under Web Content folder if more than 2 segments
		if (path.segmentCount() > 2) {
			path = path.removeFirstSegments(2);
		}
		else {
			path = path.removeFirstSegments(1);
		}
		String typeName = path.toString().replace(IPath.SEPARATOR, '.');
		if (res.getFileExtension() != null) {
			typeName = typeName.substring(0, typeName.lastIndexOf('.'));
		}
		return typeName;
	}


	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.extensions.BreakpointProvider#canAddBreakpoint(org.w3c.dom.Document, org.eclipse.jface.text.IDocument, org.eclipse.ui.IEditorInput, org.w3c.dom.Node, int, int)
	 */
	public boolean canAddBreakpoint(Document doc, IDocument idoc, IEditorInput input, Node node, int lineNumber, int offset) {
		IResource res = input instanceof IFileEditorInput ? ((IFileEditorInput) input).getFile() : null;

		return res != null && !isBreakpointExist(res, lineNumber) && isValidPosition(doc, idoc, lineNumber) && (getPageLanguage(doc) == JAVA);
	}

	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.extensions.breakpoint.IBreakpointProvider#addBreakpoint(org.w3c.dom.Document, org.eclipse.jface.text.IDocument, org.eclipse.ui.IEditorInput, org.w3c.dom.Node, int, int)
	 */
	public IStatus addBreakpoint(Document doc, IDocument idoc, IEditorInput input, Node node, int lineNumber, int offset) throws CoreException {
		int pos = getValidPosition(doc, idoc, lineNumber);
		if (pos != NO_VALID_CONTENT) {
			IResource res = getEditorInputResource(input);
			if (res != null) {
				String typeName = getTypeName(res);
				try {
					JDIDebugModel.createLineBreakpoint(res, typeName, lineNumber, pos, pos, 0, true, null);
				}
				catch(CoreException e) {
					return e.getStatus();
				}
			}
		}
		return new Status(IStatus.OK, JSPUIPlugin.ID, IStatus.OK, JSPUIMessages.OK, null); //$NON-NLS-1$
	}

	/*
	 * @param res
	 * @param lineNumber
	 * @return boolean
	 */
	private boolean isBreakpointExist(IResource res, int lineNumber) {
		try {
			return JDIDebugModel.lineBreakpointExists(getTypeName(res), lineNumber) != null;
		}
		catch (CoreException e) {
			return false;
		}
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
	 * @see com.ibm.sse.editor.extensions.BreakpointProvider#setSourceEditingTextTools(com.ibm.sse.editor.extensions.SourceEditingTextTools)
	 */
	public void setSourceEditingTextTools(SourceEditingTextTools util) {
	}

	/* (non-Javadoc)
	 * @see com.ibm.sse.editor.extensions.BreakpointProvider#getResource(org.eclipse.ui.IStorageEditorInput)
	 */
	public IResource getResource(IEditorInput input) {
		return getEditorInputResource(input);
	}
}
