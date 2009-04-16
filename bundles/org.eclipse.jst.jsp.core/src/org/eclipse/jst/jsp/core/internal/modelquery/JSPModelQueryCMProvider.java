/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.modelquery;

import java.util.List;

import org.eclipse.core.runtime.Path;
import org.eclipse.jst.jsp.core.internal.contentmodel.JSPCMDocumentFactory;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TLDCMDocumentManager;
import org.eclipse.jst.jsp.core.internal.contenttype.DeploymentDescriptorPropertyCache;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQueryCMProvider;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
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
		CMDocument jcmdoc = null;
		if (node instanceof IDOMNode) {
			IDOMModel model = ((IDOMNode) node).getModel();
			String modelPath = model.getBaseLocation();
			if (modelPath != null && !IModelManager.UNMANAGED_MODEL.equals(modelPath)) {
				float version = DeploymentDescriptorPropertyCache.getInstance().getJSPVersion(new Path(modelPath));
				jcmdoc = JSPCMDocumentFactory.getCMDocument(version);
			}
		}
		if (jcmdoc == null) {
			jcmdoc = JSPCMDocumentFactory.getCMDocument();
		}
		
		CMDocument result = null;
		try {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String elementName = node.getNodeName();

				// test to see if this node belongs to JSP's CMDocument (case
				// sensitive)
				CMElementDeclaration dec = (CMElementDeclaration) jcmdoc.getElements().getNamedItem(elementName);
				if (dec != null) {
					result = jcmdoc;
				}
			}

			String prefix = node.getPrefix();

			if (result == null && prefix != null && prefix.length() > 0 && node instanceof IDOMNode) {
				// check position dependent
				IDOMNode xmlNode = (IDOMNode) node;
				TLDCMDocumentManager tldmgr = TaglibController.getTLDCMDocumentManager(xmlNode.getStructuredDocument());
				if (tldmgr != null) {
					List documents = tldmgr.getCMDocumentTrackers(node.getPrefix(), xmlNode.getStartOffset());
					// there shouldn't be more than one cmdocument returned
					if (documents != null && documents.size() > 0)
						result = (CMDocument) documents.get(0);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
