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

import org.eclipse.jface.text.reconciler.IReconcilableModel;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationExtension;

/**
 * Wrapper class to make a JSPTranslation into an IReconcilableModel.
 * Translation must be accessed via getTranslation()
 * 
 * @pavery
 */
public class JSPTranslationWrapper implements IReconcilableModel {
	private JSPTranslationExtension fTranslation = null;

	public JSPTranslationWrapper(JSPTranslationExtension trans) {
		fTranslation = trans;
	}

	public JSPTranslationExtension getTranslation() {
		return fTranslation;
	}
}