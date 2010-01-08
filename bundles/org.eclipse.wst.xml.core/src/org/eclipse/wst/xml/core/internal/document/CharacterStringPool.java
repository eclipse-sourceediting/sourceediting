/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.document;

import java.util.LinkedHashMap;

/**
 * Organizes a pool of frequently used Strings as character arrays.
 * 
 */
final class CharacterStringPool {

	static private LinkedHashMap fPool = new LimitedHashMap();

	static private class LimitedHashMap extends LinkedHashMap {
		private static final long serialVersionUID = 1L;
		private static final int MAX = 500;

		public LimitedHashMap() {
			super(MAX / 10, .75f, true);
		}
		protected boolean removeEldestEntry(java.util.Map.Entry eldest) {
			return size() > MAX;
		}
	}

	static private class CharArray {
		char[] fArray;
		/**
		 * Answers a hashcode for the array. Algorithm from org.eclipse.jdt.core.compiler.CharOperation
		 *
		 * @param array the array for which a hashcode is required
		 * @return the hashcode
		 * @throws NullPointerException if array is null
		 */
		public int hashCode() {
			int length = fArray.length;
			int hash = length == 0 ? 31 : fArray[0];
			if (length < 8) {
				for (int i = length; --i > 0;)
					hash = (hash * 31) + fArray[i];
			} else {
				// 8 characters is enough to compute a decent hash code, don't waste time examining every character
				for (int i = length - 1, last = i > 16 ? i - 16 : 0; i > last; i -= 2)
					hash = (hash * 31) + fArray[i];
			}
			return hash & 0x7FFFFFFF;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof CharArray))
				return false;
			CharArray other = (CharArray) obj;
			if (fArray.length != other.fArray.length)
				return false;
			for (int i = 0; i < fArray.length; i++) {
				if (fArray[i] != other.fArray[i])
					return false;
			}
			return true;
		}
	}

	private CharacterStringPool() {
	}

	/**
	 * Returns the character array for <code>string</code>. If
	 * the character array is already in the pool for character arrays,
	 * the array is reused.
	 * 
	 * @param string the string to retrieve the character array for
	 * @return a pooled instance of the character array
	 */
	public static char[] getCharString(String string) {
		CharArray array = new CharArray();
		array.fArray = string.toCharArray();

		Object obj = null;
		synchronized (fPool) {
			obj = fPool.get(array);
			if (obj == null) {
				obj = array;
				fPool.put(obj, obj);
			}
		}
		return ((CharArray) obj).fArray;
	}

}
