/*******************************************************************************
 * Copyright (c) 2005, 2009 Jesper Steen Moeller, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jesper Steen Moeller - bug 285152 - implement fn:normalize-unicocde
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.SeqType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import com.ibm.icu.text.Normalizer;

/**
 * <p>
 * Function to normalize unicode.
 * </p>
 * 
 * <p>
 * Usage: fn:normalize-unicode($arg as xs:string?) as xs:string
 * or fn:normalize-unicode($arg as xs:string?, $normalization-form as xs:string) as xs:string
 * </p>
 * 
 * <p>
 * This function returns the normalized value of the first argument in the normalization form specified by the second argument.
 * </p>
 * 
 */
public class FnNormalizeUnicode extends Function {
	private static Collection _expected_args = null;

	/**
	 * Constructor for FnNormalizeUnicode
	 */
	public FnNormalizeUnicode() {
		super(new QName("normalize-unicode"), 1, 2);
	}

	public interface W3CNormalizer {
		String normalize(String argument, String normalizationForm) throws DynamicError;
	};
	
	static class ICUNormalizer implements W3CNormalizer {
		
		public String normalize(String argument, String normalizationForm)
				throws DynamicError {
			if (normalizationForm.equals("NFC")) {
				return Normalizer.normalize(argument, Normalizer.NFC);
			} else if (normalizationForm.equals("NFD")) {
				return Normalizer.normalize(argument, Normalizer.NFD);
			} else if (normalizationForm.equals("NFKC")) {
				return Normalizer.normalize(argument, Normalizer.NFKC);
			} else if (normalizationForm.equals("NFKD")) {
				return Normalizer.normalize(argument, Normalizer.NFKD);
//			} else if (normalizationForm.equals("FULLY-NORMALIZED")) {
//				String normalized_but_possibly_partial = Normalizer.normalize(argument, Normalizer.NFC);
				// XXX: Check to see that the string starts with a modifier, and, if required, neutralize it to its composed equivalent
//				return normalized_but_possibly_partial;
			} else {
				throw DynamicError.unsupported_normalization_form(normalizationForm);
			}
		}
	}
	
	/**
	 * Evaluate the arguments.
	 * 
	 * @param args
	 *            are evaluated.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return The evaluation of the space in the arguments being normalized.
	 */
	@Override
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return normalize_unicode(args, dynamic_context());
	}

	/**
	 * Normalize unicode codepoints in the argument.
	 * 
	 * @param args
	 *            are used to obtain the input string and optionally the normalization type from.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return The result of normalizing the space in the arguments.
	 */
	public static ResultSequence normalize_unicode(Collection args, DynamicContext d_context)
			throws DynamicError {
		assert args.size() >= 1 && args.size() <= 2;

		Collection cargs = Function.convert_arguments(args, expected_args());
		
		Iterator cargsIterator = cargs.iterator();
		ResultSequence arg1 = (ResultSequence) cargsIterator.next();


		String normalizationType = "NFC";
		if (cargsIterator.hasNext()) {
			ResultSequence arg2 = (ResultSequence)cargsIterator.next();
			// Trim and convert to upper as per the spec
			if (arg2.empty()) {
				normalizationType = "";
			} else {
				normalizationType = ((XSString) arg2.first()).value().trim().toUpperCase();
			}
		}
		
		String argument = "";
		if (! arg1.empty()) argument = ((XSString) arg1.first()).value();
				
		ResultSequence rs = ResultSequenceFactory.create_new();
		String normalized = normalizationType.equals("") ? argument : getNormalizer().normalize(argument, normalizationType);
		rs.add(new XSString(normalized));
		
		return rs;
	}

	private static W3CNormalizer getNormalizer() {
		return new ICUNormalizer();
	}

	/**
	 * Calculate the expected arguments.
	 * 
	 * @return The expected arguments.
	 */
	public synchronized static Collection expected_args() {
		if (_expected_args == null) {
			_expected_args = new ArrayList();
			_expected_args.add(new SeqType(new XSString(), SeqType.OCC_QMARK));
			_expected_args.add(new SeqType(new XSString(), SeqType.OCC_NONE));
		}

		return _expected_args;
	}
}
