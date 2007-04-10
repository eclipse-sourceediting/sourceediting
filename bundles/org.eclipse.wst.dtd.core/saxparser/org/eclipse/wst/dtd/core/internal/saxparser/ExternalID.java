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

public class ExternalID {
	private static final int T_SYSTEM = 0;
	private static final int T_PUBLIC = 1;

	int type = T_PUBLIC;
	String publicID = null;
	String systemID = null;

	/**
	 * Constructor for system IDs.
	 * 
	 * @param systemID
	 *            URI, which may be used to retrieve an external entity's
	 *            content.
	 */
	public ExternalID(String systemID) {
		this.type = T_SYSTEM;
		this.publicID = null;
		this.systemID = systemID;
	}

	/**
	 * Constructor for public and system IDs.
	 * 
	 * @param publicID
	 *            Identifier to be used to try to generate an alternative URI
	 *            in order to retrieve the external entity's content, or
	 *            <var>null</var> if a system identitier is to be
	 *            constructed.
	 * @param systemID
	 *            URI, which may be used to retrieve an external entity's
	 *            content.
	 */
	public ExternalID(String publicID, String systemID) {
		this.type = T_PUBLIC;
		this.publicID = publicID;
		this.systemID = systemID;
		if (null == this.publicID)
			this.type = T_SYSTEM;
	}

	/**
	 * Returns if this external ID is a system ID (or public ID).
	 * 
	 * @return System ID=true, Public ID=false.
	 * @see #isPublic
	 */
	public boolean isSystem() {
		return this.type == T_SYSTEM;
	}

	/**
	 * Returns if this external ID is a public ID (or system ID).
	 * 
	 * @return Public ID=true, System ID=false.
	 * @see #isSystem
	 */
	public boolean isPublic() {
		return this.type == T_PUBLIC;
	}

	/**
	 * Returns the system identifier of this external ID. A system identifier
	 * is a URI, which may be used to retrieve an external entity's content.
	 * 
	 * @return The system identifier, or <var>null</var> if the identifier is
	 *         not defined.
	 * @see #getPubidLiteral
	 */
	public String getSystemLiteral() {
		return this.systemID;
	}

	/**
	 * Returns the public identifier of this external ID. This value is only
	 * valid if the identifier is defined as <var>public</var> (as opposed to
	 * <var>system</var>). Public identifiers may be used to try to generate
	 * an alternative URI in order to retrieve an external entity's content.
	 * If retrieval fails using the public identifier, an attempt must be made
	 * to retrieve content using the system identifier.
	 * 
	 * @return The public identifier, or <var>null</var> if the identifier is
	 *         not defined.
	 * @see #getSystemLiteral
	 */
	public String getPubIdLiteral() {
		return this.publicID;
	}

	/**
	 * Returns this external ID in the format it was declared: <CODE>SYSTEM
	 * &quot;<VAR>systemID</VAR>&quot;</CODE> or <CODE>PUBLIC &quot;<VAR>publicID</VAR>&quot;
	 * &quot;<VAR>systemID</VAR>&quot;</CODE>.
	 * 
	 * @return XML string representing the content of the external ID (never
	 *         <var>null</var>).
	 */
	public String toString() {
		String ret;
		if (isSystem()) {
			ret = "SYSTEM \"" + getSystemLiteral() + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		}
		else if (null != getSystemLiteral()) {
			ret = "PUBLIC \"" + getPubIdLiteral() + "\" \"" + getSystemLiteral() + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		else {
			ret = "PUBLIC \"" + getPubIdLiteral() + "\""; // for NOTATION //$NON-NLS-1$ //$NON-NLS-2$
		}
		return ret;
	}

	/**
	 * 
	 */
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof ExternalID))
			return false;
		ExternalID eid = (ExternalID) obj;
		if (!((eid.publicID == null && this.publicID == null) || eid.publicID != null && eid.publicID.equals(this.publicID)))
			return false;
		if (!((eid.systemID == null && this.systemID == null) || eid.systemID != null && eid.systemID.equals(this.systemID)))
			return false;
		return true;
	}


	/**
	 * 
	 */
	public int hashCode() {
		int retval = 0;
		if (publicID != null)
			retval = publicID.hashCode();
		if (systemID != null)
			retval += systemID.hashCode();
		return retval;
	}

}
