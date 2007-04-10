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

public class EntityDecl extends BaseNode {
	boolean isParameter = false;
	String expandedValue = null; // Always <var>null</var> for external
									// Entities
	String value = null; // Always <var>null</var> for external Entities
	ExternalID externalID = null; // Always <var>null</var> for internal
									// Entities
	String ndata = null; // Always <var>null</var> for internal Entities

	boolean parsed = false;
	DTDParser dtd = null; // Always null, except when it has been parsed.

	boolean entityReferenced = false;

	/**
	 * Constructor for internal Entities.
	 * 
	 * @param name
	 *            Name of this Entity.
	 * @param value
	 *            The XML-encoded value that was directly assigned to the
	 *            Entity.
	 * @param isParameter
	 *            =true if a parameter Entity; otherwise =false.
	 */
	public EntityDecl(String name, String ownerDTD, String value, boolean isParameter) {
		super(name, ownerDTD, null, null);
		this.value = value;
		this.isParameter = isParameter;
	}

	/**
	 * Constructor for external Entities.
	 * 
	 * @param name
	 *            Name of the Entity.
	 * @param externalID
	 *            The reference(s) to the external entity to retrieve.
	 * @param isParameter
	 *            =true if a parameter Entity; otherwise =false.
	 * @param ndata
	 *            The notation associated with the binary Entity, or <var>null</var>
	 *            if the Entity is a text Entity.
	 * @see sax.ExternalID
	 */
	public EntityDecl(String name, String ownerDTD, ExternalID externalID, boolean isParameter, String ndata) {
		super(name, ownerDTD, null, null);
		this.externalID = externalID;
		this.isParameter = isParameter;
		this.ndata = ndata;
	}

	/**
	 * Returns whether this Entity is a parameter Entity.
	 * 
	 * @return =true if an internal parameter Entity; otherwise =false.
	 */
	public boolean isParameter() {
		return this.isParameter;
	}

	/**
	 * Returns the value of this Entity.
	 * 
	 * @return The XML-encoded value that was directly assigned to the
	 *         internal Entity; otherwise, <var>null</var>.
	 */
	public String getValue() {
		return this.value;
	}

	public boolean isEntityReferenced() {
		return entityReferenced;
	}

	public void setEntityReferenced(boolean reference) {
		entityReferenced = reference;
	}


	/**
	 * Returns the system identifier of the Notation. A system identifier is a
	 * URI, which may be used to retrieve an external entity's content.
	 * <p>
	 * This method is defined by DOM.
	 * 
	 * @return The system identifier, or <var>null</var> if the identifier is
	 *         not defined.
	 * @see org.eclipcse.wst.dtd.parser.ExternalID#getSystemLiteral
	 */
	public String getSystemId() {
		if (externalID == null)
			return null;
		else
			return this.externalID.getSystemLiteral();
	}

	/**
	 * Sets the system identifier of the Notation. A system identifier is a
	 * URI, which may be used to retrieve an external entity's content.
	 * <p>
	 * This method is defined by DOM.
	 * 
	 * @param systemIdentifier
	 *            The system identifier.
	 * @see org.eclipcse.wst.dtd.parser.ExternalID
	 */
	public void setSystemId(String systemIdentifier) {
		// System.out.println("setSYstemId - externalId: " + externalID);
		this.externalID = new ExternalID(this.externalID.getPubIdLiteral(), systemIdentifier);
	}

	/**
	 * Returns the public identifier of the Notation. This value is only valid
	 * if the identifier is defined as <var>public</var> (as opposed to
	 * <var>system</var>). Public identifiers may be used to try to generate
	 * an alternative URI in order to retrieve the an external entities
	 * content. If retrieval fails using the public identifier, an attempt
	 * must be made to retrieve content using the system identifier.
	 * <p>
	 * This method is defined by DOM.
	 * 
	 * @return The public identifier, or <var>null</var> if the identifier is
	 *         not defined.
	 * @see org.eclipcse.wst.dtd.parser.ExternalID
	 */
	public String getPublicId() {
		if (externalID == null)
			return null;
		else
			return this.externalID.getPubIdLiteral();
	}

	/**
	 * Sets the public identifier of the Notation. This value is only valid if
	 * the identifier is defined as <var>public</var> (as opposed to
	 * <var>system</var>). Public identifiers may be used to try to generate
	 * an alternative URI in order to retrieve the an external entities
	 * content. If retrieval fails using the public identifier, an attempt
	 * must be made to retrieve content using the system identifier.
	 * <p>
	 * This method is defined by DOM.
	 * 
	 * @param publicIdentifier
	 *            The public identifier.
	 * @see #getPublicId
	 * @see org.eclipcse.wst.dtd.parser.ExternalID
	 */
	public void setPublicId(String publicIdentifier) {
		this.externalID = new ExternalID(publicIdentifier, this.externalID.getSystemLiteral());
	}

	/**
	 * Returns the external ID of this Entity.
	 * 
	 * @return The reference(s) to the external entity to retrieve; otherwise,
	 *         <var>null</var>.
	 * @see org.eclipcse.wst.dtd.parser.ExternalID
	 */
	public ExternalID getExternalID() {
		return this.externalID;
	}

	/**
	 * Returns whether this entity value is external.
	 * 
	 * @return =true if entity is external; otherwise, =false.
	 * @see org.eclipcse.wst.dtd.parser.ExternalID
	 */
	public boolean isExternal() {
		return this.externalID != null;
	}

	/**
	 * Returns the notation associated with this Entity.
	 * 
	 * @return The notation associated with the external binary Entity,
	 *         otherwise, <var>null</var>.
	 */
	public String getNotation() {
		return this.ndata;
	}

	public void setNotation(String ndata) {
		this.ndata = ndata;
	}

	/**
	 * Returns the notation associated with this Entity.
	 * <p>
	 * This method is defined by DOM.
	 * 
	 * @return The notation associated with the external binary Entity,
	 *         otherwise, <var>null</var>.
	 */
	public String getNotationName() {
		return this.ndata;
	}

	/**
	 * <p>
	 * This method is defined by DOM.
	 * 
	 * public void setNotationName(String arg) { this.ndata = arg; }
	 */

	/**
	 * Returns whether there is a notation associated with this entity value.
	 * 
	 * @return =true if the external binary entity contains a notation;
	 *         otherwise, =false.
	 */
	public boolean isNotation() {
		return this.ndata != null;
	}

	public void setValue(String s) {
		this.value = s;
	}

	public void setParsed(boolean p) {
		this.parsed = p;
	}

	public boolean getParsed() {
		return this.parsed;
	}

}
