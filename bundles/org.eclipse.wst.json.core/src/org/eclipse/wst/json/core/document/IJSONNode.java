/**
 *  Copyright (c) 2013-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core.document;

import org.eclipse.json.jsonpath.IJSONPath;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;

/**
 * The JSON node API.
 *
 */
public interface IJSONNode extends IndexedRegion, INodeNotifier {

	short DOCUMENT_NODE = -1;
	short OBJECT_NODE = 0;
	short ARRAY_NODE = 1;
	short PAIR_NODE = 2;

	short VALUE_STRING_NODE = 3;
	short VALUE_NUMBER_NODE = 4;
	short VALUE_BOOLEAN_NODE = 5;
	short VALUE_NULL_NODE = 6;

	short OBJECT_KEY_NODE = 7;

	/**
	 * Returns the structured document that underlies this node's model.
	 * 
	 * Returns null if this node is not actively part of a source document. In
	 * contrast, in the pure DOM world, "owning document" is not null even after
	 * a node is deleted from the DOM.
	 * 
	 * ISSUE: we need to fix our implementation to match this spec.
	 * 
	 * @return the structured document.
	 */
	IStructuredDocument getStructuredDocument();

	/**
	 * Gets the last structured document region of this node.
	 * 
	 * ISSUE: need to resolve getEnd/getLast confusion.
	 * 
	 * @return IStructuredDocumentRegion - returns the last structured document
	 *         region associated with
	 */
	IStructuredDocumentRegion getEndStructuredDocumentRegion();

	/**
	 * Gets the first structured document region of this node.
	 * 
	 * ISSUE: need to resolve getFirst/getStart confusion
	 * 
	 * @return the first structured document region of this node.
	 */
	IStructuredDocumentRegion getFirstStructuredDocumentRegion();

	/**
	 * Gets the last structured document region of this node.
	 * 
	 * ISSUE: need to resolve getEnd/getLast confusion.
	 * 
	 * @return IStructuredDocumentRegion - returns the last structured document
	 *         region associated with
	 */
	IStructuredDocumentRegion getLastStructuredDocumentRegion();

	String getNodeName();

	String getNodeValue() throws JSONException;

	void setNodeValue(String paramString) throws JSONException;

	IJSONNode removeChild(IJSONNode oldChild) throws JSONException;

	IJSONNode cloneNode(boolean deep);

	IJSONDocument getOwnerDocument();

	IJSONNode getFirstChild();

	IJSONNode getLastChild();

	IJSONNode getPreviousSibling();

	IJSONNode getNextSibling();

	IJSONNode getParentNode();

	short getNodeType();

	IStructuredDocumentRegion getStartStructuredDocumentRegion();

	IJSONNode insertBefore(IJSONNode node, IJSONNode next) throws JSONException;

	/**
	 * Returns the model associated with this node. Returns null if not part of
	 * an active model.
	 * 
	 * @return IDOMModel - returns the IDOMModel this node is part of.
	 */
	IJSONModel getModel();

	boolean hasChildNodes();

	IJSONPair getOwnerPairNode();

	IJSONNode getParentOrPairNode();

	IJSONPath getPath();
}
