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

import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HTMLElementContentValidator extends PrimeValidator {

	/**
	 * HTMLElementContentValidator constructor comment.
	 */
	public HTMLElementContentValidator() {
		super();
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type allows it
	 * to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return ((type == HTMLElementContentValidator.class) || super.isAdapterForType(type));
	}

	/**
	 */
	public void validate(IndexedRegion node) {
		Element target = (Element) node;
		if (CMUtil.isForeign(target))
			return;

		validateContent(target, target.getChildNodes());
	}

	private void validateContent(Element parent, NodeList children) {
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child == null)
				continue;

			// perform actual validation
			validateNode(parent, child);
		}
	}

	// private int countExplicitSiblings(Element parent, String tagName) {
	// NodeList children = parent.getChildNodes();
	// int count = 0;
	// for (int i = 0; i < children.getLength(); i++) {
	// Node child = children.item(i);
	// if (child.getNodeType() != Node.ELEMENT_NODE)
	// continue;
	// if (tagName.equalsIgnoreCase(((Element) child).getTagName())) {
	// count++;
	// }
	// }
	// return count;
	// }

	/*
	 * The implementation of the following method is practical but accurate.
	 * The accurate maximum occurence should be retreive from the content
	 * model. However, it is useful enough, since almost implicit elements are
	 * HTML, HEAD, or BODY.
	 */
	// private int getMaxOccur(Element parent, String childTag) {
	// return 1;
	// }
	private void validateNode(Element target, Node child) {
		// NOTE: If the target element is 'UNKNOWN', that is, it has no
		// element declaration, the content type of the element should be
		// regarded as 'ANY'. -- 9/10/2001
		int contentType = CMElementDeclaration.ANY;
		CMElementDeclaration edec = CMUtil.getDeclaration(target);
		if (edec != null)
			contentType = edec.getContentType();

		int error = ErrorState.NONE_ERROR;
		int segType = FMUtil.SEG_WHOLE_TAG;

		switch (child.getNodeType()) {
			case Node.ELEMENT_NODE :
				Element childElem = (Element) child;
				// Defect 200321:
				// This validator cares only HTML/XHTML elements. If a child
				// is
				// an element of foreign markup languages, just ignore it.
				if (CMUtil.isForeign(childElem))
					return;

				CMElementDeclaration ced = CMUtil.getDeclaration((Element) child);
				// Defect 186774: If a child is not one of HTML elements,
				// it should be regarded as a valid child regardless the
				// type of the parent content model. -- 10/12/2001
				if (ced == null || CMUtil.isSSI(ced) || (!CMUtil.isHTML(ced)))
					return;

				switch (contentType) {
					case CMElementDeclaration.ANY :
						// Keep going.
						return;
					case CMElementDeclaration.ELEMENT :
					case CMElementDeclaration.MIXED :
						if (ced == null)
							return;
						if (CMUtil.isValidChild(edec, ced))
							return;
						// Now, it is the time to check inclusion, unless the
						// target
						// document is not a XHTML.
						if (!CMUtil.isXHTML(edec)) {
							// pure HTML
							if (CMUtil.isValidInclusion(ced, target))
								return;
						}
						error = ErrorState.INVALID_CONTENT_ERROR;
						break;
					default :
						error = ErrorState.INVALID_CONTENT_ERROR;
						break;
				}
				// Mark the whole START tag as an error segment.
				segType = FMUtil.SEG_START_TAG;
				break;
			case Node.TEXT_NODE :
				switch (contentType) {
					case CMElementDeclaration.ANY :
					case CMElementDeclaration.MIXED :
					case CMElementDeclaration.PCDATA :
					case CMElementDeclaration.CDATA :
						// D184339
						// Keep going.
						return;
					case CMElementDeclaration.ELEMENT :
					case CMElementDeclaration.EMPTY :
						if (((IDOMText) child).isElementContentWhitespace())
							return;
						error = ErrorState.INVALID_CONTENT_ERROR;
						break;
					default :
						error = ErrorState.INVALID_CONTENT_ERROR;
						break;
				}
				// Mark the whole node as an error segment.
				segType = FMUtil.SEG_WHOLE_TAG;
				break;
			case Node.COMMENT_NODE :
			case Node.PROCESSING_INSTRUCTION_NODE :
				if (contentType != CMElementDeclaration.EMPTY)
					return;
				error = ErrorState.INVALID_CONTENT_ERROR;
				// Mark the whole node as an error segment.
				segType = FMUtil.SEG_WHOLE_TAG;
				break;
			default :
				error = ErrorState.INVALID_CONTENT_ERROR;
				// Mark the whole node as an error segment.
				segType = FMUtil.SEG_WHOLE_TAG;
				break;
		}
		if (error != ErrorState.NONE_ERROR) {
			Segment errorSeg = FMUtil.getSegment((IDOMNode) child, segType);
			if (errorSeg != null)
				reporter.report(new ErrorInfoImpl(error, errorSeg, child));
		}
	}
}
