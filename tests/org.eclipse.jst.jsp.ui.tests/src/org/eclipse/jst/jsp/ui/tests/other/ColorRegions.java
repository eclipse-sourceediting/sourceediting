/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.tests.other;

/**
 * Insert the type's description here.
 * Creation date: (7/21/00 4:14:40 PM)
 * @author: David Williams
 */
public class ColorRegions extends java.util.Vector {
	/**
	 * Default <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * ColorRegions constructor comment.
	 */
	public ColorRegions() {
		super();
	}

	/**
	 * ColorRegions constructor comment.
	 * @param initialCapacity int
	 */
	public ColorRegions(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * ColorRegions constructor comment.
	 * @param initialCapacity int
	 * @param capacityIncrement int
	 */
	public ColorRegions(int initialCapacity, int capacityIncrement) {
		super(initialCapacity, capacityIncrement);
	}

	public int[] asIntArray() {
		trimToSize();
		int arraySize = elementData.length;
		int[] results = new int[arraySize];
		//
		for (int i = 0; i < arraySize; i++) {
			results[i] = ((Integer) elementData[i]).intValue();
		}
		//
		return results;
	}
}