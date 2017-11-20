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

import org.eclipse.wst.xml.xpath2.api.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;

/**
 * Support for Mathematical Subtraction.
 */
public interface MathMinus {
	/**
	 * Subtraction operation.
	 * 
	 * @param arg
	 *            input argument.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of operation.
	 */
	public ResultSequence minus(ResultSequence arg) throws DynamicError;
}
