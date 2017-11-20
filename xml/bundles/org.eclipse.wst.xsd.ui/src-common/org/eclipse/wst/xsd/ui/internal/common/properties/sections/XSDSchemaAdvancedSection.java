/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateAttributeValueCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.xsd.XSDForm;
import org.eclipse.xsd.util.XSDConstants;

public class XSDSchemaAdvancedSection extends AbstractSection
{
  IWorkbenchPart part;
  protected CLabel elementFormLabel, attributeFormLabel, blockDefaultLabel, finalDefaultLabel;
  protected CCombo elementFormCombo, attributeFormCombo, blockDefaultCombo, finalDefaultCombo;  
  
  protected static final String emptyOption = ""; //$NON-NLS-1$
  
  protected static final String [] formQualification = { emptyOption, XSDForm.QUALIFIED_LITERAL.getLiteral(), 
      XSDForm.UNQUALIFIED_LITERAL.getLiteral() };  //$NON-NLS-1$

    
  protected static final String derivedByChoicesComboValues[] = { "", XSDConstants.RESTRICTION_ELEMENT_TAG, //$NON-NLS-1$ 
      XSDConstants.EXTENSION_ELEMENT_TAG }; 
  
  protected static final String blockDefaultValues[] = { emptyOption, "#" + XSDConstants.ALL_ELEMENT_TAG, //$NON-NLS-1$
      XSDConstants.EXTENSION_ELEMENT_TAG, XSDConstants.RESTRICTION_ELEMENT_TAG,
      "substitution"};
  
  protected static final String finalDefaultValues[] = { emptyOption, "#" + XSDConstants.ALL_ELEMENT_TAG, //$NON-NLS-1$
      XSDConstants.EXTENSION_ELEMENT_TAG, XSDConstants.RESTRICTION_ELEMENT_TAG, 
      XSDConstants.LIST_ELEMENT_TAG, XSDConstants.UNION_ELEMENT_TAG};      

  public XSDSchemaAdvancedSection()
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
    gridLayout.numColumns = 2;
    composite.setLayout(gridLayout);

    // Create elementFormDefault label and combo       
    elementFormLabel = XSDCommonUIUtils.getNewPropertiesLabel(composite,getWidgetFactory(),
        Messages._UI_LABEL_ELEMENTFORMDEFAULT);
    elementFormCombo = XSDCommonUIUtils.getNewPropertiesCombo(composite,getWidgetFactory(),this,
        formQualification,XSDEditorCSHelpIds.ADVANCE_TAB__SCHEMA_ELEMENT_FORM_DEFAULT);

    // Create attributeFormDefault label and combo
    attributeFormLabel = XSDCommonUIUtils.getNewPropertiesLabel(composite,getWidgetFactory(),
        Messages._UI_LABEL_ATTRIBUTEFORMDEFAULT);
    attributeFormCombo = XSDCommonUIUtils.getNewPropertiesCombo(composite,getWidgetFactory(),this,
        formQualification,XSDEditorCSHelpIds.ADVANCE_TAB__SCHEMA_ATTRIBUTE_FORM_DEFAULT);    
    
    // Create blockDefault label and combo
    blockDefaultLabel = XSDCommonUIUtils.getNewPropertiesLabel(composite,getWidgetFactory(),
        Messages._UI_LABEL_BLOCKDEFAULT);
    blockDefaultCombo = XSDCommonUIUtils.getNewPropertiesCombo(composite,getWidgetFactory(),this,
        blockDefaultValues,XSDEditorCSHelpIds.ADVANCE_TAB__SCHEMA_BLOCK_DEFAULT);
    
    // Create finalDefault label and combo
    finalDefaultLabel = XSDCommonUIUtils.getNewPropertiesLabel(composite,getWidgetFactory(),
        Messages._UI_LABEL_FINALDEFAULT);
    finalDefaultCombo = XSDCommonUIUtils.getNewPropertiesCombo(composite,getWidgetFactory(),this,
        finalDefaultValues,XSDEditorCSHelpIds.ADVANCE_TAB__SCHEMA_FINAL_DEFAULT);    
    
    Dialog.applyDialogFont(parent);
  }

  /*
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
   */
  public void refresh()
  {
    super.refresh();
    setListenerEnabled(false);
    
    if (xsdSchema != null)
    {
      if (elementFormCombo != null)
      {
        String text;
        if (xsdSchema.getElement().hasAttribute(XSDConstants.ELEMENTFORMDEFAULT_ATTRIBUTE))
        {
          text = xsdSchema.getElementFormDefault().getName();  
        }
        else 
        {
          text = emptyOption;  
        } 
        elementFormCombo.setText(text);
      }
      
      if (attributeFormCombo != null)
      {
        String text;
        if (xsdSchema.getElement().hasAttribute(XSDConstants.ATTRIBUTEFORMDEFAULT_ATTRIBUTE))
        {
          text = xsdSchema.getAttributeFormDefault().getName();
        }
        else 
        {
          text = emptyOption;
        }
        attributeFormCombo.setText(text);
      }
      
      if (blockDefaultCombo != null)
      {
        String text;
        if (xsdSchema.getElement().hasAttribute(XSDConstants.BLOCKDEFAULT_ATTRIBUTE))
        {
          text = xsdSchema.getElement().getAttribute(XSDConstants.BLOCKDEFAULT_ATTRIBUTE);
        }
        else 
        {
          text = emptyOption;
        }
        blockDefaultCombo.setText(text);
      }
      
      if (finalDefaultCombo != null)
      {
        String text;
        if (xsdSchema.getElement().hasAttribute(XSDConstants.FINALDEFAULT_ATTRIBUTE))
        {
          text = xsdSchema.getElement().getAttribute(XSDConstants.FINALDEFAULT_ATTRIBUTE);
        }
        else 
        {
          text = emptyOption;
        }
        finalDefaultCombo.setText(text);
      }      
    }    
    setListenerEnabled(true);
  }
  
  public void doWidgetSelected(SelectionEvent e)
  {
    if (xsdSchema != null)
    {          
      if (e.widget == elementFormCombo && elementFormCombo != null)
      {
        String valueElementForm = elementFormCombo.getText();
        UpdateAttributeValueCommand command = new UpdateAttributeValueCommand(xsdSchema.getElement(), 
            XSDConstants.ELEMENTFORMDEFAULT_ATTRIBUTE, valueElementForm, 
            Messages._UI_LABEL_ELEMENTFORMDEFAULT);
        command.setDeleteIfEmpty(true);
        getCommandStack().execute(command);
      }
            
      if (e.widget == attributeFormCombo && attributeFormCombo != null)
      {     
        String valueAttributeForm = attributeFormCombo.getText();
        UpdateAttributeValueCommand command = new UpdateAttributeValueCommand(xsdSchema.getElement(), 
            XSDConstants.ATTRIBUTEFORMDEFAULT_ATTRIBUTE, valueAttributeForm, 
            Messages._UI_LABEL_ATTRIBUTEFORMDEFAULT);
        command.setDeleteIfEmpty(true);
        getCommandStack().execute(command);        
      }
      
      if (e.widget == blockDefaultCombo && blockDefaultCombo != null)
      {
        String valueBlockDefault = blockDefaultCombo.getText();        
        UpdateAttributeValueCommand command = new UpdateAttributeValueCommand(xsdSchema.getElement(), 
            XSDConstants.BLOCKDEFAULT_ATTRIBUTE, valueBlockDefault, Messages._UI_LABEL_BLOCKDEFAULT);
        command.setDeleteIfEmpty(true);
        getCommandStack().execute(command);
      }
      
      if(e.widget == finalDefaultCombo && finalDefaultCombo != null)
      {
        String finalBlockDefault = finalDefaultCombo.getText();        
        UpdateAttributeValueCommand command = new UpdateAttributeValueCommand(xsdSchema.getElement(), 
            XSDConstants.FINALDEFAULT_ATTRIBUTE, finalBlockDefault, Messages._UI_LABEL_FINALDEFAULT);
        command.setDeleteIfEmpty(true);
        getCommandStack().execute(command);
      }
    }
  }
}
