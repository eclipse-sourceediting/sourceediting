/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.ui.style;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wst.sse.core.preferences.PreferenceChangeListener;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.core.util.Debug;
import org.eclipse.wst.sse.ui.preferences.PreferenceManager;
import org.eclipse.wst.sse.ui.util.EditorUtility;


public abstract class AbstractLineStyleProvider implements PreferenceChangeListener {
	/** Contains all text attributes pretaining to this line style provider*/
	private HashMap fTextAttributes = null;
	
	// had to make this not final, or got in to infinite recursion during class loading in VAJava!
	// (must be a VAJava thing)
	private LineStyleProvider defaultAttributeProvider = null;
	private IStructuredDocument fDocument;
	private Highlighter fHighlighter;
	private boolean initialized;

	//protected IStructuredDocumentRegion currentStructuredDocumentRegion;
	// Note: the var=x.class contructs were put into this method
	// as a workaround for slow VAJava class lookups. They compiler
	// assigns them to a variable in the JDK, but Class.forName("x")
	// in VAJava. It is workaround specific for VAJava environment, so could
	// be simplified in future.
	static Class LineStyleProviderClass = LineStyleProvider.class;

	// we keep track of LogMessage to avoid writing hundreds of messages,
	// but still give a hint that something is wrong with attributeProviders and/or regions.
	// It's only written in the case of a program error, but there's no use adding
	// salt to the wound.
	//private boolean wroteOneLogMessage;
	/**
	 */
	protected AbstractLineStyleProvider() {
	}

	protected void addEmptyRange(int start, int length, Collection holdResults) {
		StyleRange result = new StyleRange();
		result.start = start;
		result.length = length;
		holdResults.add(result);
	}

	/**
	 * this version does "trim" regions to match request 
	 */
	protected StyleRange createStyleRange(ITextRegionCollection flatNode, ITextRegion region, TextAttribute attr, int startOffset, int length) {

		int start = flatNode.getStartOffset(region);
		if (start < startOffset)
			start = startOffset;
		int maxOffset = startOffset + length;
		int end = flatNode.getEndOffset(region); // use get length directly instead of end-start?
		if (end > maxOffset)
			end = maxOffset;
		StyleRange result = new StyleRange(start, end - start, attr.getForeground(), attr.getBackground(), attr.getStyle());
		return result;

	}

	protected TextAttribute createTextAttribute(RGB foreground, RGB background, boolean bold) {
		return new TextAttribute((foreground != null) ? EditorUtility.getColor(foreground) : null, (background != null) ? EditorUtility.getColor(background) : null, bold ? SWT.BOLD : SWT.NORMAL);
	}

	protected TextAttribute getAttributeFor(ITextRegion region) {
		// should be "abstract" method
		return null;
	}

	abstract protected PreferenceManager getColorManager();

	/**
	 * See also Highligher::getTextAttributeProvider
	 */
	protected LineStyleProvider getDefaultLineStyleProvider() {
		if (defaultAttributeProvider == null) {
			defaultAttributeProvider = new LineStyleProviderForNoOp();
		}
		return defaultAttributeProvider;
	}

	/**
	 */
	protected Highlighter getHighlighter() {
		return fHighlighter;
	}

	public void init(IStructuredDocument structuredDocument, Highlighter highlighter) {

		commonInit(structuredDocument, highlighter);

		if (isInitialized())
			return;

		registerPreferenceManager();

		setInitialized(true);
	}

	protected void commonInit(IStructuredDocument document, Highlighter highlighter) {

		fDocument = document;
		fHighlighter = highlighter;
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type
	 * allows it to return true in more than one case.
	 */
	public boolean isAdapterForType(java.lang.Object type) {
		return type == LineStyleProviderClass;
	}

	public void preferencesChanged() {
		// force a full update of the text viewer
		fHighlighter.refreshDisplay();
	}

	public boolean prepareRegions(ITypedRegion typedRegion, int lineRequestStart, int lineRequestLength, Collection holdResults) {
		final int partitionStartOffset = typedRegion.getOffset();
		final int partitionLength = typedRegion.getLength();
		IStructuredDocumentRegion structuredDocumentRegion = getDocument().getRegionAtCharacterOffset(partitionStartOffset);
		boolean handled = false;

		handled = prepareTextRegions(structuredDocumentRegion, partitionStartOffset, partitionLength, holdResults);

		return handled;
	}

	private boolean prepareTextRegions(IStructuredDocumentRegion structuredDocumentRegion, int partitionStartOffset, int partitionLength, Collection holdResults) {
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
					handled = prepareTextRegion((ITextRegionCollection) region, partitionStartOffset, partitionLength, holdResults);
				}
				else {

					attr = getAttributeFor(region);
					if (attr != null) {
						handled = true;
						// if this region's attr is the same as previous one, then just adjust the previous style range
						// instead of creating a new instance of one
						// note: to use 'equals' in this case is important, since sometimes
						// different instances of attributes are associated with a region, even the
						// the attribute has the same values.
						// TODO: this needs to be improved to handle readonly regions correctly
						if ((styleRange != null) && (previousAttr != null) && (previousAttr.equals(attr))) {
							styleRange.length += region.getLength();
						}
						else {
							styleRange = createStyleRange(structuredDocumentRegion, region, attr, partitionStartOffset, partitionLength);
							holdResults.add(styleRange);
							// technically speaking, we don't need to update previousAttr
							// in the other case, because the other case is when it hasn't changed
							previousAttr = attr;
						}
					}
					else {
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

	/**
	 * @param region
	 * @param start
	 * @param length
	 * @param holdResults
	 * @return
	 */
	private boolean prepareTextRegion(ITextRegionCollection blockedRegion, int partitionStartOffset, int partitionLength, Collection holdResults) {
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
			}
			else {

				attr = getAttributeFor(region);
				if (attr != null) {
					handled = true;
					// if this region's attr is the same as previous one, then just adjust the previous style range
					// instead of creating a new instance of one
					// note: to use 'equals' in this case is important, since sometimes
					// different instances of attributes are associated with a region, even the
					// the attribute has the same values.
					// TODO: this needs to be improved to handle readonly regions correctly
					if ((styleRange != null) && (previousAttr != null) && (previousAttr.equals(attr))) {
						styleRange.length += region.getLength();
					}
					else {
						styleRange = createStyleRange(blockedRegion, region, attr, partitionStartOffset, partitionLength);
						holdResults.add(styleRange);
						// technically speaking, we don't need to update previousAttr
						// in the other case, because the other case is when it hasn't changed
						previousAttr = attr;
					}
				}
				else {
					previousAttr = null;
				}
			}
		}
		return handled;
	}

	protected void registerPreferenceManager() {
		PreferenceManager mgr = getColorManager();
		if (mgr != null) {
			mgr.addPreferenceChangeListener(this);
		}
	}

	public void release() {
		unRegisterPreferenceManager();
		getTextAttributes().clear();
	}

	protected void unRegisterPreferenceManager() {
		PreferenceManager mgr = getColorManager();
		if (mgr != null) {
			mgr.removePreferenceChangeListener(this);
		}
	}

	/**
	 * Returns the initialized.
	 * @return boolean
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Sets the initialized.
	 * @param initialized The initialized to set
	 */
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	protected IStructuredDocument getDocument() {
		return fDocument;
	}
	/**
	 * Returns the hashtable containing all the text attributes for this line style provider.
	 * Lazily creates a hashtable if one has not already been created.
	 * @return
	 */
	protected HashMap getTextAttributes() {
		if (fTextAttributes == null) {
			fTextAttributes = new HashMap();
		}
		return fTextAttributes;
	}
}
