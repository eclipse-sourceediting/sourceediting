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
package org.eclipse.wst.xsd.ui.internal.properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.navigator.ResourceNavigator;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.wst.common.ui.viewers.ResourceFilter;
import org.eclipse.wst.xml.uriresolver.util.URIHelper;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.wizards.XSDSelectIncludeFileWizard;
import org.eclipse.xsd.XSDSchema;

public abstract class SchemaDirectiveHelperPropertySource
  extends BasePropertySource
{
  protected IFile currentIFile;

  IFile selectedIFile;
  String selectedNamespace;
  XSDSchema selectedXSDSchema;
  boolean isInclude;
  /**
   * 
   */
  public SchemaDirectiveHelperPropertySource(boolean isInclude)
  {
    super();
    this.isInclude = isInclude;
  }
  /**
   * @param viewer
   * @param xsdSchema
   */
  public SchemaDirectiveHelperPropertySource(
    Viewer viewer,
    XSDSchema xsdSchema,
    boolean isInclude)
  {
    super(viewer, xsdSchema);
    this.isInclude = isInclude;
  }
  /**
   * @param xsdSchema
   */
  public SchemaDirectiveHelperPropertySource(XSDSchema xsdSchema, boolean isInclude)
  {
    super(xsdSchema);
    this.isInclude = isInclude;
  }

  
  public class SchemaLocationPropertyDescriptor extends PropertyDescriptor
  {
    /**
     * @param id
     * @param displayName
     */
    public SchemaLocationPropertyDescriptor(Object id, String displayName)
    {
      super(id, displayName);
    }
    
    public CellEditor createPropertyEditor(Composite parent)
    {
      CellEditor editor = new SchemaLocationDialogCellEditor(parent);
      if (getValidator() != null)
        editor.setValidator(getValidator());
      return editor;
    }
  }

  public class SchemaLocationDialogCellEditor extends DialogCellEditor {

    /**
     * Creates a new Font dialog cell editor parented under the given control.
     * The cell editor value is <code>null</code> initially, and has no 
     * validator.
     *
     * @param parent the parent control
     */
    protected SchemaLocationDialogCellEditor(Composite parent) {
      super(parent);
    }

    /**
     * @see org.eclipse.jface.viewers.DialogCellEditor#openDialogBox(Control)
     */
    protected Object openDialogBox(Control cellEditorWindow)
    {
      Shell shell = Display.getCurrent().getActiveShell();
      
//      oldSchemaLocation = locationField.getText().trim();
//      IFile currentIFile = ((IFileEditorInput)getIEditorPart().getEditorInput()).getFile();
      ViewerFilter filter = new ResourceFilter(new String[] { ".xsd" }, 
      new IFile[] { currentIFile },
      null);
//

      IViewPart viewParts[] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViews();
      ResourceNavigator resourceNav = null;
      for (int i = 0; i < viewParts.length; i++) 
       {
        if (viewParts[i] instanceof ResourceNavigator) 
         {
          resourceNav = (ResourceNavigator) viewParts[i];
          break;
        }
      }
      IStructuredSelection selection = StructuredSelection.EMPTY;
      if (resourceNav != null)
       {
        selection = (IStructuredSelection)resourceNav.getViewSite().getSelectionProvider().getSelection();
      }
      
      XSDSelectIncludeFileWizard fileSelectWizard = 
      new XSDSelectIncludeFileWizard(xsdSchema, isInclude,
          XSDEditorPlugin.getXSDString("_UI_FILEDIALOG_SELECT_XML_SCHEMA"),
          XSDEditorPlugin.getXSDString("_UI_FILEDIALOG_SELECT_XML_DESC"),
          filter,
          selection);

      WizardDialog wizardDialog = new WizardDialog(shell, fileSelectWizard);
      wizardDialog.create();
      wizardDialog.setBlockOnOpen(true);
      int result = wizardDialog.open();

      
      String value = (String)getValue();
      // System.out.println("VALUE IS *** = " + value);
      if (result == Window.OK)
      {
        selectedIFile = fileSelectWizard.getResultFile();
        String schemaFileString = value;
        if (selectedIFile != null) 
         {
          schemaFileString = URIHelper.getRelativeURI(selectedIFile.getLocation(), currentIFile.getLocation());
        }
        else
         {
          schemaFileString = fileSelectWizard.getURL();
        }

//        updateExternalModel(selectedIFile, fileSelectWizard.getNamespace(), fileSelectWizard.getExternalSchema());
        selectedNamespace = fileSelectWizard.getNamespace();
        selectedXSDSchema = fileSelectWizard.getExternalSchema();
        
        return schemaFileString;
      }
      return value;
    }
  }

  public IFile getSelectedIFile()
  {
    return selectedIFile;
  }
  
  public String getSelectedNamespace()
  {
    return selectedNamespace;
  }
  
  public XSDSchema getSelectedXSDSchema()
  {
    return selectedXSDSchema;
  }
  
}
