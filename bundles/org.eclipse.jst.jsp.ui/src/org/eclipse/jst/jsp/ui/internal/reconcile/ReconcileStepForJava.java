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
package org.eclipse.jst.jsp.ui.internal.reconcile;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilableModel;
import org.eclipse.jface.text.reconciler.IReconcileResult;
import org.eclipse.jface.text.reconciler.IReconcileStep;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationExtension;
import org.eclipse.jst.jsp.core.internal.provisional.text.IJSPPartitionTypes;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.internal.reconcile.DocumentAdapter;
import org.eclipse.wst.sse.ui.internal.reconcile.ReconcileAnnotationKey;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredReconcileStep;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;

/**
 * This reconcile step has a Java source document as input model and maintains
 * a Java working copy as its model.
 * 
 * @plannedfor 1.0
 */
public class ReconcileStepForJava extends StructuredReconcileStep {

	/**
	 * Adapts an <code>ICompilationUnit</code> to the
	 * <code>ITextModel</code> interface.
	 * 
	 * ISSUE: according to "never used" compiler warnings, 
	 * this class doesn't do anything?
	 */
	private class CompilationUnitAdapter implements IReconcilableModel {

		// private ICompilationUnit fCompilationUnit;

		CompilationUnitAdapter(ICompilationUnit cu) {
			// fCompilationUnit = cu;
		}
		// never used
		// private ICompilationUnit getCompilationUnit() {
		// return fCompilationUnit;
		// }
	}

	private JSPTranslation fJspTranslation;
	private CompilationUnitAdapter fModel;

	/**
	 * Creates the last reconcile step of the pipe.
	 */
	public ReconcileStepForJava() {
		super();
	}

	/**
	 * Creates an intermediate reconcile step which adds the given step to the
	 * pipe.
	 */
	public ReconcileStepForJava(IReconcileStep step) {
		super(step);
	}

	/*
	 * @see AbstractReconcileStep#reconcileModel(DirtyRegion, IRegion)
	 */
	protected IReconcileResult[] reconcileModel(DirtyRegion dirtyRegion, IRegion subRegion) {
		Assert.isTrue(getInputModel() instanceof JSPTranslationWrapper, "wrong model"); //$NON-NLS-1$

		fJspTranslation = ((JSPTranslationWrapper) getInputModel()).getTranslation();

		if (DEBUG)
			System.out.println("[trace reconciler] > reconciling model in JAVA step w/ dirty region: " + dirtyRegion.getText()); //$NON-NLS-1$

		try {
			fJspTranslation.setProblemCollectingActive(true);
			fJspTranslation.reconcileCompilationUnit();
		}
		finally {
			if (fJspTranslation != null)
				fJspTranslation.setProblemCollectingActive(false);
		}

		List problems = null;
		// I was frequently seeing null here, especially as editors closed,
		// so just gaurding against that. (Not sure why it was null).
		if (fJspTranslation != null) {
			problems = fJspTranslation.getProblems();
		}
		IReconcileResult[] results = adaptProblemsToAnnotations(problems);
		return results;
	}

	/**
	 * @return
	 */
	private IReconcileResult[] adaptProblemsToAnnotations(List problems) {
		if (problems == null)
			return new IReconcileResult[0];

		TemporaryAnnotation[] annotations = new TemporaryAnnotation[problems.size()];
		IProblem p = null;
		for (int i = 0; i < problems.size(); i++) {
			p = (IProblem) problems.get(i);
			annotations[i] = createTemporaryAnnotationFromProblem(p);
		}
		return annotations;
	}

	/**
	 * Converts an IProblem to a TemporaryAnnotation.
	 * 
	 * @param problem
	 * @return
	 */
	private TemporaryAnnotation createTemporaryAnnotationFromProblem(IProblem problem) {
		String type = TemporaryAnnotation.ANNOT_ERROR;
		if (problem.isWarning())
			type = TemporaryAnnotation.ANNOT_WARNING;
		Position pos = new Position(problem.getSourceStart(), problem.getSourceEnd() - problem.getSourceStart() + 1);
		JSPTranslationExtension translation = ((JSPTranslationWrapper) getInputModel()).getTranslation();
		int jspOffset = translation.getJspOffset(pos.offset);

		ReconcileAnnotationKey key = null;
		IStructuredDocument document = getInputStructuredDocument();
		if (jspOffset != -1 && document != null) {
			key = createKey(document.getRegionAtCharacterOffset(jspOffset), ReconcileAnnotationKey.TOTAL);
		}
		else {
			key = createKey(IJSPPartitionTypes.JSP_DEFAULT, ReconcileAnnotationKey.TOTAL);
		}
		TemporaryAnnotation annotation = new TemporaryAnnotation(pos, type, problem.getMessage(), key, problem.getID());
		annotation.setAdditionalFixInfo(problem);

		return annotation;
	}

	private IStructuredDocument getInputStructuredDocument() {
		IStructuredDocument structuredDocument = null;

		IReconcilableModel inputModel = getInputModel();
		IDocument document = null;
		if (inputModel instanceof DocumentAdapter)
			document = ((DocumentAdapter) inputModel).getDocument();
		if (document instanceof IStructuredDocument)
			structuredDocument = (IStructuredDocument) document;
		return structuredDocument;
	}

	/*
	 * @see AbstractReconcileStep#getModel()
	 */
	public IReconcilableModel getModel() {
		if (fModel == null) {
			fModel = new CompilationUnitAdapter(fJspTranslation.getCompilationUnit());
		}
		return fModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.reconciler.IReconcileStep#setInputModel(org.eclipse.jface.text.reconciler.IReconcilableModel)
	 */
	public void setInputModel(IReconcilableModel inputModel) {
		super.setInputModel(inputModel);
	}
}