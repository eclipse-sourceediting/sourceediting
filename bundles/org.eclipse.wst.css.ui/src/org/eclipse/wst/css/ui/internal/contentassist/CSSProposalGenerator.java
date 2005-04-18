/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.css.ui.internal.contentassist;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.provisional.preferences.CSSPreferenceHelper;
import org.eclipse.wst.css.core.internal.util.RegionIterator;
import org.eclipse.wst.css.ui.internal.image.CSSImageHelper;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

abstract class CSSProposalGenerator {


	protected class StringAndOffset {
		StringAndOffset(String string, int offset) {
			this.fString = string;
			this.fOffset = offset;
		}

		String fString;
		int fOffset;
	}

	protected CSSContentAssistContext fContext = null;

	/**
	 * CSSProposalGenerator constructor comment.
	 */
	private CSSProposalGenerator() {
		super();
	}

	CSSProposalGenerator(CSSContentAssistContext context) {
		super();
		fContext = context;
	}

	/**
	 *  
	 */
	protected boolean checkLeadingColon() {
		boolean hasLeadingColon = false;
		ITextRegion targetRegion = fContext.getTargetRegion();
		if (targetRegion == null && 0 < fContext.getCursorPos()) {
			targetRegion = fContext.getRegionByOffset(fContext.getCursorPos() - 1);
			if (targetRegion != null && targetRegion.getType() == CSSRegionContexts.CSS_SELECTOR_PSEUDO) {
				hasLeadingColon = true;
			}
		} else if (targetRegion != null) {
			RegionIterator iterator = fContext.getRegionIterator();
			if (iterator.hasPrev()) {
				iterator.prev();
				if (iterator.hasPrev() && iterator.prev().getType() == CSSRegionContexts.CSS_SELECTOR_PSEUDO) {
					hasLeadingColon = true;
				}
			}
		}
		return hasLeadingColon;
	}

	/**
	 *  
	 */
	protected StringAndOffset generateBraces() {
		StringBuffer buf = new StringBuffer();
		String lineDelimiter = fContext.getStructuredDocument().getLineDelimiter();
		CSSPreferenceHelper prefs = CSSPreferenceHelper.getInstance();
		String indentStr = prefs.getIndentString();
		if (prefs.isNewLineOnOpenBrace()) {
			buf.append(lineDelimiter);
		}
		buf.append("{");//$NON-NLS-1$
		if (prefs.isOnePropertyPerLine()) {
			buf.append(lineDelimiter);
			buf.append(indentStr);
		} else {
			buf.append(" ");//$NON-NLS-1$
		}
		int offset = buf.length();
		if (prefs.isOnePropertyPerLine()) {
			buf.append(lineDelimiter);
		} else {
			buf.append(" ");//$NON-NLS-1$
		}
		buf.append("}");//$NON-NLS-1$
		return new StringAndOffset(buf.toString(), offset);
	}

	/**
	 *  
	 */
	protected StringAndOffset generateParenthesis() {
		StringBuffer buf = new StringBuffer();
		int offset;
		buf.append("(");//$NON-NLS-1$
		offset = 1;
		buf.append(")");//$NON-NLS-1$
		return new StringAndOffset(buf.toString(), offset);
	}

	/**
	 *  
	 */
	protected StringAndOffset generateQuotes() {
		StringBuffer buf = new StringBuffer();
		char quoteChar = getQuoteChar();
		buf.append(quoteChar);
		buf.append(quoteChar);
		return new StringAndOffset(buf.toString(), 1);
	}

	/**
	 *  
	 */
	protected StringAndOffset generateSemicolon() {
		StringBuffer buf = new StringBuffer();
		int offset;
		buf.append(";");//$NON-NLS-1$
		offset = 0;
		return new StringAndOffset(buf.toString(), offset);
	}

	/**
	 *  
	 */
	protected StringAndOffset generateURI() {
		StringBuffer buf = new StringBuffer();
		CSSPreferenceHelper prefs = CSSPreferenceHelper.getInstance();
		char quoteChar = getQuoteChar();
		buf.append("url(");//$NON-NLS-1$
		if (prefs.isQuoteInURI()) {
			buf.append(quoteChar);
		}
		int offset = buf.length();
		if (prefs.isQuoteInURI()) {
			buf.append(quoteChar);
		}
		buf.append(")");//$NON-NLS-1$
		return new StringAndOffset(buf.toString(), offset);
	}

	abstract protected Iterator getCandidates();

	List getProposals() {
		List proposals = new ArrayList();

		CSSImageHelper imageHelper = CSSImageHelper.getInstance();
		Iterator i = getCandidates();
		while (i.hasNext()) {
			CSSCACandidate candidate = (CSSCACandidate) i.next();
			Image image = imageHelper.getImage(candidate.getImageType());
			ICompletionProposal item = new CompletionProposal(candidate.getReplacementString(), fContext.getReplaceBegin() + fContext.getDocumentOffset(), fContext.getTextToReplace().length(), candidate.getCursorPosition(), image, candidate.getDisplayString(), null, null);
			proposals.add(item);
		}

		return proposals;
	}

	/**
	 * 
	 * @return char
	 */
	private char getQuoteChar() {
		CSSPreferenceHelper prefs = CSSPreferenceHelper.getInstance();
		String quoteStr = prefs.getQuoteString(fContext.getModel());
		char quoteChar = (quoteStr != null && 0 < quoteStr.length()) ? quoteStr.charAt(0) : '"';
		char attrQuote = fContext.getQuoteOfStyleAttribute();
		if (attrQuote != 0) {
			if (attrQuote == '"' && quoteChar == '"') {
				quoteChar = '\'';
			} else if (attrQuote == '\'' && quoteChar == '\'') {
				quoteChar = '"';
			}
		}
		return quoteChar;
	}

	/**
	 *  
	 */
	protected boolean isMatch(String text) {
		String textToCompare = fContext.getTextToCompare();
		if (textToCompare.length() == 0) {
			return true;
		} else {
			return (text.toUpperCase().indexOf(textToCompare.toUpperCase()) == 0);
		}
		/*
		 * String textToReplace = fContext.getTextToReplace(); if
		 * (textToReplace.length() == 0) { return true; } else { return
		 * (text.toUpperCase().indexOf(textToReplace.toUpperCase()) == 0); }
		 */
	}
}