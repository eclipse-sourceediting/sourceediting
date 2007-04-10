/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.validation;

/**
 * An element references location holds the location of an element reference.
 * An element reference is declared in a DTD element declaration such as
 * <!ELEMENT myelem (elementref)> An element reference may be part of a linked
 * list and can contain a reference to the next element reference in the list.
 * 
 * @author Lawrence Mandel, IBM
 */
class ElementRefLocation {
	private int column = -1;

	private int line = -1;

	private ElementRefLocation next = null;

	private String uri;

	/**
	 * Constructor.
	 * 
	 * @param line
	 *            The line location of the element reference.
	 * @param column
	 *            The column location of the element reference.
	 * @param uri
	 *            The URI of the file containing the element reference.
	 * @param next
	 *            The next element reference in the list.
	 */
	public ElementRefLocation(int line, int column, String uri, ElementRefLocation next) {
		this.line = line;
		this.column = column;
		this.uri = uri;
		this.next = next;
	}

	/**
	 * Get the column location of the element reference.
	 * 
	 * @return The column location of the element reference.
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Get the line location of the element reference.
	 * 
	 * @return The line location of the element reference.
	 */
	public int getLine() {
		return line;
	}

	/**
	 * Get the next element reference in the linked list.
	 * 
	 * @return The next element reference in the linked list.
	 */
	public ElementRefLocation getNext() {
		return next;
	}

	/**
	 * Get the URI of the file that contains the element reference.
	 * 
	 * @return The URI of the file that contains the element reference.
	 */
	public String getURI() {
		return uri;
	}

}
