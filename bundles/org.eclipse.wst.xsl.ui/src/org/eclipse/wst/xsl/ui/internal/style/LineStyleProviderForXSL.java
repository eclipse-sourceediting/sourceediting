/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 		David Carver (STAR) - initial api and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.style;

import java.util.Collection;
import java.util.HashMap;
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
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.sse.ui.internal.provisional.style.AbstractLineStyleProvider;
import org.eclipse.wst.sse.ui.internal.provisional.style.Highlighter;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.internal.provisional.style.ReconcilerHighlighter;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.style.IStyleConstantsXML;

/**
 * This implements a Syntax Line Style Provider for XSL. It leverages some
 * information from the XML Sytnax Coloring, but adds specific coloring for XSL
 * specific elements and attributes.
 * 
 * @author David Carver
 * @since 1.0
 * 
 */
public class LineStyleProviderForXSL extends AbstractLineStyleProvider
		implements LineStyleProvider {

	protected IStructuredDocument structuredDocument;
	protected Highlighter highlighter;
	private boolean initialized;
	protected PropertyChangeListener preferenceListener = new PropertyChangeListener();
	protected ReconcilerHighlighter recHighlighter = null;

	private IPreferenceStore xmlPreferenceStore = null;

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

				if (Debug.syntaxHighlighting && !handled) {
					System.out.println("not handled in prepareRegions"); //$NON-NLS-1$
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

	protected TextAttribute getAttributeFor(ITextRegionCollection collection,
			ITextRegion textRegion) {
		if (textRegion == null) {
			return (TextAttribute) XMLTextAttributeMap.getInstance()
					.getTextAttributeMap().get(IStyleConstantsXML.CDATA_TEXT);
		}

		String type = textRegion.getType();
		if (collection.getText().contains("xsl:")) {
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

		// addXSLTextAttribute(styleKey);
	}

	private class PropertyChangeListener implements IPropertyChangeListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org
		 * .eclipse.jface.util.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent event) {
			// have to do it this way so others can override the method
			handlePropertyChange(event);
		}
	}

	protected IPreferenceStore getXMLColorPreferences() {
		if (xmlPreferenceStore == null) {
			xmlPreferenceStore = XMLUIPlugin.getDefault().getPreferenceStore();
		}
		return xmlPreferenceStore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider#init
	 * (org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument,
	 * org.eclipse.wst.sse.ui.internal.provisional.style.Highlighter)
	 */
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
	public void init(IStructuredDocument structuredDocument,
			ReconcilerHighlighter highlighter) {
		this.structuredDocument = structuredDocument;
		recHighlighter = highlighter;

		if (isInitialized())
			return;

		registerPreferenceManager();

		setInitialized(true);
	}

	public void release() {
		unRegisterPreferenceManager();
		setInitialized(false);
	}

	@Override
	protected void unRegisterPreferenceManager() {
		// TODO: Implement listening for Preference Changes.
	}

	@Override
	protected void registerPreferenceManager() {
		// TODO: Implement listen for Preference Changes...does this belong
		// here, or elsewhere?
	}

	/**
	 * Returns the initialized.
	 * 
	 * @return boolean
	 */
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

	protected IStructuredDocument getDocument() {
		return structuredDocument;
	}

	/**
	 * This is now part of the TextAttributeMap classes, left here to override
	 * AbstractStyleClasses
	 */
	@Deprecated
	@Override
	protected TextAttribute createTextAttribute(RGB foreground, RGB background,
			boolean bold) {
		return null;
	}

	/**
	 * This is now part of the TextAttributeMap classes, left here to override
	 * AbstractStyleClasses
	 */
	@Deprecated
	@Override
	protected TextAttribute createTextAttribute(RGB foreground, RGB background,
			int style) {
		return new TextAttribute((foreground != null) ? EditorUtility
				.getColor(foreground) : null,
				(background != null) ? EditorUtility.getColor(background)
						: null, style);
	}

	@Override
	@Deprecated
	protected TextAttribute getAttributeFor(ITextRegion region) {
		return null;
	}

	@Override
	@Deprecated
	protected IPreferenceStore getColorPreferences() {
		return null;
	}

	@Override
	@Deprecated
	protected void loadColors() {

	}

}
