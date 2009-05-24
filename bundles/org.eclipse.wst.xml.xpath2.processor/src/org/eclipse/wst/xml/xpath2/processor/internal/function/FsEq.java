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
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;
import java.lang.reflect.*;

/**
 * Class for the Equality function.
 */
public class FsEq extends Function {
	/**
	 * Constructor for FsEq.
	 */
	public FsEq() {
		super(new QName("eq"), 2);
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
		assert args.size() == arity();

		return fs_eq_value(args);
	}

	/**
	 * Converts arguments to values.
	 * 
	 * @param args
	 *            Result from expressions evaluation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of conversion.
	 */
	private static Collection value_convert_args(Collection args)
			throws DynamicError {
		Collection result = new ArrayList(args.size());

		// atomize arguments
		for (Iterator i = args.iterator(); i.hasNext();) {
			ResultSequence rs = (ResultSequence) i.next();

			//FnData.fast_atomize(rs);
			rs = FnData.atomize(rs);

			if (rs.empty())
				return new ArrayList();

			if (rs.size() > 1)
				throw new DynamicError(TypeError.invalid_type(null));

			AnyType arg = rs.first();

			if (arg instanceof UntypedAtomic)
				arg = new XSString(arg.string_value());

			rs = ResultSequenceFactory.create_new();
			rs.add(arg);
			result.add(rs);
		}

		return result;
	}

	/**
	 * Conversion operation for the values of the arguments.
	 * 
	 * @param args
	 *            Result from convert value operation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of conversion.
	 */
	public static ResultSequence fs_eq_value(Collection args)
			throws DynamicError {
		return do_cmp_value_op(args, CmpEq.class, "eq");
	}

	/**
	 * A fast Equality operation, no conversion for the inputs performed.
	 * 
	 * @param one
	 *            input1 of any type.
	 * @param two
	 *            input2 of any type.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of Equality operation.
	 */
	public static boolean fs_eq_fast(AnyType one, AnyType two)
			throws DynamicError {

		one = FnData.atomize(one);
		two = FnData.atomize(two);

		if (one instanceof UntypedAtomic)
			one = new XSString(one.string_value());

		if (two instanceof UntypedAtomic)
			two = new XSString(two.string_value());

		if (!(one instanceof CmpEq))
			DynamicError.throw_type_error();

		CmpEq cmpone = (CmpEq) one;

		return cmpone.eq(two);
	}

	/**
	 * Making sure that the types are the same before comparing the inputs.
	 * 
	 * @param a
	 *            input1 of any type.
	 * @param b
	 *            input2 of any type.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of Equality operation.
	 */
	private static boolean do_general_pair(AnyType a, AnyType b,
			Method comparator) throws DynamicError {

		// section 3.5.2

		// rule a
		// if one is untyped and other is numeric, cast untyped to
		// double
		if ((a instanceof UntypedAtomic && b instanceof NumericType)
				|| (b instanceof UntypedAtomic && a instanceof NumericType)) {
			if (a instanceof UntypedAtomic)
				a = new XSDouble(a.string_value());
			else
				b = new XSDouble(b.string_value());

		}

		// rule b
		// if one is untyped and other is string or untyped, then cast
		// untyped to string
		else if ((a instanceof UntypedAtomic
				&& (b instanceof XSString || b instanceof UntypedAtomic) || (b instanceof UntypedAtomic && (a instanceof XSString || a instanceof UntypedAtomic)))) {

			if (a instanceof UntypedAtomic)
				a = new XSString(a.string_value());
			if (b instanceof UntypedAtomic)
				b = new XSString(a.string_value());
		}

		// rule c
		// if one is untyped and other is not string,untyped,numeric
		// cast untyped to dynamic type of other

		// XXX?
		else if (a instanceof UntypedAtomic) {
			ResultSequence converted = ResultSequenceFactory.create_new(a);
			assert converted.size() == 1;
			a = converted.first();
		} else if (b instanceof UntypedAtomic) {
			ResultSequence converted = ResultSequenceFactory.create_new(b);
			assert converted.size() == 1;
			b = converted.first();
		}

		// rule d
		// if value comparison is true, return true.

		ResultSequence one = ResultSequenceFactory.create_new(a);
		ResultSequence two = ResultSequenceFactory.create_new(b);

		Collection args = new ArrayList();
		args.add(one);
		args.add(two);

		Object margs[] = { args };

		ResultSequence result = null;
		try {
			result = (ResultSequence) comparator.invoke(null, margs);
		} catch (IllegalAccessException err) {
			assert false;
		} catch (InvocationTargetException err) {
			Throwable ex = err.getTargetException();

			if (ex instanceof DynamicError)
				throw (DynamicError) ex;

			ex.printStackTrace();
			System.exit(1);
		}

		if (((XSBoolean) result.first()).value())
			return true;

		return false;
	}

	/**
	 * A general equality function.
	 * 
	 * @param args
	 *            input arguments.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of general equality operation.
	 */
	public static ResultSequence fs_eq_general(Collection args)
			throws DynamicError {
		return do_cmp_general_op(args, FsEq.class, "fs_eq_value");
	}

	// voodoo 3
	/**
	 * Actual equality operation for fs_eq_general.
	 * 
	 * @param args
	 *            input arguments.
	 * @param type
	 *            type of the arguments.
	 * @param mname
	 *            Method name for template simulation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of the operation.
	 */
	public static ResultSequence do_cmp_general_op(Collection args, Class type,
			String mname) throws DynamicError {

		// do the voodoo
		Method comparator = null;

		try {
			Class margsdef[] = { Collection.class };

			comparator = type.getMethod(mname, margsdef);

		} catch (NoSuchMethodException err) {
			assert false;
		}

		// sanity check args and get them
		if (args.size() != 2)
			DynamicError.throw_type_error();

		Iterator argiter = args.iterator();

		ResultSequence one = (ResultSequence) argiter.next();
		ResultSequence two = (ResultSequence) argiter.next();

		// XXX ?
		if (one.empty() || two.empty())
			return ResultSequenceFactory.create_new(new XSBoolean(false));

		// atomize
		one = FnData.atomize(one);
		two = FnData.atomize(two);

		// we gotta find a pair that satisfied the condition
		for (Iterator i = one.iterator(); i.hasNext();) {
			for (Iterator j = two.iterator(); j.hasNext();) {
				AnyType a = (AnyType) i.next();
				AnyType b = (AnyType) j.next();

				if (do_general_pair(a, b, comparator))
					return ResultSequenceFactory
							.create_new(new XSBoolean(true));
			}
		}

		return ResultSequenceFactory.create_new(new XSBoolean(false));
	}

	// voodoo 2
	/**
	 * Actual equality operation for fs_eq_value.
	 * 
	 * @param args
	 *            input arguments.
	 * @param type
	 *            type of the arguments.
	 * @param mname
	 *            Method name for template simulation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of the operation.
	 */
	public static ResultSequence do_cmp_value_op(Collection args, Class type,
			String mname) throws DynamicError {

		// sanity check args + convert em
		if (args.size() != 2)
			DynamicError.throw_type_error();

		Collection cargs = value_convert_args(args);

		ResultSequence result = ResultSequenceFactory.create_new();

		if (cargs.size() == 0)
			return result;

		// make sure arugments are comparable by equality
		Iterator argi = cargs.iterator();
		AnyType arg = ((ResultSequence) argi.next()).first();
		ResultSequence arg2 = (ResultSequence) argi.next();

		if (arg2.size() != 1)
			DynamicError.throw_type_error();

		if (!(type.isInstance(arg)))
			DynamicError.throw_type_error();

		try {
			Class margsdef[] = { AnyType.class };
			Method method = null;

			method = type.getMethod(mname, margsdef);

			Object margs[] = { arg2.first() };
			Boolean cmpres = (Boolean) method.invoke(arg, margs);

			return ResultSequenceFactory.create_new(new XSBoolean(cmpres
					.booleanValue()));
		} catch (NoSuchMethodException err) {
			assert false;
		} catch (IllegalAccessException err) {
			assert false;
		} catch (InvocationTargetException err) {
			Throwable ex = err.getTargetException();

			if (ex instanceof DynamicError)
				throw (DynamicError) ex;

			ex.printStackTrace();
			System.exit(1);
		}
		return null; // unreach!
	}
}
