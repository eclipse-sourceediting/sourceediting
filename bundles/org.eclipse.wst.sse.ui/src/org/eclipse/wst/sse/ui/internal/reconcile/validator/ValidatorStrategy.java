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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcileResult;
import org.eclipse.jface.text.reconciler.IReconcileStep;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.ui.internal.reconcile.AbstractStructuredTextReconcilingStrategy;
import org.eclipse.wst.sse.ui.internal.reconcile.DocumentAdapter;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredReconcileStep;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;
import org.eclipse.wst.validation.core.IValidator;


/**
 * Special validator strategy. Runs validator steps contributed via
 * reconcileValidator extension point
 * 
 * @author pavery
 */
public class ValidatorStrategy extends AbstractStructuredTextReconcilingStrategy {
	
    private String[] fContentTypeIds = null;
	private HashMap fIdToStepMap = null;
	private List fMetaData = null;

	public ValidatorStrategy(ITextEditor editor, String contentType) {
		super(editor);
		fMetaData = new ArrayList();
		fContentTypeIds = calculateParentContentTypeIds(contentType);
		fIdToStepMap = new HashMap();
	}

	/**
     * The content type passed in should be the most specific one.
     * @param contentType
     * @return
     */
    private String[] calculateParentContentTypeIds(String contentTypeId) {

        Set parentTypes = new HashSet();
        
        IContentTypeManager ctManager = Platform.getContentTypeManager();    
        IContentType ct = ctManager.getContentType(contentTypeId);
        String id = contentTypeId;

        while(ct != null && id != null) {
            
            parentTypes.add(id);
            ct = ctManager.getContentType(id);
            if(ct != null) {
                IContentType baseType = ct.getBaseType();
                id = (baseType != null) ? baseType.getId() : null;
            }
        }
        return (String[])parentTypes.toArray(new String[parentTypes.size()]);
    }

    public void addValidatorMetaData(ValidatorMetaData vmd) {
		fMetaData.add(vmd);
	}

    /**
     * 
     * @param partitionType
     * @return true if the strategy contains at least one ValidatorMetaData
     *          that says it can handle the partition type (for a given content type) 
     */
	public boolean canValidatePartition(String partitionType) {
		ValidatorMetaData vmd = null;
		for (int i = 0; i < fMetaData.size(); i++) {
			vmd = (ValidatorMetaData) fMetaData.get(i);
			if (vmd.canHandleParitionType(getContentTypeIds(), partitionType))
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

	public String[] getContentTypeIds() {
		return fContentTypeIds;
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
			StructuredReconcileStep step = (StructuredReconcileStep) fIdToStepMap.get(key);
			partitionTypes.addAll(Arrays.asList(step.getPartitionTypes()));
		}
		return (String[]) partitionTypes.toArray(new String[partitionTypes.size()]);
	}

	public void reconcile(ITypedRegion tr, DirtyRegion dr) {

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
					if (vmd.canHandleParitionType(getContentTypeIds(), partitionType)) {
						// get step for partition type
						Object o = fIdToStepMap.get(vmd.getValidatorId());
						ReconcileStepForValidator validatorStep = null;
						if (o != null) {
							validatorStep = (ReconcileStepForValidator) o;
						} else {
							// if doesn't exist, create one
							IValidator validator = vmd.createValidator();
							validatorStep = new ReconcileStepForValidator(validator);
							validatorStep.setInputModel(new DocumentAdapter(fDocument));

							fIdToStepMap.put(vmd.getValidatorId(), validatorStep);
						}
						annotationsToRemove.addAll(Arrays.asList(getAnnotationsToRemove(dr)));
                        annotationsToAdd.addAll(Arrays.asList(validatorStep.reconcile(dr, dr)));
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
