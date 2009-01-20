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

import java.util.*;

/**
 * the preceding axis contains all nodes that are descendants of the root of  
 * the tree in which the context node is found
 */
public class PrecedingAxis extends ReverseAxis {
// XXX DOCUMENT ORDER....  dunno

	/**
 	 * returns preceding nodes of the context node
 	 * @param node is the node type.
	 * @throws dc is the Dynamic context.
	 * @return the descendants of the context node
 	 */
	public ResultSequence iterate(NodeType node, DynamicContext dc) {
		ResultSequence result = ResultSequenceFactory.create_new();
	
		// get the parent
		NodeType parent = null;
		ParentAxis pa = new ParentAxis();
		ResultSequence rs = pa.iterate(node, dc);
		if(rs.size() == 1)
			parent = (NodeType) rs.get(0);

	
		// get the preceding siblings of this node, and add them
		PrecedingSiblingAxis psa = new PrecedingSiblingAxis();
		rs = psa.iterate(node, dc);
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

			rs.concat(result);
			result = rs;
		}
		return result;
	}
}
