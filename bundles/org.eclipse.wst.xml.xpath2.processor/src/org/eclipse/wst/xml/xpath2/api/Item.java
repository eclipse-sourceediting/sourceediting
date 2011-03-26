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

package org.eclipse.wst.xml.xpath2.api;

import org.eclipse.wst.xml.xpath2.api.typesystem.ItemType;

/**
 * A match found by the XPath2 pattern matcher
 * 
 * * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 *
 */

public interface Item {
	/**
	 * @return The number of matching patterns on the input.
	 */
	ItemType getItemType();
	
	Object getNativeValue();
}
