/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.cleanup;



public interface CSSCleanupStrategy {

	static final short ASIS = 0;
	static final short LOWER = 1;
	static final short UPPER = 2;

	/**
	 * 
	 * @return short
	 */
	short getIdentCase();

	/**
	 * 
	 * @return short
	 */
	short getPropNameCase();

	/**
	 * 
	 * @return short
	 */
	short getPropValueCase();

	/**
	 * 
	 * @return short
	 */
	short getSelectorTagCase();

	short getClassSelectorCase();

	short getIdSelectorCase();

	/**
	 * 
	 * @return boolean
	 */
	boolean isFormatSource();

	/**
	 * 
	 * @return boolean
	 */
	boolean isQuoteValues();

	/**
	 * 
	 * @param formatSource
	 *            boolean
	 */
	void setFormatSource(boolean formatSource);

	/**
	 * 
	 * @param identCase
	 *            short
	 */
	void setIdentCase(short identCase);

	/**
	 * 
	 * @param propNameCase
	 *            short
	 */
	void setPropNameCase(short propNameCase);

	/**
	 * 
	 * @param propValueCase
	 *            short
	 */
	void setPropValueCase(short propValueCase);

	/**
	 * 
	 * @param quoteValues
	 *            boolean
	 */
	void setQuoteValues(boolean quoteValues);

	/**
	 * 
	 * @param selectorTagCase
	 *            short
	 */
	void setSelectorTagCase(short selectorTagCase);

	void setClassSelectorCase(short classSelectorCase);

	void setIdSelectorCase(short idSelectorCase);
}
