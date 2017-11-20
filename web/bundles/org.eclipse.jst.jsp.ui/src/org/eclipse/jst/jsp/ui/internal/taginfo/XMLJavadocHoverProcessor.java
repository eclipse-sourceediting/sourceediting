/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
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
package org.eclipse.jst.jsp.ui.internal.taginfo;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.utils.StringUtils;



/**
 * Provides hover help documentation for xml tags
 * 
 * @see org.eclipse.jface.text.ITextHover
 */
public class XMLJavadocHoverProcessor extends JSPJavaJavadocHoverProcessor {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jst.jsp.ui.internal.taginfo.JSPJavaJavadocHoverProcessor
	 * #getHoverInfo(org.eclipse.jface.text.ITextViewer,
	 * org.eclipse.jface.text.IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		String elementName = null;
		try {
			elementName = textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());
		}
		catch (BadLocationException e) {
			return null;
		}

		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getExistingModelForRead(textViewer.getDocument());
			if (model != null) {
				String baseLocation = model.getBaseLocation();
				// URL fixup from the taglib index record
				if (baseLocation.startsWith("jar:/file:")) {
					baseLocation = StringUtils.replace(baseLocation, "jar:/", "jar:");
				}
				/*
				 * Handle opened TLD files from JARs on the Java Build Path by
				 * finding a package fragment root for the same .jar file and
				 * opening the class from there. Note that this might be from
				 * a different Java project's build path than the TLD.
				 */
				if (baseLocation.startsWith("jar:file:") && baseLocation.indexOf('!') > 9) {
					String baseFile = baseLocation.substring(9, baseLocation.indexOf('!'));
					IPath basePath = new Path(baseFile);
					IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
					for (int i = 0; i < projects.length; i++) {
						try {
							if (projects[i].isAccessible() && projects[i].hasNature(JavaCore.NATURE_ID)) {
								IJavaProject javaProject = JavaCore.create(projects[i]);
								if (javaProject.exists()) {
									IPackageFragmentRoot root = javaProject.findPackageFragmentRoot(basePath);
									if (root != null) {
										// TLDs don't reference method names
										IType type = javaProject.findType(elementName);
										if (type != null) {
											return getHoverInfo(new IJavaElement[]{type});
										}
									}
								}
							}
						}
						catch (CoreException e) {
							Logger.logException(e);
						}
					}
				}
				else {
					IPath basePath = new Path(baseLocation);
					if (basePath.segmentCount() > 1) {
						IJavaProject javaProject = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot().getProject(basePath.segment(0)));
						if (javaProject.exists()) {
							try {
								// TLDs don't reference method names
								IType type = javaProject.findType(elementName);
								if (type != null) {
									return getHoverInfo(new IJavaElement[]{type});
								}
							}
							catch (JavaModelException e) {
								Logger.logException(e);
							}
						}
					}
				}
			}

		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.
	 * text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return JavaWordFinder.findWord(textViewer.getDocument(), offset, true);
	}
}
