/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.validation.XMLValidationReport
 *                                           modified in order to process JSON Objects.       
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.validation;

import org.eclipse.wst.json.core.internal.validation.core.ValidationReport;

/**
 * An interface represention a validation report for JSON validation.
 *
 */
public interface JSONValidationReport extends ValidationReport {

	/**
	 * Returns whether a grammar was encountered during the validation.
	 * 
	 * @return True if a grammar was encountered, false otherwise.
	 */
	public boolean isGrammarEncountered();

}
