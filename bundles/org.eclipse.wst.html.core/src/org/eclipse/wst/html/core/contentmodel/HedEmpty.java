/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.contentmodel;



import org.eclipse.wst.sse.core.internal.contentmodel.CMContent;
import org.eclipse.wst.sse.core.internal.contentmodel.CMElementDeclaration;

/**
 * Base class for EMPTY type element declarations.
 */
abstract class HedEmpty extends HTMLElemDeclImpl {

	/**
	 */
	public HedEmpty(String elementName, ElementCollection collection) {
		super(elementName, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_EMPTY;
		// EMPTY type has no end tag.
		omitType = OMIT_END_MUST;
	}

	/**
	 * Content.<br>
	 * EMPTY type always returns <code>null</code>.
	 * <br>
	 * @return org.eclipse.wst.sse.core.internal.contentmodel.CMContent
	 */
	public CMContent getContent() {
		return null;
	}

	/**
	 * Content type.<br>
	 * @see org.eclipse.wst.sse.core.internal.contentmodel.CMElementDeclaration
	 */
	public int getContentType() {
		return CMElementDeclaration.EMPTY;
	}
}