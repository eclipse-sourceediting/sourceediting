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

import org.eclipse.jface.text.reconciler.IReconcileStep;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.ui.internal.reconcile.AbstractStructuredTextReconcilingStrategy;

/**
 * 
 * @author pavery
 */
public class StructuredTextReconcilingStrategyForJSP extends AbstractStructuredTextReconcilingStrategy {

	public StructuredTextReconcilingStrategyForJSP(ITextEditor editor) {
		super(editor);
	}

	public void createReconcileSteps() {

		// the order is:
		// 1. translation step
		// 2. java step
		if (getFile() != null) {
			IReconcileStep javaStep = new ReconcileStepForJava(getFile());
			fFirstStep = new ReconcileStepForJspTranslation(javaStep);
		}
	}
	
	/**
	 * @return <code>true</code> if the entire document is validated
	 * for each edit (this strategy can't handle partial document validation).
	 * This will greatly help performance.
	 */
	public boolean isTotalScope() {
		return true;
	}
}