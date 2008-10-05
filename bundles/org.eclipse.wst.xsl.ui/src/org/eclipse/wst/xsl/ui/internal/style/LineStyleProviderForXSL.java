/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Benjamin Muskalla, b.muskalla@gmx.net - [158660] character entities should have their own syntax highlighting preference     
 *     
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.style;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.internal.provisional.style.AbstractLineStyleProvider;
import org.eclipse.wst.sse.ui.internal.provisional.style.Highlighter;
import org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider;
import org.eclipse.wst.sse.ui.internal.provisional.style.ReconcilerHighlighter;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.style.IStyleConstantsXML;
import org.eclipse.wst.xml.ui.internal.style.LineStyleProviderForXML;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;
import org.w3c.dom.Node;

/**
 * This implements a Syntax Line Style Provider for XSL.  It leverages some
 * information from the XML Sytnax Coloring, but adds specific coloring for 
 * XSL specific elements and attributes.
 * 
 * @author David Carver
 * @since 1.0
 *
 */
public class LineStyleProviderForXSL extends AbstractLineStyleProvider implements LineStyleProvider {
	
	protected IStructuredDocument structuredDocument;
	protected Highlighter highlighter;
	private boolean initialized;
	protected PropertyChangeListener preferenceListener = new PropertyChangeListener();

	protected ReconcilerHighlighter recHighlighter = null;
	
	private HashMap<String, TextAttribute> xslTextAttributes = null;
	private HashMap<String, TextAttribute> xmlTextAttributes = null;
	private HashMap<String,String> xmlRegionMap = null;
	private HashMap<String,String> xslRegionMap = null;
	
	
	private IPreferenceStore xslPreferenceStore = null;
	private IPreferenceStore xmlPreferenceStore = null;

	
	protected void commonInit(IStructuredDocument document, Highlighter highlighter) {

		structuredDocument = document;
		this.highlighter = highlighter;
	}
	
    /**
	 * this version does "trim" regions to match request
	 */
	private StyleRange createStyleRange(ITextRegionCollection flatNode, ITextRegion region, TextAttribute attr, int startOffset, int length) {	
		int start = flatNode.getStartOffset(region);
		if (start < startOffset)
			start = startOffset;
		
		// Base the text end offset off of the, possibly adjusted, start
		int textEnd = start + flatNode.getText(region).length();
		int maxOffset = startOffset + length;
		
		int end = flatNode.getEndOffset(region);
		// Use the end of the text in the region to avoid applying background color to trailing whitespace
		if(textEnd < end)
			end = textEnd;
		// instead of end-start?
		if (end > maxOffset)
			end = maxOffset;
		StyleRange result = new StyleRange(start, end - start, attr.getForeground(), attr.getBackground(), attr.getStyle());
		if((attr.getStyle() & TextAttribute.STRIKETHROUGH) != 0) {
			result.strikeout = true;
		}
		if((attr.getStyle() & TextAttribute.UNDERLINE) != 0) {
			result.underline = true;
		}
		return result;

	}
	
	protected TextAttribute getAttributeFor(ITextRegionCollection collection,
			ITextRegion region) {
		if (region == null) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.CDATA_TEXT);
		}
		
		String type = region.getType();
		if (collection.getText().contains("xsl:")) {
			return getXSLAttribute(type);
		}

		return getXMLAttribute(region);
	}

	/**
	 * @param type
	 */
	private TextAttribute getXSLAttribute(String type) {
		TextAttribute attribute = null;
		HashMap<String,String> regionMap = getXSLRegions();
		HashMap<String,TextAttribute> textAttributes = getXSLTextAttributes();
		
		if (regionMap.containsKey(type)) {
			attribute = textAttributes.get(regionMap.get(type));
		}
		return attribute;
	}
	
	protected HashMap<String,String> getXSLRegions() {
		if (xslRegionMap == null) {
			xslRegionMap = new HashMap<String,String>();
			loadXSLRegions();
		}
		return xslRegionMap;
	}
	
	protected void loadXSLRegions() {
		xslRegionMap.put(DOMRegionContext.XML_TAG_OPEN, IStyleConstantsXSL.TAG_BORDER);
		xslRegionMap.put(DOMRegionContext.XML_END_TAG_OPEN, IStyleConstantsXSL.TAG_BORDER);
		xslRegionMap.put(DOMRegionContext.XML_TAG_CLOSE, IStyleConstantsXSL.TAG_BORDER);
		xslRegionMap.put(DOMRegionContext.XML_EMPTY_TAG_CLOSE, IStyleConstantsXSL.TAG_BORDER);
		xslRegionMap.put(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME, IStyleConstantsXSL.TAG_ATTRIBUTE_NAME);
		xslRegionMap.put(DOMRegionContext.XML_TAG_NAME, IStyleConstantsXSL.TAG_NAME);
		xslRegionMap.put(DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE, IStyleConstantsXSL.TAG_ATTRIBUTE_VALUE);
	}
	

	protected HashMap<String,TextAttribute> getXSLTextAttributes() {
		if (xslTextAttributes == null) {
			xslTextAttributes = new HashMap<String,TextAttribute>();
			loadXSLColors();
		}
		return xslTextAttributes;
	}
	
	protected void loadXSLColors() {
		addXSLTextAttribute(IStyleConstantsXSL.TAG_NAME);
		addXSLTextAttribute(IStyleConstantsXSL.TAG_BORDER);
		addXSLTextAttribute(IStyleConstantsXSL.TAG_ATTRIBUTE_NAME);
		addXSLTextAttribute(IStyleConstantsXSL.TAG_ATTRIBUTE_VALUE);
	}
	
	
	
	/**
	 * Returns the hashtable containing all the text attributes for this line
	 * style provider. Lazily creates a hashtable if one has not already been
	 * created.
	 * 
	 * @return
	 */
	protected HashMap getXMLTextAttributes() {
		if (xmlTextAttributes == null) {
			xmlTextAttributes = new HashMap();
			loadXMLColors();
		}
		return xmlTextAttributes;
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

		addXSLTextAttribute(styleKey);
	}
	
	protected TextAttribute getXMLAttribute(ITextRegion region) {
		/**
		 * a method to centralize all the "format rules" for regions
		 * specifically associated for how to "open" the region.
		 */
		// not sure why this is coming through null, but just to catch it
		if (region == null) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.CDATA_TEXT);
		}
		String type = region.getType();
		if ((type == DOMRegionContext.XML_CONTENT) || (type == DOMRegionContext.XML_DOCTYPE_INTERNAL_SUBSET)) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.XML_CONTENT);
		}
		else if ((type == DOMRegionContext.XML_TAG_OPEN) || (type == DOMRegionContext.XML_END_TAG_OPEN) || (type == DOMRegionContext.XML_TAG_CLOSE) || (type == DOMRegionContext.XML_EMPTY_TAG_CLOSE)) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.TAG_BORDER);
		}
		else if ((type == DOMRegionContext.XML_CDATA_OPEN) || (type == DOMRegionContext.XML_CDATA_CLOSE)) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.CDATA_BORDER);
		}
		else if (type == DOMRegionContext.XML_CDATA_TEXT) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.CDATA_TEXT);
		}
		else if (type == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.TAG_ATTRIBUTE_NAME);
		}
		else if (type == DOMRegionContext.XML_DOCTYPE_DECLARATION) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.TAG_NAME);
		}
		else if (type == DOMRegionContext.XML_TAG_NAME) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.TAG_NAME);
		}
		else if ((type == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE)) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.TAG_ATTRIBUTE_VALUE);
		}
		else if (type == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.TAG_ATTRIBUTE_EQUALS);
		}
		else if ((type == DOMRegionContext.XML_COMMENT_OPEN) || (type == DOMRegionContext.XML_COMMENT_CLOSE)) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.COMMENT_BORDER);
		}
		else if (type == DOMRegionContext.XML_COMMENT_TEXT) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.COMMENT_TEXT);
		}
		else if (type == DOMRegionContext.XML_DOCTYPE_NAME) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.DOCTYPE_NAME);
		}
		else if ((type == DOMRegionContext.XML_CHAR_REFERENCE) || (type == DOMRegionContext.XML_ENTITY_REFERENCE) || (type == DOMRegionContext.XML_PE_REFERENCE)) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.ENTITY_REFERENCE);
		}
		else if (type == DOMRegionContext.XML_PI_CONTENT) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.PI_CONTENT);
		}
		else if ((type == DOMRegionContext.XML_PI_OPEN) || (type == DOMRegionContext.XML_PI_CLOSE)) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.PI_BORDER);
		}
		else if ((type == DOMRegionContext.XML_DECLARATION_OPEN) || (type == DOMRegionContext.XML_DECLARATION_CLOSE)) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.DECL_BORDER);
		}
		else if (type == DOMRegionContext.XML_DOCTYPE_EXTERNAL_ID_SYSREF) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_SYSREF);
		}
		else if (type == DOMRegionContext.XML_DOCTYPE_EXTERNAL_ID_PUBREF) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_PUBREF);
		}
		else if ((type == DOMRegionContext.XML_DOCTYPE_EXTERNAL_ID_PUBLIC) || (type == DOMRegionContext.XML_DOCTYPE_EXTERNAL_ID_SYSTEM)) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID);
		}
		else if (type == DOMRegionContext.UNDEFINED) {
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.CDATA_TEXT);
		}
		else if (type == DOMRegionContext.WHITE_SPACE) {
			// white space is normall not on its own ... but when it is, we'll
			// treat as content
			return (TextAttribute) getXMLTextAttributes().get(IStyleConstantsXML.XML_CONTENT);
		}
		return null;
	}
	


	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider#prepareRegions(org.eclipse.jface.text.ITypedRegion, int, int, java.util.Collection)
	 */
	public boolean prepareRegions(ITypedRegion typedRegion, int lineRequestStart, int lineRequestLength, Collection holdResults) {
		final int partitionStartOffset = typedRegion.getOffset();
		final int partitionLength = typedRegion.getLength();
		IStructuredDocumentRegion structuredDocumentRegion = getDocument().getRegionAtCharacterOffset(partitionStartOffset);
		boolean handled = false;

		handled = prepareTextRegions(structuredDocumentRegion, partitionStartOffset, partitionLength, holdResults);

		return handled;
	}
	
	/**
	 * @param region
	 * @param start
	 * @param length
	 * @param holdResults
	 * @return
	 */
	protected boolean prepareTextRegion(ITextRegionCollection blockedRegion, int partitionStartOffset, int partitionLength, Collection holdResults) {
		boolean handled = false;
		final int partitionEndOffset = partitionStartOffset + partitionLength - 1;
		ITextRegion region = null;
		ITextRegionList regions = blockedRegion.getRegions();
		int nRegions = regions.size();
		StyleRange styleRange = null;
		for (int i = 0; i < nRegions; i++) {
			region = regions.get(i);
			TextAttribute attr = null;
			TextAttribute previousAttr = null;
			if (blockedRegion.getStartOffset(region) > partitionEndOffset)
				break;
			if (blockedRegion.getEndOffset(region) <= partitionStartOffset)
				continue;

			if (region instanceof ITextRegionCollection) {
				handled = prepareTextRegion((ITextRegionCollection) region, partitionStartOffset, partitionLength, holdResults);
			} else {

				attr = getAttributeFor(blockedRegion, region);
				if (attr != null) {
					handled = true;
					// if this region's attr is the same as previous one, then
					// just adjust the previous style range
					// instead of creating a new instance of one
					// note: to use 'equals' in this case is important, since
					// sometimes
					// different instances of attributes are associated with a
					// region, even the
					// the attribute has the same values.
					// TODO: this needs to be improved to handle readonly
					// regions correctly
					if ((styleRange != null) && (previousAttr != null) && (previousAttr.equals(attr))) {
						styleRange.length += region.getLength();
					} else {
						styleRange = createStyleRange(blockedRegion, region, attr, partitionStartOffset, partitionLength);
						holdResults.add(styleRange);
						// technically speaking, we don't need to update
						// previousAttr
						// in the other case, because the other case is when
						// it hasn't changed
						previousAttr = attr;
					}
				} else {
					previousAttr = null;
				}
			}
		}
		return handled;
	}
	
	protected boolean prepareTextRegions(IStructuredDocumentRegion structuredDocumentRegion, int partitionStartOffset, int partitionLength, Collection holdResults) {
		boolean handled = false;
		final int partitionEndOffset = partitionStartOffset + partitionLength - 1;
		while (structuredDocumentRegion != null && structuredDocumentRegion.getStartOffset() <= partitionEndOffset) {
			ITextRegion region = null;
			ITextRegionList regions = structuredDocumentRegion.getRegions();
			int nRegions = regions.size();
			StyleRange styleRange = null;
			for (int i = 0; i < nRegions; i++) {
				region = regions.get(i);
				TextAttribute attr = null;
				TextAttribute previousAttr = null;
				if (structuredDocumentRegion.getStartOffset(region) > partitionEndOffset)
					break;
				if (structuredDocumentRegion.getEndOffset(region) <= partitionStartOffset)
					continue;

				if (region instanceof ITextRegionCollection) {
					boolean handledCollection = (prepareTextRegion((ITextRegionCollection) region, partitionStartOffset, partitionLength, holdResults));
					handled = (!handled) ? handledCollection : handled;
				} else {

					attr = getAttributeFor(structuredDocumentRegion, region);
					if (attr != null) {
						handled = true;
						// if this region's attr is the same as previous one,
						// then just adjust the previous style range
						// instead of creating a new instance of one
						// note: to use 'equals' in this case is important,
						// since sometimes
						// different instances of attributes are associated
						// with a region, even the
						// the attribute has the same values.
						// TODO: this needs to be improved to handle readonly
						// regions correctly
						if ((styleRange != null) && (previousAttr != null) && (previousAttr.equals(attr))) {
							styleRange.length += region.getLength();
						} else {
							styleRange = createStyleRange(structuredDocumentRegion, region, attr, partitionStartOffset, partitionLength);
							holdResults.add(styleRange);
							// technically speaking, we don't need to update
							// previousAttr
							// in the other case, because the other case is
							// when it hasn't changed
							previousAttr = attr;
						}
					} else {
						previousAttr = null;
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

	protected void loadXMLColors() {
		addXMLTextAttribute(IStyleConstantsXML.TAG_NAME);
		addXMLTextAttribute(IStyleConstantsXML.TAG_BORDER);
		addXMLTextAttribute(IStyleConstantsXML.TAG_ATTRIBUTE_NAME);
		addXMLTextAttribute(IStyleConstantsXML.TAG_ATTRIBUTE_VALUE);
		addXMLTextAttribute(IStyleConstantsXML.TAG_ATTRIBUTE_EQUALS);
		addXMLTextAttribute(IStyleConstantsXML.COMMENT_BORDER);
		addXMLTextAttribute(IStyleConstantsXML.COMMENT_TEXT);
		addXMLTextAttribute(IStyleConstantsXML.CDATA_BORDER);
		addXMLTextAttribute(IStyleConstantsXML.CDATA_TEXT);
		addXMLTextAttribute(IStyleConstantsXML.DECL_BORDER);
		addXMLTextAttribute(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID);
		addXMLTextAttribute(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_PUBREF);
		addXMLTextAttribute(IStyleConstantsXML.DOCTYPE_EXTERNAL_ID_SYSREF);
		addXMLTextAttribute(IStyleConstantsXML.DOCTYPE_NAME);
		addXMLTextAttribute(IStyleConstantsXML.PI_CONTENT);
		addXMLTextAttribute(IStyleConstantsXML.PI_BORDER);
		addXMLTextAttribute(IStyleConstantsXML.XML_CONTENT);
		addXMLTextAttribute(IStyleConstantsXML.ENTITY_REFERENCE);
	}

	protected void addXSLTextAttribute(String colorKey) {
		if (getXSLColorPreferences() != null) {
			String prefString = getXSLColorPreferences().getString(colorKey);
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

				TextAttribute createTextAttribute = createTextAttribute(foreground, background, style);
				getXSLTextAttributes().put(colorKey, createTextAttribute);
			}
		}
	}
	
	protected void addXMLTextAttribute(String colorKey) {
		if (getXMLColorPreferences() != null) {
			String prefString = getXMLColorPreferences().getString(colorKey);
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

				TextAttribute createTextAttribute = createTextAttribute(foreground, background, style);
				getXMLTextAttributes().put(colorKey, createTextAttribute);
			}
		}
	}
	
	
	protected IPreferenceStore getXSLColorPreferences() {
		if (xslPreferenceStore == null) {
			xslPreferenceStore = XSLUIPlugin.getDefault().getPreferenceStore();
		}
		return xslPreferenceStore;
	}
	
	protected IPreferenceStore getXMLColorPreferences() {
		if (xmlPreferenceStore == null) {
			xmlPreferenceStore = XMLUIPlugin.getDefault().getPreferenceStore();
		}
		return xmlPreferenceStore;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.internal.provisional.style.LineStyleProvider#init(org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument, org.eclipse.wst.sse.ui.internal.provisional.style.Highlighter)
	 */
	public void init(IStructuredDocument document, Highlighter highlighter) {
		commonInit(structuredDocument, highlighter);

		if (isInitialized())
			return;

		registerPreferenceManager();

		setInitialized(true);
	}

	public void release() {
		unRegisterPreferenceManager();
		if (xslTextAttributes != null) {
			xslTextAttributes.clear();
			xslTextAttributes = null;
		}
		if (xmlTextAttributes != null) {
			xmlTextAttributes.clear();
			xmlTextAttributes = null;
		}
		setInitialized(false);		
	}
	
	protected void unRegisterPreferenceManager() {
		IPreferenceStore xslPref = getXSLColorPreferences();
		if (xslPref != null) {
			xslPref.removePropertyChangeListener(preferenceListener);
		}
	}	
	
	public void init(IStructuredDocument structuredDocument, ISourceViewer sourceViewer) {
		init(structuredDocument, (Highlighter) null);
	}
	
	public void init(IStructuredDocument structuredDocument, ReconcilerHighlighter highlighter)	{
		this.structuredDocument = structuredDocument;
		recHighlighter = highlighter;
		
		if(isInitialized())
			return;
		
		registerPreferenceManager();
		
		setInitialized(true);
	}
	
	
	protected void registerPreferenceManager() {
		IPreferenceStore xslPref = getXSLColorPreferences();
		if (xslPref != null) {
			xslPref.addPropertyChangeListener(preferenceListener);
		}
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
	
	protected TextAttribute createTextAttribute(RGB foreground, RGB background, boolean bold) {
		return new TextAttribute((foreground != null) ? EditorUtility.getColor(foreground) : null, (background != null) ? EditorUtility.getColor(background) : null, bold ? SWT.BOLD : SWT.NORMAL);
	}

	protected TextAttribute createTextAttribute(RGB foreground, RGB background, int style) {
		return new TextAttribute((foreground != null) ? EditorUtility.getColor(foreground) : null, (background != null) ? EditorUtility.getColor(background) : null, style);
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
