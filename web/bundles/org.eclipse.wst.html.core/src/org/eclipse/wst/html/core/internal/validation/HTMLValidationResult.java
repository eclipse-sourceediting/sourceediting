/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.validation;

public class HTMLValidationResult {
	private int errors = 0;
	private int warnings = 0;
	private int informations = 0;

	/**
	 */
	public HTMLValidationResult() {
	}

	/**
	 */
	public void addError() {
		this.errors++;
	}

	/**
	 */
	public void addInformation() {
		this.informations++;
	}

	/**
	 */
	public void addWarning() {
		this.warnings++;
	}

	/**
	 */
	public int getErrors() {
		return this.errors;
	}

	/**
	 */
	public int getInformations() {
		return this.informations;
	}

	/**
	 */
	public int getWarnings() {
		return this.warnings;
	}

	/**
	 */
	public boolean isValid() {
		return (this.errors == 0 && this.warnings == 0 && this.informations == 0);
	}
}
