/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.style.java;

import java.util.Collection;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.jsdt.ui.PreferenceConstants;
import org.eclipse.wst.jsdt.web.ui.internal.JsUIPlugin;
import org.eclipse.wst.jsdt.web.ui.internal.style.IStyleConstantsJs;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.internal.provisional.style.AbstractLineStyleProvider;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.xml.ui.internal.style.IStyleConstantsXML;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class LineStyleProviderForJSDT extends AbstractLineStyleProvider implements LineStyleProvider {
	/** The scanner it uses */
	private JSDTCodeScanner fScanner;
	
	private IPropertyChangeListener fPreferenceListener = new IPropertyChangeListener() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent event) {
			// have to do it this way so others can override the method
			handlePropertyChange(event);
		}
	};
	
	public LineStyleProviderForJSDT() {
		super();
		fScanner = new JSDTCodeScanner();
	}
	
	/**
	 * Looks up the colorKey in the preference store and adds the style
	 * information to list of TextAttributes
	 * 
	 * @param colorKey
	 */
	protected void addTextAttribute(String colorKey) {
		IPreferenceStore javaStore = getJavaColorPreferences();
		if (javaStore != null && colorKey != null) {
			TextAttribute ta = null;
			if (colorKey == IStyleConstantsJSDT.JAVA_KEYWORD) {
				// keyword
				RGB foreground = PreferenceConverter.getColor(javaStore, PreferenceConstants.EDITOR_JAVA_KEYWORD_COLOR);
				boolean bold = javaStore.getBoolean(PreferenceConstants.EDITOR_JAVA_KEYWORD_BOLD);
				boolean italics = javaStore.getBoolean(PreferenceConstants.EDITOR_JAVA_KEYWORD_ITALIC);
				boolean strikethrough = javaStore.getBoolean(PreferenceConstants.EDITOR_JAVA_KEYWORD_STRIKETHROUGH);
				boolean underline = javaStore.getBoolean(PreferenceConstants.EDITOR_JAVA_KEYWORD_UNDERLINE);
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
			} else if (colorKey == IStyleConstantsJSDT.JAVA_STRING) {
				// string
				RGB foreground = PreferenceConverter.getColor(javaStore, PreferenceConstants.EDITOR_STRING_COLOR);
				boolean bold = javaStore.getBoolean(PreferenceConstants.EDITOR_STRING_BOLD);
				boolean italics = javaStore.getBoolean(PreferenceConstants.EDITOR_STRING_ITALIC);
				boolean strikethrough = javaStore.getBoolean(PreferenceConstants.EDITOR_STRING_STRIKETHROUGH);
				boolean underline = javaStore.getBoolean(PreferenceConstants.EDITOR_STRING_UNDERLINE);
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
			} else if (colorKey == IStyleConstantsJSDT.JAVA_SINGLE_LINE_COMMENT) {
				// single line comment
				RGB foreground = PreferenceConverter.getColor(javaStore, PreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_COLOR);
				boolean bold = javaStore.getBoolean(PreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_BOLD);
				boolean italics = javaStore.getBoolean(PreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_ITALIC);
				boolean strikethrough = javaStore.getBoolean(PreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_STRIKETHROUGH);
				boolean underline = javaStore.getBoolean(PreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_UNDERLINE);
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
			} else if (colorKey == IStyleConstantsJSDT.JAVA_MULTI_LINE_COMMENT) {
				// multi line comment
				RGB foreground = PreferenceConverter.getColor(javaStore, PreferenceConstants.EDITOR_MULTI_LINE_COMMENT_COLOR);
				boolean bold = javaStore.getBoolean(PreferenceConstants.EDITOR_MULTI_LINE_COMMENT_BOLD);
				boolean italics = javaStore.getBoolean(PreferenceConstants.EDITOR_MULTI_LINE_COMMENT_ITALIC);
				boolean strikethrough = javaStore.getBoolean(PreferenceConstants.EDITOR_MULTI_LINE_COMMENT_STRIKETHROUGH);
				boolean underline = javaStore.getBoolean(PreferenceConstants.EDITOR_MULTI_LINE_COMMENT_UNDERLINE);
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
			} else if (colorKey == IStyleConstantsJSDT.JAVA_DEFAULT) {
				// default
				RGB foreground = PreferenceConverter.getColor(javaStore, PreferenceConstants.EDITOR_JAVA_DEFAULT_COLOR);
				boolean bold = javaStore.getBoolean(PreferenceConstants.EDITOR_JAVA_DEFAULT_BOLD);
				boolean italics = javaStore.getBoolean(PreferenceConstants.EDITOR_JAVA_DEFAULT_ITALIC);
				boolean strikethrough = javaStore.getBoolean(PreferenceConstants.EDITOR_JAVA_DEFAULT_STRIKETHROUGH);
				boolean underline = javaStore.getBoolean(PreferenceConstants.EDITOR_JAVA_DEFAULT_UNDERLINE);
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
			} else if(colorKey == IStyleConstantsXML.COMMENT_BORDER ||
					colorKey == IStyleConstantsJs.JSP_CONTENT) {
				
				/** @see AbstractLineStyleProvider#addTextAttribute */
				ta = (TextAttribute)getTextAttributes().get(colorKey);
				String prefString = getHTMLColorPreferences().getString(colorKey);
				String[] stylePrefs = ColorHelper.unpackStylePreferences(prefString);
				if (stylePrefs != null) {
					RGB foreground = ColorHelper.toRGB(stylePrefs[0]);
					RGB background = ColorHelper.toRGB(stylePrefs[1]);
					boolean bold = Boolean.valueOf(stylePrefs[2]).booleanValue();
					boolean italic = Boolean.valueOf(stylePrefs[3]).booleanValue();
					boolean strikethrough = Boolean.valueOf(stylePrefs[4]).booleanValue();
					boolean underline = Boolean.valueOf(stylePrefs[5]).booleanValue();
					int style = SWT.NORMAL;
					if (bold) {
						style = style | SWT.BOLD;
					}
					if (italic) {
						style = style | SWT.ITALIC;
					}
					if (strikethrough) {
						style = style | TextAttribute.STRIKETHROUGH;
					}
					if (underline) {
						style = style | TextAttribute.UNDERLINE;
					}

					ta = createTextAttribute(foreground, background, style);
				}
			}
			if (ta != null) {
				getTextAttributes().put(colorKey, ta);
				fScanner.setTokenData(colorKey, ta);
			}
		}
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
		TextAttribute ta = (TextAttribute) getTextAttributes().get(IStyleConstantsJs.JSP_CONTENT);
		Color bgColor = ta.getBackground();
		if (bgColor == null) {
			bgColor = attr.getBackground();
		}
		StyleRange result = new StyleRange(offset, length, attr.getForeground(), bgColor, attr.getStyle());
		if ((attr.getStyle() & TextAttribute.STRIKETHROUGH) != 0) {
			result.strikeout = true;
		}
		if ((attr.getStyle() & TextAttribute.UNDERLINE) != 0) {
			result.underline = true;
		}
		presentation.add(result);
	}
	
	protected IPreferenceStore getColorPreferences() {
		return JsUIPlugin.getDefault().getPreferenceStore();
	}
	
	private IPreferenceStore getJavaColorPreferences() {
		return PreferenceConstants.getPreferenceStore();
	}
	
	private IPreferenceStore getHTMLColorPreferences() {
		return HTMLUIPlugin.getDefault().getPreferenceStore();
	}
	
	/**
	 * Returns a text attribute encoded in the given token. If the token's data
	 * is not <code>null</code> and a text attribute it is assumed that it is
	 * the encoded text attribute. It returns the default text attribute if
	 * there is no encoded text attribute found.
	 * 
	 * @param token
	 *            the token whose text attribute is to be determined
	 * @return the token's text attribute
	 */
	private TextAttribute getTokenTextAttribute(IToken token) {
		TextAttribute ta = null;
		Object data = token.getData();
		if (data instanceof TextAttribute) {
			ta = (TextAttribute) data;
		} else {
			ta = (TextAttribute) getTextAttributes().get(IStyleConstantsJSDT.JAVA_DEFAULT);
		}
		return ta;
	}
	
	protected void handlePropertyChange(PropertyChangeEvent event) {
		String styleKey = null;
		if (event != null) {
			String prefKey = event.getProperty();
			// check if preference changed is a style preference
			if(IStyleConstantsXML.COMMENT_BORDER.equals(prefKey)) {
				styleKey = IStyleConstantsXML.COMMENT_BORDER;
			} else if(IStyleConstantsXML.COMMENT_TEXT.equals(prefKey)) {
				styleKey = IStyleConstantsXML.COMMENT_TEXT;
			} else if (PreferenceConstants.EDITOR_JAVA_KEYWORD_COLOR.equals(prefKey) || (PreferenceConstants.EDITOR_JAVA_KEYWORD_BOLD.equals(prefKey)) || (PreferenceConstants.EDITOR_JAVA_KEYWORD_ITALIC.equals(prefKey))) {
				styleKey = IStyleConstantsJSDT.JAVA_KEYWORD;
			} else if (PreferenceConstants.EDITOR_STRING_COLOR.equals(prefKey) || (PreferenceConstants.EDITOR_STRING_BOLD.equals(prefKey)) || (PreferenceConstants.EDITOR_STRING_ITALIC.equals(prefKey))) {
				styleKey = IStyleConstantsJSDT.JAVA_STRING;
			} else if (PreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_COLOR.equals(prefKey) || (PreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_BOLD.equals(prefKey)) || (PreferenceConstants.EDITOR_SINGLE_LINE_COMMENT_ITALIC.equals(prefKey))) {
				styleKey = IStyleConstantsJSDT.JAVA_SINGLE_LINE_COMMENT;
			} else if (PreferenceConstants.EDITOR_MULTI_LINE_COMMENT_COLOR.equals(prefKey) || (PreferenceConstants.EDITOR_MULTI_LINE_COMMENT_BOLD.equals(prefKey)) || (PreferenceConstants.EDITOR_MULTI_LINE_COMMENT_ITALIC.equals(prefKey))) {
				styleKey = IStyleConstantsJSDT.JAVA_MULTI_LINE_COMMENT;
			} else if (PreferenceConstants.EDITOR_JAVA_DEFAULT_COLOR.equals(prefKey) || (PreferenceConstants.EDITOR_JAVA_DEFAULT_BOLD.equals(prefKey)) || (PreferenceConstants.EDITOR_JAVA_DEFAULT_ITALIC.equals(prefKey))) {
				styleKey = IStyleConstantsJSDT.JAVA_DEFAULT;
			}
		}
		if (styleKey != null) {
			// overwrite style preference with new value
			addTextAttribute(styleKey);
			fRecHighlighter.refreshDisplay();
			fScanner.initializeRules();
		}
	}
	
	protected void loadColors() {
		addTextAttribute(IStyleConstantsXML.COMMENT_BORDER);
		addTextAttribute(IStyleConstantsXML.COMMENT_TEXT);
		addTextAttribute(IStyleConstantsJs.JSP_CONTENT);
		addTextAttribute(IStyleConstantsJSDT.JAVA_KEYWORD);
		addTextAttribute(IStyleConstantsJSDT.JAVA_STRING);
		addTextAttribute(IStyleConstantsJSDT.JAVA_SINGLE_LINE_COMMENT);
		addTextAttribute(IStyleConstantsJSDT.JAVA_MULTI_LINE_COMMENT);
		addTextAttribute(IStyleConstantsJSDT.JAVA_DEFAULT);
		fScanner.initializeRules();
	}
	
	public boolean prepareRegions(ITypedRegion typedRegion, int ssssrequestedStart, int ssssrequestedLength, Collection holdResults) {
		boolean result = true;
		/* Initialize the text attributes. Also load the colors and initialize the rules of the scanner */
		getTextAttributes();
		try {
			// ideally, eventually, we'll have a "virtualDocument" we can
			// refer to, but for now ... we'll simple rescan the one region.
			// use simple adjustment (since "sub-content" starts at 0
			int lastStart = typedRegion.getOffset();
			int length = 0;
			IToken lastToken = Token.UNDEFINED;
			fScanner.setRange(getDocument(), lastStart, typedRegion.getLength());
			while (true) {
				IToken token = fScanner.nextToken();
				if (token.isEOF()) {
					if (!lastToken.isUndefined() && length != 0) {
						addRange(holdResults, lastStart, length, getTokenTextAttribute(lastToken));
					}
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
					addRange(holdResults, lastStart, length, getTokenTextAttribute(lastToken));
					lastToken = token;
					lastStart = fScanner.getTokenOffset();
					length = fScanner.getTokenLength();
					continue;
				}
				length += fScanner.getTokenLength();
			}
		} catch (Exception e) {
			// shouldn't happen, but we don't want it to stop other
			// highlighting, if it does.
			result = false;
		}
		return result;
	}

	protected TextAttribute getAttributeFor(ITextRegion region) {
		return null;
	}
	
	protected void registerPreferenceManager() {
		getColorPreferences().addPropertyChangeListener(fPreferenceListener);
		getJavaColorPreferences().addPropertyChangeListener(fPreferenceListener);
		this.getHTMLColorPreferences().addPropertyChangeListener(fPreferenceListener);
	}
	
	public void release() {
		super.release();
		fScanner = null;
	}
	
	protected void unRegisterPreferenceManager() {
		getColorPreferences().removePropertyChangeListener(fPreferenceListener);
		getJavaColorPreferences().removePropertyChangeListener(fPreferenceListener);
		this.getHTMLColorPreferences().removePropertyChangeListener(fPreferenceListener);
	}
}