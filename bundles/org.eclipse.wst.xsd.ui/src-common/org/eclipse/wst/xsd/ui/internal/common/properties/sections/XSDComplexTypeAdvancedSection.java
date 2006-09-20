/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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

  private String finalValues[] = { EMPTY, XSDConstants.RESTRICTION_ELEMENT_TAG, //$NON-NLS-1$
      XSDConstants.EXTENSION_ELEMENT_TAG, "#" + XSDConstants.ALL_ELEMENT_TAG }; //$NON-NLS-1$

  private String blockValues[] = { EMPTY, XSDConstants.RESTRICTION_ELEMENT_TAG, //$NON-NLS-1$
      XSDConstants.EXTENSION_ELEMENT_TAG, "#" + XSDConstants.ALL_ELEMENT_TAG }; //$NON-NLS-1$

  private String abstractValues[] = { EMPTY, TRUE, FALSE }; // TODO use some
                                                            // external string
                                                            // here instead
  private CCombo abstractCombo;

  protected void createContents(Composite parent)
  {
    composite = getWidgetFactory().createFlatFormComposite(parent);

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    composite.setLayout(gridLayout);

    // ------------------------------------------------------------------
    // BlockLabel
    // ------------------------------------------------------------------
    GridData data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;

    // TODO Should be a translatable string here
    CLabel blockLabel = getWidgetFactory().createCLabel(composite, XSDConstants.BLOCK_ATTRIBUTE + ":");
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

    // TODO Should be a translatable string here
    CLabel finalLabel = getWidgetFactory().createCLabel(composite, XSDConstants.FINAL_ATTRIBUTE + ":");
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
    // AbstractLabel
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;

    // TODO Should be a translatable string here
    CLabel abstractLabel = getWidgetFactory().createCLabel(composite, XSDConstants.ABSTRACT_ATTRIBUTE + ":");
    abstractLabel.setLayoutData(data);

    // ------------------------------------------------------------------
    // FinalCombo
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    abstractCombo = getWidgetFactory().createCCombo(composite);
    abstractCombo.setLayoutData(data);
    abstractCombo.setEditable(false);

    abstractCombo.setItems(abstractValues);
    abstractCombo.addSelectionListener(this);
  }

  public void doWidgetSelected(SelectionEvent e)
  {
    if (e.widget == blockCombo)
    {
      XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) input;
      String value = blockCombo.getText();

      UpdateAttributeValueCommand command = new UpdateAttributeValueCommand(complexType.getElement(), XSDConstants.BLOCK_ATTRIBUTE, value);
      command.setDeleteIfEmpty(true);
      getCommandStack().execute(command);
    }
    else if (e.widget == finalCombo)
    {
      XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) input;
      String value = finalCombo.getText();

      UpdateAttributeValueCommand command = new UpdateAttributeValueCommand(complexType.getElement(), XSDConstants.FINAL_ATTRIBUTE, value);
      command.setDeleteIfEmpty(true);
      getCommandStack().execute(command);
    }
    else if (e.widget == abstractCombo)
    {
      XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) input;
      String value = abstractCombo.getText();

      if (value.equals(EMPTY))
        complexType.getElement().removeAttribute(XSDConstants.ABSTRACT_ATTRIBUTE);
      else
      {
        if (value.equals(TRUE))
          complexType.setAbstract(true);
        else if (value.equals(FALSE))
          complexType.setAbstract(false);
      }
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
        
        if (complexType.getContainer() instanceof XSDSchema)
        {
          composite.setEnabled(!isReadOnly);
        }
        else
        {
          composite.setEnabled(false);
        }
        
        String blockAttValue = complexType.getElement().getAttribute(XSDConstants.BLOCK_ATTRIBUTE);
        if (blockAttValue != null)
        {
          blockCombo.setText(blockAttValue);
        }
        else
        {
          blockCombo.setText(EMPTY);
        }

        String finalAttValue = complexType.getElement().getAttribute(XSDConstants.FINAL_ATTRIBUTE);
        if (finalAttValue != null)
        {
          finalCombo.setText(finalAttValue);
        }
        else
        {
          finalCombo.setText(EMPTY);
        }

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
      }
    }
    catch (Exception e)
    {
    }
    setListenerEnabled(true);
  }
}
