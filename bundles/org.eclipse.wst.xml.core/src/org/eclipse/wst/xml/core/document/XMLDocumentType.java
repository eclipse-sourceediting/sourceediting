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



import org.w3c.dom.DocumentType;

/**
 * This interface enables setting of Public and System ID for DOCTYPE declaration.
 */
public interface XMLDocumentType extends XMLNode, DocumentType {

	/**
	 */
	void setPublicId(String publicId);

	/**
	 */
	void setSystemId(String systemId);
}
