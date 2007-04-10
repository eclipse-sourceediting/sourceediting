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
package org.eclipse.wst.xml.core.internal.provisional.document;



import org.w3c.dom.DocumentType;

/**
 * This interface enables setting of Public and System ID for DOCTYPE
 * declaration.
 * 
 * @plannedfor 1.0
 */
public interface IDOMDocumentType extends IDOMNode, DocumentType {


	/**
	 * Sets document type's public id, as source.
	 * 
	 * @param String -
	 *            the publicId
	 */
	void setPublicId(String publicId);

	/**
	 * Sets docment type's system id, as source.
	 * 
	 * @param String -
	 *            the systemId
	 */
	void setSystemId(String systemId);
}
