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
	 * @param dc
	 *            is the dynamic context.
	 * @return The context node and its ancestors.
	 */
	public ResultSequence iterate(NodeType node, DynamicContext dc) {
		// get ancestors
		AncestorAxis aa = new AncestorAxis();
		ResultSequence rs = aa.iterate(node, dc);

		// add self
		rs.add(node);

		return rs;
	}

}
