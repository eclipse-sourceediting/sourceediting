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
package org.eclipse.wst.xml.core.document;



import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;

/**
 * A interface to make concept clearer, just to denote the combination of
 * three other interfaces.
 *
 */
public interface XMLNode extends org.eclipse.wst.sse.core.IndexedRegion, org.eclipse.wst.sse.core.INodeNotifier, org.w3c.dom.Node {

	/**
	 * 
	 */
	IStructuredDocumentRegion getEndStructuredDocumentRegion();

	/**
	 */
	IStructuredDocumentRegion getFirstStructuredDocumentRegion();

	/**
	 * 
	 */
	IStructuredDocument getStructuredDocument();

	/**
	 */
	IStructuredDocumentRegion getLastStructuredDocumentRegion();

	/**
	 */
	XMLModel getModel();

	/**
	 */
	ITextRegion getNameRegion();

	/**
	 */
	String getSource();

	/**
	 * 
	 */
	IStructuredDocumentRegion getStartStructuredDocumentRegion();

	/**
	 */
	ITextRegion getValueRegion();

	/**
	 */
	String getValueSource();

	/**
	 */
	boolean isClosed();

	/**
	 * isContainer method
	 * @return boolean
	 */
	boolean isContainer();

	/**
	 * Sets the specified raw source to the Text node.
	 * Throws InvalidCharacterException when the specified raw source includes
	 * invalid characters, such as, '<', '>' and '&'.
	 * Valid character entities, such as, "&lt;", are accepted.
	 */
	void setSource(String source) throws InvalidCharacterException;

	/**
	 * Sets the specified raw source to the Text or Attr node's value.
	 * When the specified raw source includes invalid characters, such as,
	 * '<', '>' and '&', converts them.
	 * Valid character entities, such as, "&lt;", are accepted.
	 */
	void setValueSource(String source);

	/**
	 * 
	 * @return boolean Whether children of the element can be appended or removed. 
	 */
	boolean isChildEditable();

	/**
	 */
	void setChildEditable(boolean editable);

	/**
	 */
	boolean isDataEditable();

	/**
	 */
	void setDataEditable(boolean editable);

	/**
	 * faster approach to set 
	 */
	void setEditable(boolean editable, boolean deep);
}
