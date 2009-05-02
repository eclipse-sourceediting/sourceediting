/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Frits Jalvingh - contributions for bug 150794
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.style.java;

import java.util.Collection;
import java.util.HashMap;

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
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.style.IStyleConstantsJSP;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.html.ui.internal.style.IStyleConstantsHTML;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.internal.provisional.style.Highlighter;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;
import org.eclipse.wst.xml.ui.internal.style.IStyleConstantsXML;

// Note: many of the methods in this class were based on (or copied from) those
// found in the example Java Editor
public class LineStyleProviderForJava implements LineStyleProvider{
	private class PropertyChangeListener implements IPropertyChangeListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent event) {
			// have to do it this way so others can override the method
			handlePropertyChange(event);
		}
	}

	private IDocument fDocument;
	private Highlighter fHighlighter;
	private boolean fIsInitialized = false;
	private PropertyChangeListener fPreferenceListener = new PropertyChangeListener();
	/** The scanner it uses */
	private JavaCodeScanner fScanner;
	/** Contains all text attributes pretaining to this line style provider */
	private HashMap fTextAttributes = null;

	public LineStyleProviderForJava() {
		super();
		fScanner = new JavaCodeScanner();
		loadColors();		// Make sure we have rules before we start parsing
	}

	/**
	 * Adds style information to the given text presentation.
	 *
	 * @param presentation the text presentation to be extended
	 * @param offset the offset of the range to be styled
	 * @param length the length of the range to be styled
	 * @param attr the attribute describing the style of the range to be styled
	 */
	private void addRange(Collection presentation, int offset, int length, TextAttribute attr) {
		// support for user defined backgroud for JSP scriptlet regions
		String styleString = JSPUIPlugin.getDefault().getPreferenceStore().getString(IStyleConstantsJSP.JSP_CONTENT);
		String[] prefs = ColorHelper.unpackStylePreferences(styleString);
		Color bgColor = (prefs != null && prefs.length == 3 && prefs[1].startsWith("#") && Display.getCurrent() != null) //$NON-NLS-1$
							? new Color(Display.getCurrent(), ColorHelper.toRGB(prefs[1]))
							: attr.getBackground();
							
		presentation.add(new StyleRange(offset, length, attr.getForeground(), bgColor, attr.getStyle()));
	}

	/**
	 * Looks up the colorKey in the preference store and adds the style
	 * information to list of TextAttributes
	 * 
	 * @param colorKey
	 */
	private void addTextAttribute(String colorKey) {
		if (getColorPreferences() != null) {
			String prefString = getColorPreferences().getString(colorKey);
			String[] stylePrefs = ColorHelper.unpackStylePreferences(prefString);
			if (stylePrefs != null) {
				RGB foreground = ColorHelper.toRGB(stylePrefs[0]);
				RGB background = ColorHelper.toRGB(stylePrefs[1]);
				boolean bold = Boolean.valueOf(stylePrefs[2]).booleanValue();
				getTextAttributes().put(colorKey, createTextAttribute(foreground, background, bold));
			}
		}
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
			if (colorKey == IStyleConstantsJSPJava.JAVA_KEYWORD) {
				// keyword
				RGB foreground = PreferenceConverter.getColor(store, PreferenceConstants.EDITOR_JAVA_KEYWORD_COLOR);
				boolean bold = store.getBoolean(PreferenceConstants.EDITOR_JAVA_KEYWORD_BOLD);
				boolean italics = store.getBoolean(PreferenceConstants.EDITOR_JAVA_KEYWORD_ITALIC);
				ta = createTextAttribute(foreground, null, bold, italics);
			} else if (colorKey == IStyleConstantsJSPJava.JAVA_STRING) {
				// string
				RGB foreground = PreferenceConverter.getColor(store, PreferenceConstants.EDITOR_STRING_COLOR);
				boolean bold = store.getBoolean(PreferenceConstants.EDITOR_STRING_BOLD);
				boolean italics = store.getBoolean(PreferenceConstants.EDITOR_STRING_ITALIC);
				ta = createTextAttribute(foreground, null, bold, italics);
			} else if (colorKey == IStyleConstantsJSPJava.JAVA_SINGLE_LINE_COMMENT) {
				// single line comment
				RGB foreground = PreferenceConverter.getColor(store, PreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_COLOR);
				boolean bold = store.getBoolean(PreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_BOLD);
				boolean italics = store.getBoolean(PreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_ITALIC);
				ta = createTextAttribute(foreground, null, bold, italics);
			} else if (colorKey == IStyleConstantsJSPJava.JAVA_DEFAULT) {
				// default
				RGB foreground = PreferenceConverter.getColor(store, PreferenceConstants.EDITOR_JAVA_DEFAULT_COLOR);
				boolean bold = store.getBoolean(PreferenceConstants.EDITOR_JAVA_DEFAULT_BOLD);
				boolean italics = store.getBoolean(PreferenceConstants.EDITOR_JAVA_DEFAULT_ITALIC);
				ta = createTextAttribute(foreground, null, bold, italics);
			}
			if (ta != null) {
				getTextAttributes().put(colorKey, ta);
				fScanner.setTokenData(colorKey, ta);
			}
		}
	}

	private TextAttribute createTextAttribute(RGB foreground, RGB background, boolean bold) {
		return new TextAttribute((foreground != null) ? EditorUtility.getColor(foreground) : null, (background != null) ? EditorUtility.getColor(background) : null, bold ? SWT.BOLD : SWT.NORMAL);
	}
	
	private TextAttribute createTextAttribute(RGB foreground, RGB background, boolean bold, boolean italics) {
		int style = bold ? SWT.BOLD : SWT.NORMAL;
		if (italics)
			style |= SWT.ITALIC;
		
		return new TextAttribute((foreground != null) ? EditorUtility.getColor(foreground) : null, (background != null) ? EditorUtility.getColor(background) : null, style);
	}
	
	/**
	 * Returns the hashtable containing all the text attributes for this line
	 * style provider. Lazily creates a hashtable if one has not already been
	 * created.
	 * 
	 * @return
	 */
	private HashMap getTextAttributes() {
		if (fTextAttributes == null) {
			fTextAttributes = new HashMap();
			loadColors();
		}
		return fTextAttributes;
	}
	
	/**
	 * Returns a text attribute encoded in the given token. If the token's
	 * data is not <code>null</code> and a text attribute it is assumed that
	 * it is the encoded text attribute. It returns the default text attribute
	 * if there is no encoded text attribute found.
	 *
	 * @param token the token whose text attribute is to be determined
	 * @return the token's text attribute
	 */
	private TextAttribute getTokenTextAttribute(IToken token) {
		TextAttribute ta = null;
		
		Object data = token.getData();
		if (data instanceof TextAttribute)
			ta = (TextAttribute)data;
		else {
			ta = (TextAttribute)getTextAttributes().get(IStyleConstantsJSPJava.JAVA_DEFAULT);
		}
		return ta; 
	}
	
	public void init(IStructuredDocument document, Highlighter highlighter) {
		fDocument = document;
		fHighlighter = highlighter;
		
		if (fIsInitialized)
			return;

		registerPreferenceListener();

		fIsInitialized = true;
	}

	private void loadColors() {
		addTextAttribute(IStyleConstantsHTML.SCRIPT_AREA_BORDER);
		addTextAttribute(IStyleConstantsXML.TAG_ATTRIBUTE_NAME);
		addTextAttribute(IStyleConstantsXML.TAG_ATTRIBUTE_VALUE);
		
		addJavaTextAttribute(IStyleConstantsJSPJava.JAVA_KEYWORD);
		addJavaTextAttribute(IStyleConstantsJSPJava.JAVA_STRING);
		addJavaTextAttribute(IStyleConstantsJSPJava.JAVA_SINGLE_LINE_COMMENT);
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
			}
			else if (IStyleConstantsXML.TAG_ATTRIBUTE_NAME.equals(prefKey)) {
				styleKey = IStyleConstantsXML.TAG_ATTRIBUTE_NAME;
			}
			else if (IStyleConstantsXML.TAG_ATTRIBUTE_VALUE.equals(prefKey)) {
				styleKey = IStyleConstantsXML.TAG_ATTRIBUTE_VALUE;
			} else if (PreferenceConstants.EDITOR_JAVA_KEYWORD_COLOR.equals(prefKey) || (PreferenceConstants.EDITOR_JAVA_KEYWORD_BOLD.equals(prefKey))|| (PreferenceConstants.EDITOR_JAVA_KEYWORD_ITALIC.equals(prefKey))) {
				javaStyleKey = IStyleConstantsJSPJava.JAVA_KEYWORD;
			} else if (PreferenceConstants.EDITOR_STRING_COLOR.equals(prefKey) || (PreferenceConstants.EDITOR_STRING_BOLD.equals(prefKey))|| (PreferenceConstants.EDITOR_STRING_ITALIC.equals(prefKey))) {
				javaStyleKey = IStyleConstantsJSPJava.JAVA_STRING;
			} else if (PreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_COLOR.equals(prefKey) || (PreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_BOLD.equals(prefKey))|| (PreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_ITALIC.equals(prefKey))) {
				javaStyleKey = IStyleConstantsJSPJava.JAVA_SINGLE_LINE_COMMENT;
			} else if (PreferenceConstants.EDITOR_JAVA_DEFAULT_COLOR.equals(prefKey) || (PreferenceConstants.EDITOR_JAVA_DEFAULT_BOLD.equals(prefKey))|| (PreferenceConstants.EDITOR_JAVA_DEFAULT_ITALIC.equals(prefKey))) {
				javaStyleKey = IStyleConstantsJSPJava.JAVA_DEFAULT;
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
			fHighlighter.refreshDisplay();
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
		}
		catch (BadLocationException e) {
			// shouldn't happen, but we don't want it to stop other highlighting, if it does.
			result = false;
		}
		return result;
	}
	
	private void registerPreferenceListener() {
		getColorPreferences().addPropertyChangeListener(fPreferenceListener);
		getJavaColorPreferences().addPropertyChangeListener(fPreferenceListener);
	}
	
	public void release() {
		unregisterPreferenceManager();
		if (fTextAttributes != null) {
			fTextAttributes.clear();
			fTextAttributes = null;
		}
	}

	private void unregisterPreferenceManager() {
		getColorPreferences().removePropertyChangeListener(fPreferenceListener);
		getJavaColorPreferences().removePropertyChangeListener(fPreferenceListener);
	}
	
	private IPreferenceStore getColorPreferences() {
		return JSPUIPlugin.getDefault().getPreferenceStore();
	}
	
	private IPreferenceStore getJavaColorPreferences() {
		return PreferenceConstants.getPreferenceStore();
	}
}
