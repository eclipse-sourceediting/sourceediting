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
package org.eclipse.wst.xml.ui.reconcile;

import org.eclipse.jface.text.reconciler.IReconcileStep;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.ui.internal.reconcile.AbstractStructuredTextReconcilingStrategy;


/**
 * @deprecated using reconcileValidator extension point
 * @author pavery
 */
public class StructuredTextReconcilingStrategyForContentModel extends AbstractStructuredTextReconcilingStrategy {

	public StructuredTextReconcilingStrategyForContentModel(ITextEditor editor) {
		super(editor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.internal.ui.text.AbstractStructuredTextReconcilingStrategy#createReconcileSteps()
	 */
	public void createReconcileSteps() {

		IReconcileStep cmStep = new ReconcileStepForContentModel();
		fFirstStep = new ReconcileStepForMarkup(cmStep);
	}

}
