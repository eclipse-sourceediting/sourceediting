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
package org.eclipse.wst.xml.core.document;



import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.document.InvalidCharacterException;
import org.w3c.dom.Node;

/**
 * A interface to make concept clearer, just to denote the combination of
 * three other interfaces.
 * 
 * @since 1.0
 * 
 */
public interface IDOMNode extends IndexedRegion, INodeNotifier, Node {

	/**
	 * Gets the last structured document region of this node.
	 * 
	 * @return
	 */
	IStructuredDocumentRegion getEndStructuredDocumentRegion();

	/**
	 * Gets the first structured document region of this node.
	 * 
	 * @return
	 */
	IStructuredDocumentRegion getFirstStructuredDocumentRegion();

	/**
	 * Gets the last structured document region of this node.
	 * 
	 * @return
	 */
	IStructuredDocumentRegion getLastStructuredDocumentRegion();

	/**
	 * Returns the model associated with this node.
	 */
	IDOMModel getModel();

	/**
	 * Returns a region representing the name of this node.
	 */
	ITextRegion getNameRegion();

	/**
	 * Returns the source representing this node.
	 */
	String getSource();

	/**
	 * Returns the first StructuredDocumentRegion of this Node.
	 */
	IStructuredDocumentRegion getStartStructuredDocumentRegion();

	/**
	 * Returns the structured Document that underlies this model.
	 */
	IStructuredDocument getStructuredDocument();

	/**
	 * Get's the region representing the value of this node.
	 */
	ITextRegion getValueRegion();

	/**
	 * Get's the source of this nodes value.
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
	 * Returns true if tag is closed.
	 */
	boolean isClosed();

	/**
	 * Returns true if this node is a container.
	 * 
	 * @return boolean
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
	 * Set's readonly state of data
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
}
