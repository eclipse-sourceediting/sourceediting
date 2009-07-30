/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     David Carver (STAR) - bug 262765 - Was not handling xml loaded dynamically in variables. 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal;

import org.w3c.dom.*;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.XSModel;
import org.eclipse.wst.xml.xpath2.processor.DefaultDynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

/**
 * The child axis contains the children of the context node.
 */
public class ChildAxis extends ForwardAxis {

	/**
	 * Retrieves the context node's children.
	 * 
	 * @param node
	 *            is the type of node.
	 * @param dc
	 *            is the dynamic context.
	 * @return The context node's children.
	 */
	public ResultSequence iterate(NodeType node, DynamicContext dc) {
		DynamicContext tempDC = dc;
		ResultSequence rs = ResultSequenceFactory.create_new();
		NodeList nl = null;
		

		// only document and element nodes have children
		if (node instanceof DocType) {
			nl = ((DocType) node).value().getChildNodes();
		}
		if (node instanceof ElementType)
			nl = ((ElementType) node).value().getChildNodes();

		// add the children to the result
		if (nl != null) {
			for (int i = 0; i < nl.getLength(); i++) {
				Node dnode = nl.item(i);
				NodeType n = null;
				try {
					n = NodeType.dom_to_xpath(dnode, tempDC
							.node_position(dnode));
				} catch (NullPointerException ex) {
					// The node wasn't found, so try creating a dynamic context to look it up
					// This happens when multiple variables are loaded with different docs so we create the context
					// on the fly
					XSModel model = null;
					if (dnode instanceof ElementPSVI) {
						ElementPSVI dnodePSVI = (ElementPSVI) dnode;
						model = dnodePSVI.getSchemaInformation();
					}
					tempDC = new DefaultDynamicContext(model, dnode.getOwnerDocument());
					n = NodeType.dom_to_xpath(dnode, tempDC.node_position(dnode));
				}

				rs.add(n);
			}
		}

		return rs;
	}

}
