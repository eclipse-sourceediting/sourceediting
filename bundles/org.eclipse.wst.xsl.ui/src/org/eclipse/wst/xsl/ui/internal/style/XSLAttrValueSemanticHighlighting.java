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

import org.eclipse.jface.text.Position;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.ISemanticHighlighting;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class XSLAttrValueSemanticHighlighting extends
		AbstractXSLSemanticHighlighting implements ISemanticHighlighting {

	public XSLAttrValueSemanticHighlighting() {
	}

	public String getStylePreferenceKey() {
		return IStyleConstantsXSL.TAG_ATTRIBUTE_VALUE;
	}

	@Override
	public String getEnabledPreferenceKey() {
		return "xsl.ui.highlighting.tag.enabled"; //$NON-NLS-1$
	}

	public Position[] consumes(IStructuredDocumentRegion region) {
		ArrayList array = new ArrayList();
		array.addAll(createSemanticPositions(region, DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE));
		Position[] allPos = new Position[array.size()];
		if (!array.isEmpty()) {
			array.toArray(allPos);
		}
		return allPos;	}

}
