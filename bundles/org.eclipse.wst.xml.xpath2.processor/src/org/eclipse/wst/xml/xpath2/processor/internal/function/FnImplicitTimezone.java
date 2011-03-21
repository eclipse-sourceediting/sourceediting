/*******************************************************************************
 * Copyright (c) 2005, 2010 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi - bug 273760 - wrong namespace for functions and data types
 *     David Carver - bug 282223 - implementation of xs:duration 
 *     Mukul Gandhi - bug 280798 - PsychoPath support for JDK 1.4
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.function;

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.types.*;

import java.util.*;

/**
 * Returns the value of the implicit timezone property from the dynamic context.
 * Components of the dynamic context are discussed in Section C.2 Dynamic
 * Context Components
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
	 * 
	 * @param args
	 *            argument expressions.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of evaluation.
	 */
	public ResultSequence evaluate(Collection args) throws DynamicError {
		return implicit_timezone(args, dynamic_context());
	}

	/**
	 * Implicit-Timezone operation.
	 * 
	 * @param args
	 *            Result from the expressions evaluation.
	 * @param dc
	 *            Result of dynamic context operation.
	 * @throws DynamicError
	 *             Dynamic error.
	 * @return Result of fn:implicit-timezone operation.
	 */
	public static ResultSequence implicit_timezone(Collection args,
			DynamicContext dc) throws DynamicError {
		assert args.size() == 0;

		try {
			AnyType res = (XSDuration) dc.tz().clone();

			return ResultSequenceFactory.create_new(res);
		} catch (CloneNotSupportedException err) {
			assert false;
			return null;
		}
	}
}
