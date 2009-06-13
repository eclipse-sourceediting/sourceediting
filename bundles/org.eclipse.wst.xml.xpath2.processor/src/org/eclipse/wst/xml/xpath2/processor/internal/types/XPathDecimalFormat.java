/*******************************************************************************
 * Copyright (c) 2009 Standards for Technology in Automotive Retail and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    David Carver - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.FieldPosition;

/**
 * This is an XPath specific implementation of DecimalFormat to handle
 * some of the xpath specific formatting requirements.   Specifically
 * it allows for E# to be represented to indicate that the exponent value
 * is optional.  Otherwise all existing DecimalFormat patterns are handled
 * as is.
 * @author dcarver
 * @see 1.1
 *
 */
public class XPathDecimalFormat extends DecimalFormat {
	
	public XPathDecimalFormat(String pattern) {
		super(pattern);
	}

	/**
	 * Formats the string dropping a Zero Exponent Value if it exists.
	 * @param obj
	 * @return
	 */
	public String formatDropZeroExp(Object obj) {
		String curPattern = toPattern();
		if (obj instanceof Float) {
            Float floatValue = (Float) obj;
			if (floatValue > 1E-6f && floatValue < 1E6f) {
				applyPattern(curPattern.replace("E0", ""));
			}
		}
		if (obj instanceof Double) {
			Double doubleValue = (Double) obj;
			if (doubleValue > 1E-6d && doubleValue < 1E6d) {
				applyPattern(curPattern.replace("E0", ""));
			}
		}
		String formatted = super.format(obj, new StringBuffer(), new FieldPosition(0)).toString();
		return formatted;
	}
	
	
}
