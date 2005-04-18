/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.provisional.extensions.breakpoint;



import org.eclipse.core.resources.IMarker;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Interface to provide convenient functions for a plugin which doesn't want
 * to depends on sed.editor or sed.model plugins, but needs some information
 * which the sed functions provide as a convenience to clients
 */
public interface SourceEditingTextTools {


	/**
	 * Returns w3c DOM document for a given marker
	 * 
	 * @param marker
	 *            marker object to check
	 * @return Document w3c DOM document object or <code>null</code> if
	 *         corresponding document does not exist
	 */
	Document getDOMDocument(IMarker marker);

	/**
	 * Returns a NodeLocation object describing the position information of
	 * the Node's start and end tags. Returns null for unsupported Node types
	 * (Nodes which are not Elements or in a supported Document).
	 * 
	 * @param node
	 * @return
	 */
	NodeLocation getNodeLocation(Node node);

	/**
	 * Returns the current server-side page language for the Document of the
	 * given Node.
	 * 
	 * @return
	 */
	String getPageLanguage(Node node);

	/**
	 * Returns start offset of given Node
	 * 
	 * @param node
	 *            w3c <code>Node</code> object to check
	 * @return int start offset or -1 for error
	 */
	int getStartOffset(Node node);

}
