/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.ui.extensions.breakpoint;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Interface to provide breakpoint creation
 */
public interface IBreakpointProvider {

	/**
	 * Adds breakpoint to specified position
	 * 
	 * @param doc
	 *            w3c DOM Document object or <code>null</code> if called
	 *            from a non-DOM based editor plugin
	 * @param idoc
	 *            IDocument object
	 * @param input
	 *            current editor input, not necessarily an IFileEditorInput or
	 *            linked to a resource in any way
	 * @param node
	 *            current caret node or <code>null</code> if called from a
	 *            non-DOM based editor plugin
	 * @param lineNumber
	 *            current line number
	 * @param offset
	 *            current caret offset
	 * @throws CoreException
	 * @return IStatus the status after being asked to add a breakpoint. The
	 *         Severity of ERROR should only be used if the location
	 *         information is both valid for a breakpoint and one could not be
	 *         added.
	 */
	IStatus addBreakpoint(Document doc, IDocument idoc, IEditorInput input, Node node, int lineNumber, int offset) throws CoreException;

	/**
	 * Set SourceEditingTextTools object
	 * 
	 * @param tool
	 *            SourceEditingTextTools object
	 */
	void setSourceEditingTextTools(SourceEditingTextTools tool);

	/**
	 * Returns corresponding resource from editor input
	 * 
	 * @param input
	 * @return IResource
	 */
	IResource getResource(IEditorInput input);
}
