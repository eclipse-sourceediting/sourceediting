/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.css.core.internal.formatter.CSSSourceGenerator
 *                                           modified in order to process JSON Objects.     
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.format;

import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.json.core.document.IJSONNode;

public interface IJSONSourceGenerator extends IJSONSourceFormatter {

	/**
	 * 
	 */
	//StringBuilder formatAttrChanged(IJSONNode node, IJSONAttr attr,
	//		boolean insert, AttrChangeContext region);

	/**
	 * 
	 */
	StringBuilder formatBefore(IJSONNode node, IJSONNode child, IRegion exceptFor);

	/**
	 * 
	 */
	int getAttrInsertPos(IJSONNode node, String attrName);

	/**
	 * 
	 */
	int getChildInsertPos(IJSONNode node);

	/**
	 * 
	 * @return int
	 * @param node
	 *            org.eclipse.wst.css.core.model.interfaces.IJSONNode
	 * @param insertPos
	 *            int
	 */
	int getLengthToReformatAfter(IJSONNode node, int insertPos);

	/**
	 * 
	 * @return int
	 * @param node
	 *            org.eclipse.wst.css.core.model.interfaces.IJSONNode
	 * @param insertPos
	 *            int
	 */
	int getLengthToReformatBefore(IJSONNode node, int insertPos);
}
