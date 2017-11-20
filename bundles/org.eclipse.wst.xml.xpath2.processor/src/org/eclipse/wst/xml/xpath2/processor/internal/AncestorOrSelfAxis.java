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
 * The ancestor-or-self axis contains the context node and the ancestors of the
 * context node, this always includes the root node.
 */
// multiple inheretance might be cool here =D
public class AncestorOrSelfAxis extends ReverseAxis {

	/**
	 * Get ancestor nodes of the context node and the context node itself.
	 * 
	 * @param node
	 *            is the type of node.
	 */
	public void iterate(NodeType node, ResultBuffer copyInto, Node limitNode) {
		// get ancestors
		AncestorAxis aa = new AncestorAxis();
		aa.iterate(node, copyInto, null);

		// add self
		copyInto.add(node);
	}

	public String name() {
		return "ancestor-or-self";
	}
}
