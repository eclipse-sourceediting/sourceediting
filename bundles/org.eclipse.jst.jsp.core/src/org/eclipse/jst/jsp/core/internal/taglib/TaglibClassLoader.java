/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.taglib;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.jsp.core.internal.Logger;



/**
 * Custom classloader which allows you to add source directories (folders
 * containing .class files) and jars to the classpath.
 * 
 * @author pavery
 */
public class TaglibClassLoader extends ClassLoader {

	// for debugging
	private static final boolean DEBUG;
	static {
		String value = Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/taglibclassloader"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}

	private List jarsList = new ArrayList();
	private List dirsList = new ArrayList();
	private List usedJars = new ArrayList();
	private List usedDirs = new ArrayList();

	private Map failedClasses = new HashMap(); // CL: added to optimize
													// failed loading

	// private List loadedClassFilenames = new ArrayList();

	public TaglibClassLoader(ClassLoader parentLoader) {
		super(parentLoader);
	}

	/**
	 * Adds a new jar to classpath.
	 * 
	 * @param filename -
	 *            full path to the jar file
	 */
	public void addJar(String filename) {
		if (DEBUG)
			System.out.println("trying to add: [" + filename + "] to classpath"); //$NON-NLS-1$ //$NON-NLS-2$
		// don't add the same entry twice, or search times will get even worse
		if (!jarsList.contains(filename)) {
			jarsList.add(filename);
			failedClasses = new HashMap();
			if (DEBUG)
				System.out.println(" + [" + filename + "] added to classpath"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Removes a jar from the classpath.
	 * 
	 * @param filename -
	 *            full path to the jar file
	 */
	public void removeJar(String filename) {
		jarsList.remove(filename);
		failedClasses = new HashMap();
		if (DEBUG)
			System.out.println("removed: [" + filename + "] from classpath"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void addDirectory(String dirPath) {
		if (!dirsList.contains(dirPath)) {
			dirsList.add(dirPath);
			failedClasses = new HashMap();
			if (DEBUG)
				System.out.println("added: [" + dirPath + "] to classpath"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Removes a directory from the classpath.
	 * 
	 * @param dirPath -
	 *            full path of the directory
	 */
	public void removeDirectory(String dirPath) {
		dirsList.remove(dirPath);
		failedClasses = new HashMap();
		if (DEBUG)
			System.out.println("removed: [" + dirPath + "] from classpath"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Returns the list of JARs on this loader's classpath that contain
	 * classes that have been loaded.
	 * 
	 * @return List - the list of JARs
	 */
	public List getJarsInUse() {
		return usedJars;
	}

	/**
	 * Returns a list of directories on this loader's classpath that contain
	 * classes that have been loaded.
	 * 
	 * @return List - the list of directories (fully-qualified filename
	 *         Strings)
	 */
	public List getDirectoriesInUse() {
		return usedDirs;
	}
	
	Map getFailures() {
		return failedClasses;
	}

	/**
	 * Returns a list of filenames for loose classes that have been loaded out
	 * of directories.
	 * 
	 * @return List - the list of class filenames
	 */
	// public List getClassFilenamesFromDirectories() {
	// return loadedClassFilenames;
	// }
	/**
	 * Searches for the given class name on the defined classpath -
	 * directories are checked before JARs.
	 * 
	 * @param className -
	 *            the name of the class to find
	 * @return Class - the loaded class
	 * @throws ClassNotFoundException
	 */
	protected synchronized Class findClass(String className) throws ClassNotFoundException {
		Class oldClass = findLoadedClass(className);
		if (oldClass != null) {
			if (DEBUG)
				System.out.println(">> TaglibClassLoader " + this + " returning existing class: " + className); //$NON-NLS-1$ //$NON-NLS-2$
			return oldClass;
		}
		if (failedClasses.containsKey(className)) {
			if (DEBUG)
				System.out.println(">> TaglibClassLoader " + this + " known missing class: " + className); //$NON-NLS-1$ //$NON-NLS-2$
			throw new ClassNotFoundException();
		}

		if (DEBUG)
			System.out.println(">> TaglibClassLoader " + this + " finding class: " + className); //$NON-NLS-1$ //$NON-NLS-2$

		Class newClass = null;
		JarFile jarfile = null;
		JarEntry entry = null;

		// get the correct name of the actual .class file to search for
		String fileName = calculateClassFilename(className);
		InputStream stream = null;
		try {
			// first try searching the classpath directories
			Iterator dirs = dirsList.iterator();
			File f;
			String dirName;
			String fileToFind = ""; //$NON-NLS-1$
			while (dirs.hasNext()) {
				dirName = (String) dirs.next();
				fileToFind = dirName + "/" + fileName; //$NON-NLS-1$

				f = new File(fileToFind);
				if (f.exists()) {
					stream = new FileInputStream(f);
					usedDirs.add(dirName);
					// loadedClassFilenames.add(fileToFind);
					if (DEBUG)
						System.out.println(">> added file from dir: " + dirName + "/" + fileName); //$NON-NLS-1$ //$NON-NLS-2$
					break;

				}
			}

			if (stream != null) {
				// found a class from a directory
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[2048];
				while (stream.available() > 0) {
					int amountRead = stream.read(buffer);
					if(amountRead > 0) {
						byteStream.write(buffer, 0, amountRead);
					}
				}

				byte[] byteArray = byteStream.toByteArray();
				try {
					if (DEBUG)
						System.out.println(">> defining newClass:" + className); //$NON-NLS-1$
					newClass = defineClass(className, byteArray, 0, byteArray.length);
					resolveClass(newClass);
				}
				catch (Throwable t) {
					if (DEBUG)
						Logger.logException("Error loading TEI class " + className, t);

					// j9 can give ClassCircularityError
					// parent should already have the class then
					// try parent loader
					try {
						Class c = getParent().loadClass(className);
						if (DEBUG)
							System.out.println(">> loaded: " + className + " with: " + getParent()); //$NON-NLS-1$ //$NON-NLS-2$
						return c;
					}
					catch (ClassNotFoundException cnf) {
						if (DEBUG)
							cnf.printStackTrace();
					}
				}
				stream.close();
			}

			if (stream == null) {
				// still haven't found the class, so now try searching the
				// jars
				// search each of the jars until we find an entry matching the
				// classname
				Iterator jars = jarsList.iterator();
				String jarName;
				while (jars.hasNext()) {
					jarName = (String) jars.next();

					// make sure the file exists or "new JarFile()" will throw
					// an exception
					f = new File(jarName);
					if (!f.exists()) {
						continue;
					}
					try {
						jarfile = new JarFile(jarName);
					}
					catch (IOException e) {
						if (DEBUG)
							Logger.logException("bad jar file", e); //$NON-NLS-1$
					}
					if (jarfile == null) {
						continue;
					}

					entry = jarfile.getJarEntry(fileName);

					if (DEBUG)
						System.out.println("looking for filename: " + fileName + " in: " + jarfile.getName()); //$NON-NLS-1$ //$NON-NLS-2$
					if (entry != null) {
						if (DEBUG)
							System.out.println("found the entry: " + entry + " for filename: " + fileName); //$NON-NLS-1$ //$NON-NLS-2$
						// found the class
						if (!usedJars.contains(jarName)) {
							// add the jar to the list of in-use jars
							usedJars.add(jarName);
						}
						break;
					}
					jarfile.close();
				}

				if (entry != null) {
					// we've found an entry for the desired class
					stream = jarfile.getInputStream(entry);
					ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
					long byteLength = entry.getSize();
					long totalBytesRead = 0;
					int bytesRead;
					byte[] byteBuffer = new byte[10000];
					while (totalBytesRead < byteLength) {
						bytesRead = stream.read(byteBuffer);
						if (bytesRead == -1) {
							break;
						}
						totalBytesRead = totalBytesRead + bytesRead;
						byteStream.write(byteBuffer, 0, bytesRead);
					}

					byte[] byteArray = byteStream.toByteArray();
					try {
						if (DEBUG)
							System.out.println(">> defining newClass:" + className); //$NON-NLS-1$
						// define the class from the byte array
						newClass = defineClass(className, byteArray, 0, byteArray.length);
						resolveClass(newClass);
					}
					catch (Throwable t) {
						Logger.logException("Error loading TEI class " + className, t);
						// j9 can give ClassCircularityError
						// parent should already have the class then

						// try parent
						try {
							Class c = getParent().loadClass(className);
							if (DEBUG)
								System.out.println(">> loaded: " + className + " with: " + getParent()); //$NON-NLS-1$ //$NON-NLS-2$
							return c;
						}
						catch (ClassNotFoundException cnf) {
							if (DEBUG)
								cnf.printStackTrace();
							failedClasses.put(className, cnf);
						}
					}
					stream.close();
					jarfile.close();
				}
			}
		}
		catch (Throwable t) {
			failedClasses.put(className, t);
			return null;
		}
		finally {
			try {
				if (stream != null) {
					stream.close();
				}
				if (jarfile != null) {
					jarfile.close();
				}
			}
			catch (IOException ioe) {
				// ioe.printStackTrace();
				// just trying to close down anyway - ignore
			}
		}

		if (newClass != null) {
			if (DEBUG)
				System.out.println(">> loaded: " + newClass + " with: " + this); //$NON-NLS-1$ //$NON-NLS-2$
			return newClass;
		}

//		failedClasses.add(className);
		throw new ClassNotFoundException();
	}

	/**
	 * Replaces '.' in the classname with '/' and appends '.class' if needed.
	 * 
	 * @return String - the properly-formed class name
	 */
	private String calculateClassFilename(String name) {
		StringBuffer buffer = new StringBuffer(name.replace('.', '/'));
		if (!name.endsWith(".class")) { //$NON-NLS-1$
			buffer.append(".class"); //$NON-NLS-1$
		}
		return buffer.toString();
	}
}