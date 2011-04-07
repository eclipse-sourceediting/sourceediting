/*******************************************************************************
 * Copyright (c) 2005, 2011 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi - bug 273719 - String Length does not work with Element arg.
 *     Mukul Gandhi - bug 273795 - improvements to function, substring (implemented
 *                                 numeric type promotion). 
 *     Jesper Steen Moeller - bug 285145 - implement full arity checking
 *     Jesper Steen Moeller - bug 281159 - implement xs:anyUri -> xs:string promotion
 *     Jesper Steen Moller  - bug 281938 - undefined context should raise error
 *     Jesper Steen Moller  - bug 340933 - Migrate to new XPath2 API
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.wst.xml.xpath2.api.EvaluationContext;
import org.eclipse.wst.xml.xpath2.api.typesystem.TypeDefinition;
import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.StaticContext;
import org.eclipse.wst.xml.xpath2.processor.internal.SeqType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyAtomicType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.NumericType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.QName;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSAnyURI;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSDouble;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSString;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSUntypedAtomic;
import org.eclipse.wst.xml.xpath2.processor.internal.types.builtin.BuiltinTypeLibrary;
import org.eclipse.wst.xml.xpath2.processor.util.ResultSequenceUtil;

/**
 * Support for functions.
 */
public abstract class Function implements org.eclipse.wst.xml.xpath2.api.Function {

	protected QName _name;
	/**
	 * if negative, need to have "at least"
	 */
	protected int _min_arity; 
	
	/**
	 * If "at least", this speci, unlimited if -1
	 */
	protected int _max_arity;
	
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
		if (arity < 0) {
			throw new RuntimeException("We want to avoid this!");
		}
		_min_arity = arity;
		_max_arity = arity;
		_fl = null;
	}

	/**
	 * Constructor for Function.
	 * 
	 * @param name
	 *            QName.
	 * @param arity
	 *            the arity of a specific function.
	 */
	public Function(QName name, int min_arity, int max_arity) {
		_name = name;
		if (min_arity < 0 || max_arity < 0 || max_arity < min_arity) {
			throw new RuntimeException("We want to avoid this!");
		}
		_min_arity = min_arity;
		_max_arity = max_arity;
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
	 * Minimal number of allowed arguments.
	 * 
	 * @return The smallest number of erguments possible
	 */
	public int min_arity() {
		return _min_arity;
	}

	/**
	 * Maximum number of allowed arguments.
	 * 
	 * @return The highest number of erguments possible
	 */
	public int max_arity() {
		return _max_arity;
	}

	/**
	 * Checks if this function has an to the
	 * 
	 * @param actual_arity
	 * @return
	 */
	public boolean matches_arity(int actual_arity) {
		if (actual_arity < min_arity()) return false;
		if (actual_arity > max_arity()) return false;
		return true;
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
		return signature(f.name(), f.is_vararg() ? -1 : f.min_arity());
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
	public ResultSequence evaluate(Collection args)
			throws DynamicError {
		
		return ResultSequenceUtil.newToOld(evaluate(args, new EvaluationContext() {
			
			public org.eclipse.wst.xml.xpath2.api.StaticContext getStaticContext() {
				throw new UnsupportedOperationException("ikke længere");
			}
			
			public int getLastPosition() {
				throw new UnsupportedOperationException("ikke længere");
			}
			
			public org.eclipse.wst.xml.xpath2.api.DynamicContext getDynamicContext() {
				throw new UnsupportedOperationException("ikke længere");
			}
			
			public int getContextPosition() {
				throw new UnsupportedOperationException("ikke længere");
			}
			
			public Object getContextItem() {
				throw new UnsupportedOperationException("ikke længere");
			}
		}));
	}

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

		// XXX: Should use type_class instead and use item.getClass().isAssignableTo(expected.type_class())
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
				
				if (item instanceof XSUntypedAtomic) {
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
				// xs:anyURI promotion to xs:string
				else if (item instanceof XSAnyURI && expected_aat instanceof XSString) {
					result.add(new XSString(item.string_value()));
				}
				// numeric type promotion
				else if (item instanceof NumericType) {
					if (expected_aat instanceof XSDouble) {
					  XSDouble doubleType = new XSDouble(item.string_value());
					  result.add(doubleType);
					}
					else {
					  result.add(item);
					}
				} else {
					result.add(item);
				}
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

		assert args.size() <= expected.size();

		Iterator argi = args.iterator();
		Iterator expi = expected.iterator();

		// convert all arguments
		while (argi.hasNext()) {
			result.add(convert_argument(ResultSequenceUtil.newToOld((org.eclipse.wst.xml.xpath2.api.ResultSequence) argi.next()),
					(SeqType) expi.next()));
		}

		return result;
	}

	protected static ResultSequence getResultSetForArityZero(DynamicContext d_context)
			throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();
		
		AnyType contextItem = d_context.context_item();
		if (contextItem != null) {
		  // if context item is defined, then that is the default argument
		  // to fn:string function
		  rs.add(new XSString(contextItem.string_value()));
		} else {
			throw DynamicError.contextUndefined();
		}
		return rs;
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

	public boolean is_vararg() {
		return _min_arity != _max_arity;
	}

	public String getName() {
		return name().local();
	}

	public int getMinArity() {
		return min_arity();
	}

	public int getMaxArity() {
		return max_arity();
	}

	public boolean isVariableArgument() {
		return this.is_vararg();
	}

	public boolean canMatchArity(int actualArity) {
		return matches_arity(actualArity);
	}

	public TypeDefinition getResultType() {
		return BuiltinTypeLibrary.XS_UNTYPED;
	}

	public TypeDefinition getArgumentType(int index) {
		return BuiltinTypeLibrary.XS_UNTYPED;
	}

	public String getArgumentNameHint(int index) {
		return "argument_"  + index;
	}

	public TypeDefinition computeReturnType(Collection args,
			org.eclipse.wst.xml.xpath2.api.StaticContext sc) {
		return BuiltinTypeLibrary.XS_UNTYPED;
	}

	public org.eclipse.wst.xml.xpath2.api.ResultSequence evaluate(Collection args,
			EvaluationContext evaluationContext) {
		
		ResultSequence result = evaluate(args);
		return ResultSequenceUtil.oldToNew(result);
	}

}
