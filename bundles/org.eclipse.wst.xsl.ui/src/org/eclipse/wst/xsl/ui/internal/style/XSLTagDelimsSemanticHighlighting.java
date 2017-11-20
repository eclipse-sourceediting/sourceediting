/*******************************************************************************
 * Copyright (c) 2010 Intalio, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver (Intalio) - bug 256339 - initial API and implementation
 *                            - bug 307924 - Fix NPE when textRegion is null.
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.style;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
		
		List openPos = createSemanticPositions(region.getFirstRegion(), region, DOMRegionContext.XML_TAG_OPEN);
		List endOpenPos = createSemanticPositions(region.getFirstRegion(), region, DOMRegionContext.XML_END_TAG_OPEN);
		List emptyTagClose = createSemanticPositions(region, DOMRegionContext.XML_EMPTY_TAG_CLOSE);
		List closePos = createSemanticPositions(region,	DOMRegionContext.XML_TAG_CLOSE);
		ArrayList arrpos = new ArrayList();
		arrpos.addAll(openPos);
		arrpos.addAll(closePos);
		arrpos.addAll(endOpenPos);
		arrpos.addAll(emptyTagClose);
		Position[] allPos = new Position[arrpos.size()];
		if (!arrpos.isEmpty()) {
			arrpos.toArray(allPos);
		}
		return allPos;
	}
	
	protected List createSemanticPositions(ITextRegion textRegion, IStructuredDocumentRegion region, String regionType) {
		ArrayList arrpos = new ArrayList();
		if (textRegion == null) {
			return Collections.EMPTY_LIST;
		}
	
		if (regionType.equals(textRegion.getType())) {
			Position pos = new Position(region
					.getStartOffset(textRegion), textRegion.getLength());
			arrpos.add(pos);
		}
		return arrpos;
	}

}
