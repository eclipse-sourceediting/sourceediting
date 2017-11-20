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
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.core.internal.Logger;


public class JarUtilities {

	/**
	 * @see http://java.sun.com/products/jsp/errata_1_1_a_042800.html, Issues
	 *      8 & 9
	 * 
	 *      "There are two cases. In both cases the TLD_URI is to be
	 *      interpreted relative to the root of the Web Application. In the
	 *      first case the TLD_URI refers to a TLD file directly. In the
	 *      second case, the TLD_URI refers to a JAR file. If so, that JAR
	 *      file should have a TLD at location META-INF/taglib.tld."
	 */
	public static final String JSP11_TAGLIB = "META-INF/taglib.tld"; //$NON-NLS-1$

	public static void closeJarFile(ZipFile file) {
		if (file == null)
			return;
		try {
			file.close();
		}
		catch (IOException ioe) {
			// no cleanup can be done
		}
	}

	/**
	 * Provides a stream to a local copy of the input or null if not possible
	 */
	protected static InputStream getCachedInputStream(String jarFilename, String entryName) {
		File testFile = new File(jarFilename);
		if (!testFile.exists())
			return getInputStream(ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(jarFilename)), entryName);

		InputStream cache = null;
		ZipFile jarfile = null;
		try {
			jarfile = new ZipFile(jarFilename);
		}
		catch (IOException ioExc) {
			closeJarFile(jarfile);
		}

		if (jarfile != null) {
			try {
				ZipEntry zentry = jarfile.getEntry(entryName);
				if (zentry != null) {
					InputStream entryInputStream = null;
					try {
						entryInputStream = jarfile.getInputStream(zentry);
					}
					catch (IOException ioExc) {
						// no cleanup can be done
					}

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
						byte bytes[] = new byte[2048];
						try {
							while ((c = entryInputStream.read(bytes)) >= 0) {
								buffer.write(bytes, 0, c);
							}
							cache = new ByteArrayInputStream(buffer.toByteArray());
							closeJarFile(jarfile);
						}
						catch (IOException ioe) {
							// no cleanup can be done
						}
						finally {
							try {
								entryInputStream.close();
							}
							catch (IOException e) {
								// no cleanup can be done
							}
						}
					}
				}
			}
			finally {
				closeJarFile(jarfile);
			}
		}
		return cache;
	}

	private static InputStream copyAndCloseStream(InputStream original) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		InputStream cachedCopy = null;

		if (original != null) {
			int c;
			// array dim restriction?
			byte bytes[] = new byte[2048];
			try {
				while ((c = original.read(bytes)) >= 0) {
					buffer.write(bytes, 0, c);
				}
				cachedCopy = new ByteArrayInputStream(buffer.toByteArray());
				closeStream(original);
			}
			catch (IOException ioe) {
				// no cleanup can be done
			}
		}
		return cachedCopy;
	}

	/**
	 * @param jarResource
	 *            the zip file
	 * @return a string array containing the entry paths to every file in this
	 *         zip resource, excluding directories
	 */
	public static String[] getEntryNames(IResource jarResource) {
		if (jarResource == null || jarResource.getType() != IResource.FILE || !jarResource.isAccessible())
			return new String[0];

		try {
			return getEntryNames(jarResource.getFullPath().toString(), new ZipInputStream(((IFile) jarResource).getContents()), true);
		}
		catch (CoreException e) {
			// no cleanup can be done
		}

		IPath location = jarResource.getLocation();
		if (location != null)
			return getEntryNames(location.toString());
		return new String[0];
	}

	/**
	 * @param jarFilename
	 *            the location of the zip file
	 * @return a string array containing the entry paths to every file in the
	 *         zip file at this location, excluding directories
	 */
	public static String[] getEntryNames(String jarFilename) {
		return getEntryNames(jarFilename, true);
	}

	private static String[] getEntryNames(String filename, ZipInputStream jarInputStream, boolean excludeDirectories) {
		List entryNames = new ArrayList();
		try {
			ZipEntry z = jarInputStream.getNextEntry();
			while (z != null) {
				if (!(z.isDirectory() && excludeDirectories))
					entryNames.add(z.getName());
				z = jarInputStream.getNextEntry();
			}
		}
		catch (ZipException zExc) {
			Logger.log(Logger.WARNING_DEBUG, "JarUtilities ZipException: (stream) " + filename, zExc); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (IOException ioExc) {
			// no cleanup can be done
		}
		finally {
			closeStream(jarInputStream);
		}
		String[] names = (String[]) entryNames.toArray(new String[0]);
		return names;
	}

	private static void closeStream(InputStream inputStream) {
		try {
			inputStream.close();
		}
		catch (IOException e) {
			// no cleanup can be done
		}
	}

	/**
	 * @param jarFilename
	 *            the location of the zip file
	 * @param excludeDirectories
	 *            whether to not include directories in the results
	 * @return a string array containing the entry paths to every file in the
	 *         zip file at this location, excluding directories if indicated
	 */
	public static String[] getEntryNames(String jarFilename, boolean excludeDirectories) {
		ZipFile jarfile = null;
		List entryNames = new ArrayList();
		File f = new File(jarFilename);
		if (f.exists() && f.canRead()) {
			try {
				jarfile = new ZipFile(f);
				Enumeration entries = jarfile.entries();
				while (entries.hasMoreElements()) {
					ZipEntry z = (ZipEntry) entries.nextElement();
					if (!(z.isDirectory() && excludeDirectories))
						entryNames.add(z.getName());
				}
			}
			catch (ZipException zExc) {
				Logger.log(Logger.WARNING_DEBUG, "JarUtilities ZipException: " + jarFilename + " " + zExc.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			catch (IOException ioExc) {
				// no cleanup can be done
			}
			finally {
				closeJarFile(jarfile);
			}
		}
		String[] names = (String[]) entryNames.toArray(new String[0]);
		return names;
	}

	/**
	 * @param jarResource
	 *            the zip file
	 * @param entryName
	 *            the entry's path in the zip file
	 * @return an InputStream to the contents of the given entry or null if
	 *         not possible
	 */
	public static InputStream getInputStream(IResource jarResource, String entryName) {
		if (jarResource == null || jarResource.getType() != IResource.FILE || !jarResource.isAccessible())
			return null;

		try {
			InputStream zipStream = ((IFile) jarResource).getContents();
			return getInputStream(jarResource.getFullPath().toString(), new ZipInputStream(zipStream), entryName);
		}
		catch (CoreException e) {
			// no cleanup can be done, probably out of sync
		}

		IPath location = jarResource.getLocation();
		if (location != null) {
			return getInputStream(location.toString(), entryName);
		}
		return null;
	}

	private static InputStream getInputStream(String filename, ZipInputStream zip, String entryName) {
		InputStream result = null;
		try {
			ZipEntry z = zip.getNextEntry();
			while (z != null && !z.getName().equals(entryName)) {
				z = zip.getNextEntry();
			}
			if (z != null) {
				result = copyAndCloseStream(zip);
			}
		}
		catch (ZipException zExc) {
			Logger.log(Logger.WARNING_DEBUG, "JarUtilities ZipException: (stream) " + filename, zExc); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (IOException ioExc) {
			// no cleanup can be done
		}
		finally {
			closeStream(zip);
		}
		return result;
	}

	/**
	 * @param jarFilename
	 *            the location of the zip file
	 * @param entryName
	 *            the entry's path in the zip file
	 * @return an InputStream to the contents of the given entry or null if
	 *         not possible
	 */
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
	 * @param url
	 *            a URL pointint to a zip file
	 * @return a cached copy of the contents at this URL, opening it as a file
	 *         if it is a jar:file: URL, and using a URLConnection otherwise,
	 *         or null if it could not be read. All sockets and file handles
	 *         are closed as quickly as possible.
	 */
	public static InputStream getInputStream(URL url) {
		String urlString = url.toString();
		if (urlString.length() > 12 && urlString.startsWith("jar:file:") && urlString.indexOf("!/") > 9) { //$NON-NLS-1$ //$NON-NLS-2$
			int fileIndex = urlString.indexOf("!/"); //$NON-NLS-1$ 
			String jarFileName = urlString.substring(9, fileIndex);
			if (fileIndex < urlString.length()) {
				String jarPath = urlString.substring(fileIndex + 1);
				return getInputStream(jarFileName, jarPath);
			}
		}

		InputStream input = null;
		JarURLConnection jarUrlConnection = null;
		try {
			URLConnection openConnection = url.openConnection();
			openConnection.setDefaultUseCaches(false);
			openConnection.setUseCaches(false);
			if (openConnection instanceof JarURLConnection) {
				jarUrlConnection = (JarURLConnection) openConnection;
				JarFile jarFile = jarUrlConnection.getJarFile();
				input = jarFile.getInputStream(jarUrlConnection.getJarEntry());
			}
			else {
				input = openConnection.getInputStream();
			}
			if (input != null) {
				return copyAndCloseStream(input);
			}
		}
		catch (FileNotFoundException e) {
			// May be a file URL connection, do not log
		}
		catch (IOException e) {
			Logger.logException(e);
		}
		finally {
			if (jarUrlConnection != null) {
				try {
					jarUrlConnection.getJarFile().close();
				}
				catch (IOException e) {
					// ignore
				}
				catch (IllegalStateException e) {
					/*
					 * ignore. Can happen in case the stream.close() did close
					 * the jar file see
					 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=140750
					 */
				}

			}
		}
		return null;
	}
}
