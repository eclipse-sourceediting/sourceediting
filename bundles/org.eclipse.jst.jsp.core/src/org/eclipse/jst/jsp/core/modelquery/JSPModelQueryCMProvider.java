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
package org.eclipse.jst.jsp.core.modelquery;



import java.util.List;

import org.eclipse.jst.jsp.core.contentmodel.JSPCMDocumentFactory;
import org.eclipse.jst.jsp.core.contentmodel.tld.TaglibSupport;
import org.eclipse.wst.common.contentmodel.CMDocument;
import org.eclipse.wst.common.contentmodel.CMElementDeclaration;
import org.eclipse.wst.common.contentmodel.modelquery.ModelQueryCMProvider;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.w3c.dom.Node;

/**
 * CMDocument provider for HTML and JSP documents.
 */
public class JSPModelQueryCMProvider implements ModelQueryCMProvider {

	protected TaglibSupport fTaglibSupport = null;

	protected JSPModelQueryCMProvider() {
		super();
	}

	public JSPModelQueryCMProvider(TaglibSupport taglibSupport) {
		fTaglibSupport = taglibSupport;
	}

	/**
	 * Returns the CMDocument that corresponds to the DOM Node.
	 * or null if no CMDocument is appropriate for the DOM Node.
	 */
	public CMDocument getCorrespondingCMDocument(Node node) {
		CMDocument jcmdoc = JSPCMDocumentFactory.getCMDocument();

		CMDocument result = null;
		try {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String elementName = node.getNodeName();

				// test to see if this node belongs to JSP's CMDocument (case sensitive)
				CMElementDeclaration dec = (CMElementDeclaration) jcmdoc.getElements().getNamedItem(elementName);
				if (dec != null) {
					result = jcmdoc;
				}
			}

			if (result == null && fTaglibSupport != null && node instanceof IndexedRegion) {
				// check position dependent
				IndexedRegion indexedNode = (IndexedRegion) node;
				List documents = fTaglibSupport.getCMDocuments(node.getPrefix(), indexedNode.getStartOffset());
				if (documents != null && documents.size() > 0)
					result = (CMDocument) documents.get(0);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}