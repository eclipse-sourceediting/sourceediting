/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contentmodel;

/**
 * CMGroup interface
 */
public interface CMGroup extends CMContent {
  
	static final int ALL = 3;
	static final int CHOICE = 2;
	static final int SEQUENCE = 1;
/**
 * getChildNodes method
 * @return CMNodeList
 *
 * Returns child CMNodeList, which includes ElementDefinition or CMElement.
 */
CMNodeList getChildNodes();

/**
 * getOperation method
 * @return int
 *
 * Returns one of :
 * ALONE (a), SEQUENCE (a,b), CHOICE (a|b), ALL (a&b).
 */
int getOperator();
}
