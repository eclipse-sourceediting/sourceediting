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
package org.eclipse.wst.html.core.internal.validate;



import org.eclipse.wst.html.core.internal.provisional.HTMLCMProperties;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.Element;

public class HTMLElementAncestorValidator extends PrimeValidator {

	/**
	 * HTMLElementAncestorValidator constructor comment.
	 */
	public HTMLElementAncestorValidator() {
		super();
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type
	 * allows it to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return ((type == HTMLElementAncestorValidator.class) || super.isAdapterForType(type));
	}

	/**
	 * Check exclusion which is defined in only HTML DTD (SGML).
	 */
	public void validate(IndexedRegion node) {
		Element target = (Element) node;
		if (CMUtil.isForeign(target))
			return;
		CMElementDeclaration dec = CMUtil.getDeclaration(target);
		if (dec == null)
			return; // cannot validate.
		if (!CMUtil.isHTML(dec))
			return; // no need to validate
		if (!dec.supports(HTMLCMProperties.PROHIBITED_ANCESTORS))
			return; // cannot validate.
		CMNamedNodeMap prohibited = (CMNamedNodeMap) dec.getProperty(HTMLCMProperties.PROHIBITED_ANCESTORS);
		if (prohibited.getLength() <= 0)
			return; // no prohibited ancestors.

		Element parent = SMUtil.getParentElement(target);
		while (parent != null) {
			CMNode pdec = prohibited.getNamedItem(parent.getNodeName());
			if (pdec != null) { // prohibited element is found in ancestors.
				Segment errorSeg = FMUtil.getSegment((IDOMNode) node, FMUtil.SEG_START_TAG);
				if (errorSeg != null)
					reporter.report(new ErrorInfoImpl(ErrorState.INVALID_CONTENT_ERROR, errorSeg, target));
				break; // If one prohibited ancestor is found, it's enough.
			}
			parent = SMUtil.getParentElement(parent);
		}
	}
}
