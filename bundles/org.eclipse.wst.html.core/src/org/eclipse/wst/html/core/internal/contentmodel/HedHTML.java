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
package org.eclipse.wst.html.core.internal.contentmodel;



import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;


/**
 * HTML.
 */
final class HedHTML extends HTMLElemDeclImpl {

	private static String[] terminators = {HTML40Namespace.ElementName.HTML};

	/**
	 */
	public HedHTML(ElementCollection collection) {
		super(HTML40Namespace.ElementName.HTML, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_HTML;
		layoutType = LAYOUT_HIDDEN;
		omitType = OMIT_BOTH;
	}

	/**
	 * Create all attribute declarations.
	 * This method is called once in the constructor of the super class.
	 * The <code>HTML</code> element may have the following attributes:
	 * <table>
	 * <tbody>
	 *   <tr>
	 *     <th>NAME</th><th>TYPE</th><th>USAGE</th><th>DEFAULT (INITIAL) VALUE</th><th>MEMO</th>
	 *   </tr>
	 *   <tr>
	 *     <td><code>%i18n;</code></td><td>-</td><td>-</td><td>-</td>
	 *     <td>{@link PDCMDocImpl#getAttrDeclarationsI18n}</td>
	 *   </tr>
	 *   <tr>
	 *     <td>version</td><td>CDATA</td><td>#FIXED</td>
	 *     <td>{@link HTML_VERSION_TRANSITIONAL}</td><td>deplecated in HTML4.01</td>
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

		//different sets of attributes for html 4 & 5
		attributeCollection.createAttributeDeclarations(HTML40Namespace.ElementName.HTML, attributes);
	
		}

	/**
	 * HTML has terminators.
	 * @return java.util.Iterator
	 */
	protected Iterator getTerminators() {
		return Arrays.asList(terminators).iterator();
	}
}
