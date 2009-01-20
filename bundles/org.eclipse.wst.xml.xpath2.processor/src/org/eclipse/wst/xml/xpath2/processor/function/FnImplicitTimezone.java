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
import org.eclipse.wst.xml.xpath2.processor.types.*;

import java.util.*;
/**
 * Returns the value of the implicit timezone property from the dynamic context.
 * Components of the dynamic context are discussed in Section C.2 Dynamic Context Components
 */
public class FnImplicitTimezone extends Function {
	/**
	 * Constructor for FnImplicitTimezone.
	 */	
	public FnImplicitTimezone() {
		super(new QName("implicit-timezone"), 0);
	}
	/**
         * Evaluate arguments.
         * @param args argument expressions.
         * @throws DynamicError Dynamic error.
         * @return Result of evaluation.
         */
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return implicit_timezone(args, dynamic_context());
	}
	/**
         * Implicit-Timezone operation.
         * @param args Result from the expressions evaluation.
	 * @param dc Result of dynamic context operation.
         * @throws DynamicError Dynamic error.
         * @return Result of fn:implicit-timezone operation.
         */
	public static ResultSequence implicit_timezone(Collection args, DynamicContext dc) throws DynamicError {
		assert args.size() == 0;

		try {
			AnyType res = (XDTDayTimeDuration) dc.tz().clone();

			return ResultSequenceFactory.create_new(res);
		} catch(CloneNotSupportedException err) {
			assert false;
			return null;
		}
	}
}
