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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.internal.XSLCorePlugin;
import org.eclipse.wst.xsl.core.internal.model.StylesheetModel;
import org.eclipse.wst.xsl.core.internal.model.XSLAttribute;
import org.eclipse.wst.xsl.core.internal.model.XSLNode;
import org.eclipse.wst.xsl.core.internal.validation.XSLValidationMessage;
import org.eclipse.wst.xsl.core.internal.validation.XSLValidator;

/**
 * TODO: Add Javadoc
 * @author Doug Satchwell
 *
 */
public class Validator extends AbstractNestedValidator {
	
	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor)
	{
		// TODO this method is NOT being called! Why?
		ValidationResult res = super.validate(resource, kind, state, monitor);
		if (resource.getType() == IResource.FILE)
		{
			StylesheetModel stylesheet = XSLCore.getInstance().getStylesheet((IFile)resource);
			IFile[] dependencies = stylesheet.getFiles().toArray(new IFile[0]);
			res.setDependsOn(dependencies);
		}
		// TODO clean project (when project == null)
		return res;
	}
	
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

	protected void addInfoToMessage(ValidationMessage validationMessage, IMessage message) {
		XSLValidationMessage msg = (XSLValidationMessage)validationMessage;
		XSLNode node = msg.getNode();
		// constants are defined in org.eclipse.wst.xml.ui.internal.validation.DelegatingSourceValidator
		if (node.getNodeType() == XSLNode.ATTRIBUTE_NODE)
		{
			message.setAttribute("ERROR_SIDE", "ERROR_SIDE_RIGHT");
			message.setAttribute(COLUMN_NUMBER_ATTRIBUTE, new Integer(validationMessage.getColumnNumber()));
			message.setAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE, "ATTRIBUTE_VALUE");  // whether to squiggle the element, attribute or text
			message.setAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE, ((XSLAttribute)node).getName());
		}
		else if (node.getNodeType() == XSLNode.ELEMENT_NODE)
		{
			message.setAttribute("ERROR_SIDE", "ERROR_SIDE_RIGHT");
			message.setAttribute(COLUMN_NUMBER_ATTRIBUTE, new Integer(validationMessage.getColumnNumber()));
			message.setAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE, "START_TAG");  // whether to squiggle the element, attribute or text
		}
	}
}
