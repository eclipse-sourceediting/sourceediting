/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.document;

import org.eclipse.osgi.util.NLS;

class DOMMessages extends NLS {
	private static String BUNDLE_NAME = "org.eclipse.wst.xml.core.internal.document.DOMMessages"; //$NON-NLS-1$

	static {
		NLS.initializeMessages(BUNDLE_NAME, DOMMessages.class);
	}

	private DOMMessages() {
	}

	static String DOMSTRING_SIZE_ERR;
	static String HIERARCHY_REQUEST_ERR;
	static String INDEX_SIZE_ERR;
	static String INUSE_ATTRIBUTE_ERR;
	static String INVALID_ACCESS_ERR;
	static String INVALID_CHARACTER_ERR;
	static String INVALID_MODIFICATION_ERR;
	static String INVALID_STATE_ERR;
	static String NAMESPACE_ERR;
	static String NO_DATA_ALLOWED_ERR;
	static String NO_MODIFICATION_ALLOWED_ERR;
	static String NOT_FOUND_ERR;
	static String NOT_SUPPORTED_ERR;
	static String SYNTAX_ERR;
	static String TYPE_MISMATCH_ERR;
	static String VALIDATION_ERR;
	static String WRONG_DOCUMENT_ERR;
}
