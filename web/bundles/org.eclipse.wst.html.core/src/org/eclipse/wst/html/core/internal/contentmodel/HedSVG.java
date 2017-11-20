/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;



import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.wst.html.core.internal.provisional.HTML50Namespace;


/**
 * SVG.
 */
final class HedSVG extends HTMLElemDeclImpl {

	private static String[] terminators = {HTML50Namespace.ElementName.SVG};

	/**
	 */
	public HedSVG(ElementCollection collection) {
		super(HTML50Namespace.ElementName.SVG, collection);
		layoutType = LAYOUT_OBJECT;
	}

	/**
	 * Create all attribute declarations.
	 * SVG namespace
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		attributeCollection.getAttrs(attributes);
	}

	/**
	 * SVG has terminators.
	 * @return java.util.Iterator
	 */
	protected Iterator getTerminators() {
		return Arrays.asList(terminators).iterator();
	}
}
