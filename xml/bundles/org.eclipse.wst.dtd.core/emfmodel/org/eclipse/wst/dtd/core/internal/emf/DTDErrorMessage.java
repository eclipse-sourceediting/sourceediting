/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.emf;

public class DTDErrorMessage {
	public String errorKey;
	public String errorMessage;
	public String lineNo;
	public String charOffSet;
	public String declarationType;
	public String declarationName;
	public String sourceLine;


	public DTDErrorMessage(String declarationType, String declarationName, String lineNo, String charOffSet, String errorKey, String errorMessage) {
		this(declarationType, declarationName, lineNo, charOffSet, errorKey, errorMessage, null);
	}

	public DTDErrorMessage(String declarationType, String declarationName, String lineNo, String charOffSet, String errorKey, String errorMessage, String srcLine) {
		this.declarationType = declarationType;
		this.declarationName = declarationName;
		this.lineNo = lineNo;
		this.charOffSet = charOffSet;
		this.errorKey = errorKey;
		this.errorMessage = errorMessage;
		this.sourceLine = srcLine;
	}

	public String toString() {
		return "(" + declarationType + ":" + declarationName + ") " + lineNo + ":" + charOffSet + " " + errorMessage; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}

	/**
	 * @generated
	 */
	protected String toStringGen() {

		return "(" + declarationType + ":" + declarationName + ") " + lineNo + ":" + charOffSet + " " + errorMessage; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}
}
