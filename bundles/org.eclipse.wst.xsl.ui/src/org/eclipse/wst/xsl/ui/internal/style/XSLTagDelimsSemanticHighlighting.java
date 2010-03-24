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
import java.util.Arrays;

import org.eclipse.jface.text.Position;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.ui.ISemanticHighlighting;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class XSLTagDelimsSemanticHighlighting extends
		AbstractXSLSemanticHighlighting implements ISemanticHighlighting {

	public XSLTagDelimsSemanticHighlighting() {
	}

	public String getStylePreferenceKey() {
		return IStyleConstantsXSL.TAG_BORDER;
	}

	@Override
	public String getEnabledPreferenceKey() {
		return "xsl.ui.highlighting.tag.enabled"; //$NON-NLS-1$
	}

	public Position[] consumes(IStructuredDocumentRegion region) {
		
		Position[] openPos = createSemanticPositions(region.getFirstRegion(), region, DOMRegionContext.XML_TAG_OPEN);
		Position[] endOpenPos = createSemanticPositions(region.getFirstRegion(), region, DOMRegionContext.XML_END_TAG_OPEN);
		Position[] emptyTagClose = createSemanticPositions(region, DOMRegionContext.XML_EMPTY_TAG_CLOSE);
		Position[] closePos = createSemanticPositions(region,	DOMRegionContext.XML_TAG_CLOSE);
		ArrayList arrpos = new ArrayList();
		arrpos.addAll(Arrays.asList(openPos));
		arrpos.addAll(Arrays.asList(closePos));
		arrpos.addAll(Arrays.asList(endOpenPos));
		arrpos.addAll(Arrays.asList(emptyTagClose));
		Position[] allPos = new Position[arrpos.size()];
		arrpos.toArray(allPos);
		return allPos;
	}
	
	protected Position[] createSemanticPositions(ITextRegion textRegion, IStructuredDocumentRegion region, String regionType) {
		if (textRegion == null) {
			return null;
		}
		
		Position p[] = null;
	
		ArrayList arrpos = new ArrayList();
		if (textRegion.getType().equals(regionType)) {
			Position pos = new Position(region
					.getStartOffset(textRegion), textRegion.getLength());
			arrpos.add(pos);
		}
		p = new Position[arrpos.size()];
		arrpos.toArray(p);
		return p;
	}

}
