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
package org.eclipse.wst.sse.ui.internal.reconcile;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ValidatorStrategy;

/**
 * Adds default and validator strategies.
 * Adds DirtyRegion processing logic.
 * 
 * @author pavery
 */
public class StructuredRegionProcessorExtension extends StructuredRegionProcessor {

    /** strategy called for unmapped partitions */
    private IReconcilingStrategy fDefaultStrategy;
    
    /**
     * the strategy that runs validators contributed via reconcileValidator
     * extension point
     */
    private ValidatorStrategy fValidatorStrategy;
    
    /**
     * @return Returns the fDefaultStrategy.
     */
    public IReconcilingStrategy getDefaultStrategy() {
        return fDefaultStrategy;
    }
    
    /**
     * @see org.eclipse.wst.sse.ui.internal.reconcile.DirtyRegionProcessor#getAppropriateStrategy(org.eclipse.jface.text.reconciler.DirtyRegion)
     */
    protected IReconcilingStrategy getStrategy(DirtyRegion dirtyRegion) {
        IReconcilingStrategy strategy = super.getStrategy(dirtyRegion);
        if(strategy == null)
            strategy = getDefaultStrategy();
        return strategy;
    }
    /**
     * @return Returns the fValidatorStrategy.
     */
    public ValidatorStrategy getValidatorStrategy() {
        return fValidatorStrategy;
    }
    
    /**
     * @param dirtyRegion
     */
    protected void process(DirtyRegion dirtyRegion) {        
        if (!isInstalled())
            return;
       
        ITypedRegion[] tr = computePartitioning(dirtyRegion);
        IReconcilingStrategy s = null;
        DirtyRegion dirty = null;
        for (int i = 0; i < tr.length; i++) {
            
            dirty = createDirtyRegion(tr[i], DirtyRegion.INSERT);
            s = getReconcilingStrategy(tr[i].getType());
            if (s != null && dirty != null)
                s.reconcile(dirty, dirty);

            // validator for this partition
            if (fValidatorStrategy != null)
                fValidatorStrategy.reconcile(tr[i], dirty, false);
        }
    }
    /**
     * @param defaultStrategy The fDefaultStrategy to set.
     */
    public void setDefaultStrategy(IReconcilingStrategy defaultStrategy) {
        fDefaultStrategy = defaultStrategy;
        if(fDefaultStrategy != null) {
            fDefaultStrategy.setDocument(getDocument());
            if (fDefaultStrategy instanceof IReconcilingStrategyExtension)
                ((IReconcilingStrategyExtension) fDefaultStrategy).setProgressMonitor(getLocalProgressMonitor());
        }
    }
    
    /**
     * @see org.eclipse.wst.sse.ui.internal.reconcile.DirtyRegionProcessor#setDocumentOnAllStrategies(org.eclipse.jface.text.IDocument)
     */
    protected void setDocumentOnAllStrategies(IDocument document) {
        
        super.setDocumentOnAllStrategies(document);
        
        IReconcilingStrategy defaultStrategy = getDefaultStrategy();
        IReconcilingStrategy validatorStrategy = getValidatorStrategy();
        
        // default strategies
        if (defaultStrategy != null)
            defaultStrategy.setDocument(document);

        // external validator strategy
        if (validatorStrategy != null)
            validatorStrategy.setDocument(document);
    }
    /**
     * @param validatorStrategy The fValidatorStrategy to set.
     */
    public void setValidatorStrategy(ValidatorStrategy validatorStrategy) {
        fValidatorStrategy = validatorStrategy;
        if (fValidatorStrategy != null) {
            fValidatorStrategy.setDocument(getDocument());
            fValidatorStrategy.setProgressMonitor(getLocalProgressMonitor());
        }
    }

}
