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
package org.eclipse.wst.sse.core.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.eclipse.core.resources.IResource;
import org.eclipse.wst.sse.core.internal.Logger;


public class JarUtilities {

	/**
	 * @see http://java.sun.com/products/jsp/errata_1_1_a_042800.html, Issues 8 & 9
	 * 
	 * "There are two cases. In both cases the TLD_URI is to be interpreted relative to the root of the Web Application. In the first case the TLD_URI refers to a TLD file directly. In the second case, the TLD_URI refers to a JAR file. If so, that JAR file should have a TLD at location META-INF/taglib.tld."
	 */
	public static final String JSP11_TAGLIB = "META-INF/taglib.tld"; //$NON-NLS-1$

	public static String[] getEntryNames(String jarFilename, boolean excludeDirectories) {
		JarFile jarfile = null;
		List entryNames = new ArrayList();
		String[] names = null;
		try {
			jarfile = new JarFile(jarFilename);
			Enumeration entries = jarfile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry z = (ZipEntry) entries.nextElement();
				if (!(z.isDirectory() && excludeDirectories))
					entryNames.add(z.getName());
			}
		}
		catch (IOException ioExc) {
			Logger.logException(ioExc);
			return null;
		}
		finally {
			try {
				jarfile.close();
			}
			catch (IOException e) {
				// nothing can be done
			}
		}
		names = new String[entryNames.size()];
		entryNames.toArray(names);
		return names;
	}

	public static String[] getEntryNames(String jarFilename) {
		return getEntryNames(jarFilename, true);
	}

	public static String[] getEntryNames(IResource jarResource) {
		if (jarResource == null)
			return null;
		return getEntryNames(jarResource.getLocation().toString());
	}

	public static InputStream getInputStream(String jarFilename, String entryName) {
		// check sanity
		if (jarFilename == null || jarFilename.length() < 1 || entryName == null || entryName.length() < 1)
			return null;

		// JAR files are not allowed to have leading '/' in member names
		String internalName = null;
		if (entryName.startsWith("/")) //$NON-NLS-1$
			internalName = entryName.substring(1);
		else
			internalName = entryName;

		return getCachedInputStream(jarFilename, internalName);
	}

	/**
	 * Provides a stream to a local copy of the input
	 */
	protected static InputStream getCachedInputStream(String jarFilename, String entryName) {
		File testFile = new File(jarFilename);
		if (!testFile.exists())
			return null;

		JarFile jarfile = null;
		try {
			jarfile = new JarFile(jarFilename);
		}
		catch (IOException ioExc) {
			Logger.logException(ioExc);
			closeJarFile(jarfile);
			return null;
		}

		ZipEntry zentry = jarfile.getEntry(entryName);
		if (zentry == null) {
			closeJarFile(jarfile);
			return null;
		}

		InputStream entryInputStream = null;
		try {
			entryInputStream = jarfile.getInputStream(zentry);
		}
		catch (IOException ioExc) {
			Logger.logException(ioExc);
			return null;
		}

		byte bytes[] = null;
		if (entryInputStream != null) {
			StringBuffer buffer = new StringBuffer();
			try {
				// read, bytes->chars
				int c;
				while ((c = entryInputStream.read()) >= 0)
					buffer.append((char) c);
				entryInputStream.close();
			}
			catch (IOException ioe) {
				// no cleanup can be done
			}
			// convert back, chars->bytes
			bytes = new byte[buffer.length()];
			for (int i = 0; i < bytes.length; i++)
				bytes[i] = (byte) buffer.charAt(i);
		}
		//		}

		closeJarFile(jarfile);

		return new ByteArrayInputStream(bytes);
	}

	public static void closeJarFile(JarFile file) {
		if (file == null)
			return;
		try {
			file.close();
		}
		catch (IOException ioe) {
			// no cleanup can be done
			Logger.log(Logger.ERROR, "Could not close file " + file.getName()); //$NON-NLS-1$
		}
		finally {
			file = null;
		}
	}

	public static InputStream getInputStream(IResource jarResource, String entryName) {
		if (jarResource == null)
			return null;
		return getInputStream(jarResource.getLocation().toString(), entryName);
	}
}
