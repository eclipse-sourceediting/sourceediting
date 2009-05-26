/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi - bug 276134 - improvements to schema aware primitive type support
 *                                 for attribute/element nodes
 *     David Carver - bug 277774 - XSDecimal returning wrong values. 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * A representation of a Node datatype
 */
public abstract class NodeType extends AnyType {
	private Node _node;
	private int _document_order;

	/**
	 * Initialises according to the supplied parameters
	 * 
	 * @param node
	 *            The Node being represented
	 * @param document_order
	 *            The document order
	 */
	public NodeType(Node node, int document_order) {
		_node = node;
		_document_order = document_order;
	}

	/**
	 * Retrieves the actual node being represented
	 * 
	 * @return Actual node being represented
	 */
	public Node node_value() {
		return _node;
	}

	/**
	 * Retrieves the document order as an integer
	 * 
	 * @return Document order as an integer
	 */
	public int document_order() {
		return _document_order;
	}

	// Accessors defined in XPath Data model
	// http://www.w3.org/TR/xpath-datamodel/
	/**
	 * Retrieves the actual node being represented
	 * 
	 * @return Actual node being represented
	 */
	public abstract ResultSequence typed_value();

	/**
	 * Retrieves the name of the node
	 * 
	 * @return QName representation of the name of the node
	 */
	public abstract QName node_name(); // may return null ["empty sequence"]

	// XXX element should override
	public ResultSequence nilled() {
		return ResultSequenceFactory.create_new();
	}

	// a little factory for converting from DOM to our representation
	public static NodeType dom_to_xpath(Node node, int doc_pos) {
		switch (node.getNodeType()) {
		case Node.ELEMENT_NODE:
			return new ElementType((Element) node, doc_pos);

		case Node.COMMENT_NODE:
			return new CommentType((Comment) node, doc_pos);

		case Node.ATTRIBUTE_NODE:
			return new AttrType((Attr) node, doc_pos);

		case Node.TEXT_NODE:
			return new TextType((Text) node, doc_pos);

		case Node.DOCUMENT_NODE:
			return new DocType((Document) node, doc_pos);

		case Node.PROCESSING_INSTRUCTION_NODE:
			return new PIType((ProcessingInstruction) node, doc_pos);

			// XXX
		default:
			assert false;

		}

		// unreach... hopefully
		return null;
	}

	public static ResultSequence eliminate_dups(ResultSequence rs) {
		Hashtable added = new Hashtable(rs.size());

		for (Iterator i = rs.iterator(); i.hasNext();) {
			NodeType node = (NodeType) i.next();
			Node n = node.node_value();

			if (added.containsKey(n))
				i.remove();
			else
				added.put(n, new Boolean(true));
		}
		return rs;
	}

	public static ResultSequence sort_document_order(ResultSequence rs) {
		ArrayList res = new ArrayList(rs.size());

		for (Iterator i = rs.iterator(); i.hasNext();) {
			NodeType node = (NodeType) i.next();
			boolean added = false;

			for (int j = 0; j < res.size(); j++) {
				NodeType x = (NodeType) res.get(j);

				if (before(node, x)) {
					res.add(j, node);
					added = true;
					break;
				}
			}
			if (!added)
				res.add(node);
		}

		rs = ResultSequenceFactory.create_new();
		for (Iterator i = res.iterator(); i.hasNext();) {
			NodeType node = (NodeType) i.next();

			rs.add(node);
		}

		return rs;
	}

	public static boolean same(NodeType a, NodeType b) {
		return a.document_order() == b.document_order();
	}

	public boolean before(NodeType two) {
		return before(this, two);
	}

	public static boolean before(NodeType a, NodeType b) {
		return a.document_order() < b.document_order();
	}

	public boolean after(NodeType two) {
		return after(this, two);
	}

	public static boolean after(NodeType a, NodeType b) {
		if (same(a, b))
			return false;

		return !before(a, b);
	}
	
	protected Object getTypedValueForPrimitiveType(XSTypeDefinition typeDef) {		
		Object schemaTypeValue = null;
		
		if (typeDef.getName().equals("date")) {		
		   schemaTypeValue = XSDate.parse_date(string_value());
		}
		else if (typeDef.getName().equals("int")) {		
		   schemaTypeValue = new XSInt(new BigInteger(string_value()));
		}
		else if (typeDef.getName().equals("long")) {		
		  schemaTypeValue = new XSLong(new BigInteger(string_value()));
	    }
		else if (typeDef.getName().equals("integer")) {		
		  schemaTypeValue = new XSInteger(new BigInteger(string_value()));
		}
		else if (typeDef.getName().equals("double")) {		
		  schemaTypeValue = new XSDouble(Double.parseDouble(string_value()));
		}
		else if (typeDef.getName().equals("float")) {		
		  schemaTypeValue = new XSFloat(Float.parseFloat(string_value()));
	    }
		else if (typeDef.getName().equals("decimal")) {		
		  schemaTypeValue = new XSDecimal(new BigDecimal(string_value()));
		}
		
		return schemaTypeValue;
	}
}
