/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.taginfo;



import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.html.ui.internal.taginfo.HTMLTagInfoHoverProcessor;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * Provides hover help documentation for JSP tags
 */
public class JSPTagInfoHoverProcessor extends HTMLTagInfoHoverProcessor {

	protected String computeRegionHelp(IndexedRegion treeNode, IDOMNode parentNode, IStructuredDocumentRegion flatNode, ITextRegion region) {
		String result = null;

		if (region == null)
			return null;

		String regionType = region.getType();
		if (regionType == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
			result = computeJSPDirHelp((IDOMNode) treeNode, parentNode, flatNode, region);
		}
		else
			result = super.computeRegionHelp(treeNode, parentNode, flatNode, region);

		return result;
	}

	/**
	 * Computes the hover help for the jsp directive name
	 * for now, treat jsp directives like any other tag name
	 */
	protected String computeJSPDirHelp(IDOMNode xmlnode, IDOMNode parentNode, IStructuredDocumentRegion flatNode, ITextRegion region) {
		return computeTagNameHelp(xmlnode, parentNode, flatNode, region);
	}

}
