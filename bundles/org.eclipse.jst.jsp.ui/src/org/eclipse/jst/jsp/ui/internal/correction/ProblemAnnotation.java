/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.correction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation;
import org.eclipse.jdt.internal.ui.javaeditor.JavaMarkerAnnotation;
import org.eclipse.jdt.internal.ui.text.correction.JavaCorrectionProcessor;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ProblemAnnotation extends Annotation implements IJavaAnnotation {
	private static Image fgQuickFixImage;
	private static Image fgQuickFixErrorImage;
	private static boolean fgQuickFixImagesInitialized = false;

	private ICompilationUnit fCompilationUnit;
	private List fOverlaids;
	private IProblem fProblem;
	private Image fImage;
	private boolean fQuickFixImagesInitialized = false;

	public ProblemAnnotation(IProblem problem, ICompilationUnit cu) {

		fProblem = problem;
		fCompilationUnit = cu;


		
		if (IProblem.Task == fProblem.getID()) {
			setType(JavaMarkerAnnotation.TASK_ANNOTATION_TYPE);
			//fLayer = TASK_LAYER;
		}
		else if (fProblem.isWarning()) {
			setType(JavaMarkerAnnotation.WARNING_ANNOTATION_TYPE);
			//fLayer = WARNING_LAYER;
		}
		else if (fProblem.isError()) {
			setType(JavaMarkerAnnotation.ERROR_ANNOTATION_TYPE);
			//fLayer = ERROR_LAYER;
		}
		else {
			setType(JavaMarkerAnnotation.INFO_ANNOTATION_TYPE);
			//fLayer = INFO_LAYER;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation#hasOverlay()
	 */
	public boolean hasOverlay() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation#getOverlay()
	 */
	public IJavaAnnotation getOverlay() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation#getOverlaidIterator()
	 */
	public Iterator getOverlaidIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation#addOverlaid(org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation)
	 */
	public void addOverlaid(IJavaAnnotation annotation) {
		if (fOverlaids == null)
			fOverlaids = new ArrayList(1);
		fOverlaids.add(annotation);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation#removeOverlaid(org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation)
	 */
	public void removeOverlaid(IJavaAnnotation annotation) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation#isProblem()
	 */
	public boolean isProblem() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation#getCompilationUnit()
	 */
	public ICompilationUnit getCompilationUnit() {
		return fCompilationUnit;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation#getArguments()
	 */
	public String[] getArguments() {
		return isProblem() ? fProblem.getArguments() : null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation#getId()
	 */
	public int getId() {
		return fProblem.getID();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation#getImage(org.eclipse.swt.widgets.Display)
	 */
	public Image getImage(Display display) {
		initializeImages();
		return fImage;
	}

	private void initializeImages() {
		// http://bugs.eclipse.org/bugs/show_bug.cgi?id=18936
		if (!fQuickFixImagesInitialized) {
			if (isProblem() && indicateQuixFixableProblems() && JavaCorrectionProcessor.hasCorrections(this)) { // no light bulb for tasks
				if (!fgQuickFixImagesInitialized) {
					fgQuickFixImage = JavaPluginImages.get(JavaPluginImages.IMG_OBJS_FIXABLE_PROBLEM);
					fgQuickFixErrorImage = JavaPluginImages.get(JavaPluginImages.IMG_OBJS_FIXABLE_ERROR);
					fgQuickFixImagesInitialized = true;
				}
				if (JavaMarkerAnnotation.ERROR_ANNOTATION_TYPE.equals(getType()))
					fImage = fgQuickFixErrorImage;
				else
					fImage = fgQuickFixImage;
			}
			fQuickFixImagesInitialized = true;
		}
	}

	private boolean indicateQuixFixableProblems() {
		return PreferenceConstants.getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_CORRECTION_INDICATION);
	}
}