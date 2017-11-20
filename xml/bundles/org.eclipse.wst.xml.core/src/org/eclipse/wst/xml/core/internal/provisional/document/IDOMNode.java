/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.provisional.document;



import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.document.InvalidCharacterException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

/**
 * This interface describes the extended functionality of our source-oriented
 * DOM. First, our nodes extend the w3c Node interface, IndexedRegion, and
 * INodeNotifier. Plus, has the extra methods called out here.
 * 
 * ISSUE: the 'read-only' API should be broken out in their own interface
 * 
 * @plannedfor 1.0
 * 
 */
public interface IDOMNode extends IndexedRegion, INodeNotifier, Node {

	/**
	 * Gets the last structured document region of this node.
	 * 
	 * ISSUE: need to resolve getEnd/getLast confusion.
	 * 
	 * @return IStructuredDocumentRegion - returns the last structured
	 *         document region associated with
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
	 * @return IStructuredDocumentRegion - returns the last structured
	 *         document region associated with
	 */
	IStructuredDocumentRegion getLastStructuredDocumentRegion();

	/**
	 * Returns the model associated with this node. Returns null if not part
	 * of an active model.
	 * 
	 * @return IDOMModel - returns the IDOMModel this node is part of.
	 */
	IDOMModel getModel();

	/**
	 * Get's the region representing the name of this node
	 * 
	 * ISSUE: only implemented/used at attribute and DTDNodes -- should move.
	 * 
	 * @return ITextRegion - returns the ITextRegion associated with this
	 *         Node.
	 * 
	 * @deprecated
	 */
	ITextRegion getNameRegion();

	/**
	 * Returns the literal source representing this node in source document.
	 * 
	 * ISSUE: need to fix implementation to match.
	 * 
	 * @return the literal source representing this node in source document.
	 */
	String getSource();

	/**
	 * Gets the first structured document region of this node.
	 * 
	 * ISSUE: need to resolve getFirst/getStart confusion
	 * 
	 * @return the first structured document region of this node.
	 */
	IStructuredDocumentRegion getStartStructuredDocumentRegion();

	/**
	 * Returns the structured document that underlies this node's model.
	 * 
	 * Returns null if this node is not actively part of a source document. In
	 * contrast, in the pure DOM world, "owning document" is not null even
	 * after a node is deleted from the DOM.
	 * 
	 * ISSUE: we need to fix our implementation to match this spec.
	 * 
	 * @return the structured document.
	 */
	IStructuredDocument getStructuredDocument();

	/**
	 * Get's the region representing the value of this node if only one
	 * ITextRegion, null otherwise.
	 * 
	 * ISSUE: only implemented/used at attribute level, move "down".
	 * 
	 * @return ITextRegion - returns the ITextRegion associated with this
	 *         Node.
	 * 
	 * @deprecated
	 */
	ITextRegion getValueRegion();

	/**
	 * Returns a string representing the source of this node, but with
	 * character enties converted (e.g. &lt; is converted to '<').
	 * 
	 * ISSUE: need to better spec extent of this conversion, we may not know
	 * all character entities.
	 * 
	 * ISSUE: need to fix implementation to match spec.
	 * 
	 * @return String - get's the source of this Node.
	 */
	String getValueSource();

	/**
	 * Used to know read-only state of children.
	 * 
	 * @return boolean Whether children of the element can be appended or
	 *         removed.
	 */
	boolean isChildEditable();

	/**
	 * Returns true if tag is closed in source.
	 * 
	 * In our source orient DOM we sometimes end a Node without it being
	 * explicitly closed in source.
	 * 
	 * @return boolean - true if node is closed
	 */
	boolean isClosed();

	/**
	 * Returns true if this node can contain children.
	 * 
	 * @return boolean - true if this node can contain children.
	 */
	boolean isContainer();

	/**
	 * Used to know read-only state of data.
	 * 
	 */
	boolean isDataEditable();

	/**
	 * Set's readonly state of children
	 * 
	 */
	void setChildEditable(boolean editable);

	/**
	 * Set's readonly state of data
	 * 
	 */
	void setDataEditable(boolean editable);

	/**
	 * Sets readonly state of data
	 * 
	 * faster approach to set read-only state.
	 */
	void setEditable(boolean editable, boolean deep);

	/**
	 * Sets the specified raw source to the Text node. Throws
	 * InvalidCharacterException when the specified raw source includes
	 * invalid characters, such as, ' <', '>' and '&'. Valid character
	 * entities, such as, "&amp;lt;", are accepted.
	 */
	void setSource(String source) throws InvalidCharacterException;

	/**
	 * Sets the specified raw source to the Text or Attr node's value. When
	 * the specified raw source includes invalid characters, such as, ' <',
	 * '>' and '&', converts them. Valid character entities, such as,
	 * "&amp;lt;", are accepted.
	 */
	void setValueSource(String source);
	
	

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public short compareDocumentPosition(Node other) throws DOMException;

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public String getBaseURI();

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public Object getFeature(String feature, String version);

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public String getTextContent() throws DOMException;

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public Object getUserData(String key);

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public boolean isDefaultNamespace(String namespaceURI);

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public boolean isEqualNode(Node arg);
	
	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public boolean isSameNode(Node other);

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public String lookupNamespaceURI(String prefix);

	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public String lookupPrefix(String namespaceURI);
	
	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public void setTextContent(String textContent) throws DOMException;
	
	
	/**
	 * NOT IMPLEMENTED, is defined here in preparation of DOM Level 3
	 */
	public boolean isId();
}
