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
package org.eclipse.jst.jsp.core.contentmodel.tld;



import java.util.List;

import org.eclipse.wst.common.contentmodel.CMElementDeclaration;
import org.eclipse.wst.sse.core.text.IStructuredDocument;

public interface TaglibSupport {

	void clearCMDocumentCache();

	/**
	 * Return the list of CMDocuments active after this offset.
	 * Elements in the list are only required to implement 
	 * CMDocument.	 
	 */
	List getCMDocuments(int offset);

	/**
	 * Return the list of CMDocuments active after this offset
	 * and possessing the given prefix.  Elements in the list
	 * are only required to implement CMDocument.
	 */
	List getCMDocuments(String prefix, int offset);

	/**
	 * Return the list of CMDocumentTrackers active after this offset.
	 * Difference from the CMDocument method in that the objects
	 * are trackers; exists as a formality.
	 */
	List getCMDocumentTrackers(int offset);

	/**
	 * Return the list of CMDocumentsTrackers active after this offset
	 * and possessing the given prefix.  Different in that the List is
	 * expected to contain tracker objects; exists as a formality.
	 */
	List getCMDocumentTrackers(String prefix, int offset);

	/**
	 * Return the first declaration found for the qualified tag
	 * name using CMDocuments enabled for the given offset.
	 */
	CMElementDeclaration getDeclaration(String tagName, int offset);

	IStructuredDocument getStructuredDocument();

	void setStructuredDocument(IStructuredDocument model);
}