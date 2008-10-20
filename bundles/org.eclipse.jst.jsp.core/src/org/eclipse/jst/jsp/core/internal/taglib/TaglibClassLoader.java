/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.taglib;


import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.jsp.core.internal.Logger;

/**
 * Custom classloader which allows you to add source directories (folders
 * containing .class files) and jars to the classpath.
 */
public class TaglibClassLoader extends URLClassLoader {
	// for debugging
	private static final boolean DEBUG = Boolean.valueOf(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/taglibclassloader")).booleanValue(); //$NON-NLS-1$

	private static final String FILE = "file:/";
	private static final String RESOURCE = "platform:/resource/";

	public TaglibClassLoader(ClassLoader parentLoader) {
		super(new URL[0], parentLoader);
	}

	public void addDirectory(String dirPath) {
		addFile(dirPath);
	}

	private void addFile(String filename) {
		try {
			URL url = new URL(FILE + filename);
			super.addURL(url);
			if (DEBUG)
				System.out.println("added: [" + url + "] to classpath"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (MalformedURLException e) {
			Logger.logException(filename, e);
		}
	}

	public void addJar(String filename) {
		addFile(filename);
	}

	public void addPath(IPath resourcePath) {
		try {
			URL url = new URL(RESOURCE + resourcePath.toString());
			super.addURL(url);
			if (DEBUG)
				System.out.println("added: [" + url + "] to classpath"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (MalformedURLException e) {
			Logger.logException(resourcePath.toString(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.URLClassLoader#findClass(java.lang.String)
	 */
	protected Class findClass(String className) throws ClassNotFoundException {
		if (DEBUG)
			System.out.println("finding: [" + className + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		return super.findClass(className);
	}
}