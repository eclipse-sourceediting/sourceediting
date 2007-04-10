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
package org.eclipse.wst.html.core.internal.contentmodel.chtml;


/**
 * In HTML Documents, name and value of an attribute/element/entity
 * should be treated ignoring theirs case.  However, in XML documents,
 * they should be distinguished with sensitiveness of their case.
 * CMNode is basically designed to represent DTDs or Schemas for XML
 * documents.  So, it doesn't have interfaces to retrieve such information.
 * However, declarations in the HTML CM should provide such information.
 * This intermediate interface is intended to provide whether ignore cases
 * or not.<br>
 */
interface HTMLCMNode extends org.eclipse.wst.xml.core.internal.contentmodel.CMNode {

	/**
	 * Returns <code>true</code>, if declaration is for HTML attribute/element/entity.
	 * Otherwise, returns <code>false</code>.
	 * @return boolean
	 */
	boolean shouldIgnoreCase();
}
