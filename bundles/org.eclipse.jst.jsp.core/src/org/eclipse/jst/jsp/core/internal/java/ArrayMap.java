/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * This implementation of a hashtable maps keys to arrays
 * to support multiple values being associated with a single
 * key. To remove a specific entry, a key and inserted value must
 * be provided. Removing just based on the key name will remove all
 * values stored under that key
 * 
 */
public class ArrayMap extends Hashtable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ArrayMap(int size) {
		super(size);
	}
	
	public synchronized Object put(Object key, Object value) {
		Object[] values = (Object[]) super.get(key);
		Object[] newValues = null;
		
		if (values == null || values.length == 0)
			newValues = new Object[1];
		else {
			newValues = new Object[values.length + 1];
			System.arraycopy(values, 0, newValues, 0, values.length);
		}

		newValues[newValues.length - 1] = value;
		return super.put(key, newValues);
	}
	
	/**
	 * Removes the first occurrence of <code>value</code> from the list 
	 * of values associated with <code>key</code>
	 * 
	 * @param key the key that has <code>value</code>
	 * @param value the specific value to remove from the key
	 * @return The item removed from the list of values
	 */
	public synchronized Object remove(Object key, Object value) {
		Object[] values = (Object[]) super.get(key);
		Object removed = null;
		Object[] result = null;
		if (values != null && value != null) {
			for (int i = 0; i < values.length; i++) {
				if (value.equals(values[i])) {
					removed = values[i];
					result = new Object[values.length - 1];
					
					if (result.length > 0) {
						// Copy left of value
						System.arraycopy(values, 0, result, 0, i);
						// Copy right of value
						if (i < (values.length - 1))
							System.arraycopy(values, i+1, result, i, result.length - i);
					}
					else
						super.remove(key);
					
					break;
				}
			}
		}
		
		if(result != null && result.length > 0)
			super.put(key, result);
		
		return removed;
	}
	
	public Collection values() {
		Collection valuemaps = super.values();
		Collection values = new ArrayList();
		
		for(Iterator i = valuemaps.iterator(); i.hasNext();)
			values.addAll(Arrays.asList((Object[]) i.next()));
		
		return values;
	}

}
