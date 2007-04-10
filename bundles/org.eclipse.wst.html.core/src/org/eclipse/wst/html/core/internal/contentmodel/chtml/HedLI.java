/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel.chtml;



import java.util.Arrays;
import java.util.Iterator;

/**
 * LI.
 */
final class HedLI extends HedFlowContainer {

	private static String[] terminators = {CHTMLNamespace.ElementName.LI};

	/**
	 */
	public HedLI(ElementCollection collection) {
		super(CHTMLNamespace.ElementName.LI, collection);
		layoutType = LAYOUT_BLOCK;
		omitType = OMIT_END_DEFAULT;
	}

	/**
	 * %attrs;
	 * (type %LIStyle; #IMPLIED) ... should be defined locally.
	 * (value NUMBER #IMPLIED) ... should be defined locally.
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

	}

	/**
	 * LI has terminators.
	 * @return java.util.Iterator
	 */
	protected Iterator getTerminators() {
		return Arrays.asList(terminators).iterator();
	}
}
