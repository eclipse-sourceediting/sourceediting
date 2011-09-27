/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.parserz.CSSTextToken;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorCombinator;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSSelectorItem;


/**
 * 
 */
public class CSSSelectorParser {

	private final static int IDLE = 0;
	private final static int ATTRIBUTE = 1;
	private final static int SIMPLE = 2;

	private List fTokens = null;
	private List fItems = null;
	private List fErrors = null;

	/**
	 * 
	 */
	CSSSelectorParser(List tokens) {
		super();
		fTokens = new ArrayList(tokens);
	}

	/**
	 * 
	 */
	List getSelector() {
		if (fItems == null) {
			parseSelector();
		}
		return fItems;
	}

	/**
	 * 
	 */
	private void parseSelector() {
		fItems = new ArrayList();

		List attrBuf = null;
		CSSSimpleSelector item = null;

		int status = IDLE;
		Iterator i = fTokens.iterator();
		while (i.hasNext()) {
			CSSTextToken token = (CSSTextToken) i.next();
			if (token == null || token.kind == CSSRegionContexts.CSS_S || token.kind == CSSRegionContexts.CSS_COMMENT) {
				continue;
			}
			switch (status) {
				case IDLE :
					if (isTag(token)) {
						item = createSimple();
						appendTag(item, token);
						status = SIMPLE;
					}
					else if (isID(token)) {
						item = createSimple();
						appendID(item, token);
						status = SIMPLE;
					}
					else if (isClass(token)) {
						item = createSimple();
						appendClass(item, token);
						status = SIMPLE;
					}
					else if (isPseudo(token)) {
						item = createSimple();
						appendPseudo(item, token);
						status = SIMPLE;
					}
					else if (isAttributeBegin(token)) {
						item = createSimple();
						status = ATTRIBUTE;
					}
					else {
						addError(token);
					}
					break;
				case SIMPLE :
					if (isID(token)) {
						appendID(item, token);
					}
					else if (isClass(token)) {
						appendClass(item, token);
					}
					else if (isPseudo(token)) {
						appendPseudo(item, token);
					}
					else if (isAttributeBegin(token)) {
						status = ATTRIBUTE;
					}
					else if (isCombinator(token)) { // combinator
						closeItem(item);
						closeItem(createCombinator(token));
						status = IDLE;
					}
					else {
						addError(token);
					}
					break;
				case ATTRIBUTE :
					if (isAttributeEnd(token) && attrBuf != null) {
						appendAttribute(item, attrBuf);
						attrBuf = null;
						status = SIMPLE;
					}
					else {
						if (attrBuf == null) {
							attrBuf = new ArrayList();
						}
						attrBuf.add(token);
						if (!isAttributeContent(token)) {
							addError(token);
						}
					}
					break;
				default :
					break;
			}
		}

		closeItem(item);
	}

	/**
	 * @param token
	 * @return
	 */
	private boolean isAttributeContent(CSSTextToken token) {
		String type = token.kind;
		return (type == CSSRegionContexts.CSS_SELECTOR_ATTRIBUTE_NAME || type == CSSRegionContexts.CSS_SELECTOR_ATTRIBUTE_OPERATOR || type == CSSRegionContexts.CSS_SELECTOR_ATTRIBUTE_VALUE);
	}

	/**
	 * @param token
	 * @return
	 */
	private boolean isAttributeEnd(CSSTextToken token) {
		String type = token.kind;
		return (type == CSSRegionContexts.CSS_SELECTOR_ATTRIBUTE_END);
	}

	/**
	 * @param token
	 * @return
	 */
	private boolean isAttributeBegin(CSSTextToken token) {
		String type = token.kind;
		return (type == CSSRegionContexts.CSS_SELECTOR_ATTRIBUTE_START);
	}

	/**
	 * @param token
	 * @return
	 */
	private boolean isCombinator(CSSTextToken token) {
		String type = token.kind;
		return (type == CSSRegionContexts.CSS_SELECTOR_COMBINATOR);
	}

	/**
	 * @param token
	 * @return
	 */
	private boolean isPseudo(CSSTextToken token) {
		String type = token.kind;
		return (type == CSSRegionContexts.CSS_SELECTOR_PSEUDO);
	}

	/**
	 * @param token
	 * @return
	 */
	private boolean isClass(CSSTextToken token) {
		String type = token.kind;
		return (type == CSSRegionContexts.CSS_SELECTOR_CLASS);
	}

	/**
	 * @param token
	 * @return
	 */
	private boolean isID(CSSTextToken token) {
		String type = token.kind;
		return (type == CSSRegionContexts.CSS_SELECTOR_ID);
	}

	/**
	 * @param token
	 * @return
	 */
	private boolean isTag(CSSTextToken token) {
		String type = token.kind;
		return (type == CSSRegionContexts.CSS_SELECTOR_ELEMENT_NAME || type == CSSRegionContexts.CSS_SELECTOR_UNIVERSAL || type == CSSRegionContexts.CSS_UNKNOWN);
	}

	private CSSSimpleSelector createSimple() {
		return new CSSSimpleSelector();
	}

	/**
	 * if " " or "+" or ">" appeared, close simpleselector and add new
	 * combinator
	 */
	private CSSSelectorCombinator createCombinator(CSSTextToken token) {
		char type = 0;

		String str = token.image;
		if (str.trim().length() == 0) { // space
			type = ICSSSelectorCombinator.DESCENDANT;
		}
		else if (str.equals("+")) { //$NON-NLS-1$
			type = ICSSSelectorCombinator.ADJACENT;
		}
		else if (str.equals(">")) { //$NON-NLS-1$
			type = ICSSSelectorCombinator.CHILD;
		}

		if (0 < type) {
			return new CSSSelectorCombinator(type);
		}
		else {
			return null;
		}
	}

	/**
	 * 
	 */
	private void closeItem(ICSSSelectorItem item) {
		if (item != null) {
			fItems.add(item);
		}
	}

	private void appendTag(CSSSimpleSelector item, CSSTextToken token) {
		item.setName(token.image);
		if (token.kind == CSSRegionContexts.CSS_UNKNOWN) {
			addError(token);
		}
	}

	/**
	 * if "#xxxx" appeared, add ID to current selector
	 */
	private void appendID(CSSSimpleSelector item, CSSTextToken token) {
		String text = token.toString();
		String idContent = text.substring(1, text.length());
		item.addID(idContent);
	}

	/**
	 * if ".xxxx" appeared, add Class to current selector
	 */
	private void appendClass(CSSSimpleSelector item, CSSTextToken token) {
		String text = token.toString();
		String classContent = text.substring(1, text.length());
		item.addClass(classContent);
		if (Character.isDigit(classContent.charAt(0))) {
			addError(token);
		}
	}

	/**
	 * if ":xxxx" appeared, add Pseudo(element/class) to current selector
	 */
	private void appendPseudo(CSSSimpleSelector item, CSSTextToken token) {
		String text = token.toString();
		String pseudoContent = text.substring(1, text.length());
		item.addPseudoName(pseudoContent);
	}

	/**
	 * 
	 */
	private void appendAttribute(CSSSimpleSelector item, List tokens) {
		StringBuffer buf = new StringBuffer();

		CSSTextToken token;
		Iterator i = tokens.iterator();
		while (i.hasNext()) {
			token = (CSSTextToken) i.next();
			buf.append(token.image);
		}

		item.addAttribute(buf.toString());
	}

	/**
	 * 
	 */
	List getSelectorTags() {
		List tagList = new ArrayList();
		return tagList;
	}

	/**
	 * 
	 */
	private void addError(CSSTextToken token) {
		if (fErrors == null) {
			fErrors = new ArrayList();
		}
		fErrors.add(token);
	}

	/**
	 * 
	 */
	Iterator getErrors() {
		return (fErrors == null) ? Collections.EMPTY_LIST.iterator() : fErrors.iterator();
	}
}
