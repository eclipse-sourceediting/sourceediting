/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.document.XMLModelParser
 *                                           modified in order to process JSON Objects.
 *     Alina Marin <alina@mx1.ibm.com> - fixed some stuff to improve the synch between the editor and the model.  
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.document;

import java.util.Iterator;

import org.eclipse.wst.json.core.document.IJSONArray;
import org.eclipse.wst.json.core.document.IJSONModel;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONObject;
import org.eclipse.wst.json.core.document.IJSONPair;
import org.eclipse.wst.json.core.document.IJSONValue;
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;

/**
 * JSONModelParser
 */
public class JSONModelParser {
	private JSONModelContext context = null;
	private JSONModelImpl model = null;

	/**
	 */
	protected JSONModelParser(JSONModelImpl model) {
		super();

		if (model != null) {
			this.model = model;
		}
	}

	/**
	 * changeAttrName method
	 * 
	 */
	private void changeAttrName(IStructuredDocumentRegion flatNode,
			ITextRegion region) {
		int offset = flatNode.getStart();
		if (offset < 0)
			return;
		JSONNodeImpl root = (JSONNodeImpl) this.context.getRootNode();
		if (root == null)
			return;
		IJSONNode node = root.getNodeAt(offset);
		if (node == null)
			return;
		if (node.getNodeType() != IJSONNode.PAIR_NODE) {
			return;
		}

		JSONPairImpl pair = (JSONPairImpl) node;
		String name = flatNode.getText(region); 
		pair.setName(name);
		JSONObjectImpl parentObj = (JSONObjectImpl) node.getParentNode();
		if (parentObj != null && parentObj.getParentOrPairNode() instanceof JSONPairImpl) {
			JSONPairImpl parentPair = (JSONPairImpl) parentObj.getParentOrPairNode();
			if (parentPair.getValue() != parentObj.getParentNode()) {
				parentPair.setValue(parentObj);
			}
		}
	}
	
	private void changeAttrValue(IStructuredDocumentRegion flatNode,
			ITextRegion region) {
		int offset = flatNode.getStart();
		if (offset < 0)
			return;
		JSONNodeImpl root = (JSONNodeImpl) this.context.getRootNode();
		if (root == null)
			return;
		IJSONNode node = root.getNodeAt(offset);
		if (node == null)
			return;
		JSONValueImpl value = (JSONValueImpl) createJSONValue(region.getType());
		value.setStructuredDocumentRegion(flatNode);
		if(node.getNodeType() == IJSONNode.PAIR_NODE) {
			JSONPairImpl pair = (JSONPairImpl) node;
			pair.updateValue(value);
		} else if (node.getFirstStructuredDocumentRegion() != null && isJSONValue(node.getFirstStructuredDocumentRegion().getType())) {
			JSONValueImpl oldValue = (JSONValueImpl) node;
			oldValue.updateValue(value);
		} else if (node instanceof JSONArrayImpl) {
			if (value.getValueRegionType().equals(JSONRegionContexts.JSON_VALUE_BOOLEAN)
					|| value.getValueRegionType().equals(JSONRegionContexts.JSON_VALUE_NULL)) {
				// If the parent is a JSONArray insert the new JSONValue at
				// the end of the structure
				JSONArrayImpl array = (JSONArrayImpl) node;
				node.insertBefore(value, null);
				array.add(value);
				JSONPairImpl ownerPair = (JSONPairImpl) array.getParentOrPairNode();
				if (ownerPair.getValue() == null)
					ownerPair.setValue(array);
				else
					ownerPair.updateValue(array);
			}
		}
	}

	/**
	 * changeRegion method
	 * 
	 */
	void changeRegion(RegionChangedEvent change,
			IStructuredDocumentRegion flatNode, ITextRegion region) {
		if (flatNode == null || region == null)
			return;
		if (this.model.getDocument() == null)
			return;
		this.context = new JSONModelContext(this.model.getDocument());

		// determine if change was whitespace only change
		boolean isWhitespaceChange = false;
		if (change.getText() != null && change.getText().length() > 0) {
			isWhitespaceChange = Character.isWhitespace(change.getText()
					.charAt(0));
		} else if (change.getDeletedText() != null
				&& change.getDeletedText().length() > 0) {
			isWhitespaceChange = Character.isWhitespace(change.getDeletedText()
					.charAt(0));
		}
		if (isWhitespaceChange)
			return;
		// optimize typical cases
		String regionType = region.getType();
		/*
		 * if (regionType == JSONRegionContexts.JSON_CONTENT || regionType ==
		 * JSONRegionContexts.JSON_COMMENT_TEXT || regionType ==
		 * JSONRegionContexts.JSON_CDATA_TEXT || regionType ==
		 * JSONRegionContexts.BLOCK_TEXT || isNestedContent(regionType)) {
		 * changeData(flatNode, region); } else
		 */
		if (regionType == JSONRegionContexts.JSON_OBJECT_KEY) {
			changeAttrName(flatNode, region);
		} else if (isJSONValue(regionType)) {
			changeAttrValue(flatNode, region);
		} else if(regionType == JSONRegionContexts.JSON_UNKNOWN) {
			return;
		}
		// else if (regionType == JSONRegionContexts.JSON_TAG_ATTRIBUTE_VALUE) {
		// if (isWhitespaceChange
		// && (change.getOffset() >= flatNode.getStartOffset()
		// + region.getTextEnd())) {
		// // change is entirely in white-space
		// return;
		// }
		// changeAttrValue(flatNode, region);
		// } else if (regionType ==
		// JSONRegionContexts.JSON_TAG_ATTRIBUTE_EQUALS) {
		// if (isWhitespaceChange
		// && (change.getOffset() >= flatNode.getStartOffset()
		// + region.getTextEnd())) {
		// // change is entirely in white-space
		// return;
		// }
		// changeAttrEqual(flatNode, region);
		// } else if (regionType == JSONRegionContexts.JSON_TAG_NAME
		// || isNestedTagName(regionType)) {
		// if (isWhitespaceChange
		// && (change.getOffset() >= flatNode.getStartOffset()
		// + region.getTextEnd())) {
		// // change is entirely in white-space
		// return;
		// }
		// changeTagName(flatNode, region);
		// }
		else {
			changeStructuredDocumentRegion(flatNode);
		}
	}

	/**
	 */
	private void changeStartObject(IStructuredDocumentRegion flatNode,
			ITextRegionList newRegions, ITextRegionList oldRegions) {
		int offset = flatNode.getStart();
		if (offset < 0)
			return; // error
		JSONNodeImpl root = (JSONNodeImpl) this.context.getRootNode();
		if (root == null)
			return; // error
		IJSONNode node = root.getNodeAt(offset);
		if (node == null)
			return; // error

		if (node.getNodeType() != IJSONNode.OBJECT_NODE) {
			changeStructuredDocumentRegion(flatNode);
			return;
		}
		JSONObjectImpl element = (JSONObjectImpl) node;

		// check if changes are only for attributes and close tag
		boolean tagNameUnchanged = false;
		if (newRegions != null) {
			Iterator e = newRegions.iterator();
			while (e.hasNext()) {
				ITextRegion region = (ITextRegion) e.next();
				String regionType = region.getType();
				// if (regionType == JSONRegionContexts.JSON_TAG_ATTRIBUTE_NAME
				// || regionType == JSONRegionContexts.JSON_TAG_ATTRIBUTE_EQUALS
				// || regionType == JSONRegionContexts.JSON_TAG_ATTRIBUTE_VALUE)
				// continue;
				// if (regionType == JSONRegionContexts.JSON_TAG_CLOSE) {
				// // change from empty tag may have impact on structure
				// if (!element.isEmptyTag())
				// continue;
				// } else if (regionType == JSONRegionContexts.JSON_TAG_NAME
				// || isNestedTagName(regionType)) {
				// String oldTagName = element.getTagName();
				// String newTagName = flatNode.getText(region);
				// if (oldTagName != null && newTagName != null
				// && oldTagName.equals(newTagName)) {
				// // the tag name is unchanged
				// tagNameUnchanged = true;
				// continue;
				// }
				// }

				// other region has changed
				changeStructuredDocumentRegion(flatNode);
				return;
			}
		}
		// if (oldRegions != null) {
		// Iterator e = oldRegions.iterator();
		// while (e.hasNext()) {
		// ITextRegion region = (ITextRegion) e.next();
		// String regionType = region.getType();
		// if (regionType == JSONRegionContexts.JSON_TAG_ATTRIBUTE_NAME
		// || regionType == JSONRegionContexts.JSON_TAG_ATTRIBUTE_EQUALS
		// || regionType == JSONRegionContexts.JSON_TAG_ATTRIBUTE_VALUE)
		// continue;
		// if (regionType == JSONRegionContexts.JSON_TAG_CLOSE) {
		// // change from empty tag may have impact on structure
		// if (!element.isEmptyTag())
		// continue;
		// } else if (regionType == JSONRegionContexts.JSON_TAG_NAME
		// || isNestedTagName(regionType)) {
		// // if new tag name is unchanged, it's OK
		// if (tagNameUnchanged)
		// continue;
		// }
		//
		// // other region has changed
		// changeStructuredDocumentRegion(flatNode);
		// return;
		// }
		// }

		// update attributes
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return; // error
		// NamedNodeMap attributes = element.getAttributes();
		// if (attributes == null)
		// return; // error
		//
		// // first remove attributes
		// int regionIndex = 0;
		// int attrIndex = 0;
		// AttrImpl attr = null;
		// while (attrIndex < attributes.getLength()) {
		// attr = (AttrImpl) attributes.item(attrIndex);
		// if (attr == null) { // error
		// attrIndex++;
		// continue;
		// }
		// ITextRegion nameRegion = attr.getNameRegion();
		// if (nameRegion == null) { // error
		// element.removeAttributeNode(attr);
		// continue;
		// }
		// boolean found = false;
		// for (int i = regionIndex; i < regions.size(); i++) {
		// ITextRegion region = regions.get(i);
		// if (region == nameRegion) {
		// regionIndex = i + 1; // next region
		// found = true;
		// break;
		// }
		// }
		// if (found) {
		// attrIndex++;
		// } else {
		// element.removeAttributeNode(attr);
		// }
		// }
		//
		// // insert or update attributes
		// attrIndex = 0; // reset to first
		// AttrImpl newAttr = null;
		// ITextRegion oldValueRegion = null;
		// Iterator e = regions.iterator();
		// while (e.hasNext()) {
		// ITextRegion region = (ITextRegion) e.next();
		// String regionType = region.getType();
		// if (regionType == JSONRegionContexts.JSON_TAG_ATTRIBUTE_NAME) {
		// if (newAttr != null) {
		// // insert deferred new attribute
		// element.insertAttributeNode(newAttr, attrIndex++);
		// newAttr = null;
		// } else if (attr != null && oldValueRegion != null) {
		// // notify existing attribute value removal
		// attr.notifyValueChanged();
		// }
		//
		// oldValueRegion = null;
		// attr = (AttrImpl) attributes.item(attrIndex);
		// if (attr != null && attr.getNameRegion() == region) {
		// // existing attribute
		// attrIndex++;
		// // clear other regions
		// oldValueRegion = attr.getValueRegion();
		// attr.setEqualRegion(null);
		// attr.setValueRegion(null);
		// } else {
		// String name = flatNode.getText(region);
		// attr = (AttrImpl) this.model.getDocument().createAttribute(
		// name);
		// if (attr != null)
		// attr.setNameRegion(region);
		// // defer insertion of new attribute
		// newAttr = attr;
		// }
		// } else if (regionType ==
		// JSONRegionContexts.JSON_TAG_ATTRIBUTE_EQUALS) {
		// if (attr != null) {
		// attr.setEqualRegion(region);
		// }
		// } else if (regionType ==
		// JSONRegionContexts.JSON_TAG_ATTRIBUTE_VALUE) {
		// if (attr != null) {
		// attr.setValueRegion(region);
		// if (attr != newAttr && oldValueRegion != region) {
		// // notify existing attribute value changed
		// attr.notifyValueChanged();
		// }
		// oldValueRegion = null;
		// attr = null;
		// }
		// }
		// }
		//
		// if (newAttr != null) {
		// // insert deferred new attribute
		// element.appendAttributeNode(newAttr);
		// } else if (attr != null && oldValueRegion != null) {
		// // notify existing attribute value removal
		// attr.notifyValueChanged();
		// }
	}

	/**
	 * changeStructuredDocumentRegion method
	 * 
	 */
	private void changeStructuredDocumentRegion(
			IStructuredDocumentRegion flatNode) {
		if (flatNode == null)
			return;
		if (this.model.getDocument() == null)
			return;

		setupContext(flatNode);
		
		removeStructuredDocumentRegion(flatNode);
		// make sure the parent is set to deepest level
		// when end tag has been removed
		this.context.setLast();
		insertStructuredDocumentRegion(flatNode);

		// cleanupText();
		cleanupEndTag();
	}

	/**
	 * cleanupContext method
	 */
	private void cleanupEndTag() {
		IJSONNode parent = this.context.getParentNode();
		IJSONNode next = this.context.getNextNode();
		while (parent != null) {
			while (next != null) {
				if (next.getNodeType() == IJSONNode.OBJECT_NODE) {
					JSONObjectImpl element = (JSONObjectImpl) next;
					// if (element.isEndTag()) {
					// // floating end tag
					// String tagName = element.getTagName();
					// String rootName = getFindRootName(tagName);
					// JSONObjectImpl start = (JSONObjectImpl) this.context
					// .findStartTag(tagName, rootName);
					// if (start != null) {
					// insertEndTag(start);
					// // move the end tag from 'element' to 'start'
					// start.addEndTag(element);
					// removeNode(element);
					// parent = this.context.getParentNode();
					// next = this.context.getNextNode();
					// continue;
					// }
					// }
				}

				IJSONNode first = next.getFirstChild();
				if (first != null) {
					parent = next;
					next = first;
					this.context.setCurrentNode(next);
				} else {
					next = next.getNextSibling();
					this.context.setCurrentNode(next);
				}
			}

			// if (parent.getNodeType() == IJSONNode.OBJECT_NODE) {
			// JSONObjectImpl element = (JSONObjectImpl) parent;
			// if (!element.hasEndTag() && element.hasStartTag()
			// && element.getNextSibling() == null) {
			// String tagName = element.getTagName();
			// JSONObjectImpl end = (JSONObjectImpl) this.context
			// .findEndTag(tagName);
			// if (end != null) {
			// // move the end tag from 'end' to 'element'
			// element.addEndTag(end);
			// removeEndTag(end);
			// this.context.setParentNode(parent); // reset context
			// continue;
			// }
			// }
			// }

			next = parent.getNextSibling();
			parent = parent.getParentNode();
			if (next != null) {
				this.context.setCurrentNode(next);
			} else {
				this.context.setParentNode(parent);
			}
		}
	}

	/**
	 */
	private void demoteNodes(IJSONNode root, IJSONNode newParent,
			IJSONNode oldParent, IJSONNode next) {
		if (newParent.getNodeType() != IJSONNode.OBJECT_NODE)
			return;
		JSONObjectImpl newElement = (JSONObjectImpl) newParent;

		// find next
		while (next == null) {
			if (oldParent.getNodeType() != IJSONNode.OBJECT_NODE)
				return;
			JSONObjectImpl oldElement = (JSONObjectImpl) oldParent;
			if (oldElement.hasEndTag())
				return;
			oldParent = oldElement.getParentNode();
			if (oldParent == null)
				return; // error
			next = oldElement.getNextSibling();
		}

		while (next != null) {
			boolean done = false;
			if (next.getNodeType() == IJSONNode.OBJECT_NODE) {
				JSONObjectImpl nextElement = (JSONObjectImpl) next;
				if (!nextElement.hasStartTag()) {
					IJSONNode nextChild = nextElement.getFirstChild();
					if (nextChild != null) {
						// demote children
						next = nextChild;
						oldParent = nextElement;
						continue;
					}

					// if (nextElement.hasEndTag()) {
					// if (nextElement.matchEndTag(newElement)) {
					// // stop at the matched invalid end tag
					// next = nextElement.getNextSibling();
					// oldParent.removeChild(nextElement);
					// newElement.addEndTag(nextElement);
					//
					// if (newElement == root)
					// return;
					// IJSONNode p = newElement.getParentNode();
					// // check if reached to top
					// if (p == null || p == oldParent
					// || p.getNodeType() != IJSONNode.OBJECT_NODE)
					// return;
					// newElement = (JSONObjectImpl) p;
					// done = true;
					// }
					// } else {
					// remove implicit element
					next = nextElement.getNextSibling();
					oldParent.removeChild(nextElement);
					done = true;
					// }
				}
			}

			if (!done) {
				// if (!canContain(newElement, next)) {
				// if (newElement == root)
				// return;
				// Node p = newElement.getParentNode();
				// // check if reached to top
				// if (p == null || p == oldParent
				// || p.getNodeType() != IJSONNode.OBJECT_NODE)
				// return;
				// newElement = (JSONObjectImpl) p;
				// continue;
				// }

				IJSONNode child = next;
				next = next.getNextSibling();
				oldParent.removeChild(child);
				insertNode(newElement, child, null);
				IJSONNode childParent = child.getParentNode();
				if (childParent != newElement) {
					newElement = (JSONObjectImpl) childParent;
				}
			}

			// find next parent and sibling
			while (next == null) {
				if (oldParent.getNodeType() != IJSONNode.OBJECT_NODE)
					return;
				JSONObjectImpl oldElement = (JSONObjectImpl) oldParent;

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
	protected final IJSONModel getModel() {
		return this.model;
	}

	/**
	 * insertEndTag method
	 * 
	 */
	private void updateEndObject(IStructuredDocumentRegion flatNode) {
		// ITextRegionList regions = flatNode.getRegions();
		// if (regions == null)
		// return;
		//
		// String tagName = null;
		// Iterator e = regions.iterator();
		// while (e.hasNext()) {
		// ITextRegion region = (ITextRegion) e.next();
		// String regionType = region.getType();
		// if (regionType == JSONRegionContexts.JSON_TAG_NAME
		// || isNestedTagName(regionType)) {
		// if (tagName == null)
		// tagName = flatNode.getText(region);
		// }
		// }
		//
		// if (tagName == null) { // invalid end tag
		// insertText(flatNode); // regard as invalid text
		// return;
		// }
		//
		// String rootName = getFindRootName(tagName);
		JSONObjectImpl start = (JSONObjectImpl) this.context
				.findParentObject();
		if (start != null) { // start tag found
			// insertEndTag(start);
			start.setEndStructuredDocumentRegion(flatNode);

			// update context
			this.context.setParentNode(start.getParentNode());
			// // re-check the next sibling
			// newNext = start.getNextSibling();
			// if (newNext != null)
			// this.context.setCurrentNode(newNext);
			// else
			// this.context.setParentNode(newParent);
			// return;
		}
		//
		// // invalid end tag
		// JSONObjectImpl end = null;
		// try {
		// end = (JSONObjectImpl)
		// this.model.getDocument().createElement(tagName);
		// } catch (JSONException ex) {
		// }
		// if (end == null) { // invalid end tag
		// insertText(flatNode); // regard as invalid text
		// return;
		// }
		// end.setEndStructuredDocumentRegion(flatNode);
		// insertNode(end);
	}

	/**
	 * insertNode method
	 * 
	 * @param child
	 *            org.w3c.dom.Node
	 */
	private void insertNode(IJSONNode node) {
		if (node == null || this.context == null) {
			return;
		}
		IJSONNode parent = this.context.getParentNode();
		if (parent == null) {
			return;
		}

		if (parent.getNodeType() == IJSONNode.PAIR_NODE) {
			IJSONPair pair = (IJSONPair) parent;
			((JSONPairImpl) pair).setValue((IJSONValue) node);
			return;
		}
		
		if (parent.getLastChild() != null
				&& parent.getLastChild().getNodeType() == IJSONNode.PAIR_NODE) {
			IJSONPair pair = (IJSONPair) parent.getLastChild();
			((JSONPairImpl) pair).setValue((IJSONValue) node);
			return;
		}

		IJSONNode next = this.context.getNextNode();
		insertNode(parent, node, next);
		next = node.getNextSibling();
		if (next != null) {
			this.context.setCurrentNode(next);
		} else {
			this.context.setParentNode(node.getParentNode());
		}

		// if (node != null && this.context != null) {
		// IJSONNode aparent = this.context.getParentNode();
		// if (parent != null) {
		// IJSONNode next = this.context.getNextNode();
		// // Reset parents which are closed container elements; should not
		// // be parents
		// if (parent.getNodeType() == IJSONNode.OBJECT_NODE) {
		// String type = ((JSONObjectImpl) parent)
		// .getStartStructuredDocumentRegion().getLastRegion()
		// .getType();
		// if (((JSONObjectImpl) parent).isContainer()
		// /* && type == JSONRegionContexts.JSON_EMPTY_TAG_CLOSE */) {
		// // next = parent.getNextSibling();
		// // parent = parent.getParentNode();
		// } else {
		// // ModelParserAdapter adapter = getParserAdapter();
		// // if (adapter != null) {
		// // while (parent.getNodeType() == IJSONNode.OBJECT_NODE
		// // && !adapter.canContain((Element) parent,
		// // node)
		// // && adapter
		// // .isEndTagOmissible((Element) parent)) {
		// // next = parent.getNextSibling();
		// // parent = parent.getParentNode();
		// // }
		// // }
		// }
		// }
		// insertNode(parent, node, next);
		// next = node.getNextSibling();
		// if (next != null) {
		// this.context.setCurrentNode(next);
		// } else {
		// this.context.setParentNode(node.getParentNode());
		// }
		// }
		// }
	}

	/**
	 */
	private void insertNode(IJSONNode parent, IJSONNode node, IJSONNode next) {
		while (next != null && next.getNodeType() == IJSONNode.OBJECT_NODE) {
			JSONObjectImpl nextElement = (JSONObjectImpl) next;
			if (nextElement.hasStartTag())
				break;
			// if (!canBeImplicitTag(nextElement, node))
			// break;
			parent = nextElement;
			next = nextElement.getFirstChild();
		}
		// Element implicitElement = createImplicitElement(parent, node);
		// if (implicitElement != null)
		// node = implicitElement;
		parent.insertBefore(node, next);
	}

	private void insertObject(IStructuredDocumentRegion flatNode) {
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return;

		JSONObjectImpl element = null;
		if(this.context.getCurrentNode() != null && this.context.getCurrentNode() instanceof IJSONObject) {
			element = (JSONObjectImpl) this.context.getCurrentNode();
		} else {
			element = (JSONObjectImpl) this.model.getDocument()
					.createJSONObject();
		}
		if (element == null) {
			return;
		}
		element.setStartStructuredDocumentRegion(flatNode);
		insertStartObjectOLD(element);
	}

	protected void insertStartObjectOLD(IJSONObject element) {
		if (element == null)
			return;
		if (this.context == null)
			return;

		insertNode(element);

		JSONObjectImpl newElement = (JSONObjectImpl) element;
		if (newElement.isEmptyTag() || !newElement.isContainer())
			return;

		// Ignore container tags that have been closed
		String type = newElement.getStartStructuredDocumentRegion()
				.getLastRegion().getType();
		// if (newElement.isContainer()
		// && type == JSONRegionContexts.JSON_EMPTY_TAG_CLOSE)
		// return;

		// demote siblings
		IJSONNode parent = this.context.getParentNode();
		if (parent == null)
			return; // error
		IJSONNode next = this.context.getNextNode();
		demoteNodes(element, element, parent, next);

		// update context
		IJSONNode firstChild = element.getFirstChild();
		if (firstChild != null)
			this.context.setCurrentNode(firstChild);
		else
			this.context.setParentNode(element);
	}

	// ---------------------------- JSON Array

	protected void insertStartArray(IJSONArray element) {
		if (element == null)
			return;
		if (this.context == null)
			return;

		insertNode(element);

		JSONArrayImpl newElement = (JSONArrayImpl) element;
		String type = newElement.getStartStructuredDocumentRegion()
				.getLastRegion().getType();
		// demote siblings
		IJSONNode parent = this.context.getParentNode();
		if (parent == null)
			return; // error
		IJSONNode next = this.context.getNextNode();
		demoteNodes(element, element, parent, next);
		// update context
		IJSONNode firstChild = element.getFirstChild();
		if (firstChild != null)
			this.context.setCurrentNode(firstChild);
		else
			this.context.setParentNode(element);
	}

	/**
	 * insertStartTag method
	 * 
	 */
	private void insertArray(IStructuredDocumentRegion flatNode) {
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return;
		JSONArrayImpl element = (JSONArrayImpl) this.model.getDocument()
				.createJSONArray();
		element.setStartStructuredDocumentRegion(flatNode);
		insertStartArray(element);
	}

	private void updateEndArray(IStructuredDocumentRegion flatNode) {
		JSONArrayImpl start = (JSONArrayImpl) this.context.findParentArray();
		if (start != null) { // start tag found
			start.setEndStructuredDocumentRegion(flatNode);
			// update context
			this.context.setParentNode(start.getParentNode());
		}

	}

	// ---------------- Commons insert

	/**
	 * insertStructuredDocumentRegion method
	 * 
	 */
	protected void insertStructuredDocumentRegion(
			IStructuredDocumentRegion flatNode) {
		String regionType = StructuredDocumentRegionUtil
				.getFirstRegionType(flatNode);
		if (regionType == JSONRegionContexts.JSON_OBJECT_OPEN) {
			insertObject(flatNode);
		} else if (regionType == JSONRegionContexts.JSON_OBJECT_CLOSE) {
			updateEndObject(flatNode);
		} else if (regionType == JSONRegionContexts.JSON_ARRAY_OPEN) {
			insertArray(flatNode);
		} else if (regionType == JSONRegionContexts.JSON_ARRAY_CLOSE) {
			updateEndArray(flatNode);
		} else if (regionType == JSONRegionContexts.JSON_OBJECT_KEY) {
			insertObjectKey(flatNode);
		} else if (regionType == JSONRegionContexts.JSON_VALUE_BOOLEAN) {
			insertValue(flatNode, regionType);
		} else if (regionType == JSONRegionContexts.JSON_VALUE_NULL) {
			insertValue(flatNode, regionType);
		} else if (regionType == JSONRegionContexts.JSON_VALUE_NUMBER) {
			insertValue(flatNode, regionType);
		} else if (regionType == JSONRegionContexts.JSON_VALUE_STRING) {
			insertValue(flatNode, regionType);
		}
	}

	private void insertValue(IStructuredDocumentRegion flatNode,
			String regionType) {
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return;
		ITextRegion nameRegion = StructuredDocumentRegionUtil
				.getFirstRegion(flatNode);
		// Create JSON Value
		JSONValueImpl value = (JSONValueImpl) createJSONValue(regionType);
		value.setStructuredDocumentRegion(flatNode);
		// If current node is different from null, it means this value shoyld be
		// inserted as the value of an existing pair node
		if (this.context.getCurrentNode() != null && this.context.getCurrentNode() instanceof JSONPairImpl) {
			JSONPairImpl pair = (JSONPairImpl) this.context.getCurrentNode();
			pair.setValue(value);
			JSONNodeImpl parent = (JSONNodeImpl) this.context.getParentNode();
			parent.insertBefore(pair, this.context.getNextNode());
			// If parent is an instance of JSONObject add the pair node to the
			// parent node
			if(parent instanceof IJSONObject) {
				JSONObjectImpl parentObject = (JSONObjectImpl) parent;
				parentObject.add(pair);
			}
		} else {
			// There is no context, it means the model is reloaded or loaded for
			// the first time
			JSONStructureImpl structure = (JSONStructureImpl) this.context.findParentStructure();
			if (structure != null) {
				if (structure.getNodeType() == IJSONNode.OBJECT_NODE) {
					// If the parent is a JSONObject look for the last children,
					// ensure it is a JSONPair and add set the value of the last
					// children
					if (structure.getLastChild() != null
							&& structure.getLastChild().getNodeType() == IJSONNode.PAIR_NODE) {
						((JSONPairImpl) structure.getLastChild()).setValue(value);
					}
					if(structure.getParentOrPairNode() instanceof JSONPairImpl) {
						JSONPairImpl parentPair = (JSONPairImpl) structure.getParentOrPairNode();
						if (structure.getParentNode() != parentPair.getValue()) {
							parentPair.setValue(structure);
						}
					}
				} else if (structure.getNodeType() == IJSONNode.ARRAY_NODE) {
					// If the parent is a JSONArray insert the new JSONValue at
					// the end of the structure
					JSONArrayImpl array = (JSONArrayImpl) structure;
					structure.insertBefore(value, null);
					array.add(value);
					JSONPairImpl ownerPair = (JSONPairImpl) array.getParentOrPairNode();
					if(ownerPair.getValue() == null)
						ownerPair.setValue(array);
					else
						ownerPair.updateValue(array);
				} else {
					insertNode(structure, value, null);
				}
			}
		}
	}

	private IJSONValue createJSONValue(String regionType) {
		if (regionType == JSONRegionContexts.JSON_VALUE_BOOLEAN) {
			return this.model.getDocument().createBooleanValue();
		} else if (regionType == JSONRegionContexts.JSON_VALUE_NULL) {
			return this.model.getDocument().createNullValue();
		} else if (regionType == JSONRegionContexts.JSON_VALUE_NUMBER) {
			return this.model.getDocument().createNumberValue();
		} else if (regionType == JSONRegionContexts.JSON_VALUE_STRING) {
			return this.model.getDocument().createStringValue();
		}
		return null;
	}

	private void insertNumberValue(IStructuredDocumentRegion flatNode) {
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return;

		ITextRegion nameRegion = StructuredDocumentRegionUtil
				.getFirstRegion(flatNode);
		JSONStructureImpl array = (JSONStructureImpl) this.context
				.findParentStructure();
		if (array != null) {
			JSONNumberValueImpl value = (JSONNumberValueImpl) this.model
					.getDocument().createNumberValue();
			insertNode(array, value, null);
		}
	}

	private void insertNullValue(IStructuredDocumentRegion flatNode) {
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return;

		ITextRegion nameRegion = StructuredDocumentRegionUtil
				.getFirstRegion(flatNode);
		JSONStructureImpl array = (JSONStructureImpl) this.context
				.findParentStructure();
		if (array != null) {
			JSONNullValueImpl value = (JSONNullValueImpl) this.model
					.getDocument().createNullValue();
			insertNode(array, value, null);
		}
	}

	private void insertStringValue(IStructuredDocumentRegion flatNode) {
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null)
			return;

		ITextRegion nameRegion = StructuredDocumentRegionUtil
				.getFirstRegion(flatNode);
		JSONStructureImpl array = (JSONStructureImpl) this.context
				.findParentStructure();
		if (array != null) {
			JSONStringValueImpl value = (JSONStringValueImpl) this.model
					.getDocument().createStringValue();
			insertNode(array, value, null);
		}
	}

	private void insertObjectKey(IStructuredDocumentRegion flatNode) {
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null) {
			return;
		}
		JSONObjectImpl object = (JSONObjectImpl) this.context
				.findParentObject();
		if (object == null) {
			return;
		}

		JSONPairImpl pair = null;
		for (Iterator i = regions.iterator(); i.hasNext();) {
			ITextRegion region = (ITextRegion) i.next();
			if (region == null) {
				continue;
			}
			if (region.getType() == JSONRegionContexts.JSON_OBJECT_KEY) {
				// Set the proper name and region to the JSONPair node
				String name = flatNode.getText(region);
				pair = (JSONPairImpl) this.model.getDocument().createJSONPair(name);
				pair.setStartStructuredDocumentRegion(flatNode);
				pair.setNameRegion(region);
				// Ensure to add the pair node in the right place, taking as
				// reference the current node offset from the context
				if (context.getCurrentNode() != null
						&& context.getCurrentNode().getStartOffset() > pair.getStartOffset())
					insertNode(object, pair, this.context.getCurrentNode());
				else
					insertNode(object, pair, this.context.getNextNode());
				// Add the pair to the parent object
				object.add(pair);
				// Update context with the new JSONPair as the current node
				if (this.context.getCurrentNode() != null
						&& this.context.getCurrentNode().getStartOffset() == pair.getStartOffset())
					this.context.setCurrentNode(pair);
				if(object.getParentOrPairNode() instanceof JSONPairImpl) {
					JSONPairImpl parentPair = (JSONPairImpl) object.getParentOrPairNode();
					if (object.getParentNode() != parentPair.getValue()) {
						parentPair.setValue(object);
					}
				}
			} else if (region.getType() == JSONRegionContexts.JSON_COLON) {
				pair.setEqualRegion(region);
			} else if (region.getType() == JSONRegionContexts.JSON_VALUE_BOOLEAN) {
				JSONBooleanValueImpl value = (JSONBooleanValueImpl) this.model
						.getDocument().createBooleanValue();
				pair.setValue(value);
			} else if (region.getType() == JSONRegionContexts.JSON_VALUE_NULL) {
				JSONNullValueImpl value = (JSONNullValueImpl) this.model
						.getDocument().createNullValue();
				pair.setValue(value);
			} else if (region.getType() == JSONRegionContexts.JSON_VALUE_NUMBER) {
				JSONNumberValueImpl value = (JSONNumberValueImpl) this.model
						.getDocument().createNumberValue();
				pair.setValue(value);
			} else if (region.getType() == JSONRegionContexts.JSON_VALUE_STRING) {
				JSONStringValueImpl value = (JSONStringValueImpl) this.model
						.getDocument().createStringValue();
				pair.setValue(value);
			}
		}
	}

	protected boolean isNestedTag(String regionType) {
		boolean result = false;
		return result;
	}

	protected boolean isNestedCommentText(String regionType) {
		boolean result = false;
		return result;
	}

	protected boolean isNestedCommentOpen(String regionType) {
		boolean result = false;
		return result;
	}

	protected boolean isNestedTagName(String regionType) {
		boolean result = false;
		return result;
	}

	protected boolean isNestedContent(String regionType) {
		boolean result = false;
		return result;
	}

	/**
	 */
	private void promoteNodes(IJSONNode root, IJSONNode newParent,
			IJSONNode newNext, IJSONNode oldParent, IJSONNode next) {
		JSONObjectImpl newElement = null;
		if (newParent.getNodeType() == IJSONNode.OBJECT_NODE) {
			newElement = (JSONObjectImpl) newParent;
		}

		IJSONNode rootParent = root.getParentNode();
		while (oldParent != rootParent) {
			while (next != null) {
				boolean done = false;
				boolean endTag = false;
				if (next.getNodeType() == IJSONNode.OBJECT_NODE) {
					JSONObjectImpl nextElement = (JSONObjectImpl) next;
					if (!nextElement.hasStartTag()) {
						IJSONNode nextChild = nextElement.getFirstChild();
						if (nextChild != null) {
							// promote children
							next = nextChild;
							oldParent = nextElement;
							continue;
						}

						// if (nextElement.hasEndTag()) {
						// if (nextElement.matchEndTag(newElement)) {
						// endTag = true;
						// }
						// } else {
						// remove implicit element
						next = nextElement.getNextSibling();
						oldParent.removeChild(nextElement);
						done = true;
						// }
					}
				}

				if (!done) {
					if (!endTag && newElement != null
					/* && !canContain(newElement, next) */) {
						newParent = newElement.getParentNode();
						if (newParent == null)
							return; // error
						IJSONNode elementNext = newElement.getNextSibling();
						// promote siblings
						promoteNodes(newElement, newParent, elementNext,
								newElement, newNext);
						newNext = newElement.getNextSibling();
						if (newParent.getNodeType() == IJSONNode.OBJECT_NODE) {
							newElement = (JSONObjectImpl) newParent;
						} else {
							newElement = null;
						}
						continue;
					}

					IJSONNode child = next;
					next = next.getNextSibling();
					oldParent.removeChild(child);
					insertNode(newParent, child, newNext);
					IJSONNode childParent = child.getParentNode();
					if (childParent != newParent) {
						newParent = childParent;
						newElement = (JSONObjectImpl) newParent;
						newNext = child.getNextSibling();
					}
				}
			}

			if (oldParent.getNodeType() != IJSONNode.OBJECT_NODE)
				return;
			JSONObjectImpl oldElement = (JSONObjectImpl) oldParent;
			oldParent = oldElement.getParentNode();
			if (oldParent == null)
				return; // error
			next = oldElement.getNextSibling();

			if (oldElement.hasEndTag()) {
				IJSONObject end = null;
				if (!oldElement.hasChildNodes() && !oldElement.hasStartTag()) {
					oldParent.removeChild(oldElement);
					end = oldElement;
				} else {
					// end = oldElement.removeEndTag();
				}
				if (end != null) {
					insertNode(newParent, end, newNext);
					IJSONNode endParent = end.getParentNode();
					if (endParent != newParent) {
						newParent = endParent;
						newElement = (JSONObjectImpl) newParent;
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
	private void removeEndTag(IJSONNode element) {
		if (element == null)
			return;
		if (this.context == null)
			return;

		IJSONNode parent = element.getParentNode();
		if (parent == null)
			return; // error

		if (!((JSONObjectImpl) element).isContainer()) {
			// just update context
			IJSONNode elementNext = element.getNextSibling();
			if (elementNext != null)
				this.context.setCurrentNode(elementNext);
			else
				this.context.setParentNode(parent);
			return;
		}

		// demote siblings
		IJSONNode next = element.getNextSibling();
		JSONObjectImpl newElement = (JSONObjectImpl) element;
		// find new parent
		for (IJSONNode last = newElement.getLastChild(); last != null; last = last
				.getLastChild()) {
			if (last.getNodeType() != IJSONNode.OBJECT_NODE)
				break;
			JSONObjectImpl lastElement = (JSONObjectImpl) last;
			if (lastElement.hasEndTag() || lastElement.isEmptyTag()
					|| !lastElement.isContainer())
				break;
			newElement = lastElement;
		}
		IJSONNode lastChild = newElement.getLastChild();
		demoteNodes(element, newElement, parent, next);

		// update context
		IJSONNode newNext = null;
		if (lastChild != null)
			newNext = lastChild.getNextSibling();
		else
			newNext = newElement.getFirstChild();
		if (newNext != null)
			this.context.setCurrentNode(newNext);
		else
			this.context.setParentNode(newElement);
	}

	/**
	 * removeNode method
	 * 
	 * @param node
	 *            org.w3c.dom.Node
	 */
	private void removeNode(IJSONNode node) {
		if (node == null)
			return;
		if (this.context == null)
			return;

		IJSONNode parent = node.getParentNode();
		if (parent == null) {
			if(node instanceof IJSONValue) {
				parent = node.getParentOrPairNode();
				if(parent == null) {
					return;
				}
			}
		}
		IJSONNode next = node.getNextSibling();
		IJSONNode prev = node.getPreviousSibling();

		// update context
		IJSONNode oldParent = this.context.getParentNode();
		if (node == oldParent) {
			if (next != null)
				this.context.setCurrentNode(next);
			else
				this.context.setParentNode(parent);
		} else {
			IJSONNode oldNext = this.context.getNextNode();
			if (node == oldNext) {
				this.context.setCurrentNode(next);
			}
		}

		parent.removeChild(node);

		// if (removeImplicitElement(parent) != null)
		// return;

		// demote sibling
		if (prev != null && prev.getNodeType() == IJSONNode.OBJECT_NODE) {
			JSONObjectImpl newElement = (JSONObjectImpl) prev;
			if (!newElement.hasEndTag() && !newElement.isEmptyTag()
					&& newElement.isContainer()) {
				// find new parent
				for (IJSONNode last = newElement.getLastChild(); last != null; last = last
						.getLastChild()) {
					if (last.getNodeType() != IJSONNode.OBJECT_NODE)
						break;
					JSONObjectImpl lastElement = (JSONObjectImpl) last;
					if (lastElement.hasEndTag() || lastElement.isEmptyTag()
							|| !lastElement.isContainer())
						break;
					newElement = lastElement;
				}
				IJSONNode lastChild = newElement.getLastChild();
				demoteNodes(prev, newElement, parent, next);

				// update context
				IJSONNode newNext = null;
				if (lastChild != null)
					newNext = lastChild.getNextSibling();
				else
					newNext = newElement.getFirstChild();
				if (newNext != null)
					this.context.setCurrentNode(newNext);
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
	private void removeStartObject(IJSONObject element) {
		if (element == null)
			return;
		if (this.context == null)
			return;

		// for implicit tag
		JSONObjectImpl oldElement = (JSONObjectImpl) element;
		// if (canBeImplicitTag(oldElement)) {
		// Node newParent = null;
		// Node prev = oldElement.getPreviousSibling();
		// if (prev != null && prev.getNodeType() == IJSONNode.OBJECT_NODE) {
		// JSONObjectImpl prevElement = (JSONObjectImpl) prev;
		// if (!prevElement.hasEndTag()) {
		// if (prevElement.hasStartTag()
		// || prevElement
		// .matchTagName(oldElement.getTagName())) {
		// newParent = prevElement;
		// }
		// }
		// }
		// if (newParent == null) {
		// // this element should stay as implicit tag
		// // just remove all attributes
		// oldElement.removeStartTag();
		//
		// // update context
		// Node child = oldElement.getFirstChild();
		// if (child != null) {
		// this.context.setCurrentNode(child);
		// } else if (oldElement.hasEndTag()) {
		// this.context.setParentNode(oldElement);
		// }
		// return;
		// }
		// }
		// // for comment tag
		// if (oldElement.isCommentTag())
		// oldElement.removeStartTag();

		// promote children
		IJSONNode elementParent = element.getParentNode();
		IJSONNode parent = elementParent;
		if (parent == null)
			return;
		IJSONNode first = element.getFirstChild();
		IJSONNode firstElement = null; // for the case first is removed as end
		// tag
		if (first != null) {
			// find new parent for children
			JSONObjectImpl newElement = null;
			for (IJSONNode last = element.getPreviousSibling(); last != null; last = last
					.getLastChild()) {
				if (last.getNodeType() != IJSONNode.OBJECT_NODE)
					break;
				JSONObjectImpl lastElement = (JSONObjectImpl) last;
				if (lastElement.hasEndTag() || lastElement.isEmptyTag()
						|| !lastElement.isContainer())
					break;
				newElement = lastElement;
			}
			IJSONNode next = first;
			if (newElement != null) {
				while (next != null) {
					if (!newElement.hasEndTag() && newElement.hasStartTag()
							&& next.getNodeType() == IJSONNode.OBJECT_NODE) {
						JSONObjectImpl nextElement = (JSONObjectImpl) next;
						if (!nextElement.hasStartTag()
								&& nextElement.hasEndTag()
						/* && nextElement.matchEndTag(newElement) */) {
							// stop at the matched invalid end tag
							IJSONNode elementChild = nextElement
									.getFirstChild();
							while (elementChild != null) {
								IJSONNode nextChild = elementChild
										.getNextSibling();
								nextElement.removeChild(elementChild);
								newElement.appendChild(elementChild);
								elementChild = nextChild;
							}

							next = nextElement.getNextSibling();
							element.removeChild(nextElement);
							// newElement.addEndTag(nextElement);
							if (nextElement == first)
								firstElement = newElement;

							IJSONNode newParent = newElement.getParentNode();
							if (newParent == parent)
								break;
							if (newParent == null
									|| newParent.getNodeType() != IJSONNode.OBJECT_NODE)
								break; // error
							newElement = (JSONObjectImpl) newParent;
							continue;
						}
					}
					// if (!canContain(newElement, next)) {
					// Node newParent = newElement.getParentNode();
					// if (newParent == parent)
					// break;
					// if (newParent == null
					// || newParent.getNodeType() != IJSONNode.OBJECT_NODE)
					// break; // error
					// newElement = (JSONObjectImpl) newParent;
					// continue;
					// }
					IJSONNode child = next;
					next = next.getNextSibling();
					element.removeChild(child);
					newElement.appendChild(child);
				}
				newElement = null;
			}
			if (parent.getNodeType() == IJSONNode.OBJECT_NODE) {
				newElement = (JSONObjectImpl) parent;
			}
			while (next != null) {
				if (newElement == null /* || canContain(newElement, next) */) {
					IJSONNode child = next;
					next = next.getNextSibling();
					element.removeChild(child);
					parent.insertBefore(child, element);
					continue;
				}

				parent = newElement.getParentNode();
				if (parent == null)
					return;

				// promote siblings
				IJSONNode newNext = newElement.getNextSibling();
				IJSONNode child = element;
				while (child != null) {
					IJSONNode nextChild = child.getNextSibling();
					newElement.removeChild(child);
					parent.insertBefore(child, newNext);
					child = nextChild;
				}

				// leave the old end tag where it is
				if (newElement.hasEndTag()) {
					// IJSONObject end = newElement.removeEndTag();
					// if (end != null) {
					// parent.insertBefore(end, newNext);
					// }
				}
				if (!newElement.hasStartTag()) {
					// implicit element
					if (!newElement.hasChildNodes()) {
						parent.removeChild(newElement);
					}
				}

				if (parent.getNodeType() == IJSONNode.OBJECT_NODE) {
					newElement = (JSONObjectImpl) parent;
				} else {
					newElement = null;
				}
			}
		}

		IJSONNode newNext = element;
		IJSONNode startElement = null; // for the case element is removed as end
		// tag
		if (oldElement.hasEndTag()) {
			// find new parent for invalid end tag and siblings
			JSONObjectImpl newElement = null;
			for (IJSONNode last = element.getPreviousSibling(); last != null; last = last
					.getLastChild()) {
				if (last.getNodeType() != IJSONNode.OBJECT_NODE)
					break;
				JSONObjectImpl lastElement = (JSONObjectImpl) last;
				if (lastElement.hasEndTag() || lastElement.isEmptyTag()
						|| !lastElement.isContainer())
					break;
				newElement = lastElement;
			}
			if (newElement != null) {
				// demote invalid end tag and sibling
				IJSONNode next = element;
				while (next != null) {
					if (!newElement.hasEndTag() && newElement.hasStartTag()
							&& next.getNodeType() == IJSONNode.OBJECT_NODE) {
						JSONObjectImpl nextElement = (JSONObjectImpl) next;
						if (!nextElement.hasStartTag()
								&& nextElement.hasEndTag()
						/* && nextElement.matchEndTag(newElement) */) {
							// stop at the matched invalid end tag
							IJSONNode elementChild = nextElement
									.getFirstChild();
							while (elementChild != null) {
								IJSONNode nextChild = elementChild
										.getNextSibling();
								nextElement.removeChild(elementChild);
								newElement.appendChild(elementChild);
								elementChild = nextChild;
							}

							next = nextElement.getNextSibling();
							parent.removeChild(nextElement);
							// newElement.addEndTag(nextElement);
							if (nextElement == newNext)
								startElement = newElement;

							IJSONNode newParent = newElement.getParentNode();
							if (newParent == parent)
								break;
							if (newParent == null
									|| newParent.getNodeType() != IJSONNode.OBJECT_NODE)
								break; // error
							newElement = (JSONObjectImpl) newParent;
							continue;
						}
					}
					// if (!canContain(newElement, next)) {
					// Node newParent = newElement.getParentNode();
					// if (newParent == parent)
					// break;
					// if (newParent == null
					// || newParent.getNodeType() != IJSONNode.OBJECT_NODE)
					// break; // error
					// newElement = (JSONObjectImpl) newParent;
					// continue;
					// }
					IJSONNode child = next;
					next = next.getNextSibling();
					parent.removeChild(child);
					if (child == oldElement) {
						// if (!oldElement.isCommentTag()) {
						// clone (re-create) end tag
						// IJSONObject end = oldElement.removeEndTag();
						// if (end != null) {
						// child = end;
						// newNext = end;
						// }
						// }
					}
					newElement.appendChild(child);
				}
			} else {
				// if (!oldElement.isCommentTag()) {
				// clone (re-create) end tag
				// IJSONObject end = oldElement.removeEndTag();
				// if (end != null) {
				// parent.insertBefore(end, oldElement);
				// parent.removeChild(oldElement);
				// newNext = end;
				// }
				// }
			}
		} else {
			newNext = oldElement.getNextSibling();
			parent.removeChild(oldElement);
		}

		// update context
		IJSONNode oldParent = this.context.getParentNode();
		IJSONNode oldNext = this.context.getNextNode();
		if (element == oldParent) {
			if (oldNext != null) {
				this.context.setCurrentNode(oldNext); // reset for new parent
			} else if (newNext != null) {
				this.context.setCurrentNode(newNext);
			} else {
				this.context.setParentNode(parent);
			}
		} else if (element == oldNext) {
			if (firstElement != null) {
				this.context.setParentNode(firstElement);
			} else if (first != null) {
				this.context.setCurrentNode(first);
			} else if (startElement != null) {
				this.context.setParentNode(startElement);
			} else {
				this.context.setCurrentNode(newNext);
			}
		}

		// removeImplicitElement(elementParent);
	}

	private void removeStructuredDocumentRegion(
			IStructuredDocumentRegion oldStructuredDocumentRegion) {
		JSONNodeImpl node = (JSONNodeImpl) this.context.getCurrentNode();
		if (node != null) {
			short nodeType = node.getNodeType();
			if (nodeType == IJSONNode.OBJECT_NODE
					|| nodeType == IJSONNode.ARRAY_NODE
					|| nodeType == IJSONNode.PAIR_NODE) {
				removeNode(node);
//				if(context.getCurrentNode() != null && context.getCurrentNode().equals(node))
//					context.setCurrentNode(null);
				return;
			}
		}
	}
	
	/**
	 * replaceRegions method
	 * 
	 * @param newRegions
	 *            java.util.Vector
	 * @param oldRegions
	 *            java.util.Vector
	 */
	void replaceRegions(IStructuredDocumentRegion flatNode,
			ITextRegionList newRegions, ITextRegionList oldRegions) {
		if (flatNode == null)
			return;
		if (this.model.getDocument() == null)
			return;
		this.context = new JSONModelContext(this.model.getDocument());

		boolean isWhiteSpaces = false;
		if (newRegions != null
				&& (oldRegions == null || oldRegions.size() == 0)) {
			isWhiteSpaces = true;
			Iterator e = newRegions.iterator();
			while (e.hasNext() && isWhiteSpaces) {
				ITextRegion region = (ITextRegion) e.next();
				String regionType = region.getType();
				isWhiteSpaces = (regionType == JSONRegionContexts.WHITE_SPACE);
			}
			if (isWhiteSpaces) {
				return;
			}
		}
		// optimize typical cases
		String regionType = StructuredDocumentRegionUtil
				.getFirstRegionType(flatNode);
		if (regionType == JSONRegionContexts.JSON_OBJECT_OPEN) {
			changeStartObject(flatNode, newRegions, oldRegions);
		}
		if(regionType == JSONRegionContexts.JSON_OBJECT_KEY) {
			changeAttrName(flatNode, flatNode.getFirstRegion());
		} else if (isJSONValue(regionType)) {
			changeAttrValue(flatNode, flatNode.getFirstRegion());
		} else if(regionType == JSONRegionContexts.JSON_UNKNOWN){
			return;
		}
		// else if (regionType == JSONRegionContexts.JSON_END_TAG_OPEN) {
		// changeEndTag(flatNode, newRegions, oldRegions);
		// }
		else {
			changeStructuredDocumentRegion(flatNode);
		}
	}

	/**
	 * replaceStructuredDocumentRegions method
	 * 
	 */
	void replaceStructuredDocumentRegions(
			IStructuredDocumentRegionList newStructuredDocumentRegions,
			IStructuredDocumentRegionList oldStructuredDocumentRegions) {
		if (this.model.getDocument() == null)
			return;
		this.context = new JSONModelContext(this.model.getDocument());

		int newCount = (newStructuredDocumentRegions != null ? newStructuredDocumentRegions
				.getLength() : 0);
		int oldCount = (oldStructuredDocumentRegions != null ? oldStructuredDocumentRegions
				.getLength() : 0);
		
		int index;
		// Find the first document region from the list that is not a comma
		// region
		for (index = 0; index < oldCount-1; index++) {
			if(!oldStructuredDocumentRegions.item(index).getType().equals(JSONRegionContexts.JSON_COMMA))
				break;
		}
		
		if(oldCount > 0 && !oldStructuredDocumentRegions.item(index).getType().equals(JSONRegionContexts.JSON_COMMA)) {
			// Set up the model context, just ensure the region is not a comma
			setupContext(oldStructuredDocumentRegions.item(index));

			for (int i = index; i < oldCount; i++) {
				IStructuredDocumentRegion documentRegion = oldStructuredDocumentRegions
						.item(i);
				// Remove the old document region
				removeStructuredDocumentRegion(documentRegion);
			}
		} else {
			if (newCount == 0)
				return;
			// In case the oldStructuredDocumentRegions list is empty find the
			// first document region from the list that is not a comma region
			for (index = 0; index < newCount-1; index++) {
				if(!newStructuredDocumentRegions.item(index).getType().equals(JSONRegionContexts.JSON_COMMA))
					break;
			}
			
			if(newCount > 0 && !newStructuredDocumentRegions.item(index).getType().equals(JSONRegionContexts.JSON_COMMA)) {
				setupContext(newStructuredDocumentRegions.item(index));
			}
		}
		// make sure the parent is set to deepest level
		// when end tag has been removed
		this.context.setLast();

		if (newCount > 0) {
			for (int i = 0; i < newCount; i++) {
				IStructuredDocumentRegion documentRegion = newStructuredDocumentRegions
						.item(i);
				// Add each of the new document regions
				insertStructuredDocumentRegion(documentRegion);
			}
		}

		this.context.setCurrentNode(null);
	}

	/**
	 * setupContext method
	 * 
	 */
	private void setupContext(
			IStructuredDocumentRegion startStructuredDocumentRegion) {
		int offset = startStructuredDocumentRegion.getStart();
		if (offset < 0)
			return;
		JSONNodeImpl root = (JSONNodeImpl) this.context.getRootNode();
		if (root == null)
			return;

		if (offset == 0) {
			// at the beginning of document
			IJSONNode child = root.getFirstChild();
			if (child != null)
				this.context.setCurrentNode(child);
			else
				this.context.setParentNode(root);
			return;
		}
		
		// Get the JSON Node at the specified position in the document, this
		// position is the start offset of the flatNode
		JSONNodeImpl node = (JSONNodeImpl) root.getNodeAt(offset);
		if (node == null) {
			// might be at the end of document
			this.context.setParentNode(root);
			this.context.setLast();
			return;
		}
		
		if(node.getStartStructuredDocumentRegion() != null) {
			if(offset == node.getStartStructuredDocumentRegion().getStartOffset()) {
				// This is a JSONPair and the flatNode might be and update of
				// the object key value
				this.context.setCurrentNode(node);
				return;
			} else if (node.getEndStructuredDocumentRegion() != null) {
				if(offset == node.getEndStructuredDocumentRegion().getStartOffset()) {
					// This is a JSONPair and the flatNode might be an update of
					// the pair value
					this.context.setCurrentNode(node);
					return;
				}
			}
		}
		
		if(node instanceof JSONPairImpl && ((JSONPairImpl) node).getValue() != null) {
			if(isJSONValue(startStructuredDocumentRegion.getType())) {
				// This is a JSONPair and the flatNode might be the pair value
				this.context.setCurrentNode(node);
				return;
			}
		}
		
		// In case Node is a JSONObject or JSONArray, look for the current node
		// into the children
		for (IJSONNode child = node.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			if (offset >= ((JSONNodeImpl) child).getEndOffset()) {
				if(child instanceof JSONPairImpl && ((JSONPairImpl) child).getValue() == null) {
					if (isJSONValue(startStructuredDocumentRegion.getType())) {
						this.context.setCurrentNode(child);
						return;
					}
				} else {
					continue;
				}
			}		
			this.context.setCurrentNode(child);
			return;
		}
		
		// The node is tha paret node of the flatNode
		this.context.setParentNode(node);
		this.context.setLast();
	}

	protected JSONModelContext getContext() {
		return context;
	}
	
	/**
	 * Returns true if the region type is a JSONValue
	 * 
	 */
	private boolean isJSONValue(String regionType){
		if(regionType == JSONRegionContexts.JSON_VALUE_STRING
				|| regionType == JSONRegionContexts.JSON_VALUE_BOOLEAN
				|| regionType == JSONRegionContexts.JSON_VALUE_NUMBER
				|| regionType == JSONRegionContexts.JSON_VALUE_NULL) {
			return true;
		}
		return false;
	}

}
