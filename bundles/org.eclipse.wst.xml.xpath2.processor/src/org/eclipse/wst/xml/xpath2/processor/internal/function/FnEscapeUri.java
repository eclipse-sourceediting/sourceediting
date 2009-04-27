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

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

/**
 * <p>
 * Function to apply URI escaping rules.
 * </p>
 * 
 * <p>
 * Usage: fn:escape-uri($uri-part as xs:string?, $escape-reserved as xs:boolean)
 * as xs:string
 * </p>
 * 
 * <p>
 * This class applies the URI escaping rules (with one exception), to the string
 * supplied as $uri-part, which typically represents all or part of a URI. The
 * effect of the function is to escape a set of identified characters in the
 * string. Each such character is replaced in the string by an escape sequence,
 * which is formed by encoding the character as a sequence of octets in UTF-8,
 * and then representing each of these octets in the form %HH, where HH is the
 * hexadecimal representation of the octet.
 * </p>
 * 
 * <p>
 * The set of characters that are escaped depends on the setting of the boolean
 * argument $escape-reserved.
 * </p>
 * 
 * <p>
 * If $uri-part is the empty sequence, returns the zero-length string.
 * </p>
 * 
 * <p>
 * If $escape-reserved is true, all characters are escaped other than the lower
 * case letters a-z, the upper case letters A-Z, the digits 0-9, the PERCENT
 * SIGN "%" and the NUMBER SIGN "#", as well as certain other characters:
 * specifically, HYPHEN-MINUS ("-"), LOW LINE ("_"), FULL STOP ".", EXCLAMATION
 * MARK "!", TILDE "~", ASTERISK "*", APOSTROPHE "'", LEFT PARENTHESIS "(", and
 * RIGHT PARENTHESIS ")".
 * </p>
 * 
 * <p>
 * If $escape-reserved is false, additional characters are added to the list of
 * characters that are not escaped. These are the following: SEMICOLON ";",
 * SOLIDUS "/", QUESTION MARK "?", COLON ":", COMMERCIAL AT "@", AMPERSAND "&",
 * EQUALS SIGN "=", PLUS SIGN "+", DOLLAR SIGN "$", COMMA ",", LEFT SQUARE
 * BRACKET "[" and RIGHT SQUARE BRACKET "]".
 * </p>
 * 
 * <p>
 * To ensure that escaped URIs can be compared using string comparison
 * functions, this function must always generate hexadecimal values using the
 * upper-case letters A-F.
 * </p>
 * 
 * <p>
 * Generally, $escape-reserved should be set to true when escaping a string that
 * is to form a single part of a URI, and to false when escaping an entire URI
 * or URI reference.
 * </p>
 * 
 * <p>
 * Since this function does not escape the PERCENT SIGN "%" and this character
 * is not allowed in a URI, users wishing to convert character strings, such as
 * file names, that include "%" to a URI should manually escape "%" by replacing
 * it with "%25".
 * </p>
 */
public class FnEscapeUri extends Function {
	private static Collection _expected_args = null;

	/**
	 * Constructor for FnEscapeUri.
	 */
	public FnEscapeUri() {
		super(new QName("escape-uri"), 2);
	}

	/**
	 * Evaluate the arguments.
	 * 
	 * @param args
	 *            are evaluated.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return The evaluation of the arguments after application of the URI
	 *         escaping rules.
	 */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return escape_uri(args);
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
	public static ResultSequence escape_uri(Collection args)
			throws DynamicError {
		Collection cargs = Function.convert_arguments(args, expected_args());

		Iterator argi = cargs.iterator();
		ResultSequence arg1 = (ResultSequence) argi.next();
		ResultSequence arg2 = (ResultSequence) argi.next();

		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg1.empty()) {
			rs.add(new XSString(""));
			return rs;
		}

		String str = ((XSString) arg1.first()).value();
		boolean escape_reserved = ((XSBoolean) arg2.first()).value();

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < str.length(); i++) {
			char x = str.charAt(i);

			if (needs_escape(x, escape_reserved)) {
				sb.append("%");
				sb.append(Integer.toHexString(x));
			} else
				sb.append(x);
		}

		rs.add(new XSString(sb.toString()));

		return rs;
	}

	private static boolean needs_escape(char x, boolean er) {
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
			if (!er)
				return false;
		default:
			return true;
		}
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
			_expected_args.add(new SeqType(new XSBoolean(), SeqType.OCC_NONE));
		}

		return _expected_args;
	}
}
