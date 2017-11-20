/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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



import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

/**
 * This interface provides extensions to corresponding DOM interface to enable
 * functions for source editing and incremental parsing.
 * 
 * @plannedfor 1.0
 * 
 */
public interface IDOMText extends IDOMNode, Text {

	/**
	 * NOT API - can be eliminated or moved to ltk level
	 * 
	 * Appends the content of the text node
	 * 
	 * @param text -
	 *            the Text to append.
	 */
	void appendText(Text text);

	/**
	 * NOT API - can be eliminated or moved to ltk level
	 * 
	 * Returns true if is not valid.
	 */
	boolean isInvalid();

	/**
	 * Returns true if is entirely white space.
	 * 
	 * This is intened to be better performing that all clients getting the
	 * source, and checking themselves.
	 * 
	 * ISSUE: need to clarify if implementation is pure to "white space" as
	 * per DOM spec? Here is the DOM spec:
	 * 
	 * Returns whether this text node contains <a
	 * href='http://www.w3.org/TR/2004/REC-xml-infoset-20040204#infoitem.character'>
	 * element content whitespace</a>, often abusively called "ignorable
	 * whitespace". The text node is determined to contain whitespace in
	 * element content during the load of the document or if validation occurs
	 * while using <code>Document.normalizeDocument()</code>.
	 * 
	 * @see DOM Level 3
	 * 
	 * @return true if is entirely white space.
	 */

	public boolean isElementContentWhitespace();

	/* (non-Javadoc)
	 * @see org.w3c.dom.Text#getWholeText()
	 */
	public String getWholeText();

	/**
	 * NOT YET IMPLEMENTED but exists here interface in preparation for DOM3
	 * 
	 * Replaces the text of the current node and all logically-adjacent text
	 * nodes with the specified text. All logically-adjacent text nodes are
	 * removed including the current node unless it was the recipient of the
	 * replacement text. <br>
	 * This method returns the node which received the replacement text. The
	 * returned node is:
	 * <ul>
	 * <li><code>null</code>, when the replacement text is the empty
	 * string; </li>
	 * <li>the current node, except when the current node is read-only; </li>
	 * <li> a new <code>Text</code> node of the same type (
	 * <code>Text</code> or <code>CDATASection</code>) as the current
	 * node inserted at the location of the replacement. </li>
	 * </ul>
	 * <br>
	 * For instance, in the above example calling
	 * <code>replaceWholeText</code> on the <code>Text</code> node that
	 * contains "bar" with "yo" in argument results in the following: <br>
	 * Where the nodes to be removed are read-only descendants of an
	 * <code>EntityReference</code>, the <code>EntityReference</code>
	 * must be removed instead of the read-only nodes. If any
	 * <code>EntityReference</code> to be removed has descendants that are
	 * not <code>EntityReference</code>, <code>Text</code>, or
	 * <code>CDATASection</code> nodes, the <code>replaceWholeText</code>
	 * method must fail before performing any modification of the document,
	 * raising a <code>DOMException</code> with the code
	 * <code>NO_MODIFICATION_ALLOWED_ERR</code>. <br>
	 * For instance, in the example below calling
	 * <code>replaceWholeText</code> on the <code>Text</code> node that
	 * contains "bar" fails, because the <code>EntityReference</code> node
	 * "ent" contains an <code>Element</code> node which cannot be removed.
	 * 
	 * @param content
	 *            The content of the replacing <code>Text</code> node.
	 * @return The <code>Text</code> node created with the specified
	 *         content.
	 * @exception DOMException
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised if one of the
	 *                <code>Text</code> nodes being replaced is readonly.
	 * @see DOM Level 3
	 */
	public Text replaceWholeText(String content) throws DOMException;

}
