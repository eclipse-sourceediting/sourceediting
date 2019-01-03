/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.provisional.document;



/**
 * 
 */
public interface ICSSSimpleSelector extends ICSSSelectorItem {

	/**
	 * @return java.lang.String
	 * @param index
	 *            int
	 */
	String getAttribute(int index);

	/**
	 * @return java.lang.String
	 * @param index
	 *            int
	 */
	String getClass(int index);

	/**
	 * @return java.lang.String
	 * @param index
	 *            int
	 */
	String getID(int index);

	/**
	 * @return java.lang.String
	 */
	String getName();

	/**
	 * @return boolean
	 */
	int getNumOfAttributes();

	/**
	 * @return boolean
	 */
	int getNumOfClasses();

	/**
	 * @return boolean
	 */
	int getNumOfIDs();

	/**
	 * @return boolean
	 */
	int getNumOfPseudoNames();

	/**
	 * @return java.lang.String
	 * @param index
	 *            int
	 */
	String getPseudoName(int index);

	/**
	 * @return boolean
	 */
	boolean isUniversal();
}
