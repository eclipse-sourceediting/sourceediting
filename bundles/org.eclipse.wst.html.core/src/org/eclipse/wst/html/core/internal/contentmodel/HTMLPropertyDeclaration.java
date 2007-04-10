/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.contentmodel;

import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;

/**
 * This interface is commonly used in declaration implementation, and internal use.
 * Use getProperty() method for public use.
 */
public interface HTMLPropertyDeclaration {
	/**
	 * To determin the type, look up the following keywords in C++DOM/DTDParser.cpp:
	 * <ul>
	 *   <li>CORRECT_DUPLICATED - <code>GROUP_NODUP</code></li>
	 *   <li>CORRECT_EMPTY - <code>GROUP_COMPACT</code></li>
	 *   <li>CORRECT_NEUTRAL - <code>GROUP_NEUTRAL</code></li>
	 *   <li>CORRECT_NONE - <code>(N/A)</code></li>
	 * </ul>
	 * @return int
	 */
	int getCorrectionType();
	/**
	 * Get the list of declarations those should be excluded from the content.<br>
	 * @return org.eclipse.wst.xml.core.internal.contentmodel.CMContent
	 */
	CMContent getExclusion();
	/**
	 * To determin the type, see the following files in the C++DOM:
	 *   ElementType.cpp - ElementType#setGroup().
	 *   Element.cpp - Element#getStartTag().
	 * @return int
	 */
	int getFormatType();
	/**
	 * Get the list of declarations those should be included into the content.<br>
	 * @return org.eclipse.wst.xml.core.internal.contentmodel.CMContent
	 */
	CMContent getInclusion();
	/**
	 * To determin the type, look up the following keywords in C++DOM/DTDParser.cpp:
	 * <ul>
	 *   <li>LAYOUT_BLOCK - <code>GROUP_BLOCK</code></li>
	 *   <li>LAYOUT_BREAK - <code>GROUP_BREAK</code></li>
	 *   <li>LAYOUT_HIDDEN - <code>GROUP_HIDDEN</code></li>
	 *   <li>LAYOUT_NONE - <code>(N/A)</code></li>
	 *   <li>LAYOUT_OBJECT - <code>GROUP_NOWRAP</code></li>
	 *   <li>LAYOUT_WRAP - <code>(N/A)</code></li>
	 * </ul>
	 * @return int
	 */
	int getLayoutType();
	/**
	 * To determine the type, see <code>Element::isBreakingBeforeElement()</code>
	 * and <code>Element::isBreakingAfterElement()</code> defined in C++DOM/Element.cpp.<br>
	 * @return int
	 */
	int getLineBreakHint();
	/**
	 * To determine the type, Check the HTML DTD.
	 * And look up <code>GROUP_NOEND</code> in C++DOM/DTDParser.cpp.<br>
	 * @return int
	 */
	int getOmitType();
	/**
	 * @return org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap
	 */
	CMNamedNodeMap getProhibitedAncestors();
	/**
	 * return true when the element is a JSP element.
	 */
	boolean isJSP();
	/**
	 * @return boolean
	 */
	boolean shouldIndentChildSource();
	/**
	 * Some elements should keep spaces in its child text nodes.
	 * For example, PRE and TEXTAREA.  This method returns true,
	 * if a target element is one of such elements.
	 * @return boolean
	 */
	boolean shouldKeepSpaces();
	/**
	 * Returns <code>true</code>, if <code>nextElement</code> terminates
	 * the current element.
	 * Some elements like <code>P</code> terminates other <code>P</code>.
	 * That is, when <code>&lt;P&gt;</code> appears at the next to <code>P</code>,
	 * which end tags is omitted, it represents not only the beginning of the
	 * new <code>P</code> element but also the end of the previous <code>P</code>.
	 */
	boolean shouldTerminateAt(HTMLElementDeclaration nextElement);
}
