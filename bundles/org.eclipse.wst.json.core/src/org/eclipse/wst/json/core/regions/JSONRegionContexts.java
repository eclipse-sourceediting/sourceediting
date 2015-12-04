/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core.regions;

/**
 * JSON region contexts.
 *
 */
public interface JSONRegionContexts {

	// JSON Object
	public static final String JSON_OBJECT_OPEN = "JSON_OBJECT_OPEN"; //$NON-NLS-1$
	public static final String JSON_OBJECT_CLOSE = "JSON_OBJECT_CLOSE"; //$NON-NLS-1$
	public static final String JSON_OBJECT_KEY = "JSON_OBJECT_KEY"; //$NON-NLS-1$
	public static final String JSON_COLON = "JSON_COLON"; //$NON-NLS-1$
	public static final String JSON_COMMA = "JSON_COMMA"; //$NON-NLS-1$

	// JSON Array
	public static final String JSON_ARRAY_OPEN = "JSON_ARRAY_OPEN"; //$NON-NLS-1$
	public static final String JSON_ARRAY_CLOSE = "JSON_ARRAY_CLOSE"; //$NON-NLS-1$

	// JSON Value
	public static final String JSON_VALUE_STRING = "JSON_VALUE_STRING"; //$NON-NLS-1$
	public static final String JSON_VALUE_BOOLEAN = "JSON_VALUE_BOOLEAN"; //$NON-NLS-1$
	public static final String JSON_VALUE_NUMBER = "JSON_VALUE_NUMBER"; //$NON-NLS-1$
	public static final String JSON_VALUE_NULL = "JSON_VALUE_NULL"; //$NON-NLS-1$

	// Other
	public static final String JSON_COMMENT = "JSON_COMMENT"; //$NON-NLS-1$
	public static final String JSON_UNKNOWN = "JSON_UNKNOWN"; //$NON-NLS-1$
	public static final String WHITE_SPACE = "WHITE_SPACE"; //$NON-NLS-1$
	public static final String UNDEFINED = "UNDEFINED"; //$NON-NLS-1$
}
