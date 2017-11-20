/*******************************************************************************
 * Copyright (c) 2007, 2009 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - externalize strings
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.validation;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.ui.internal.validation.DelegatingSourceValidator;
import org.eclipse.wst.xsl.ui.internal.editor.XSLEditor;

/**
 * This performs the as-you-type validation for xsl files
 * 
 */
public class DelegatingSourceValidatorForXSL extends DelegatingSourceValidator
{
	private static final String XSL_UI_XSL_EDITOR_ID = "org.eclipse.wst.xsl.ui.XSLEditor"; //$NON-NLS-1$
	private final static String Id = "org.eclipse.wst.xsl.core.xsl"; //$NON-NLS-1$
	private final String QUICKASSISTPROCESSOR = IQuickAssistProcessor.class.getName();
	
	private Validator _validator;

	/**
	 * Constructor
	 */
	public DelegatingSourceValidatorForXSL()
	{
		super();
	}
	
	@Override
	protected void updateValidationMessages(List messages, IDOMDocument document, IReporter reporter)
	{
		super.updateValidationMessages(messages, document, reporter);
		// insert the quick assist information as an attribute for the messages
//		for (Object msg : reporter.getMessages())
//		{
//			IMessage message = (IMessage)msg;
//
//			ValidationQuickAssist processor = new ValidationQuickAssist();
//			
//			String id = (String)message.getAttribute("validationErrorId");
//			processor.setProblemId(id);
//			
//			message.setAttribute(QUICKASSISTPROCESSOR, processor);
//		}		
	}

	@Override
	public void validate(IValidationContext helper, IReporter reporter) throws ValidationException
	{
		super.validate(helper, reporter);
		
		// validating will refresh the model, so now calculate the overrides.
		// (we only calculate overrides for source validation as we only want to do it for files open in an editor)
		// There follows a very complicated way of creating the required annotations in an editor.
		// TODO resolve this when bug 247222 is sorted
		
		String[] delta = helper.getURIs();
		if (delta.length > 0)
		{
			IFile file = getFile(delta[0]);
			// find any files with open editors...
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			for (IWorkbenchWindow workbenchWindow : windows)
			{
				IWorkbenchPage page = workbenchWindow.getActivePage();
				if (page != null)
				{
					IEditorReference[] refs = page.findEditors(new FileEditorInput(file), XSL_UI_XSL_EDITOR_ID, IWorkbenchPage.MATCH_ID | IWorkbenchPage.MATCH_INPUT);
					// lets hope we only have one XSL editor open on the file, or else we don't know which one started this validation...
					if (refs.length == 1)
					{
						XSLEditor editor = (XSLEditor) refs[0].getEditor(false);
						if (editor != null)
						{// all this work just to get here...
							editor.getOverrideIndicatorManager().updateAnnotations();
						}
					}
				}
			}
		}
	}

	private Validator getValidator()
	{
		if (_validator == null)
			_validator = ValidationFramework.getDefault().getValidator(Id, null);
		return _validator;
	}

	@Override
	protected IValidator getDelegateValidator()
	{
		Validator v = getValidator();
		if (v == null)
			return null;
		return v.asIValidator();
	}
}
