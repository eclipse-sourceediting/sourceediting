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
package org.eclipse.wst.xsd.ui.internal.properties.section;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.common.ui.internal.viewers.ResourceFilter;
import org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertyConstants;
import org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xml.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
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
		public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory)
		{
			super.createControls(parent, factory);
			Composite composite = getWidgetFactory().createFlatFormComposite(parent);

      int leftCoordinate = getStandardLabelWidth(composite, 
          new String[] {XSDEditorPlugin.getXSDString("_UI_LABEL_SCHEMA_LOCATION")});
      
			// Create Schema Location Label
			CLabel schemaLocationLabel = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_SCHEMA_LOCATION")); //$NON-NLS-1$
			
      // Create Schema Location Text
      schemaLocationText = getWidgetFactory().createText(composite, "", SWT.NONE); //$NON-NLS-1$
      
			// Create Wizard Button
			wizardButton = getWidgetFactory().createButton(composite, "", SWT.NONE); //$NON-NLS-1$
      
      wizardButton.setImage(XSDEditorPlugin.getXSDImage("icons/browsebutton.gif")); //$NON-NLS-1$
			FormData buttonFormData = new FormData();
			buttonFormData.left = new FormAttachment(100, -rightMarginSpace + 2);
			buttonFormData.right = new FormAttachment(100,0);
			buttonFormData.top = new FormAttachment(schemaLocationText, 0, SWT.CENTER);
			wizardButton.setLayoutData(buttonFormData);
			wizardButton.addSelectionListener(this);
			
			schemaLocationText.setEditable(true);
			FormData schemaLocationData = new FormData();
      schemaLocationData.left = new FormAttachment(0, leftCoordinate);
      schemaLocationData.right = new FormAttachment(wizardButton, 0);
			schemaLocationText.setLayoutData(schemaLocationData);
			schemaLocationText.addListener(SWT.Modify, this);		    

			FormData data = new FormData();
			data.left = new FormAttachment(0, 0);
			data.right = new FormAttachment(schemaLocationText, -ITabbedPropertyConstants.HSPACE);
			data.top = new FormAttachment(schemaLocationText, 0, SWT.CENTER);
			schemaLocationLabel.setLayoutData(data);
			
      // error text
      errorText = new StyledText(composite, SWT.FLAT);
      errorText.setEditable(false);
      errorText.setEnabled(false);
      errorText.setText("");
      
      data = new FormData();
      data.left = new FormAttachment(schemaLocationText, 0, SWT.LEFT);
      data.right = new FormAttachment(100, 0);
      data.top = new FormAttachment(schemaLocationText, 0);
      errorText.setLayoutData(data);
		}
		
		public void widgetSelected(SelectionEvent event)
    {
			if (event.widget == wizardButton)
      {
				Shell shell = Display.getCurrent().getActiveShell();
			    
				IFile currentIFile = ((IFileEditorInput)getActiveEditor().getEditorInput()).getFile();
				ViewerFilter filter = new ResourceFilter(new String[] { ".xsd" },  //$NON-NLS-1$
			            new IFile[] { currentIFile },
			            null);
			      
			  XSDSelectIncludeFileWizard fileSelectWizard = 
			      new XSDSelectIncludeFileWizard(xsdSchema, true,
			          XSDEditorPlugin.getXSDString("_UI_FILEDIALOG_SELECT_XML_SCHEMA"), //$NON-NLS-1$
			          XSDEditorPlugin.getXSDString("_UI_FILEDIALOG_SELECT_XML_DESC"), //$NON-NLS-1$
			          filter,
			          (IStructuredSelection) selection);

			  WizardDialog wizardDialog = new WizardDialog(shell, fileSelectWizard);
			  wizardDialog.create();
			  wizardDialog.setBlockOnOpen(true);
			  int result = wizardDialog.open();
				  
	      String value = schemaLocationText.getText();
	      if (result == Window.OK)
	      {
          errorText.setText("");
	        IFile selectedIFile = fileSelectWizard.getResultFile();
	        String schemaFileString = value;
	        if (selectedIFile != null) 
	        {
	          schemaFileString = URIHelper.getRelativeURI(selectedIFile.getLocation(), currentIFile.getLocation());
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
			if (doRefresh)
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
            location = "";
          }
					schemaLocationText.setText(location);
				}

        setListenerEnabled(true);
			}
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
