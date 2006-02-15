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
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.sse.ui.internal.IReleasable;
import org.eclipse.wst.sse.ui.internal.reconcile.DocumentAdapter;
import org.eclipse.wst.sse.ui.internal.reconcile.ReconcileAnnotationKey;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredReconcileStep;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredTextReconcilingStrategy;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;


/**
 * Special validator strategy. Runs validator steps contributed via the
 * <code>org.eclipse.wst.sse.ui.extensions.sourcevalidation</code> extension point
 * 
 * @author pavery
 */
public class ValidatorStrategy extends StructuredTextReconcilingStrategy {
	
    private String[] fContentTypeIds = null;
    /** validator id (as declared in ext point) -> ReconcileStepForValidator **/
	private HashMap fVidToVStepMap = null;
	private List fMetaData = null;

	public ValidatorStrategy(ISourceViewer sourceViewer, String contentType) {
		super(sourceViewer);
		fMetaData = new ArrayList();
		fContentTypeIds = calculateParentContentTypeIds(contentType);
		fVidToVStepMap = new HashMap();
	}

	/**
     * The content type passed in should be the most specific one.   
     * TODO: This exact method is also in ValidatorMetaData. Should be in a common place.
     * 
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
	
	protected boolean canHandlePartition(String partition) {
		return canValidatePartition(partition);
	}

	/*
	 * so that removal will work properly
	 * 
	 * @see org.eclipse.wst.sse.ui.reconcile.AbstractStructuredTextReconcilingStrategy#containsStep(org.eclipse.jface.text.reconciler.IReconcileStep)
	 */
	protected boolean containsStep(IReconcileStep step) {
		return step != null ? fVidToVStepMap.values().contains(step) : false;
	}

	/**
	 * @see org.eclipse.wst.sse.ui.internal.provisional.reconcile.AbstractStructuredTextReconcilingStrategy#createReconcileSteps()
	 */
	public void createReconcileSteps() {
		// do nothing, steps are created
	}
	/**
	 * All content types on which this ValidatorStrategy can run
	 * @return
	 */
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
		Iterator keys = fVidToVStepMap.keySet().iterator();
		String key = null;
		while (keys.hasNext()) {
			key = (String) keys.next();
			StructuredReconcileStep step = (StructuredReconcileStep) fVidToVStepMap.get(key);
			partitionTypes.addAll(Arrays.asList(step.getPartitionTypes()));
		}
		return (String[]) partitionTypes.toArray(new String[partitionTypes.size()]);
	}
	/**
	 * @param tr Partition of the region to reconcile.
	 * @param dr Dirty region representation of the typed region
	 */
	public void reconcile(ITypedRegion tr, DirtyRegion dr) {
		
		if(isCanceled())
			return;
		
		IDocument doc = getDocument();
		// for external files, this can be null
		if (doc == null) 
			return;
			
		String partitionType = tr.getType();
		if (canValidatePartition(partitionType)) {
			ValidatorMetaData vmd = null;

			// IReconcileResult[]
			ArrayList annotationsToAdd = new ArrayList();
            // loop all of the relevant validator meta data
            // to find new annotations
			for (int i = 0; i < fMetaData.size() && !isCanceled(); i++) {
				vmd = (ValidatorMetaData) fMetaData.get(i);
				if (vmd.canHandleParitionType(getContentTypeIds(), partitionType)) {
					// get step for partition type
					Object o = fVidToVStepMap.get(vmd.getValidatorId());
					ReconcileStepForValidator validatorStep = null;
					if (o != null) {
						validatorStep = (ReconcileStepForValidator) o;
					} else {
						// if doesn't exist, create one
						IValidator validator = vmd.createValidator();
						validatorStep = new ReconcileStepForValidator(validator, vmd.getValidatorScope());
						validatorStep.setInputModel(new DocumentAdapter(doc));

						fVidToVStepMap.put(vmd.getValidatorId(), validatorStep);
					}
					annotationsToAdd.addAll(Arrays.asList(validatorStep.reconcile(dr, dr)));
				}
			}
            
            TemporaryAnnotation[] annotationsToRemove= getAnnotationsToRemove(dr);   
            if (annotationsToRemove.length + annotationsToAdd.size() > 0)
                smartProcess(annotationsToRemove, (IReconcileResult[]) annotationsToAdd.toArray(new IReconcileResult[annotationsToAdd.size()]));
  
		}
	}

    /**
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.AbstractStructuredTextReconcilingStrategy#setDocument(org.eclipse.jface.text.IDocument)
	 */
	public void setDocument(IDocument document) {
		
		super.setDocument(document);
		
		// validator steps are in "fVIdToVStepMap" (as opposed to fFirstStep >
		// next step etc...)
		Iterator it = fVidToVStepMap.values().iterator();
		IReconcileStep step = null;
		while (it.hasNext()) {
			step = (IReconcileStep) it.next();
			step.setInputModel(new DocumentAdapter(document));
		}
	}
	
	public void release() {
		super.release();
		Iterator it = fVidToVStepMap.values().iterator();
		IReconcileStep step = null;
		while (it.hasNext()) {
			step = (IReconcileStep) it.next();
			if(step instanceof IReleasable)
				((IReleasable)step).release();
		}
	}
	/**
	 * 
	 * @param partitionType
	 * @return true if all validators associated with this 
	 * 			parition type are total scope, otherwise return false
	 */
	public boolean allTotalScope(String partitionType) {
		Iterator vmds = fMetaData.iterator();
		while(vmds.hasNext()) {
			ValidatorMetaData vmd = (ValidatorMetaData)vmds.next();
			if(vmd.canHandleParitionType(getContentTypeIds(), partitionType)) {
				if(vmd.getValidatorScope() ==  ReconcileAnnotationKey.PARTIAL)
					return false;
			}
		}
		return true;
	}
	
	public boolean isTotalScope() {
		return true;
	}
}
