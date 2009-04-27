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

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

/**
 * The following-sibling axis contains the context node's following siblings,
 * those children of the context node's parent that occur after the context node
 * in document order; if the context node is an attribute nodeor namespace node,
 * the following-sibling axis is empty.
 */
public class FollowingSiblingAxis extends ForwardAxis {

	/**
	 * Return the result of FollowingSiblingAxis expression
	 * 
	 * @param node
	 *            is the type of node.
	 * @param dc
	 *            is the dynamic context.
	 * @return The result of FollowingSiblingAxis.
	 */
	public ResultSequence iterate(NodeType node, DynamicContext dc) {
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

		// get the following siblings
		for (Iterator i = rs.iterator(); i.hasNext();) {
			NodeType n = (NodeType) i.next();

			// if we haven't found the node yet, remove elements
			// [preciding siblings]
			// note: if node is first... its ok... cuz we don't
			// include node...
			i.remove();

			// check reference of DOM object... should be correct ?!
			// dunno... XXX
			if (n.node_value() == node.node_value())
				break;
		}

		return rs;
	}

}
