/*******************************************************************************
 * Copyright (c) 2014, 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class WebContainerInitializer extends ClasspathContainerInitializer {
	IClasspathEntry bundles[] = null;
	public WebContainerInitializer() {
	}

	public void initialize(final IPath containerPath, IJavaProject project) throws CoreException {
		if (bundles == null) {
			try {
				IClasspathEntry servlet = JavaCore.newLibraryEntry(new Path(FileLocator.getBundleFile(Platform.getBundle("javax.servlet-api")).getAbsolutePath()), null, null);
				IClasspathEntry jsp = JavaCore.newLibraryEntry(new Path(FileLocator.getBundleFile(Platform.getBundle("javax.servlet.jsp-api")).getAbsolutePath()), null, null);
				bundles = new IClasspathEntry[] {servlet, jsp};
			}
			catch (Exception e) {
				bundles = new IClasspathEntry[0];
				System.err.println("Couldn't find a necessary bundle " + e);
				e.printStackTrace();
				Logger.logException(e);
			}
		}

		IClasspathContainer container = new IClasspathContainer() {
			public IPath getPath() {
				return containerPath;
			}
			
			public int getKind() {
				return IClasspathContainer.K_APPLICATION;
			}
			
			public String getDescription() {
				return "JARs for a Java Web Project";
			}
			
			public IClasspathEntry[] getClasspathEntries() {
				return bundles;
			}
		};
		JavaCore.setClasspathContainer(containerPath, new IJavaProject[] { project }, new IClasspathContainer[] { container }, new NullProgressMonitor());
	}

}
