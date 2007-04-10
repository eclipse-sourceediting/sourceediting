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
package org.eclipse.jst.jsp.core.internal.contentmodel.tld;



import org.eclipse.jst.jsp.core.internal.contentmodel.CMDocumentWrapperImpl;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.provisional.contentmodel.CMDocumentTracker;

/**
 * TaglibTracker class
 */
public class TaglibTracker extends CMDocumentWrapperImpl implements CMDocumentTracker {

	private IStructuredDocumentRegion fStructuredDocumentRegion;

	public TaglibTracker(String newURI, String newPrefix, CMDocument tld, IStructuredDocumentRegion aStructuredDocumentRegion) {
		super(newURI, newPrefix, tld);
		fStructuredDocumentRegion = aStructuredDocumentRegion;
	}

	public IStructuredDocumentRegion getStructuredDocumentRegion() {
		return fStructuredDocumentRegion;
	}

	public String toString() {
		if (getStructuredDocumentRegion() != null)
			return getPrefix() + "@" + getStructuredDocumentRegion().getStartOffset(); //$NON-NLS-1$
		return super.toString();
	}
}
