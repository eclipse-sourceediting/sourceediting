/*******************************************************************************
 * Copyright (c) 2005, 2010 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Jesper Moller- bug 275610 - Avoid big time and memory overhead for externals
 *     David Carver  - bug 281186 - implementation of fn:id and fn:idref
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeModel;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.types.builtin.BuiltinTypeLibrary;
import org.w3c.dom.ProcessingInstruction;

/**
 * A representation of the ProcessingInstruction datatype
 */
public class PIType extends NodeType {
	private static final String PROCESSING_INSTRUCTION = "processing instruction";
	private ProcessingInstruction _value;

	/**
	 * Initialises according to the supplied parameters
	 * 
	 * @param v
	 *            The processing instruction this node represents
	 * @param doc_order
	 *            The document order
	 */
	public PIType(ProcessingInstruction v, TypeModel tm) {
		super(v, tm);
		_value = v;
	}

	/**
	 * Retrieves the actual processing instruction this node represents
	 * 
	 * @return Actual processing instruction this node represents
	 */
	public ProcessingInstruction value() {
		return _value;
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "processing-instruction" which is the datatype's full pathname
	 */
	public String string_type() {
		return PROCESSING_INSTRUCTION;
	}

	/**
	 * Retrieves a String representation of the actual processing instruction
	 * stored
	 * 
	 * @return String representation of the actual processing instruction stored
	 */
	public String string_value() {
		return _value.getData();
	}

	/**
	 * Creates a new ResultSequence consisting of the processing instruction
	 * stored
	 * 
	 * @return New ResultSequence consisting of the processing instruction
	 *         stored
	 */
	public ResultSequence typed_value() {
		ResultSequence rs = ResultSequenceFactory.create_new();

		rs.add(new XSString(string_value()));

		return rs;
	}

	/**
	 * Constructs the node's name
	 * 
	 * @return A QName representation of the node's name
	 */
	public QName node_name() {
		QName name = new QName(null, _value.getTarget());

		name.set_namespace(null);

		return name;
	}

	/**
	 * @since 1.1
	 */
	public boolean isID() {
		return false;
	}

	/**
	 * @since 1.1
	 */
	public boolean isIDREF() {
		return false;
	}
	
	public TypeDefinition getTypeDefinition() {
		return BuiltinTypeLibrary.XS_UNTYPEDATOMIC;
	}
}
