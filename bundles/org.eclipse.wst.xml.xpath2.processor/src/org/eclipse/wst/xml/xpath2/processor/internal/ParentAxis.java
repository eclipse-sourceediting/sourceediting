/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *     Jesper Moller - bug 275610 - Avoid big time and memory overhead for externals
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal;

import org.eclipse.wst.xml.xpath2.api.ResultBuffer;
import org.eclipse.wst.xml.xpath2.processor.internal.types.NodeType;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/**
 * the parent axis contains the sequence returned by the dm:parent accessor in,
 * which returns the parent of the context node, or an empty sequence if the
 * context node has no parent
 */
public class ParentAxis extends ReverseAxis {

	/**
	 * returns parent accessors of the context node
	 * 
	 * @param node
	 *            is the node type.
	 * @throws dc
	 *             is the Dynamic context.
	 */
	public void iterate(NodeType node, ResultBuffer copyInto, Node limitNode) {
		Node n = node.node_value();
		
		if (limitNode != null && limitNode.isSameNode(n)) {
			// no further, we have reached the limit node
			return;
		}

		Node parent = findParent(n);

		// if a parent exists... add it
		if (parent != null)
			copyInto.add(NodeType.dom_to_xpath(parent, node.getTypeModel()));
	}

	public Node findParent(Node n) {
		Node parent = n.getParentNode();

		// special case attribute elements...
		// in this case... the parent is the element which owns the attr
		if (n.getNodeType() == Node.ATTRIBUTE_NODE) {
			Attr att = (Attr) n;

			parent = att.getOwnerElement();
		}
		return parent;
	}
	
	public String name() {
		return "parent";
	}
}
