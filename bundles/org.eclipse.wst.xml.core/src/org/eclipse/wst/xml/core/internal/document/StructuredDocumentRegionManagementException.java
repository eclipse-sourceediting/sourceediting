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
package org.eclipse.wst.xml.core.internal.document;

import org.eclipse.wst.sse.core.internal.provisional.exceptions.SourceEditingRuntimeException;



/**
 */
public class StructuredDocumentRegionManagementException extends SourceEditingRuntimeException {

	/**
	 * Default <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * StructuredDocumentRegionManagementException constructor
	 */
	public StructuredDocumentRegionManagementException() {
		super("IStructuredDocumentRegion management failed.");//$NON-NLS-1$
	}
}
