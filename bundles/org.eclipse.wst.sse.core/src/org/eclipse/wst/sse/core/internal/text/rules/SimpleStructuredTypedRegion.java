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
package org.eclipse.wst.sse.core.internal.text.rules;




/**
 * Similar jace TypedRegion, but had to subclass our version which allowed
 * length to be set.
 */
public class SimpleStructuredTypedRegion extends SimpleStructuredRegion implements IStructuredTypedRegion {

	/** The region's type */
	private String fType;

	/**
	 * Creates a typed region based on the given specification.
	 * 
	 * @param offset
	 *            the region's offset
	 * @param length
	 *            the region's length
	 * @param type
	 *            the region's type
	 */
	public SimpleStructuredTypedRegion(int offset, int length, String type) {
		super(offset, length);
		fType = type;
	}

	/**
	 * Two typed positions are equal if they have the same offset, length, and
	 * type.
	 * 
	 * @see Object#equals
	 */
	public boolean equals(Object o) {
		if (o instanceof SimpleStructuredTypedRegion) {
			SimpleStructuredTypedRegion r = (SimpleStructuredTypedRegion) o;
			return super.equals(r) && ((fType == null && r.getType() == null) || fType.equals(r.getType()));
		}
		return false;
	}

	/*
	 * @see ITypedRegion#getType()
	 */
	public String getType() {
		return fType;
	}

	/*
	 * @see Object#hashCode
	 */
	public int hashCode() {
		int type = fType == null ? 0 : fType.hashCode();
		return super.hashCode() | type;
	}

	public void setType(String type) {
		fType = type;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append(getOffset());
		s.append(":"); //$NON-NLS-1$
		s.append(getLength());
		s.append(" - "); //$NON-NLS-1$
		s.append(getType());
		return s.toString();
	}

}
