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
 * HTML.
 */
final class HedHEAD extends HTMLElemDeclImpl {

	private static String[] terminators = {CHTMLNamespace.ElementName.HEAD, CHTMLNamespace.ElementName.BODY, CHTMLNamespace.ElementName.HTML};

	/**
	 */
	public HedHEAD(ElementCollection collection) {
		super(CHTMLNamespace.ElementName.HEAD, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_HEAD;
		layoutType = LAYOUT_HIDDEN;
		omitType = OMIT_BOTH;
	}

	/**
	 * Create all attribute declarations.
	 * This method is called once in the constructor of the super class.
	 * The <code>HEAD</code> element may have the following attributes:
	 * <table>
	 * <tbody>
	 *   <tr>
	 *     <th>NAME</th><th>TYPE</th><th>USAGE</th><th>DEFAULT (INITIAL) VALUE</th><th>MEMO</th>
	 *   </tr>
	 *   <tr>
	 *     <td>%i18n;</td><td>-</td><td>-</td><td>-</td><td>-</td>
	 *   </tr>
	 *   <tr>
	 *     <td>profile</td><td>URI</td><td>#IMPLIED</td><td>N/A</td><td>-</td>
	 *   </tr>
	 * </tbody>
	 * </table>
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %i18n;
		attributeCollection.getI18n(attributes);
	}

	/**
	 * HEAD has terminators.
	 * @return java.util.Iterator
	 */
	protected Iterator getTerminators() {
		return Arrays.asList(terminators).iterator();
	}
}
