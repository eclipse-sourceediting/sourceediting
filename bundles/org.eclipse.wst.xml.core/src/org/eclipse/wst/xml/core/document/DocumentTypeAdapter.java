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
package org.eclipse.wst.xml.core.document;



import org.eclipse.wst.sse.core.INodeAdapter;
import org.w3c.dom.DocumentType;

/**
 */
public interface DocumentTypeAdapter extends INodeAdapter {
	static final int STRICT_CASE = 0;
	static final int UPPER_CASE = 1;
	static final int LOWER_CASE = 2;

	/**
	 */
	DocumentType getDocumentType();

	/**
	 */
	int getTagNameCase();

	/**
	 */
	int getAttrNameCase();

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
