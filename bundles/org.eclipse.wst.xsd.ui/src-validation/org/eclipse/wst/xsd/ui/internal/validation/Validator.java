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

package org.eclipse.wst.xsd.ui.internal.validation;


import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.IRuleGroup;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;


public class Validator implements IValidator
{
  private final String GET_FILE = "getFile";
  public final String GET_PROJECT_FILES = "getAllFiles";

  public void validate(IFile file)
  {
    ValidateAction validateAction = new ValidateAction(file, false);
    validateAction.setValidator(this);
    validateAction.run();	
  }
  /**
   * This is the method which performs the validation on the MOF model.
   * <br><br>
   * <code>helper</code> and <code>reporter</code> may not be null. <code>changedFiles</code> may be null, if a full
   * build is desired.
   * <br><br>
   * <code>helper</code> returns the ifile for the given information in the IFileDelta array
   * <br><br>
   * <code>reporter</code> is an instance of an IReporter interface, which is used for interaction with the user.
   * <br><br>
   * <code>changedFiles</code> is an array of file names which have changed since the last validation. 
   * If <code>changedFiles</code> is null, or if it is an empty array, then a full build
   * is performed. Otherwise, validation on just the files listed in the Vector is performed.
   */
  public void validate(IValidationContext helper, IReporter reporter) throws ValidationException {
	 String[] changedFiles = helper.getURIs();
     if (changedFiles != null && changedFiles.length > 0) 
    {
      for (int i = 0; i < changedFiles.length; i++) 
      {
        String fileName = changedFiles[i];
        if (fileName != null)
        {
          Object []parms = {fileName};

          IFile file = (IFile) helper.loadModel(GET_FILE, parms);
          if (file != null && shouldValidate(file)) 
          {            
            //the helper might not have the file stored in it. could have an InputStream          
            if (helper.loadModel("inputStream") instanceof InputStream)
            {
              validate(file, (InputStream)helper.loadModel("inputStream"), reporter); //do we need the fileName?  what is int ruleGroup?
            }                    
            else
            {  
              //if (((Helper)helper).isInJavaBuildPath(file) &&
              //    !((AWorkbenchHelper)helper).isInJavaSourcePath(file))
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
        if(shouldValidate(file))
        {
          validateIfNeeded(file, helper, reporter);
        }
      }
    }
  }
  
 
  private void validate(IFile file, InputStream inputStream, IReporter reporter)
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
  
  protected void validateIfNeeded(IFile file, IValidationContext helper, IReporter reporter)
  {
    ValidatorManager mgr = ValidatorManager.getManager();
 
    Integer ruleGroupInt = (Integer)helper.loadModel(IRuleGroup.PASS_LEVEL, null); // pass in a "null" so that loadModel doesn't attempt to cast the result into a RefObject
    int ruleGroup = (ruleGroupInt == null) ? IRuleGroup.PASS_FULL : ruleGroupInt.intValue();

    Object stream = helper.loadModel("inputStream");
    if (stream instanceof InputStream)
    {
      validate(file, (InputStream)stream, reporter);      
    } 
    else
    {  
      validate(file, null, reporter);
    }  
  }
  
  /**
   * Unpacks the fileModelPair and returns an IFile object.
   */
  //protected IFile getFile(Object object)
  //{
  //  IFile result = null;
  //  if (object instanceof List)
  //  {
  //    List fileModelPair = (List)object;
  //    if (fileModelPair.size()>0)
  //    {
  //      Object file = fileModelPair.get(0);
  //      if (file instanceof IFile)
  //      {
  ///        result = (IFile)file;
  //      }
  //    }
  //  }
  //  return result;
 // } 
  
  /* (non-Javadoc)
   * @see org.eclipse.wtp.validation.core.IValidator#cleanup(org.eclipse.wtp.validation.core.IReporter)
   */
  public void cleanup(IReporter reporter)
  {
  }

  boolean shouldValidate(IFile file) {
    IResource resource = file;
    do {
      if (resource.isDerived() || resource.isTeamPrivateMember() || !resource.isAccessible() || resource.getName().charAt(0) == '.') {
        return false;
      }
      resource = resource.getParent();
    }
	while ((resource.getType() & IResource.PROJECT) == 0 && (resource.getType() & IResource.ROOT) == 0);
	return true;
  }
}
