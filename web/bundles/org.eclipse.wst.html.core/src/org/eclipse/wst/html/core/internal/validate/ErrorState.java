/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.validate;



interface ErrorState {
	static final int NONE_ERROR = 0;
	// generic error
	static final int UNDEFINED_NAME_ERROR = 11;
	static final int UNDEFINED_VALUE_ERROR = 12;
	static final int MISMATCHED_VALUE_ERROR = 13;
	static final int OBSOLETE_ATTR_NAME_ERROR = 14;
	static final int OBSOLETE_TAG_NAME_ERROR = 15;
	static final int MISSING_ATTR_VALUE_EQUALS_ERROR = 16;
	
	// format error
	static final int FORMAT_ERROR_LEVEL = 100;
	static final int INVALID_NAME_ERROR = 101;
	static final int INVALID_CHAR_ERROR = 102;
	static final int MISMATCHED_ERROR = 103;
	static final int MISMATCHED_END_TAG_ERROR = 104;
	static final int MISSING_START_TAG_ERROR = 105;
	static final int MISSING_END_TAG_ERROR = 106;
	static final int UNNECESSARY_END_TAG_ERROR = 107;
	static final int INVALID_ATTR_ERROR = 108;
	static final int INVALID_DIRECTIVE_ERROR = 109;
	static final int UNCLOSED_TAG_ERROR = 110;
	static final int UNCLOSED_END_TAG_ERROR = 111;
	static final int INVALID_EMPTY_ELEMENT_TAG = 112;
	static final int UNCLOSED_ATTR_VALUE = 113; //D210422
	static final int RESOURCE_NOT_FOUND = 114; //D210422
	static final int INVALID_TEXT_IN_ELEM_ERROR = 115;

	// layout error
	static final int LAYOUT_ERROR_LEVEL = 1000;
	static final int INVALID_CONTENT_ERROR = 1001;
	static final int DUPLICATE_ERROR = 1002;
	static final int COEXISTENCE_ERROR = 1003;
}
