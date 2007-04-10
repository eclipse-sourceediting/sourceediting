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

package org.eclipse.wst.dtd.core.internal.saxparser;

/**
 * Notations are how the Document Type Description (DTD) records hints about
 * the format of an XML "unparsed entity" -- in other words, non-XML data
 * bound to this document type, which some applications may wish to consult
 * when manipulating the document. A Notation represents a name-value pair,
 * with its nodeName being set to the declared name of the notation.
 */
public class NotationDecl extends BaseNode {
	/** Public identifier. */
	protected String publicId = null;

	/** System identifier. */
	protected String systemId = null;

	//
	// Constructors
	//

	/** Factory constructor. */
	public NotationDecl(String name, String ownerDTD) {
		super(name, ownerDTD);
	}

	//
	// Notation methods
	//

	/**
	 * The Public Identifier for this Notation. If no public identifier was
	 * specified, this will be null.
	 */
	public String getPublicId() {
		return publicId;
	} // getPublicId():String

	/**
	 * The System Identifier for this Notation. If no system identifier was
	 * specified, this will be null.
	 */
	public String getSystemId() {
		return systemId;
	} // getSystemId():String

	//
	// Public methods
	//

	/**
	 * NON-DOM: The Public Identifier for this Notation. If no public
	 * identifier was specified, this will be null.
	 */
	public void setPublicId(String id) {
		publicId = id;
	} // setPublicId(String)

	/**
	 * NON-DOM: The System Identifier for this Notation. If no system
	 * identifier was specified, this will be null.
	 */
	public void setSystemId(String id) {
		systemId = id;
	} // setSystemId(String)

} // class NotationImpl
