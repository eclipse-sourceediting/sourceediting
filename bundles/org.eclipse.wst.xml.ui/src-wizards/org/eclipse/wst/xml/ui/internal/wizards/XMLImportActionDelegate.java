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
package org.eclipse.wst.xml.ui.internal.wizards;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xml.uriresolver.util.URIHelper;
import org.osgi.framework.Bundle;

public class XMLImportActionDelegate implements IActionDelegate
{
  private static final String validateXSDPluginID = "org.eclipse.wst.xsd.validation";
  private static final String xsdValidatorClassName = "org.eclipse.wst.xsd.validation.internal.ui.eclipse.XSDValidator";
  
  private static final String validateDTDPluginID = "org.eclipse.wst.dtd.validation";
  private static final String dtdValidatorClassName = "org.eclipse.wst.dtd.validation.internal.ui.eclipse.DTDValidator";

  private static final String validationReportClassName = "org.eclipse.wst.xml.validation.internal.core.ValidationReport";

  
  /**
   * Checks the current selection and runs the separate browser
   * to show the content of the Readme file. This code shows how
   * to launch separate browsers that are not VA/Base desktop parts.
   *
   * @param action  the action that was performed
   */
  public void run(IAction action)
  {
    IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    ISelection selection = workbenchWindow.getSelectionService().getSelection();
    Object selectedObject = getSelection(selection);

    if (selectedObject instanceof IFile && selection instanceof IStructuredSelection)
    {
      IFile file = (IFile)selectedObject;
      IStructuredSelection structuredSelection = (IStructuredSelection)selection;

      if (file.getName().endsWith(".dtd") || file.getName().endsWith(".xsd"))
      { 
        IPath path = file.getLocation();
        String uri = URIHelper.getURIForFilePath(path.toString());
        
        boolean validationPluginsFound = false;
        
        Boolean isValid = null;

        Class[] parameterTypes = new Class[] {String.class};
        Object[] arguments = new Object[] {uri};
        
        try
        {

          if (file.getName().endsWith(".xsd"))
          {
//          Here is the Reflection equivalent way of performing the following lines
//          XSDValidator xsdValidator = XSDValidator.getInstance();
//          ValidationReport valreport = xsdValidator.validate(uri);
           
            Bundle validateXSDBundle = Platform.getBundle(validateXSDPluginID);
            if (validateXSDBundle != null)
            {
              Class xsdValidatorClass, validationReportClass;
              Object xsdValidatorObject, validationReportObject;
              
              xsdValidatorClass = validateXSDBundle.loadClass(xsdValidatorClassName);

              Method getInstanceMethod = xsdValidatorClass.getMethod("getInstance", null);
              xsdValidatorObject = getInstanceMethod.invoke(null, null);  // static and no parameters
              
              Method validateMethod = xsdValidatorClass.getMethod("validate", parameterTypes);
              validationReportObject = validateMethod.invoke(xsdValidatorObject, arguments);
              
              validationReportClass = validateXSDBundle.loadClass(validationReportClassName);              
              
              Method isValidMethod = validationReportClass.getMethod("isValid", null);
              isValid = (Boolean)isValidMethod.invoke(validationReportObject, null);

              validationPluginsFound = true; // made it this far, so declare that validation can be performed
            }
          }
          else
          {
//          Here is the Reflection equivalent way of performing the following lines            
//          DTDValidator dtdValidator = DTDValidator.getInstance();
//          ValidationReport valreport = dtdValidator.validate(uri);

            Bundle validateDTDBundle = Platform.getBundle(validateDTDPluginID);
            
            if (validateDTDBundle != null)
            {
              Class dtdValidatorClass, validationReportClass;
              Object dtdValidatorObject, validationReportObject;

              dtdValidatorClass = validateDTDBundle.loadClass(dtdValidatorClassName);
              
              Method getInstanceMethod = dtdValidatorClass.getMethod("getInstance", null);
              dtdValidatorObject = getInstanceMethod.invoke(null, null);  // static and no parameters
              
              Method validateMethod = dtdValidatorClass.getMethod("validate", parameterTypes);
              validationReportObject = validateMethod.invoke(dtdValidatorObject, arguments);
              
              validationReportClass = validateDTDBundle.loadClass(validationReportClassName);              
              
              Method isValidMethod = validationReportClass.getMethod("isValid", null);
              isValid = (Boolean)isValidMethod.invoke(validationReportObject, null);
              
              validationPluginsFound = true; // made it this far, so declare that validation can be performed
            }
          }
        }
        catch (ClassNotFoundException e)
        {
        }
        catch (NoSuchMethodException e)
        {
        }
        catch (IllegalAccessException e)
        {
        }
        catch (InvocationTargetException e)
        {
        }

        if (validationPluginsFound)
        {
          if (isValid != null && !isValid.booleanValue())
          {
            String title = XMLWizard.getString("_UI_DIALOG_TITLE_INVALID_GRAMMAR");
            String message = XMLWizard.getString("_UI_DIALOG_MESSAGE_INVALID_GRAMMAR");
            boolean answer = MessageDialog.openQuestion(Display.getDefault().getActiveShell(), title, message);
            if (!answer)
              return;
          }
        }
        else
        {
          // TODO externalize these strings
          String title = "Validation Plugins Unavailable";
          String message = "Validation cannot be performed because the validation plugins were disabled or not found.  The generated file may be invalid.  Do you wish to continue?";
          boolean answer = MessageDialog.openQuestion(Display.getDefault().getActiveShell(), title, message);
          if (!answer)
            return;
        }
        NewXMLWizard.showDialog(workbenchWindow.getShell(), file, structuredSelection);
      }
    }
  }

  /**
   * unused
   */
  public void selectionChanged(IAction action, ISelection selection)
  {
  }

  // scammed from WindowUtility
  //
  public static Object getSelection(ISelection selection)
  {
    if (selection == null)
    {
      return null;
    } // end of if ()

    Object result = null;
    if (selection instanceof IStructuredSelection)
    {
       IStructuredSelection es= (IStructuredSelection)selection;
       Iterator i= es.iterator();
       if (i.hasNext())
       {
         result= i.next();
       }
    }			
    return result;
  }
}
