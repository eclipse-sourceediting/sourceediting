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

import org.eclipse.core.resources.IMarker;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class NullSourceEditingTextTools implements SourceEditingTextTools {
	private static NullSourceEditingTextTools instance;
	public static final String ID = "sourceeditingtexttools"; //$NON-NLS-1$

	private NullSourceEditingTextTools() {
		super();
	}

	/* (non-Javadoc)
	 */
	public Document getDOMDocument(IMarker marker) {
		return null;
	}

	/**
	 * @return
	 */
	public synchronized static SourceEditingTextTools getInstance() {
		if (instance == null)
			instance = new NullSourceEditingTextTools();
		return instance;
	}

	/* (non-Javadoc)
	 */
	public NodeLocation getNodeLocation(Node node) {
		return null;
	}

	/* (non-Javadoc)
	 */
	public String getPageLanguage(Node node) {
		return null;
	}

	/* (non-Javadoc)
	 */
	public int getStartOffset(Node node) {
		return 0;
	}

}
