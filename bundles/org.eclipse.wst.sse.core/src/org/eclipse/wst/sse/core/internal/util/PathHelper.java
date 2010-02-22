/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     David Carver (Intalio) - bug 300430 - String concatenation
 *          
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.util;



import java.io.File;
import com.ibm.icu.util.StringTokenizer;

/**
 * Collection of helper methods to manage and convert links Originally part of
 * the LinksManager 
 */
public class PathHelper {
	public static final String BACKWARD_SLASH = "\\";//$NON-NLS-1$

	public static final String FORWARD_SLASH = "/";//$NON-NLS-1$
	public static final String RELATIVE_PATH_SIG = "../";//$NON-NLS-1$

	/**
	 * adjust relative path isside the absolute path
	 */
	public static String adjustPath(String path) {
		int i = 0;
		while ((i = path.indexOf(RELATIVE_PATH_SIG)) > 0) {
			// split the string into two
			String part1 = path.substring(0, i - 1);
			String part2 = path.substring(i + RELATIVE_PATH_SIG.length() - 1);
			// strip one path seg from part1
			int j = part1.lastIndexOf(FORWARD_SLASH);
			if (j == -1) {
				// can't resolve. passed path is like
				// E:/eclipseproject/../../sample.css.
				return "";//$NON-NLS-1$
			}
			part1 = part1.substring(0, j);
			path = part1 + part2;
		}
		return path;
	}

	/**
	 * Append trailing url slash if needed
	 */
	public static String appendTrailingURLSlash(String input) {
		// check to see already a slash
		if (!input.endsWith(FORWARD_SLASH)) {
			input += FORWARD_SLASH;
		}
		return input;
	}

	/**
	 * Convert to relative url based on base
	 */
	public static String convertToRelative(String input, String base) {
		// tokenize the strings
		StringTokenizer inputTokenizer = new StringTokenizer(input, FORWARD_SLASH);
		StringTokenizer baseTokenizer = new StringTokenizer(base, FORWARD_SLASH);
		String token1 = "", token2 = "";//$NON-NLS-2$//$NON-NLS-1$
		//
		// Go through until equls
		while (true) {
			if (!inputTokenizer.hasMoreTokens() || !baseTokenizer.hasMoreTokens())
				break;
			token1 = baseTokenizer.nextToken();
			token2 = inputTokenizer.nextToken();
			if (!token1.equals(token2))
				break;
		}
		// now generate the backs
		String output = "";//$NON-NLS-1$
		StringBuffer sb = new StringBuffer(output);
		while (baseTokenizer.hasMoreTokens()) {
			baseTokenizer.nextToken();
			sb.append("../"); //$NON-NLS-1$
		}
		sb.append(token2);
		// generate the rest
		while (inputTokenizer.hasMoreTokens()) {
			sb.append(FORWARD_SLASH);
			sb.append(inputTokenizer.nextToken());
		}
		output = sb.toString();
		return output;
	}

	/**
	 * Return the containing folder path. Will handle both url and file path
	 */
	public static String getContainingFolderPath(String path) {
		String retValue = path;

		int urlSlashIndex = path.lastIndexOf(FORWARD_SLASH);
		int filePathSlashIndex = path.lastIndexOf(File.separator);
		int index = filePathSlashIndex;
		if (urlSlashIndex > filePathSlashIndex)
			index = urlSlashIndex;
		if (index >= 0)
			retValue = path.substring(0, index);
		return retValue;
	}

	/**
	 * Remove leading path separator
	 */
	public static String removeLeadingPathSeparator(String path) {
		if (path.startsWith(File.separator))
			path = path.substring(File.separator.length());
		return path;
	}

	/**
	 * Remove leading path separator
	 */
	public static String removeLeadingSeparator(String path) {
		if (path.startsWith(File.separator))
			path = path.substring(File.separator.length());
		else if (path.startsWith(FORWARD_SLASH) || path.startsWith(BACKWARD_SLASH))
			path = path.substring(FORWARD_SLASH.length());
		return path;
	}

	/**
	 * Switch to file path slashes
	 */
	public static String switchToFilePathSlashes(String path) {
		path = path.replace(FORWARD_SLASH.charAt(0), File.separatorChar);
		path = path.replace(BACKWARD_SLASH.charAt(0), File.separatorChar);
		return path;
	}

	/**
	 * Switch to file path slashes
	 */
	public static String switchToForwardSlashes(String path) {
		path = path.replace(File.separatorChar, FORWARD_SLASH.charAt(0));
		path = path.replace(BACKWARD_SLASH.charAt(0), FORWARD_SLASH.charAt(0));
		return path;
	}
}
