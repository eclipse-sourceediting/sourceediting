/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal.contentassist;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;


/**
 * @author pavery
 *  
 */
public class ContentAssistUtils {

	/**
	 * Returns the closest IndexedRegion for the offset and viewer allowing
	 * for differences between viewer offsets and model positions. note: this
	 * method returns an IndexedRegion for read only
	 * 
	 * @param viewer
	 *            the viewer whose document is used to compute the proposals
	 * @param documentOffset
	 *            an offset within the document for which completions should
	 *            be computed
	 * @return an IndexedRegion
	 */
	public static IndexedRegion getNodeAt(ITextViewer viewer, int documentOffset) {

		if (viewer == null)
			return null;

		IndexedRegion node = null;
		IModelManager mm = StructuredModelManager.getModelManager();
		IStructuredModel model = null;
		if (mm != null)
			model = mm.getExistingModelForRead(viewer.getDocument());
		try {
			if (model != null) {
				int lastOffset = documentOffset;
				node = model.getIndexedRegion(documentOffset);
				while (node == null && lastOffset >= 0) {
					lastOffset--;
					node = model.getIndexedRegion(lastOffset);
				}
			}
		} finally {
			if (model != null)
				model.releaseFromRead();
		}
		return node;
	}

	/**
	 * Returns the closest IStructuredDocumentRegion for the offest and
	 * viewer.
	 * 
	 * @param viewer
	 * @param documentOffset
	 * @return the closest IStructuredDocumentRegion for the offest and
	 *         viewer.
	 */
	public static IStructuredDocumentRegion getStructuredDocumentRegion(ITextViewer viewer, int documentOffset) {
		IStructuredDocumentRegion sdRegion = null;
		if (viewer == null || viewer.getDocument() == null)
			return null;

		int lastOffset = documentOffset;
		IStructuredDocument doc = (IStructuredDocument) viewer.getDocument();
		sdRegion = doc.getRegionAtCharacterOffset(documentOffset);
		while (sdRegion == null && lastOffset >= 0) {
			lastOffset--;
			sdRegion = doc.getRegionAtCharacterOffset(lastOffset);
		}
		return sdRegion;
	}

}
