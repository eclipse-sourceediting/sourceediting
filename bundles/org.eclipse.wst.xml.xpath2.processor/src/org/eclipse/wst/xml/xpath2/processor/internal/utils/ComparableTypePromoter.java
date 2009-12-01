/*******************************************************************************
 * Copyright (c) 2009 Jesper Steen Moller, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jesper Steen Moller - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.utils;

import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyAtomicType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSAnyURI;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSDate;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSDateTime;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSString;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSTime;

public class ComparableTypePromoter extends ScalarTypePromoter {

	@Override
	protected boolean checkCombination(Class<? extends AnyAtomicType> newType) {

		Class<? extends AnyAtomicType> targetType = getTargetType();
		if (newType == XSString.class || newType == XSTime.class || targetType == XSString.class || targetType == XSTime.class) {
			return targetType == newType;	
		}
		if (newType == XSDate.class && targetType == XSDateTime.class) return true; // leave alone
		if (newType == XSDateTime.class && targetType != XSDateTime.class) {
			if (targetType == XSDate.class) {
				setTargetType(XSDateTime.class);
			} else return false;
		}

		return super.checkCombination(newType);
	}

	@Override
	protected Class<? extends AnyAtomicType> substitute(Class<? extends AnyAtomicType> typeToConsider) {
		if (typeToConsider == XSAnyURI.class || typeToConsider == XSString.class) {
			return XSString.class;
		}
		if (typeToConsider == XSDateTime.class || typeToConsider == XSDate.class || typeToConsider == XSTime.class) {
			return typeToConsider;
		}

		return super.substitute(typeToConsider);
	}
	
}
