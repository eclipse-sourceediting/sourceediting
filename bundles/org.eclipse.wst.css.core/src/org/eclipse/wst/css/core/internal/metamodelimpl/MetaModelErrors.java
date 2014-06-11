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
package org.eclipse.wst.css.core.internal.metamodelimpl;

interface MetaModelErrors {

	static final short NO_ERROR = 0;

	// MASK
	static final short MASK_WARNING = 0x1000;
	static final short MASK_ERROR = 0x2000;

	// ERROR
	static final short ERROR_NOT_DEFINED = MASK_ERROR + 0x01;
	static final short ERROR_INVALID_NAME = MASK_ERROR + 0x04;
	static final short ERROR_INVAILD_CHILD = MASK_ERROR + 0x02;
	static final short ERROR_NO_CHILD = MASK_ERROR + 0x08;
	// static final short ERROR_NO_ESSENTIAL_CHILD = MASK_ERROR + 0x10

	// WARNING
	static final short WARNING_HAS_NO_CHILD = MASK_WARNING + 0x01;
}
