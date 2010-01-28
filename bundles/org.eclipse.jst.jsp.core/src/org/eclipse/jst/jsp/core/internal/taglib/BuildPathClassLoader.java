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
package org.eclipse.jst.jsp.core.internal.taglib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.wst.sse.core.utils.StringUtils;

/**
 * Custom ClassLoader backed by a Java Project.
 */
public class BuildPathClassLoader extends ClassLoader {
	private static final boolean DEBUG = Boolean.valueOf(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/taglibclassloader")).booleanValue(); //$NON-NLS-1$
	private IJavaProject fProject;

	public BuildPathClassLoader(ClassLoader parent, IJavaProject project) {
		super(parent);
		fProject = project;
	}

	/**
	 * Closes the given file with "extreme prejudice".
	 * 
	 * @param file the zip file to be closed
	 */
	public void closeJarFile(ZipFile file) {
		if (file == null)
			return;
		try {
			file.close();
		}
		catch (IOException ioe) {
			// no cleanup can be done
			Logger.logException("JarUtilities: Could not close file " + file.getName(), ioe); //$NON-NLS-1$
		}
	}

	/*
	 * This may pose a runtime performance problem as it opens the containing
	 * .jar file for each class, but this is countered by no longer leaving
	 * file handles open nor having to directly interact the build path at
	 * all. If it is a problem, the TaglibHelper should control some
	 * "batching" whereby we leave the JarFiles open until directed to close
	 * them at the end of TaglibHelper.addTEIVariables(...).
	 * 
	 * @see java.lang.ClassLoader#findClass(java.lang.String)
	 */
	protected Class findClass(String className) throws ClassNotFoundException {
		if (DEBUG)
			System.out.println("finding: [" + className + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			IType type = fProject.findType(className);
			int offset = -1;
			if (type == null && (offset = className.indexOf('$')) != -1) {
				// Internal classes from source files must be referenced by . instead of $
				String cls = className.substring(0, offset) + className.substring(offset).replace('$', '.');
				type = fProject.findType(cls);
			}
			if (type != null) {
				IPath path = null;
				IResource resource = type.getResource();

				if (resource != null)
					path = resource.getLocation();
				if (path == null)
					path = type.getPath();

				// needs to be compiled before we can load it
				if ("class".equalsIgnoreCase(path.getFileExtension())) {
					IFile file = null;

					if (resource != null && resource.getType() == IResource.FILE)
						file = (IFile) resource;
					else
						file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

					if (file != null && file.isAccessible()) {
						byte[] bytes = loadBytes(file);
						return defineClass(className, bytes, 0, bytes.length);
					}
				}
				// Look up the class file based on the output location of the java project
				else if ("java".equalsIgnoreCase(path.getFileExtension()) && resource != null) { //$NON-NLS-1$
					if (resource.getProject() != null) {
						IJavaProject jProject = JavaCore.create(resource.getProject());
						String outputClass = StringUtils.replace(type.getFullyQualifiedName(), ".", "/").concat(".class"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						IPath classPath = jProject.getOutputLocation().append(outputClass);
						IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(classPath);
						if (file != null && file.isAccessible()) {
							byte[] bytes = loadBytes(file);
							return defineClass(className, bytes, 0, bytes.length);
						}
					}
				}
				else if ("jar".equalsIgnoreCase(path.getFileExtension())) {
					String expectedFileName = StringUtils.replace(className, ".", "/").concat(".class"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					byte[] bytes = getCachedInputStream(path.toOSString(), expectedFileName);
					return defineClass(className, bytes, 0, bytes.length);
				}
			}
		}
		catch (JavaModelException e) {
			Logger.logException(e);
		}
		return super.findClass(className);
	}

	/**
	 * Get the entry from the jarfile
	 * @param jarFilename the string path of the jarfile
	 * @param entryName the fully-qualified entry
	 * @return the bytes for the entry within the jarfile or a byte array of size 0
	 */
	private byte[] getCachedInputStream(String jarFilename, String entryName) {
		ByteArrayOutputStream buffer = null;

		File testFile = new File(jarFilename);
		if (!testFile.exists())
			return null;

		ZipFile jarfile = null;
		try {
			jarfile = new ZipFile(jarFilename);
			
			if (jarfile != null) {
				ZipEntry zentry = jarfile.getEntry(entryName);
				if (zentry != null) {
					InputStream entryInputStream = null;
					try {
						entryInputStream = jarfile.getInputStream(zentry);
					}
					catch (IOException ioExc) {
						Logger.logException("JarUtilities: " + jarFilename, ioExc); //$NON-NLS-1$
					}

					if (entryInputStream != null) {
						int c;
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
				}
			}
		}
		catch (IOException ioExc) {
			Logger.logException("JarUtilities: " + jarFilename, ioExc); //$NON-NLS-1$
		}
		finally {
			closeJarFile(jarfile);
		}

		if (buffer != null) {
			return buffer.toByteArray();
		}
		return new byte[0];
	}

	/**
	 * @param file
	 * @return
	 */
	private byte[] loadBytes(IFile file) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = null;
		try {
			in = file.getContents();
			byte[] buffer = new byte[4096];
			int read = 0;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		catch (IOException e) {
			Logger.logException(e);
		}
		finally {
			try {
				if (in != null)
					in.close();
			}
			catch (IOException e) {
			}
		}
		return out.toByteArray();
	}

}