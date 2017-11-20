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

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.common.commands.SetXSDFacetValueCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.xsd.XSDEnumerationFacet;

public class XSDEnumerationFacetSection extends AbstractSection
{
  CCombo finalCombo;
  Text valueText;
  
  public XSDEnumerationFacetSection()
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
    // ValueLabel
    // ------------------------------------------------------------------
    data = new GridData();
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;

    CLabel valueLabel = getWidgetFactory().createCLabel(composite, Messages._UI_LABEL_VALUE);
    valueLabel.setLayoutData(data);

    // ------------------------------------------------------------------
    // ValueText
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    valueText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
    valueText.setLayoutData(data);
    applyAllListeners(valueText);
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(valueText,
    		XSDEditorCSHelpIds.GENERAL_TAB__SIMPLE_TYPE__NAME);    
    
  }
  
  public void setInput(IWorkbenchPart part, ISelection selection)
  {
    super.setInput(part, selection);
  }

  public void refresh()
  {
    super.refresh();

    setListenerEnabled(false);

    valueText.setText(""); //$NON-NLS-1$
    
    if (input instanceof XSDEnumerationFacet)
    {
      XSDEnumerationFacet xsdEnumeration = (XSDEnumerationFacet)input;
      
      if(xsdEnumeration != null)
      {
        valueText.setText(xsdEnumeration.getLexicalValue());
      }
    }
    setListenerEnabled(true);
  }
  
  public void doHandleEvent(Event event) 
  {
    if (event.widget == valueText)
    {
      if (!valueText.getEditable())
        return;

      String value = valueText.getText();
      
      if (input instanceof XSDEnumerationFacet)
      {
    	XSDEnumerationFacet xsdEnumeration = (XSDEnumerationFacet)input;
    	  
    	Command command = null;
    	command = new SetXSDFacetValueCommand(Messages._UI_ACTION_CHANGE_ENUMERATION_VALUE,
          xsdEnumeration, value);

        if (command != null && getCommandStack() != null)
        {
          getCommandStack().execute(command);
          valueText.setText(value);          
        }
      }
    }
  }
}
