/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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



import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.xml.core.commentelement.impl.CommentElementConfiguration;
import org.eclipse.wst.xml.core.commentelement.impl.CommentElementRegistry;
import org.eclipse.wst.xml.core.document.JSPTag;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLElement;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.parser.XMLRegionContext;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


/**
 * XMLModelParser
 */
public class XMLModelParser implements org.eclipse.wst.xml.core.jsp.model.parser.temp.XMLJSPRegionContexts {
	private ModelParserAdapter adapter = null;
	private XMLModelContext context = null;
	private DocumentImpl document = null;

	private XMLModelImpl model = null;

	/**
	 */
	protected XMLModelParser(XMLModelImpl model) {
		super();

		if (model != null) {
			this.model = model;
			this.document = (DocumentImpl) model.getDocument();
			if (this.document != null) {
				this.adapter = (ModelParserAdapter) this.document.getAdapterFor(ModelParserAdapter.class);
			}
		}
	}

	/**
	 */
	protected boolean canBeImplicitTag(Element element) {
		if (this.adapter != null) {
			return this.adapter.canBeImplicitTag(element);
		}
		return false;
	}

	/**
	 */
	protected boolean canBeImplicitTag(Element element, Node child) {
		if (this.adapter != null) {
			return this.adapter.canBeImplicitTag(element, child);
		}
		return false;
	}

	/**
	 */
	protected boolean canContain(Element element, Node child) {
		if (element == null || child == null)
			return false;
		ElementImpl impl = (ElementImpl) element;
		if (impl.isEndTag())
			return false; // invalid (floating) end tag
		if (!impl.isContainer())
			return false;
		if (child.getNodeType() != Node.TEXT_NODE) {
			if (impl.isJSPContainer() || impl.isCDATAContainer()) {
				// accepts only Text child
				return false;
			}
		}
		if (this.adapter != null) {
			return this.adapter.canContain(element, child);
		}
		return true;
	}

	/**
	 */
	private void changeAttrEqual(IStructuredDocumentRegion flatNode, ITextRegion region) {
		int offset = flatNode.getStart();
		if (offset < 0)
			return;
		NodeImpl root = (NodeImpl) this.context.getRootNode();
		if (root == null)
			return;
		Node node = root.getNodeAt(offset);
		if (node == null)
			return;
		if (node.getNodeType() != Node.ELEMENT_NODE) {
			if (node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
				// just notify the change instead of setting data
				ProcessingInstructionImpl pi = (ProcessingInstructionImpl) node;
				pi.notifyValueChanged();
			}
			return;
		}
		// actually, do nothing
	}

	/**
	 * changeAttrName method
	 * 
	 * @param flatNode
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 * @param region
	 *            com.ibm.sed.structuredDocument.ITextRegion
	 */
	private void changeAttrName(IStructuredDocumentRegion flatNode, ITextRegion region) {
		int offset = flatNode.getStart();
		if (offset < 0)
			return;
		NodeImpl root = (NodeImpl) this.context.getRootNode();
		if (root == null)
			return;
		Node node = root.getNodeAt(offset);
		if (node == null)
			return;
		if (node.getNodeType() != Node.ELEMENT_NODE) {
			if (node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
				// just notify the change instead of setting data
				ProcessingInstructionImpl pi = (ProcessingInstructionImpl) node;
				pi.notifyValueChanged();
			}
			return;
		}

		ElementImpl element = (ElementImpl) node;
		NamedNodeMap attributes = element.getAttributes();
		if (attributes == null)
			return;
		int length = attributes.getLength();
		for (int i = 0; i < length; i++) {
			AttrImpl attr = (AttrImpl) attributes.item(i);
			if (attr == null)
				continue;
			if (attr.getNameRegion() != region)
				continue;

			String name = flatNode.getText(region);
			attr.setName(name);
			break;
		}
	}

	/**
	 * changeAttrValue method
	 * 
	 * @param flatNode
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 * @param region
	 *            com.ibm.sed.structuredDocument.ITextRegion
	 */
	private void changeAttrValue(IStructuredDocumentRegion flatNode, ITextRegion region) {
		int offset = flatNode.getStart();
		if (offset < 0)
			return;
		NodeImpl root = (NodeImpl) this.context.getRootNode();
		if (root == null)
			return;
		Node node = root.getNodeAt(offset);
		if (node == null)
			return;
		if (node.getNodeType() != Node.ELEMENT_NODE) {
			if (node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
				// just notify the change instead of setting data
				ProcessingInstructionImpl pi = (ProcessingInstructionImpl) node;
				pi.notifyValueChanged();
			}
			return;
		}

		ElementImpl element = (ElementImpl) node;
		NamedNodeMap attributes = element.getAttributes();
		if (attributes == null)
			return;
		int length = attributes.getLength();
		for (int i = 0; i < length; i++) {
			AttrImpl attr = (AttrImpl) attributes.item(i);
			if (attr == null)
				continue;
			if (attr.getValueRegion() != region)
				continue;
			// just notify the change instead of setting value
			attr.notifyValueChanged();
			break;
		}
	}

	/**
	 * changeData method
	 * 
	 * @param flatNode
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 * @param region
	 *            com.ibm.sed.structuredDocument.ITextRegion
	 */
	private void changeData(IStructuredDocumentRegion flatNode, ITextRegion region) {
		int offset = flatNode.getStart();
		if (offset < 0)
			return;
		NodeImpl root = (NodeImpl) this.context.getRootNode();
		if (root == null)
			return;
		Node node = root.getNodeAt(offset);
		if (node == null)
			return;
		switch (node.getNodeType()) {
			case Node.TEXT_NODE : {
				TextImpl text = (TextImpl) node;
				if (text.isSharingStructuredDocumentRegion(flatNode)) {
					// has consecutive text sharing IStructuredDocumentRegion
					changeStructuredDocumentRegion(flatNode);
					return;
				}
				this.context.setNextNode(node);
				cleanupText();
				break;
			}
			case Node.CDATA_SECTION_NODE :
			case Node.PROCESSING_INSTRUCTION_NODE :
				break;
			case Node.COMMENT_NODE :
			case Node.ELEMENT_NODE :
				// comment tag
				changeStructuredDocumentRegion(flatNode);
				return;
			default :
				return;
		}

		// just notify the change instead of setting data
		NodeImpl impl = (NodeImpl) node;
		impl.notifyValueChanged();
	}

	/**
	 */
	private void changeEndTag(IStructuredDocumentRegion flatNode, ITextRegionList newRegions, ITextRegionList oldRegions) {
		int offset = flatNode.getStart();
		if (offset < 0)
			return; // error
		NodeImpl root = (NodeImpl) this.context.getRootNode();
		if (root == null)
			return; // error
		Node node = root.getNodeAt(offset);
		if (node == null)
			return; // error

		if (node.getNodeType() != Node.ELEMENT_NODE) {
			changeStructuredDocumentRegion(flatNode);
			return;
		}

		// check if change is only for close tag
		if (newRegions != null) {
			Iterator e = newRegions.iterator();
			while (e.hasNext()) {
				ITextRegion region = (ITextRegion) e.next();
				String regionType = region.getType();
				if (regionType == XMLRegionContext.XML_TAG_CLOSE)
					continue;

				// other region has changed
				changeStructuredDocumentRegion(flatNode);
				return;
			}
		}
		if (oldRegions != null) {
			Iterator e = oldRegions.iterator();
			while (e.hasNext()) {
				ITextRegion region = (ITextRegion) e.next();
				String regionType = region.getType();
				if (regionType == XMLRegionContext.XML_TAG_CLOSE)
					continue;

				// other region has changed
				changeStructuredDocumentRegion(flatNode);
				return;
			}
		}

		// change for close tag has no impact
		// do nothing
	}

	/**
	 * changeRegion method
	 * 
	 * @param flatNode
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 * @param region
	 *            com.ibm.sed.structuredDocument.ITextRegion
	 */
	void changeRegion(IStructuredDocumentRegion flatNode, ITextRegion region) {
		if (flatNode == null || region == null)
			return;
		if (this.document == null)
			return;
		this.context = new XMLModelContext(this.document);

		// optimize typical cases
		String regionType = region.getType();
		if (regionType == XMLRegionContext.XML_CONTENT || regionType == XMLRegionContext.XML_COMMENT_TEXT || regionType == XMLRegionContext.XML_CDATA_TEXT || regionType == XMLRegionContext.BLOCK_TEXT || regionType == JSP_CONTENT) {
			changeData(flatNode, region);
		} else if (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME) {
			changeAttrName(flatNode, region);
		} else if (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
			changeAttrValue(flatNode, region);
		} else if (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
			changeAttrEqual(flatNode, region);
		} else if (regionType == XMLRegionContext.XML_TAG_NAME || regionType == JSP_ROOT_TAG_NAME || regionType == JSP_DIRECTIVE_NAME) {
			changeTagName(flatNode, region);
		} else {
			changeStructuredDocumentRegion(flatNode);
		}
	}

	/**
	 */
	private void changeStartTag(IStructuredDocumentRegion flatNode, ITextRegionList newRegions, ITextRegionList oldRegions) {
		int offset = flatNode.getStart();
		if (offset < 0)
			return; // error
		NodeImpl root = (NodeImpl) this.context.getRootNode();
		if (root == null)
			return; // error
		Node node = root.getNodeAt(offset);
		if (node == null)
			return; // error

		if (node.getNodeType() != Node.ELEMENT_NODE) {
			changeStructuredDocumentRegion(flatNode);
			return;
		}
		ElementImpl element = (ElementImpl) node;

		// check if changes are only for attributes and close tag
		boolean tagNameUnchanged = false;
		if (newRegions != null) {
			Iterator e = newRegions.iterator();
			while (e.hasNext()) {
				ITextRegion region = (ITextRegion) e.next();
				String regionType = region.getType();
				if (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME || regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_EQUALS || regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE)
					continue;
				if (regionType == XMLRegionContext.XML_TAG_CLOSE) {
					// change from empty tag may have impact on structure
					if (!element.isEmptyTag())
						continue;
				} else if (regionType == XMLRegionContext.XML_TAG_NAME || regionType == JSP_ROOT_TAG_NAME || regionType == JSP_DIRECTIVE_NAME) {
					String oldTagName = element.getTagName();
					String newTagName = flatNode.getText(region);
					if (oldTagName != null && newTagName != null && oldTagName.equals(newTagName)) {
						// the tag name is unchanged
						tagNameUnchanged = true;
						continue;
					}
				}

				// other region has changed
				changeStructuredDocumentRegion(flatNode);
				return;
			}
		}
		if (oldRegions != null) {
			Iterator e = oldRegions.iterator();
			while (e.hasNext()) {
				ITextRegion region = (ITextRegion) e.next();
				String regionType = region.getType();
				if (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME || regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_EQUALS || regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE)
					continue;
				if (regionType == XMLRegionContext.XML_TAG_CLOSE) {
					// change from empty tag may have impact on structure
					if (!element.isEmptyTag())
						continue;
				} else if (regionType == XMLRegionContext.XML_TAG_NAME || regionType == JSP_ROOT_TAG_NAME) {
					// if new tag name is unchanged, it's OK
					if (tagNameUnchanged)
						continue;
				}

				// other region has changed
				changeStructuredDocumentRegion(flatNode);
				return;
			}
		}

		// update attributes
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return; // error
		NamedNodeMap attributes = element.getAttributes();
		if (attributes == null)
			return; // error

		// first remove attributes
		int regionIndex = 0;
		int attrIndex = 0;
		AttrImpl attr = null;
		while (attrIndex < attributes.getLength()) {
			attr = (AttrImpl) attributes.item(attrIndex);
			if (attr == null) { // error
				attrIndex++;
				continue;
			}
			ITextRegion nameRegion = attr.getNameRegion();
			if (nameRegion == null) { // error
				element.removeAttributeNode(attr);
				continue;
			}
			boolean found = false;
			for (int i = regionIndex; i < regions.size(); i++) {
				ITextRegion region = regions.get(i);
				if (region == nameRegion) {
					regionIndex = i + 1; // next region
					found = true;
					break;
				}
			}
			if (found) {
				attrIndex++;
			} else {
				element.removeAttributeNode(attr);
			}
		}

		// insert or update attributes
		attrIndex = 0; // reset to first
		AttrImpl newAttr = null;
		ITextRegion oldValueRegion = null;
		Iterator e = regions.iterator();
		while (e.hasNext()) {
			ITextRegion region = (ITextRegion) e.next();
			String regionType = region.getType();
			if (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME) {
				if (newAttr != null) {
					// insert deferred new attribute
					element.insertAttributeNode(newAttr, attrIndex++);
					newAttr = null;
				} else if (attr != null && oldValueRegion != null) {
					// notify existing attribute value removal
					attr.notifyValueChanged();
				}

				oldValueRegion = null;
				attr = (AttrImpl) attributes.item(attrIndex);
				if (attr != null && attr.getNameRegion() == region) {
					// existing attribute
					attrIndex++;
					// clear other regions
					oldValueRegion = attr.getValueRegion();
					attr.setEqualRegion(null);
					attr.setValueRegion(null);
				} else {
					String name = flatNode.getText(region);
					attr = (AttrImpl) this.document.createAttribute(name);
					if (attr != null)
						attr.setNameRegion(region);
					// defer insertion of new attribute
					newAttr = attr;
				}
			} else if (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
				if (attr != null) {
					attr.setEqualRegion(region);
				}
			} else if (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				if (attr != null) {
					attr.setValueRegion(region);
					if (attr != newAttr && oldValueRegion != region) {
						// notify existing attribute value changed
						attr.notifyValueChanged();
					}
					oldValueRegion = null;
					attr = null;
				}
			}
		}

		if (newAttr != null) {
			// insert deferred new attribute
			element.appendAttributeNode(newAttr);
		} else if (attr != null && oldValueRegion != null) {
			// notify existing attribute value removal
			attr.notifyValueChanged();
		}
	}

	/**
	 * changeStructuredDocumentRegion method
	 * 
	 * @param flatNode
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 */
	private void changeStructuredDocumentRegion(IStructuredDocumentRegion flatNode) {
		if (flatNode == null)
			return;
		if (this.document == null)
			return;

		setupContext(flatNode);

		removeStructuredDocumentRegion(flatNode);
		// make sure the parent is set to deepest level
		// when end tag has been removed
		this.context.setLast();
		insertStructuredDocumentRegion(flatNode);

		cleanupText();
		cleanupEndTag();
	}

	/**
	 */
	private void changeTagName(IStructuredDocumentRegion flatNode, ITextRegion region) {
		int offset = flatNode.getStart();
		if (offset < 0)
			return; // error
		NodeImpl root = (NodeImpl) this.context.getRootNode();
		if (root == null)
			return; // error
		Node node = root.getNodeAt(offset);
		if (node == null)
			return; // error

		if (node.getNodeType() != Node.ELEMENT_NODE) {
			changeStructuredDocumentRegion(flatNode);
			return;
		}

		ElementImpl element = (ElementImpl) node;
		String newTagName = flatNode.getText(region);
		if (newTagName == null || !element.matchTagName(newTagName)) {
			// the tag name is changed
			changeStructuredDocumentRegion(flatNode);
			return;
		}

		// the tag name is unchanged
		// this happens when typing spaces after the tag name
		// do nothing, but...
		// if it's not a change in the end tag of an element with the start
		// tag,
		// and case has been changed, set to element and notify
		if (!element.hasStartTag() || StructuredDocumentRegionUtil.getFirstRegionType(flatNode) != XMLRegionContext.XML_END_TAG_OPEN) {
			String tagName = element.getTagName();
			if (tagName == null || !tagName.equals(newTagName)) {
				element.setTagName(newTagName);
				element.notifyValueChanged();
			}
		}
	}

	/**
	 * cleanupContext method
	 */
	private void cleanupEndTag() {
		Node parent = this.context.getParentNode();
		Node next = this.context.getNextNode();
		while (parent != null) {
			while (next != null) {
				if (next.getNodeType() == Node.ELEMENT_NODE) {
					ElementImpl element = (ElementImpl) next;
					if (element.isEndTag()) {
						// floating end tag
						String tagName = element.getTagName();
						String rootName = getFindRootName(tagName);
						ElementImpl start = (ElementImpl) this.context.findStartTag(tagName, rootName);
						if (start != null) {
							insertEndTag(start);
							// move the end tag from 'element' to 'start'
							start.addEndTag(element);
							removeNode(element);
							parent = this.context.getParentNode();
							next = this.context.getNextNode();
							continue;
						}
					}
				}

				Node first = next.getFirstChild();
				if (first != null) {
					parent = next;
					next = first;
					this.context.setNextNode(next);
				} else {
					next = next.getNextSibling();
					this.context.setNextNode(next);
				}
			}

			if (parent.getNodeType() == Node.ELEMENT_NODE) {
				ElementImpl element = (ElementImpl) parent;
				if (!element.hasEndTag() && element.hasStartTag() && element.getNextSibling() == null) {
					String tagName = element.getTagName();
					ElementImpl end = (ElementImpl) this.context.findEndTag(tagName);
					if (end != null) {
						// move the end tag from 'end' to 'element'
						element.addEndTag(end);
						removeEndTag(end);
						this.context.setParentNode(parent); // reset context
						continue;
					}
				}
			}

			next = parent.getNextSibling();
			parent = parent.getParentNode();
			if (next != null) {
				this.context.setNextNode(next);
			} else {
				this.context.setParentNode(parent);
			}
		}
	}

	/**
	 */
	private void cleanupText() {
		Node parent = this.context.getParentNode();
		if (parent == null)
			return; // error
		Node next = this.context.getNextNode();
		Node prev = (next == null ? parent.getLastChild() : next.getPreviousSibling());

		TextImpl nextText = null;
		TextImpl prevText = null;
		if (next != null && next.getNodeType() == Node.TEXT_NODE) {
			nextText = (TextImpl) next;
		}
		if (prev != null && prev.getNodeType() == Node.TEXT_NODE) {
			prevText = (TextImpl) prev;
		}
		if (nextText == null && prevText == null)
			return;
		if (nextText != null && prevText != null) {
			// consecutive Text nodes created by setupContext(),
			// concat them
			IStructuredDocumentRegion flatNode = nextText.getStructuredDocumentRegion();
			if (flatNode != null)
				prevText.appendStructuredDocumentRegion(flatNode);
			Node newNext = next.getNextSibling();
			parent.removeChild(next);
			next = null;
			this.context.setNextNode(newNext);
		}

		TextImpl childText = (prevText != null ? prevText : nextText);
		if (childText.getNextSibling() == null && childText.getPreviousSibling() == null) {
			if (parent.getNodeType() == Node.ELEMENT_NODE) {
				ElementImpl parentElement = (ElementImpl) parent;
				if (!parentElement.hasStartTag() && !parentElement.hasEndTag()) {
					if (childText.isWhitespace() || childText.isInvalid()) {
						// implicit parent is not required
						Node newParent = parent.getParentNode();
						if (newParent != null) {
							Node newNext = parent.getNextSibling();
							newParent.removeChild(parent);
							parent.removeChild(childText);
							newParent.insertBefore(childText, newNext);
							if (childText == next) {
								this.context.setNextNode(childText);
							} else if (newNext != null) {
								this.context.setNextNode(newNext);
							} else {
								this.context.setParentNode(newParent);
							}
							// try again
							cleanupText();
						}
					}
				}
			}
		}
	}

	/**
	 * This routine create an Element from comment data for comment style
	 * elements, such as SSI and METADATA
	 */
	protected Element createCommentElement(String data, boolean isJSPTag) {
		String trimmedData = data.trim();
		CommentElementConfiguration[] configs = CommentElementRegistry.getInstance().getConfigurations();
		for (int iConfig = 0; iConfig < configs.length; iConfig++) {
			CommentElementConfiguration config = configs[iConfig];
			if ((isJSPTag && !config.acceptJSPComment()) || (!isJSPTag && !config.acceptXMLComment())) {
				continue;
			}
			String[] prefixes = config.getPrefix();
			for (int iPrefix = 0; iPrefix < prefixes.length; iPrefix++) {
				if (trimmedData.startsWith(prefixes[iPrefix])) {
					return config.createElement(this.document, data, isJSPTag);
				}
			}
		}
		if (this.adapter != null) {
			return this.adapter.createCommentElement(this.document, data, isJSPTag);
		}
		return null;
	}

	/**
	 * This routine create an implicit Element for given parent and child,
	 * such as HTML, BODY, HEAD, and TBODY for HTML document.
	 */
	protected Element createImplicitElement(Node parent, Node child) {
		if (this.adapter != null) {
			return this.adapter.createImplicitElement(this.document, parent, child);
		}
		return null;
	}

	/**
	 */
	private void demoteNodes(Node root, Node newParent, Node oldParent, Node next) {
		if (newParent.getNodeType() != Node.ELEMENT_NODE)
			return;
		ElementImpl newElement = (ElementImpl) newParent;

		// find next
		while (next == null) {
			if (oldParent.getNodeType() != Node.ELEMENT_NODE)
				return;
			ElementImpl oldElement = (ElementImpl) oldParent;
			if (oldElement.hasEndTag())
				return;
			oldParent = oldElement.getParentNode();
			if (oldParent == null)
				return; // error
			next = oldElement.getNextSibling();
		}

		while (next != null) {
			boolean done = false;
			if (next.getNodeType() == Node.ELEMENT_NODE) {
				ElementImpl nextElement = (ElementImpl) next;
				if (!nextElement.hasStartTag()) {
					Node nextChild = nextElement.getFirstChild();
					if (nextChild != null) {
						// demote children
						next = nextChild;
						oldParent = nextElement;
						continue;
					}

					if (nextElement.hasEndTag()) {
						if (nextElement.matchEndTag(newElement)) {
							// stop at the matched invalid end tag
							next = nextElement.getNextSibling();
							oldParent.removeChild(nextElement);
							newElement.addEndTag(nextElement);

							if (newElement == root)
								return;
							Node p = newElement.getParentNode();
							// check if reached to top
							if (p == null || p == oldParent || p.getNodeType() != Node.ELEMENT_NODE)
								return;
							newElement = (ElementImpl) p;
							done = true;
						}
					} else {
						// remove implicit element
						next = nextElement.getNextSibling();
						oldParent.removeChild(nextElement);
						done = true;
					}
				}
			}

			if (!done) {
				if (!canContain(newElement, next)) {
					if (newElement == root)
						return;
					Node p = newElement.getParentNode();
					// check if reached to top
					if (p == null || p == oldParent || p.getNodeType() != Node.ELEMENT_NODE)
						return;
					newElement = (ElementImpl) p;
					continue;
				}

				Node child = next;
				next = next.getNextSibling();
				oldParent.removeChild(child);
				insertNode(newElement, child, null);
				Node childParent = child.getParentNode();
				if (childParent != newElement) {
					newElement = (ElementImpl) childParent;
				}
			}

			// find next parent and sibling
			while (next == null) {
				if (oldParent.getNodeType() != Node.ELEMENT_NODE)
					return;
				ElementImpl oldElement = (ElementImpl) oldParent;

				// dug parent must not have children at this point
				if (!oldElement.hasChildNodes() && !oldElement.hasStartTag()) {
					oldParent = oldElement.getParentNode();
					if (oldParent == null)
						return; // error
					next = oldElement;
					break;
				}

				if (oldElement.hasEndTag())
					return;
				oldParent = oldElement.getParentNode();
				if (oldParent == null)
					return; // error
				next = oldElement.getNextSibling();
			}
		}
	}

	/**
	 */
	protected final XMLDocument getDocument() {
		return this.document;
	}

	/**
	 */
	protected String getFindRootName(String tagName) {
		if (this.adapter != null) {
			return this.adapter.getFindRootName(tagName);
		}
		return null;
	}

	/**
	 */
	protected final XMLModel getModel() {
		return this.model;
	}

	/**
	 * insertCDATASection method
	 * 
	 * @param flatNode
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 */
	private void insertCDATASection(IStructuredDocumentRegion flatNode) {
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return;

		CDATASectionImpl cdata = null;
		try {
			cdata = (CDATASectionImpl) this.document.createCDATASection(null);
		} catch (DOMException ex) {
		}
		if (cdata == null) { // CDATA section might not be supported
			insertInvalidDecl(flatNode); // regard as invalid decl
			return;
		}

		cdata.setStructuredDocumentRegion(flatNode);
		insertNode(cdata);
	}

	/**
	 * insertComment method
	 * 
	 * @param flatNode
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 */
	private void insertComment(IStructuredDocumentRegion flatNode) {
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return;

		String data = null;
		boolean isJSPTag = false;
		Iterator e = regions.iterator();
		while (e.hasNext()) {
			ITextRegion region = (ITextRegion) e.next();
			String regionType = region.getType();
			if (regionType == JSP_COMMENT_OPEN) {
				isJSPTag = true;
			} else if (regionType == XMLRegionContext.XML_COMMENT_TEXT || regionType == JSP_COMMENT_TEXT) {
				if (data == null) {
					data = flatNode.getText(region);
				}
			}
		}

		if (data != null) {
			ElementImpl element = (ElementImpl) createCommentElement(data, isJSPTag);
			if (element != null) {
				if (!isEndTag(element)) {
					element.setStartStructuredDocumentRegion(flatNode);
					insertStartTag(element);
					return;
				}

				// end tag
				element.setEndStructuredDocumentRegion(flatNode);

				String tagName = element.getTagName();
				String rootName = getFindRootName(tagName);
				ElementImpl start = (ElementImpl) this.context.findStartTag(tagName, rootName);
				if (start != null) { // start tag found
					insertEndTag(start);
					start.addEndTag(element);
					return;
				}

				// invalid end tag
				insertNode(element);
				return;
			}
		}

		CommentImpl comment = (CommentImpl) this.document.createComment(null);
		if (comment == null)
			return;
		if (isJSPTag)
			comment.setJSPTag(true);
		comment.setStructuredDocumentRegion(flatNode);
		insertNode(comment);
	}

	/**
	 * insertDecl method
	 * 
	 * @param flatNode
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 */
	private void insertDecl(IStructuredDocumentRegion flatNode) {
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return;

		boolean isDocType = false;
		String name = null;
		String publicId = null;
		String systemId = null;
		Iterator e = regions.iterator();
		while (e.hasNext()) {
			ITextRegion region = (ITextRegion) e.next();
			String regionType = region.getType();
			if (regionType == XMLRegionContext.XML_DOCTYPE_DECLARATION) {
				isDocType = true;
			} else if (regionType == XMLRegionContext.XML_DOCTYPE_NAME) {
				if (name == null)
					name = flatNode.getText(region);
			} else if (regionType == XMLRegionContext.XML_DOCTYPE_EXTERNAL_ID_PUBREF) {
				if (publicId == null)
					publicId = StructuredDocumentRegionUtil.getAttrValue(flatNode, region);
			} else if (regionType == XMLRegionContext.XML_DOCTYPE_EXTERNAL_ID_SYSREF) {
				if (systemId == null)
					systemId = StructuredDocumentRegionUtil.getAttrValue(flatNode, region);
			}
		}

		// invalid declaration
		if (!isDocType) {
			insertInvalidDecl(flatNode);
			return;
		}

		DocumentTypeImpl docType = (DocumentTypeImpl) this.document.createDoctype(name);
		if (docType == null)
			return;
		if (publicId != null)
			docType.setPublicId(publicId);
		if (systemId != null)
			docType.setSystemId(systemId);
		docType.setStructuredDocumentRegion(flatNode);
		insertNode(docType);
	}

	/**
	 * insertEndTag method
	 * 
	 * @param element
	 *            org.w3c.dom.Element
	 */
	private void insertEndTag(Element element) {
		if (element == null)
			return;

		Node newParent = element.getParentNode();
		if (newParent == null)
			return; // error

		if (!((ElementImpl) element).isContainer()) {
			// just update context
			Node elementNext = element.getNextSibling();
			if (elementNext != null)
				this.context.setNextNode(elementNext);
			else
				this.context.setParentNode(newParent);
			return;
		}

		// promote children
		Node newNext = element.getNextSibling();
		Node oldParent = this.context.getParentNode();
		if (oldParent == null)
			return; // error
		Node oldNext = this.context.getNextNode();
		promoteNodes(element, newParent, newNext, oldParent, oldNext);

		// update context
		// re-check the next sibling
		newNext = element.getNextSibling();
		if (newNext != null)
			this.context.setNextNode(newNext);
		else
			this.context.setParentNode(newParent);
	}

	/**
	 * insertEndTag method
	 * 
	 * @param flatNode
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 */
	private void insertEndTag(IStructuredDocumentRegion flatNode) {
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return;

		String tagName = null;
		Iterator e = regions.iterator();
		while (e.hasNext()) {
			ITextRegion region = (ITextRegion) e.next();
			if (region.getType() == XMLRegionContext.XML_TAG_NAME || region.getType() == JSP_ROOT_TAG_NAME || region.getType() == JSP_DIRECTIVE_NAME) {
				if (tagName == null)
					tagName = flatNode.getText(region);
			}
		}

		if (tagName == null) { // invalid end tag
			insertText(flatNode); // regard as invalid text
			return;
		}

		String rootName = getFindRootName(tagName);
		ElementImpl start = (ElementImpl) this.context.findStartTag(tagName, rootName);
		if (start != null) { // start tag found
			insertEndTag(start);
			start.setEndStructuredDocumentRegion(flatNode);
			return;
		}

		// invalid end tag
		ElementImpl end = null;
		try {
			end = (ElementImpl) this.document.createElement(tagName);
		} catch (DOMException ex) {
		}
		if (end == null) { // invalid end tag
			insertText(flatNode); // regard as invalid text
			return;
		}
		end.setEndStructuredDocumentRegion(flatNode);
		insertNode(end);
	}

	/**
	 * insertEntityRef method
	 * 
	 * @param flatNode
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 */
	private void insertEntityRef(IStructuredDocumentRegion flatNode) {
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return;

		String name = null;
		Iterator e = regions.iterator();
		while (e.hasNext()) {
			ITextRegion region = (ITextRegion) e.next();
			String regionType = region.getType();
			if (regionType == XMLRegionContext.XML_ENTITY_REFERENCE || regionType == XMLRegionContext.XML_CHAR_REFERENCE) {
				if (name == null)
					name = StructuredDocumentRegionUtil.getEntityRefName(flatNode, region);
			}
		}

		if (name == null) { // invalid entity
			insertText(flatNode);
			return;
		}

		String value = this.document.getCharValue(name);
		if (value != null) { // character entity
			TextImpl text = (TextImpl) this.context.findPreviousText();
			if (text != null) { // existing text found
				// do not append data
				text.appendStructuredDocumentRegion(flatNode);
				// notify the change
				text.notifyValueChanged();
				return;
			}

			// new text
			text = (TextImpl) this.document.createTextNode(null);
			if (text == null)
				return;
			text.setStructuredDocumentRegion(flatNode);
			insertNode(text);
			return;
		}

		// general entity reference
		EntityReferenceImpl ref = null;
		try {
			ref = (EntityReferenceImpl) this.document.createEntityReference(name);
		} catch (DOMException ex) {
		}
		if (ref == null) { // entity reference might not be supported
			insertText(flatNode); // regard as invalid text
			return;
		}

		ref.setStructuredDocumentRegion(flatNode);
		insertNode(ref);
	}

	/**
	 * insertInvalidDecl method
	 * 
	 * @param flatNode
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 */
	private void insertInvalidDecl(IStructuredDocumentRegion flatNode) {
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return;

		ElementImpl element = null;
		try {
			element = (ElementImpl) this.document.createElement("!");//$NON-NLS-1$
		} catch (DOMException ex) {
		}
		if (element == null) { // invalid tag
			insertText(flatNode); // regard as invalid text
			return;
		}
		element.setEmptyTag(true);
		element.setStartStructuredDocumentRegion(flatNode);
		insertNode(element);
	}

	/**
	 * insertJSPTag method
	 * 
	 * @param flatNode
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 */
	private void insertJSPTag(IStructuredDocumentRegion flatNode) {
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return;

		String tagName = null;
		AttrImpl attr = null;
		Vector attrNodes = null;
		boolean isCloseTag = false;
		Iterator e = regions.iterator();
		while (e.hasNext()) {
			ITextRegion region = (ITextRegion) e.next();
			String regionType = region.getType();
			if (regionType == JSP_SCRIPTLET_OPEN) {
				tagName = JSPTag.JSP_SCRIPTLET;
			} else if (regionType == JSP_EXPRESSION_OPEN) {
				tagName = JSPTag.JSP_EXPRESSION;
			} else if (regionType == JSP_DECLARATION_OPEN) {
				tagName = JSPTag.JSP_DECLARATION;
			} else if (regionType == JSP_DIRECTIVE_OPEN) {
				tagName = JSPTag.JSP_DIRECTIVE;
			} else if (regionType == JSP_DIRECTIVE_NAME) {
				tagName += '.';
				tagName += flatNode.getText(region);
			} else if (regionType == JSP_CLOSE) {
				isCloseTag = true;
			} else if (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME) {
				String name = flatNode.getText(region);
				attr = (AttrImpl) this.document.createAttribute(name);
				if (attr != null) {
					attr.setNameRegion(region);
					if (attrNodes == null)
						attrNodes = new Vector();
					attrNodes.addElement(attr);
				}
			} else if (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
				if (attr != null) {
					attr.setEqualRegion(region);
				}
			} else if (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				if (attr != null) {
					attr.setValueRegion(region);
					attr = null;
				}
			}
		}

		if (tagName == null) {
			if (isCloseTag) {
				// close JSP tag
				Node parent = this.context.getParentNode();
				if (parent != null && parent.getNodeType() == Node.ELEMENT_NODE) {
					ElementImpl start = (ElementImpl) parent;
					if (start.isJSPContainer()) {
						insertEndTag(start);
						start.setEndStructuredDocumentRegion(flatNode);
						return;
					}
				}
			}
			// invalid JSP tag
			insertText(flatNode); // regard as invalid text
			return;
		}

		ElementImpl element = null;
		try {
			element = (ElementImpl) this.document.createElement(tagName);
		} catch (DOMException ex) {
		}
		if (element == null) { // invalid tag
			insertText(flatNode); // regard as invalid text
			return;
		}
		if (attrNodes != null) {
			Enumeration ae = attrNodes.elements();
			while (ae.hasMoreElements()) {
				Attr a = (Attr) ae.nextElement();
				if (a == null)
					continue;
				element.appendAttributeNode(a);
			}
		}
		element.setJSPTag(true);
		element.setStartStructuredDocumentRegion(flatNode);
		insertStartTag(element);
	}

	/**
	 * insertNode method
	 * 
	 * @param child
	 *            org.w3c.dom.Node
	 */
	private void insertNode(Node node) {
		if (node == null)
			return;
		if (this.context == null)
			return;

		Node parent = this.context.getParentNode();
		if (parent == null)
			return;
		Node next = this.context.getNextNode();
		while (parent.getNodeType() == Node.ELEMENT_NODE) {
			ElementImpl element = (ElementImpl) parent;
			if (canContain(element, node)) {
				if (!element.hasStartTag() && next == element.getFirstChild()) {
					// first child of implicit tag
					// deletege to the parent
					parent = element.getParentNode();
					if (parent == null)
						return;
					next = element;
					this.context.setNextNode(next);
					continue;
				}
				break;
			}
			parent = element.getParentNode();
			if (parent == null)
				return;

			// promote siblings
			Node newNext = element.getNextSibling();
			Node child = next;
			while (child != null) {
				Node nextChild = child.getNextSibling();
				element.removeChild(child);
				parent.insertBefore(child, newNext);
				child = nextChild;
			}

			// leave the old end tag where it is
			if (element.hasEndTag()) {
				Element end = element.removeEndTag();
				if (end != null) {
					parent.insertBefore(end, newNext);
					if (next == null)
						next = end;
				}
			}
			if (!element.hasStartTag()) {
				// implicit element
				if (!element.hasChildNodes()) {
					parent.removeChild(element);
				}
			}

			// update context
			if (next == null)
				next = newNext;
			if (next != null)
				this.context.setNextNode(next);
			else
				this.context.setParentNode(parent);
		}

		insertNode(parent, node, next);
		next = node.getNextSibling();
		if (next != null)
			this.context.setNextNode(next);
		else
			this.context.setParentNode(node.getParentNode());
	}

	/**
	 */
	private void insertNode(Node parent, Node node, Node next) {
		while (next != null && next.getNodeType() == Node.ELEMENT_NODE) {
			ElementImpl nextElement = (ElementImpl) next;
			if (nextElement.hasStartTag())
				break;
			if (!canBeImplicitTag(nextElement, node))
				break;
			parent = nextElement;
			next = nextElement.getFirstChild();
		}
		Element implicitElement = createImplicitElement(parent, node);
		if (implicitElement != null)
			node = implicitElement;
		parent.insertBefore(node, next);
	}

	/**
	 * insertPI method
	 * 
	 * @param flatNode
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 */
	private void insertPI(IStructuredDocumentRegion flatNode) {
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return;

		String target = null;
		Iterator e = regions.iterator();
		while (e.hasNext()) {
			ITextRegion region = (ITextRegion) e.next();
			String regionType = region.getType();
			if (regionType == XMLRegionContext.XML_PI_OPEN || regionType == XMLRegionContext.XML_PI_CLOSE)
				continue;
			if (target == null)
				target = flatNode.getText(region);
		}

		ProcessingInstructionImpl pi = (ProcessingInstructionImpl) this.document.createProcessingInstruction(target, null);
		if (pi == null)
			return;
		pi.setStructuredDocumentRegion(flatNode);
		insertNode(pi);
	}

	/**
	 * insertStartTag method
	 * 
	 * @param element
	 *            org.w3c.dom.Element
	 */
	private void insertStartTag(Element element) {
		if (element == null)
			return;
		if (this.context == null)
			return;

		insertNode(element);

		ElementImpl newElement = (ElementImpl) element;
		if (newElement.isEmptyTag() || !newElement.isContainer())
			return;

		// demote siblings
		Node parent = this.context.getParentNode();
		if (parent == null)
			return; // error
		Node next = this.context.getNextNode();
		demoteNodes(element, element, parent, next);

		// update context
		Node firstChild = element.getFirstChild();
		if (firstChild != null)
			this.context.setNextNode(firstChild);
		else
			this.context.setParentNode(element);
	}

	/**
	 * insertStartTag method
	 * 
	 * @param flatNode
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 */
	private void insertStartTag(IStructuredDocumentRegion flatNode) {
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return;

		String tagName = null;
		boolean isEmptyTag = false;
		AttrImpl attr = null;
		Vector attrNodes = null;
		Iterator e = regions.iterator();
		while (e.hasNext()) {
			ITextRegion region = (ITextRegion) e.next();
			String regionType = region.getType();
			if (regionType == XMLRegionContext.XML_TAG_NAME || regionType == JSP_ROOT_TAG_NAME || regionType == JSP_DIRECTIVE_NAME) {
				if (tagName == null)
					tagName = flatNode.getText(region);
			} else if (regionType == XMLRegionContext.XML_EMPTY_TAG_CLOSE) {
				isEmptyTag = true;
			} else if (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_NAME) {
				String name = flatNode.getText(region);
				attr = (AttrImpl) this.document.createAttribute(name);
				if (attr != null) {
					attr.setNameRegion(region);
					if (attrNodes == null)
						attrNodes = new Vector();
					attrNodes.addElement(attr);
				}
			} else if (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_EQUALS) {
				if (attr != null) {
					attr.setEqualRegion(region);
				}
			} else if (regionType == XMLRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
				if (attr != null) {
					attr.setValueRegion(region);
					attr = null;
				}
			}
		}

		if (tagName == null) { // invalid start tag
			insertText(flatNode); // regard as invalid text
			return;
		}

		ElementImpl element = null;
		try {
			element = (ElementImpl) this.document.createElement(tagName);
		} catch (DOMException ex) {
		}
		if (element == null) { // invalid tag
			insertText(flatNode); // regard as invalid text
			return;
		}
		if (attrNodes != null) {
			Enumeration ae = attrNodes.elements();
			while (ae.hasMoreElements()) {
				Attr a = (Attr) ae.nextElement();
				if (a == null)
					continue;
				element.appendAttributeNode(a);
			}
		}
		if (isEmptyTag)
			element.setEmptyTag(true);
		element.setStartStructuredDocumentRegion(flatNode);
		insertStartTag(element);
	}

	/**
	 * insertStructuredDocumentRegion method
	 * 
	 * @param flatNode
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 */
	private void insertStructuredDocumentRegion(IStructuredDocumentRegion flatNode) {
		String regionType = StructuredDocumentRegionUtil.getFirstRegionType(flatNode);
		if (regionType == XMLRegionContext.XML_TAG_OPEN) {
			insertStartTag(flatNode);
		} else if (regionType == XMLRegionContext.XML_END_TAG_OPEN) {
			insertEndTag(flatNode);
		} else if (regionType == XMLRegionContext.XML_COMMENT_OPEN || regionType == JSP_COMMENT_OPEN) {
			insertComment(flatNode);
		} else if (regionType == XMLRegionContext.XML_ENTITY_REFERENCE || regionType == XMLRegionContext.XML_CHAR_REFERENCE) {
			insertEntityRef(flatNode);
		} else if (regionType == XMLRegionContext.XML_DECLARATION_OPEN) {
			insertDecl(flatNode);
		} else if (regionType == XMLRegionContext.XML_PI_OPEN) {
			insertPI(flatNode);
		} else if (regionType == XMLRegionContext.XML_CDATA_OPEN) {
			insertCDATASection(flatNode);
		} else if (regionType == JSP_SCRIPTLET_OPEN || regionType == JSP_EXPRESSION_OPEN || regionType == JSP_DECLARATION_OPEN || regionType == JSP_DIRECTIVE_OPEN || regionType == JSP_CLOSE) {
			insertJSPTag(flatNode);
		} else {
			insertText(flatNode);
		}
	}

	/**
	 * insertText method
	 * 
	 * @param flatNode
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 */
	private void insertText(IStructuredDocumentRegion flatNode) {
		TextImpl text = (TextImpl) this.context.findPreviousText();
		if (text != null) { // existing text found
			text.appendStructuredDocumentRegion(flatNode);
			// notify the change
			text.notifyValueChanged();
			return;
		}

		// new text
		text = (TextImpl) this.document.createTextNode(null);
		if (text == null)
			return;
		text.setStructuredDocumentRegion(flatNode);
		insertNode(text);
	}

	/**
	 */
	protected boolean isEndTag(XMLElement element) {
		if (this.adapter != null) {
			return this.adapter.isEndTag(element);
		}
		return element.isEndTag();
	}

	/**
	 */
	private void promoteNodes(Node root, Node newParent, Node newNext, Node oldParent, Node next) {
		ElementImpl newElement = null;
		if (newParent.getNodeType() == Node.ELEMENT_NODE) {
			newElement = (ElementImpl) newParent;
		}

		Node rootParent = root.getParentNode();
		while (oldParent != rootParent) {
			while (next != null) {
				boolean done = false;
				boolean endTag = false;
				if (next.getNodeType() == Node.ELEMENT_NODE) {
					ElementImpl nextElement = (ElementImpl) next;
					if (!nextElement.hasStartTag()) {
						Node nextChild = nextElement.getFirstChild();
						if (nextChild != null) {
							// promote children
							next = nextChild;
							oldParent = nextElement;
							continue;
						}

						if (nextElement.hasEndTag()) {
							if (nextElement.matchEndTag(newElement)) {
								endTag = true;
							}
						} else {
							// remove implicit element
							next = nextElement.getNextSibling();
							oldParent.removeChild(nextElement);
							done = true;
						}
					}
				}

				if (!done) {
					if (!endTag && newElement != null && !canContain(newElement, next)) {
						newParent = newElement.getParentNode();
						if (newParent == null)
							return; // error
						Node elementNext = newElement.getNextSibling();
						// promote siblings
						promoteNodes(newElement, newParent, elementNext, newElement, newNext);
						newNext = newElement.getNextSibling();
						if (newParent.getNodeType() == Node.ELEMENT_NODE) {
							newElement = (ElementImpl) newParent;
						} else {
							newElement = null;
						}
						continue;
					}

					Node child = next;
					next = next.getNextSibling();
					oldParent.removeChild(child);
					insertNode(newParent, child, newNext);
					Node childParent = child.getParentNode();
					if (childParent != newParent) {
						newParent = childParent;
						newElement = (ElementImpl) newParent;
						newNext = child.getNextSibling();
					}
				}
			}

			if (oldParent.getNodeType() != Node.ELEMENT_NODE)
				return;
			ElementImpl oldElement = (ElementImpl) oldParent;
			oldParent = oldElement.getParentNode();
			if (oldParent == null)
				return; // error
			next = oldElement.getNextSibling();

			if (oldElement.hasEndTag()) {
				Element end = null;
				if (!oldElement.hasChildNodes() && !oldElement.hasStartTag()) {
					oldParent.removeChild(oldElement);
					end = oldElement;
				} else {
					end = oldElement.removeEndTag();
				}
				if (end != null) {
					insertNode(newParent, end, newNext);
					Node endParent = end.getParentNode();
					if (endParent != newParent) {
						newParent = endParent;
						newElement = (ElementImpl) newParent;
						newNext = end.getNextSibling();
					}
				}
			}
		}
	}

	/**
	 * removeEndTag method
	 * 
	 * @param element
	 *            org.w3c.dom.Element
	 */
	private void removeEndTag(Element element) {
		if (element == null)
			return;
		if (this.context == null)
			return;

		Node parent = element.getParentNode();
		if (parent == null)
			return; // error

		if (!((ElementImpl) element).isContainer()) {
			// just update context
			Node elementNext = element.getNextSibling();
			if (elementNext != null)
				this.context.setNextNode(elementNext);
			else
				this.context.setParentNode(parent);
			return;
		}

		// demote siblings
		Node next = element.getNextSibling();
		ElementImpl newElement = (ElementImpl) element;
		// find new parent
		for (Node last = newElement.getLastChild(); last != null; last = last.getLastChild()) {
			if (last.getNodeType() != Node.ELEMENT_NODE)
				break;
			ElementImpl lastElement = (ElementImpl) last;
			if (lastElement.hasEndTag() || lastElement.isEmptyTag() || !lastElement.isContainer())
				break;
			newElement = lastElement;
		}
		Node lastChild = newElement.getLastChild();
		demoteNodes(element, newElement, parent, next);

		// update context
		Node newNext = null;
		if (lastChild != null)
			newNext = lastChild.getNextSibling();
		else
			newNext = newElement.getFirstChild();
		if (newNext != null)
			this.context.setNextNode(newNext);
		else
			this.context.setParentNode(newElement);
	}

	/**
	 * Remove the specified node if it is no longer required implicit tag with
	 * remaining child nodes promoted.
	 */
	private Element removeImplicitElement(Node parent) {
		if (parent == null)
			return null;
		if (parent.getNodeType() != Node.ELEMENT_NODE)
			return null;
		ElementImpl element = (ElementImpl) parent;
		if (!element.isImplicitTag())
			return null;
		if (canBeImplicitTag(element))
			return null;

		Node elementParent = element.getParentNode();
		if (elementParent == null)
			return null; // error
		Node firstChild = element.getFirstChild();
		Node child = firstChild;
		Node elementNext = element.getNextSibling();
		while (child != null) {
			Node nextChild = child.getNextSibling();
			element.removeChild(child);
			elementParent.insertBefore(child, elementNext);
			child = nextChild;
		}

		// reset context
		if (this.context.getParentNode() == element) {
			Node oldNext = this.context.getNextNode();
			if (oldNext != null) {
				this.context.setNextNode(oldNext);
			} else {
				if (elementNext != null) {
					this.context.setNextNode(elementNext);
				} else {
					this.context.setParentNode(elementParent);
				}
			}
		} else if (this.context.getNextNode() == element) {
			if (firstChild != null) {
				this.context.setNextNode(firstChild);
			} else {
				if (elementNext != null) {
					this.context.setNextNode(elementNext);
				} else {
					this.context.setParentNode(elementParent);
				}
			}
		}

		removeNode(element);
		return element;
	}

	/**
	 * removeNode method
	 * 
	 * @param node
	 *            org.w3c.dom.Node
	 */
	private void removeNode(Node node) {
		if (node == null)
			return;
		if (this.context == null)
			return;

		Node parent = node.getParentNode();
		if (parent == null)
			return;
		Node next = node.getNextSibling();
		Node prev = node.getPreviousSibling();

		// update context
		Node oldParent = this.context.getParentNode();
		if (node == oldParent) {
			if (next != null)
				this.context.setNextNode(next);
			else
				this.context.setParentNode(parent);
		} else {
			Node oldNext = this.context.getNextNode();
			if (node == oldNext) {
				this.context.setNextNode(next);
			}
		}

		parent.removeChild(node);

		if (removeImplicitElement(parent) != null)
			return;

		// demote sibling
		if (prev != null && prev.getNodeType() == Node.ELEMENT_NODE) {
			ElementImpl newElement = (ElementImpl) prev;
			if (!newElement.hasEndTag() && !newElement.isEmptyTag() && newElement.isContainer()) {
				// find new parent
				for (Node last = newElement.getLastChild(); last != null; last = last.getLastChild()) {
					if (last.getNodeType() != Node.ELEMENT_NODE)
						break;
					ElementImpl lastElement = (ElementImpl) last;
					if (lastElement.hasEndTag() || lastElement.isEmptyTag() || !lastElement.isContainer())
						break;
					newElement = lastElement;
				}
				Node lastChild = newElement.getLastChild();
				demoteNodes(prev, newElement, parent, next);

				// update context
				Node newNext = null;
				if (lastChild != null)
					newNext = lastChild.getNextSibling();
				else
					newNext = newElement.getFirstChild();
				if (newNext != null)
					this.context.setNextNode(newNext);
				else
					this.context.setParentNode(newElement);
			}
		}
	}

	/**
	 * removeStartTag method
	 * 
	 * @param element
	 *            org.w3c.dom.Element
	 */
	private void removeStartTag(Element element) {
		if (element == null)
			return;
		if (this.context == null)
			return;

		// for implicit tag
		ElementImpl oldElement = (ElementImpl) element;
		if (canBeImplicitTag(oldElement)) {
			Node newParent = null;
			Node prev = oldElement.getPreviousSibling();
			if (prev != null && prev.getNodeType() == Node.ELEMENT_NODE) {
				ElementImpl prevElement = (ElementImpl) prev;
				if (!prevElement.hasEndTag()) {
					if (prevElement.hasStartTag() || prevElement.matchTagName(oldElement.getTagName())) {
						newParent = prevElement;
					}
				}
			}
			if (newParent == null) {
				// this element should stay as implicit tag
				// just remove all attributes
				oldElement.removeStartTag();

				// update context
				Node child = oldElement.getFirstChild();
				if (child != null) {
					this.context.setNextNode(child);
				} else if (oldElement.hasEndTag()) {
					this.context.setParentNode(oldElement);
				}
				return;
			}
		}
		// for comment tag
		if (oldElement.isCommentTag())
			oldElement.removeStartTag();

		// promote children
		Node elementParent = element.getParentNode();
		Node parent = elementParent;
		if (parent == null)
			return;
		Node first = element.getFirstChild();
		Node firstElement = null; // for the case first is removed as end tag
		if (first != null) {
			// find new parent for children
			ElementImpl newElement = null;
			for (Node last = element.getPreviousSibling(); last != null; last = last.getLastChild()) {
				if (last.getNodeType() != Node.ELEMENT_NODE)
					break;
				ElementImpl lastElement = (ElementImpl) last;
				if (lastElement.hasEndTag() || lastElement.isEmptyTag() || !lastElement.isContainer())
					break;
				newElement = lastElement;
			}
			Node next = first;
			if (newElement != null) {
				while (next != null) {
					if (!newElement.hasEndTag() && newElement.hasStartTag() && next.getNodeType() == Node.ELEMENT_NODE) {
						ElementImpl nextElement = (ElementImpl) next;
						if (!nextElement.hasStartTag() && nextElement.hasEndTag() && nextElement.matchEndTag(newElement)) {
							// stop at the matched invalid end tag
							Node elementChild = nextElement.getFirstChild();
							while (elementChild != null) {
								Node nextChild = elementChild.getNextSibling();
								nextElement.removeChild(elementChild);
								newElement.appendChild(elementChild);
								elementChild = nextChild;
							}

							next = nextElement.getNextSibling();
							element.removeChild(nextElement);
							newElement.addEndTag(nextElement);
							if (nextElement == first)
								firstElement = newElement;

							Node newParent = newElement.getParentNode();
							if (newParent == parent)
								break;
							if (newParent == null || newParent.getNodeType() != Node.ELEMENT_NODE)
								break; // error
							newElement = (ElementImpl) newParent;
							continue;
						}
					}
					if (!canContain(newElement, next)) {
						Node newParent = newElement.getParentNode();
						if (newParent == parent)
							break;
						if (newParent == null || newParent.getNodeType() != Node.ELEMENT_NODE)
							break; // error
						newElement = (ElementImpl) newParent;
						continue;
					}
					Node child = next;
					next = next.getNextSibling();
					element.removeChild(child);
					newElement.appendChild(child);
				}
				newElement = null;
			}
			if (parent.getNodeType() == Node.ELEMENT_NODE) {
				newElement = (ElementImpl) parent;
			}
			while (next != null) {
				if (newElement == null || canContain(newElement, next)) {
					Node child = next;
					next = next.getNextSibling();
					element.removeChild(child);
					parent.insertBefore(child, element);
					continue;
				}

				parent = newElement.getParentNode();
				if (parent == null)
					return;

				// promote siblings
				Node newNext = newElement.getNextSibling();
				Node child = element;
				while (child != null) {
					Node nextChild = child.getNextSibling();
					newElement.removeChild(child);
					parent.insertBefore(child, newNext);
					child = nextChild;
				}

				// leave the old end tag where it is
				if (newElement.hasEndTag()) {
					Element end = newElement.removeEndTag();
					if (end != null) {
						parent.insertBefore(end, newNext);
					}
				}
				if (!newElement.hasStartTag()) {
					// implicit element
					if (!newElement.hasChildNodes()) {
						parent.removeChild(newElement);
					}
				}

				if (parent.getNodeType() == Node.ELEMENT_NODE) {
					newElement = (ElementImpl) parent;
				} else {
					newElement = null;
				}
			}
		}

		Node newNext = element;
		Node startElement = null; // for the case element is removed as end
		// tag
		if (oldElement.hasEndTag()) {
			// find new parent for invalid end tag and siblings
			ElementImpl newElement = null;
			for (Node last = element.getPreviousSibling(); last != null; last = last.getLastChild()) {
				if (last.getNodeType() != Node.ELEMENT_NODE)
					break;
				ElementImpl lastElement = (ElementImpl) last;
				if (lastElement.hasEndTag() || lastElement.isEmptyTag() || !lastElement.isContainer())
					break;
				newElement = lastElement;
			}
			if (newElement != null) {
				// demote invalid end tag and sibling
				Node next = element;
				while (next != null) {
					if (!newElement.hasEndTag() && newElement.hasStartTag() && next.getNodeType() == Node.ELEMENT_NODE) {
						ElementImpl nextElement = (ElementImpl) next;
						if (!nextElement.hasStartTag() && nextElement.hasEndTag() && nextElement.matchEndTag(newElement)) {
							// stop at the matched invalid end tag
							Node elementChild = nextElement.getFirstChild();
							while (elementChild != null) {
								Node nextChild = elementChild.getNextSibling();
								nextElement.removeChild(elementChild);
								newElement.appendChild(elementChild);
								elementChild = nextChild;
							}

							next = nextElement.getNextSibling();
							parent.removeChild(nextElement);
							newElement.addEndTag(nextElement);
							if (nextElement == newNext)
								startElement = newElement;

							Node newParent = newElement.getParentNode();
							if (newParent == parent)
								break;
							if (newParent == null || newParent.getNodeType() != Node.ELEMENT_NODE)
								break; // error
							newElement = (ElementImpl) newParent;
							continue;
						}
					}
					if (!canContain(newElement, next)) {
						Node newParent = newElement.getParentNode();
						if (newParent == parent)
							break;
						if (newParent == null || newParent.getNodeType() != Node.ELEMENT_NODE)
							break; // error
						newElement = (ElementImpl) newParent;
						continue;
					}
					Node child = next;
					next = next.getNextSibling();
					parent.removeChild(child);
					if (child == oldElement) {
						if (!oldElement.isCommentTag()) {
							// clone (re-create) end tag
							Element end = oldElement.removeEndTag();
							if (end != null) {
								child = end;
								newNext = end;
							}
						}
					}
					newElement.appendChild(child);
				}
			} else {
				if (!oldElement.isCommentTag()) {
					// clone (re-create) end tag
					Element end = oldElement.removeEndTag();
					if (end != null) {
						parent.insertBefore(end, oldElement);
						parent.removeChild(oldElement);
						newNext = end;
					}
				}
			}
		} else {
			newNext = oldElement.getNextSibling();
			parent.removeChild(oldElement);
		}

		// update context
		Node oldParent = this.context.getParentNode();
		Node oldNext = this.context.getNextNode();
		if (element == oldParent) {
			if (oldNext != null) {
				this.context.setNextNode(oldNext); // reset for new parent
			} else if (newNext != null) {
				this.context.setNextNode(newNext);
			} else {
				this.context.setParentNode(parent);
			}
		} else if (element == oldNext) {
			if (firstElement != null) {
				this.context.setParentNode(firstElement);
			} else if (first != null) {
				this.context.setNextNode(first);
			} else if (startElement != null) {
				this.context.setParentNode(startElement);
			} else {
				this.context.setNextNode(newNext);
			}
		}

		removeImplicitElement(elementParent);
	}

	/**
	 * removeStructuredDocumentRegion method
	 * 
	 * @param oldStructuredDocumentRegion
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 */
	private void removeStructuredDocumentRegion(IStructuredDocumentRegion oldStructuredDocumentRegion) {
		NodeImpl next = (NodeImpl) this.context.getNextNode();
		if (next != null) {
			short nodeType = next.getNodeType();
			if (nodeType != Node.ELEMENT_NODE) {
				IStructuredDocumentRegion flatNode = next.getStructuredDocumentRegion();
				if (flatNode == oldStructuredDocumentRegion) {
					removeNode(next);
					return;
				}
				if (nodeType != Node.TEXT_NODE) {
					throw new StructuredDocumentRegionManagementException();
				}
				if (flatNode == null) {
					// this is the case for empty Text
					// remove and continue
					removeNode(next);
					removeStructuredDocumentRegion(oldStructuredDocumentRegion);
					return;
				}
				TextImpl text = (TextImpl) next;
				boolean isShared = text.isSharingStructuredDocumentRegion(oldStructuredDocumentRegion);
				if (isShared) {
					// make sure there is next Text node sharing this
					TextImpl nextText = (TextImpl) this.context.findNextText();
					if (nextText == null || !nextText.hasStructuredDocumentRegion(oldStructuredDocumentRegion)) {
						isShared = false;
					}
				}
				oldStructuredDocumentRegion = text.removeStructuredDocumentRegion(oldStructuredDocumentRegion);
				if (oldStructuredDocumentRegion == null) {
					throw new StructuredDocumentRegionManagementException();
				}
				if (text.getStructuredDocumentRegion() == null) {
					// this is the case partial IStructuredDocumentRegion is
					// removed
					removeNode(text);
				} else {
					// notify the change
					text.notifyValueChanged();
				}
				// if shared, continue to remove IStructuredDocumentRegion
				// from them
				if (isShared)
					removeStructuredDocumentRegion(oldStructuredDocumentRegion);
				return;
			}

			ElementImpl element = (ElementImpl) next;
			if (element.hasStartTag()) {
				IStructuredDocumentRegion flatNode = element.getStartStructuredDocumentRegion();
				if (flatNode != oldStructuredDocumentRegion) {
					throw new StructuredDocumentRegionManagementException();
				}
				if (element.hasEndTag() || element.hasChildNodes()) {
					element.setStartStructuredDocumentRegion(null);
					removeStartTag(element);
				} else {
					removeNode(element);
				}
			} else {
				Node child = element.getFirstChild();
				if (child != null) {
					this.context.setNextNode(child);
					removeStructuredDocumentRegion(oldStructuredDocumentRegion);
					return;
				}

				if (!element.hasEndTag()) {
					// implicit element
					removeNode(element);
					removeStructuredDocumentRegion(oldStructuredDocumentRegion);
					return;
				}

				IStructuredDocumentRegion flatNode = element.getEndStructuredDocumentRegion();
				if (flatNode != oldStructuredDocumentRegion) {
					throw new StructuredDocumentRegionManagementException();
				}
				removeNode(element);
			}
			return;
		}

		Node parent = this.context.getParentNode();
		if (parent == null || parent.getNodeType() != Node.ELEMENT_NODE) {
			throw new StructuredDocumentRegionManagementException();
		}

		ElementImpl end = (ElementImpl) parent;
		if (end.hasEndTag()) {
			IStructuredDocumentRegion flatNode = end.getEndStructuredDocumentRegion();
			if (flatNode != oldStructuredDocumentRegion) {
				throw new StructuredDocumentRegionManagementException();
			}
			if (!end.hasStartTag() && !end.hasChildNodes()) {
				this.context.setNextNode(end);
				removeNode(end);
			} else {
				end.setEndStructuredDocumentRegion(null);
				removeEndTag(end);
			}
			return;
		}

		next = (NodeImpl) end.getNextSibling();
		if (next != null) {
			this.context.setNextNode(next);
			removeStructuredDocumentRegion(oldStructuredDocumentRegion);
			return;
		}

		parent = (NodeImpl) end.getParentNode();
		if (parent != null) {
			this.context.setParentNode(parent);
			removeStructuredDocumentRegion(oldStructuredDocumentRegion);
			return;
		}
	}

	/**
	 * replaceRegions method
	 * 
	 * @param flatNode
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 * @param newRegions
	 *            java.util.Vector
	 * @param oldRegions
	 *            java.util.Vector
	 */
	void replaceRegions(IStructuredDocumentRegion flatNode, ITextRegionList newRegions, ITextRegionList oldRegions) {
		if (flatNode == null)
			return;
		if (this.document == null)
			return;
		this.context = new XMLModelContext(this.document);

		// optimize typical cases
		String regionType = StructuredDocumentRegionUtil.getFirstRegionType(flatNode);
		if (regionType == XMLRegionContext.XML_TAG_OPEN) {
			changeStartTag(flatNode, newRegions, oldRegions);
		} else if (regionType == XMLRegionContext.XML_END_TAG_OPEN) {
			changeEndTag(flatNode, newRegions, oldRegions);
		} else {
			changeStructuredDocumentRegion(flatNode);
		}
	}

	/**
	 * replaceStructuredDocumentRegions method
	 * 
	 * @param newStructuredDocumentRegions
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegionList
	 * @param oldStructuredDocumentRegions
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegionList
	 */
	void replaceStructuredDocumentRegions(IStructuredDocumentRegionList newStructuredDocumentRegions, IStructuredDocumentRegionList oldStructuredDocumentRegions) {
		if (this.document == null)
			return;
		this.context = new XMLModelContext(this.document);

		int newCount = (newStructuredDocumentRegions != null ? newStructuredDocumentRegions.getLength() : 0);
		int oldCount = (oldStructuredDocumentRegions != null ? oldStructuredDocumentRegions.getLength() : 0);

		if (oldCount > 0) {
			setupContext(oldStructuredDocumentRegions.item(0));
			// Node startParent = this.context.getParentNode();

			Enumeration e = oldStructuredDocumentRegions.elements();
			while (e.hasMoreElements()) {
				IStructuredDocumentRegion flatNode = (IStructuredDocumentRegion) e.nextElement();
				if (flatNode == null)
					continue;
				removeStructuredDocumentRegion(flatNode);
			}
		} else {
			if (newCount == 0)
				return;
			setupContext(newStructuredDocumentRegions.item(0));
		}
		// make sure the parent is set to deepest level
		// when end tag has been removed
		this.context.setLast();

		if (newCount > 0) {
			Enumeration e = newStructuredDocumentRegions.elements();
			while (e.hasMoreElements()) {
				IStructuredDocumentRegion flatNode = (IStructuredDocumentRegion) e.nextElement();
				if (flatNode == null)
					continue;
				insertStructuredDocumentRegion(flatNode);
			}
		}

		cleanupText();
		cleanupEndTag();
	}

	/**
	 * setupContext method
	 * 
	 * @param flatNode
	 *            com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 */
	private void setupContext(IStructuredDocumentRegion startStructuredDocumentRegion) {
		int offset = startStructuredDocumentRegion.getStart();
		if (offset < 0)
			return;
		NodeImpl root = (NodeImpl) this.context.getRootNode();
		if (root == null)
			return;

		if (offset == 0) {
			// at the beggining of document
			Node child = root.getFirstChild();
			if (child != null)
				this.context.setNextNode(child);
			else
				this.context.setParentNode(root);
			return;
		}

		NodeImpl node = (NodeImpl) root.getNodeAt(offset);
		if (node == null) {
			// might be at the end of document
			this.context.setParentNode(root);
			this.context.setLast();
			return;
		}

		if (offset == node.getStartOffset()) {
			this.context.setNextNode(node);
			return;
		}

		if (node.getNodeType() == Node.TEXT_NODE) {
			TextImpl text = (TextImpl) node;
			Text nextText = text.splitText(startStructuredDocumentRegion);
			// notify the change
			text.notifyValueChanged();
			if (nextText == null)
				return; // error
			this.context.setNextNode(nextText);
			return;
		}

		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (offset >= ((NodeImpl) child).getEndOffset())
				continue;
			this.context.setNextNode(child);
			return;
		}
		this.context.setParentNode(node);
		this.context.setLast();
	}
}
