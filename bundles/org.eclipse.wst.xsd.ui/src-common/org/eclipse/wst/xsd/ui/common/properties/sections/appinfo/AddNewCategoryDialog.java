/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.common.properties.sections.appinfo;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wst.common.ui.internal.dialogs.SelectSingleFileDialog;
import org.eclipse.wst.xsd.editor.XSDEditorPlugin;

public class AddNewCategoryDialog extends Dialog
{

  private static final String SCHEMA_LABEL = "Schema:";
  private static final String NAME_LABEL = "Name:";
  private String dialogTitle = "Add Category";
  private MenuManager browseMenu;
  private Label name;
  private Text nameText;
  private Label schema;
  private CLabel schemaDisplayer;
  private ToolBar browseToolBar;
  private ToolItem browseItem;

  private List invalidNames;
  private String appInfoSchemaLocation;
  private String categoryName;
  private CLabel errDisplayer;

  private boolean isCategoryNameValid;

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
  
  public SpecificationForAppinfoSchema getAppinfoSpec(){
	SpecificationForAppinfoSchema schemaSpec = new SpecificationForAppinfoSchema();
	schemaSpec.setDisplayName(getNewCategoryName());
	schemaSpec.setLocation(getCategoryLocation());
	
	return schemaSpec;
  }
  
  // we initially disable the Ok button
  protected Control createButtonBar(Composite parent)
  {
    Control result = super.createButtonBar(parent);
    getButton(IDialogConstants.OK_ID).setEnabled(false);
    return result;
  }

  protected Control createDialogArea(Composite parent)
  {
    getShell().setText(dialogTitle);

    Composite mainComposite = (Composite) super.createDialogArea(parent);
    GridLayout layout = new GridLayout(3, false);
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

    Button hidden = new Button(mainComposite, SWT.NONE);
    hidden.setVisible(false);

    // Line 2, schema
    schema = new Label(mainComposite, SWT.NONE);
    schema.setText(SCHEMA_LABEL);

    schemaDisplayer = new CLabel(mainComposite, SWT.BORDER | SWT.SINGLE);
    schemaDisplayer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    browseToolBar = new ToolBar(mainComposite, SWT.FLAT);
    browseItem = new ToolItem(browseToolBar, SWT.NONE);
    // TODO: Should be able to get the image from the XML plugin. Don't need
    // to copy to XSDEditor icons folder like this.
    browseItem.setImage(XSDEditorPlugin.getXSDImage("icons/appinfo_browse.gif"));

    browseMenu = new MenuManager();

    BrowseInWorkspaceAction browseInWorkspace = new BrowseInWorkspaceAction();
    browseMenu.add(browseInWorkspace);

    BrowseCatalogAction browseCatalog = new BrowseCatalogAction();
    browseMenu.add(browseCatalog);

    browseItem.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent e)
      {
        Menu menu = browseMenu.createContextMenu(getShell());
        Rectangle bounds = browseItem.getBounds();
        Point topLeft = new Point(bounds.x, bounds.y + bounds.height);
        topLeft = browseToolBar.toDisplay(topLeft);
        menu.setLocation(topLeft.x, topLeft.y);
        menu.setVisible(true);
      }
    });

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
            errDisplayer.setText("The name is already being used.");
            errDisplayer.setImage(XSDEditorPlugin.getXSDImage("icons/error_st_obj.gif"));
            return;
          }
        }
        // name is empty string
        if (categoryName.equals(""))
        {
          isCategoryNameValid = false;

          getButton(IDialogConstants.OK_ID).setEnabled(false);
          errDisplayer.setText("");
          errDisplayer.setImage(null);
          return;
        }

        /*
         * Enable the Ok button if the location field AND the name field are not
         * empty
         */
        if (!categoryName.equals(""))
        {
          isCategoryNameValid = true;
          errDisplayer.setText("");
          errDisplayer.setImage(null);
        }
        if (!appInfoSchemaLocation.equals(""))
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

  protected class BrowseInWorkspaceAction extends Action
  {
    private static final String XSD_FILE_EXTENSION = ".xsd";

    public BrowseInWorkspaceAction()
    {
      super("Workspace");
    }

    public void run()
    {
      SelectSingleFileDialog dialog = new SelectSingleFileDialog(getShell(), null, true);
      dialog.addFilterExtensions(new String[] { XSD_FILE_EXTENSION });
      dialog.create();
      dialog.setTitle("Select XSD file");
      dialog.setMessage("Choose an XSD file containing schema for your extensible components");

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

          appInfoSchemaLocation = "file://" + Platform.getLocation().toString() + "/" + appInfoSchemaLocation;
          // TODO... be careful how we construct the location
          // UNIX related issues here

          schemaDisplayer.setImage(XSDEditorPlugin.getXSDImage("icons/XSDFile.gif"));
          schemaDisplayer.setText(uri);

          // Enable the OK button if we should..
          if (isCategoryNameValid)
          {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
            errDisplayer.setText("");
            errDisplayer.setImage(null);
          }
        }
      }
    }
  }

  protected class BrowseCatalogAction extends Action
  {
    public BrowseCatalogAction()
    {
      super("Catalog");
    }

    public void run()
    {
      SelectFromCatalogDialog dialog = new SelectFromCatalogDialog(getShell());
      // dialog.open();
      if (dialog.open() == Window.OK)
      {
        appInfoSchemaLocation = dialog.getCurrentSelectionLocation();

        schemaDisplayer.setImage(XSDEditorPlugin.getXSDImage("icons/xmlcatalog_obj.gif"));
        schemaDisplayer.setText(dialog.getCurrentSelectionNamespace());

        // Enable the OK button if we should..
        if (isCategoryNameValid && !appInfoSchemaLocation.equals(""))
        {
          getButton(IDialogConstants.OK_ID).setEnabled(true);
          errDisplayer.setText("");
          errDisplayer.setImage(null);
        }
      }
    }
  }

}
