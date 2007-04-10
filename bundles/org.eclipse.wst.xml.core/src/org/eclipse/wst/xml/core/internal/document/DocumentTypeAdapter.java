/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.document;



import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.w3c.dom.DocumentType;


/**
 */
public interface DocumentTypeAdapter extends INodeAdapter {
	static final int LOWER_CASE = 2;
	static final int STRICT_CASE = 0;
	static final int UPPER_CASE = 1;

	/**
	 */
	int getAttrNameCase();

	/**
	 */
	DocumentType getDocumentType();

	/**
	 */
	int getTagNameCase();

	/**
	 */
	boolean hasFeature(String feature);

	/**
	 */
	boolean isXMLType();

	/**
	 */
	void release();
}
