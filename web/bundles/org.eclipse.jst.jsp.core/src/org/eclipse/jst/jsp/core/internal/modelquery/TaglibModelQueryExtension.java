/*******************************************************************************
 * Copyright (c) 2009, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.modelquery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TLDCMDocumentManager;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.TaglibTracker;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension.ModelQueryExtension;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * An implementation of {@link ModelQueryExtension} for tag libraries in JSP documents
 */
public class TaglibModelQueryExtension extends ModelQueryExtension {
	
	private final Stack fExtensions = new Stack();
	/**
	 * @see org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension.ModelQueryExtension#getAvailableElementContent(org.w3c.dom.Element, java.lang.String, int)
	 */
	public CMNode[] getAvailableElementContent(Element parentElement,
			String namespace, int includeOptions) {
		
		CMNode[] nodes = EMPTY_CMNODE_ARRAY;
		ArrayList nodeList = new ArrayList();
		
		//only returns anything if looking for child nodes
		if(((includeOptions & ModelQuery.INCLUDE_CHILD_NODES) != 0) && parentElement instanceof IDOMElement) {
			//get the trackers
			IDOMElement elem = (IDOMElement)parentElement;
			IStructuredDocument structDoc = elem.getModel().getStructuredDocument();
			TLDCMDocumentManager manager = TaglibController.getTLDCMDocumentManager(structDoc);

			if(manager != null) {
				List trackers = new ArrayList(manager.getTaglibTrackers());
				Set prefixes = new HashSet();
				//for each tracker add each of its elements to the node list
				for(int trackerIndex = 0; trackerIndex < trackers.size(); ++trackerIndex) {
					TaglibTracker tracker = ((TaglibTracker)trackers.get(trackerIndex));
					CMNamedNodeMap elements = tracker.getElements();
					for(int elementIndex = 0; elementIndex < elements.getLength(); ++elementIndex) {
						nodeList.add(elements.item(elementIndex));
					}
					prefixes.add(tracker.getPrefix());
				}
				String prefix = parentElement.getPrefix();
				if (prefixes.contains(prefix)) {
					Node parent = parentElement;
					while ((parent = parent.getParentNode()) != null && parent.getNodeType() == Node.ELEMENT_NODE) {
						prefix = parent.getPrefix();
						if (prefix == null || !prefixes.contains(prefix)) {
							ModelQuery query = ModelQueryUtil.getModelQuery(parentElement.getOwnerDocument());
							if (query != null) {
								CMElementDeclaration decl = query.getCMElementDeclaration((Element) parent);
								if (decl != null && !fExtensions.contains(this)) {
									fExtensions.push(this);
									nodeList.addAll(query.getAvailableContent((Element) parent, decl, includeOptions));
									fExtensions.pop();
								}
							}
							break;
						}
					}
				}
				nodes = (CMNode[])nodeList.toArray(new CMNode[nodeList.size()]);
			}
		}
		
		return nodes;
	}
}
