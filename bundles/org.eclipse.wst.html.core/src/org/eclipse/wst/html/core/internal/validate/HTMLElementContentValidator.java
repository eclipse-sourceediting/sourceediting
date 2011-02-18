/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.validate;

import java.util.List;
import java.util.Locale;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.core.internal.provisional.HTMLCMProperties;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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

		validateContent(target, target.getFirstChild());
	}
	
	private void validateContent(Element parent, Node child) {
		if (child == null)
			return;

		CMElementDeclaration ed = CMUtil.getDeclaration(parent);
		if(ed == null || ed.getContentType() == CMElementDeclaration.ANY)
			return;
		
		List[] extendedContent = new List[1];
		while (child != null) {
			// perform actual validation
			validateNode(parent, child, ed, extendedContent);
			child = child.getNextSibling();
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
	 * The accurate maximum occurrence should be retrieve from the content
	 * model. However, it is useful enough, since almost implicit elements are
	 * HTML, HEAD, or BODY.
	 */
	// private int getMaxOccur(Element parent, String childTag) {
	// return 1;
	// }

//	private boolean containsName(String name, Object[] possible) {
//		if (name != null && possible != null) {
//			for (int i = 0; i < possible.length; i++) {
//				if(name.equals(possible[i]))
//				return true;
//			}
//		}
//		return false;
//	}

	private void validateNode(Element target, Node child, CMElementDeclaration edec, List[] extendedContent) {
		// NOTE: If the target element is 'UNKNOWN', that is, it has no
		// element declaration, the content type of the element should be
		// regarded as 'ANY'. -- 9/10/2001
		int contentType = CMElementDeclaration.ANY;
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
				if (CMUtil.isObsolete(ced)){
					error = ErrorState.OBSOLETE_TAG_NAME_ERROR;
					break;
				}
				
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
						
						/*
						 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=218143 - 
						 * ModelQuery use not pervasive enough
						 */
						if (extendedContent[0] == null) {
							extendedContent[0] = ModelQueryUtil.getModelQuery(target.getOwnerDocument()).getAvailableContent(target, edec, ModelQuery.INCLUDE_CHILD_NODES);
						}

						List availableChildElementDeclarations = extendedContent[0];
						/*
						 * Retrieve and set aside just the element names for faster checking
						 * later.
						 */
						int availableChildCount = availableChildElementDeclarations.size();
						String elementName = ced.getElementName().toLowerCase(Locale.US);
						for (int i = 0; i < availableChildCount; i++) {
							CMNode cmnode = (CMNode) availableChildElementDeclarations.get(i);
							if (cmnode.getNodeType() == CMNode.ELEMENT_DECLARATION && cmnode.getNodeName().toLowerCase(Locale.US).equals(elementName)) {
								return;
							}
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
			case Node.CDATA_SECTION_NODE :
				if (edec.supports(HTMLCMProperties.IS_XHTML) && Boolean.TRUE.equals(edec.getProperty(HTMLCMProperties.IS_XHTML)))
					return;
				if (!edec.getNodeName().equalsIgnoreCase(HTML40Namespace.ElementName.BODY)) { // special case for body element
					switch (contentType) {
						case CMElementDeclaration.ANY:
						case CMElementDeclaration.CDATA:
						case CMElementDeclaration.MIXED:
						case CMElementDeclaration.PCDATA:
							return;
					}
				}
				// Mark the whole CDATA section as an error segment
				error = ErrorState.INVALID_CONTENT_ERROR;
				segType = FMUtil.SEG_WHOLE_TAG;
				break;
			case Node.ENTITY_REFERENCE_NODE :
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
