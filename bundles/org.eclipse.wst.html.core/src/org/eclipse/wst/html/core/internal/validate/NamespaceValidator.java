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
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NamespaceValidator extends PrimeValidator implements ErrorState {

	private final static String XMLNS_PREFIX = "xmlns";//$NON-NLS-1$
	private final static String NS_SEPARATOR = ":";//$NON-NLS-1$

	public NamespaceValidator() {
		super();
	}

	public boolean isAdapterForType(Object type) {
		return ((type == NamespaceValidator.class) || super.isAdapterForType(type));
	}

	public void validate(IndexedRegion node) {
		Element target = (Element) node;
		if (isXMLElement(target) && hasUnknownPrefix(target)) {
			IDOMElement e = (IDOMElement) target;
			if (!isValidPrefix(e.getPrefix(), target) && !e.isCommentTag()) {
				// report unknown tag error.
				Segment errorSeg = null;
				if (e.hasStartTag())
					errorSeg = FMUtil.getSegment(e, FMUtil.SEG_START_TAG);
				else if (e.hasEndTag())
					errorSeg = FMUtil.getSegment(e, FMUtil.SEG_END_TAG);

				if (errorSeg != null)
					reporter.report(new ErrorInfoImpl(UNDEFINED_NAME_ERROR, errorSeg, e));
			}
		}
		// (2) check prefix of each attr 
		NamedNodeMap attrs = target.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			Node n = attrs.item(i);
			if (!(n instanceof IDOMAttr))
				continue;
			IDOMAttr a = (IDOMAttr) n;
			String prefix = a.getPrefix();
			if ((prefix != null) && isUnknownAttr(a, target)) {
				// The attr has unknown prefix.  So, check it.
				if (!isValidPrefix(prefix, target)) {
					// report unknown attr error.
					ITextRegion r = a.getNameRegion();
					if (r == null)
						continue;
					int a_offset = a.getNameRegionStartOffset();
					int a_length = a.getNameRegion().getLength();
					reporter.report(new ErrorInfoImpl(UNDEFINED_NAME_ERROR, new Segment(a_offset, a_length), a));
				}
			}
		}
	}

	// private methods	
	private boolean isXMLElement(Element target) {
		return target instanceof IDOMElement;
	}

	private boolean hasUnknownPrefix(Element target) {
		return isUnknownElement(target) && CMUtil.isForeign(target);
	}

	private boolean isUnknownElement(Element target) {
		CMElementDeclaration dec = CMUtil.getDeclaration(target);
		return dec == null;
	}

	private boolean isUnknownAttr(IDOMAttr attr, Element target) {
		CMElementDeclaration dec = CMUtil.getDeclaration(target);
		if (dec == null)
			return true; // unknown.
		CMNamedNodeMap adecls = dec.getAttributes();
		CMAttributeDeclaration adec = (CMAttributeDeclaration) adecls.getNamedItem(attr.getName());
		return adec == null;
	}

	private boolean isValidPrefix(String prefix, Element e) {
		if (prefix.equals(XMLNS_PREFIX))
			return true; // "xmlns:foo" attr is always valid.

		// (1) check the element has the namespace definition or not.
		if (isValidPrefixWithinElement(prefix, e))
			return true;

		// (2) check ancestors of the element have the namespace definition or not.
		Element parent = SMUtil.getParentElement(e);
		while (parent != null) {
			if (isValidPrefixWithinElement(prefix, parent))
				return true;
			parent = SMUtil.getParentElement(parent);
		}
		return false;
	}

	private boolean isValidPrefixWithinElement(String prefix, Element e) {
		String ns = XMLNS_PREFIX + NS_SEPARATOR + prefix;
		NamedNodeMap attrs = e.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			Node n = attrs.item(i);
			if (n == null)
				continue;
			if (n.getNodeType() != Node.ATTRIBUTE_NODE)
				continue;
			if (ns.equals(((Attr) n).getName()))
				return true;
		}
		return false;
	}
}
