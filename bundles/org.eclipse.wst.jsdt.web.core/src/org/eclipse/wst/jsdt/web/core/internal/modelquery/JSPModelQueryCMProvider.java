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
package org.eclipse.wst.jsdt.web.core.internal.modelquery;

import java.util.List;

import org.eclipse.wst.jsdt.web.core.internal.contentmodel.JSPCMDocumentFactory;


import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQueryCMProvider;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Node;

/**
 * CMDocument provider for HTML and JSP documents.
 */
public class JSPModelQueryCMProvider implements ModelQueryCMProvider {

	protected JSPModelQueryCMProvider() {
		super();
	}

	/**
	 * Returns the CMDocument that corresponds to the DOM Node. or null if no
	 * CMDocument is appropriate for the DOM Node.
	 */
	public CMDocument getCorrespondingCMDocument(Node node) {
		CMDocument jcmdoc = JSPCMDocumentFactory.getCMDocument();

		CMDocument result = null;
		
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String elementName = node.getNodeName();

				// test to see if this node belongs to JSP's CMDocument (case
				// sensitive)
				CMElementDeclaration dec = (CMElementDeclaration) jcmdoc
						.getElements().getNamedItem(elementName);
				if (dec != null) {
					result = jcmdoc;
				}
			}

			String prefix = node.getPrefix();

			if (result == null && prefix != null && prefix.length() > 0
					&& node instanceof IDOMNode) {
						// hacked out some tag lib stuff 
						result = (CMDocument) node.getOwnerDocument();
					
			}
			
		
		return result;
	}
}