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

import org.w3c.dom.*;
import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

/**
 * The attribute axis contains the attributes of the context node. The axis will
 * be empty unless the context node is an element.
 */
public class AttributeAxis extends ForwardAxis {

	/**
	 * Retrieves the context node's attributes.
	 * 
	 * @param node
	 *            is the type of node.
	 * @param dc
	 *            is the dynamic context.
	 * @return The attibutes of the context node.
	 */
	public ResultSequence iterate(NodeType node, DynamicContext dc) {
		ResultSequence rs = ResultSequenceFactory.create_new();

		// only elements have attributes
		if (!(node instanceof ElementType))
			return rs;

		// get attributes
		ElementType elem = (ElementType) node;
		NamedNodeMap attrs = elem.value().getAttributes();

		// add attributes
		for (int i = 0; i < attrs.getLength(); i++) {
			Attr attr = (Attr) attrs.item(i);

			rs.add(NodeType.dom_to_xpath(attr, dc.getTypeModel(attr)));
		}

		return rs;

	}

	/**
	 * Retrieves the node's principle node kind.
	 * 
	 * @return The type of node.
	 */
	@Override
	public NodeType principal_node_kind() {
		return new AttrType();
	}

}
