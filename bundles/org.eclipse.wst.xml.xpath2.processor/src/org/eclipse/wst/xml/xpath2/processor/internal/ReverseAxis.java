/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal;

import org.eclipse.wst.xml.xpath2.processor.internal.types.ElementType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.NodeType;

/**
 * An axis that only ever contains the context node or nodes that are before the
 * context node in document order is a reverse axis.
 */
public abstract class ReverseAxis implements Axis {

	/**
	 * @return new element type
	 */
	// should always be element i fink
	public NodeType principal_node_kind() {
		return new ElementType();
	}
}
