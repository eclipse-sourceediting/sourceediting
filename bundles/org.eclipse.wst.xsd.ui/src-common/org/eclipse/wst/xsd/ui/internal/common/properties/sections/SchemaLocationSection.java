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
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDDirectivesSchemaLocationUpdater;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDInclude;
import org.eclipse.xsd.XSDRedefine;
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
        XSDDirectivesSchemaLocationUpdater.updateSchemaLocation(xsdSchema,input,true);
        refresh();
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
}
