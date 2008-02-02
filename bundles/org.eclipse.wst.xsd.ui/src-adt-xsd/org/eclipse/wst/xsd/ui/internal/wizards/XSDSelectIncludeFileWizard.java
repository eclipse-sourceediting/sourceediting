/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.ui.internal.viewers.SelectSingleFilePage;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.ViewUtility;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDParser;


/**
 * Extend the base wizard to select a file from the project or outside the workbench
 * and add error handling
 */
public class XSDSelectIncludeFileWizard extends Wizard implements INewWizard
{
  boolean isInclude;
  XSDSchema mainSchema;
  XSDSchema externalSchema;

  XSDLocationChoicePage choicePage;
  XSDSelectSingleFilePage filePage;
  XSDURLPage urlPage;

  IFile resultFile;
  String resultURL;
  String namespace = "";

  public XSDSelectIncludeFileWizard(XSDSchema mainSchema, boolean isInclude,
                                    String title, String desc, 
                                    ViewerFilter filter,
                                    IStructuredSelection selection)
  {
    super();
    setWindowTitle(title);
    setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(XSDEditorPlugin.class, "icons/NewXSD.png"));

    setNeedsProgressMonitor(true);

    // Choice Page
    choicePage = new XSDLocationChoicePage();

    // Select File Page
    filePage = new XSDSelectSingleFilePage(PlatformUI.getWorkbench(),  selection, true);
    filePage.setTitle(title);
    filePage.setDescription(desc);
    filePage.addFilter(filter);

    // URL Page
    urlPage = new XSDURLPage();
    urlPage.setTitle(title);
    urlPage.setDescription(XSDEditorPlugin.getXSDString("_UI_FILEDIALOG_SELECT_XML_URL"));
    
    this.mainSchema = mainSchema;
    this.isInclude = isInclude;
  }

  public void init(IWorkbench aWorkbench, IStructuredSelection aSelection)
  { 
  }

  public void addPages()
  {
    addPage(choicePage);
    addPage(filePage);
    addPage(urlPage);
  }

  public IWizardPage getNextPage(IWizardPage currentPage)
  {
    WizardPage nextPage = null;

    if (currentPage == choicePage)
    {
      if (choicePage.isURL()) 
      {
        nextPage = urlPage;
      }
      else
      {
        nextPage = filePage;
      }
    }
    return nextPage;
  }

  public boolean canFinish()
  {
    if (!choicePage.isURL())
    {
      return filePage.isPageComplete(); 
    }
    return true;
  }

  public boolean performFinish()
  { 
    if (choicePage.isURL())
    {
      try 
      {
        getContainer().run(false, true, urlPage.getRunnable());
        resultURL = urlPage.getURL();
      }
      catch (Exception e)
      {
        return false;
      }
      return true;
    }
    else
    {  
      resultFile = filePage.getFile();
    }
    return true;
  }

  /**
   * Get the MOF object that represents the external file
   */
  public XSDSchema getExternalSchema()
  {
    return externalSchema;
  }

  public IFile getResultFile()
  {
    return resultFile;
  }

  public String getURL()
  {
    return resultURL;
  }
  
  public String getNamespace()
  {
  	return namespace;
  }

  /**
   * Create a MOF model for the imported file
   */
  protected String doLoadExternalModel(IProgressMonitor monitor, String xsdModelFile, String xsdFileName)
  { 
    String errorMessage = null;
    String currentNameSpace = mainSchema.getTargetNamespace();

    monitor.beginTask("Loading XML Schema", 100);
    monitor.worked(50);

    XSDParser parser = new XSDParser();
    parser.parse(xsdModelFile);

    externalSchema = parser.getSchema();
    if (externalSchema != null)
    {
      String extNamespace = externalSchema.getTargetNamespace();
      namespace = extNamespace;
     
      if (externalSchema.getDiagnostics() != null &&
          externalSchema.getDiagnostics().size() > 0)
      {
        errorMessage = XSDEditorPlugin.getPlugin().getString("_UI_INCORRECT_XML_SCHEMA", xsdFileName);
      }  
      else
      {
        if (isInclude) 
        {  
          // Check the namespace to make sure they are the same as current file
          if (extNamespace != null)
          {
            if (currentNameSpace != null && !extNamespace.equals(currentNameSpace))
            {
              errorMessage = XSDEditorPlugin.getPlugin().getString("_UI_DIFFERENT_NAME_SPACE", xsdFileName);
            }
          }
        }
        else
        {  
          // Check the namespace to make sure they are different from the current file
          if (extNamespace != null)
          {
            if (currentNameSpace != null && extNamespace.equals(currentNameSpace))
            {
              errorMessage = XSDEditorPlugin.getPlugin().getString("_UI_SAME_NAME_SPACE", xsdFileName);
            }
          }
        }
      }
    }
    else
    {
      errorMessage = XSDEditorPlugin.getPlugin().getString("_UI_INCORRECT_XML_SCHEMA", xsdFileName);
    }

    monitor.subTask("Finish Loading");
    monitor.worked(80);

    return errorMessage;
  }

 
  /**
   * URL page
   */
  class XSDURLPage extends WizardPage
  { 
    Text urlField;
    String saveString;

    public XSDURLPage()
    {
      super("URLPage");
    }

    public void createControl(Composite parent)
    {
      Composite client = ViewUtility.createComposite(parent,2);
      ViewUtility.setComposite(client);

      ViewUtility.createLabel(client, XSDEditorPlugin.getXSDString("_UI_LABEL_URL"));
      ViewUtility.createLabel(client, "");

      urlField = ViewUtility.createTextField(client, 50);
      saveString = "http://";
      urlField.setText(saveString);

      setControl(client);
    }

    public String getURL()
    {
      return urlField.getText();
    }

    private boolean openExternalSchema(IProgressMonitor monitor)
    {
      String text = urlField.getText();
//      if (text.equals(saveString)) 
//      {
//        return false;
//      }
//      saveString = text;

      if (text.equals(""))
      {
        setErrorMessage(XSDEditorPlugin.getXSDString("_UI_SPECIFY_URL"));
        return false;
      }

      if ( !text.startsWith("http://") )
      {
        setErrorMessage(XSDEditorPlugin.getXSDString("_UI_URL_START_WITH"));
        return false;
      }

      setErrorMessage(null);
      String errorMessage = doLoadExternalModel(monitor, text, text);
      if (errorMessage != null) 
      {
        setErrorMessage(errorMessage); 
        return false;
      }
      else
      {
        return true;
      }
    }

    public IRunnableWithProgress getRunnable()
    {
      return new IRunnableWithProgress()
      {
        public void run(IProgressMonitor monitor)   
          throws InvocationTargetException, InterruptedException
        {
          if (monitor == null)
          {
            monitor= new NullProgressMonitor();
          }
          monitor.beginTask("", 6);
        
          boolean ok = openExternalSchema(monitor);

          if (!ok) 
          { 
            throw new InvocationTargetException(new java.lang.Error());
          }

          monitor.done();
        }
      };
    }
  }

  /**
   * Select XML Schema File
   */
  class XSDSelectSingleFilePage extends SelectSingleFilePage
  {
    public XSDSelectSingleFilePage(IWorkbench workbench, IStructuredSelection selection, boolean isFileMandatory)
    {          
      super(workbench,selection,isFileMandatory);
    }

    private boolean openExternalSchema()
    {
      // Get the fully-qualified file name
      IFile iFile = getFile();
      if (iFile == null) 
        return false;

      setErrorMessage(null);

      String xsdModelFile = iFile.getLocationURI().toString();
      String xsdFileName = iFile.getName();
      String errorMessage = doLoadExternalModel(new NullProgressMonitor(), xsdModelFile, xsdFileName);

      if (errorMessage != null) 
      {
        setErrorMessage(errorMessage);
        return false;
      }
      else
      {
        return true;
      }
    }

    public boolean isPageComplete()
    {  
      if (choicePage.isURL()) 
      {
        return true;
      }

      if (super.isPageComplete()) 
      {
        return openExternalSchema();
      }
      return super.isPageComplete();
    }
  }
}
