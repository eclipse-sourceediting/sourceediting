/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.document;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.parserz.CSSTextParser;
import org.eclipse.wst.css.core.internal.parserz.CSSTextToken;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelector;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorList;
import org.w3c.dom.Element;


/**
 * 
 */
public class CSSSelectorListImpl implements ICSSSelectorList {

	private String fText = null;
	private String fCachedString = null; // normalized string
	private List fSelectors = null;

	public CSSSelectorListImpl(String cssText) {
		super();
		fText = cssText;
		parseSelectorText();
	}

	/**
	 * @return boolean
	 * @param obj
	 *            java.lang.Object
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || this.getClass() != obj.getClass())
			return false;

		CSSSelectorListImpl foreign = (CSSSelectorListImpl) obj;

		if (getLength() != foreign.getLength())
			return false;

		for (int i = 0; i < getLength(); i++) {
			if (!getSelector(i).equals(foreign.getSelector(i)))
				return false;
		}

		return true;
	}

	/**
	 * @return java.util.Iterator
	 */
	public Iterator getIterator() {
		return getSelectors().iterator();
	}

	/**
	 * @return int
	 */
	public int getLength() {
		return getSelectors().size();
	}

	public ICSSSelector getSelector(int index) {
		List selectors = getSelectors();
		if (0 <= index && index < selectors.size()) {
			return (ICSSSelector) selectors.get(index);
		}
		else {
			return null;
		}
	}

	private List getSelectors() {
		if (fSelectors == null) {
			parseSelectorText();
		}
		return fSelectors;
	}

	/**
	 * @return java.lang.String
	 */
	public String getString() {
		if (fCachedString == null) {
			StringBuffer buf = new StringBuffer();
			boolean bFirst = true;
			// groups is list of comma-separated selectors
			Iterator i = getSelectors().iterator();
			while (i.hasNext()) {
				if (!bFirst) {
					buf.append(", ");//$NON-NLS-1$
				}
				ICSSSelector item = (ICSSSelector) i.next();
				buf.append(item.getString());
				bFirst = false;
			}
			fCachedString = buf.toString();
		}
		return fCachedString;
	}

	/**
	 * @return boolean
	 * @param element
	 *            org.w3c.dom.Element
	 */
	public boolean match(Element element, String pseudoName) {
		int nSelectors = getLength();
		for (int iSelector = 0; iSelector < nSelectors; iSelector++) {
			// Check each Selector Lists
			ICSSSelector selector = getSelector(iSelector);
			if (selector.match(element, pseudoName))
				return true;
		}
		return false;
	}

	/**
	 * 
	 */
	private void parseSelectorText() {
		fSelectors = new ArrayList();
		if (fText == null) {
			return;
		}

		CSSTextParser parser = new CSSTextParser(CSSTextParser.MODE_STYLESHEET, fText);
		CSSTextToken[] tokens = parser.getTokens();
		if (tokens.length <= 0) {
			return;
		}

		List tokenGroup = new ArrayList();
		for (int i = 0; i < tokens.length; i++) {
			CSSTextToken token = tokens[i];
			if (token.kind == CSSRegionContexts.CSS_SELECTOR_SEPARATOR && 0 < tokenGroup.size()) {
				ICSSSelector selector = new CSSSelector(tokenGroup);
				if (selector != null) {
					fSelectors.add(selector);
				}
				tokenGroup.clear();
			}
			else {
				tokenGroup.add(tokens[i]);
			}
		}
		if (0 < tokenGroup.size()) {
			ICSSSelector selector = new CSSSelector(tokenGroup);
			if (selector != null) {
				fSelectors.add(selector);
			}
		}
	}

	/**
	 * 
	 */
	public String toString() {
		return getString();
	}

	/**
	 * 
	 */
	public Iterator getErrors() {
		List errors = new ArrayList();
		Iterator iSelector = getSelectors().iterator();
		while (iSelector.hasNext()) {
			Iterator iError = ((ICSSSelector) iSelector.next()).getErrors();
			while (iError.hasNext()) {
				errors.add(iError.next());
			}
		}
		return errors.iterator();
	}

	/**
	 * 
	 */
	public int getErrorCount() {
		int nErrors = 0;
		Iterator i = getErrors();
		while (i.hasNext()) {
			nErrors++;
			i.next();
		}
		return nErrors;
	}
}
