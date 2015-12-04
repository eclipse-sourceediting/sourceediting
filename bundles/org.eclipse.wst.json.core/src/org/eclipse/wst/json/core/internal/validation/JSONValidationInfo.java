/*******************************************************************************
 * Copyright (c) 2001, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver - STAR - [205989] - [validation] validate XML after XInclude resolution
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.validation.XMLValidationInfo
 *                                           modified in order to process JSON Objects.                                                    
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.validation;

import org.eclipse.json.IValidationReporter;
import org.eclipse.wst.json.core.internal.validation.core.ValidationInfo;
import org.eclipse.wst.json.core.validation.AnnotationMsg;

public class JSONValidationInfo extends ValidationInfo implements
		JSONValidationReport, IValidationReporter {

	/**
	 * Constructor.
	 * 
	 * @param uri
	 *            The URI of the file this report describes.
	 */
	public JSONValidationInfo(String uri) {
		super(uri);
	}

	@Override
	public boolean isGrammarEncountered() {
		return false;
	}

	@Override
	public void addMessage(String message, int line, int column, int offset) {
		boolean isEndOfText = message.startsWith("Unexpected end of input");		
		AnnotationMsg annotation = new AnnotationMsg(
				ProblemIDsJSON.MissingEndBracket, null, 1);//isEndOfText ? 1 : 1);
		if (line > 1 && column == 0) {
			
		}
		int o = offset -1; //isEndOfText ? offset - 1 : offset -1;// -1;// - ( (line -2)* 2);
		// if (column == 0) o=o-2;
		super.addError(message, line - 1, o, getFileURI(), "null",
				new Object[] { annotation });
	}
}
