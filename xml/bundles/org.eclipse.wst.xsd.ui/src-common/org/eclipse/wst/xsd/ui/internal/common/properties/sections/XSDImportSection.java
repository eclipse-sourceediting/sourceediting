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

import java.util.Map;

import org.apache.xerces.util.XMLChar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDDirectivesSchemaLocationUpdater;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class XSDImportSection extends SchemaLocationSection
{
  protected Text namespaceText, prefixText;
  protected String oldPrefixValue;

  public XSDImportSection()
  {
    super();
  }

  /**
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
   *      org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
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
    CLabel namespaceLabel = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_NAMESPACE")); //$NON-NLS-1$
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    namespaceLabel.setLayoutData(data);

    namespaceText = getWidgetFactory().createText(composite, "", SWT.NONE); //$NON-NLS-1$
    namespaceText.setEditable(false);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    applyAllListeners(namespaceText);
    namespaceText.setLayoutData(data);

    // ------------------------------------------------------------------
    // DummyLabel
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$

    CLabel prefixLabel = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_PREFIX")); //$NON-NLS-1$
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    prefixLabel.setLayoutData(data);

    prefixText = getWidgetFactory().createText(composite, "", SWT.NONE); //$NON-NLS-1$
    prefixText.setEditable(true);
    applyAllListeners(prefixText);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    prefixText.setLayoutData(data);

    // ------------------------------------------------------------------
    // DummyLabel
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$

    // Create Schema Location Label
    CLabel schemaLocationLabel = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_SCHEMA_LOCATION")); //$NON-NLS-1$
    data = new GridData();
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

    PlatformUI.getWorkbench().getHelpSystem().setHelp(schemaLocationText,
    		XSDEditorCSHelpIds.GENERAL_TAB__IMPORT__SCHEMALOCATION);

    PlatformUI.getWorkbench().getHelpSystem().setHelp(namespaceText,
    		XSDEditorCSHelpIds.GENERAL_TAB__IMPORT__NAMESPACE);
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(prefixText,
    		XSDEditorCSHelpIds.GENERAL_TAB__IMPORT__PREFIX);

  }

  public void refresh()
  {
    setListenerEnabled(false);

    errorText.setText("");
    Element element = null;
    if (input instanceof XSDImport)
    {
      element = ((XSDImport) input).getElement();

      String namespace = element.getAttribute("namespace"); //$NON-NLS-1$
      String schemaLocation = element.getAttribute("schemaLocation"); //$NON-NLS-1$

      TypesHelper helper = new TypesHelper(xsdSchema);
      String prefix = helper.getPrefix(element.getAttribute(XSDConstants.NAMESPACE_ATTRIBUTE), false);

      if (namespace == null)
      {
        namespace = ""; //$NON-NLS-1$
      }

      if (prefix == null)
      {
        prefix = ""; //$NON-NLS-1$
      }

      if (schemaLocation == null)
      {
        schemaLocation = ""; //$NON-NLS-1$
      }

      namespaceText.setText(namespace);
      prefixText.setText(prefix);
      schemaLocationText.setText(schemaLocation);
      oldPrefixValue = prefixText.getText();
    }

    setListenerEnabled(true);
  }

  public void widgetSelected(SelectionEvent event)
  {
    if (event.widget == wizardButton)
    {
      setListenerEnabled(false);
      
      XSDDirectivesSchemaLocationUpdater.updateSchemaLocation(xsdSchema,input,false);
      refresh();
      setListenerEnabled(true);
      prefixText.addListener(SWT.Modify, this);
    }
  }

  public void doHandleEvent(Event event)
  {
    setErrorMessage(null);
    super.doHandleEvent(event);
    if (event.widget == prefixText)
    {
      String newPrefix = prefixText.getText();
      if (oldPrefixValue.equals(newPrefix) || !isValidSchemaLocation)
        return;
      Map map = xsdSchema.getQNamePrefixToNamespaceMap();
      String key = prefixText.getText();      
      if (key.length() == 0) key = null;
      
      if (validatePrefix(newPrefix) && schemaLocationText.getText().trim().length() > 0)
      {
        if (map.containsKey(key))
        {
          setErrorMessage(XSDEditorPlugin.getXSDString("_ERROR_LABEL_PREFIX_EXISTS"));
        }
        else
        {
          Element schemaElement = xsdSchema.getElement();
          
          if (key != null) {
            if (oldPrefixValue.length() == 0) 
              schemaElement.removeAttribute("xmlns");
            else 
              schemaElement.removeAttribute("xmlns:"+oldPrefixValue);
            
            schemaElement.setAttribute("xmlns:" + newPrefix, namespaceText.getText());
          } 
          else {
            schemaElement.removeAttribute("xmlns:"+oldPrefixValue);
            schemaElement.setAttribute("xmlns", namespaceText.getText());
          }
          xsdSchema.updateElement();
          setErrorMessage(null);
          oldPrefixValue = newPrefix;
        }
      }
      else
      {
        setErrorMessage(XSDEditorPlugin.getXSDString("_ERROR_LABEL_INVALID_PREFIX"));
      }
    }
  }

  public void aboutToBeHidden()
  {
    setErrorMessage(null);
    super.aboutToBeHidden();
  }
  
  protected boolean validatePrefix(String prefix)
  {
    if (prefix.length() == 0) return true;
    return XMLChar.isValidNCName(prefix);
  }
}
