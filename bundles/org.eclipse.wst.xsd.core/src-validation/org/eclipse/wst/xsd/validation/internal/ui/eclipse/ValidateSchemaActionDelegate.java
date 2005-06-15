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

package org.eclipse.wst.xsd.validation.internal.ui.eclipse;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionDelegate;

/**
 * Validate schema - from popup
 */
public class ValidateSchemaActionDelegate
       implements IActionDelegate
{
  public static final String copyright = "(c) Copyright IBM Corporation 2002.";
  protected ISelection selection;

  public void run(IAction action)
  {
  	try {
  		// CS.. for now the following line tests to ensure the user has xerces jars installed
        // so that we can perform some 'fail fast' behaviour
        //
        Class theClass = Class.forName("org.apache.xerces.xni.parser.XMLParserConfiguration", true, this.getClass().getClassLoader());
        if (theClass == null)
        {
         throw(new Exception("Missing Xerces jars in plugin's 'jars' folder"));       
        }

	    IFile fileResource = null;
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
	    ValidateAction validateaction = new ValidateAction(fileResource, true);
	    validateaction.setValidator(new Validator());
	    validateaction.run();
  	}
  	catch (Exception e) {
        // CS..here's where we need to pop up a dialog to tell the user that xerces is not available
        //
        String xercesLine1 = "Required files xercesImpl.jar and xmlParserAPIs.jar cannot be found.\n\n";
        String xercesLine2 = "Download Xerces 2.6.2 and place xercesImpl.jar and xmlParserAPIs.jar in a folder entitled jars in the org.eclipse.wst.xml.validation plugin.\n\n";
        String xercesLine3 = "For more information see www.eclipse.org/webtools/wst/components/xml/xercesInfo.xml.";
        MessageDialog.openError(Display.getDefault().getActiveShell(), "Missing Xerces", xercesLine1 + xercesLine2 + xercesLine3);
  	}
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    this.selection = selection;
  }
}
