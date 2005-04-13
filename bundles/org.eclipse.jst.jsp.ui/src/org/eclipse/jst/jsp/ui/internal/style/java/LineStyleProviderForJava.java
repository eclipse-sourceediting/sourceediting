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
package org.eclipse.jst.jsp.ui.internal.style.java;

import java.util.Collection;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wst.html.ui.internal.style.IStyleConstantsHTML;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;
import org.eclipse.wst.sse.ui.style.AbstractLineStyleProvider;
import org.eclipse.wst.sse.ui.style.LineStyleProvider;
import org.eclipse.wst.xml.ui.internal.style.IStyleConstantsXML;
import org.w3c.dom.Node;

// Note: many of the methods in this class were based on (or copied from) those
// found in the example Java Editor
public class LineStyleProviderForJava extends AbstractLineStyleProvider implements LineStyleProvider{

	private IDocument fDocument;
	/** The scanner it uses */
	protected RuleBasedScanner fScanner;
	/** The default text attribute if non is returned as data by the current token */
	protected TextAttribute fDefaultTextAttribute;
	protected Node targetNode;

	public LineStyleProviderForJava() {
		super();
		loadColors();
		fScanner = new JavaCodeScanner();
		fDefaultTextAttribute = new TextAttribute(EditorUtility.getColor(JavaColorProvider.DEFAULT));

	}

	/**
	 * Adds style information to the given text presentation.
	 *
	 * @param presentation the text presentation to be extended
	 * @param offset the offset of the range to be styled
	 * @param length the length of the range to be styled
	 * @param attr the attribute describing the style of the range to be styled
	 */
	protected void addRange(Collection presentation, int offset, int length, TextAttribute attr) {
		
		Point range = getHighlighter().getTextViewer().getSelectedRange();
		int caret = range.x;
		int selection = range.y;
		IDocument doc = getDocument();
		Color bg = attr.getBackground();
		// all editors use same background color
//		// if current line highlight on, use line highlight color for background
//		if(JavaColorProvider.EDITOR_CURRENT_LINE && selection == 1) {
//			try {
//				IRegion lineRegion = doc.getLineInformation(doc.getLineOfOffset(caret));
//				if(lineRegion.getOffset()<=offset && offset<=lineRegion.getOffset()+lineRegion.getLength())
//					bg = EditorUtility.getColor(JavaColorProvider.EDITOR_CURRENT_LINE_COLOR);
//						
//			} catch (BadLocationException e) {
//				Logger.logException(e);
//			}
//		}
		
		presentation.add(new StyleRange(offset, length, attr.getForeground(), bg, attr.getStyle()));
	}

	protected void clearColors() {
		getTextAttributes().clear();
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
	protected TextAttribute getTokenTextAttribute(IToken token) {
		Object data = token.getData();
		if (data instanceof TextAttribute)
			return (TextAttribute) data;
		return fDefaultTextAttribute;
	}

	protected void loadColors() {
		clearColors();
		
		addTextAttribute(IStyleConstantsHTML.SCRIPT_AREA_BORDER);
		addTextAttribute(IStyleConstantsXML.TAG_ATTRIBUTE_NAME);
		addTextAttribute(IStyleConstantsXML.TAG_ATTRIBUTE_VALUE);
	}

	protected void handlePropertyChange(PropertyChangeEvent event) {
		String styleKey = null;

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
			}
		}

		if (styleKey != null) {
			// overwrite style preference with new value
			addTextAttribute(styleKey);
			super.handlePropertyChange(event);
		}
	}

	public boolean prepareRegions(ITypedRegion typedRegion, int ssssrequestedStart, int ssssrequestedLength, Collection holdResults) {
		boolean result = true;
		try {
			// ideally, eventually, we'll have a "virtualDocument" we can 
			// refer to, but for now ... we'll simple rescan the one region.
			// use simple adjustment (since "sub-content" starts at 0
			int offsetAdjustment = typedRegion.getOffset();
			String content = super.getDocument().get(typedRegion.getOffset(), typedRegion.getLength());
			IDocument document = new Document(content);
			this.setDocument(document);

			int lastStart = 0;
			int length = 0;
			IToken lastToken = Token.UNDEFINED;

			int remainingLength = typedRegion.getLength();
			fScanner.setRange(fDocument, lastStart, remainingLength);

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
	
	protected IPreferenceStore getColorPreferences() {
		return JSPUIPlugin.getDefault().getPreferenceStore();
	}
	
	protected void setDocument(IDocument document) {
		fDocument = document;
	}

	public void setTargetNode(org.w3c.dom.Node newTargetNode) {
		targetNode = newTargetNode;
	}
}
