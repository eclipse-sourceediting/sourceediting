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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.wst.xml.xpath2.processor.internal.types.AnyType;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSDecimal;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSDouble;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSFloat;
import org.eclipse.wst.xml.xpath2.processor.internal.types.XSInteger;

public class NumericTypePromoter extends TypePromoter {

	@Override
	protected void checkCombination(Class<? extends AnyType> newType) {
		// Note: Double or float will override everything
		if (newType == XSDouble.class || getTargetType() == XSDouble.class) {
			setTargetType(XSDouble.class);
		} else if (newType == XSFloat.class || getTargetType() == XSFloat.class) {
			setTargetType(XSFloat.class);
		// If we're still with integers, stick with it
		} else if (newType == XSInteger.class && getTargetType() == XSInteger.class) {
			setTargetType(XSInteger.class);
		} else {
			// Otherwise, switch to decimals
			setTargetType(XSDecimal.class);
		}
	}

	@Override
	public AnyType promote(AnyType value) {
		if (getTargetType() == XSFloat.class) {
			return new XSFloat(new Float(value.string_value()));
		} else if (getTargetType() == XSDouble.class) {
			return new XSDouble(new Double(value.string_value()));
		} else if (getTargetType() == XSInteger.class) {
			return new XSInteger(new BigInteger(value.string_value()));
		} else if (getTargetType() == XSDecimal.class) {
			return new XSDecimal(new BigDecimal(value.string_value()));
		}
		return null;
	}

	@Override
	protected Class<? extends AnyType> substitute(Class<? extends AnyType> typeToConsider) {
		if (isDerivedFrom(typeToConsider, XSFloat.class)) return XSFloat.class;
		if (isDerivedFrom(typeToConsider, XSDouble.class)) return XSDouble.class;
		if (isDerivedFrom(typeToConsider, XSInteger.class)) return XSInteger.class;
		if (isDerivedFrom(typeToConsider, XSDecimal.class)) return XSDecimal.class;
		return null;
	}

	private boolean isDerivedFrom(Class<? extends AnyType> typeToConsider, Class<? extends AnyType> superType) {
		return superType.isAssignableFrom(typeToConsider);
	}
	
}
