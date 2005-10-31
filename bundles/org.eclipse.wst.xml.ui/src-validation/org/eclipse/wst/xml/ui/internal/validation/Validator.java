/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.ui.internal.validation;


import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.IRuleGroup;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;

/**
 * A validator to plug the XML validator into the validation framework.
 * 
 * @author Craig Salter, IBM
 * @author Lawrence Mandel, IBM
 */
public class Validator implements IValidator
{
	class LocalizedMessage extends Message {
		private String _message = null;

		public LocalizedMessage(int severity, String messageText) {
			this(severity, messageText, null);
		}

		public LocalizedMessage(int severity, String messageText, IResource targetObject) {
			this(severity, messageText, (Object) targetObject);
		}

		public LocalizedMessage(int severity, String messageText, Object targetObject) {
			super(null, severity, null);
			setLocalizedMessage(messageText);
			setTargetObject(targetObject);
		}

		public void setLocalizedMessage(String message) {
			_message = message;
		}

		public String getLocalizedMessage() {
			return _message;
		}

		public String getText() {
			return getLocalizedMessage();
		}

		public String getText(ClassLoader cl) {
			return getLocalizedMessage();
		}

		public String getText(Locale l) {
			return getLocalizedMessage();
		}

		public String getText(Locale l, ClassLoader cl) {
			return getLocalizedMessage();
		}
	}
	
  private final String GET_FILE = "getFile";
  public final String GET_PROJECT_FILES = "getAllFiles";

	static boolean shouldValidate(IFile file) {
		IResource resource = file;
		do {
			if (resource.isDerived() || resource.isTeamPrivateMember() || !resource.isAccessible() || resource.getName().charAt(0) == '.') {
				return false;
			}
			resource = resource.getParent();
		}
		while ((resource.getType() & IResource.PROJECT) == 0);
		return true;
	}

  /**
   * Validate the given file.
   * 
   * @param file The file to validate.
   */
  public void validate(IFile file)
  {
    ValidateAction validateAction = new ValidateAction(file, false);
    validateAction.setValidator(this);
    validateAction.run();	
  }
  public void validate(IFile file, InputStream inputStream, IReporter reporter)
  {
    ValidateAction validateAction = new ValidateAction(file, false);
    validateAction.setValidator(this);
    if (inputStream != null)
    {
      validateAction.setInputStream(inputStream);
      validateAction.setReporter(reporter);
    }
    validateAction.run(); 
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.wtp.validation.core.IValidator#validate(org.eclipse.wtp.validation.core.IHelper, org.eclipse.wtp.validation.core.IReporter, org.eclipse.wtp.validation.core.IFileDelta[])
   */
  public void validate(IValidationContext helper, IReporter reporter) throws ValidationException {
	 String[] fileURIs = helper.getURIs();
     if (fileURIs != null && fileURIs.length > 0) 
    {
      for (int i = 0; i < fileURIs.length; i++) 
      {
        String fileName = fileURIs[i];
        if (fileName != null)
        {          
          Object []parms = {fileName};

          IFile file = (IFile) helper.loadModel(GET_FILE, parms);
          if (file != null && shouldValidate(file)) 
          { //the helper might not have the file stored in it. could have an InputStream
            if (helper.loadModel("inputStream") instanceof InputStream)
            {
              validate(file, (InputStream)helper.loadModel("inputStream"), reporter); //do we need the fileName?  what is int ruleGroup?
            }
            else
            {
              //if (((Helper)helper).isInJavaBuildPath(file) &&
              //    !((WorkbenchContext)helper).isInJavaSourcePath(file))
              //{
              //  continue;
              //}
              validateIfNeeded(file, helper, reporter);
            }
          }
        }
      }
    }
    else 
    {
      Object []parms = {this.getClass().getName()};
      Collection files = (Collection) helper.loadModel(GET_PROJECT_FILES, parms);
      Iterator iter = files.iterator();
      while (iter.hasNext()) 
      {
        IFile file = (IFile) iter.next();
        validateIfNeeded(file, helper, reporter);
      }
    }
  }
  
  /**
   * Validate the given file and use the reporter for the validation messages.
   * 
   * @param file The file to validate.
   * @param reporter The reporter to report the validation messages.
   * @param ruleGroup
   */
  public void validate(IFile file, IReporter reporter, int ruleGroup)
  {  
    ValidateAction validateAction = new ValidateAction(file, false);
    validateAction.setValidator(this);
    validateAction.run();  
  }
  
  /**
   * Validate the given file if validation is required.
   * 
   * @param file The file to validate.
   * @param helper An aid for the validation.
   * @param reporter The reporter to report the validation messages.
   */
  protected void validateIfNeeded(IFile file, IValidationContext helper, IReporter reporter)
  {
    Message message = new LocalizedMessage(IMessage.LOW_SEVERITY, NLS.bind(XMLUIMessages.MESSAGE_XML_VALIDATION_MESSAGE_UI_, new String[]{file.getFullPath().toString()}));
    reporter.displaySubtask(this, message);

    Integer ruleGroupInt = (Integer)helper.loadModel(IRuleGroup.PASS_LEVEL, null); // pass in a "null" so that loadModel doesn't attempt to cast the result into a RefObject
    int ruleGroup = (ruleGroupInt == null) ? IRuleGroup.PASS_FULL : ruleGroupInt.intValue();

    validate(file, reporter, ruleGroup);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.wtp.validation.core.IValidator#cleanup(org.eclipse.wtp.validation.core.IReporter)
   */
  public void cleanup(IReporter reporter)
  {
	// Nothing to do.
  }
}
