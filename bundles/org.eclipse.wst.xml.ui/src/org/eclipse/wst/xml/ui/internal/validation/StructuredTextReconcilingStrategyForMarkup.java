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
package org.eclipse.wst.xml.ui.internal.validation;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.sse.ui.internal.reconcile.StructuredTextReconcilingStrategy;


/**
 * 
 * @author pavery
 *  
 */
public class StructuredTextReconcilingStrategyForMarkup extends StructuredTextReconcilingStrategy {

	public StructuredTextReconcilingStrategyForMarkup(ISourceViewer sourceViewer) {
		super(sourceViewer);
	}

	public void createReconcileSteps() {
		// only one step, to check syntax
		setFirstStep(new ReconcileStepForMarkup());
	}
}
