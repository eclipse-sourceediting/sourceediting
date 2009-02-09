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

package org.eclipse.wst.xml.xpath2.processor.function;

// this is the equivalent of libc =D
/**
 * Maintains a library of built-in operators as functions.
 * 
 * This is necessary if normalization is being used.
 */
public class OpFunctionLibrary extends FunctionLibrary {

	// XXX should be internal
	public static final String XPATH_OP_NS = "http://www.w3.org/TR/2003/WD-xquery-semantics-20030502/";

	/**
	 * Constructor for OpFunctionLibrary.
	 */
	public OpFunctionLibrary() {
		super(XPATH_OP_NS);

		// operators according to formal semantics
		add_function(new FsDiv());
		add_function(new FsEq());
		add_function(new FsGe());
		add_function(new FsGt());
		add_function(new FsIDiv());
		add_function(new FsLe());
		add_function(new FsLt());
		add_function(new FsMinus());
		add_function(new FsMod());
		add_function(new FsNe());
		add_function(new FsPlus());
		add_function(new FsTimes());

		// utility functions in formal semantics
		add_function(new FsConvertOperand());

		// operators according to functions & operators
		add_function(new OpExcept());
		add_function(new OpIntersect());
		add_function(new OpTo());
		add_function(new OpUnion());
	}
}
