/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateAttributeValueCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;

public class XSDSimpleTypeAdvancedSection extends AbstractSection
{
  private static final String EMPTY = ""; //$NON-NLS-1$		
  private String finalValues[] = { EMPTY, "#" + XSDConstants.ALL_ELEMENT_TAG,  //$NON-NLS-1$
	      XSDConstants.LIST_ELEMENT_TAG, XSDConstants.RESTRICTION_ELEMENT_TAG, 
	      XSDConstants.UNION_ELEMENT_TAG};
  
  CCombo finalCombo;
  
  public XSDSimpleTypeAdvancedSection()
  {
    super();
  }

  protected void createContents(Composite parent)
  {
    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    composite = factory.createFlatFormComposite(parent);

    GridData data = new GridData();

    GridLayout gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginBottom = 0;
    gridLayout.numColumns = 3;
    composite.setLayout(gridLayout);

    // ------------------------------------------------------------------
    // FinalLabel
    // ------------------------------------------------------------------
    data = new GridData();
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
  }
  
  public void setInput(IWorkbenchPart part, ISelection selection)
  {
    super.setInput(part, selection);
  }

  public void refresh()
  {
    super.refresh();

    setListenerEnabled(false);

    finalCombo.setText(""); //$NON-NLS-1$); 
    
    if (input instanceof XSDSimpleTypeDefinition)
    {
      XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) input;

      
      if (simpleType.getElement().hasAttribute(XSDConstants.FINAL_ATTRIBUTE))
      {
        String finalAttValue = simpleType.getElement().getAttribute(XSDConstants.FINAL_ATTRIBUTE);
        finalCombo.setText(finalAttValue);
      }
      else
      {
        finalCombo.setText(EMPTY);
      }
    }
    setListenerEnabled(true);
  }

  public void doWidgetSelected(SelectionEvent e)
  {
    if (e.widget == finalCombo && input != null && input instanceof XSDSimpleTypeDefinition)
    {
      XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) input;
      String value = finalCombo.getText();
      UpdateAttributeValueCommand command = new UpdateAttributeValueCommand(simpleType.getElement(), XSDConstants.FINAL_ATTRIBUTE, value, Messages._UI_LABEL_FINAL);
      command.setDeleteIfEmpty(true);
      getCommandStack().execute(command);
    }
  }
}
