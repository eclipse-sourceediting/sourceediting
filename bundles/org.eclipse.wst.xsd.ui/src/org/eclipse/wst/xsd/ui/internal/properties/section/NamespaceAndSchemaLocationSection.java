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

import java.util.Map;

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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.common.ui.properties.ITabbedPropertyConstants;
import org.eclipse.wst.common.ui.properties.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.common.ui.viewers.ResourceFilter;
import org.eclipse.wst.xml.uriresolver.util.URIHelper;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.refactor.delete.XSDExternalFileCleanup;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.XSDSchemaHelper;
import org.eclipse.wst.xsd.ui.internal.wizards.XSDSelectIncludeFileWizard;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.impl.XSDImportImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class NamespaceAndSchemaLocationSection extends CommonDirectivesSection
{
	Text namespaceText, prefixText;
  protected String oldPrefixValue;
	  
	public NamespaceAndSchemaLocationSection()
  {
		super();
	}
	
	/**
	 * @see org.eclipse.wst.common.ui.properties.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory)
	{
		super.createControls(parent, factory);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);

		CLabel namespaceLabel = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_NAMESPACE")); //$NON-NLS-1$
    namespaceText = getWidgetFactory().createText(composite, "", SWT.NONE); //$NON-NLS-1$
		namespaceText.setEditable(false);

    FormData data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(namespaceText, +ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(namespaceText, 0, SWT.CENTER);
    namespaceLabel.setLayoutData(data);
    
    data = new FormData();
    data.left = new FormAttachment(0, 110);
    data.right = new FormAttachment(100, -rightMarginSpace - ITabbedPropertyConstants.HSPACE);
    data.top = new FormAttachment(0, 0);
    namespaceText.setLayoutData(data);
    
		CLabel prefixLabel = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_PREFIX")); //$NON-NLS-1$				
		prefixText = getWidgetFactory().createText(composite, "", SWT.NONE); //$NON-NLS-1$
		prefixText.setEditable(true);
    prefixText.addListener(SWT.Modify, this);

    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(prefixText, 0);
    data.top = new FormAttachment(prefixText, 0, SWT.CENTER);
    prefixLabel.setLayoutData(data);  
    
		data = new FormData();
		data.left = new FormAttachment(0, 110);
		data.right = new FormAttachment(100, -rightMarginSpace - ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(namespaceText, +ITabbedPropertyConstants.VSPACE);
		prefixText.setLayoutData(data);
   
		// Create Schema Location Label
		CLabel schemaLocationLabel = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_SCHEMA_LOCATION")); //$NON-NLS-1$
    schemaLocationText = getWidgetFactory().createText(composite, "", SWT.NONE); //$NON-NLS-1$

		// Create Wizard Button
		wizardButton = getWidgetFactory().createButton(composite, "", SWT.NONE); //$NON-NLS-1$
    wizardButton.setImage(XSDEditorPlugin.getXSDImage("icons/browsebutton.gif")); //$NON-NLS-1$
		FormData buttonFormData = new FormData();
		buttonFormData.left = new FormAttachment(100, -rightMarginSpace + 2);
		buttonFormData.right = new FormAttachment(100, 0);
		buttonFormData.top = new FormAttachment(schemaLocationText, 0, SWT.CENTER);
		wizardButton.setLayoutData(buttonFormData);
		wizardButton.addSelectionListener(this);
		
		// Create Schema Location Text
		schemaLocationText.setEditable(true);
		FormData schemaLocationData = new FormData();
		schemaLocationData.left = new FormAttachment(0, 110);
		schemaLocationData.right = new FormAttachment(wizardButton, 0);
		schemaLocationData.top = new FormAttachment(prefixText, +ITabbedPropertyConstants.VSPACE);
		schemaLocationText.setLayoutData(schemaLocationData);
		schemaLocationText.addListener(SWT.Modify, this);
    
    data = new FormData();
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(schemaLocationText, 0);
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
      setListenerEnabled(false);
			Shell shell = Display.getCurrent().getActiveShell();
		    
			IFile currentIFile = ((IFileEditorInput)getActiveEditor().getEditorInput()).getFile();
			ViewerFilter filter = new ResourceFilter(new String[] { ".xsd" }, //$NON-NLS-1$ 
		            new IFile[] { currentIFile },
		            null);
		      
			XSDSelectIncludeFileWizard fileSelectWizard = 
			   new XSDSelectIncludeFileWizard(xsdSchema, 
                                        false,
			                                  XSDEditorPlugin.getXSDString("_UI_FILEDIALOG_SELECT_XML_SCHEMA"), //$NON-NLS-1$
			                                  XSDEditorPlugin.getXSDString("_UI_FILEDIALOG_SELECT_XML_DESC"), //$NON-NLS-1$
			                                  filter,
			                                  (IStructuredSelection) selection);

			WizardDialog wizardDialog = new WizardDialog(shell, fileSelectWizard);
			wizardDialog.create();
			wizardDialog.setBlockOnOpen(true);
			int result = wizardDialog.open();
			  
		  String value = schemaLocationText.getText();
      prefixText.removeListener(SWT.Modify, this);
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

        String namespace = fileSelectWizard.getNamespace();
        if (namespace == null) namespace = "";

        XSDSchema externalSchema = fileSelectWizard.getExternalSchema();
        handleSchemaLocationChange(schemaFileString, namespace, externalSchema);
		  }
      setListenerEnabled(true);
      prefixText.addListener(SWT.Modify, this);
		}
	}

  protected void handleSchemaLocationChange(String schemaFileString, String namespace, XSDSchema externalSchema)
  {
    XSDConcreteComponent comp = (XSDConcreteComponent)getInput();
    if (comp instanceof XSDImport)
    {
      XSDImport xsdImport = (XSDImport)comp;
      Element importElement = comp.getElement();
      
      beginRecording(XSDEditorPlugin.getXSDString("_UI_IMPORT_CHANGE"), importElement);

      xsdImport.setNamespace(namespace);
      xsdImport.setSchemaLocation(schemaFileString);
      xsdImport.setResolvedSchema(externalSchema);
      
      java.util.Map map = xsdSchema.getQNamePrefixToNamespaceMap();
      
//      System.out.println("changed Import Map is " + map.values());
//      System.out.println("changed import Map keys are " + map.keySet());

      // Referential integrity on old import
      // How can we be sure that if the newlocation is the same as the oldlocation
      // the file hasn't changed
      
      XSDSchema referencedSchema = xsdImport.getResolvedSchema();
      if (referencedSchema != null)
      {
        XSDExternalFileCleanup cleanHelper = new XSDExternalFileCleanup(referencedSchema);
        cleanHelper.visitSchema(xsdSchema);
      }

      Element schemaElement = getSchema().getElement();

      // update the xmlns in the schema element first, and then update the import element next
      // so that the last change will be in the import element.  This keeps the selection
      // on the import element
      TypesHelper helper = new TypesHelper(externalSchema);
      String prefix = helper.getPrefix(namespace, false);
      
      if (map.containsKey(prefix))
      {
        prefix = null;
      }

      if (prefix == null || (prefix !=null && prefix.length() == 0))
      {
        StringBuffer newPrefix = new StringBuffer("pref");  //$NON-NLS-1$
        int prefixExtension = 1;
        while (map.containsKey(newPrefix.toString()) && prefixExtension < 100)
        {
          newPrefix = new StringBuffer("pref" + String.valueOf(prefixExtension));
          prefixExtension++;
        }
        prefix = newPrefix.toString();
      }

      if (namespace.length() > 0)
      {
        // if ns already in map, use its corresponding prefix
        if (map.containsValue(namespace))
        {
          TypesHelper typesHelper = new TypesHelper(xsdSchema);
          prefix = typesHelper.getPrefix(namespace, false);
        }
        else // otherwise add to the map
        {
          schemaElement.setAttribute("xmlns:"+prefix, namespace);
        }
        prefixText.setText(prefix);
      }
      else
      {
        prefixText.setText("");
        namespaceText.setText("");
      }
        
      endRecording(importElement);
      
//      System.out.println("changed Import Map is " + map.values());
//      System.out.println("changed import Map keys are " + map.keySet());

    }        
    refresh();
  }
  
	/*
	 * @see org.eclipse.wst.common.ui.properties.view.ITabbedPropertySection#refresh()
	 */
	public void refresh()
	{
		if (doRefresh)
		{
      errorText.setText("");
			setListenerEnabled(false);

			Element element = null;
			if (input instanceof XSDImport) { 
				element = ((XSDImportImpl) input).getElement();
				
				String namespace = element.getAttribute("namespace"); //$NON-NLS-1$
				String schemaLocation = element.getAttribute("schemaLocation"); //$NON-NLS-1$
				
				TypesHelper helper = new TypesHelper(xsdSchema);
		        String prefix = helper.getPrefix(element.getAttribute(XSDConstants.NAMESPACE_ATTRIBUTE), false);

				if (namespace == null) {
					namespace = ""; //$NON-NLS-1$
				}
				if (prefix == null) {
					prefix = ""; //$NON-NLS-1$
				}
				if (schemaLocation == null) {
					schemaLocation = ""; //$NON-NLS-1$
				}
				
				namespaceText.setText(namespace);
				prefixText.setText(prefix);
				schemaLocationText.setText(schemaLocation);
        oldPrefixValue = prefixText.getText();
			}

			setListenerEnabled(true);
		}
	}

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.ISection#shouldUseExtraSpace()
   */
  public boolean shouldUseExtraSpace()
  {
    return true;
  }

  public void doHandleEvent(Event event)
  {
    super.doHandleEvent(event);
    if (event.type == SWT.Modify)
    {
      if (event.widget == prefixText)
      {
        if (validatePrefix(prefixText.getText()) && schemaLocationText.getText().trim().length() > 0)
        {
          Element element = ((XSDConcreteComponent)getInput()).getElement();
          Map map = getSchema().getQNamePrefixToNamespaceMap();

          if (map.containsKey(prefixText.getText()))
          {
            setErrorMessage(XSDEditorPlugin.getXSDString("_ERROR_LABEL_PREFIX_EXISTS"));
          }
          else
          {
            beginRecording(XSDEditorPlugin.getXSDString("_UI_PREFIX_CHANGE"), element);
            
            Element schemaElement = getSchema().getElement();
            schemaElement.removeAttribute("xmlns:"+oldPrefixValue);
            schemaElement.setAttribute("xmlns:" + prefixText.getText(), namespaceText.getText());
            XSDSchemaHelper.updateElement(getSchema());

            clearErrorMessage();
            oldPrefixValue = prefixText.getText();
            endRecording(element);  

//            System.out.println("Map is " + map.values());
//            System.out.println("Map keys are " + map.keySet());
          }
        }
      }
    }
  }
}
