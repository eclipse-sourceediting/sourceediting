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

/**
 * This is the interface class for an Axis.
 * 
 * An axis defines the "direction of movement" for a step between a context node
 * and another node that is reachable via the axis.
 */
public interface Axis {
	/**
	 * Get elements and attributes.
	 * 
	 * @param node
	 *            is the type of node.
	 * @param dc
	 *            is the dynamic context.
	 * @return The result sequence.
	 */
	public ResultSequence iterate(NodeType node, DynamicContext dc);

	/**
	 * Get the principle kind of node.
	 * 
	 * @return The principle node kind.
	 */
	public NodeType principal_node_kind();
}
