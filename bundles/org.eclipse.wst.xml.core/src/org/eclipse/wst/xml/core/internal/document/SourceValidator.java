/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.document;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.XMLCoreMessages;
import org.eclipse.wst.xml.core.internal.provisional.IXMLCharEntity;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/**
 */
public class SourceValidator {

	private NodeImpl node = null;

	/**
	 */
	public SourceValidator(Node node) {
		super();

		if (node != null) {
			this.node = (NodeImpl) node;
		}
	}

	/**
	 */
	public String convertSource(String source) {
		if (source == null)
			return null;
		if (this.node == null)
			return null; // error

		// setup conversion conditions
		boolean acceptTag = false;
		boolean acceptClose = false;
		boolean acceptQuote = false;
		boolean acceptAmpersand = false;
		boolean acceptEntityRef = true;
		boolean acceptJSPEnd = true;
		String endTagName = null;
		if (this.node.getNodeType() == Node.ATTRIBUTE_NODE) {
			IDOMDocument document = (IDOMDocument) this.node.getOwnerDocument();
			if (document != null && document.isJSPType())
				acceptTag = true;
			if (acceptTag) {
				Attr attr = (Attr) this.node;
				ElementImpl element = (ElementImpl) attr.getOwnerElement();
				if (element != null && element.isJSPTag())
					acceptTag = false;
			}
			// if the source does not include single quote,
			// double quote is valid
			acceptQuote = (source.indexOf('\'') < 0);
		} else if (this.node.getNodeType() == Node.TEXT_NODE) {
			TextImpl text = (TextImpl) this.node;
			if (text.isJSPContent()) {
				int index = source.indexOf(JSPTag.TAG_CLOSE);
				if (index < 0)
					return source;
				acceptTag = true;
				acceptClose = true;
				acceptQuote = true;
				acceptAmpersand = true;
				acceptJSPEnd = false;
			} else if (text.isCDATAContent()) {
				endTagName = text.getParentNode().getNodeName();
				if (endTagName == null)
					return null; // error
				acceptTag = true;
				acceptClose = true;
				acceptQuote = true;
				acceptAmpersand = true;
			}
		} else {
			IDOMDocument document = null;
			if (this.node.getNodeType() == Node.DOCUMENT_NODE) {
				document = (IDOMDocument) this.node;
			} else {
				document = (IDOMDocument) this.node.getOwnerDocument();
			}
			if (document != null && document.isJSPType())
				acceptTag = true;
		}

		StringBuffer buffer = null;
		int copiedLength = 0;
		int length = source.length();
		for (int i = 0; i < length; i++) {
			String ref = null;
			char c = source.charAt(i);
			switch (c) {
				case '<' :
					if (acceptTag) {
						if (endTagName != null) {
							if (!matchEndTag(source, i + 1, endTagName))
								continue;
						} else {
							int skip = skipTag(source, i + 1);
							if (skip >= 0) {
								i += skip;
								continue;
							}
						}
						// invalid JSP tag
					}
					ref = IXMLCharEntity.LT_REF;
					break;
				case '>' :
					if (acceptClose)
						continue;
					ref = IXMLCharEntity.GT_REF;
					break;
				case '&' :
					if (acceptAmpersand)
						continue;
					if (acceptEntityRef) {
						int skip = skipEntityRef(source, i + 1);
						if (skip >= 0) {
							i += skip;
							continue;
						}
					}
					ref = IXMLCharEntity.AMP_REF;
					break;
				case '"' :
					if (acceptQuote)
						continue;
					ref = IXMLCharEntity.QUOT_REF;
					break;
				case '%' :
					if (acceptJSPEnd)
						continue;
					if (source.charAt(i + 1) != '>')
						continue;
					i++;
					ref = IXMLCharEntity.GT_REF;
					break;
				default :
					continue;
			}

			if (ref != null) {
				if (buffer == null) {
					buffer = new StringBuffer(length + 8);
				}
				if (i > copiedLength) {
					buffer.append(source.substring(copiedLength, i));
				}
				buffer.append(ref);
				copiedLength = i + 1; // skip this character
			}
		}

		if (buffer != null) {
			if (copiedLength < length) {
				buffer.append(source.substring(copiedLength, length));
			}
			return buffer.toString();
		}
		return source;
	}

	/**
	 */
	private final boolean matchEndTag(String source, int offset, String endTagName) {
		if (source == null || endTagName == null)
			return false;
		int length = source.length();
		if (offset < 0 || offset >= length)
			return false;
		if (source.charAt(offset) != '/')
			return false;
		offset++;
		int end = offset + endTagName.length();
		if (end > length)
			return false;
		return endTagName.equalsIgnoreCase(source.substring(offset, end));
	}

	/**
	 */
	private final int skipEntityRef(String source, int offset) {
		if (source == null)
			return -1;
		if (offset < 0 || offset >= source.length())
			return -1;
		DocumentImpl document = (DocumentImpl) this.node.getOwnerDocument();
		if (document == null)
			return -1; // error

		int end = source.indexOf(';', offset);
		if (end < 0 || end == offset)
			return -1;
		String name = source.substring(offset, end);
		if (name == null || document.getCharValue(name) == null)
			return -1;
		return (end + 1 - offset);
	}

	/**
	 */
	private final int skipTag(String source, int offset) {
		if (source == null)
			return -1;
		if (offset < 0 || offset >= source.length())
			return -1;

		int end = offset;
		if (source.charAt(offset) == '%') {
			// JSP tag
			int found = source.indexOf(JSPTag.TAG_CLOSE, offset + 1);
			if (found < 0)
				return -1; // invalid JSP tag
			end = found + 2;
		} else {
			// normal tag
			int found = source.lastIndexOf('>');
			if (found < offset)
				return -1; // invalid tag
			end = found + 1;
		}
		return (end - offset);
	}

	/**
	 */
	public boolean validateSource(String source) throws InvalidCharacterException {
		if (source == null)
			return true;
		if (this.node == null)
			return false; // error
		String message = null;


		// setup validation conditions
		boolean acceptTag = false;
		boolean acceptClose = false;
		boolean acceptQuote = true;
		boolean acceptEntityRef = true;
		String endTagName = null;
		if (this.node.getNodeType() == Node.ATTRIBUTE_NODE) {
			IDOMDocument document = (IDOMDocument) this.node.getOwnerDocument();
			if (document != null && document.isJSPType())
				acceptTag = true;
			if (acceptTag) {
				Attr attr = (Attr) this.node;
				ElementImpl element = (ElementImpl) attr.getOwnerElement();
				if (element != null && element.isJSPTag())
					acceptTag = false;
			}
			// if the source does not include single quote,
			// double quote is valid
			acceptQuote = (source.indexOf('\'') < 0);
		} else if (this.node.getNodeType() == Node.TEXT_NODE) {
			TextImpl text = (TextImpl) this.node;
			if (text.isJSPContent()) {
				int index = source.indexOf(JSPTag.TAG_CLOSE);
				if (index < 0)
					return true;
				message = XMLCoreMessages.Invalid_character_gt_fo_ERROR_;
				throw new InvalidCharacterException(message, '>', index + 1);
			} else if (text.isCDATAContent()) {
				endTagName = text.getParentNode().getNodeName();
				if (endTagName == null)
					return false; // error
				acceptTag = true;
				acceptClose = true;
			} else if(hasNestedRegion(text)) {
				//there are cases (such as with EL) that the text is to complicated
				//	to be verified by this validator
				return true;
			}
		} else {
			IDOMDocument document = null;
			if (this.node.getNodeType() == Node.DOCUMENT_NODE) {
				document = (IDOMDocument) this.node;
			} else {
				document = (IDOMDocument) this.node.getOwnerDocument();
			}
			if (document != null && document.isJSPType())
				acceptTag = true;
		}

		char c = 0;
		int length = source.length();
		for (int i = 0; i < length; i++) {
			c = source.charAt(i);
			switch (c) {
				case '<' :
					if (acceptTag) {
						if (endTagName != null) {
							if (!matchEndTag(source, i + 1, endTagName))
								continue;
						} else {
							int skip = skipTag(source, i + 1);
							if (skip >= 0) {
								i += skip;
								continue;
							}
						}
						// invalid tag
					}
					message = XMLCoreMessages.Invalid_character_lt_fo_ERROR_;
					break;
				case '>' :
					if (acceptClose)
						continue;
					message = XMLCoreMessages.Invalid_character_gt_fo_ERROR_;
					break;
				case '&' :
					if (acceptEntityRef) {
						if (endTagName != null)
							continue;
						int skip = skipEntityRef(source, i + 1);
						if (skip >= 0) {
							i += skip;
							continue;
						}
						// invalid entity reference
					}
					message = XMLCoreMessages.Invalid_character_amp_fo_ERROR_;
					break;
				case '"' :
					if (acceptQuote)
						continue;
					message = XMLCoreMessages.Invalid_character__f_EXC_;
					break;
				default :
					continue;
			}

			if (message != null) {
				throw new InvalidCharacterException(message, c, i);
			}
		}

		return true;
	}
	
	/**
	 * True if the text has nested regions, meaning container is probably too
	 * complicated (like EL regions) to validate with this validator.
	 *
	 * @param text
	 * @return
	 */
	private boolean hasNestedRegion(TextImpl text) {
		boolean done = false;

		IStructuredDocumentRegion currRegion = text.getFirstStructuredDocumentRegion();
		IStructuredDocumentRegion lastRegion = text.getLastStructuredDocumentRegion();

		while(currRegion != null && !done) {
			
			ITextRegionList regions = currRegion.getRegions();
			for(int i = 0; i < regions.size(); ++i) {
				ITextRegion container = regions.get(i);
				if ((container instanceof ITextRegionContainer)) {
					ITextRegionList regions2 = ((ITextRegionContainer) container).getRegions();
					if (regions2 != null) {
						return true;
					}
				}
			}

			done = currRegion == lastRegion;
			currRegion = currRegion.getNext();
		}

		return false;
	}
}
