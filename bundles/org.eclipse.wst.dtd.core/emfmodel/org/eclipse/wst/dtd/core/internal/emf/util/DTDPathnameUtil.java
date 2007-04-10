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

package org.eclipse.wst.dtd.core.internal.emf.util;



public class DTDPathnameUtil {
	static public String makePath(String parentPath, String type, String name, int cnt) {
		String pn = null;
		name = encode(name);
		if ((name == null) && (cnt <= 0)) {
			pn = type;
		}
		else if (cnt <= 0) {
			pn = type + ":" + ((name == null) ? "" : name); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else {
			pn = type + ":" + ((name == null) ? "" : name) + ":" + cnt; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		if (parentPath != null) {
			return parentPath + "." + pn; //$NON-NLS-1$
		}
		else {
			return pn;
		}
	}

	static public Object[] parsePathComponent(String path) {
		/*
		 * This routine parse the first component of the path and returns the
		 * result in an arrray of Strings. result[0] = type (String) result[1] =
		 * name (String) result[2] = count (Integer) result[3] = the rest of
		 * the path (String)
		 * 
		 * E.g. PathComponent result[0] result[1] result[2] result[3]
		 * ------------------------------------------------------------- type
		 * type <null> <null> <null> type.rest type <null> <null> rest
		 * type:name type name <null> <null> type:name.rest type name <null>
		 * rest type::n type <null> n <null> type::n.rest type <null> n rest
		 * type:name:n type name n <null> type:name:n.rest type name n rest
		 */

		Object[] result = new Object[4];
		if (path == null)
			return result;

		int i = path.indexOf('.');
		int length = path.length();

		if (i < 0) {
			i = length;
		}

		String type = null;
		String name = null;
		Integer n = null;
		String rest = null;

		// get the type
		int j = path.indexOf(':');
		if (j > i)
			j = -1;
		if (j < 0) {
			type = path.substring(0, i);
		}
		else {
			type = path.substring(0, j);

			// get the name
			int k = path.indexOf(':', j + 1);
			if (k > i)
				k = -1;
			if (k < 0) {
				name = path.substring(j + 1, i);
			}
			else {
				name = path.substring(j + 1, k);

				// get the count
				try {
					n = new Integer(path.substring(k + 1, i));
				}
				catch (Exception exc) {
				}
				if ((n != null) && (n.intValue() < 0)) {
					n = null;
				}
			}
		}

		if ((name != null) && (name.length() == 0)) {
			name = null;
		}

		if (i < length) {
			rest = path.substring(i + 1);
		}

		result[0] = type;
		result[1] = decode(name);
		result[2] = n;
		result[3] = rest;
		return result;
	}

	static private String encode(String s) {
		if (s == null)
			return null;
		StringBuffer o = new StringBuffer(s.length());
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (Character.isLetterOrDigit(c) || (c == '-')) {
				o.append(c);
			}
			else { // if ((c == '.') || (c == ':') || (c == '_') || ...)
				// convert the character to a 4 digit hex code prefixed by "_"
				String hex = Integer.toHexString(c);
				int l = hex.length();
				if (l == 1) {
					o.append("_000"); //$NON-NLS-1$
					o.append(hex);
				}
				else if (l == 2) {
					o.append("_00"); //$NON-NLS-1$
					o.append(hex);
				}
				else if (l == 3) {
					o.append("_0"); //$NON-NLS-1$
					o.append(hex);
				}
				else {
					o.append('_');
					o.append(hex);
				}
			}
		} // for
		return o.toString();
	}

	static private String decode(String s) {
		if (s == null)
			return null;
		StringBuffer o = new StringBuffer(s.length());
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (c != '_') {
				o.append(c);
			}
			else { // next 4 characters are the hex code
				String hex;
				if (len > i + 4) {
					hex = s.substring(i + 1, i + 5);
					i += 4;
				}
				else {
					hex = s.substring(i + 1);
					i = len - 1;
				}
				o.append((char) Integer.parseInt(hex, 16));
			}
		} // for
		return o.toString();
	}

	/**
	 * @generated
	 */
	protected static String makePathGen(String parentPath, String type, String name, int cnt) {

		String pn = null;
		name = encode(name);
		if ((name == null) && (cnt <= 0)) {
			pn = type;
		}
		else if (cnt <= 0) {
			pn = type + ":" + ((name == null) ? "" : name); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else {
			pn = type + ":" + ((name == null) ? "" : name) + ":" + cnt; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		if (parentPath != null) {
			return parentPath + "." + pn; //$NON-NLS-1$
		}
		else {
			return pn;
		}
	}

	/**
	 * @generated
	 */
	protected static Object[] parsePathComponentGen(String path) {

		/*
		 * This routine parse the first component of the path and returns the
		 * result in an arrray of Strings. result[0] = type (String) result[1] =
		 * name (String) result[2] = count (Integer) result[3] = the rest of
		 * the path (String)
		 * 
		 * E.g. PathComponent result[0] result[1] result[2] result[3]
		 * ------------------------------------------------------------- type
		 * type <null> <null> <null> type.rest type <null> <null> rest
		 * type:name type name <null> <null> type:name.rest type name <null>
		 * rest type::n type <null> n <null> type::n.rest type <null> n rest
		 * type:name:n type name n <null> type:name:n.rest type name n rest
		 */

		Object[] result = new Object[4];
		if (path == null)
			return result;

		int i = path.indexOf('.');
		int length = path.length();

		if (i < 0) {
			i = length;
		}

		String type = null;
		String name = null;
		Integer n = null;
		String rest = null;

		// get the type
		int j = path.indexOf(':');
		if (j > i)
			j = -1;
		if (j < 0) {
			type = path.substring(0, i);
		}
		else {
			type = path.substring(0, j);

			// get the name
			int k = path.indexOf(':', j + 1);
			if (k > i)
				k = -1;
			if (k < 0) {
				name = path.substring(j + 1, i);
			}
			else {
				name = path.substring(j + 1, k);

				// get the count
				try {
					n = new Integer(path.substring(k + 1, i));
				}
				catch (Exception exc) {
				}
				if ((n != null) && (n.intValue() < 0)) {
					n = null;
				}
			}
		}

		if ((name != null) && (name.length() == 0)) {
			name = null;
		}

		if (i < length) {
			rest = path.substring(i + 1);
		}

		result[0] = type;
		result[1] = decode(name);
		result[2] = n;
		result[3] = rest;
		return result;
	}

	/**
	 * @generated
	 */
	protected static String encodeGen(String s) {

		if (s == null)
			return null;
		StringBuffer o = new StringBuffer(s.length());
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (Character.isLetterOrDigit(c) || (c == '-')) {
				o.append(c);
			}
			else { // if ((c == '.') || (c == ':') || (c == '_') || ...)
				// convert the character to a 4 digit hex code prefixed by "_"
				String hex = Integer.toHexString(c);
				int l = hex.length();
				if (l == 1) {
					o.append("_000"); //$NON-NLS-1$
					o.append(hex);
				}
				else if (l == 2) {
					o.append("_00"); //$NON-NLS-1$
					o.append(hex);
				}
				else if (l == 3) {
					o.append("_0"); //$NON-NLS-1$
					o.append(hex);
				}
				else {
					o.append('_');
					o.append(hex);
				}
			}
		} // for
		return o.toString();
	}

	/**
	 * @generated
	 */
	protected static String decodeGen(String s) {

		if (s == null)
			return null;
		StringBuffer o = new StringBuffer(s.length());
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (c != '_') {
				o.append(c);
			}
			else { // next 4 characters are the hex code
				String hex;
				if (len > i + 4) {
					hex = s.substring(i + 1, i + 5);
					i += 4;
				}
				else {
					hex = s.substring(i + 1);
					i = len - 1;
				}
				o.append((char) Integer.parseInt(hex, 16));
			}
		} // for
		return o.toString();
	}
}
