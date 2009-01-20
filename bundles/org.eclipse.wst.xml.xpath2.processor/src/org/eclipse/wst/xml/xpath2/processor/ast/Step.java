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

package org.eclipse.wst.xml.xpath2.processor.ast;

/**
 * Support for Step operations.
 */
public abstract class Step extends XPathNode {

	private NodeTest _node_test;

	/**
	 * Constructor for Step.
	 * 
	 * @param node_test
	 *            Nodes for operation.
	 */
	public Step(NodeTest node_test) {
		_node_test = node_test;
	}

	/**
	 * Support for NodeTest interface.
	 * 
	 * @return Result of NodeTest operation.
	 */
	public NodeTest node_test() {
		return _node_test;
	}
}
