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
import org.eclipse.jdt.internal.ui.text.spelling.SpellReconcileStrategy.SpellProblem;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationAccessExtension;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.AnnotationPreferenceLookup;

public class ProblemAnnotation extends Annotation implements IJavaAnnotation {
	private static Image fgQuickFixImage;
	private static Image fgQuickFixErrorImage;
	private static boolean fgQuickFixImagesInitialized = false;

	private ICompilationUnit fCompilationUnit;
	private List fOverlaids;
	private IProblem fProblem;
	private Image fImage;
	private boolean fQuickFixImagesInitialized = false;
	private int fLayer = IAnnotationAccessExtension.DEFAULT_LAYER;

	private static final String SPELLING_ANNOTATION_TYPE = "org.eclipse.ui.workbench.texteditor.spelling"; //$NON-NLS-1$

	//XXX: To be fully correct these constants should be non-static
	/** 
	 * The layer in which task problem annotations are located.
	 */
	private static final int TASK_LAYER;
	/** 
	 * The layer in which info problem annotations are located.
	 */
	private static final int INFO_LAYER;
	/** 
	 * The layer in which warning problem annotations representing are located.
	 */
	private static final int WARNING_LAYER;
	/** 
	 * The layer in which error problem annotations representing are located.
	 */
	private static final int ERROR_LAYER;

	static {
		AnnotationPreferenceLookup lookup = EditorsUI.getAnnotationPreferenceLookup();
		TASK_LAYER = computeLayer("org.eclipse.ui.workbench.texteditor.task", lookup); //$NON-NLS-1$
		INFO_LAYER = computeLayer("org.eclipse.jdt.ui.info", lookup); //$NON-NLS-1$
		WARNING_LAYER = computeLayer("org.eclipse.jdt.ui.warning", lookup); //$NON-NLS-1$
		ERROR_LAYER = computeLayer("org.eclipse.jdt.ui.error", lookup); //$NON-NLS-1$
	}

	private static int computeLayer(String annotationType, AnnotationPreferenceLookup lookup) {
		Annotation annotation = new Annotation(annotationType, false, null);
		AnnotationPreference preference = lookup.getAnnotationPreference(annotation);
		if (preference != null)
			return preference.getPresentationLayer() + 1;
		else
			return IAnnotationAccessExtension.DEFAULT_LAYER + 1;
	}

	public ProblemAnnotation(IProblem problem, ICompilationUnit cu) {

		fProblem = problem;
		fCompilationUnit = cu;

		// TODO: commented out for M3 to M4 compatibility
		// Not sure what longer term solution is, or if we 
		// can just drop this as spell checking done in text level?. 
//		if (SpellProblem.Spelling == fProblem.getID()) {
//			setType(SPELLING_ANNOTATION_TYPE);
//			fLayer = WARNING_LAYER;
//		}
//		else 
		
		if (IProblem.Task == fProblem.getID()) {
			setType(JavaMarkerAnnotation.TASK_ANNOTATION_TYPE);
			fLayer = TASK_LAYER;
		}
		else if (fProblem.isWarning()) {
			setType(JavaMarkerAnnotation.WARNING_ANNOTATION_TYPE);
			fLayer = WARNING_LAYER;
		}
		else if (fProblem.isError()) {
			setType(JavaMarkerAnnotation.ERROR_ANNOTATION_TYPE);
			fLayer = ERROR_LAYER;
		}
		else {
			setType(JavaMarkerAnnotation.INFO_ANNOTATION_TYPE);
			fLayer = INFO_LAYER;
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