/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.style.jspel;

import java.util.Collection;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.style.IStyleConstantsJSP;
import org.eclipse.jst.jsp.ui.internal.style.java.IStyleConstantsJSPJava;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.html.ui.internal.style.IStyleConstantsHTML;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.internal.provisional.style.AbstractLineStyleProvider;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.xml.ui.internal.style.IStyleConstantsXML;

// Note: many of the methods in this class were based on (or copied from)
// those
// found in the example Java Editor
public class LineStyleProviderForJSPEL extends AbstractLineStyleProvider implements LineStyleProvider {

	/** The scanner it uses */
	private JSPELCodeScanner fScanner;

	public LineStyleProviderForJSPEL() {
		super();
		fScanner = new JSPELCodeScanner();
	}

	/**
	 * Adds style information to the given text presentation.
	 * 
	 * @param presentation
	 *            the text presentation to be extended
	 * @param offset
	 *            the offset of the range to be styled
	 * @param length
	 *            the length of the range to be styled
	 * @param attr
	 *            the attribute describing the style of the range to be styled
	 */
	private void addRange(Collection presentation, int offset, int length, TextAttribute attr) {
		// support for user defined backgroud for JSP scriptlet regions
		String styleString = JSPUIPlugin.getDefault().getPreferenceStore().getString(IStyleConstantsJSP.JSP_CONTENT);
		String[] prefs = ColorHelper.unpackStylePreferences(styleString);
		Color bgColor = (prefs != null && prefs.length == 3 && prefs[1].startsWith("#") && Display.getCurrent() != null) //$NON-NLS-1$
					? new Color(Display.getCurrent(), ColorHelper.toRGB(prefs[1])) : attr.getBackground();

		presentation.add(new StyleRange(offset, length, attr.getForeground(), bgColor, attr.getStyle()));
	}

	/**
	 * Looks up the colorKey in the preference store and adds the style
	 * information to list of TextAttributes
	 * 
	 * @param colorKey
	 */
	private void addJavaTextAttribute(String colorKey) {
		IPreferenceStore store = getJavaColorPreferences();
		if (store != null && colorKey != null) {
			TextAttribute ta = null;
			if (colorKey == IStyleConstantsJSPEL.EL_KEYWORD) {
				// keyword
				RGB foreground = PreferenceConverter.getColor(store, PreferenceConstants.EDITOR_JAVA_KEYWORD_COLOR);
				boolean bold = store.getBoolean(PreferenceConstants.EDITOR_JAVA_KEYWORD_BOLD);
				boolean italics = store.getBoolean(PreferenceConstants.EDITOR_JAVA_KEYWORD_ITALIC);
				boolean strikethrough = store.getBoolean(PreferenceConstants.EDITOR_JAVA_KEYWORD_STRIKETHROUGH);
				boolean underline = store.getBoolean(PreferenceConstants.EDITOR_JAVA_KEYWORD_UNDERLINE);
				int style = SWT.NORMAL;
				if (bold) {
					style = style | SWT.BOLD;
				}
				if (italics) {
					style = style | SWT.ITALIC;
				}
				if (strikethrough) {
					style = style | TextAttribute.STRIKETHROUGH;
				}
				if (underline) {
					style = style | TextAttribute.UNDERLINE;
				}

				ta = createTextAttribute(foreground, null, style);
			} else if (colorKey == IStyleConstantsJSPEL.EL_DEFAULT) {
				// default
				RGB foreground = PreferenceConverter.getColor(store, PreferenceConstants.EDITOR_JAVA_DEFAULT_COLOR);
				boolean bold = store.getBoolean(PreferenceConstants.EDITOR_JAVA_DEFAULT_BOLD);
				boolean italics = store.getBoolean(PreferenceConstants.EDITOR_JAVA_DEFAULT_ITALIC);
				boolean strikethrough = store.getBoolean(PreferenceConstants.EDITOR_JAVA_DEFAULT_STRIKETHROUGH);
				boolean underline = store.getBoolean(PreferenceConstants.EDITOR_JAVA_DEFAULT_UNDERLINE);
				int style = SWT.NORMAL;
				if (bold) {
					style = style | SWT.BOLD;
				}
				if (italics) {
					style = style | SWT.ITALIC;
				}
				if (strikethrough) {
					style = style | TextAttribute.STRIKETHROUGH;
				}
				if (underline) {
					style = style | TextAttribute.UNDERLINE;
				}

				ta = createTextAttribute(foreground, null, style);
			}
			if (ta != null) {
				getTextAttributes().put(colorKey, ta);
				fScanner.setTokenData(colorKey, ta);
			}
		}
	}

	/**
	 * Returns a text attribute encoded in the given token. If the token's
	 * data is not <code>null</code> and a text attribute it is assumed that
	 * it is the encoded text attribute. It returns the default text attribute
	 * if there is no encoded text attribute found.
	 * 
	 * @param token
	 *            the token whose text attribute is to be determined
	 * @return the token's text attribute
	 */
	private TextAttribute getTokenTextAttribute(IToken token) {
		TextAttribute ta = null;

		Object data = token.getData();
		if (data instanceof TextAttribute)
			ta = (TextAttribute) data;
		else {
			ta = (TextAttribute) getTextAttributes().get(IStyleConstantsJSPJava.JAVA_DEFAULT);
		}
		return ta;
	}

	protected void loadColors() {
		addTextAttribute(IStyleConstantsHTML.SCRIPT_AREA_BORDER);
		addTextAttribute(IStyleConstantsXML.TAG_ATTRIBUTE_NAME);
		addTextAttribute(IStyleConstantsXML.TAG_ATTRIBUTE_VALUE);

		addJavaTextAttribute(IStyleConstantsJSPJava.JAVA_KEYWORD);
		addJavaTextAttribute(IStyleConstantsJSPJava.JAVA_DEFAULT);

		fScanner.initializeRules();
	}

	protected void handlePropertyChange(PropertyChangeEvent event) {
		String styleKey = null;
		String javaStyleKey = null;

		if (event != null) {
			String prefKey = event.getProperty();
			// check if preference changed is a style preference
			if (IStyleConstantsHTML.SCRIPT_AREA_BORDER.equals(prefKey)) {
				styleKey = IStyleConstantsHTML.SCRIPT_AREA_BORDER;
			} else if (IStyleConstantsXML.TAG_ATTRIBUTE_NAME.equals(prefKey)) {
				styleKey = IStyleConstantsXML.TAG_ATTRIBUTE_NAME;
			} else if (IStyleConstantsXML.TAG_ATTRIBUTE_VALUE.equals(prefKey)) {
				styleKey = IStyleConstantsXML.TAG_ATTRIBUTE_VALUE;
			} else if (PreferenceConstants.EDITOR_JAVA_KEYWORD_COLOR.equals(prefKey) || (PreferenceConstants.EDITOR_JAVA_KEYWORD_BOLD.equals(prefKey)) || (PreferenceConstants.EDITOR_JAVA_KEYWORD_ITALIC.equals(prefKey))) {
				javaStyleKey = IStyleConstantsJSPEL.EL_KEYWORD;
			} else if (PreferenceConstants.EDITOR_JAVA_DEFAULT_COLOR.equals(prefKey) || (PreferenceConstants.EDITOR_JAVA_DEFAULT_BOLD.equals(prefKey)) || (PreferenceConstants.EDITOR_JAVA_DEFAULT_ITALIC.equals(prefKey))) {
				javaStyleKey = IStyleConstantsJSPEL.EL_DEFAULT;
			}
		}

		if (styleKey != null) {
			// overwrite style preference with new value
			addTextAttribute(styleKey);
		}
		if (javaStyleKey != null) {
			// overwrite style preference with new value
			addJavaTextAttribute(javaStyleKey);
			fScanner.initializeRules();
		}
		if (styleKey != null || javaStyleKey != null) {
			// force a full update of the text viewer
			fRecHighlighter.refreshDisplay();
		}
	}

	public boolean prepareRegions(ITypedRegion typedRegion, int ssssrequestedStart, int ssssrequestedLength, Collection holdResults) {
		boolean result = true;
		try {
			// ideally, eventually, we'll have a "virtualDocument" we can
			// refer to, but for now ... we'll simple rescan the one region.
			// use simple adjustment (since "sub-content" starts at 0
			int offsetAdjustment = typedRegion.getOffset();
			String content = fDocument.get(typedRegion.getOffset(), typedRegion.getLength());
			IDocument document = new Document(content);

			int lastStart = 0;
			int length = 0;
			IToken lastToken = Token.UNDEFINED;

			int remainingLength = typedRegion.getLength();
			fScanner.setRange(document, lastStart, remainingLength);

			while (true) {

				IToken token = fScanner.nextToken();

				if (token.isEOF()) {
					if (!lastToken.isUndefined() && length != 0)
						addRange(holdResults, lastStart + offsetAdjustment, length, getTokenTextAttribute(lastToken));
					break;
				}

				if (token.isWhitespace()) {
					length += fScanner.getTokenLength();
					continue;
				}

				if (lastToken.isUndefined()) {
					lastToken = token;
					length += fScanner.getTokenLength();
					continue;
				}

				if (token != lastToken) {
					addRange(holdResults, lastStart + offsetAdjustment, length, getTokenTextAttribute(lastToken));
					lastToken = token;
					lastStart = fScanner.getTokenOffset();
					length = fScanner.getTokenLength();
					continue;
				}

				length += fScanner.getTokenLength();
			}
		} catch (BadLocationException e) {
			// shouldn't happen, but we don't want it to stop other
			// highlighting, if it does.
			result = false;
		}
		return result;
	}
	
	protected IPreferenceStore getColorPreferences() {
		return JSPUIPlugin.getDefault().getPreferenceStore();
	}
	
	private IPreferenceStore getJavaColorPreferences() {
		return PreferenceConstants.getPreferenceStore();
	}
	
	protected void registerPreferenceManager() {
		getColorPreferences().addPropertyChangeListener(fPreferenceListener);
		getJavaColorPreferences().addPropertyChangeListener(fPreferenceListener);
	}
	
	protected void unRegisterPreferenceManager() {
		getColorPreferences().removePropertyChangeListener(fPreferenceListener);
		getJavaColorPreferences().removePropertyChangeListener(fPreferenceListener);
	}

	protected TextAttribute getAttributeFor(ITextRegion region) {
		return null;
	}
}
