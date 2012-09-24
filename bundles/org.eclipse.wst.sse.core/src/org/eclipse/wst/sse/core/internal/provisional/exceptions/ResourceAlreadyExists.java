/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.provisional.exceptions;



/**
 * Indicates that a Resource which a model or document was expected to create
 * already exists.
 */
public class ResourceAlreadyExists extends Exception {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * ResourceAlreadyExists constructor comment.
	 */
	public ResourceAlreadyExists() {
		super();
	}

	/**
	 * ResourceAlreadyExists constructor comment.
	 * 
	 * @param s
	 *            java.lang.String
	 */
	public ResourceAlreadyExists(String s) {
		super(s);
	}
}
