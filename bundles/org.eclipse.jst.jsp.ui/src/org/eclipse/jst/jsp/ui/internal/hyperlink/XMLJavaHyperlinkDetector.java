/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.hyperlink;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.utils.StringUtils;

/**
 * Detects hyper-links in Tag Library Descriptors
 */
public class XMLJavaHyperlinkDetector extends AbstractHyperlinkDetector {

	private static final String JAR_FILE_PROTOCOL = "jar:file:"; //$NON-NLS-1$

	/**
	 * Based on org.eclipse.jdt.internal.ui.javaeditor.JavaElementHyperlink
	 */
	static class JavaElementHyperlink implements IHyperlink {

		private final IJavaElement fElement;
		private final IRegion fRegion;


		/**
		 * Creates a new Java element hyperlink.
		 * 
		 * @param region
		 *            the region of the link
		 * @param element
		 *            the java element to open
		 * @param qualify
		 *            <code>true</code> if the hyper-link text should show a
		 *            qualified name for element.
		 */
		JavaElementHyperlink(IRegion region, IJavaElement element) {
			fRegion = region;
			fElement = element;
		}

		public IRegion getHyperlinkRegion() {
			return fRegion;
		}

		public String getHyperlinkText() {
			String elementLabel = JavaElementLabels.getElementLabel(fElement, JavaElementLabels.ALL_POST_QUALIFIED);
			return NLS.bind(JSPUIMessages.Open, elementLabel);
		}

		public String getTypeLabel() {
			return null;
		}

		public void open() {
			try {
				JavaUI.openInEditor(fElement);
			}
			catch (PartInitException e) {
			}
			catch (JavaModelException e) {
			}
		}
	}


	private IHyperlink createHyperlink(String elementName, IRegion region, IDocument document) {
		// try file buffers
		ITextFileBuffer textFileBuffer = FileBuffers.getTextFileBufferManager().getTextFileBuffer(document);
		if (textFileBuffer != null) {
			IPath basePath = textFileBuffer.getLocation();
			if (basePath != null && !basePath.isEmpty()) {
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(basePath.segment(0));
				if (basePath.segmentCount() > 1 && project.isAccessible()) {
					return createJavaElementHyperlink(JavaCore.create(project), elementName, region);
				}
			}
		}
		// fallback to SSE-specific knowledge
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getExistingModelForRead(document);
			if (model != null) {
				String baseLocation = model.getBaseLocation();
				// URL fixup from the taglib index record
				if (baseLocation.startsWith("jar:/file:")) { //$NON-NLS-1$
					baseLocation = StringUtils.replace(baseLocation, "jar:/", "jar:"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				/*
				 * Handle opened TLD files from JARs on the Java Build Path by
				 * finding a package fragment root for the same .jar file and
				 * opening the class from there. Note that this might be from
				 * a different Java project's build path than the TLD.
				 */
				if (baseLocation.startsWith(JAR_FILE_PROTOCOL) && baseLocation.indexOf('!') > JAR_FILE_PROTOCOL.length()) {
					String baseFile = baseLocation.substring(JAR_FILE_PROTOCOL.length(), baseLocation.indexOf('!'));
					IPath basePath = new Path(baseFile);
					IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
					for (int i = 0; i < projects.length; i++) {
						try {
							if (projects[i].isAccessible() && projects[i].hasNature(JavaCore.NATURE_ID)) {
								IJavaProject javaProject = JavaCore.create(projects[i]);
								if (javaProject.exists()) {
									IPackageFragmentRoot root = javaProject.findPackageFragmentRoot(basePath);
									if (root != null) {
										return createJavaElementHyperlink(javaProject, elementName, region);
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
						return createJavaElementHyperlink(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot().getProject(basePath.segment(0))), elementName, region);
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

	private IHyperlink createJavaElementHyperlink(IJavaProject javaProject, String elementName, IRegion region) {
		if (javaProject != null && javaProject.exists()) {
			try {
				IJavaElement element = javaProject.findType(elementName);
				if (element != null && element.exists()) {
					return new JavaElementHyperlink(region, element);
				}
			}
			catch (JavaModelException e) {
				// bad name, no link
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.hyperlink.IHyperlinkDetector#detectHyperlinks
	 * (org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion,
	 * boolean)
	 */
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (region != null && textViewer != null) {
			IDocument document = textViewer.getDocument();
			// find hyperlink range for Java element
			IRegion hyperlinkRegion = region.getLength() > 0 ? region : selectQualifiedName(document, region.getOffset());
			String name = null;
			try {
				name = document.get(hyperlinkRegion.getOffset(), hyperlinkRegion.getLength()).trim();
			}
			catch (BadLocationException e) {
			}
			if (name != null) {
				IHyperlink link = createHyperlink(name, hyperlinkRegion, document);
				if (link != null)
					return new IHyperlink[]{link};
			}
		}

		return null;
	}

	/**
	 * Java always selects word when defining region
	 * 
	 * @param document
	 * @param anchor
	 * @return IRegion
	 */
	private IRegion selectQualifiedName(IDocument document, int anchor) {

		try {
			int offset = anchor;
			char c;

			while (offset >= 0) {
				c = document.getChar(offset);
				if (!Character.isJavaIdentifierPart(c) && c != '.')
					break;
				--offset;
			}

			int start = offset;

			offset = anchor;
			int length = document.getLength();

			while (offset < length) {
				c = document.getChar(offset);
				if (!Character.isJavaIdentifierPart(c) && c != '.')
					break;
				++offset;
			}

			int end = offset;

			if (start == end)
				return new Region(start, 0);

			return new Region(start + 1, end - start - 1);

		}
		catch (BadLocationException x) {
			return null;
		}
	}
}
