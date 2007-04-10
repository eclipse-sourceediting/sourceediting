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
package org.eclipse.wst.sse.core.internal.encoding;


/**
 * Class to provided "enumerated types" for encoding rule parameter. This is
 * to be used by client to have some control over how encoding is determined.
 */
public class EncodingRule {
	/**
	 * CONTENT_BASED means the class which uses the parameter (such as
	 * contentType Loaders) should use what ever rules it normally would.
	 * (Note, some content type loaders may not always literally use the file
	 * content to determine encoding, but the point is they should use what
	 * ever rules they normally would.)
	 */
	public static final EncodingRule CONTENT_BASED = new EncodingRule("CONTENT_BASED"); //$NON-NLS-1$
	/**
	 * FORCE_DEFAULT means the class which uses the parameter (such as
	 * contentType Loaders) should use what ever it defines as the default
	 * encoding.
	 */
	public static final EncodingRule FORCE_DEFAULT = new EncodingRule("FORCE_DEFAULT"); //$NON-NLS-1$

	/**
	 * IGNORE_CONVERSION_ERROR means that the save operation should save even
	 * if it encounters conversion errors. This will result in some data loss,
	 * so should only be used after the user confirms that is indeed what they
	 * want to do.
	 */
	public static final EncodingRule IGNORE_CONVERSION_ERROR = new EncodingRule("IGNORE_CONVERSION_ERROR"); //$NON-NLS-1$


	private final String encodingRule;

	/**
	 * Constructor for EncodingRule is private, so no one can instantiate
	 * except this class itself.
	 */
	private EncodingRule(String ruleName) {
		super();
		encodingRule = ruleName;
	}

	public String toString() {
		return encodingRule;
	}
}
