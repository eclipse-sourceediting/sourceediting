/*******************************************************************************
 * Copyright (c) 2006, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.validation.errorcustomization;

/**
 * A sample error message customizer for testing. This customizer has a simple
 * implementation that simple returns the string "AAAA".
 */
public class SampleErrorMessageCustomizer implements IErrorMessageCustomizer {

	/* (non-Javadoc)
	 * @see org.eclipse.wst.xml.core.internal.validation.errorcustomization.IErrorMessageCustomizer#customizeMessage(org.eclipse.wst.xml.core.internal.validation.errorcustomization.ElementInformation, java.lang.String, java.lang.Object[])
	 */
	public String customizeMessage(ElementInformation elementInfo,
			String errorKey, Object[] arguments) {
		// TODO Auto-generated method stub
		return "AAAA";
	}

}
