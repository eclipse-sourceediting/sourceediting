/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.taginfo;



import org.eclipse.jst.jsp.core.model.parser.DOMJSPRegionContexts;
import org.eclipse.wst.html.ui.taginfo.HTMLTagInfoHoverProcessor;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.xml.core.document.XMLNode;

/**
 * Provides hover help documentation for JSP tags
 */
public class JSPTagInfoHoverProcessor extends HTMLTagInfoHoverProcessor {

	/* (non-Javadoc)
	 * @see com.ibm.sed.structured.taginfo.AbstractTextHoverProcessor#computeRegionHelp(com.ibm.sed.model.IndexedRegion, com.ibm.sed.model.xml.XMLNode, com.ibm.sed.structured.text.IStructuredDocumentRegion, com.ibm.sed.structured.text.ITextRegion)
	 */
	protected String computeRegionHelp(IndexedRegion treeNode, XMLNode parentNode, IStructuredDocumentRegion flatNode, ITextRegion region) {
		String result = null;

		if (region == null)
			return null;

		String regionType = region.getType();
		if (regionType == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
			result = computeJSPDirHelp((XMLNode) treeNode, parentNode, flatNode, region);
		}
		else
			result = super.computeRegionHelp(treeNode, parentNode, flatNode, region);

		return result;
	}

	/**
	 * Computes the hover help for the jsp directive name
	 * for now, treat jsp directives like any other tag name
	 */
	protected String computeJSPDirHelp(XMLNode xmlnode, XMLNode parentNode, IStructuredDocumentRegion flatNode, ITextRegion region) {
		return computeTagNameHelp(xmlnode, parentNode, flatNode, region);
	}

}