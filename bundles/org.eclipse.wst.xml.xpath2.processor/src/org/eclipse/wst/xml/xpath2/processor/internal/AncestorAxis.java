/*******************************************************************************
 * Copyright (c) 2005, 2010 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal;

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

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
