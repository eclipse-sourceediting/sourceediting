/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal.validation.eclipse;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsl.core.XSLCorePlugin;
import org.eclipse.wst.xsl.core.internal.validation.XSLValidator;

/**
 * TODO: Add Javadoc
 * @author Doug Satchwell
 *
 */
public class Validator extends AbstractNestedValidator {
	
	public ValidationReport validate(String uri, InputStream inputstream, NestedValidatorContext context) {
		ValidationReport valreport = null;
		try {
			IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(new URI(uri));
			if (files.length > 0) {
				IFile xslFile = files[0];
				valreport = XSLValidator.getInstance().validate(uri, xslFile);
			}
		} catch (URISyntaxException e) {
			XSLCorePlugin.log(e);
		} catch (CoreException e) {
			XSLCorePlugin.log(e);
		}
		return valreport;
	}

	// TODO which attributes to set
	protected void addInfoToMessage(ValidationMessage validationMessage, IMessage message) {
		// The value constants are kept in DelegatingSourceValidator!

		// TODO select attribute or element squiggling depending on type of error
		String key = validationMessage.getKey();
		message.setAttribute("ERROR_SIDE", "ERROR_SIDE_RIGHT");
		message.setAttribute(COLUMN_NUMBER_ATTRIBUTE, new Integer(validationMessage.getColumnNumber()));
		message.setAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE, "START_TAG"); // whether to squiggle the element, attribute or text
	//	message.setAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE, "name"); // name of attribute to underline
	}
}
