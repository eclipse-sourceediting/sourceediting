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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsl.core.XSLCorePlugin;
import org.eclipse.wst.xsl.core.internal.validation.XSLValidator;

public class Validator extends AbstractNestedValidator {

	public Validator() {
		System.out.println("Validator ctor");
	}

	private IDOMModel getModelForResource(IFile file) throws IOException,
			CoreException {
		IStructuredModel model = null;
		IModelManager manager = StructuredModelManager.getModelManager();
		model = manager.getModelForRead(file);
		return model instanceof IDOMModel ? (IDOMModel) model : null;
	}

	public ValidationReport validate(String uri, InputStream inputstream,
			NestedValidatorContext context) {
		ValidationReport valreport = null;
		try {
			IFile[] files = ResourcesPlugin.getWorkspace().getRoot()
					.findFilesForLocationURI(new URI(uri));
			if (files.length > 0) {
				IFile xslFile = files[0];
				IDOMModel model = getModelForResource(xslFile);
				if (model != null)
					valreport = XSLValidator.getInstance().validate(uri,
							xslFile, model.getDocument());
			}
		} catch (URISyntaxException e) {
			XSLCorePlugin.log(e);
		} catch (IOException e) {
			XSLCorePlugin.log(e);
		} catch (CoreException e) {
			XSLCorePlugin.log(e);
		}
		return valreport;
	}

	// TODO whats this for?
	protected void addInfoToMessage(ValidationMessage validationMessage,
			IMessage message) {
		String key = validationMessage.getKey();
		message.setAttribute(COLUMN_NUMBER_ATTRIBUTE, new Integer(
				validationMessage.getColumnNumber()));
		message.setAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE, "hello");
		message.setAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE, "world");
	}
}
