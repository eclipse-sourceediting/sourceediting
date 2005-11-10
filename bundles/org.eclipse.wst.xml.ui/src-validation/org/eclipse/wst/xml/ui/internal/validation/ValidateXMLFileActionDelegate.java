/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.ui.internal.validation;
                                       
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionDelegate;

/**
 * A delegate to invoke XML validation from the context menu's Validate XML option.
 * 
 * @author Craig Saler, IBM
 * @author Lawrence Mandel, IBM
 */
public class ValidateXMLFileActionDelegate implements IActionDelegate
{
  protected ISelection selection;

  /* (non-Javadoc)
   * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
   */
  public void run(IAction action)
  {
    IFile fileResource = null;
    try
 {
      // CS.. for now the following line tests to ensure the user has xerces jars installed
      // so that we can perform some 'fail fast' behaviour
      //
      Class theClass = Class.forName("org.apache.xerces.xni.parser.XMLParserConfiguration", true, this.getClass().getClassLoader()); //$NON-NLS-1$
      if (theClass == null)
      {
       throw(new Exception("Missing Xerces jars in plugin's 'jars' folder"));        //$NON-NLS-1$
      }
        
    if (!selection.isEmpty() && selection instanceof IStructuredSelection)
    {
      IStructuredSelection structuredSelection = (IStructuredSelection) selection;
      Object element = structuredSelection.getFirstElement();

      if (element instanceof IFile)
      {
        fileResource = (IFile) element;
      }
      else
      {
        return;
      }
    }
          
    if (fileResource != null)
    {            
      //IProject project = fileResource.getProject();
      //URIResolver resolver = (URIResolver) project.getAdapter(URIResolver.class);
 // if (resolver == null)
  // resolver = new ProjectResolver(project);
  //resolver.setFileBaseLocation(fileResource.getLocation().toString());
  //return resolver;
      ValidateAction validateAction = new ValidateAction(fileResource, true);
      validateAction.setValidator(new Validator());
      validateAction.run();
    }
 }
    catch (Exception e)
 {
      // CS... here's where we need to pop up a dialog to tell the user that xerces is not available
      //
      String xercesLine1 = XMLValidationUIMessages.ValidateXMLFileActionDelegate_2;
      String xercesLine2 = XMLValidationUIMessages.ValidateXMLFileActionDelegate_3;
      String xercesLine3 = XMLValidationUIMessages.ValidateXMLFileActionDelegate_4;
      MessageDialog.openError(Display.getDefault().getActiveShell(), XMLValidationUIMessages.ValidateXMLFileActionDelegate_5, xercesLine1 + xercesLine2 + xercesLine3);
 }
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
   */
  public void selectionChanged(IAction action, ISelection selection)
  {
    this.selection = selection;
  }   
}