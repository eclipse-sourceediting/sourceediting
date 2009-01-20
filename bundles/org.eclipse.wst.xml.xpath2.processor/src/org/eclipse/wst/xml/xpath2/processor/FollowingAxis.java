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

import org.w3c.dom.*;
import org.eclipse.wst.xml.xpath2.processor.types.*;

import java.util.*;

/**
 * the following axis contains the context node's following siblings, 
 * those children of the context node's parent that occur after the context 
 * node in document order.
 */
public class FollowingAxis extends ForwardAxis {

	/**
 	 * Return the result of FollowingAxis expression
 	 * @param node is the type of node.
	 * @param dc is the dynamic context.
	 * @return The result of FollowingAxis.
 	 */
	public ResultSequence iterate(NodeType node, DynamicContext dc) {
		ResultSequence result = ResultSequenceFactory.create_new();

		// XXX should be root... not parent!!! read the spec.... BUG BUG
		// BUG LAME LAME....

		// get the parent
		NodeType parent = null;
		ParentAxis pa = new ParentAxis();
		ResultSequence rs = pa.iterate(node, dc);
		if(rs.size() == 1)
			parent = (NodeType) rs.get(0);

	
		// get the following siblings of this node, and add them
		FollowingSiblingAxis fsa = new FollowingSiblingAxis();
		rs = fsa.iterate(node, dc);
		result.concat(rs);
		

		// for each sibling, get all its descendants
		DescendantAxis da = new DescendantAxis();
		for(Iterator i = rs.iterator(); i.hasNext();) {
			ResultSequence desc = da.iterate((NodeType)i.next(), dc);

			// add all descendants to the result
			result.concat(desc);
		}

		// if we got a parent, we gotta repeat the story for the parent
		// and add the results
		if(parent != null) {
			rs = iterate(parent, dc);
			result.concat(rs);
		}
		return result;
	}
}
