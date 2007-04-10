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
package org.eclipse.wst.css.core.internal.provisional.document;



/**
 * 
 */
public interface ICSSSelectorCombinator extends ICSSSelectorItem {

	public static final char DESCENDANT = ' ';
	public static final char CHILD = '>';
	public static final char ADJACENT = '+';
	public static final char UNKNOWN = '?'; // error case

	/**
	 * @return int
	 */
	char getCombinatorType();
}
