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
package org.eclipse.wst.css.core.internal.encoding;



public class ByteUtil {


	public static void main(String[] args) {
		String bigString = "\n<@ page\n"; //$NON-NLS-1$
		String target = "<@ PAGE"; //$NON-NLS-1$
		int match = findIfMatch(stringToByteArray(bigString), 7, 0, stringToByteArray(target));
		System.out.println(match);
	}

	/**
	 * ByteUtil constructor comment.
	 */
	public ByteUtil() {
		super();
	}

	/**
	 * @return int
	 * @param s1
	 *            byte[]
	 * @param s2
	 *            byte[]
	 * @param n
	 *            int
	 * 
	 * Compare bytes. If same, return 0. Otherwise non-zero.
	 */
	private static int _cmp(byte[] s1, int starts1pos, byte[] s2, int starts2pos, int complen, boolean ignorecase) {
		if ((s1 == null) || (s2 == null))
			return 1;
		int len_s1 = s1.length - starts1pos;
		int len_s2 = s2.length - starts2pos;
		if ((len_s1 <= 0) || (len_s2 <= 0))
			return 1;
		if ((len_s1 < complen) || (len_s2 < complen))
			return 1;

		int len = complen;
		int offset = 0;
		byte c1 = (byte) 0, c2 = (byte) 0;
		while (offset < len) {
			if (ignorecase == true) {
				c1 = _toUpper(s1[starts1pos + offset]);
				c2 = _toUpper(s2[starts2pos + offset]);
			}
			else {
				c1 = s1[starts1pos + offset];
				c2 = s2[starts2pos + offset];
			}
			if (c1 == c2) {
				offset++;
				continue;
			}
			else {
				break;
			}
		}

		return (offset == len ? 0 : (int) (c1 - c2));
	}

	/**
	 * @return byte
	 * @param c1
	 *            byte
	 * 
	 * Convert to upper case letter. It's for ASCII only
	 */
	private static byte _toUpper(byte c1) {
		if ((c1 >= (byte) 0x61) && (c1 <= (byte) 0x7a))
			c1 -= 0x20;
		return c1;
	}

	/**
	 * @return int
	 * @param string
	 *            byte[]
	 * @param target
	 *            byte[]
	 * 
	 * Look for the index of 2nd byte[] within 1st byte[]. If being found,
	 * return the pos. Otherwise, return -1. This is case-insensitive. It's
	 * simlar to String.indexOf().
	 */
	public static int findIfMatch(byte[] container, int length, int startpos, byte[] target) {
		int container_len = length - startpos;
		int target_len = target.length;

		// Clearly different
		if ((container_len <= 0) || (target_len <= 0))
			return -1;
		if (target_len > container_len)
			return -1;

		// Look for the same bytes as 'target' in 'container' from startpos
		int pos;
		for (pos = startpos; pos < length; pos++) {
			if (strCaseCmp(container, pos, target, 0, target_len) == 0) {
				// matched.
				return pos;
			}
		}

		return -1;
	}

	/**
	 * Let the conversion be done in code since it's easier to reuse and
	 * verify than converting them by hand.
	 */
	private static byte[] stringToByteArray(String string) {
		byte[] bytes = new byte[string.length()];
		for (int i = 0; i < string.length(); i++)
			bytes[i] = (byte) string.charAt(i); // typecast to number using
												// lower 8 bits only
		return bytes;
	}

	/**
	 * @return int
	 * @param container
	 *            byte[]
	 * @param startpos
	 *            int
	 * @param target
	 *            byte
	 * 
	 * This is case-insensitive.
	 */
	public static int findIfMatch(byte[] container, int length, int startpos, byte target) {
		int container_len = length - startpos;

		if (container_len <= 0)
			return -1;

		// Look for the same byte as 'target' in 'container' from startpos
		int pos;
		for (pos = startpos; pos < length; pos++) {
			if (_toUpper(container[pos]) == _toUpper(target))
				return pos;
		}

		return -1;
	}

	/**
	 * @return int
	 * @param container
	 *            byte[]
	 * @param length
	 *            int
	 * @param startpos
	 *            int
	 * @param target
	 *            byte
	 * 
	 * Skip same bytes as target. Return the position. If goes to end, return
	 * -1
	 */
	public static int skipIfMatch(byte[] container, int length, int startpos, byte target) {
		int container_len = length - startpos;

		// Clearly error
		if (container_len <= 0)
			return -1;

		int pos;
		for (pos = startpos; pos < length; pos++) {
			if (container[pos] != target)
				break;
		}
		if (pos == length) {
			// I found everything is target
			pos = -1;
		}
		return pos;
	}

	/**
	 * @return int
	 * @param s1
	 *            byte[]
	 * @param starts1pos
	 *            int
	 * @param s2
	 *            byte[]
	 * @param starts2pos
	 *            int
	 * 
	 * It's similar to XPG4's strcasecmp()
	 */
	public static int strCaseCmp(byte[] s1, int starts1pos, byte[] s2, int starts2pos, int complen) {
		return _cmp(s1, starts1pos, s2, starts2pos, complen, true);
	}

	/**
	 * @return int
	 * @param s1
	 *            byte[]
	 * @param starts1pos
	 *            int
	 * @param s2
	 *            byte[]
	 * @param starts2pos
	 *            int
	 * 
	 * It's similar to XPG4's strcmp()
	 */
	public static int strCmp(byte[] s1, int starts1pos, byte[] s2, int starts2pos, int complen) {
		return _cmp(s1, starts1pos, s2, starts2pos, complen, false);
	}
}