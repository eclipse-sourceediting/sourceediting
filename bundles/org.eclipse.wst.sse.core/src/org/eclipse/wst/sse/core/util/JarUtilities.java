/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
	 * @see http://java.sun.com/products/jsp/errata_1_1_a_042800.html, Issues
	 *      8 & 9
	 * 
	 * "There are two cases. In both cases the TLD_URI is to be interpreted
	 * relative to the root of the Web Application. In the first case the
	 * TLD_URI refers to a TLD file directly. In the second case, the TLD_URI
	 * refers to a JAR file. If so, that JAR file should have a TLD at
	 * location META-INF/taglib.tld."
	 */
	public static final String JSP11_TAGLIB = "META-INF/taglib.tld"; //$NON-NLS-1$

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

	/**
	 * Provides a stream to a local copy of the input or null if not possible
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
		InputStream cache = null;
		if (entryInputStream != null) {
			int c;
			ByteArrayOutputStream buffer = null;
			if (zentry.getSize() > 0) {
				buffer = new ByteArrayOutputStream((int) zentry.getSize());
			}
			else {
				buffer = new ByteArrayOutputStream();
			}
			// array dim restriction?
			bytes = new byte[2048];
			try {
				while ((c = entryInputStream.read(bytes)) >= 0) {
					buffer.write(bytes, 0, c);
				}
				cache = new ByteArrayInputStream(buffer.toByteArray());
			}
			catch (IOException ioe) {
				// no cleanup can be done
			}
			finally {
				try {
					entryInputStream.close();
				}
				catch (IOException e) {
				}
			}
		}
		// }

		closeJarFile(jarfile);

		return cache;
	}

	public static String[] getEntryNames(IResource jarResource) {
		if (jarResource == null)
			return new String[0];
		return getEntryNames(jarResource.getLocation().toString());
	}

	public static String[] getEntryNames(String jarFilename) {
		return getEntryNames(jarFilename, true);
	}

	public static String[] getEntryNames(String jarFilename, boolean excludeDirectories) {
		JarFile jarfile = null;
		List entryNames = new ArrayList();
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
			Logger.log(Logger.WARNING, "JarUtilities: " + ioExc.getMessage());
		}
		finally {
			try {
				if (jarfile != null) {
					jarfile.close();
				}
			}
			catch (IOException e) {
				// nothing can be done
			}
		}
		String[] names = (String[]) entryNames.toArray(new String[0]);
		return names;
	}

	public static InputStream getInputStream(IResource jarResource, String entryName) {
		if (jarResource == null)
			return null;
		return getInputStream(jarResource.getLocation().toString(), entryName);
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
}
