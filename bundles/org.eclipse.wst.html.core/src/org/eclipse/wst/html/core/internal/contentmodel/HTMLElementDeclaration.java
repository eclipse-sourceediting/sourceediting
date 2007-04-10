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



import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;

/**
 * This interface is intended to be a public interface which has
 * interfaces defined in both of {@link <code>CMElementDeclaration</code>}
 * and {@link <code>HTMLCMNode</code>}.<br>
 * In addition to this, some interfaces are added to this interface,
 * those are specially to HTML elements.<br>
 */
public interface HTMLElementDeclaration extends CMElementDeclaration {

	/** Tag ommission; Not ommisible. */
	int OMIT_NONE = 0;
	/** Tag ommission; Both tags are ommisible. */
	int OMIT_BOTH = 1;
	/** Tag ommission; The end tag is ommisible. */
	int OMIT_END = 2;
	/** Tag ommission; The end tag is ommitted when created. */
	int OMIT_END_DEFAULT = 3;
	/** Tag ommission; The end tag must be omitted. */
	int OMIT_END_MUST = 4;
	/** Line Break; No break. */
	int BREAK_NONE = 10;
	/** Line Break; Break after the start tag. */
	int BREAK_AFTER_START = 11;
	/** Line Break; Break both before the start tagn and after the end tag. */
	int BREAK_BEFORE_START_AND_AFTER_END = 12;
	/* Layout */
	/** initial value; the value should never returns to client programs. */
	int LAYOUT_NONE = 100;
	int LAYOUT_BLOCK = 101;
	int LAYOUT_WRAP = 102;
	/** No wrap object; like IMG, APPLET,... */
	int LAYOUT_OBJECT = 103;
	/** BR */
	int LAYOUT_BREAK = 104;
	/** Hidden object; like HTML or HEAD */
	int LAYOUT_HIDDEN = 105;
	/* Correction */
	/** Correct; No correct. */
	int CORRECT_NONE = 1000;
	/** Correct; Meaningless when the content is empty. */
	int CORRECT_EMPTY = 1001;
	/** Correct; Meaningless when no attribut is set. */
	int CORRECT_NEUTRAL = 1002;
	/** Correct; Meaningless when same element is nested. */
	int CORRECT_DUPLICATED = 1003;
	/** Format; HTML */
	int FORMAT_HTML = 10000;
	/** Format; SSI */
	int FORMAT_SSI = 10001;
	/** Format; JSP script */
	int FORMAT_JSP_SCRIPT = 10002;
	/** Format; JSP directive */
	int FORMAT_JSP_DIRECTIVE = 10003;
	/** Format; XML */
	int FORMAT_XML = 10004;
	/** Format; MW */
	int FORMAT_MW = 10005;

	/**
	 * A short hand method to get an attribute declaration of a HTML element.
	 * @param attrName java.lang.String
	 */
	HTMLAttributeDeclaration getAttributeDeclaration(String attrName);
}
