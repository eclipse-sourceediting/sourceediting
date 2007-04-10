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

import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;


/**
 * 
 */
class CSSModelNodeFeeder {
	/**
	 * currently ICSSDocument is used but may go back to CSSDocumentImpl in future.
	 */
	ICSSDocument fDocument = null;
	CSSModelUpdateContext fUpdateContext = null;

	/**
	 * CSSModelNodeFeeder constructor comment.
	 */
	CSSModelNodeFeeder() {
		super();
	}

	/**
	 * CSSModelNodeFeeder constructor comment.
	 * currently ICSSDocument is used but may go back to CSSDocumentImpl in future.
	 */
	CSSModelNodeFeeder(ICSSDocument document, CSSModelUpdateContext updateContext) {
		super();
		fDocument = document;
		fUpdateContext = updateContext;
	}

	/**
	 * 
	 */
	CSSCharsetRuleImpl getCSSCharsetRule() {
		CSSCharsetRuleImpl node;
		if (fUpdateContext.isActive()) {
			node = fUpdateContext.getCSSCharsetRule();
		}
		else {
			node = (CSSCharsetRuleImpl) fDocument.createCSSCharsetRule();
		}
		return node;
	}

	/**
	 * 
	 */
	CSSFontFaceRuleImpl getCSSFontFaceRule() {
		CSSFontFaceRuleImpl node;
		if (fUpdateContext.isActive()) {
			node = fUpdateContext.getCSSFontFaceRule();
		}
		else {
			node = (CSSFontFaceRuleImpl) fDocument.createCSSFontFaceRule();
		}
		return node;
	}

	/**
	 * 
	 */
	CSSImportRuleImpl getCSSImportRule() {
		CSSImportRuleImpl node;
		if (fUpdateContext.isActive()) {
			node = fUpdateContext.getCSSImportRule();
		}
		else {
			node = (CSSImportRuleImpl) fDocument.createCSSImportRule();
		}
		return node;
	}

	/**
	 * 
	 */
	CSSMediaRuleImpl getCSSMediaRule() {
		CSSMediaRuleImpl node;
		if (fUpdateContext.isActive()) {
			node = fUpdateContext.getCSSMediaRule();
		}
		else {
			node = (CSSMediaRuleImpl) fDocument.createCSSMediaRule();
		}
		return node;
	}

	/**
	 * 
	 */
	CSSPageRuleImpl getCSSPageRule() {
		CSSPageRuleImpl node;
		if (fUpdateContext.isActive()) {
			node = fUpdateContext.getCSSPageRule();
		}
		else {
			node = (CSSPageRuleImpl) fDocument.createCSSPageRule();
		}
		return node;
	}

	/**
	 * 
	 */
	CSSStyleRuleImpl getCSSStyleRule() {
		CSSStyleRuleImpl node;
		if (fUpdateContext.isActive()) {
			node = fUpdateContext.getCSSStyleRule();
		}
		else {
			node = (CSSStyleRuleImpl) fDocument.createCSSStyleRule();
		}
		return node;
	}
}
