/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi - bug 273760 - wrong namespace for functions and data types 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor;

import org.apache.xerces.xs.*;
import org.eclipse.wst.xml.xpath2.processor.internal.DefaultStaticContext;
import org.eclipse.wst.xml.xpath2.processor.internal.Focus;
import org.eclipse.wst.xml.xpath2.processor.internal.function.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;
import org.w3c.dom.*;
import java.io.*;

/**
 * The default implementation of a Dynamic Context.
 * 
 * Initializes and provides functionality of a dynamic context according to the
 * XPath 2.0 specification.
 */
public class DefaultDynamicContext extends DefaultStaticContext implements
		DynamicContext {

	private Focus _focus;
	private XSDayTimeDuration _tz;
	private Hashtable _node_order;

	/**
	 * Constructor.
	 * 
	 * @param schema
	 *            Schema information of document. May be null
	 * @param doc
	 *            Document [root] node of XML source.
	 */
	public DefaultDynamicContext(XSModel schema, Document doc) {
		super(schema);

		_focus = null;
		_tz = new XSDayTimeDuration();

		init_node_order(doc);
	}

	private void init_node_order(Document doc) {
		_node_order = new Hashtable();
		add_node_order(doc, 1);
	}

	private int add_node_order(Node node, int pos) {

		// add node
		_node_order.put(node, new Integer(pos));
		pos++;

		// add attributes
		NamedNodeMap attributes = node.getAttributes();
		if (attributes != null) {
			for (int i = 0; i < attributes.getLength(); i++) {
				Attr a = (Attr) attributes.item(i);
				_node_order.put(a, new Integer(pos));
				pos++;
			}
		}

		// add children [depth first]
		NodeList children = node.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);

			pos = add_node_order(n, pos);
		}
		return pos;
	}

	/**
	 * Reads the day from a TimeDuration type
	 * 
	 * @return an xs:integer _tz
	 * @since 1.1
	 */
	public XSDayTimeDuration tz() {
		return _tz;
	}

	/**
	 * Changes the current focus.
	 * 
	 * @param f
	 *            focus to set
	 */
	public void set_focus(Focus f) {
		_focus = f;
	}

	/**
	 * Return the focus
	 * 
	 * @return _focus
	 */
	public Focus focus() {
		return _focus;
	}

	/**
	 * Retrieve context item that is in focus
	 * 
	 * @return an AnyType result from _focus.context_item()
	 */
	public AnyType context_item() {
		return _focus.context_item();
	}

	/**
	 * Retrieve the position of the focus
	 * 
	 * @return an integer result from _focus.position()
	 */
	public int context_position() {
		return _focus.position();
	}

	/**
	 * Retrieve the position of the last focus
	 * 
	 * @return an integer result from _focus.last()
	 */
	public int last() {
		return _focus.last();
	}

	/**
	 * Retrieve the variable name
	 * 
	 * @return an AnyType result from get_var(name) or return NULL
	 */
	public AnyType get_variable(QName name) {
		// XXX: built-in variables
		if ("fs".equals(name.prefix())) {
			if (name.local().equals("dot"))
				return context_item();

			return null;
		}
		return get_var(name);
	}

	/**
	 * 
	 * @return a ResultSequence from funct.evaluate(args)
	 */
	public ResultSequence evaluate_function(QName name, Collection args)
			throws DynamicError {
		Function funct = function(name, args.size());

		assert funct != null;

		return funct.evaluate(args);
	}

	/**
	 * Adds function definitions.
	 * 
	 * @param fl
	 *            Function library to add.
	 * 
	 */
	@Override
	public void add_function_library(FunctionLibrary fl) {
		super.add_function_library(fl);
		fl.set_dynamic_context(this);
	}

	/**
	 * get document
	 * 
	 * @return a ResultSequence from ResultSequenceFactory.create_new()
	 */
	public ResultSequence get_doc(String uri) {
		Document doc = retrieve_doc(uri);

		if (doc == null)
			return null;

		// XXX comparing nodes will screw up now [doc order]
		init_node_order(doc);
		return ResultSequenceFactory.create_new(new DocType(doc, 0));
	}

	// XXX make it nice, and move it out as a utility function
	private Document retrieve_doc(String uri) {
		try {
			DOMLoader loader = new XercesLoader();
			loader.set_validating(true);

			// assuming file...
			Document doc = loader.load(new FileInputStream(uri));

			return doc;
		} catch (DOMLoaderException e) {
			return null;
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	/**
	 * Sets the value of a variable.
	 * 
	 * @param var
	 *            Variable name.
	 * @param val
	 *            Variable value.
	 */
	@Override
	public void set_variable(QName var, AnyType val) {
		super.set_variable(var, val);
	}

	/**
	 * Retrieve integer of the position of node
	 * 
	 * @return integer from _node_order.get(node)
	 */
	public int node_position(Node node) {
		Integer pos = (Integer) _node_order.get(node);

		assert pos != null;

		return pos.intValue();
	}

}
