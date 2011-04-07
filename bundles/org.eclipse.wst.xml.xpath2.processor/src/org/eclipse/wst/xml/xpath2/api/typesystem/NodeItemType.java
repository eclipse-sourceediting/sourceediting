/*******************************************************************************
 * Copyright (c) 2011 Jesper Moller, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jesper Moller - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.api.typesystem;

import javax.xml.namespace.QName;

/**
 * @since 2.0
 */
public interface NodeItemType extends ItemType {
	/**
	 * For attribute and element types, return whether the name
	 * part of the type test is a wildcard.
	 * 
	 * @return Wildcard test?
	 */
	boolean isWildcard();
	
	/**
	 * @return name of the item type, if applicable, otherwise null
	 */
	QName getName();

	/**
	 * Node type as per list in org.w3c.dom.Node
	 * 
	 * @return The DOM node type
	 */
	short getNodeType();
}