package org.eclipse.wst.html.validation;

/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2001, 2003 - All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */

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