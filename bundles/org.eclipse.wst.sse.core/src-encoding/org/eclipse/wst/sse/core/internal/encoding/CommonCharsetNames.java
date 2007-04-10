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
package org.eclipse.wst.sse.core.internal.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.internal.encoding.util.Logger;
import org.osgi.framework.Bundle;

/**
 * CommonCharsets is a utility class to provide a central place to map some
 * IANA charset name to a Java charset name. In 1.4 JRE's this mostly is a
 * thin wrapper of existing Charset functionality. It does, however, allow
 * some "overriding" of the built in JRE mappings in the event they are
 * necessary. See CodedIO.checkMappingOverrides.
 * <p>
 * This class also provides some convenient human readable description for the
 * charset names which can be used in user interfaces. The description is NL
 * aware based on locale. The data is populated via the charset.properties
 * file only once, based on static initialization of the hashtables.
 * <p>
 * The IANA tags are based on reference information found at the
 * http://www.iana.org site. Specifically see
 * http://www.iana.org/assignments/character-sets
 */
public final class CommonCharsetNames {

	private static Properties defaultIANAmappings = null;

	private static ArrayList encodings = null;

	private static Hashtable supportedEncodingDisplayNames = null;

	/**
	 * Returns list of commonly available encoding names. Suitable for
	 * populating a UI dialog or drop down. This list would be a subset of all
	 * possible charsets the VM supports (which can get into the hundreds).
	 * For the VM supported charsets, use
	 * <code>Charset.availableCharsets()</code>
	 * 
	 * @return String[]
	 */
	public static String[] getCommonCharsetNames() {
		String[] enc = new String[getEncodings().size()];
		getEncodings().toArray(enc);
		return enc;
	}

	/**
	 * @return Returns the defaultIANAmappings.
	 */
	private static Properties getDefaultIANAMappings() {
		if (defaultIANAmappings == null) {
			defaultIANAmappings = new Properties();
			Bundle keyBundle = Platform.getBundle(ICodedResourcePlugin.ID);
			IPath keyPath = new Path("config/defaultIANA.properties"); //$NON-NLS-1$
			URL location = Platform.find(keyBundle, keyPath);
			InputStream propertiesInputStream = null;
			try {
				propertiesInputStream = location.openStream();
				defaultIANAmappings.load(propertiesInputStream);
			}
			catch (IOException e) {
				// if can't read, just assume there's no
				// default IANA mappings
				// and repeated attempts will not occur,
				// since they
				// will be represented by an empty
				// Properties object
			}
		}
		return defaultIANAmappings;
	}

	/**
	 * Returns display (translated) string for encoding name. If there is no
	 * "custom" translated version available, it defers to ther VM's Charset
	 * support. It will return null if no display name is available.
	 * 
	 * @param String
	 *            charset name
	 * @return Human friendly display name
	 */
	public static String getDisplayString(String charsetName) {
		if (charsetName == null)
			return null;
		String result = (String) getSupportedEncodingDisplayNames().get(charsetName);
		if (result == null) {
			// if we don't have a special one, just return
			// what's provided by Charset

			try {
				Charset charset = Charset.forName(charsetName);
				result = charset.displayName();
			}
			catch (UnsupportedCharsetException e) {
				// if not supported, the display name is
				// the least of clients concerns :)
			}
		}
		return result;
	}

	/**
	 * @return Returns the javaEncodings.
	 */
	private static ArrayList getEncodings() {
		if (encodings == null) {
			initHashTables();
		}
		return encodings;
	}

	public static String getIanaPreferredCharsetName(String charsetName) {
		String preferredName = charsetName;

		try {
			Charset charset = Charset.forName(charsetName);
			if (charset.name() != null) {
				preferredName = charset.name();
			}
		}
		catch (IllegalCharsetNameException e) {
			// just return input if illegal
		}
		catch (UnsupportedCharsetException e) {
			// just return input if illegal
		}
		return preferredName;
	}

	/**
	 * Returns a default IANA name that is listed in CommonCharsetNames. Here
	 * is how it checks: 1. check to see if charsetName is in the
	 * CommonCharsetNames list and if so, just return it. 2. check to see if
	 * charsetName is listed in defaultIANAmappings which contains a mapping
	 * of more common encodings and the default IANA name they should map to.
	 * 3. return defaultIanaName if all else fails
	 */
	public static String getPreferredDefaultIanaName(String charsetName, String defaultIanaName) {
		String preferredName = defaultIanaName;
		String guessedName = charsetName;
		try {
			guessedName = CodedIO.getAppropriateJavaCharset(charsetName);
		}
		catch (IllegalCharsetNameException e) {
			// just ignore if illegal
		}
		catch (UnsupportedCharsetException e) {
			// just ignore if illegal
		}
		if (getEncodings().contains(guessedName))
			preferredName = guessedName;
		else {
			preferredName = getDefaultIANAMappings().getProperty(guessedName, preferredName);
		}

		return preferredName;
	}

	/**
	 * @return
	 */
	private static Hashtable getSupportedEncodingDisplayNames() {
		if (supportedEncodingDisplayNames == null) {
			initHashTables();
		}
		return supportedEncodingDisplayNames;
	}

	private static void initHashTables() {
		if (supportedEncodingDisplayNames == null) {
			// Initialize hash table for encoding table
			supportedEncodingDisplayNames = new Hashtable();
			encodings = new ArrayList();

			ResourceBundle bundle = null;
			InputStream bundleStream = null;
			try {
				URL bundleURL = Platform.find(Platform.getBundle(ICodedResourcePlugin.ID), Path.fromOSString("$nl$/config/charset.properties")); //$NON-NLS-1$
				if (bundleURL != null) {
					bundleStream = bundleURL.openStream();
					bundle = new PropertyResourceBundle(bundleStream);
				}

				String totalNumString = bundle.getString("totalnumber");//$NON-NLS-1$
				int totalNum = 0;
				if (totalNumString.length() != 0) {
					try {
						totalNum = Integer.valueOf(totalNumString).intValue();
					}
					catch (NumberFormatException e) {
						totalNum = 0;
					}
				}

				for (int i = 0; i < totalNum; i++) {
					String iana = bundle.getString("codeset." + i + ".iana");//$NON-NLS-2$//$NON-NLS-1$
					String displayName = bundle.getString("codeset." + i + ".label");//$NON-NLS-2$//$NON-NLS-1$

					encodings.add(iana);
					supportedEncodingDisplayNames.put(iana, displayName);
				}
			}
			catch (IOException e) {
				Logger.logException("invalid install or configuration", e); //$NON-NLS-1$
			}
			finally {
				try {
					if (bundleStream != null)
						bundleStream.close();
				}
				catch (IOException x) {
				}
			}
		}
	}

	public static void main(String[] args) {
		// unit test only
		String test = "Cp1252"; //$NON-NLS-1$
		String result = CommonCharsetNames.getIanaPreferredCharsetName(test);
		System.out.println(test + " --> " + result); //$NON-NLS-1$

		test = "MS932"; //$NON-NLS-1$
		result = CommonCharsetNames.getIanaPreferredCharsetName(test);
		System.out.println(test + " --> " + result); //$NON-NLS-1$

	}

	public CommonCharsetNames() {
		super();
		initHashTables();
	}
}
