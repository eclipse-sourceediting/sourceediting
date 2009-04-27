/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Ganhdi - bug 273719 - String Length does not work with Element arg. 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.StaticContext;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

/**
 * Support for functions.
 */
public abstract class Function {

	protected QName _name;
	protected int _arity; // if negative, need to have "at least"
	// abs(_arity) args
	// variable args basically...
	protected FunctionLibrary _fl;

	/**
	 * Constructor for Function.
	 * 
	 * @param name
	 *            QName.
	 * @param arity
	 *            the arity of a specific function.
	 */
	public Function(QName name, int arity) {
		_name = name;
		_arity = arity;
		_fl = null;
	}

	/**
	 * Support for QName interface.
	 * 
	 * @return Result of QName operation.
	 */
	public QName name() {
		return _name;
	}

	/**
	 * Support for int interface.
	 * 
	 * @return Result of int operation.
	 */
	public int arity() {
		return _arity;
	}

	/**
	 * Default constructor for signature.
	 * 
	 * @return Signature.
	 */
	public String signature() {
		return signature(this);
	}

	/**
	 * Obtain the function name and arity from signature.
	 * 
	 * @param f
	 *            current function.
	 * @return Signature.
	 */
	public static String signature(Function f) {
		return signature(f.name(), f.arity());
	}

	/**
	 * Apply the name and arity to signature.
	 * 
	 * @param name
	 *            QName.
	 * @param arity
	 *            arity of the function.
	 * @return Signature.
	 */
	public static String signature(QName name, int arity) {
		String n = name.expanded_name();
		if (n == null)
			return null;

		n += "_";

		if (arity < 0)
			n += "x";
		else
			n += arity;

		return n;
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
	public abstract ResultSequence evaluate(Collection args)
			throws DynamicError;

	// convert argument according to section 3.1.5 of xpath 2.0 spec
	/**
	 * Convert the input argument according to section 3.1.5 of specification.
	 * 
	 * @param arg
	 *            input argument.
	 * @param expected
	 *            Expected Sequence type.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Converted argument.
	 */
	public static ResultSequence convert_argument(ResultSequence arg,
			SeqType expected) throws DynamicError {
		ResultSequence result = arg;

		AnyType expected_type = expected.type();
		
		// expected is atomic
		if (expected_type instanceof AnyAtomicType) {
			AnyAtomicType expected_aat = (AnyAtomicType) expected_type;
			
			// atomize
			ResultSequence rs = FnData.atomize(arg);

			// cast untyped to expected type
			result = ResultSequenceFactory.create_new();
			for (Iterator i = rs.iterator(); i.hasNext();) {
				AnyType item = (AnyType) i.next();
				
				if (item instanceof UntypedAtomic) {
					// create a new item of the expected
					// type initialized with from the string
					// value of the item
					ResultSequence converted = null;
					if (expected_aat instanceof XSString) {
					   XSString strType = new XSString(item.string_value());
					   converted = ResultSequenceFactory.create_new(strType);
					}
					else {
					   converted = ResultSequenceFactory.create_new(item);
					}
					
					result.concat(converted);
				}

				// numeric type promotion
				else if (item instanceof NumericType) {
					// XXX TODO numeric type promotion
					result.add(item);
				} else
					result.add(item);
			}
		}

		// do sequence type matching on converted arguments
		return expected.match(result);
	}

	// convert arguments
	// returns collection of arguments
	/**
	 * Convert arguments.
	 * 
	 * @param args
	 *            input arguments.
	 * @param expected
	 *            expected arguments.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Converted arguments.
	 */
	public static Collection convert_arguments(Collection args,
			Collection expected) throws DynamicError {
		Collection result = new ArrayList();

		assert args.size() == expected.size();

		Iterator argi = args.iterator();
		Iterator expi = expected.iterator();

		// convert all arguments
		while (argi.hasNext()) {
			result.add(convert_argument((ResultSequence) argi.next(),
					(SeqType) expi.next()));
		}

		return result;
	}

	/**
	 * Set the function library variable.
	 * 
	 * @param fl
	 *            Function Library.
	 */
	public void set_function_library(FunctionLibrary fl) {
		_fl = fl;
	}

	protected StaticContext static_context() {
		if (_fl == null)
			return null;

		return _fl.static_context();
	}

	protected DynamicContext dynamic_context() {
		if (_fl == null)
			return null;

		return _fl.dynamic_context();
	}

}
