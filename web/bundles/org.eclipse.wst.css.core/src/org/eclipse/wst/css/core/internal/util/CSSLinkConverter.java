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
package org.eclipse.wst.css.core.internal.util;



import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSImportRule;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPrimitiveValue;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;


/**
 * 
 */
public class CSSLinkConverter extends org.eclipse.wst.css.core.internal.util.AbstractCssTraverser {
	private static final String URL_BEGIN = "url("; //$NON-NLS-1$
	private static final String URL_END = ")"; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String D_QUOTE = "\""; //$NON-NLS-1$
	private static final String S_QUOTE = "\'"; //$NON-NLS-1$

	IStructuredModel baseModel;

	/**
	 * 
	 */
	public CSSLinkConverter(IStructuredModel model) {
		super();
		baseModel = model;
		if (model instanceof ICSSModel && ((ICSSModel) model).getStyleSheetType() != ICSSModel.EXTERNAL) {
			IDOMNode node = (IDOMNode) ((ICSSModel) model).getOwnerDOMNode();
			baseModel = node.getModel();
		}
	}

	/**
	 * 
	 */
	public static String addFunc(String value) {
		if (!value.trim().toLowerCase().startsWith(URL_BEGIN)) {
			// pa_TODO css pref
			Preferences preferences = CSSCorePlugin.getDefault().getPluginPreferences();

			String quote = preferences.getString(CSSCorePreferenceNames.FORMAT_QUOTE);
			value = CSSUtil.stripQuotes(value);
			quote = CSSUtil.detectQuote(value, quote);
			String str = URL_BEGIN;
			if (preferences.getInt(CSSCorePreferenceNames.CASE_PROPERTY_VALUE) == CSSCorePreferenceNames.UPPER)
				str = str.toUpperCase();
			StringBuffer buf = new StringBuffer(str);
			buf.append(quote);
			buf.append(value);
			buf.append(quote);
			buf.append(URL_END);
			return buf.toString();
		}
		return value;
	}



	/**
	 * 
	 */
	protected void begin(ICSSNode node) {
		if (baseModel == null) {
			baseModel = node.getOwnerDocument().getModel();
			if (baseModel instanceof ICSSModel && ((ICSSModel) baseModel).getStyleSheetType() != ICSSModel.EXTERNAL) {
				IDOMNode xmlNode = (IDOMNode) ((ICSSModel) baseModel).getOwnerDOMNode();
				baseModel = xmlNode.getModel();
			}
		}
	}

	/**
	 * 
	 */
	private static boolean isUrl(String source) {
		if (source == null)
			return false;
		source = source.trim().toLowerCase();
		return source.startsWith(URL_BEGIN);
	}

	/**
	 * 
	 */
	protected short preNode(ICSSNode node) {
		if (node.getNodeType() == ICSSNode.PRIMITIVEVALUE_NODE) {
			toAbsolute((CSSValue) node);
		} else if (node.getNodeType() == ICSSNode.IMPORTRULE_NODE) {
			ICSSImportRule iRule = (ICSSImportRule) node;
			iRule.setHref(toAbsolute(addFunc(iRule.getHref())));
		}
		return TRAV_CONT;
	}

	/**
	 * 
	 * @return java.lang.String
	 * @param value
	 *            java.lang.String
	 */
	public static String removeFunc(String value) {
		if (value == null)
			return EMPTY;
		String field = value.trim();
		// first : tear "url(....)"
		if (field.toLowerCase().startsWith(URL_BEGIN)) {
			int url = field.toLowerCase().indexOf(URL_BEGIN);
			int endParenthesis = field.lastIndexOf(URL_END);
			if (endParenthesis > url) {
				field = field.substring(url + 4, endParenthesis);
			} else
				field = field.substring(url + 4);
		}
		return field.trim();
	}

	/**
	 * 
	 */
	public static String stripFunc(String value) {
		if (value == null)
			return EMPTY;
		// first : tear "url(....)"
		String field = removeFunc(value);
		// second : tear quotations
		if (field.toLowerCase().startsWith(D_QUOTE)) {
			int quote = field.indexOf(D_QUOTE);
			int end = field.lastIndexOf(D_QUOTE);
			if (end > quote) {
				field = field.substring(quote + 1, end);
			} else
				field = field.substring(quote + 1);
		} else if (field.toLowerCase().startsWith(S_QUOTE)) {
			int quote = field.indexOf(S_QUOTE);
			int end = field.lastIndexOf(S_QUOTE);
			if (end > quote) {
				field = field.substring(quote + 1, end);
			} else
				field = field.substring(quote + 1);
		}

		return field.trim();
	}

	/**
	 * 
	 */
	public String toAbsolute(String source) {
		if (isUrl(source)) {
			String url = CSSPathService.getAbsoluteURL(baseModel, stripFunc(source));
			return (url != null) ? addFunc(url) : (URL_BEGIN + URL_END);
		}
		return source;
	}

	/**
	 * 
	 */
	public boolean toAbsolute(CSSValue node) {
		if (node instanceof ICSSPrimitiveValue) {
			ICSSPrimitiveValue value = (ICSSPrimitiveValue) node;
			if (value.getPrimitiveType() == CSSPrimitiveValue.CSS_URI) {
				value.setValue(stripFunc(toAbsolute(URL_BEGIN + value.getStringValue() + URL_END)));
				return true;
			}
		}
		return false;
	}
}
