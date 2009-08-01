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

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;
import org.eclipse.wst.xml.xpath2.processor.internal.utils.SurrogateUtils;

import java.util.*;

/**
 * <p>
 * Translation function.
 * </p>
 * 
 * <p>
 * Usage: fn:translate($arg as xs:string?, $mapString as xs:string, $transString
 * as xs:string) as xs:string
 * </p>
 * 
 * <p>
 * This class returns the value of $arg modified so that every character in the
 * value of $arg that occurs at some position N in the value of $mapString has
 * been replaced by the character that occurs at position N in the value of
 * $transString.
 * </p>
 * 
 * <p>
 * If the value of $arg is the empty sequence, the zero-length string is
 * returned.
 * </p>
 * 
 * <p>
 * Every character in the value of $arg that does not appear in the value of
 * $mapString is unchanged.
 * </p>
 * 
 * <p>
 * Every character in the value of $arg that appears at some position M in the
 * value of $mapString, where the value of $transString is less than M
 * characters in length, is omitted from the returned value. If $mapString is
 * the zero-length string $arg is returned.
 * </p>
 * 
 * <p>
 * If a character occurs more than once in $mapString, then the first occurrence
 * determines the replacement character. If $transString is longer than
 * $mapString, the excess characters are ignored.
 * </p>
 */
public class FnTranslate extends Function {
	private static Collection _expected_args = null;

	/**
	 * Constructor for FnTranslate.
	 */
	public FnTranslate() {
		super(new QName("translate"), 3);
	}

	/**
	 * Evaluate the arguments.
	 * 
	 * @param args
	 *            are evaluated.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return The evaluation of the arguments being translated.
	 */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return translate(args);
	}

	/**
	 * Translate arguments.
	 * 
	 * @param args
	 *            are translated.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return The result of translating the arguments.
	 */
	public static ResultSequence translate(Collection args) throws DynamicError {
		Collection cargs = Function.convert_arguments(args, expected_args());

		Iterator argi = cargs.iterator();
		ResultSequence arg1 = (ResultSequence) argi.next();
		ResultSequence arg2 = (ResultSequence) argi.next();
		ResultSequence arg3 = (ResultSequence) argi.next();

		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg1.empty()) {
			rs.add(new XSString(""));
			return rs;
		}

		String str = ((XSString) arg1.first()).value();
		str = SurrogateUtils.decodeXML(str);
		String mapstr = ((XSString) arg2.first()).value();
		mapstr = SurrogateUtils.decodeXML(mapstr);
		String transstr = ((XSString) arg3.first()).value();
		transstr = SurrogateUtils.decodeXML(transstr);

		String result = new String(str);

		// ok the spec says that first occurence decides how to change
		// it... 
		Map repmap = new Hashtable(256);
		for (int i = 0; i < mapstr.length(); i++) {
			String replace = "";
			String chartofind = "" + mapstr.charAt(i);

			if (transstr.length() > i)
				replace += transstr.charAt(i);

			if (repmap.containsKey(chartofind))
				replace = (String) repmap.get(chartofind);

			else
				repmap.put(chartofind, replace);

			result = result.replaceAll(chartofind, replace);
		}
		
		result = StringEscapeUtils.escapeXml(result);
		rs.add(new XSString(result));

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
			_expected_args.add(new SeqType(new XSString(), SeqType.OCC_NONE));
			_expected_args.add(new SeqType(new XSString(), SeqType.OCC_NONE));
		}

		return _expected_args;
	}
}
