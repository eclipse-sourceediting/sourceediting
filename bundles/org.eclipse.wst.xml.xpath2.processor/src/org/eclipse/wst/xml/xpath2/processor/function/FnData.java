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

package org.eclipse.wst.xml.xpath2.processor.function;

import org.eclipse.wst.xml.xpath2.processor.*;
import org.eclipse.wst.xml.xpath2.processor.types.*;

import java.util.*;

/**
 * fn:data takes a sequence of items and returns a sequence of atomic values.
 * The result of fn:data is the sequence of atomic values produced by applying
 * the following rules to each item in $arg: - If the item is an atomic value,
 * it is returned. - If the item is a node, fn:data() returns the typed value of
 * the node as defined by the accessor function dm:typed-value in Section 5.6
 * typed-value Accessor in the specification.
 */
public class FnData extends Function {
	/**
	 * Constructor for FnData.
	 */
	public FnData() {
		super(new QName("data"), 1);
	}

	/**
	 * Evaluate arguments.
	 * 
	 * @param args
	 *            argument expressions.
	 * @return Result of evaluation.
	 */
	@Override
	public ResultSequence evaluate(Collection args) {
		// 1 argument only!
		assert args.size() == arity();

		ResultSequence argument = (ResultSequence) args.iterator().next();

		return atomize(argument);
	}

	/**
	 * Atomize a ResultSequnce argument expression.
	 * 
	 * @param arg
	 *            input expression.
	 * @return Result of operation.
	 */
	public static ResultSequence atomize(ResultSequence arg) {

		ResultSequence rs = ResultSequenceFactory.create_new();

		for (Iterator i = arg.iterator(); i.hasNext();) {
			AnyType at = (AnyType) i.next();

			if (at instanceof AnyAtomicType)
				rs.add(at);
			else if (at instanceof NodeType) {
				NodeType nt = (NodeType) at;

				rs.concat(nt.typed_value());
			} else
				assert false;
		}

		return rs;
	}

	/**
	 * Atomize a ResultSequnce argument expression.
	 * 
	 * @param arg
	 *            input expression.
	 */
	public static void fast_atomize(ResultSequence arg) {
		for (ListIterator i = arg.iterator(); i.hasNext();) {
			AnyType at = (AnyType) i.next();

			if (at instanceof AnyAtomicType) {
				continue;
			}

			// XXX prolly wrong!
			else if (at instanceof NodeType) {
				NodeType nt = (NodeType) at;

				i.set(nt.typed_value().first());
			} else
				assert false;
		}
	}

	/**
	 * Atomize argument expression of any type.
	 * 
	 * @param arg
	 *            input expression.
	 * @return Result of operation.
	 */
	public static AnyType atomize(AnyType arg) {
		if (arg instanceof AnyAtomicType)
			return arg;
		else if (arg instanceof NodeType) {
			NodeType nt = (NodeType) arg;

			return nt.typed_value().first();
		} else {
			assert false;
			return null;
		}
	}
}
