/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.modelquery;


import java.util.List;
import java.util.Vector;

import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.html.core.internal.provisional.HTMLCMProperties;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl.ModelQueryImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.xml.core.internal.modelquery.XMLModelQueryAssociationProvider;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.ssemodelquery.MovableModelQuery;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This added and/or made public specifically for experimentation. It
 * will change as this functionality becomes API. See
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=119084
 */
 
public class HTMLModelQueryImpl extends ModelQueryImpl implements MovableModelQuery {

	protected CMDocumentCache fCache = null;
	protected XMLModelQueryAssociationProvider xmlAssocProv = null;

	public HTMLModelQueryImpl(CMDocumentCache cache, URIResolver idResolver) {
		super(new HTMLModelQueryAssociationProvider(cache, idResolver));
		fCache = cache;
		xmlAssocProv = new XMLModelQueryAssociationProvider(cache, idResolver);
	}

	public List getAvailableContent(Element element, CMElementDeclaration ed, int includeOptions) {
		List originalCandidates = super.getAvailableContent(element, ed, includeOptions);
		if ((includeOptions & INCLUDE_CHILD_NODES) == 0)
			return originalCandidates;
		// When the target document is XHTML, it is waste to find inclusions,
		// since inclusion is available in HTML only.
		if (!ed.supports(HTMLCMProperties.IS_XHTML))
			return originalCandidates;
		
		Boolean isXhtml = Boolean.FALSE;
		isXhtml = (Boolean) ed.getProperty(HTMLCMProperties.IS_XHTML);
		if (isXhtml != null && isXhtml.booleanValue())
			return originalCandidates;

		// OK, the target is surely a HTML element, so it may have inclusion.
		// Try to find it.
		Vector candidates = new Vector(originalCandidates);

		switch (ed.getContentType()) {
			case CMElementDeclaration.ANY :
			case CMElementDeclaration.ELEMENT :
			case CMElementDeclaration.MIXED :
				// do enumerate inclusions.
				candidates.addAll(HMQUtil.getInclusions(element));
				break;
			case CMElementDeclaration.EMPTY :
			case CMElementDeclaration.PCDATA :
			case CMElementDeclaration.CDATA :
			default :
				// should not add any inclusions.
				// so, nothing to do here.
				break;
		}
		// If the current element does not available, it is impossible
		// to filter out exclusion.
		if (element == null)
			return candidates;

		// Now, the time to check exclusion.
		Vector content = new Vector(candidates.size());
		for (int i = 0; i < candidates.size(); i++) {
			Object eCandidate = candidates.elementAt(i);
			if(eCandidate instanceof CMElementDeclaration) {
				CMElementDeclaration candidate = (CMElementDeclaration) eCandidate;
				if (candidate == null)
					continue;
				if (isExcluded(candidate, element))
					continue;
				content.add(candidate);
			}
		}

		return content;
	}

	/**
	 * @see MovableModelQuery#setIdResolver(IdResolver)
	 */
	public void setIdResolver(URIResolver newIdResolver) {
		modelQueryAssociationProvider = new HTMLModelQueryAssociationProvider(fCache, newIdResolver);
	}

	// utilities
	private static boolean isExcluded(CMElementDeclaration candidate, Element target) {
		CMNamedNodeMap prohibited = getProhibitedAncestors(candidate);
		if (prohibited == null)
			return false;
		Element parent = target;
		while (parent != null) {
			CMNode pdec = prohibited.getNamedItem(parent.getNodeName());
			if (pdec != null)
				return true;
			parent = getExplicitParentElement(parent);
		}
		return false;
	}

	private static CMNamedNodeMap getProhibitedAncestors(CMElementDeclaration dec) {
		if (!dec.supports(HTMLCMProperties.PROHIBITED_ANCESTORS))
			return null;
		return (CMNamedNodeMap) dec.getProperty(HTMLCMProperties.PROHIBITED_ANCESTORS);
	}

	/* get an ancestor element ignoring implicit ones. */
	private static Element getExplicitParentElement(Node child) {
		if (child == null)
			return null;

		Node p = child.getParentNode();
		while (p != null) {
			if (p.getNodeType() == Node.ELEMENT_NODE) {
				if (p instanceof IDOMElement) {
					if (((IDOMElement) p).isImplicitTag()) {
						p = p.getParentNode();
						continue;
					}
				}
				return (Element) p;
			}
			p = p.getParentNode();
		}
		return null;
	}

	public CMElementDeclaration getCMElementDeclaration(Element element) {
		CMElementDeclaration result = super.getCMElementDeclaration(element);
		if (null != result)
			return result;
		
		return xmlAssocProv.getCMElementDeclaration(element);
	}

}
