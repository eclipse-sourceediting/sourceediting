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
package org.eclipse.wst.dtd.ui.style.dtd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wst.dtd.core.text.rules.StructuredTextPartitionerForDTD;
import org.eclipse.wst.dtd.ui.preferences.ui.DTDColorManager;
import org.eclipse.wst.encoding.content.IContentTypeIdentifier;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.ui.preferences.PreferenceManager;
import org.eclipse.wst.sse.ui.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.style.AbstractLineStyleProvider;
import org.eclipse.wst.sse.ui.style.LineStyleProvider;
import org.w3c.dom.Element;


public class LineStyleProviderForDTDSubSet extends AbstractLineStyleProvider implements LineStyleProvider {

	private IStructuredModel fInternalModel = null;
	private StyleRange[] fInternalRanges;
	private int fInternalAdjustment;
	private LineStyleProviderForDTD fInternalProvider = null;

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
		Element color = DTDColorManager.getDTDColorManager().getColorElement(IStyleConstantsDTD.DTD_DEFAULT);

		RGB foreground = ColorHelper.toRGB(color.getAttribute(ColorHelper.FOREGROUND));
		RGB background = ColorHelper.toRGB(color.getAttribute(ColorHelper.BACKGROUND));
		boolean bold = Boolean.valueOf(color.getAttribute(ColorHelper.BOLD)).booleanValue();

		return createTextAttribute(foreground, background, bold);
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	protected PreferenceManager getColorManager() {
		return DTDColorManager.getDTDColorManager();
	}

	private IModelManager getModelManager() {

		IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		return plugin.getModelManager();
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

	/*
	 * (non-Javadoc)
	 * 
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
			}
			catch (BadLocationException e) {
				fInternalRanges = new StyleRange[0];
			}
		}
	}
}
