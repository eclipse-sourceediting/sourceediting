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

package org.eclipse.wst.xml.xpath2.processor;

import org.eclipse.wst.xml.xpath2.processor.types.*;

import java.util.*;

/**
 * the preceding axis contains all nodes that are descendants of the root of the
 * tree in which the context node is found including the context node itself
 */
public class PrecedingSiblingAxis extends ReverseAxis {
	// XXX again, unify with following

	/**
	 * returns preceding nodes of the context node
	 * 
	 * @param node
	 *            is the node type.
	 * @throws dc
	 *             is the Dynamic context.
	 * @return the descendants and the context node
	 */
	public ResultSequence iterate(NodeType node, DynamicContext dc) {
		boolean found = false;
		// XXX check for attribute / namespace node... if so return
		// empty sequence

		// get the parent
		ParentAxis pa = new ParentAxis();
		ResultSequence rs = pa.iterate(node, dc);

		// XXX: if no parent, out of luck i guess
		if (rs.size() == 0)
			return rs;

		// get the children of the parent [siblings]
		ChildAxis ca = new ChildAxis();
		NodeType parent = (NodeType) rs.get(0);
		rs = ca.iterate(parent, dc);

		// get the preceding siblings
		for (Iterator i = rs.iterator(); i.hasNext();) {
			NodeType n = (NodeType) i.next();

			// ok we passed the node, so just erase the rest of the
			// results
			if (found) {
				i.remove();
				continue;
			}

			if (n.node_value() == node.node_value()) {
				i.remove();
				found = true;
			}
		}

		return rs;
	}

}
