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

package org.w3c.dom.ranges;

/**
 * Range operations may throw a <code>RangeException</code> as specified in
 * their method descriptions.
 * <p>
 * See also the <a
 * href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Traversal-Range-20001113'>Document
 * Object Model (DOM) Level 2 Traversal and Range Specification </a>.
 * 
 * @since DOM Level 2
 */
public class RangeException extends RuntimeException {
	// RangeExceptionCode
	/**
	 * If the boundary-points of a Range do not meet specific requirements.
	 */
	public static final short BAD_BOUNDARYPOINTS_ERR = 1;
	/**
	 * If the container of an boundary-point of a Range is being set to either
	 * a node of an invalid type or a node with an ancestor of an invalid
	 * type.
	 */
	public static final short INVALID_NODE_TYPE_ERR = 2;

	public short code;

	public RangeException(short code, String message) {
		super(message);
		this.code = code;
	}

}
