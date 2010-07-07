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
 *     David Carver (STAR)- bug 277774 - XSDecimal returning wrong values.
 *     Jesper Moller - bug 275610 - Avoid big time and memory overhead for externals
 *     David Carver (STAR) - bug 281186 - implementation of fn:id and fn:idref
 *     David Carver (STAR) - bug 289304 - fixed schema awareness on elements
 *     Mukul Gandhi - bug 318313 - improvements to computation of typed values of nodes,
 *                                 when validated by XML Schema primitive types
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

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
import org.w3c.dom.TypeInfo;

/**
 * A representation of a Node datatype
 */
public abstract class NodeType extends AnyType {
	protected static final String SCHEMA_TYPE_IDREF = "IDREF";
	protected static final String SCHEMA_TYPE_ID = "ID";
	private Node _node;

	/**
	 * Initialises according to the supplied parameters
	 * 
	 * @param node
	 *            The Node being represented
	 * @param document_order
	 *            The document order
	 */
	public NodeType(Node node) {
		_node = node;
	}

	/**
	 * Retrieves the actual node being represented
	 * 
	 * @return Actual node being represented
	 */
	public Node node_value() {
		return _node;
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
	public static NodeType dom_to_xpath(Node node) {
		assert node != null;
		
		switch (node.getNodeType()) {
		case Node.ELEMENT_NODE:
			return new ElementType((Element) node);

		case Node.COMMENT_NODE:
			return new CommentType((Comment) node);

		case Node.ATTRIBUTE_NODE:
			return new AttrType((Attr) node);

		case Node.TEXT_NODE:
			return new TextType((Text) node);

		case Node.DOCUMENT_NODE:
			return new DocType((Document) node);

		case Node.PROCESSING_INSTRUCTION_NODE:
			return new PIType((ProcessingInstruction) node);

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
				added.put(n, Boolean.TRUE);
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
		return (a.node_value().isSameNode(b.node_value()));
		// While compare_node(a, b) == 0 is tempting, it is also expensive
	}

	public boolean before(NodeType two) {
		return before(this, two);
	}

	public static boolean before(NodeType a, NodeType b) {
		return compare_node(a, b) < 0;
	}

	public boolean after(NodeType two) {
		return after(this, two);
	}

	public static boolean after(NodeType a, NodeType b) {
		return compare_node(a, b) > 0;
	}
	
	private static int compare_node(NodeType a, NodeType b) {
		Node nodeA = a.node_value();
		Node nodeB = b.node_value();
		
		if (nodeA == nodeB || nodeA.isSameNode(nodeB)) return 0;

		Document docA = getDocument(nodeA);
		Document docB = getDocument(nodeB);
		
		if (docA != docB && ! docA.isSameNode(docB)) {
			return compareDocuments(docA, docB);
		}
		short relation = nodeA.compareDocumentPosition(nodeB);
		if ((relation & Node.DOCUMENT_POSITION_PRECEDING) != 0) 
			  return 1;
		if ((relation & Node.DOCUMENT_POSITION_FOLLOWING) != 0) 
			  return -1;
		throw new RuntimeException("Unexpected result from node comparison: " + relation);
	}

	private static int compareDocuments(Document docA, Document docB) {
		// Arbitrary but fulfills the spec (provided documenURI is always set)
		if (docB.getDocumentURI() == null && docA.getDocumentURI() == null) {
			// Best guess
			return 0; 
		}
		return docB.getDocumentURI().compareTo(docA.getDocumentURI());
	}

	private static Document getDocument(Node nodeA) {
		return nodeA instanceof Document ? (Document)nodeA : nodeA.getOwnerDocument();
	}

	protected Object getTypedValueForPrimitiveType(XSTypeDefinition typeDef) {		
		String strValue = string_value();
		
		if (typeDef == null) {
		   return new XSUntypedAtomic(strValue);
		}
		
		return SchemaTypeValueFactory.newSchemaTypeValue(typeDef.getName(), strValue);
		
	}
	
	public abstract boolean isID();
	
	public abstract boolean isIDREF();

	/**
	 * Utility method to check to see if a particular TypeInfo matches.
	 * @param typeInfo
	 * @param typeName
	 * @return
	 */
	protected boolean isType(TypeInfo typeInfo, String typeName) {
		if (typeInfo != null) {
			String typeInfoName = typeInfo.getTypeName();
			if (typeInfoName != null) {
				if (typeInfo.getTypeName().equalsIgnoreCase(typeName)) {
					return true;
				}
			} 
		}
		return false;
	}
}
