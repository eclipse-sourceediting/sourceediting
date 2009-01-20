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
/**
 * Support for Mathematical Subtraction.
 */
public interface MathMinus {
	/**
         * Subtraction operation.
         * @param arg input argument.
         * @throws DynamicError Dynamic error.
         * @return Result of operation.
         */
	public ResultSequence minus(ResultSequence arg) throws DynamicError;
}
