/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.wizards;
 
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;


public class XSDNewFilePage extends WizardNewFileCreationPage
{
  public XSDNewFilePage(IStructuredSelection selection) 
  {
    super(XSDEditorPlugin.getXSDString("_UI_CREATEXSD"), selection);
    setTitle(XSDEditorPlugin.getXSDString("_UI_NEW_XML_SCHEMA_TITLE"));
    setDescription(XSDEditorPlugin.getXSDString("_UI_CREATE_A_NEW_XML_SCHEMA_DESC"));
  }

  public void createControl(Composite parent) 
  {
    // inherit default container and name specification widgets
    super.createControl(parent);

    this.setFileName(computeDefaultFileName());

    setPageComplete(validatePage());
  }

  protected boolean validatePage()
  {
    Path newName = new Path(getFileName());
    String fullFileName = getFileName();
    String extension = newName.getFileExtension();
    if (extension == null || !extension.equalsIgnoreCase("xsd")) 
    {
      setErrorMessage(XSDEditorPlugin.getXSDString("_ERROR_FILENAME_MUST_END_XSD"));
      return false;
    }
    else 
    {
      setErrorMessage(null);
    }

    // check for file should be case insensitive
    String sameName = existsFileAnyCase(fullFileName);
    if (sameName != null) 
    {
      setErrorMessage(XSDEditorPlugin.getPlugin().getString("_ERROR_FILE_ALREADY_EXISTS", sameName)); //$NON-NLS-1$
      return false;
    }

    return super.validatePage();
  }

  public String defaultName = "NewXMLSchema"; //$NON-NLS-1$
  public String defaultFileExtension = ".xsd"; //$NON-NLS-1$
  public String[] filterExtensions = { "*.xsd"}; //$NON-NLS-1$

  protected String computeDefaultFileName()
  {
    int count = 0;
    String fileName = defaultName + defaultFileExtension;
    IPath containerFullPath = getContainerFullPath();
    if (containerFullPath != null)
    {
      while (true)
      {
        IPath path = containerFullPath.append(fileName);
        if (ResourcesPlugin.getWorkspace().getRoot().exists(path))
        {
          count++;
          fileName = defaultName + count + defaultFileExtension;
        }
        else
        {
          break;
        }
      }
    }
    return fileName;
  }

  // returns true if file of specified name exists in any case for selected container
  protected String existsFileAnyCase(String fileName)
  {
    if ( (getContainerFullPath() != null) && (getContainerFullPath().isEmpty() == false)
        && (fileName.compareTo("") != 0))
    {
      //look through all resources at the specified container - compare in upper case
      IResource parent = ResourcesPlugin.getWorkspace().getRoot().findMember(getContainerFullPath());
      if (parent instanceof IContainer)
      {
        IContainer container = (IContainer) parent;
        try
        {
          IResource[] members = container.members();
          String enteredFileUpper = fileName.toUpperCase();
          for (int i=0; i<members.length; i++)
          {
            String resourceUpperName = members[i].getName().toUpperCase();
            if (resourceUpperName.equals(enteredFileUpper))
            {  
              return members[i].getName();    
            }
          }
        }
        catch (CoreException e)
        {
        }
      }
    }
    return null;
  }
}
