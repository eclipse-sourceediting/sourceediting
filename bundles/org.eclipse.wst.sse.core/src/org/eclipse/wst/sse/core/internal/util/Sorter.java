/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.util;



/**
 * The SortOperation takes a collection of objects and returns a sorted
 * collection of these objects. Concrete instances of this class provide the
 * criteria for the sorting of the objects based on the type of the objects.
 */
public abstract class Sorter {

	/**
	 * Returns true iff elementTwo is 'greater than' elementOne. This is the
	 * 'ordering' method of the sort operation. Each subclass overrides this
	 * method with the particular implementation of the 'greater than' concept
	 * for the objects being sorted. If elementOne and elementTwo are
	 * equivalent in terms of their sorting order, this method must return
	 * 'false'.
	 */
	public abstract boolean compare(Object elementOne, Object elementTwo);

	/**
	 * Sort the objects in the array and return the array.
	 */
	private Object[] quickSort(Object[] array, int left, int right) {
		int originalLeft = left;
		int originalRight = right;
		Object mid = array[(left + right) / 2];

		do {
			while (compare(array[left], mid))
				left++;
			while (compare(mid, array[right]))
				right--;
			if (left <= right) {
				Object tmp = array[left];
				array[left] = array[right];
				array[right] = tmp;
				left++;
				right--;
			}
		} while (left <= right);

		if (originalLeft < right)
			array = quickSort(array, originalLeft, right);
		if (left < originalRight)
			array = quickSort(array, left, originalRight);

		return array;
	}

	/**
	 * Return a new (quick)sorted array from this unsorted array. The original
	 * array is not modified.
	 */
	public Object[] sort(Object[] unSortedCollection) {
		int size = unSortedCollection.length;
		Object[] sortedCollection = new Object[size];

		//copy the array so can return a new sorted collection
		System.arraycopy(unSortedCollection, 0, sortedCollection, 0, size);
		if (size > 1)
			quickSort(sortedCollection, 0, size - 1);

		return sortedCollection;
	}
}
