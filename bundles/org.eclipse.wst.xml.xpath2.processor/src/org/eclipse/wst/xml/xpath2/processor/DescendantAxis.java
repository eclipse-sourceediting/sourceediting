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
 * The descendant axis contains the descendants of the context node
 */
public class DescendantAxis extends ChildAxis {

	/**
	 * Using the context node retrieve the descendants of this node
	 * @param node is the type of node.
	 * @param dc is the dynamic context.
	 * @return The descendants of the context node.
	 */
	public ResultSequence iterate(NodeType node, DynamicContext dc) {
		// get the children
		ResultSequence rs = super.iterate(node, dc);

		ArrayList descendants = new ArrayList();

		// get descendants of all children
		for(Iterator i = rs.iterator(); i.hasNext();) {
			NodeType n = (NodeType) i.next();

			descendants.add(iterate(n, dc));
		}

		// add descendants to result
		for(Iterator i = descendants.iterator(); i.hasNext();) {
			ResultSequence desc = (ResultSequence) i.next();

			rs.concat(desc);
		}

		return rs;
	}

}
