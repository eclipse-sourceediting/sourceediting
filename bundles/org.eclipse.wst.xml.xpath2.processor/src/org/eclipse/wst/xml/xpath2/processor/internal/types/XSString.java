/*******************************************************************************
 * Copyright (c) 2005, 2010 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi - improved comparison of xs:string with other XDM types
 *  Jesper S Moller - bug 286061   correct handling of quoted string 
 *  Jesper S Moller - bug 280555 - Add pluggable collation support
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import java.math.BigInteger;

import org.eclipse.wst.xml.xpath2.api.DynamicContext;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.function.CmpEq;
import org.eclipse.wst.xml.xpath2.processor.internal.function.CmpGt;
import org.eclipse.wst.xml.xpath2.processor.internal.function.CmpLt;
import org.eclipse.wst.xml.xpath2.processor.internal.function.FnCompare;
import org.eclipse.wst.xml.xpath2.processor.internal.types.builtin.BuiltinTypeLibrary;

/**
 * A representation of the String datatype
 */
public class XSString extends CtrType implements CmpEq, CmpGt, CmpLt {

	private static final String XS_STRING = "xs:string";
	private String _value;

	/**
	 * Initialises using the supplied String
	 * 
	 * @param x
	 *            The String to initialise to
	 */
	public XSString(String x) {
		_value = x;
	}

	/**
	 * Initialises to null
	 */
	public XSString() {
		this(null);
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xs:string" which is the datatype's full pathname
	 */
	public String string_type() {
		return XS_STRING;
	}

	/**
	 * Retrieves the datatype's name
	 * 
	 * @return "string" which is the datatype's name
	 */
	public String type_name() {
		return "string";
	}

	/**
	 * Retrieves a String representation of the string stored. This method is
	 * functionally identical to value()
	 * 
	 * @return The String stored
	 */
	public String string_value() {
		return _value;
	}

	/**
	 * Retrieves a String representation of the string stored. This method is
	 * functionally identical to string_value()
	 * 
	 * @return The String stored
	 */
	public String value() {
		return string_value();
	}

	/**
	 * Creates a new ResultSequence consisting of the extractable String in the
	 * supplied ResultSequence
	 * 
	 * @param arg
	 *            The ResultSequence from which to extract the String
	 * @return New ResultSequence consisting of the supplied String
	 * @throws DynamicError
	 */
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		//AnyAtomicType aat = (AnyAtomicType) arg.first();
		AnyType aat = arg.first();

		rs.add(new XSString(aat.string_value()));

		return rs;
	}

	// comparisons

	// 666 indicates death [compare returned empty seq]
	private int do_compare(AnyType arg, DynamicContext dc) throws DynamicError {

		// XXX: This can't happen, I guess
		if (arg == null) return 666;

		XSString comparand = arg instanceof XSString ? (XSString)arg : new XSString(arg.string_value());
		
		BigInteger result = FnCompare.compare_string(dc.getCollationProvider().getDefaultCollation(), this, comparand, dc);

		return result.intValue();
	}

	/**
	 * Equality comparison between this and the supplied representation which
	 * must be of type String
	 * 
	 * @param arg
	 *            The representation to compare with
	 * @return True if the two representation are of the same String. False
	 *         otherwise
	 * @throws DynamicError
	 */
	public boolean eq(AnyType arg, DynamicContext dynamicContext) throws DynamicError {
		int cmp = do_compare(arg, dynamicContext);

		// XXX im not sure what to do here!!! because eq has to return
		// something i fink....
		if (cmp == 666)
			assert false;

		return cmp == 0;
	}

	/**
	 * Comparison between this and the supplied representation which must be of
	 * type String
	 * 
	 * @param arg
	 *            The representation to compare with
	 * @return True if this String is lexographically greater than that
	 *         supplied. False otherwise
	 * @throws DynamicError
	 */
	public boolean gt(AnyType arg, DynamicContext context) throws DynamicError {
		int cmp = do_compare(arg, context);

		assert cmp != 666;

		return cmp > 0;
	}

	/**
	 * Comparison between this and the supplied representation which must be of
	 * type String
	 * 
	 * @param arg
	 *            The representation to compare with
	 * @return True if this String is lexographically less than that supplied.
	 *         False otherwise
	 * @throws DynamicError
	 */
	public boolean lt(AnyType arg, DynamicContext context) throws DynamicError {
		int cmp = do_compare(arg, context);

		assert cmp != 666;

		return cmp < 0;
	}
	public TypeDefinition getTypeDefinition() {
		return BuiltinTypeLibrary.XS_STRING;
	}

}
