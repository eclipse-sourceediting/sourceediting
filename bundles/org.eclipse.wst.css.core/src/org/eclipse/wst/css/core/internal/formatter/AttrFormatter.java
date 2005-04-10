/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.formatter;



import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.css.core.internal.cleanup.CSSCleanupStrategy;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSAttr;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSCharsetRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSImportRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPageRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclItem;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.eclipse.wst.css.core.internal.util.CSSUtil;
import org.eclipse.wst.css.core.preferences.CSSPreferenceHelper;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocument;


/**
 * 
 */
public class AttrFormatter extends DefaultCSSSourceFormatter {

	private static AttrFormatter instance;

	/**
	 * 
	 */
	AttrFormatter() {
		super();
	}

	/**
	 * 
	 */
	protected void formatPre(ICSSNode node, StringBuffer source) {
		ICSSAttr attr = (ICSSAttr) node;
		CSSCleanupStrategy stgy = getCleanupStrategy(node);

		int end = ((IndexedRegion) node).getEndOffset();
		if (end > 0) { // format source
			int start = ((IndexedRegion) node).getStartOffset();
			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);

			if (attr.getName().equals(ICSSStyleRule.SELECTOR) || attr.getName().equals(ICSSPageRule.SELECTOR)) {
				for (int i = 0; i < regions.length; i++) {
					if (i != 0)
						appendSpaceBetween(node, regions[i - 1], regions[i], source);
					source.append(decoratedRegion(regions[i], 0, stgy));
				}
			}
			else if (attr.getName().equals(ICSSImportRule.HREF)) {
				for (int i = 0; i < regions.length; i++) {
					if (i != 0)
						appendSpaceBefore(node, regions[i], source);
					source.append(decoratedPropValueRegion(regions[i], stgy));
				}
			}
			else if (attr.getName().equals(ICSSCharsetRule.ENCODING)) {
				for (int i = 0; i < regions.length; i++) {
					if (i != 0)
						appendSpaceBefore(node, regions[i], source);
					source.append(decoratedIdentRegion(regions[i], stgy));
				}
			}
			else if (attr.getName().equals(ICSSStyleDeclItem.IMPORTANT)) {
				for (int i = 0; i < regions.length; i++) {
					if (i != 0)
						appendSpaceBefore(node, regions[i], source);
					source.append(decoratedPropValueRegion(regions[i], stgy));
				}
			}
			else if (attr.getName() == null || attr.getName().length() == 0) {
				for (int i = 0; i < regions.length; i++) {
					if (i != 0)
						appendSpaceBefore(node, regions[i], source);
					source.append(decoratedIdentRegion(regions[i], stgy));
				}
			}
			else {
				// counter attributes
				for (int i = 0; i < regions.length; i++) {
					if (i != 0)
						appendSpaceBefore(node, regions[i], source);
					source.append(decoratedPropValueRegion(regions[i], stgy));
				}
			}
		}
		else { // generate source
			CSSPreferenceHelper mgr = CSSPreferenceHelper.getInstance();
			String value = attr.getValue();
			if (value == null)
				value = "";//$NON-NLS-1$
			if (attr.getName().equals(ICSSStyleRule.SELECTOR) || attr.getName().equals(ICSSPageRule.SELECTOR)) {
			}
			else if (attr.getName().equals(ICSSImportRule.HREF)) {
				String uri = org.eclipse.wst.css.core.internal.util.CSSLinkConverter.stripFunc(value);
				String func = mgr.isPropValueUpperCase() ? "URL(" : "url(";//$NON-NLS-2$//$NON-NLS-1$
				if (mgr.isQuoteInURI()) {
					String quote = mgr.getQuoteString(attr.getOwnerDocument().getModel());
					quote = CSSUtil.detectQuote(uri, quote);
					value = func + quote + uri + quote + ")";//$NON-NLS-1$
				}
				else {
					value = func + uri + ")";//$NON-NLS-1$
				}
			}
			else if (attr.getName().equals(ICSSCharsetRule.ENCODING)) {
				String quote = mgr.getQuoteString(attr.getOwnerDocument().getModel());
				if (!value.startsWith("\"") && !value.startsWith("\'"))//$NON-NLS-2$//$NON-NLS-1$
					value = quote + value;
				if (!value.endsWith("\"") && !value.endsWith("\'"))//$NON-NLS-2$//$NON-NLS-1$
					value = value + quote;
			}
			else if (attr.getName().equals(ICSSStyleDeclItem.IMPORTANT)) {
				if (mgr.isPropValueUpperCase())
					value = value.toUpperCase();
				else
					value = value.toLowerCase();
			}
			else if (attr.getName() == null || attr.getName().length() == 0) {
				if (mgr.isIdentUpperCase())
					value = value.toUpperCase();
				else
					value = value.toLowerCase();
			}
			else {
				if (mgr.isPropValueUpperCase())
					value = value.toUpperCase();
				else
					value = value.toLowerCase();
			}
			source.append(value);
		}
	}

	/**
	 * 
	 */
	protected void formatPre(ICSSNode node, IRegion region, StringBuffer source) {
		formatPre(node, source);
	}

	/**
	 * 
	 */
	public synchronized static AttrFormatter getInstance() {
		if (instance == null) {
			instance = new AttrFormatter();
		}
		return instance;
	}
}