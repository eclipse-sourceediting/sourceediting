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

import org.eclipse.wst.xml.xpath2.api.ResultBuffer;
import org.eclipse.wst.xml.xpath2.processor.internal.types.NodeType;
import org.w3c.dom.Node;

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
	 */
	public void iterate(NodeType node, ResultBuffer copyInto) {
		// XXX check for attribute / namespace node... if so return
		// empty sequence

		Node iterNode = node.node_value();
		// get the children of the parent [siblings]
		do {
			iterNode = iterNode.getNextSibling();
			if (iterNode != null) {
				copyInto.add(NodeType.dom_to_xpath(iterNode, node.getTypeModel()));
			}
		} while (iterNode != null);
	}
	
	public String name() {
		return "following";
	}
}
