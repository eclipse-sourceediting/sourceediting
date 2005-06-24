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
 * <p>
 * See also the <a
 * href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Traversal-Range-20001113'>Document
 * Object Model (DOM) Level 2 Traversal and Range Specification </a>.
 * 
 * @see DOM Level 2
 */
public interface DocumentRange {
	/**
	 * This interface can be obtained from the object implementing the
	 * <code>Document</code> interface using binding-specific casting
	 * methods.
	 * 
	 * @return The initial state of the Range returned from this method is
	 *         such that both of its boundary-points are positioned at the
	 *         beginning of the corresponding Document, before any content.
	 *         The Range returned can only be used to select content
	 *         associated with this Document, or with DocumentFragments and
	 *         Attrs for which this Document is the <code>ownerDocument</code>.
	 */
	public Range createRange();

}
