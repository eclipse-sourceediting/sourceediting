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

import org.w3c.dom.Node;

/**
 * This is a compiled pattern (which is a simpler version of an expression, used to match in XSLT files, etc.)
 *
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface XPath2Pattern {
	/**
	 * @return The object passed in my the caller when the patten was created.
	 */
	Object getUserData();
	
	boolean matches(DynamicContext dc, Node context);

}
