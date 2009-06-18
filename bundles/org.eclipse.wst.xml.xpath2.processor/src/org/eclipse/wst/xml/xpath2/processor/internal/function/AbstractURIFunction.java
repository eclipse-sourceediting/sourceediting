/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - bug 262765 - renamed to correct function name. 
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.SeqType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.QName;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSString;

public abstract class AbstractURIFunction extends Function {

	static Collection _expected_args = null;

	protected static boolean needs_escape(char x, boolean er) {
		if ('A' <= x && x <= 'Z')
			return false;
		if ('a' <= x && x <= 'z')
			return false;
		if ('0' <= x && x <= '9')
			return false;
	
		switch (x) {
		case '-':
		case '_':
		case '.':
		case '!':
		case '~':
		case '*':
		case '\'':
		case '(':
		case ')':
		case '#':
		case '%':
			return false;
	
		case ';':
		case '/':
		case '?':
		case ':':
		case '@':
		case '&':
		case '=':
		case '+':
		case '$':
		case ',':
		case '[':
		case ']':
		case ' ':
			if (!er)
				return false;
		default:
			return true;
		}
	}

	/**
	 * Apply the URI escaping rules to the arguments.
	 * 
	 * @param args
	 *            have the URI escaping rules applied to them.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return The result of applying the URI escaping rules to the arguments.
	 */
	public static ResultSequence escape_uri(Collection args, boolean escape) throws DynamicError {
		Collection cargs = Function.convert_arguments(args, expected_args());
	
		Iterator argi = cargs.iterator();
		ResultSequence arg1 = (ResultSequence) argi.next();
	
		ResultSequence rs = ResultSequenceFactory.create_new();
	
		if (arg1.empty()) {
			rs.add(new XSString(""));
			return rs;
		}
				
		AnyType aat = (AnyType) arg1.first();
		String str = aat.string_value();
	
		StringBuffer sb = new StringBuffer();
	
		for (int i = 0; i < str.length(); i++) {
			char x = str.charAt(i);
	
			if (needs_escape(x, escape)) {
				sb.append("%");
				sb.append(Integer.toHexString(x).toUpperCase());
			} else
				sb.append(x);
		}
	
		rs.add(new XSString(sb.toString()));
	
		return rs;
	}

	/**
	 * Calculate the expected arguments.
	 * 
	 * @return The expected arguments.
	 */
	public static Collection expected_args() {
		if (_expected_args == null) {
			_expected_args = new ArrayList();
			_expected_args.add(new SeqType(new XSString(), SeqType.OCC_QMARK));
		}
	
		return _expected_args;
	}

	public AbstractURIFunction(QName name, int arity) {
		super(name, arity);
	}

	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		// TODO Auto-generated method stub
		return null;
	}

}
