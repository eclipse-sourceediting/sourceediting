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
	 * @return com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 */
	IStructuredDocumentRegion getEndStructuredDocumentRegion();

	/**
	 */
	IStructuredDocumentRegion getFirstStructuredDocumentRegion();

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
	 * @return com.ibm.sed.structuredDocument.IStructuredDocumentRegion
	 */
	IStructuredDocumentRegion getStartStructuredDocumentRegion();

	/**
	 * 
	 * @return com.ibm.sed.structuredDocument.IStructuredDocument
	 */
	IStructuredDocument getStructuredDocument();

	/**
	 */
	ITextRegion getValueRegion();

	/**
	 */
	String getValueSource();

	/**
	 * 
	 * @return boolean Whether children of the element can be appended or
	 *         removed.
	 */
	boolean isChildEditable();

	/**
	 */
	boolean isClosed();

	/**
	 * isContainer method
	 * 
	 * @return boolean
	 */
	boolean isContainer();

	/**
	 */
	boolean isDataEditable();

	/**
	 */
	void setChildEditable(boolean editable);

	/**
	 */
	void setDataEditable(boolean editable);

	/**
	 * faster approach to set
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
