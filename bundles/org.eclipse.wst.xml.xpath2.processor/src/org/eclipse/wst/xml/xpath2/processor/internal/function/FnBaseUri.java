/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *     Mukul Gandhi - bug274725 - implementation of base-uri function
 *     Jesper Steen Moeller - bug 285145 - implement full arity checking
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;
import org.w3c.dom.Node;

import java.util.*;

/**
 * Returns the value of the base-uri property for $arg as defined by the
 * accessor function dm:base-uri() for that kind of node in Section 5.1 base-uri
 * Accessor of the specification. If $arg is the empty sequence, the empty
 * sequence is returned. Document, element and processing-instruction nodes have
 * a base-uri property which may be empty. The base-uri property of all other
 * node types is the empty sequence. The value of the base-uri property is
 * returned if it exists and is not empty. Otherwise, if the node has a parent,
 * the value of dm:base-uri() applied to its parent is returned, recursively. If
 * the node does not have a parent, or if the recursive ascent up the ancestor
 * chain encounters a node whose base-uri property is empty and it does not have
 * a parent, the empty sequence is returned.
 */
public class FnBaseUri extends Function {
	private static Collection _expected_args = null;

	/**
	 * Constructor for FnBaseUri.
	 */
	public FnBaseUri() {
		super(new QName("base-uri"), 0, 1);
	}

	/**
	 * Evaluate arguments.
	 * 
	 * @param args
	 *            argument expressions.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of evaluation.
	 */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return base_uri(args, dynamic_context());
	}

	/**
	 * Base-Uri operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @param d_context
	 * 			  Dynamic context
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:base-uri operation.
	 */
	public static ResultSequence base_uri(Collection args, DynamicContext d_context) 
	                       throws DynamicError {
		Collection cargs = Function.convert_arguments(args, expected_args());
		
		ResultSequence rs = null;
		
		if (cargs.size() == 0) {
		  // support for arity 0
		  // get base-uri from the context item.
		  AnyType contextItem = d_context.context_item();
		  if (contextItem != null) {
			rs = getBaseUri(contextItem);
		  }
		  else {
		    throw DynamicError.contextUndefined();
		  }
		}
		else if (cargs.size() == 1) {
	      // support for arity 1
		  ResultSequence arg1 = (ResultSequence) cargs.iterator().next();
		  AnyType att = arg1.first();

		  rs = getBaseUri(att);
		}
		else {
		  // arity other than 0 or 1 is not allowed
		  throw DynamicError.throw_type_error();	
		}

		return rs;
	}

	/*
	 * Helper function for base-uri support
	 */
	public static ResultSequence getBaseUri(AnyType att) {
		ResultSequence rs = ResultSequenceFactory.create_new();
		XSAnyURI baseUri = null;
		  // depending on the node type, we get the base-uri for the node.
		  // if base-uri property in DOM is null, we set the base-uri as string "null". This
		  // avoids null pointer exception while comparing xs:anyURI values.
		
		  if (att instanceof NodeType) {
			  NodeType node = (NodeType) att;
			  Node domNode = node.node_value();
			  String buri = domNode.getBaseURI();
			  if (buri != null) {
				  baseUri = new XSAnyURI(buri);
			  } else {
				  baseUri = new XSAnyURI("null");
			  }
		  }
		  	        
	      if (baseUri != null) {
	        rs.add(baseUri);	
	      }
	      
	      return rs;
	}

	/**
	 * Obtain a list of expected arguments.
	 * 
	 * @return Result of operation.
	 */
	public synchronized static Collection expected_args() {
		if (_expected_args == null) {
			_expected_args = new ArrayList();
			_expected_args.add(new SeqType(SeqType.OCC_QMARK));
		}

		return _expected_args;
	}
}
