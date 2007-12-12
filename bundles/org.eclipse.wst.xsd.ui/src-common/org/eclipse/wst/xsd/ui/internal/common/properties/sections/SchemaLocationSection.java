/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.ui.internal.viewers.ResourceFilter;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.wizards.XSDSelectIncludeFileWizard;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.impl.XSDIncludeImpl;
import org.eclipse.xsd.impl.XSDRedefineImpl;
import org.w3c.dom.Element;

public class SchemaLocationSection extends CommonDirectivesSection
{
	  IWorkbenchPart part;
	  
	  /**
	   * 
	   */
	  public SchemaLocationSection()
	  {
	    super();
	  }

		/**
		 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
		 */
		public void createContents(Composite parent)
		{
			composite = getWidgetFactory().createFlatFormComposite(parent);

      GridLayout gridLayout = new GridLayout();
      gridLayout.marginTop = 0;
      gridLayout.marginBottom = 0;
      gridLayout.numColumns = 3;
      composite.setLayout(gridLayout);
      
      GridData data = new GridData();

			// Create Schema Location Label
			CLabel schemaLocationLabel = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_SCHEMA_LOCATION")); //$NON-NLS-1$
      data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
      data.grabExcessHorizontalSpace = false;
      schemaLocationLabel.setLayoutData(data);
			
      // Create Schema Location Text
      schemaLocationText = getWidgetFactory().createText(composite, "", SWT.NONE); //$NON-NLS-1$
      schemaLocationText.setEditable(true);
      applyAllListeners(schemaLocationText);       

      data = new GridData();
      data.grabExcessHorizontalSpace = true;
      data.horizontalAlignment = GridData.FILL;
      schemaLocationText.setLayoutData(data);
      
      PlatformUI.getWorkbench().getHelpSystem().setHelp(schemaLocationText,
      		XSDEditorCSHelpIds.GENERAL_TAB__INCLUDE_REDEFINE__SCHEMALOCATION);
      
			// Create Wizard Button
			wizardButton = getWidgetFactory().createButton(composite, "", SWT.NONE); //$NON-NLS-1$
      wizardButton.setImage(XSDEditorPlugin.getXSDImage("icons/browsebutton.gif")); //$NON-NLS-1$
      data = new GridData();
      data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
      data.grabExcessHorizontalSpace = false;
			wizardButton.setLayoutData(data);
			wizardButton.addSelectionListener(this);
			
      // error text
      errorText = new StyledText(composite, SWT.FLAT);
      errorText.setEditable(false);
      errorText.setEnabled(false);
      errorText.setText(""); //$NON-NLS-1$
      
      data = new GridData();
      data.horizontalAlignment = GridData.FILL;
      data.horizontalSpan = 3;
      data.grabExcessHorizontalSpace = true;
      errorText.setLayoutData(data);

		}
		
		public void doWidgetSelected(SelectionEvent event)
    {
			if (event.widget == wizardButton)
      {
				Shell shell = Display.getCurrent().getActiveShell();

				IFile currentIFile = null;
        IEditorInput editorInput = getActiveEditor().getEditorInput();
        ViewerFilter filter;
        if (editorInput instanceof IFileEditorInput)
        {
          currentIFile = ((IFileEditorInput)editorInput).getFile();
          filter = new ResourceFilter(new String[] { ".xsd" }, //$NON-NLS-1$ 
              new IFile[] { currentIFile }, null);
        }
        else
        {
          filter = new ResourceFilter(new String[] { ".xsd" }, //$NON-NLS-1$ 
              null, null);
        }
			      
			  XSDSelectIncludeFileWizard fileSelectWizard = 
			      new XSDSelectIncludeFileWizard(xsdSchema, true,
			          XSDEditorPlugin.getXSDString("_UI_FILEDIALOG_SELECT_XML_SCHEMA"), //$NON-NLS-1$
			          XSDEditorPlugin.getXSDString("_UI_FILEDIALOG_SELECT_XML_DESC"), //$NON-NLS-1$
			          filter,
			          (IStructuredSelection) getSelection());

			  WizardDialog wizardDialog = new WizardDialog(shell, fileSelectWizard);
			  wizardDialog.create();
			  wizardDialog.setBlockOnOpen(true);
			  int result = wizardDialog.open();
				  
	      String value = schemaLocationText.getText();
	      if (result == Window.OK)
	      {
          errorText.setText(""); //$NON-NLS-1$
	        IFile selectedIFile = fileSelectWizard.getResultFile();
	        String schemaFileString = value;
	        if (selectedIFile != null && currentIFile != null)
	        {
	          schemaFileString = URIHelper.getRelativeURI(selectedIFile.getLocation(), currentIFile.getLocation());
	        }
	        else if (selectedIFile != null && currentIFile == null)
	        {
	          schemaFileString = selectedIFile.getLocationURI().toString();
	        }
	        else
	        {
	          schemaFileString = fileSelectWizard.getURL();
	        }

          handleSchemaLocationChange(schemaFileString, fileSelectWizard.getNamespace(), null);
	        refresh();
			  } 
      }
		}

		/*
		 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
		 */
		public void refresh()
		{
				setListenerEnabled(false);

				Element element = null;
				if (input instanceof XSDInclude)
        { 
					element = ((XSDIncludeImpl) input).getElement();
				}
				else if (input instanceof XSDRedefine)
        {
					element = ((XSDRedefineImpl) input).getElement();
				}
				
				if (element != null)
        {
					String location = ""; //$NON-NLS-1$
					location = element.getAttribute("schemaLocation"); //$NON-NLS-1$
          if (location == null)
          {
            location = ""; //$NON-NLS-1$
          }
					schemaLocationText.setText(location);
				}

        setListenerEnabled(true);
		}

	  /* (non-Javadoc)
	   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISection#shouldUseExtraSpace()
	   */
	  public boolean shouldUseExtraSpace()
	  {
	    return true;
	  }
    
    protected void handleSchemaLocationChange(String schemaFileString, String namespace, XSDSchema externalSchema)
    {
      if (input instanceof XSDInclude)
      {
        Element element = ((XSDIncludeImpl) input).getElement();
        element.setAttribute("schemaLocation", schemaFileString); //$NON-NLS-1$
      }
      else if (input instanceof XSDRedefine)
      {
        Element element = ((XSDRedefineImpl) input).getElement();
        element.setAttribute("schemaLocation", schemaFileString); //$NON-NLS-1$
      }
    }
}
