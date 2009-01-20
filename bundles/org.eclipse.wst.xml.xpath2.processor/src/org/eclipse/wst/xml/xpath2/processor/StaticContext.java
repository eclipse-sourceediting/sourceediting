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

import org.apache.xerces.xs.*;
import org.eclipse.wst.xml.xpath2.processor.function.*;
import org.eclipse.wst.xml.xpath2.processor.types.*;

import java.util.*;

/**
 * interface to static context
 */
public interface StaticContext {

	/**
	 * is it xpath 1.0 compatible.
 	 * @return boolean
 	 */
	public boolean xpath1_compatible();
	
	/**
	 * namespaces
	 * does the prefix exist
	 * @param prefix is the prefix
 	 * @return boolean
 	 */
	public boolean prefix_exists(String prefix);

	/**
	 * @param prefix is the prefix
 	 * @return string
 	 */
	public String resolve_prefix(String prefix);

	/**
	 * the default namespace
 	 * @return string
 	 */
	public String default_namespace();

	/**
	 * the default function namespace
 	 * @return string
 	 */
	public String default_function_namespace();

	// in scope schema definitions
	/**
	 * @param attr is the qname variable
 	 * @return attributes's type definition
 	 */
	public XSTypeDefinition attribute_type_definition(QName attr);

	/**
	 * @param elem is the elem of the qname
 	 * @return element's type definition
 	 */
	public XSTypeDefinition element_type_definition(QName elem);

	/**
	 * is the attribute declared?
	 * @param attr is the attribute of the qname
 	 * @return boolean
 	 */
	public boolean attribute_declared(QName attr);

	/**
	 * is the element declared?
	 * @param elem is the elem of the qname
 	 * @return boolean 
 	 */
	public boolean element_declared(QName elem);

	// in scope variables

	// context item type

	/**
	 * is the element declared?
	 * @param name is the qname name
	 * @param arity integer of qname
 	 * @return boolean 
 	 */
	// function signatures
	public boolean function_exists(QName name, int arity);

	// collations

	/**
	 * base uri
 	 * @return uri
 	 */
	// base uri
	public XSAnyURI base_uri();

	// statically known documents

	// collections



	// other stuff
	/**
	 * new scope
 	 */
	public void new_scope();

	/**
	 * destroy scope
 	 */
	public void destroy_scope();

	/**
	 * add variable
	 * @param name is the qname
 	 */
	public void add_variable(QName name);


	/**
	 * delete the variable
	 * @param name is the qname
	 * @return boolean if deleted variable
 	 */
	public boolean del_variable(QName name);

	/**
	 * @param name is the qname
	 * @return boolean if variable exists
 	 */
	public boolean variable_exists(QName name); // in current scope only

	/**
	 * @param var is the variable of qname
	 */
	public boolean variable_in_scope(QName var);

	/**
	 * @param name is qname
	 * @return boolean
	 */
	public boolean type_defined(QName name);

	/**
	 * @param at the node type
	 * @param et is the qname
	 * @return boolean
 	 */
	public boolean derives_from(NodeType at, QName et);

	/**
	 * @param at the node type
	 * @param et is the XSTypeDefinition of the node
	 * @return boolean
 	 */
	public boolean derives_from(NodeType at, XSTypeDefinition et);

	/**
	 * add namespace
	 * @param prefix the prefix of the namespace
	 * @param ns is the XSTypeDefinition of the node
 	 */
	public void add_namespace(String prefix, String ns);

	/**
	 * expand function
	 * @param name is the qname
	 * @return boolean if function can be expanded
 	 */
	public boolean expand_function_qname(QName name);

	/**
	 * expand element type qname
	 * @param name is the qname
	 * @return boolean if function can be expanded
 	 */
	public boolean expand_elem_type_qname(QName name);

	/**
	 * expand qname
	 * @param name is the qname
	 * @return boolean if function can be expanded
 	 */
	public boolean expand_qname(QName name);

	/**
	 * add function to library
	 * @param fl is the function library
 	 */
	public void add_function_library(FunctionLibrary fl);

	/**
	 * @param name is the qname
	 * @return any atomic type
 	 */
	public AnyAtomicType make_atomic(QName name);
}
