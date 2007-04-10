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
 * BODY.
 */
final class HedBODY extends HedFlowContainer {

	private static String[] terminators = {CHTMLNamespace.ElementName.HEAD, CHTMLNamespace.ElementName.BODY, CHTMLNamespace.ElementName.HTML};

	/**
	 */
	public HedBODY(ElementCollection collection) {
		super(CHTMLNamespace.ElementName.BODY, collection);
		layoutType = LAYOUT_BLOCK;
		omitType = OMIT_BOTH;
	}

	/**
	 * %attrs;
	 * %bodycolors;
	 * (onload %Script; #IMPLIED)
	 * (onunload %Script; #IMPLIED)
	 * (background %URI; #IMPLIED)
	 * (marginwidth %Pixels; #IMPLIED) ... D205514
	 * (marginheight %Pixels; #IMPLIED) .. D205514
	 * (topmargin, CDATA, #IMPLIED) ...... D205514
	 * (bottommargin, CDATA, #IMPLIED) ... D205514
	 * (leftmargin, CDATA, #IMPLIED) ..... D205514
	 * (rightmargin, CDATA, #IMPLIED) .... D205514
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);
		// %bodycolors;
		attributeCollection.getBodycolors(attributes);

	}

	/**
	 * BODY has terminators.
	 * @return java.util.Iterator
	 */
	protected Iterator getTerminators() {
		return Arrays.asList(terminators).iterator();
	}
}
