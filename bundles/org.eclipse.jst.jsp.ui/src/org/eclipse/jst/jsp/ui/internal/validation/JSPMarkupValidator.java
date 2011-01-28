/*******************************************************************************
 * Copyright (c) 2009, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.validation;

import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.ui.internal.reconcile.AbstractStructuredTextReconcilingStrategy;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.eclipse.wst.xml.ui.internal.correction.ProblemIDsXML;
import org.eclipse.wst.xml.ui.internal.validation.MarkupValidator;

public class JSPMarkupValidator extends MarkupValidator {

	private boolean hasXMLAttributes(IStructuredDocumentRegion structuredDocumentRegion) {
		ITextRegionList regions = structuredDocumentRegion.getRegions();

		if (regions.size() > 1 && regions.get(0).getType() == DOMRegionContext.XML_TAG_OPEN) {
			ITextRegion region = regions.get(1);
			if (region.getType() == DOMRegionContext.XML_TAG_NAME || region.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
				// Tag name has a prefix, be strict about requiring quotes
				if (structuredDocumentRegion.getText(region).indexOf(":") != -1) //$NON-NLS-1$
						return true;
			}
		}
		return false;
	}

	protected void checkForAttributeValue(IStructuredDocumentRegion structuredDocumentRegion, IReporter reporter) {

		if (structuredDocumentRegion.isDeleted()) {
			return;
		}

		if (hasXMLAttributes(structuredDocumentRegion))
			super.checkForAttributeValue(structuredDocumentRegion, reporter);

		// check for attributes without a value
		// track the attribute/equals/value sequence using a state of 0, 1 ,2
		// representing the name, =, and value, respectively
		int attrState = 0;
		ITextRegionList textRegions = structuredDocumentRegion.getRegions();

		int errorCount = 0;
		for (int i = 0; (i < textRegions.size()) && (errorCount < AbstractStructuredTextReconcilingStrategy.ELEMENT_ERROR_LIMIT); i++) {
			ITextRegion textRegion = textRegions.get(i);
			if ((textRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) || isTagCloseTextRegion(textRegion)) {
				// dangling name and '='
				if ((attrState == 2) && (i >= 2)) {
					// create annotation
					ITextRegion nameRegion = textRegions.get(i - 2);
					if (!(nameRegion instanceof ITextRegionContainer)) {
						Object[] args = {structuredDocumentRegion.getText(nameRegion)};
						String messageText = NLS.bind(XMLUIMessages.Attribute__is_missing_a_value, args);

						int start = structuredDocumentRegion.getStartOffset(nameRegion);
						int end = structuredDocumentRegion.getEndOffset();
						int textLength = structuredDocumentRegion.getText(nameRegion).trim().length();

						// quick fix info
						ITextRegion equalsRegion = textRegions.get(i - 2 + 1);
						int insertOffset = structuredDocumentRegion.getTextEndOffset(equalsRegion) - end;
						Object[] additionalFixInfo = {structuredDocumentRegion.getText(nameRegion), new Integer(insertOffset)};

						addAttributeError(messageText, additionalFixInfo, start, textLength, ProblemIDsXML.MissingAttrValue, structuredDocumentRegion, reporter);
						// annotation.setAdditionalFixInfo(additionalFixInfo);
						// results.add(annotation);
						errorCount++;
					}
				}
				attrState = 1;
			}
			else if (textRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
				attrState = 2;
			}
			else if (textRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				attrState = 0;
			}
		}

	}

	protected void checkQuotesForAttributeValues(IStructuredDocumentRegion structuredDocumentRegion, IReporter reporter) {
		ITextRegionList regions = structuredDocumentRegion.getRegions();
		ITextRegion r = null;
		String attrValueText = ""; //$NON-NLS-1$
		int errorCount = 0;

		if (hasXMLAttributes(structuredDocumentRegion))
			super.checkQuotesForAttributeValues(structuredDocumentRegion, reporter);

		for (int i = 0; (i < regions.size()) && (errorCount < AbstractStructuredTextReconcilingStrategy.ELEMENT_ERROR_LIMIT); i++) {
			r = regions.get(i);
			if (r.getType() != DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				continue;
			}

			attrValueText = structuredDocumentRegion.getText(r);
			// attribute value includes quotes in the string
			// split up attribute value on quotes
			/*
			 * WORKAROUND till
			 * http://dev.icu-project.org/cgi-bin/icu-bugs/incoming?findid=5207
			 * is fixed. (Also see BUG143628)
			 */

			java.util.StringTokenizer st = new java.util.StringTokenizer(attrValueText, "\"'", true); //$NON-NLS-1$
			int size = st.countTokens();
			// get the pieces of the attribute value
			String one = "", two = ""; //$NON-NLS-1$ //$NON-NLS-2$
			if (size > 0) {
				one = st.nextToken();
			}
			if (size > 1) {
				two = st.nextToken();
			}
			if (size > 2) {
				// should be handled by parsing...
				// as in we can't have an attribute value like: <element
				// attr="a"b"c"/>
				// and <element attr='a"b"c' /> is legal
				continue;
			}


			if (size == 1) {
				if (one.equals(DQUOTE) || one.equals(SQUOTE)) {
					// missing closing quote
					String message = XMLUIMessages.ReconcileStepForMarkup_0;
					addAttributeError(message, attrValueText, structuredDocumentRegion.getStartOffset(r), attrValueText.trim().length(), ProblemIDsXML.Unclassified, structuredDocumentRegion, reporter);
					errorCount++;
				}
			}
			else if (size == 2) {
				if ((one.equals(SQUOTE) && !two.equals(SQUOTE)) || (one.equals(DQUOTE) && !two.equals(DQUOTE))) {
					// missing closing quote
					String message = XMLUIMessages.ReconcileStepForMarkup_0;
					addAttributeError(message, attrValueText, structuredDocumentRegion.getStartOffset(r), attrValueText.trim().length(), ProblemIDsXML.Unclassified, structuredDocumentRegion, reporter);
					errorCount++;
				}
			}
		}
		// end of region for loop
	}

	private boolean isTagCloseTextRegion(ITextRegion textRegion) {
		return (textRegion.getType() == DOMRegionContext.XML_TAG_CLOSE) || (textRegion.getType() == DOMRegionContext.XML_EMPTY_TAG_CLOSE);
	}

	protected int getMissingEndTagSeverity() {
		final int severity = HTMLCorePlugin.getDefault().getPluginPreferences().getInt(HTMLCorePreferenceNames.ELEM_MISSING_END);
		return severity < 0 ? IMessage.LOW_SEVERITY : severity;
	}
}
