/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class WebContainerInitializer extends ClasspathContainerInitializer {

	public WebContainerInitializer() {
	}

	public void initialize(final IPath containerPath, IJavaProject project) throws CoreException {
		IClasspathContainer container = new IClasspathContainer() {
			
			public IPath getPath() {
				return containerPath;
			}
			
			public int getKind() {
				return IClasspathContainer.K_SYSTEM;
			}
			
			public String getDescription() {
				return "JARs for the Java Web Project";
			}
			
			public IClasspathEntry[] getClasspathEntries() {
				try {
					IClasspathEntry servlet = JavaCore.newLibraryEntry(new Path(FileLocator.getBundleFile(Platform.getBundle("javax.servlet")).getAbsolutePath()), null, null);
					IClasspathEntry jsp = JavaCore.newLibraryEntry(new Path(FileLocator.getBundleFile(Platform.getBundle("javax.servlet.jsp")).getAbsolutePath()), null, null);
					return new IClasspathEntry[] { servlet, jsp };
				}
				catch (IOException e) { }
				return new IClasspathEntry[0];
			}
		};
		JavaCore.setClasspathContainer(containerPath, new IJavaProject[] { project }, new IClasspathContainer[] { container }, null);
	}

}
