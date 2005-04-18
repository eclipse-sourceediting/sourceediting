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

public class NullSourceEditingTextTools implements SourceEditingTextTools {
	public static final String ID = "sourceeditingtexttools"; //$NON-NLS-1$
	private static NullSourceEditingTextTools instance;

	/**
	 * @return
	 */
	public synchronized static SourceEditingTextTools getInstance() {
		if (instance == null)
			instance = new NullSourceEditingTextTools();
		return instance;
	}

	private NullSourceEditingTextTools() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.extensions.breakpoint.SourceEditingTextTools#getDOMDocument(org.eclipse.core.resources.IMarker)
	 */
	public Document getDOMDocument(IMarker marker) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.extensions.breakpoint.SourceEditingTextTools#getNodeLocation(org.w3c.dom.Node)
	 */
	public NodeLocation getNodeLocation(Node node) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.extensions.breakpoint.SourceEditingTextTools#getPageLanguage(org.w3c.dom.Node)
	 */
	public String getPageLanguage(Node node) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.extensions.breakpoint.SourceEditingTextTools#getStartOffset(org.w3c.dom.Node)
	 */
	public int getStartOffset(Node node) {
		return 0;
	}

}
