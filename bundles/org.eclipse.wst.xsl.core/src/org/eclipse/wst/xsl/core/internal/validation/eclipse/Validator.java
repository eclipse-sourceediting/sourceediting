/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver - STAR - bug 224777 - fix spaces in path names in URIs.
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal.validation.eclipse;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.uriresolver.internal.util.URIEncoder;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.internal.XSLCorePlugin;
import org.eclipse.wst.xsl.core.internal.model.XSLAttribute;
import org.eclipse.wst.xsl.core.internal.model.XSLNode;
import org.eclipse.wst.xsl.core.internal.validation.XSLValidationMessage;
import org.eclipse.wst.xsl.core.internal.validation.XSLValidator;

/**
 * TODO: Add Javadoc
 * 
 * @author Doug Satchwell
 * 
 */
public class Validator extends AbstractNestedValidator
{

/*	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor)
	{
		// TODO this method is NOT being called! Why?
		ValidationResult res = super.validate(resource, kind, state, monitor);
		if (resource.getType() == IResource.FILE)
		{
			StylesheetModel stylesheet = XSLCore.getInstance().getStylesheet((IFile) resource);
			IFile[] dependencies = stylesheet.getFiles().toArray(new IFile[0]);
			res.setDependsOn(dependencies);
		}
		// TODO clean project (when project == null)
		return res;
	} */

	public ValidationReport validate(final String uri, InputStream inputstream, NestedValidatorContext context)
	{
		ValidationReport valreport = new ValidationReport(){

			@Override
			public String getFileURI()
			{
				// TODO Auto-generated method stub
				return uri;
			}

			@Override
			public HashMap getNestedMessages()
			{
				// TODO Auto-generated method stub
				return new HashMap();
			}

			@Override
			public ValidationMessage[] getValidationMessages()
			{
				// TODO Auto-generated method stub
				return new ValidationMessage[0];
			}

			@Override
			public boolean isValid()
			{
				// TODO Auto-generated method stub
				return true;
			}};
		try
		{
			String encUri = URIEncoder.encode(uri);
			IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(new URI(encUri));
			if (files.length > 0)
			{
				IFile xslFile = files[0];
				// FIXME this guard should be unnecessary!!
				if (XSLCore.isXSLFile(xslFile))
					valreport = XSLValidator.getInstance().validate(xslFile);
			}
		}
		catch (URISyntaxException e)
		{
			XSLCorePlugin.log(e);
		}
		catch (CoreException e)
		{
			XSLCorePlugin.log(e);
		}
		return valreport;
	}

	@Override
	protected void addInfoToMessage(ValidationMessage validationMessage, IMessage message)
	{
		XSLValidationMessage msg = (XSLValidationMessage) validationMessage;
		XSLNode node = msg.getNode();
		// constants are defined in org.eclipse.wst.xml.ui.internal.validation.DelegatingSourceValidator
		if (node.getNodeType() == XSLNode.ATTRIBUTE_NODE)
		{
			message.setAttribute("ERROR_SIDE", "ERROR_SIDE_RIGHT");
			message.setAttribute(COLUMN_NUMBER_ATTRIBUTE, new Integer(validationMessage.getColumnNumber()));
			message.setAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE, "ATTRIBUTE_VALUE"); // whether to squiggle the element, attribute or text
			message.setAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE, ((XSLAttribute) node).getName());
		}
		else if (node.getNodeType() == XSLNode.ELEMENT_NODE)
		{
			message.setAttribute("ERROR_SIDE", "ERROR_SIDE_RIGHT");
			message.setAttribute(COLUMN_NUMBER_ATTRIBUTE, new Integer(validationMessage.getColumnNumber()));
			message.setAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE, "START_TAG"); // whether to squiggle the element, attribute or text
		}
	}
}
