/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.ui.internal.dialogs.SelectSingleFileDialog;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

public class AddNewCategoryDialog extends Dialog
{
  private static final String SCHEMA_LABEL = Messages._UI_LABEL_SCHEMA;
  private static final String NAME_LABEL = Messages._UI_LABEL_NAME;  
  private static final String SELECT_FROM_WORKSPACE = Messages._UI_LABEL_WORKSPACE; 
  private static final String SELECT_FROM_CATALOG = Messages._UI_LABEL_CATALOG;  
  private String dialogTitle = Messages._UI_LABEL_ADD_CATEGORY;
    
  protected MenuManager browseMenu;
  protected Label name;
  protected Text nameText;
  protected Label schema;
  protected CLabel schemaDisplayer;
  protected ToolBar browseToolBar;
  protected ToolItem browseItem;
  protected Button searchCatalog;
  protected Button searchWorkspace;  
  protected Composite sourcesComposite;

  protected List invalidNames;
  
  // TODO (cs) rename this field to extensionSchemaLocation in WTP 2.0
  protected String appInfoSchemaLocation;
  protected String categoryName;
  protected CLabel errDisplayer;
  protected boolean isCategoryNameValid;
  protected boolean fromCatalog;
  
  private boolean canOK =false; 
  
  /** Either the location if come from workspace or namespace if come from
   * 	XML Catalog  */
  protected String source;

  public AddNewCategoryDialog(Shell parentShell)
  {
    super(parentShell);
  }

  public AddNewCategoryDialog(Shell parentShell, String dialogTitle)
  {
    super(parentShell);
    this.dialogTitle = dialogTitle;
  }
  
  /**
   * receive a List of names which have already been added to the category list
   * 
   * @param unavailNames
   *          Array of unvailable names
   */
  public void setUnavailableCategoryNames(List unavailNames)
  {
    invalidNames = unavailNames;
  }

  public String getNewCategoryName()
  {
    return categoryName.trim();
  }

  public String getCategoryLocation()
  {
    return appInfoSchemaLocation;
  }
  
  public void setCategoryLocation(String s){
	  appInfoSchemaLocation = s;
  }
  
  /** @deprecated */
  public SpecificationForExtensionsSchema getExtensionsSchemaSpec(){
	SpecificationForExtensionsSchema schemaSpec = new SpecificationForExtensionsSchema();
	schemaSpec.setDisplayName(getNewCategoryName());
	schemaSpec.setLocation(getCategoryLocation());
	
	return schemaSpec;
  }
    
  public void setCategoryName(String categoryName) {
	this.categoryName = categoryName;
  }

  public boolean getFromCatalog() {
	return fromCatalog;
  }
  
  public void setFromCatalog(boolean b){
	fromCatalog = b;	
  }

  public String getSource()
  {
	return source;  
  }
  
  public void setSource(String source) {
	this.source = source;
  }

  protected Control createButtonBar(Composite parent)
  {
    Control result = super.createButtonBar(parent);
    getButton(IDialogConstants.OK_ID).setEnabled(canOK);
    return result;
  }

  // redundant method to improve speed (according to the compiler)
  protected Button getButton(int id) {
    return super.getButton(id);
  }
  
  protected Control createDialogArea(Composite parent)
  {
    getShell().setText(dialogTitle);

    Composite mainComposite = (Composite) super.createDialogArea(parent);
    GridLayout layout = new GridLayout(2, false);
    layout.marginTop = 10;
    mainComposite.setLayout(layout);
    mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
    GridData data = new GridData();
    data.widthHint = 400;

    mainComposite.setLayoutData(data);

    // Line 1, name
    name = new Label(mainComposite, SWT.NONE);
    name.setText(NAME_LABEL);

    nameText = new Text(mainComposite, SWT.BORDER | SWT.SINGLE);
    nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));    
    if (categoryName != null)
    	nameText.setText(categoryName);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(nameText,
    		XSDEditorCSHelpIds.ADD_CATEGORY__NAME);     

    

    // Line 2, schema
    schema = new Label(mainComposite, SWT.NONE);
    schema.setText(SCHEMA_LABEL);

    schemaDisplayer = new CLabel(mainComposite, SWT.BORDER | SWT.SINGLE);
    schemaDisplayer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    if (source != null)
    {
    	if (fromCatalog)
    		schemaDisplayer.setImage(
    				XSDEditorPlugin.getXSDImage("icons/xmlcatalog_obj.gif")); //$NON-NLS-1$
    	else
    		schemaDisplayer.setImage(
    				XSDEditorPlugin.getXSDImage("icons/XSDFile.gif")); //$NON-NLS-1$
    	schemaDisplayer.setText(source);
    	
    }
    PlatformUI.getWorkbench().getHelpSystem().setHelp(schemaDisplayer,
    		XSDEditorCSHelpIds.ADD_CATEGORY__SCHEMA); 
    
    if (categoryName != null && source != null)
    	canOK = true;
    
    // Line 3, schema selection buttons            
    Button hidden = new Button(mainComposite, SWT.NONE);
    hidden.setVisible(false);    
           
    sourcesComposite = new Composite(mainComposite, SWT.NONE);
    RowLayout sourcesLayout = new RowLayout();
  
    sourcesComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    sourcesComposite.setLayout(sourcesLayout);
    
    searchWorkspace = new Button(sourcesComposite, SWT.NONE);
    searchWorkspace.setText(SELECT_FROM_WORKSPACE);
        
    searchCatalog = new Button(sourcesComposite, SWT.NONE);
    searchCatalog.setText(SELECT_FROM_CATALOG);
    
    searchWorkspace.addSelectionListener(new SelectionAdapter()
    {
    	public void widgetSelected(SelectionEvent e)
    	{
    		final String XSD_FILE_EXTENSION = ".xsd"; //$NON-NLS-1$   	    
    	    
    	      SelectSingleFileDialog dialog = new SelectSingleFileDialog(getShell(), null, true);
    	      dialog.addFilterExtensions(new String[] { XSD_FILE_EXTENSION });
    	      dialog.create();
    	      dialog.setTitle(Messages._UI_LABEL_SELECT_XSD_FILE);
    	      dialog.setMessage(Messages._UI_DESCRIPTION_CHOOSE_XSD_FILE);

    	      if (dialog.open() == Window.OK)
    	      {
    	        IFile appInfoSchemaFile = dialog.getFile();
    	        if (appInfoSchemaFile != null)
    	        {
    	          // remove leading slash from the value to avoid the
    	          // whole leading slash ambiguity problem
    	          String uri = appInfoSchemaFile.getFullPath().toString();
    	          while (uri.startsWith("/") || uri.startsWith("\\")) { //$NON-NLS-1$ //$NON-NLS-2$
    	            uri = uri.substring(1);
    	          }
    	          appInfoSchemaLocation = uri.toString();
    	          source = uri;
    	          fromCatalog = false;

    	          appInfoSchemaLocation = "file://" + Platform.getLocation().toString() + "/" + appInfoSchemaLocation; //$NON-NLS-1$ //$NON-NLS-2$
    	          // TODO... be careful how we construct the location
    	          // UNIX related issues here

    	          schemaDisplayer.setImage(XSDEditorPlugin.getXSDImage("icons/XSDFile.gif")); //$NON-NLS-1$
    	          schemaDisplayer.setText(uri);

    	          // Enable the OK button if we should..
    	          if (isCategoryNameValid)
    	          {
    	            getButton(IDialogConstants.OK_ID).setEnabled(true);
    	            errDisplayer.setText(""); //$NON-NLS-1$
    	            errDisplayer.setImage(null);
    	          }
    	        }
    	      }
    	    }
    	}
    
    );
    
    searchCatalog.addSelectionListener(new SelectionAdapter()
    {
    	public void widgetSelected(SelectionEvent e)
    	{
    	      SelectFromCatalogDialog dialog = new SelectFromCatalogDialog(getShell());
    	      // dialog.open();
    	      if (dialog.open() == Window.OK)
    	      {
    	        appInfoSchemaLocation = dialog.getCurrentSelectionLocation();
    	        source = dialog.getCurrentSelectionNamespace();
    	        fromCatalog = true;

    	        schemaDisplayer.setImage(XSDEditorPlugin.getXSDImage("icons/xmlcatalog_obj.gif")); //$NON-NLS-1$
    	        schemaDisplayer.setText(dialog.getCurrentSelectionNamespace());

    	        // Enable the OK button if we should..
    	        if (isCategoryNameValid && !appInfoSchemaLocation.equals("")) //$NON-NLS-1$
    	        {
    	          getButton(IDialogConstants.OK_ID).setEnabled(true);
    	          errDisplayer.setText(""); //$NON-NLS-1$
    	          errDisplayer.setImage(null);    	        
    	      }
    	    }
    	}
    });
    

    // TODO: Should be able to get the image from the XML plugin. Don't need
    // to copy to XSDEditor icons folder like this.


    // Composite errComp = new Composite(mainComposite, SWT.NONE);
    // errComp.setBackground(org.eclipse.draw2d.ColorConstants.white);
    // errComp.setLayout(new GridLayout());
    errDisplayer = new CLabel(mainComposite, SWT.FLAT);
    // errDisplayer.setText("abd");
    GridData gd = new GridData(GridData.FILL_BOTH);
    gd.grabExcessHorizontalSpace = true;
    gd.horizontalSpan = 3;
    errDisplayer.setLayoutData(gd);

    // errComp.setLayoutData(gd);
    // errDisplayer.setLayoutData(gd);
    // errMsgContainer.setContent(errDisplayer);

    nameText.addModifyListener(new ModifyListener()
    {
      // track the nameText and enable/disable the OK button accordingly
      public void modifyText(ModifyEvent e)
      {
        categoryName = nameText.getText();

        // name is in the invalid List
        if (invalidNames != null)
        {
          if (invalidNames.contains(categoryName.trim()))
          {
            isCategoryNameValid = false;

            getButton(IDialogConstants.OK_ID).setEnabled(false);
            errDisplayer.setText(Messages._UI_ERROR_NAME_ALREADY_USED);
            errDisplayer.setImage(XSDEditorPlugin.getXSDImage("icons/error_st_obj.gif")); //$NON-NLS-1$
            return;
          }
        }
        // name is empty string
        if (categoryName.equals("")) //$NON-NLS-1$
        {
          isCategoryNameValid = false;

          getButton(IDialogConstants.OK_ID).setEnabled(false);
          errDisplayer.setText(""); //$NON-NLS-1$
          errDisplayer.setImage(null);
          return;
        }

        /*
         * Enable the Ok button if the location field AND the name field are not
         * empty
         */
        if (!categoryName.equals("")) //$NON-NLS-1$
        {
          isCategoryNameValid = true;
          errDisplayer.setText(""); //$NON-NLS-1$
          errDisplayer.setImage(null);
        }
        if (appInfoSchemaLocation != null && !appInfoSchemaLocation.equals("")) //$NON-NLS-1$
        {
          getButton(IDialogConstants.OK_ID).setEnabled(true);
        }
      }
    });

    return parent;
  }

  protected void okPressed()
  {
    super.okPressed();
  }
}
