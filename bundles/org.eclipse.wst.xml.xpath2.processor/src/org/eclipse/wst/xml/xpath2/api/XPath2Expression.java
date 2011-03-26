/*******************************************************************************
 * Copyright (c) 2011 Jesper Moller, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jesper Moller - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.api;

import java.util.Collection;

/**
 * This interface represents a parsed and statically bound XPath2 expression.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface XPath2Expression {
	
	/**
	 * Return a collections of QNames of the names of free variables referenced in the XPath expression.
	 * These variables may be requested during evaluation.
	 *  
	 * @return A Collection containing javax.xml.namespacing.QName of free variables
	 */
	Collection/*<QName>*/ getFreeVariables();

	/**
	 * Return a collections of the functions used in the XPath2 expression.
	 *  
	 * @return A Collection containing javax.xml.namespacing.QName of functions in use.
	 */
	Collection/*<QName>*/ getResolvedFunctions();

	/**
	 * Return a collections of the functions used in the XPath2 expression.
	 *  
	 * @return A Collection containing javax.xml.namespacing.QName of functions in use.
	 */
	Collection/*<String>*/ getAxes();

	/**
	 * Evaluate the XPath2 expression, using the supplied DynamicContext.
	 * 
	 * @param dynamicContext Dynamic context for the expression.
	 * @param contextItems Context item (typically nodes, often one) to evaluate under.
	 * @return A ResultSequence 
	 */
	ResultSequence evaluate(DynamicContext dynamicContext, Object[] contextItems);
}
