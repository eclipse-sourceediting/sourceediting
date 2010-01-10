/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     David Carver (STAR) - bug 296999 - Inefficient use of new String()
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.document;



import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.document.ISourceGenerator;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


/**
 * XMLModelUpdater class
 */
public class XMLModelUpdater {
	private int diff = 0;
	private int gapLength = 0;
	private int gapOffset = 0;
	private IStructuredDocumentRegion gapStructuredDocumentRegion = null;
	private ISourceGenerator generator = null;
	private DOMModelImpl model = null;
	private NodeImpl nextNode = null;
	private NodeImpl parentNode = null;

	protected XMLModelUpdater(DOMModelImpl model) {
		super();

		if (model != null) {
			this.model = model;
			this.generator = model.getGenerator();
		}
	}

	/**
	 * changeAttrValue method
	 * 
	 * @param attrNode
	 *            org.w3c.dom.Attr
	 */
	private void changeAttrName(Attr attrNode) {
		if (attrNode == null)
			return;

		AttrImpl attr = (AttrImpl) attrNode;
		ElementImpl element = (ElementImpl) attr.getOwnerElement();
		if (element == null)
			return;

		if (element.isCommentTag()) {
			changeStartTag(element);
			return;
		}

		int offset = element.getStartOffset();
		int start = offset;
		int end = offset;

		String name = attr.getName();
		if (name == null)
			name = NodeImpl.EMPTY_STRING;
		ITextRegion nameRegion = attr.getNameRegion();
		if (nameRegion == null)
			return; // error
		start += nameRegion.getStart();
		// use getTextEnd() because getEnd() may include the tailing spaces
		end += nameRegion.getTextEnd();

		replaceSource(name, start, end);
	}

	/**
	 * changeAttrValue method
	 * 
	 * @param attrNode
	 *            org.w3c.dom.Attr
	 */
	private void changeAttrValue(Attr attrNode) {
		if (attrNode == null)
			return;

		AttrImpl attr = (AttrImpl) attrNode;
		ElementImpl element = (ElementImpl) attr.getOwnerElement();
		if (element == null)
			return;

		if (element.isCommentTag()) {
			changeStartTag(element);
			return;
		}

		int offset = element.getStartOffset();
		int start = offset;
		int end = offset;

		String value = null;
		ITextRegion valueRegion = attr.getValueRegion();
		if (valueRegion != null) {
			char quote = 0; // no quote preference
			// DW: 4/16/2003 due to change in structuredDocument ... we need a
			// flatnode to
			// get at region values. For now I'll assume this is always the
			// first
			// flatnode .. may need to make smarter later (e.g. to search for
			// the flatnode that this.valueRegion belongs to.
			IStructuredDocumentRegion documentRegion = element.getFirstStructuredDocumentRegion();
			String oldValue = documentRegion.getText(valueRegion);
			if (oldValue != null && oldValue.length() > 0) {
				char firstChar = oldValue.charAt(0);
				if (firstChar == '"' || firstChar == '\'') {
					quote = firstChar;
				}
			}

			ITextRegion startRegion = valueRegion;

			value = this.generator.generateAttrValue(attr, quote);
			if (value == null) {
				value = NodeImpl.EMPTY_STRING;
				// remove equal too
				ITextRegion equalRegion = attr.getEqualRegion();
				if (equalRegion != null)
					startRegion = equalRegion;
			}
			attr.setValueRegion(valueRegion); // reset value

			start += startRegion.getStart();
			// use getTextEnd() because getEnd() may include the tailing
			// spaces
			end += valueRegion.getTextEnd();
		}
		else {
			ITextRegion equalRegion = attr.getEqualRegion();

			value = this.generator.generateAttrValue(attr);
			if (value == null) {
				if (equalRegion == null)
					return; // nothng to do
				value = NodeImpl.EMPTY_STRING;
				// remove equal
				start += equalRegion.getStart();
				end += equalRegion.getTextEnd();
			}
			else {
				if (equalRegion != null) {
					// use getTextEnd() because getEnd() may include the
					// tailing spaces
					start += equalRegion.getTextEnd();
				}
				else {
					ITextRegion nameRegion = attr.getNameRegion();
					if (nameRegion == null)
						return; // must never happen
					// use getTextEnd() because getEnd() may include the
					// tailing spaces
					start += nameRegion.getTextEnd();
					value = '=' + value;
				}
				end = start;
			}
		}

		replaceSource(value, start, end);
	}

	/**
	 */
	void changeEndTag(Element element) {
		String source = this.generator.generateEndTag(element);
		if (source == null)
			return;
		int length = source.length();
		if (length == 0)
			return;

		ElementImpl impl = (ElementImpl) element;
		int offset = impl.getEndStartOffset();
		int start = offset;
		int end = offset;
		if (impl.hasEndTag()) {
			end = impl.getEndOffset();
			this.gapStructuredDocumentRegion = impl.getEndStructuredDocumentRegion();
			impl.setEndStructuredDocumentRegion(new StructuredDocumentRegionProxy(offset, length));
		}

		replaceSource(source, start, end);
	}

	/**
	 * changeName method
	 * 
	 * @param node
	 *            org.w3c.dom.Node
	 */
	void changeName(Node node) {
		if (node == null)
			return;
		if (getStructuredDocument() == null)
			return;

		// support changing name of attribute for setPrefix()
		short nodeType = node.getNodeType();
		if (nodeType == Node.ATTRIBUTE_NODE) {
			changeAttrName((Attr) node);
			return;
		}

		// not supported
		return;
	}

	void changeRegion(RegionChangedEvent change, IStructuredDocumentRegion flatNode, ITextRegion region) {
		if (change.getOffset() >= flatNode.getStartOffset() + region.getTextEnd()) {
			// change is entirely in white-space
			return;
		}

		NodeImpl root = (NodeImpl) this.model.getDocument();
		this.parentNode = root;
		this.nextNode = (NodeImpl) root.getFirstChild();

		removeGapStructuredDocumentRegion(flatNode);
		insertGapStructuredDocumentRegionBefore(flatNode.getStart());
		changeStructuredDocumentRegion(flatNode);
		insertGapStructuredDocumentRegionAfter(flatNode.getEnd());
	}

	/**
	 * This is a fallback method to regenerate the start tag.
	 */
	void changeStartTag(Element element) {
		if (element == null)
			return;
		ElementImpl impl = (ElementImpl) element;

		if (!impl.hasStartTag() && !impl.hasEndTag()) {
			// need to generate the start and the end tags
			Node parent = element.getParentNode();
			if (parent != null) {
				replaceChild(parent, element, element);
				return;
			}
			// else error
		}

		String source = this.generator.generateStartTag(element);
		if (source == null)
			return;
		int length = source.length();
		if (length == 0)
			return;

		int offset = impl.getStartOffset();
		int start = offset;
		int end = offset;
		if (impl.hasStartTag()) {
			end = impl.getStartEndOffset();
			this.gapStructuredDocumentRegion = impl.getStartStructuredDocumentRegion();
		}
		impl.setStartStructuredDocumentRegion(new StructuredDocumentRegionProxy(offset, length));

		replaceSource(source, start, end);
	}

	private void changeStructuredDocumentRegion(IStructuredDocumentRegion oldStructuredDocumentRegion) {
		if (oldStructuredDocumentRegion == null)
			return; // error
		if (this.parentNode == null)
			return; // error

		int oldOffset = oldStructuredDocumentRegion.getStart();
		int oldEnd = oldStructuredDocumentRegion.getEnd();
		boolean isEndTag = false;

		// find owner node
		NodeImpl ownerNode = null;
		while (this.parentNode != null) {
			if (this.nextNode != null) {
				IStructuredDocumentRegion nextStructuredDocumentRegion = this.nextNode.getStructuredDocumentRegion();
				if (nextStructuredDocumentRegion != null) {
					if (nextStructuredDocumentRegion == oldStructuredDocumentRegion) {
						ownerNode = this.nextNode;
						break;
					}
					int nextOffset = nextStructuredDocumentRegion.getStart();
					if (nextOffset == oldOffset) { // found
						ownerNode = this.nextNode;
						break;
					}
					if (this.nextNode.getNodeType() == Node.TEXT_NODE) {
						TextImpl text = (TextImpl) this.nextNode;
						if (text.hasStructuredDocumentRegion(oldStructuredDocumentRegion)) {
							ownerNode = this.nextNode;
							break;
						}
						int nextEnd = nextStructuredDocumentRegion.getEnd();
						if (nextOffset < oldEnd && nextEnd > oldOffset) {
							ownerNode = this.nextNode;
							break;
						}
					}
				}

				Node child = this.nextNode.getFirstChild();
				if (child != null) {
					this.parentNode = this.nextNode;
					this.nextNode = (NodeImpl) child;
					continue;
				}

				if (this.nextNode.getNodeType() == Node.ELEMENT_NODE) {
					this.parentNode = this.nextNode;
					this.nextNode = null;
					continue;
				}

				this.nextNode = (NodeImpl) this.nextNode.getNextSibling();
				if (this.nextNode != null)
					continue;
			}

			if (this.parentNode.getNodeType() == Node.ELEMENT_NODE) {
				ElementImpl element = (ElementImpl) this.parentNode;
				IStructuredDocumentRegion endStructuredDocumentRegion = element.getEndStructuredDocumentRegion();
				if (endStructuredDocumentRegion != null) {
					if (endStructuredDocumentRegion == oldStructuredDocumentRegion) {
						ownerNode = this.parentNode;
						isEndTag = true;
						break;
					}
					int endOffset = endStructuredDocumentRegion.getStart();
					if (endOffset == oldOffset) { // found
						ownerNode = this.parentNode;
						isEndTag = true;
						break;
					}
				}
			}

			this.nextNode = (NodeImpl) this.parentNode.getNextSibling();
			this.parentNode = (NodeImpl) this.parentNode.getParentNode();
		}
		if (ownerNode == null)
			throw new StructuredDocumentRegionManagementException();

		short nodeType = ownerNode.getNodeType();
		if (nodeType == Node.ELEMENT_NODE) {
			ElementImpl element = (ElementImpl) ownerNode;
			if (isEndTag) {
				element.setEndStructuredDocumentRegion(oldStructuredDocumentRegion);
			}
			else {
				element.setStartStructuredDocumentRegion(oldStructuredDocumentRegion);
				updateAttrRegions(element, oldStructuredDocumentRegion);
			}
		}
		else if (nodeType == Node.TEXT_NODE) {
			TextImpl text = (TextImpl) ownerNode;

			IStructuredDocumentRegion flatNode = text.getStructuredDocumentRegion();
			if (flatNode == oldStructuredDocumentRegion) {
				int newOffset = oldOffset;
				int newEnd = oldEnd;
				if (oldOffset == this.gapOffset) {
					newOffset += this.diff;
				}
				else {
					newEnd = this.gapOffset;
				}
				int newLength = newEnd - newOffset;
				IStructuredDocumentRegion newStructuredDocumentRegion = new StructuredDocumentRegionProxy(newOffset, newLength, oldStructuredDocumentRegion);
				text.setStructuredDocumentRegion(newStructuredDocumentRegion);

				if (oldEnd > newEnd) {
					this.nextNode = (NodeImpl) text.getNextSibling();
					changeStructuredDocumentRegion(oldStructuredDocumentRegion);
				}
				return;
			}

			if (flatNode instanceof StructuredDocumentRegionProxy) {
				StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) flatNode;
				int offset = proxy.getOffset();
				int end = offset + proxy.getLength();
				if (proxy.getStructuredDocumentRegion() == null) {
					if (offset == oldOffset && end == oldEnd) {
						text.setStructuredDocumentRegion(oldStructuredDocumentRegion);
					}
					else {
						if (end > oldEnd) {
							StructuredDocumentRegionContainer container = new StructuredDocumentRegionContainer();
							container.appendStructuredDocumentRegion(oldStructuredDocumentRegion);
							proxy.setOffset(oldEnd);
							proxy.setLength(end - oldEnd);
							container.appendStructuredDocumentRegion(proxy);
							text.setStructuredDocumentRegion(container);
						}
						else {
							proxy.setStructuredDocumentRegion(oldStructuredDocumentRegion);

							if (end < oldEnd) { // to be shared
								this.nextNode = (NodeImpl) text.getNextSibling();
								changeStructuredDocumentRegion(oldStructuredDocumentRegion);
							}
						}
					}
					return;
				}

				if (offset >= this.gapOffset) {
					proxy.setOffset(offset + this.diff);
					end += this.diff;
				}
				if (end < oldEnd) { // to be shared
					this.nextNode = (NodeImpl) text.getNextSibling();
					changeStructuredDocumentRegion(oldStructuredDocumentRegion);
					return;
				}
			}
			else if (flatNode instanceof StructuredDocumentRegionContainer) {
				StructuredDocumentRegionContainer container = (StructuredDocumentRegionContainer) flatNode;
				int count = container.getStructuredDocumentRegionCount();
				for (int i = 0; i < count; i++) {
					IStructuredDocumentRegion content = container.getStructuredDocumentRegion(i);
					if (content == null)
						continue; // error
					if (content == oldStructuredDocumentRegion) {
						int newOffset = oldOffset;
						int newEnd = oldEnd;
						if (oldOffset == this.gapOffset) {
							newOffset += this.diff;
						}
						else {
							newEnd = this.gapOffset;
						}
						int newLength = newEnd - newOffset;
						IStructuredDocumentRegion newStructuredDocumentRegion = new StructuredDocumentRegionProxy(newOffset, newLength, oldStructuredDocumentRegion);
						container.replaceStructuredDocumentRegion(newStructuredDocumentRegion, i);

						if (oldEnd > newEnd) { // to be shared
							this.nextNode = (NodeImpl) text.getNextSibling();
							changeStructuredDocumentRegion(oldStructuredDocumentRegion);
						}
						return;
					}

					if (content instanceof StructuredDocumentRegionProxy) {
						StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) content;
						int offset = proxy.getOffset();
						int end = offset + proxy.getLength();
						if (end <= oldOffset)
							continue;
						if (proxy.getStructuredDocumentRegion() == null) {
							if (offset == oldOffset && end == oldEnd) {
								container.replaceStructuredDocumentRegion(oldStructuredDocumentRegion, i);
							}
							else {
								if (end > oldEnd) {
									container.insertStructuredDocumentRegion(oldStructuredDocumentRegion, i);
									proxy.setOffset(oldEnd);
									proxy.setLength(end - oldEnd);
								}
								else {
									proxy.setStructuredDocumentRegion(oldStructuredDocumentRegion);

									if (end < oldEnd) { // to be shared
										this.nextNode = (NodeImpl) text.getNextSibling();
										changeStructuredDocumentRegion(oldStructuredDocumentRegion);
									}
								}
							}
							return;
						}

						if (offset >= this.gapOffset) {
							proxy.setOffset(offset + this.diff);
							end += this.diff;
						}
						if (end < oldEnd) { // to be shared
							this.nextNode = (NodeImpl) text.getNextSibling();
							changeStructuredDocumentRegion(oldStructuredDocumentRegion);
							return;
						}
					}
				}
			}
			else {
				throw new StructuredDocumentRegionManagementException();
			}
		}
		else {
			ownerNode.setStructuredDocumentRegion(oldStructuredDocumentRegion);
		}
	}

	/**
	 */
	private void changeTextData(Text text) {
		if (text == null)
			return;

		String source = this.generator.generateSource(text);
		if (source == null)
			source = NodeImpl.EMPTY_STRING;
		int length = source.length();

		TextImpl impl = (TextImpl) text;
		int start = impl.getStartOffset();
		int end = impl.getEndOffset();
		int offset = start;

		// make sure previous tag is closed
		Node prev = text.getPreviousSibling();
		if (prev != null) {
			String preTag = getCloseTag((IDOMNode) prev);
			if (preTag != null && preTag.length() > 0) {
				offset += preTag.length();
				source = preTag + source;
			}
		}
		else {
			Node parent = text.getParentNode();
			if (parent != null && parent.getNodeType() == Node.ELEMENT_NODE) {
				ElementImpl element = (ElementImpl) parent;
				String preTag = getStartCloseTag(element);
				if (preTag != null && preTag.length() > 0) {
					offset += preTag.length();
					StringBuffer buffer = new StringBuffer();
					buffer.append(preTag);
					buffer.append(source);
					if (text.getNextSibling() == null && !element.hasEndTag() && (element.isJSPContainer() || element.isCDATAContainer())) {
						// need to generate the end tag
						String postTag = this.generator.generateEndTag(element);
						if (postTag != null) {
							int postLength = postTag.length();
							if (postLength > 0) {
								buffer.append(postTag);
								int postOffset = offset + length;
								IStructuredDocumentRegion flatNode = new StructuredDocumentRegionProxy(postOffset, postLength);
								element.setEndStructuredDocumentRegion(flatNode);
							}
						}
					}
					source = buffer.toString();
				}
			}
		}

		this.gapStructuredDocumentRegion = impl.getStructuredDocumentRegion();
		IStructuredDocumentRegion newStructuredDocumentRegion = null;
		if (length > 0)
			newStructuredDocumentRegion = new StructuredDocumentRegionProxy(offset, length);
		impl.setStructuredDocumentRegion(newStructuredDocumentRegion);

		replaceSource(source, start, end);
	}

	/**
	 * changeValue method
	 * 
	 * @param node
	 *            org.w3c.dom.Node
	 */
	void changeValue(Node node) {
		if (node == null)
			return;
		if (getStructuredDocument() == null)
			return;

		short nodeType = node.getNodeType();
		if (nodeType == Node.TEXT_NODE) {
			changeTextData((Text) node);
			return;
		}
		if (nodeType == Node.ATTRIBUTE_NODE) {
			changeAttrValue((Attr) node);
			return;
		}
		if (nodeType == Node.ELEMENT_NODE) {
			changeStartTag((Element) node);
			return;
		}

		String source = this.generator.generateSource(node);
		if (source == null)
			source = NodeImpl.EMPTY_STRING;
		int length = source.length();

		NodeImpl impl = (NodeImpl) node;
		int start = impl.getStartOffset();
		int end = impl.getEndOffset();

		this.gapStructuredDocumentRegion = impl.getStructuredDocumentRegion();
		IStructuredDocumentRegion flatNode = null;
		if (length > 0)
			flatNode = new StructuredDocumentRegionProxy(start, length);
		impl.setStructuredDocumentRegion(flatNode);

		replaceSource(source, start, end);
	}

	/**
	 */
	private String getAttrValueClose(IDOMElement element) {
		if (element == null)
			return null;

		IStructuredDocumentRegion flatNode = element.getStartStructuredDocumentRegion();
		if (flatNode == null)
			return null;
		ITextRegion region = StructuredDocumentRegionUtil.getLastRegion(flatNode);
		if (region == null || region.getType() != DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE)
			return null;
		String value = flatNode.getText(region);
		if (value == null)
			return null;
		int length = value.length();
		if (length == 0)
			return null;

		// check open JSP tag
		boolean closeJSPTag = false;
		int offset = value.indexOf(JSPTag.TAG_OPEN);
		while (offset >= 0) {
			offset = value.indexOf(JSPTag.TAG_CLOSE, offset + 2);
			if (offset < 0) {
				closeJSPTag = true;
				break;
			}
			offset = value.indexOf(JSPTag.TAG_OPEN, offset + 2);
		}

		// check quote
		boolean closeQuote = false;
		char firstChar = value.charAt(0);
		if (firstChar == '"' || firstChar == '\'') {
			if (closeJSPTag || length == 1 || value.charAt(length - 1) != firstChar) {
				closeQuote = true;
			}
		}

		if (!closeJSPTag && !closeQuote)
			return null;

		StringBuffer buffer = new StringBuffer();
		if (closeJSPTag)
			buffer.append(JSPTag.TAG_CLOSE);
		if (closeQuote)
			buffer.append(firstChar);
		return buffer.toString();
	}

	/**
	 * Gather close tags recursively.
	 */
	private String getCloseTag(IDOMNode node) {
		if (node == null || node.isClosed())
			return null;

		if (node.getNodeType() != Node.ELEMENT_NODE) {
			return this.generator.generateCloseTag(node);
		}

		ElementImpl element = (ElementImpl) node;
		if (element.hasEndTag()) {
			// end tag is not closed
			return this.generator.generateCloseTag(element);
		}

		// no end tag
		int offset = element.getEndOffset();
		StringBuffer buffer = new StringBuffer();

		IDOMNode lastChild = (IDOMNode) element.getLastChild();
		if (lastChild == null) {
			if (!element.isStartTagClosed()) {
				if (element.preferEmptyTag())
					element.setEmptyTag(true);
				String closeTag = getStartCloseTag(element);
				if (closeTag != null) {
					int length = closeTag.length();
					if (length > 0) {
						buffer.append(closeTag);
						offset += length;
					}
				}
			}
		}
		else {
			String closeTag = getCloseTag(lastChild);
			if (closeTag != null) {
				int length = closeTag.length();
				if (length > 0) {
					buffer.append(closeTag);
					offset += length;
				}
			}
		}

		String endTag = this.generator.generateEndTag(element);
		if (endTag != null) {
			int length = endTag.length();
			if (length > 0) {
				buffer.append(endTag);
				IStructuredDocumentRegion flatNode = new StructuredDocumentRegionProxy(offset, length);
				element.setEndStructuredDocumentRegion(flatNode);
			}
		}

		return buffer.toString();
	}

	/**
	 */
	private String getStartCloseTag(IDOMElement element) {
		if (element == null || element.isStartTagClosed())
			return null;

		StringBuffer buffer = new StringBuffer();
		String attrValueClose = getAttrValueClose(element);
		if (attrValueClose != null)
			buffer.append(attrValueClose);
		String closeTag = this.generator.generateCloseTag(element);
		if (closeTag != null)
			buffer.append(closeTag);
		return buffer.toString();
	}

	private IStructuredDocument getStructuredDocument() {
		if (model == null)
			return null;
		return model.getStructuredDocument();
	}

	/**
	 */
	void initialize() {
		this.gapStructuredDocumentRegion = null;
		this.gapOffset = 0;
		this.gapLength = 0;
		this.diff = 0;
		this.parentNode = null;
		this.nextNode = null;
	}

	private void insertGapStructuredDocumentRegionAfter(int endOffset) {
		if (this.gapStructuredDocumentRegion == null)
			return;

		if (this.gapStructuredDocumentRegion instanceof StructuredDocumentRegionProxy) {
			StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) this.gapStructuredDocumentRegion;
			IStructuredDocumentRegion flatNode = proxy.getStructuredDocumentRegion();
			if (flatNode != null)
				insertStructuredDocumentRegion(flatNode);
		}
		else if (this.gapStructuredDocumentRegion instanceof StructuredDocumentRegionContainer) {
			StructuredDocumentRegionContainer container = (StructuredDocumentRegionContainer) this.gapStructuredDocumentRegion;
			int count = container.getStructuredDocumentRegionCount();
			for (int i = 0; i < count; i++) {
				IStructuredDocumentRegion content = container.getStructuredDocumentRegion(i);
				if (content == null)
					continue;
				if (content.getStart() < endOffset)
					continue;
				if (content instanceof StructuredDocumentRegionProxy) {
					StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) content;
					IStructuredDocumentRegion flatNode = proxy.getStructuredDocumentRegion();
					if (flatNode != null)
						insertStructuredDocumentRegion(flatNode);
				}
				else {
					insertStructuredDocumentRegion(content);
				}
			}
		}
		else {
			insertStructuredDocumentRegion(this.gapStructuredDocumentRegion);
		}
	}

	private void insertGapStructuredDocumentRegionBefore(int startOffset) {
		if (this.gapStructuredDocumentRegion == null)
			return;

		if (this.gapStructuredDocumentRegion instanceof StructuredDocumentRegionProxy) {
			StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) this.gapStructuredDocumentRegion;
			IStructuredDocumentRegion flatNode = proxy.getStructuredDocumentRegion();
			if (flatNode != null)
				insertStructuredDocumentRegion(flatNode);
		}
		else if (this.gapStructuredDocumentRegion instanceof StructuredDocumentRegionContainer) {
			StructuredDocumentRegionContainer container = (StructuredDocumentRegionContainer) this.gapStructuredDocumentRegion;
			int count = container.getStructuredDocumentRegionCount();
			for (int i = 0; i < count; i++) {
				IStructuredDocumentRegion content = container.getStructuredDocumentRegion(i);
				if (content == null)
					continue;
				if (content.getStart() >= startOffset)
					return;
				if (content instanceof StructuredDocumentRegionProxy) {
					StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) content;
					IStructuredDocumentRegion flatNode = proxy.getStructuredDocumentRegion();
					if (flatNode != null)
						insertStructuredDocumentRegion(flatNode);
				}
				else {
					insertStructuredDocumentRegion(content);
				}
			}
		}
		else {
			insertStructuredDocumentRegion(this.gapStructuredDocumentRegion);
		}
	}

	/**
	 */
	private void insertStructuredDocumentRegion(IStructuredDocumentRegion newStructuredDocumentRegion) {
		if (newStructuredDocumentRegion == null)
			return; // error
		if (this.parentNode == null)
			return; // error

		int newOffset = newStructuredDocumentRegion.getStart();
		int newEnd = newStructuredDocumentRegion.getEnd();
		boolean isEndTag = false;

		// find owner node
		NodeImpl ownerNode = null;
		while (this.parentNode != null) {
			if (this.nextNode != null) {
				IStructuredDocumentRegion nextStructuredDocumentRegion = this.nextNode.getStructuredDocumentRegion();
				if (nextStructuredDocumentRegion != null) {
					int nextOffset = nextStructuredDocumentRegion.getStart();
					if (nextOffset == newOffset) { // found
						ownerNode = this.nextNode;
						break;
					}
					if (this.nextNode.getNodeType() == Node.TEXT_NODE) {
						int nextEnd = nextStructuredDocumentRegion.getEnd();
						if (nextOffset < newEnd && nextEnd > newOffset) {
							ownerNode = this.nextNode;
							break;
						}
					}
				}

				Node child = this.nextNode.getFirstChild();
				if (child != null) {
					this.parentNode = this.nextNode;
					this.nextNode = (NodeImpl) child;
					continue;
				}

				if (this.nextNode.getNodeType() == Node.ELEMENT_NODE) {
					this.parentNode = this.nextNode;
					this.nextNode = null;
					continue;
				}

				this.nextNode = (NodeImpl) this.nextNode.getNextSibling();
				if (this.nextNode != null)
					continue;
			}

			if (this.parentNode.getNodeType() == Node.ELEMENT_NODE) {
				ElementImpl element = (ElementImpl) this.parentNode;
				IStructuredDocumentRegion endStructuredDocumentRegion = element.getEndStructuredDocumentRegion();
				if (endStructuredDocumentRegion != null) {
					int endOffset = endStructuredDocumentRegion.getStart();
					if (endOffset == newOffset) { // found
						ownerNode = this.parentNode;
						isEndTag = true;
						break;
					}
				}
			}

			this.nextNode = (NodeImpl) this.parentNode.getNextSibling();
			this.parentNode = (NodeImpl) this.parentNode.getParentNode();
		}
		if (ownerNode == null)
			throw new StructuredDocumentRegionManagementException();

		short nodeType = ownerNode.getNodeType();
		if (nodeType == Node.ELEMENT_NODE) {
			ElementImpl element = (ElementImpl) ownerNode;
			if (isEndTag) {
				element.setEndStructuredDocumentRegion(newStructuredDocumentRegion);
			}
			else {
				element.setStartStructuredDocumentRegion(newStructuredDocumentRegion);
				updateAttrRegions(element, newStructuredDocumentRegion);
			}
		}
		else if (nodeType == Node.TEXT_NODE) {
			TextImpl text = (TextImpl) ownerNode;
			IStructuredDocumentRegion oldStructuredDocumentRegion = text.getStructuredDocumentRegion();
			if (oldStructuredDocumentRegion == null) {
				throw new StructuredDocumentRegionManagementException();
			}
			int oldOffset = oldStructuredDocumentRegion.getStart();
			int oldEnd = oldStructuredDocumentRegion.getEnd();
			if (oldOffset == newOffset && oldEnd == newEnd) {
				text.setStructuredDocumentRegion(newStructuredDocumentRegion);
				return;
			}

			if (oldStructuredDocumentRegion instanceof StructuredDocumentRegionProxy) {
				StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) oldStructuredDocumentRegion;
				if (oldEnd > newEnd) {
					StructuredDocumentRegionContainer container = new StructuredDocumentRegionContainer();
					if (oldOffset == newOffset) {
						container.appendStructuredDocumentRegion(newStructuredDocumentRegion);
					}
					else {
						StructuredDocumentRegionProxy newProxy = new StructuredDocumentRegionProxy();
						newProxy.setOffset(oldOffset);
						newProxy.setLength(newEnd - oldOffset);
						newProxy.setStructuredDocumentRegion(newStructuredDocumentRegion);
						container.appendStructuredDocumentRegion(newProxy);
					}
					proxy.setOffset(newEnd);
					proxy.setLength(oldEnd - newEnd);
					container.appendStructuredDocumentRegion(proxy);
					text.setStructuredDocumentRegion(container);
				}
				else {
					proxy.setStructuredDocumentRegion(newStructuredDocumentRegion);

					if (oldEnd < newEnd) { // to be shared
						this.nextNode = (NodeImpl) text.getNextSibling();
						insertStructuredDocumentRegion(newStructuredDocumentRegion);
					}
				}
				return;
			}

			if (oldStructuredDocumentRegion instanceof StructuredDocumentRegionContainer) {
				StructuredDocumentRegionContainer container = (StructuredDocumentRegionContainer) oldStructuredDocumentRegion;
				int count = container.getStructuredDocumentRegionCount();
				for (int i = 0; i < count; i++) {
					IStructuredDocumentRegion content = container.getStructuredDocumentRegion(i);
					if (content == null)
						continue; // error
					int offset = content.getStart();
					int end = content.getEnd();
					if (end <= newOffset)
						continue;
					if (offset == newOffset && end == newEnd) {
						container.replaceStructuredDocumentRegion(newStructuredDocumentRegion, i);
						return;
					}

					if (content instanceof StructuredDocumentRegionProxy) {
						StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) content;
						if (end > newEnd) {
							if (offset == newOffset) {
								container.insertStructuredDocumentRegion(newStructuredDocumentRegion, i);
							}
							else {
								StructuredDocumentRegionProxy newProxy = new StructuredDocumentRegionProxy();
								newProxy.setOffset(offset);
								newProxy.setLength(newEnd - offset);
								newProxy.setStructuredDocumentRegion(newStructuredDocumentRegion);
								container.insertStructuredDocumentRegion(newProxy, i);
							}
							proxy.setOffset(newEnd);
							proxy.setLength(end - newEnd);
							return;
						}
						else {
							proxy.setStructuredDocumentRegion(newStructuredDocumentRegion);
							if (end == newEnd)
								return;
						}
					}
				}

				if (oldEnd < newEnd) { // to be shared
					this.nextNode = (NodeImpl) text.getNextSibling();
					insertStructuredDocumentRegion(newStructuredDocumentRegion);
				}
				return;
			}
			else {
				throw new StructuredDocumentRegionManagementException();
			}
		}
		else {
			ownerNode.setStructuredDocumentRegion(newStructuredDocumentRegion);
		}
	}

	private void removeGapStructuredDocumentRegion(IStructuredDocumentRegion oldStructuredDocumentRegion) {
		if (this.gapStructuredDocumentRegion == null)
			return;

		if (this.gapStructuredDocumentRegion == oldStructuredDocumentRegion) {
			this.gapStructuredDocumentRegion = null;
			return;
		}

		if (this.gapStructuredDocumentRegion instanceof StructuredDocumentRegionProxy) {
			StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) this.gapStructuredDocumentRegion;
			IStructuredDocumentRegion flatNode = proxy.getStructuredDocumentRegion();
			if (flatNode == oldStructuredDocumentRegion)
				this.gapStructuredDocumentRegion = null;
		}
		else if (this.gapStructuredDocumentRegion instanceof StructuredDocumentRegionContainer) {
			StructuredDocumentRegionContainer container = (StructuredDocumentRegionContainer) this.gapStructuredDocumentRegion;
			int count = container.getStructuredDocumentRegionCount();
			for (int i = 0; i < count; i++) {
				IStructuredDocumentRegion content = container.getStructuredDocumentRegion(i);
				if (content == null)
					continue;
				if (content == oldStructuredDocumentRegion) {
					if (count > 1)
						container.removeStructuredDocumentRegion(i);
					else
						this.gapStructuredDocumentRegion = null;
					return;
				}
				if (content instanceof StructuredDocumentRegionProxy) {
					StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) content;
					if (proxy.getStructuredDocumentRegion() == oldStructuredDocumentRegion) {
						if (count > 1)
							container.removeStructuredDocumentRegion(i);
						else
							this.gapStructuredDocumentRegion = null;
						return;
					}
				}
			}
		}
	}

	private void removeStructuredDocumentRegion(IStructuredDocumentRegion oldStructuredDocumentRegion) {
		if (oldStructuredDocumentRegion == null)
			return; // error
		if (this.parentNode == null)
			return; // error

		int gapEnd = this.gapOffset + this.gapLength;
		int oldOffset = oldStructuredDocumentRegion.getStart();
		int oldEnd = oldStructuredDocumentRegion.getEnd();
		if (oldOffset >= this.gapOffset && oldEnd <= gapEnd)
			return; // do nothing
		int oldLength = oldEnd - oldOffset;
		if (oldOffset >= gapEnd)
			oldOffset += this.diff;

		// find owner node
		NodeImpl ownerNode = null;
		ElementImpl ownerEndTag = null;
		TextImpl ownerText = null;
		while (this.parentNode != null) {
			if (this.nextNode != null) {
				if (this.nextNode.getStructuredDocumentRegion() == oldStructuredDocumentRegion) {
					ownerNode = this.nextNode;
					break;
				}
				if (this.nextNode.getNodeType() == Node.TEXT_NODE) {
					TextImpl text = (TextImpl) this.nextNode;
					if (text.hasStructuredDocumentRegion(oldStructuredDocumentRegion)) {
						ownerNode = this.nextNode;
						ownerText = text;
						break;
					}
				}

				Node child = this.nextNode.getFirstChild();
				if (child != null) {
					this.parentNode = this.nextNode;
					this.nextNode = (NodeImpl) child;
					continue;
				}

				if (this.nextNode.getNodeType() == Node.ELEMENT_NODE) {
					this.parentNode = this.nextNode;
					this.nextNode = null;
					continue;
				}

				this.nextNode = (NodeImpl) this.nextNode.getNextSibling();
				if (this.nextNode != null)
					continue;
			}

			if (this.parentNode.getNodeType() == Node.ELEMENT_NODE) {
				ElementImpl element = (ElementImpl) this.parentNode;
				if (element.getEndStructuredDocumentRegion() == oldStructuredDocumentRegion) {
					ownerNode = this.parentNode;
					ownerEndTag = element;
					break;
				}
			}

			this.nextNode = (NodeImpl) this.parentNode.getNextSibling();
			this.parentNode = (NodeImpl) this.parentNode.getParentNode();
		}
		if (ownerNode == null)
			throw new StructuredDocumentRegionManagementException();

		if (ownerText != null) {
			IStructuredDocumentRegion flatNode = ownerText.getStructuredDocumentRegion();
			if (flatNode == oldStructuredDocumentRegion) {
				IStructuredDocumentRegion newStructuredDocumentRegion = new StructuredDocumentRegionProxy(oldOffset, oldLength);
				ownerText.setStructuredDocumentRegion(newStructuredDocumentRegion);
				return;
			}

			if (flatNode instanceof StructuredDocumentRegionProxy) {
				StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) flatNode;
				if (proxy.getStructuredDocumentRegion() != oldStructuredDocumentRegion) {
					throw new StructuredDocumentRegionManagementException();
				}
				int offset = proxy.getOffset();
				int end = offset + proxy.getLength();
				if (offset >= this.gapOffset) {
					proxy.setOffset(offset + this.diff);
				}
				proxy.setStructuredDocumentRegion(null);
				if (end < oldEnd && (end < this.gapOffset || oldEnd > gapEnd)) { // has
					// shared
					removeStructuredDocumentRegion(oldStructuredDocumentRegion);
					return;
				}
			}
			else if (flatNode instanceof StructuredDocumentRegionContainer) {
				StructuredDocumentRegionContainer container = (StructuredDocumentRegionContainer) flatNode;
				int count = container.getStructuredDocumentRegionCount();
				for (int i = 0; i < count; i++) {
					IStructuredDocumentRegion content = container.getStructuredDocumentRegion(i);
					if (content == null)
						continue; // error
					if (content == oldStructuredDocumentRegion) {
						IStructuredDocumentRegion newStructuredDocumentRegion = new StructuredDocumentRegionProxy(oldOffset, oldLength);
						container.replaceStructuredDocumentRegion(newStructuredDocumentRegion, i);
						return;
					}

					if (content instanceof StructuredDocumentRegionProxy) {
						StructuredDocumentRegionProxy proxy = (StructuredDocumentRegionProxy) content;
						if (proxy.getStructuredDocumentRegion() == oldStructuredDocumentRegion) {
							int offset = proxy.getOffset();
							int end = offset + proxy.getLength();
							if (offset >= this.gapOffset) {
								proxy.setOffset(offset + this.diff);
							}
							proxy.setStructuredDocumentRegion(null);
							if (end < oldEnd && (end < this.gapOffset || oldEnd > gapEnd)) { // has
								// shared
								removeStructuredDocumentRegion(oldStructuredDocumentRegion);
								return;
							}
						}
					}
				}
			}
			else {
				throw new StructuredDocumentRegionManagementException();
			}
		}
		else {
			IStructuredDocumentRegion newStructuredDocumentRegion = new StructuredDocumentRegionProxy(oldOffset, oldLength);
			if (ownerEndTag != null) {
				ownerEndTag.setEndStructuredDocumentRegion(newStructuredDocumentRegion);
			}
			else {
				ownerNode.setStructuredDocumentRegion(newStructuredDocumentRegion);
			}
		}
	}

	/**
	 * replaceAttr method
	 * 
	 * @param ownerElement
	 *            org.w3c.dom.Element
	 * @param newAttr
	 *            org.w3c.dom.Attr
	 * @param oldAttr
	 *            org.w3c.dom.Attr
	 */
	void replaceAttr(Element ownerElement, Attr newAttr, Attr oldAttr) {
		if (ownerElement == null)
			return;
		if (getStructuredDocument() == null)
			return;

		ElementImpl element = (ElementImpl) ownerElement;
		if (!element.hasStartTag()) {
			changeStartTag(element);
			return;
		}
		if (element.isCommentTag()) {
			changeStartTag(element);
			return;
		}

		int offset = element.getStartOffset();
		int start = offset;
		int end = offset;

		boolean insertSpace = false;
		String attrValueClose = null;
		if (oldAttr != null) {
			AttrImpl impl = (AttrImpl) oldAttr;
			ITextRegion nameRegion = impl.getNameRegion();
			if (nameRegion == null)
				return; // must never happen
			ITextRegion lastRegion = impl.getValueRegion();
			if (lastRegion != null) {
				end += lastRegion.getEnd();
			}
			else {
				lastRegion = impl.getEqualRegion();
				if (lastRegion != null) {
					end += lastRegion.getEnd();
				}
				else {
					end += nameRegion.getEnd();
					lastRegion = nameRegion;
				}
			}
			// check there are extra space before the last attribute
			IStructuredDocumentRegion flatNode = element.getStartStructuredDocumentRegion();
			if (flatNode == null)
				return; // must never happen
			ITextRegionList regions = flatNode.getRegions();
			if (regions == null)
				return; // must never happen
			ITextRegion prevRegion = null;
			ITextRegion nextRegion = null;
			for (int i = 0; i < regions.size(); i++) {
				ITextRegion region = regions.get(i);
				if (region == nameRegion) {
					if (i > 0) {
						prevRegion = regions.get(i - 1);
					}
				}
				if (region == lastRegion) {
					if (i + 1 < regions.size()) {
						nextRegion = regions.get(i + 1);
					}
					break;
				}
			}
			boolean isLastAttr = false;
			if (nextRegion != null) {
				String regionType = nextRegion.getType();
				if (regionType == DOMRegionContext.XML_TAG_CLOSE || regionType == DOMRegionContext.XML_EMPTY_TAG_CLOSE || isNestedTagClose(regionType)) {
					isLastAttr = true;
				}
			}
			if (isLastAttr && prevRegion != null) {
				start += prevRegion.getTextEnd();
			}
			else {
				start += nameRegion.getStart();
			}

			// impl.resetRegions(ownerElement);
			impl.resetRegions(element);
		}
		else { // append attribute
			IStructuredDocumentRegion flatNode = element.getStartStructuredDocumentRegion();
			if (flatNode == null)
				return; // must never happen

			attrValueClose = getAttrValueClose(element);
			if (attrValueClose != null && attrValueClose.length() > 0) {
				insertSpace = true;
				start = flatNode.getEndOffset();
				end = start;
			}
			else {
				ITextRegionList regions = flatNode.getRegions();
				if (regions == null)
					return; // must never happen
				int attrStart = 0;
				for (int i = regions.size() - 1; i >= 0; i--) {
					ITextRegion region = regions.get(i);
					String regionType = region.getType();
					if (regionType == DOMRegionContext.XML_TAG_CLOSE || regionType == DOMRegionContext.XML_EMPTY_TAG_CLOSE || isNestedTagClose(regionType))
						continue;
					int regionEnd = region.getEnd();
					if (regionEnd == region.getTextEnd())
						insertSpace = true;
					attrStart = regionEnd;
					break;
				}
				if (attrStart == 0)
					return; // not found, must never happen
				start += attrStart;
				end = start;
			}
		}

		String source = null;
		if (newAttr != null) {
			int size = 2;
			if (attrValueClose != null)
				size += attrValueClose.length();
			String name = this.generator.generateAttrName(newAttr);
			if (name != null)
				size += name.length();
			String value = this.generator.generateAttrValue(newAttr);
			if (value != null)
				size += value.length();
			StringBuffer buffer = new StringBuffer(size);
			if (attrValueClose != null)
				buffer.append(attrValueClose);
			if (insertSpace)
				buffer.append(' ');
			buffer.append(name);
			if (value != null) {
				buffer.append('=');
				buffer.append(value);
			}
			source = buffer.toString();
		}

		replaceSource(source, start, end);
	}

	protected boolean isNestedTagClose(String regionType) {
		boolean result = false;
		return result;
	}

	/**
	 * replaceChild method
	 * 
	 * @param parentNode
	 *            org.w3c.dom.Node
	 * @param newChild
	 *            org.w3c.dom.Node
	 * @param oldChild
	 *            org.w3c.dom.Node
	 */
	void replaceChild(Node parentNode, Node newChild, Node oldChild) {
		if (parentNode == null)
			return;
		if (newChild == null && oldChild == null)
			return;
		if (getStructuredDocument() == null)
			return;

		int start = 0;
		int end = 0;
		String preTag = null;
		String postTag = null;
		ElementImpl postElement = null;
		if (oldChild != null) {
			NodeImpl node = (NodeImpl) oldChild;
			start = node.getStartOffset();
			end = node.getEndOffset();
			if (oldChild.getNodeType() == Node.TEXT_NODE) {
				this.gapStructuredDocumentRegion = node.getStructuredDocumentRegion();
			}
			node.resetStructuredDocumentRegions(); // reset values from
			// IStructuredDocumentRegion
		}
		else {
			NodeImpl prev = (NodeImpl) newChild.getPreviousSibling();
			if (prev != null) {
				start = prev.getEndOffset();
				end = start;
				preTag = getCloseTag(prev);
			}
			else {
				// first child
				NodeImpl next = (NodeImpl) newChild.getNextSibling();
				if (next != null) {
					start = next.getStartOffset();
					end = start;
					if (parentNode.getNodeType() == Node.ELEMENT_NODE) {
						preTag = getStartCloseTag((IDOMElement) parentNode);
					}
				}
				else {
					// newly having a child
					if (parentNode.getNodeType() == Node.ELEMENT_NODE) {
						ElementImpl element = (ElementImpl) parentNode;
						if (element.isEmptyTag()) { // empty tag format
							// need to generate the start and the end tags
							end = element.getEndOffset();
							start = end - 2; // for "/>"
							element.setEmptyTag(false);
							preTag = this.generator.generateCloseTag(element);
							postTag = this.generator.generateEndTag(element);
							postElement = element;
						}
						else if (!element.hasStartTag()) {
							start = element.getStartOffset();
							end = start;
							// invalid end tag or implicit tag
							// need to generate the start tag
							preTag = this.generator.generateStartTag(element);
							if (preTag != null) {
								int length = preTag.length();
								if (length > 0) {
									IStructuredDocumentRegion flatNode = new StructuredDocumentRegionProxy(start, length);
									element.setStartStructuredDocumentRegion(flatNode);
								}
							}
							if (!element.hasEndTag()) {
								// implicit tag
								// need to generate the end tags
								postTag = this.generator.generateEndTag(element);
								postElement = element;
							}
						}
						else {
							start = element.getStartEndOffset();
							end = start;
							preTag = getStartCloseTag(element);
							if (preTag != null && preTag.length() > 0) {
								if (!element.hasEndTag() && (element.isJSPContainer() || element.isCDATAContainer())) {
									// need to generate the end tag
									postTag = this.generator.generateEndTag(element);
									postElement = element;
								}
							}
						}
					}
					// else might DOCUMENT_NODE, start and end are 0
				}
			}
		}

		String source = null;
		if (newChild != null) {
			StringBuffer buffer = new StringBuffer();
			int offset = start;
			if (preTag != null) {
				int length = preTag.length();
				if (length > 0) {
					offset += length;
					buffer.append(preTag);
				}
			}

			NodeImpl node = (NodeImpl) newChild;
			while (node != null) {
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					ElementImpl element = (ElementImpl) node;
					if (element.preferEmptyTag())
						element.setEmptyTag(true);
					IStructuredDocumentRegion flatNode = null;
					String startTag = this.generator.generateStartTag(element);
					if (startTag != null) {
						int length = startTag.length();
						if (length > 0) {
							buffer.append(startTag);
							flatNode = new StructuredDocumentRegionProxy(offset, length);
							offset += length;
						}
					}
					element.setStartStructuredDocumentRegion(flatNode);
				}
				else {
					String content = this.generator.generateSource(node);
					if (content == null)
						content = NodeImpl.EMPTY_STRING;
					int length = content.length();
					IStructuredDocumentRegion flatNode = null;
					if (length > 0) {
						buffer.append(content);
						flatNode = new StructuredDocumentRegionProxy(offset, length);
						offset += length;
					}
					node.setStructuredDocumentRegion(flatNode);
				}

				NodeImpl child = (NodeImpl) node.getFirstChild();
				if (child != null) {
					node = child;
					continue;
				}

				if (node.getNodeType() == Node.ELEMENT_NODE) {
					ElementImpl element = (ElementImpl) node;
					IStructuredDocumentRegion flatNode = null;
					String endTag = this.generator.generateEndTag(element);
					if (endTag != null) {
						int length = endTag.length();
						if (length > 0) {
							buffer.append(endTag);
							flatNode = new StructuredDocumentRegionProxy(offset, length);
							offset += length;
						}
					}
					element.setEndStructuredDocumentRegion(flatNode);
				}

				while (node != null) {
					if (node == newChild) {
						node = null;
						break;
					}
					NodeImpl next = (NodeImpl) node.getNextSibling();
					if (next != null) {
						node = next;
						break;
					}

					node = (NodeImpl) node.getParentNode();
					if (node.getNodeType() != Node.ELEMENT_NODE)
						continue;
					ElementImpl element = (ElementImpl) node;
					IStructuredDocumentRegion flatNode = null;
					String endTag = this.generator.generateEndTag(element);
					if (endTag != null) {
						int length = endTag.length();
						if (length > 0) {
							buffer.append(endTag);
							flatNode = new StructuredDocumentRegionProxy(offset, length);
							offset += length;
						}
					}
					element.setEndStructuredDocumentRegion(flatNode);
				}
			}

			if (postTag != null) {
				int length = postTag.length();
				if (length > 0) {
					buffer.append(postTag);
					if (postElement != null) {
						IStructuredDocumentRegion flatNode = new StructuredDocumentRegionProxy(offset, length);
						postElement.setEndStructuredDocumentRegion(flatNode);
					}
				}
			}
			source = buffer.toString();
		}

		if (start == end && (source == null || source.length() == 0)) {
			// no thing changed
			return;
		}

		replaceSource(source, start, end);
	}

	void replaceRegions(IStructuredDocumentRegion flatNode, ITextRegionList newRegions, ITextRegionList oldRegions) {
		// future_TODO: optimize

		NodeImpl root = (NodeImpl) this.model.getDocument();
		this.parentNode = root;
		this.nextNode = (NodeImpl) root.getFirstChild();

		removeGapStructuredDocumentRegion(flatNode);
		insertGapStructuredDocumentRegionBefore(flatNode.getStart());
		changeStructuredDocumentRegion(flatNode);
		insertGapStructuredDocumentRegionAfter(flatNode.getEnd());
	}

	/**
	 * Wraps IStructuredDocumentRegion.replaceText() and sets contextual
	 * information.
	 */
	private void replaceSource(String source, int start, int end) {
		int inserted = 0;
		if (source == null)
			source = NodeImpl.EMPTY_STRING;
		else
			inserted = source.length();
		int removed = end - start;
		if (inserted == 0 && removed == 0)
			return;

		this.gapOffset = start;
		this.gapLength = removed;
		this.diff = inserted - removed;
		// Note: due to bug
		// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=3619
		// for now assume "ignore readonly" region is ok -- assume DOM itself
		// checks if
		// ok to insert or not. In reality, we may have to make or "contains"
		// method more
		// better. Or, we may have to "perculate up" the parameter for clients
		// to tell us programatically
		// that its ok to insert/format in a read-only region.
		getStructuredDocument().replaceText(this.model, this.gapOffset, this.gapLength, source, true);
	}

	void replaceStructuredDocumentRegions(IStructuredDocumentRegionList newStructuredDocumentRegions, IStructuredDocumentRegionList oldStructuredDocumentRegions) {
		NodeImpl root = (NodeImpl) this.model.getDocument();

		if (oldStructuredDocumentRegions != null) {
			this.parentNode = root;
			this.nextNode = (NodeImpl) root.getFirstChild();

			Enumeration e = oldStructuredDocumentRegions.elements();
			while (e.hasMoreElements()) {
				IStructuredDocumentRegion flatNode = (IStructuredDocumentRegion) e.nextElement();
				if (flatNode == null)
					continue;
				removeStructuredDocumentRegion(flatNode);
				removeGapStructuredDocumentRegion(flatNode);
			}
		}

		if (newStructuredDocumentRegions != null) {
			this.parentNode = root;
			this.nextNode = (NodeImpl) root.getFirstChild();

			IStructuredDocumentRegion lastStructuredDocumentRegion = null;
			Enumeration e = newStructuredDocumentRegions.elements();
			while (e.hasMoreElements()) {
				IStructuredDocumentRegion flatNode = (IStructuredDocumentRegion) e.nextElement();
				if (flatNode == null)
					continue;
				if (lastStructuredDocumentRegion == null)
					insertGapStructuredDocumentRegionBefore(flatNode.getStart());
				insertStructuredDocumentRegion(flatNode);
				lastStructuredDocumentRegion = flatNode;
			}
			if (lastStructuredDocumentRegion != null) {
				insertGapStructuredDocumentRegionAfter(lastStructuredDocumentRegion.getEnd());
			}
			else {
				insertGapStructuredDocumentRegionBefore(this.gapOffset);
				// make sure to restore all backuped StructuredDocumentRegions
				insertGapStructuredDocumentRegionAfter(this.gapOffset);
			}
		}
		else {
			this.parentNode = root;
			this.nextNode = (NodeImpl) root.getFirstChild();

			insertGapStructuredDocumentRegionBefore(this.gapOffset);
			// make sure to restore all backuped StructuredDocumentRegions
			insertGapStructuredDocumentRegionAfter(this.gapOffset);
		}
	}

	/**
	 */
	private void updateAttrRegions(Element element, IStructuredDocumentRegion flatNode) {

		// update attributes
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return;
		NamedNodeMap attributes = element.getAttributes();
		if (attributes == null)
			return;
		int index = -1;
		AttrImpl attr = null;
		Iterator e = regions.iterator();
		while (e.hasNext()) {
			ITextRegion region = (ITextRegion) e.next();
			String regionType = region.getType();
			if (regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
				attr = (AttrImpl) attributes.item(++index);
				if (attr != null) {
					attr.setNameRegion(region);
					// reset other regions
					attr.setEqualRegion(null);
					attr.setValueRegion(null);
				}
			}
			else if (regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
				if (attr != null)
					attr.setEqualRegion(region);
			}
			else if (regionType == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				if (attr != null) {
					attr.setValueRegion(region);
					attr = null;
				}
			}
		}
	}
}
