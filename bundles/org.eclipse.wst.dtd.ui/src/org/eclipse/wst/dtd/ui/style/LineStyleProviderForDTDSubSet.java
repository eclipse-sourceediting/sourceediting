/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.style;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wst.common.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.dtd.core.rules.StructuredTextPartitionerForDTD;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.ui.EditorPlugin;
import org.eclipse.wst.sse.ui.preferences.PreferenceKeyGenerator;
import org.eclipse.wst.sse.ui.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.style.AbstractLineStyleProvider;
import org.eclipse.wst.sse.ui.style.LineStyleProvider;


public class LineStyleProviderForDTDSubSet extends AbstractLineStyleProvider implements LineStyleProvider {
	private int fInternalAdjustment;

	private IStructuredModel fInternalModel = null;
	private LineStyleProviderForDTD fInternalProvider = null;
	private StyleRange[] fInternalRanges;

	public LineStyleProviderForDTDSubSet() {

		super();
		fInternalProvider = new LineStyleProviderForDTD();
		fInternalRanges = new StyleRange[0];
		fInternalAdjustment = 0;
	}

	/**
	 * @param lineRequestStart
	 * @param lineRequestLength
	 * @param holdResults
	 */
	private void addStyleRanges(int lineRequestStart, int lineRequestLength, Collection holdResults) {

	}

	protected TextAttribute getAttributeFor(ITextRegion region) {
		TextAttribute ta = null;

		String prefString = getColorPreferences().getString(getPreferenceKey(IStyleConstantsDTD.DTD_DEFAULT));
		String[] stylePrefs = ColorHelper.unpackStylePreferences(prefString);
		if (stylePrefs != null) {
			RGB foreground = ColorHelper.toRGB(stylePrefs[0]);
			RGB background = ColorHelper.toRGB(stylePrefs[1]);
			boolean bold = Boolean.valueOf(stylePrefs[2]).booleanValue();
			ta = createTextAttribute(foreground, background, bold);
		}
		return ta;
	}

	protected IPreferenceStore getColorPreferences() {
		return EditorPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * @return
	 */
	private IStructuredDocument getInternalDocument() {
		if (fInternalModel == null) {
			fInternalModel = getModelManager().createUnManagedStructuredModelFor(IContentTypeIdentifier.ContentTypeID_DTD);
		}
		return fInternalModel.getStructuredDocument();
	}

	private IModelManager getModelManager() {
		return StructuredModelManager.getInstance().getModelManager();
	}

	protected String getPreferenceKey(String key) {
		String contentTypeId = IContentTypeIdentifier.ContentTypeID_DTD;
		return PreferenceKeyGenerator.generateKey(key, contentTypeId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.structured.style.AbstractLineStyleProvider#prepareRegions(org.eclipse.jface.text.ITypedRegion,
	 *      int, int, java.util.Collection)
	 */
	public boolean prepareRegions(ITypedRegion typedRegion, int lineRequestStart, int lineRequestLength, Collection holdResults) {

		if (!StructuredTextPartitionerForDTD.ST_DTD_DEFAULT.equals(typedRegion.getType())) {
			// compute an internal DTD model and return linestyles for it
			ITextRegion dtdContentRegion = null;
			IStructuredDocumentRegion doctype = getDocument().getRegionAtCharacterOffset(typedRegion.getOffset());
			if (doctype != null)
				dtdContentRegion = doctype.getRegionAtCharacterOffset(typedRegion.getOffset());
			String contents = dtdContentRegion != null ? doctype.getFullText(dtdContentRegion) : null;
			IStructuredDocument document = getInternalDocument();
			if (document == null)
				return false;
			updateStyleRanges(document, contents, typedRegion.getOffset());
			addStyleRanges(lineRequestStart, lineRequestLength, holdResults);
			return true;
		}
		return false;
	}

	public void release() {
		if (fInternalProvider != null) {
			fInternalProvider.release();
		}
		super.release();
	}

	private void updateStyleRanges(IStructuredDocument document, String contents, int adjustment) {

		if (!document.get().equals(contents) || fInternalAdjustment != adjustment) {
			document.set(contents);
			try {
				ITypedRegion regions[] = getInternalDocument().computePartitioning(0, document.getLength());
				List ranges = new ArrayList();
				fInternalProvider.init(getInternalDocument(), getHighlighter());
				for (int i = 0; i < regions.length; i++) {
					fInternalProvider.prepareRegions(regions[i], regions[i].getOffset(), regions[i].getLength(), ranges);
				}
				fInternalAdjustment = adjustment;
				fInternalRanges = (StyleRange[]) ranges.toArray(new StyleRange[0]);
				for (int i = 0; i < fInternalRanges.length; i++)
					fInternalRanges[i].start += fInternalAdjustment;
			} catch (BadLocationException e) {
				fInternalRanges = new StyleRange[0];
			}
		}
	}
}
