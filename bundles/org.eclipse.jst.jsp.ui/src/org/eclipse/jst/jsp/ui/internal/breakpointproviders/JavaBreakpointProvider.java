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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.xml.ui.internal.provisional.IDOMSourceEditingTextTools;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A BreakpointProvider supporting server-side Java as a JSP language
 * 
 * @deprecated
 */
public class JavaBreakpointProvider extends AbstractBreakpointProvider {

	/*
	 * @param res @return String
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



	public IStatus addBreakpoint(IDocument document, IEditorInput input, int lineNumber, int offset) throws CoreException {
		int pos = getValidPosition(document, lineNumber);
		if (pos != NO_VALID_CONTENT) {
			IResource res = getEditorInputResource(input);
			if (res != null) {
				String typeName = getTypeName(res);
				try {
					JDIDebugModel.createLineBreakpoint(res, typeName, lineNumber, pos, pos, 0, true, null);
				}
				catch (CoreException e) {
					return e.getStatus();
				}
			}
		}
		return new Status(IStatus.OK, JSPUIPlugin.ID, IStatus.OK, JSPUIMessages.OK, null); //$NON-NLS-1$
	}

	public boolean canAddBreakpoint(IDocument document, IEditorInput input, Node node, int lineNumber, int offset) {
		IResource res = input instanceof IFileEditorInput ? ((IFileEditorInput) input).getFile() : null;
		Document doc = null;
		if (getSourceEditingTextTools() instanceof IDOMSourceEditingTextTools) {
			doc = ((IDOMSourceEditingTextTools) getSourceEditingTextTools()).getDOMDocument();
		}

		return res != null && !isBreakpointExist(res, lineNumber) && isValidPosition(document, lineNumber) && (getPageLanguage(doc) == JAVA);
	}

	public IResource getResource(IEditorInput input) {
		return getEditorInputResource(input);
	}

	/*
	 * @param res @param lineNumber @return boolean
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
	 * @param doc @param idoc @param lineNumber @return boolean
	 */
	private boolean isValidPosition(IDocument idoc, int lineNumber) {
		return getValidPosition(idoc, lineNumber) != NO_VALID_CONTENT;
	}
}
