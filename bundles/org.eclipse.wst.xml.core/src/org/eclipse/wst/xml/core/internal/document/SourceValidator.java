/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.core.internal.document;



import org.eclipse.wst.sse.core.internal.nls.ResourceHandler1;
import org.eclipse.wst.xml.core.document.InvalidCharacterException;
import org.eclipse.wst.xml.core.document.JSPTag;
import org.eclipse.wst.xml.core.document.XMLCharEntity;
import org.eclipse.wst.xml.core.document.XMLDocument;
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
			XMLDocument document = (XMLDocument) this.node.getOwnerDocument();
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
		}
		else if (this.node.getNodeType() == Node.TEXT_NODE) {
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
			}
			else if (text.isCDATAContent()) {
				endTagName = text.getParentNode().getNodeName();
				if (endTagName == null)
					return null; // error
				acceptTag = true;
				acceptClose = true;
				acceptQuote = true;
				acceptAmpersand = true;
			}
		}
		else {
			XMLDocument document = null;
			if (this.node.getNodeType() == Node.DOCUMENT_NODE) {
				document = (XMLDocument) this.node;
			}
			else {
				document = (XMLDocument) this.node.getOwnerDocument();
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
						}
						else {
							int skip = skipTag(source, i + 1);
							if (skip >= 0) {
								i += skip;
								continue;
							}
						}
						// invalid JSP tag
					}
					ref = XMLCharEntity.LT_REF;
					break;
				case '>' :
					if (acceptClose)
						continue;
					ref = XMLCharEntity.GT_REF;
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
					ref = XMLCharEntity.AMP_REF;
					break;
				case '"' :
					if (acceptQuote)
						continue;
					ref = XMLCharEntity.QUOT_REF;
					break;
				case '%' :
					if (acceptJSPEnd)
						continue;
					if (source.charAt(i + 1) != '>')
						continue;
					i++;
					ref = XMLCharEntity.GT_REF;
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
		}
		else {
			// normal tag
			int found = source.indexOf('>', offset);
			if (found < 0)
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
			XMLDocument document = (XMLDocument) this.node.getOwnerDocument();
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
		}
		else if (this.node.getNodeType() == Node.TEXT_NODE) {
			TextImpl text = (TextImpl) this.node;
			if (text.isJSPContent()) {
				int index = source.indexOf(JSPTag.TAG_CLOSE);
				if (index < 0)
					return true;
				message = ResourceHandler1.getString("Invalid_character_('>')_fo_ERROR_"); //$NON-NLS-1$ = "Invalid character ('>') found"
				throw new InvalidCharacterException(message, '>', index + 1);
			}
			else if (text.isCDATAContent()) {
				endTagName = text.getParentNode().getNodeName();
				if (endTagName == null)
					return false; // error
				acceptTag = true;
				acceptClose = true;
			}
		}
		else {
			XMLDocument document = null;
			if (this.node.getNodeType() == Node.DOCUMENT_NODE) {
				document = (XMLDocument) this.node;
			}
			else {
				document = (XMLDocument) this.node.getOwnerDocument();
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
						}
						else {
							int skip = skipTag(source, i + 1);
							if (skip >= 0) {
								i += skip;
								continue;
							}
						}
						// invalid tag
					}
					message = ResourceHandler1.getString("Invalid_character_('<')_fo_ERROR_"); //$NON-NLS-1$ = "Invalid character ('<') found"
					break;
				case '>' :
					if (acceptClose)
						continue;
					message = ResourceHandler1.getString("Invalid_character_('>')_fo_ERROR_"); //$NON-NLS-1$ = "Invalid character ('>') found"
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
					message = ResourceHandler1.getString("Invalid_character_('&')_fo_ERROR_"); //$NON-NLS-1$ = "Invalid character ('&') found"
					break;
				case '"' :
					if (acceptQuote)
						continue;
					message = ResourceHandler1.getString("Invalid_character_('__')_f_EXC_"); //$NON-NLS-1$ = "Invalid character ('\"') found"
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
}
