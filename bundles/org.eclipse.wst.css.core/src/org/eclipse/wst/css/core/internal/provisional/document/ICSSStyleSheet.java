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
package org.eclipse.wst.css.core.internal.provisional.document;



import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.stylesheets.StyleSheetList;

/**
 * 
 */
public interface ICSSStyleSheet extends ICSSDocument, ICSSRuleContainer, CSSStyleSheet {

	/**
	 * @return org.w3c.dom.NodeList
	 */
	NodeList getOwnerNodes();

	/**
	 * @return org.w3c.dom.NodeList
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	NodeList getOwnerNodes(Document doc);

	/**
	 * @return org.w3c.dom.css.CSSRuleList
	 */
	CSSRuleList getOwnerRules();

	/**
	 * @return org.w3c.dom.stylesheets.StyleSheetList
	 */
	StyleSheetList getParentStyleSheets();
}
