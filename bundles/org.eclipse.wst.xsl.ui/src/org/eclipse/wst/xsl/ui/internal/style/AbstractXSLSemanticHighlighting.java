/*******************************************************************************
 * Copyright (c) 2010 Intalio, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (Intalio) - bug 256339 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.style;

import java.util.ArrayList;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.ui.ISemanticHighlighting;
import org.eclipse.wst.sse.ui.ISemanticHighlightingExtension;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;

public abstract class AbstractXSLSemanticHighlighting implements
		ISemanticHighlightingExtension, ISemanticHighlighting {

	public abstract String getStylePreferenceKey();

	public String getBoldPreferenceKey() {
		return null;
	}

	public String getUnderlinePreferenceKey() {
		return null;
	}

	public String getStrikethroughPreferenceKey() {
		return null;
	}

	public String getItalicPreferenceKey() {
		return null;
	}

	public String getColorPreferenceKey() {
		return null;
	}

	public IPreferenceStore getPreferenceStore() {
		return XSLUIPlugin.getDefault().getPreferenceStore();
	}

	public String getEnabledPreferenceKey() {
		return null;
	}

	public String getDisplayName() {
		return null;
	}

	public abstract Position[] consumes(IStructuredDocumentRegion region);

	protected Position[] createSemanticPositions(IStructuredDocumentRegion region, String regionType) {
		if (region == null) {
			return null;
		}
		
		if (!region.getType().equals(DOMRegionContext.XML_TAG_NAME)) {
			return null;
		}
	
		Position p[] = null;
		ITextRegionList regionList = region.getRegions();
		
		ArrayList arrpos = new ArrayList();
		for (int i = 0; i < regionList.size(); i++) {
			ITextRegion textRegion = regionList.get(i);
			if (textRegion.getType().equals(regionType)) {
				Position pos = new Position(region
						.getStartOffset(textRegion), textRegion.getLength());
				arrpos.add(pos);
			}
		}
		p = new Position[arrpos.size()];
		arrpos.toArray(p);
		return p;
	}

	public Position[] consumes(IStructuredDocumentRegion documentRegion, IndexedRegion indexedRegion) {
		if (indexedRegion != null && indexedRegion instanceof IDOMNode) {
			IDOMNode node = (IDOMNode)indexedRegion;
			if (XSLCore.isXSLNamespace(node)) {
				return consumes(documentRegion);
			}
		}
		return null;
	}
	
}
