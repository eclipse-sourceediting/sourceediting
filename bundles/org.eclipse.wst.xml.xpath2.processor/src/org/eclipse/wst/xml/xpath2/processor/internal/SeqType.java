/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0 
 *     Jesper Steen Moeller - bug 285145 - don't silently allow empty sequences always
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.StaticContext;
import org.eclipse.wst.xml.xpath2.processor.internal.ast.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

// ok gotta figure out what to do with this, and the one inf the AST

/**
 * represents a Sequence types used for matching expected arguments of functions
 */
public class SeqType {

	public static final int OCC_NONE = 0;
	public static final int OCC_STAR = 1;
	public static final int OCC_PLUS = 2;
	public static final int OCC_QMARK = 3;

	private AnyType _type;
	private int _occ;
	private Class _type_class;

	/**
	 * sequence type
	 * 
	 * @param t
	 *            is any type
	 * @param occ
	 *            is an integer in the sequence.
	 */
	public SeqType(AnyType t, int occ) {
		_type = t;
		_occ = occ;

		if (t != null)
			_type_class = t.getClass();
		else
			_type_class = null;
	}

	/**
	 * @param occ
	 *            is an integer in the sequence.
	 */
	// XXX hack to represent AnyNode...
	public SeqType(int occ) {
		this((AnyType)null, occ);

		_type_class = NodeType.class;
	}

	/**
	 * @param type_class
	 *            is a class which represents the expected type
	 * @param occ
	 *            is an integer in the sequence.
	 */
	public SeqType(Class type_class, int occ) {
		this((AnyType)null, occ);

		_type_class = type_class;
	}

	/**
	 * @param st
	 *            is a sequence type.
	 * @param sc
	 *            is a static context.
	 */
	// XXX hack 2
	public SeqType(SequenceType st, StaticContext sc) {

		_type = null;
		_type_class = null;

		// convert occurrence
		switch (st.occurrence()) {
		case SequenceType.EMPTY:
			_occ = OCC_NONE;
			return;

		case SequenceType.NONE:
			_occ = OCC_NONE;
			break;

		case SequenceType.QUESTION:
			_occ = OCC_QMARK;
			break;

		case SequenceType.STAR:
			_occ = OCC_STAR;
			break;

		case SequenceType.PLUS:
			_occ = OCC_PLUS;
			break;

		default:
			assert false;
		}

		// figure out the item is
		ItemType item = st.item_type();
		KindTest ktest = null;
		switch (item.type()) {
		case ItemType.ITEM:
			_type_class = AnyType.class;
			return;

			// XXX IMPLEMENT THIS
		case ItemType.QNAME:
			AnyAtomicType aat = sc.make_atomic(item.qname());

			assert aat != null;
			_type = aat;
			_type_class = _type.getClass();
			return;

		case ItemType.KINDTEST:
			ktest = item.kind_test();
			break;

		}

		if (ktest == null)
			return;

		if (ktest instanceof DocumentTest)
			_type_class = DocType.class;
		else if (ktest instanceof ElementTest)
			_type_class = ElementType.class;
		else
			assert false;
	}

	/**
	 * @param t
	 *            is an any type.
	 */
	public SeqType(AnyType t) {
		this(t, OCC_NONE);
	}

	/**
	 * @return an integer.
	 */
	public int occurence() {
		return _occ;
	}

	/**
	 * @return a type.
	 */
	public AnyType type() {
		return _type;
	}

	/**
	 * matches args
	 * 
	 * @param args
	 *            is a result sequence
	 * @throws a
	 *             dynamic error
	 * @return a result sequence
	 */
	public ResultSequence match(ResultSequence args) throws DynamicError {
		
		int arg_count = 0;

		for (Iterator i = args.iterator(); i.hasNext();) {
			AnyType arg = (AnyType) i.next();

			// make sure all args are the same type as expected type
			if (!(_type_class.isInstance(arg)))
				throw new DynamicError(TypeError.invalid_type(null));

			arg_count++;

		}

		switch (occurence()) {
		case OCC_NONE:
			if (arg_count != 1)
				throw new DynamicError(TypeError.invalid_type(null));
			break;

		case OCC_PLUS:
			if (arg_count == 0)
				throw new DynamicError(TypeError.invalid_type(null));
			break;

		case OCC_STAR:
			break;

		case OCC_QMARK:
			if (arg_count > 1)
				throw new DynamicError(TypeError.invalid_type(null));
			break;

		default:
			assert false;

		}

		return args;
	}
}
