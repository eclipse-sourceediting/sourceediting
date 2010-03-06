/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateAttributeValueCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;

public class XSDComplexTypeAdvancedSection extends AbstractSection
{
  private static final String EMPTY = ""; //$NON-NLS-1$
  private static final String FALSE = "false"; //$NON-NLS-1$
  private static final String TRUE = "true"; //$NON-NLS-1$
  protected CCombo blockCombo;
  protected CCombo finalCombo;
  protected CCombo mixedCombo;
  protected CCombo abstractCombo;

  private String finalValues[] = { EMPTY, XSDConstants.RESTRICTION_ELEMENT_TAG, //$NON-NLS-1$
      XSDConstants.EXTENSION_ELEMENT_TAG, "#" + XSDConstants.ALL_ELEMENT_TAG }; //$NON-NLS-1$

  private String blockValues[] = { EMPTY, XSDConstants.RESTRICTION_ELEMENT_TAG, //$NON-NLS-1$
      XSDConstants.EXTENSION_ELEMENT_TAG, "#" + XSDConstants.ALL_ELEMENT_TAG }; //$NON-NLS-1$

  private String booleanValues[] = { EMPTY, TRUE, FALSE }; // TODO use some
                                                            // external string
                                                            // here instead
  protected void createContents(Composite parent)
  {
    composite = getWidgetFactory().createFlatFormComposite(parent);

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    composite.setLayout(gridLayout);

    // ------------------------------------------------------------------
    // AbstractLabel
    // ------------------------------------------------------------------
    GridData data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;

    CLabel abstractLabel = getWidgetFactory().createCLabel(composite, Messages._UI_LABEL_ABSTRACT);
    abstractLabel.setLayoutData(data);

    // ------------------------------------------------------------------
    // AbstractCombo
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    abstractCombo = getWidgetFactory().createCCombo(composite);
    abstractCombo.setLayoutData(data);
    abstractCombo.setEditable(false);

    abstractCombo.setItems(booleanValues);
    abstractCombo.addSelectionListener(this);

    // ------------------------------------------------------------------
    // BlockLabel
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;

    CLabel blockLabel = getWidgetFactory().createCLabel(composite, Messages._UI_LABEL_BLOCK);
    blockLabel.setLayoutData(data);

    // ------------------------------------------------------------------
    // BlockCombo
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    blockCombo = getWidgetFactory().createCCombo(composite);
    blockCombo.setLayoutData(data);
    blockCombo.setEditable(false);

    blockCombo.setItems(blockValues);
    blockCombo.addSelectionListener(this);

    // ------------------------------------------------------------------
    // FinalLabel
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;

    CLabel finalLabel = getWidgetFactory().createCLabel(composite, Messages._UI_LABEL_FINAL);
    finalLabel.setLayoutData(data);

    // ------------------------------------------------------------------
    // FinalCombo
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    finalCombo = getWidgetFactory().createCCombo(composite);
    finalCombo.setLayoutData(data);
    finalCombo.setEditable(false);

    finalCombo.setItems(finalValues);
    finalCombo.addSelectionListener(this);
    
    // ------------------------------------------------------------------
    // Mixed Label
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;

    CLabel mixedLabel = getWidgetFactory().createCLabel(composite, Messages._UI_LABEL_MIXED);
    mixedLabel.setLayoutData(data);

    // ------------------------------------------------------------------
    // Mixed Combo
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    mixedCombo = getWidgetFactory().createCCombo(composite);
    mixedCombo.setLayoutData(data);
    mixedCombo.setEditable(false);

    mixedCombo.setItems(booleanValues);
    mixedCombo.addSelectionListener(this);

  }

  public void doWidgetSelected(SelectionEvent e)
  {
    XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) input;
    if (e.widget == blockCombo)
    {
      String value = blockCombo.getText();
      UpdateAttributeValueCommand command = new UpdateAttributeValueCommand(complexType.getElement(), XSDConstants.BLOCK_ATTRIBUTE, value, Messages._UI_LABEL_BLOCK);
      command.setDeleteIfEmpty(true);
      getCommandStack().execute(command);
    }
    else if (e.widget == finalCombo)
    {
      String value = finalCombo.getText();
      UpdateAttributeValueCommand command = new UpdateAttributeValueCommand(complexType.getElement(), XSDConstants.FINAL_ATTRIBUTE, value, Messages._UI_LABEL_FINAL);
      command.setDeleteIfEmpty(true);
      getCommandStack().execute(command);
    }
    else if (e.widget == abstractCombo)
    {
      String value = abstractCombo.getText();
      UpdateAttributeValueCommand command = new UpdateAttributeValueCommand(complexType.getElement(), XSDConstants.ABSTRACT_ATTRIBUTE, value, Messages._UI_LABEL_ABSTRACT);
      command.setDeleteIfEmpty(true);
      getCommandStack().execute(command);
    }
    else if (e.widget == mixedCombo)
    {
      String value = mixedCombo.getText();
      UpdateAttributeValueCommand command = new UpdateAttributeValueCommand(complexType.getElement(), XSDConstants.MIXED_ATTRIBUTE, value, Messages._UI_LABEL_MIXED);
      command.setDeleteIfEmpty(true);
      getCommandStack().execute(command);
    }
  }

  public void refresh()
  {
    super.refresh();

    setListenerEnabled(false);
    try
    {
      if (input instanceof XSDComplexTypeDefinition)
      {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) input;
        
        boolean enabled = true; 
        if (complexType.getContainer() instanceof XSDSchema)
        {
          enabled = !isReadOnly;
        }
        else
        {
          enabled = false;
        }
        
        if (complexType.getElement().hasAttribute(XSDConstants.BLOCK_ATTRIBUTE))
        {
          String blockAttValue = complexType.getElement().getAttribute(XSDConstants.BLOCK_ATTRIBUTE);
          blockCombo.setText(blockAttValue);
        }
        else
        {
          blockCombo.setText(EMPTY);
        }
        blockCombo.setEnabled(enabled);

        if (complexType.getElement().hasAttribute(XSDConstants.FINAL_ATTRIBUTE))
        {
        	String finalAttValue = complexType.getElement().getAttribute(XSDConstants.FINAL_ATTRIBUTE);
            finalCombo.setText(finalAttValue);
        }
        else
        {
          finalCombo.setText(EMPTY);
        }
        finalCombo.setEnabled(enabled);

        if (complexType.getElement().hasAttribute(XSDConstants.ABSTRACT_ATTRIBUTE))
        {
          boolean absAttValue = complexType.isAbstract();
          if (absAttValue)
            abstractCombo.setText(TRUE);
          else
            abstractCombo.setText(FALSE);
        }
        else
        {
          abstractCombo.setText(EMPTY);
        }
        abstractCombo.setEnabled(enabled);
        
        if (complexType.getElement().hasAttribute(XSDConstants.MIXED_ATTRIBUTE))
        {
          boolean mixedValue = complexType.isMixed();
          if (mixedValue)
            mixedCombo.setText(TRUE);
          else
            mixedCombo.setText(FALSE);
        }
        else
        {
          mixedCombo.setText(EMPTY);
        }

      }
    }
    catch (Exception e)
    {
    }
    setListenerEnabled(true);
  }
}
