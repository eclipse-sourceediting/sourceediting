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
 * Base class for PCDATA type element declarations.<br>
 */
abstract class HedPcdata extends HTMLElemDeclImpl {

	/**
	 */
	public HedPcdata(String elementName, ElementCollection collection) {
		super(elementName, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_PCDATA;
	}

	/**
	 * Content.<br>
	 * PCDATA type always returns <code>null</code>.
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
		return CMElementDeclaration.PCDATA;
	}
}