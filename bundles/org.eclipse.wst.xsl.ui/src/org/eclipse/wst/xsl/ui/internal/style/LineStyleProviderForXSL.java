/*******************************************************************************
 * Copyright (c) 2008, 2009 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 		David Carver (STAR) - initial api and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.style;

import java.util.Collection;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.internal.provisional.style.AbstractLineStyleProvider;
import org.eclipse.wst.sse.ui.internal.provisional.style.Highlighter;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.internal.provisional.style.ReconcilerHighlighter;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.style.IStyleConstantsXML;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;

/**
 * This implements a Syntax Line Style Provider for XSL. It leverages some
 * information from the XML Syntax Coloring, but adds specific coloring for XSL
 * specific elements and attributes.
 * 
 * @author David Carver
 * @since 1.0
 * @deprecated Use AbstractXSLSemanticHighlighting and the SemanticHighlighting extension point
 */
@Deprecated
public class LineStyleProviderForXSL extends AbstractLineStyleProvider implements LineStyleProvider {

	protected IStructuredDocument structuredDocument;
	protected Highlighter highlighter;
	private boolean initialized;
	protected ReconcilerHighlighter recHighlighter = null;

	private IPreferenceStore xmlPreferenceStore = null;
	private IPreferenceStore xslPreferenceStore = null;
	private IPreferenceStore combinedPreferenceStore = null;
	private IPropertyChangeListener preferenceListener  = new PropertyChangeListener();
	
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

	@Override
	protected void commonInit(IStructuredDocument document,
			Highlighter highlighter) {

		structuredDocument = document;
		this.highlighter = highlighter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider#
	 * prepareRegions(org.eclipse.jface.text.ITypedRegion, int, int,
	 * java.util.Collection)
	 */
	@Override
	public boolean prepareRegions(ITypedRegion typedRegion,
			int lineRequestStart, int lineRequestLength, Collection holdResults) {
		final int partitionStartOffset = typedRegion.getOffset();
		final int partitionLength = typedRegion.getLength();
		IStructuredDocumentRegion structuredDocumentRegion = getDocument()
				.getRegionAtCharacterOffset(partitionStartOffset);
		boolean handled = false;

		handled = prepareTextRegions(structuredDocumentRegion,
				partitionStartOffset, partitionLength, holdResults);

		return handled;
	}

	/**
	 * @param region
	 * @param start
	 * @param length
	 * @param holdResults
	 * @return
	 */
	protected boolean prepareTextRegion(ITextRegionCollection blockedRegion,
			int partitionStartOffset, int partitionLength,
			Collection holdResults) {
		boolean handled = false;
		final int partitionEndOffset = partitionStartOffset + partitionLength
				- 1;
		ITextRegion region = null;
		ITextRegionList regions = blockedRegion.getRegions();
		StyleRange styleRange = null;

		for (int i = 0; i < regions.size(); i++) {
			region = regions.get(i);
			TextAttribute attr = null;
			TextAttribute previousAttr = null;
			if (blockedRegion.getStartOffset(region) > partitionEndOffset)
				break;
			if (blockedRegion.getEndOffset(region) <= partitionStartOffset)
				continue;

			if (region instanceof ITextRegionCollection) {
				handled = prepareTextRegion((ITextRegionCollection) region,
						partitionStartOffset, partitionLength, holdResults);
			} else {

				attr = getAttributeFor(blockedRegion, region);
				if (attr != null) {
					handled = true;
					styleRange = applyStyleRange(blockedRegion,
							partitionStartOffset, partitionLength, holdResults,
							region, styleRange, attr, previousAttr);
				} else {
					previousAttr = null;
				}
			}
		}
		return handled;
	}

	private StyleRange applyStyleRange(ITextRegionCollection blockedRegion,
			int partitionStartOffset, int partitionLength,
			Collection holdResults, ITextRegion region, StyleRange styleRange,
			TextAttribute attr, TextAttribute previousAttr) {
		if (equalsPreviousAttribute(styleRange, attr, previousAttr)) {
			styleRange.length += region.getLength();
		} else {
			styleRange = createStyleRange(blockedRegion, region, attr,
					partitionStartOffset, partitionLength);
			holdResults.add(styleRange);
			previousAttr = attr;
		}
		return styleRange;
	}

	private boolean equalsPreviousAttribute(StyleRange styleRange,
			TextAttribute attr, TextAttribute previousAttr) {
		return (styleRange != null) && (previousAttr != null)
				&& (previousAttr.equals(attr));
	}

	protected boolean prepareTextRegions(
			IStructuredDocumentRegion structuredDocumentRegion,
			int partitionStartOffset, int partitionLength,
			Collection holdResults) {
		boolean handled = false;
		final int partitionEndOffset = partitionStartOffset + partitionLength
				- 1;
		while (structuredDocumentRegion != null
				&& structuredDocumentRegion.getStartOffset() <= partitionEndOffset) {
			ITextRegion region = null;
			ITextRegionList regions = structuredDocumentRegion.getRegions();

			StyleRange styleRange = null;
			for (int i = 0; i < regions.size(); i++) {
				region = regions.get(i);
				TextAttribute attr = null;
				TextAttribute previousAttr = null;
				if (structuredDocumentRegion.getStartOffset(region) > partitionEndOffset)
					break;
				if (structuredDocumentRegion.getEndOffset(region) <= partitionStartOffset)
					continue;

				if (region instanceof ITextRegionCollection) {
					boolean handledCollection = (prepareTextRegion(
							(ITextRegionCollection) region,
							partitionStartOffset, partitionLength, holdResults));
					handled = (!handled) ? handledCollection : handled;
				} else {
					attr = getAttributeFor(structuredDocumentRegion, region);
					if (attr == null) {
						previousAttr = null;
					} else {
						handled = true;
						styleRange = applyStyleRange(structuredDocumentRegion,
								partitionStartOffset, partitionLength,
								holdResults, region, styleRange, attr,
								previousAttr);
					}
				}
			}
			structuredDocumentRegion = structuredDocumentRegion.getNext();
		}
		return handled;
	}

	private StyleRange createStyleRange(
			ITextRegionCollection textRegionCollection, ITextRegion textRegion,
			TextAttribute attr, int startOffset, int length) {
		int startingOffset = textRegionCollection.getStartOffset(textRegion);
		if (startingOffset < startOffset)
			startingOffset = startOffset;

		int textEnd = startingOffset
				+ textRegionCollection.getText(textRegion).length();
		int maxOffset = startOffset + length;
		int endingOffset = textRegionCollection.getEndOffset(textRegion);

		if (textEnd < endingOffset)
			endingOffset = textEnd;
		if (endingOffset > maxOffset)
			endingOffset = maxOffset;
		StyleRange result = new StyleRange(startingOffset, endingOffset
				- startingOffset, attr.getForeground(), attr.getBackground(),
				attr.getStyle());
		if ((attr.getStyle() & TextAttribute.STRIKETHROUGH) != 0) {
			result.strikeout = true;
		}
		if ((attr.getStyle() & TextAttribute.UNDERLINE) != 0) {
			result.underline = true;
		}
		return result;

	}

	@Override
	protected TextAttribute getAttributeFor(ITextRegionCollection collection,
			ITextRegion textRegion) {
		if (textRegion == null) {
			return XMLTextAttributeMap.getInstance()
					.getTextAttributeMap().get(IStyleConstantsXML.CDATA_TEXT);
		}

		String type = textRegion.getType();
		if (collection.getText().contains("xsl:")) { //$NON-NLS-1$
			return getXSLAttribute(type);
		}

		return getXMLAttribute(type);
	}

	private TextAttribute getXSLAttribute(String type) {
		Map<String, String> regionMap = XSLRegionMap.getInstance()
				.getRegionMap();
		Map<String, TextAttribute> textAttributes = XSLTextAttributeMap
				.getInstance().getTextAttributeMap();
		return getTextAttribute(type, regionMap, textAttributes);
	}

	private TextAttribute getXMLAttribute(String type) {
		Map<String, String> regionMap = XMLRegionMap.getInstance()
				.getRegionMap();
		Map<String, TextAttribute> textAttributes = XMLTextAttributeMap
				.getInstance().getTextAttributeMap();

		return getTextAttribute(type, regionMap, textAttributes);
	}

	private TextAttribute getTextAttribute(String type,
			Map<String, String> regionMap,
			Map<String, TextAttribute> textAttrMap) {
		return textAttrMap.get(regionMap.get(type));
	}

	@Override
	protected void handlePropertyChange(PropertyChangeEvent event) {
		String styleKey = null;
		if (event == null)
			return;

		String prefKey = event.getProperty();
		// check if preference changed is a style preference
		if (IStyleConstantsXSL.TAG_NAME.equals(prefKey)) {
			styleKey = IStyleConstantsXSL.TAG_NAME;
		}
		if (IStyleConstantsXSL.TAG_BORDER.equals(prefKey)) {
			styleKey = IStyleConstantsXSL.TAG_BORDER;
		}
		if (IStyleConstantsXSL.TAG_ATTRIBUTE_NAME.equals(prefKey)) {
			styleKey = IStyleConstantsXSL.TAG_ATTRIBUTE_NAME;
		}
		if (IStyleConstantsXSL.TAG_ATTRIBUTE_VALUE.equals(prefKey)) {
			styleKey = IStyleConstantsXSL.TAG_ATTRIBUTE_VALUE;
		}
		if (styleKey == null)
			return;

		if (styleKey != null) {
			// overwrite style preference with new value
			addTextAttribute(styleKey);
		}
		
		if(recHighlighter != null)
			recHighlighter.refreshDisplay();
		
	}
	
	/**
	 * Looks up the colorKey in the preference store and adds the style
	 * information to list of TextAttributes
	 * 
	 * @param colorKey
	 */
	@Override
	protected void addTextAttribute(String colorKey) {
		if (getColorPreferences() != null) {
			String prefString = getColorPreferences().getString(colorKey);
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

				updateTextAttribute(colorKey, foreground, background, style);
			}
		}
	}

	private void updateTextAttribute(String colorKey, RGB foreground,
			RGB background, int style) {
		TextAttribute createTextAttribute = createTextAttribute(foreground, background, style);
		
		TextAttribute textAttribute =
			XSLTextAttributeMap.getInstance().getTextAttributeMap().get(colorKey);
		if (textAttribute != null) {
			XSLTextAttributeMap.getInstance().getTextAttributeMap().put(colorKey, createTextAttribute);
			return;
		}
		
		textAttribute =
				XMLTextAttributeMap.getInstance().getTextAttributeMap().get(colorKey);
		if (textAttribute != null) {
			XMLTextAttributeMap.getInstance().getTextAttributeMap().put(colorKey, createTextAttribute);
		}
	}	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider#init
	 * (org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument,
	 * org.eclipse.wst.sse.ui.internal.provisional.style.Highlighter)
	 */
	@Override
	public void init(IStructuredDocument document, Highlighter highlighter) {
		commonInit(structuredDocument, highlighter);

		if (isInitialized())
			return;

		registerPreferenceManager();

		setInitialized(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.sse.ui.internal.provisional.style.AbstractLineStyleProvider
	 * #
	 * init(org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument
	 * , org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	public void init(IStructuredDocument structuredDocument,
			ISourceViewer sourceViewer) {
		init(structuredDocument, (Highlighter) null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.sse.ui.internal.provisional.style.AbstractLineStyleProvider
	 * #
	 * init(org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument
	 * ,
	 * org.eclipse.wst.sse.ui.internal.provisional.style.ReconcilerHighlighter)
	 */
	@Override
	public void init(IStructuredDocument structuredDocument,
			ReconcilerHighlighter highlighter) {
		this.structuredDocument = structuredDocument;
		recHighlighter = highlighter;

		if (isInitialized())
			return;

		registerPreferenceManager();

		setInitialized(true);
	}

	@Override
	public void release() {
		unRegisterPreferenceManager();
		setInitialized(false);
	}

	@Override
	protected void unRegisterPreferenceManager() {
		IPreferenceStore pref = getColorPreferences();
		if (pref != null) {
			pref.removePropertyChangeListener(preferenceListener);
		}
	}

	@Override
	protected void registerPreferenceManager() {
		IPreferenceStore pref = getColorPreferences();
		if (pref != null) {
			pref.addPropertyChangeListener(preferenceListener  );
		}
	}

	/**
	 * Returns the initialized.
	 * 
	 * @return boolean
	 */
	@Override
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Sets the initialized.
	 * 
	 * @param initialized
	 *            The initialized to set
	 */
	private void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	@Override
	protected IStructuredDocument getDocument() {
		return structuredDocument;
	}

	/**
	 */
	@Override
	protected Highlighter getHighlighter() {
		return highlighter;
	}
	
	@Override
	protected IPreferenceStore getColorPreferences() {
		if (xmlPreferenceStore == null) {
			xmlPreferenceStore = XMLUIPlugin.getDefault().getPreferenceStore();
		}
		if (xslPreferenceStore == null) {
			xslPreferenceStore = XSLUIPlugin.getDefault().getPreferenceStore();
		}
		combinedPreferenceStore = new ChainedPreferenceStore(new IPreferenceStore[] { xmlPreferenceStore, xslPreferenceStore });
		return combinedPreferenceStore;
	}

	@Override
	protected TextAttribute createTextAttribute(RGB foreground, RGB background, boolean bold) {
		return new TextAttribute((foreground != null) ? EditorUtility.getColor(foreground) : null, (background != null) ? EditorUtility.getColor(background) : null, bold ? SWT.BOLD : SWT.NORMAL);
	}

	@Override
	protected TextAttribute createTextAttribute(RGB foreground, RGB background, int style) {
		return new TextAttribute((foreground != null) ? EditorUtility.getColor(foreground) : null, (background != null) ? EditorUtility.getColor(background) : null, style);
	}

	@Override
	protected TextAttribute getAttributeFor(ITextRegion region) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void loadColors() {
		// TODO Auto-generated method stub
		
	}
	
}
