/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor;

import org.eclipse.wst.xml.xpath2.processor.types.*;

/**
 * Returns the ancestors of the context node, this always includes the root
 * node.
 */
public class AncestorAxis extends ParentAxis {

	/**
	 * Get the ancestors of the context node.
	 * 
	 * @param node
	 *            is the type of node.
	 * @param dc
	 *            is the dynamic context.
	 * @return The nodes that are ancestors of the context node.
	 */
	// XXX unify this with descendants axis ?
	@Override
	public ResultSequence iterate(NodeType node, DynamicContext dc) {

		// get the parent
		ResultSequence rs = super.iterate(node, dc);

		// no parent
		if (rs.size() == 0)
			return rs;

		NodeType parent = (NodeType) rs.get(0);

		// get ancestors of parent
		ResultSequence ances = iterate(parent, dc);

		ances.concat(rs);

		return ances;
	}

}
