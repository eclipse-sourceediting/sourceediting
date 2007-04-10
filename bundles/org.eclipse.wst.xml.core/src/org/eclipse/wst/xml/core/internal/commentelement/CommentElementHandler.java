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
package org.eclipse.wst.xml.core.internal.commentelement;



import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 */
public interface CommentElementHandler {
	/**
	 * This method is called when the prefix of the comment content matches
	 * the string specified in &lt;startswith prefix=""/&gt; in plugin
	 * extension. Comment content is parsed and new DOM element is created in
	 * this method. Implementor has to do following:
	 * <li>For start tag :
	 * <ul>
	 * <li>parse comment content and create new element instance.</li>
	 * </ul>
	 * </li>
	 * <li>For end tag :
	 * <ul>
	 * <li>parse comment content and create new element instance.</li>
	 * <li>make isEndTag flag true.</li>
	 * <li>Parser framework searches mached start tag element instance after
	 * this createElement call, and new instance is just thrown away.</li>
	 * </ul>
	 * </li>
	 * <li>For empty tag :
	 * <ul>
	 * <li>parse comment content and create new element instance.</li>
	 * <li>make isEndTag flag true.</li>
	 * </ul>
	 * </li>
	 * 
	 * @param document
	 *            parent DOM document
	 * @param data
	 *            comment content. comment prefix (&lt;!-- or &lt;%--), suffix
	 *            (--&gt; or --%&gt;), and surrounding spaces are trimmed.
	 * @param isJSPTag
	 *            true if the comment is JSP style comment. This information
	 *            may be required by handler when the handler accepts both XML
	 *            style and JSP style comment (namely,
	 *            commenttype=&quot;both&quot; in plugin.xml).
	 * @return comment element instance if the comment content is rightly
	 *         parsed. if parse failed, returns null.
	 */
	Element createElement(Document document, String data, boolean isJSPTag);

	/**
	 * This method generates the source text of the end tag for the passed
	 * element. Do not generate comment prefix (&lt;!-- or &lt;%--) and suffix
	 * (--&gt; or --%&gt;). XMLGenerator uses this method to generate XML/HTML
	 * source for a comment element.
	 * 
	 * @param element
	 *            the comment element
	 * @return generated tag string
	 */
	String generateEndTagContent(IDOMElement element);

	/**
	 * This method generates the source text of the start tag for the passed
	 * element. Do not generate comment prefix (&lt;!-- or &lt;%--) and suffix
	 * (--&gt; or --%&gt;). XMLGenerator uses this method to generate XML/HTML
	 * source for a comment element.
	 * 
	 * @param element
	 *            the comment element
	 * @return generated tag string
	 */
	String generateStartTagContent(IDOMElement element);

	/**
	 * 
	 * @param element
	 *            the element
	 * @return boolean whether the element is comment element or not
	 */
	boolean isCommentElement(IDOMElement element);

	/**
	 * 
	 * @return boolean whether this element can have children or not
	 */
	boolean isEmpty();

	/**
	 * @return String
	 */
	//	String getElementPrefix();
}
