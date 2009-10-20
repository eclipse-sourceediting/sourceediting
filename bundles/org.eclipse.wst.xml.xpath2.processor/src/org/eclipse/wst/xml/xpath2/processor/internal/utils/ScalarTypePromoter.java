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
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSDayTimeDuration;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSYearMonthDuration;

public class ScalarTypePromoter extends NumericTypePromoter {

	@Override
	protected boolean checkCombination(Class<? extends AnyAtomicType> newType) {

		Class<? extends AnyAtomicType> targetType = getTargetType();
		if (targetType == XSDayTimeDuration.class || targetType == XSYearMonthDuration.class) {
			return targetType == newType;	
		}
		return super.checkCombination(newType);
	}

	@Override
	protected Class<? extends AnyAtomicType> substitute(Class<? extends AnyAtomicType> typeToConsider) {
		if (typeToConsider == XSDayTimeDuration.class || typeToConsider == XSYearMonthDuration.class) {
			return typeToConsider;
		}

		return super.substitute(typeToConsider);
	}
	
}
