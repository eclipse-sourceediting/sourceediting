/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal.reconcile.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcileResult;
import org.eclipse.jface.text.reconciler.IReconcileStep;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.ui.internal.reconcile.AbstractStructuredTextReconcilingStrategy;
import org.eclipse.wst.sse.ui.internal.reconcile.DocumentAdapter;
import org.eclipse.wst.sse.ui.internal.reconcile.IStructuredReconcileStep;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;
import org.eclipse.wst.validation.internal.core.IValidator;


/**
 * Special validator strategy. Runs validator steps contributed via
 * reconcileValidator extension point
 * 
 * @author pavery
 */
public class ValidatorStrategy extends AbstractStructuredTextReconcilingStrategy {
	private String fContentType = null;
	private HashMap fIdToStepMap = null;

	private List fMetaData = null;

	public ValidatorStrategy(ITextEditor editor, String contentType) {
		super(editor);
		fMetaData = new ArrayList();
		fContentType = contentType;
		fIdToStepMap = new HashMap();
	}

	public void addValidatorMetaData(ValidatorMetaData vmd) {
		fMetaData.add(vmd);
	}

	public boolean canValidatePartition(String partitionType) {
		ValidatorMetaData vmd = null;
		for (int i = 0; i < fMetaData.size(); i++) {
			vmd = (ValidatorMetaData) fMetaData.get(i);
			if (vmd.canHandleParitionType(getContentType(), partitionType))
				return true;
		}
		return false;
	}

	/*
	 * so that removal will work properly
	 * 
	 * @see org.eclipse.wst.sse.ui.reconcile.AbstractStructuredTextReconcilingStrategy#containsStep(org.eclipse.jface.text.reconciler.IReconcileStep)
	 */
	protected boolean containsStep(IReconcileStep step) {
		return step != null ? fIdToStepMap.values().contains(step) : false;
	}

	/**
	 * @see org.eclipse.wst.sse.ui.reconcile.AbstractStructuredTextReconcilingStrategy#createReconcileSteps()
	 */
	public void createReconcileSteps() {
		// do nothing, steps are created
	}

	public String getContentType() {
		return fContentType;
	}

	/*
	 * so that removal will work properly
	 * 
	 * @see org.eclipse.wst.sse.ui.reconcile.AbstractStructuredTextReconcilingStrategy#getPartitionTypes()
	 */
	public String[] getPartitionTypes() {
		List partitionTypes = new ArrayList();
		Iterator keys = fIdToStepMap.keySet().iterator();
		String key = null;
		while (keys.hasNext()) {
			key = (String) keys.next();
			IStructuredReconcileStep step = (IStructuredReconcileStep) fIdToStepMap.get(key);
			partitionTypes.addAll(Arrays.asList(step.getPartitionTypes()));
		}
		return (String[]) partitionTypes.toArray(new String[partitionTypes.size()]);
	}

	public void reconcile(ITypedRegion tr, DirtyRegion dr, boolean refreshAll) {

		// for external files, this can be null
		if (getFile() != null) {

			String partitionType = tr.getType();
			if (canValidatePartition(partitionType)) {
				ValidatorMetaData vmd = null;

				//TemporaryAnnotation[]
				List annotationsToRemove = new ArrayList();
				//IReconcileResult[]
				List annotationsToAdd = new ArrayList();
				for (int i = 0; i < fMetaData.size(); i++) {
					vmd = (ValidatorMetaData) fMetaData.get(i);
					if (vmd.canHandleParitionType(getContentType(), partitionType)) {
						// get step for partition type
						Object o = fIdToStepMap.get(vmd.getValidatorId());
						ReconcileStepForValidator validatorStep = null;
						if (o != null) {
							validatorStep = (ReconcileStepForValidator) o;
						} else {
							// if doesn't exist, create one
							IValidator validator = vmd.createValidator();
							validatorStep = new ReconcileStepForValidator(validator, vmd.getValidatorScope());
							validatorStep.setInputModel(new DocumentAdapter(fDocument));

							fIdToStepMap.put(vmd.getValidatorId(), validatorStep);
						}
						////////////////////////////////////////////////////////////////////////////
						// this logic copied from
						// AbstractStructuredTextReconcilingStrategy

						if (!refreshAll) {
							// regular reconcile
							annotationsToRemove.addAll(Arrays.asList(getAnnotationsToRemove(dr)));
							annotationsToAdd.addAll(Arrays.asList(validatorStep.reconcile(dr, dr, refreshAll)));
							fAlreadyRemovedAllThisRun = false;
						} else {
							// the entire document is being reconciled
							// (strategies may be called multiple times)
							if (!fAlreadyRemovedAllThisRun) {
								annotationsToRemove.addAll(Arrays.asList(getAllAnnotationsToRemove()));
								fAlreadyRemovedAllThisRun = true;
							}
							annotationsToAdd.addAll(Arrays.asList(validatorStep.reconcile(dr, dr, true)));
						}
						//smartProcess(annotationsToRemove,
						// annotationsToAdd);
						/////////////////////////////////////////////////////////////////////////////
					}
					// remove/add if there is anything to remove/add
					if (annotationsToRemove.size() + annotationsToAdd.size() > 0)
						smartProcess((TemporaryAnnotation[]) annotationsToRemove.toArray(new TemporaryAnnotation[annotationsToRemove.size()]), (IReconcileResult[]) annotationsToAdd.toArray(new IReconcileResult[annotationsToAdd.size()]));
				}
			}
		}
	}

	/**
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.AbstractStructuredTextReconcilingStrategy#setDocument(org.eclipse.jface.text.IDocument)
	 */
	public void setDocument(IDocument document) {

		super.setDocument(document);
		// validator steps are in "fIdToStepMap" (as opposed to fFirstStep >
		// next step etc...)
		Iterator it = fIdToStepMap.values().iterator();
		IReconcileStep step = null;
		while (it.hasNext()) {
			step = (IReconcileStep) it.next();
			step.setInputModel(new DocumentAdapter(document));
		}
	}
}
