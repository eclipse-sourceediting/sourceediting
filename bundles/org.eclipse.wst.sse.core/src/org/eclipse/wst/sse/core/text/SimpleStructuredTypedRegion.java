/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.core.text;



/**
 * Similar jace TypedRegion, but had to subclass our version 
 * which allowed length to be set.
 */
public class SimpleStructuredTypedRegion extends SimpleStructuredRegion implements StructuredTypedRegion {

	/** The region's type */
	private String fType;

	/**
	 * Creates a typed region based on the given specification.
	 *
	 * @param offset the region's offset
	 * @param length the region's length
	 * @param type the region's type
	 */
	public SimpleStructuredTypedRegion(int offset, int length, String type) {
		super(offset, length);
		fType = type;
	}

	/*
	 * @see ITypedRegion#getType()
	 */
	public String getType() {
		return fType;
	}

	public void setType(String type) {
		fType = type;
	}

	/**
	 * Two typed positions are equal if they have the same offset, length, and type.
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
	 * @see Object#hashCode
	 */
	public int hashCode() {
		int type = fType == null ? 0 : fType.hashCode();
		return super.hashCode() | type;
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
