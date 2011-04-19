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

/**
 * The descendant-or-self axis contains the context node and the descendants of
 * the context node.
 */
// multiple inheretance might be cool here =D
public class DescendantOrSelfAxis extends ForwardAxis {

	/**
	 * Retrieve the the descendants of the context node and the context node
	 * itself.
	 * 
	 * @param node
	 *            is the type of node.
	 */
	public void iterate(NodeType node, ResultBuffer rs) {

		// add self
		rs.add(node);

		// add descendants
		new DescendantAxis().iterate(node, rs);
	}

	public String name() {
		return "descendant-or-self";
	}
}
