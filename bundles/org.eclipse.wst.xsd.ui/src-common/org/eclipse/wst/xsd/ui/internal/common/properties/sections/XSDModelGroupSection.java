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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateContentModelCommand;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;

public class XSDModelGroupSection extends MultiplicitySection
{
  protected CCombo modelGroupCombo;
  private String[] modelGroupComboValues = { "sequence", "choice", "all" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

  public XSDModelGroupSection()
  {
    super();
  }

  public void createContents(Composite parent)
  {
    composite = getWidgetFactory().createFlatFormComposite(parent);

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    composite.setLayout(gridLayout);

    // ------------------------------------------------------------------
    // NameLabel
    // ------------------------------------------------------------------
    GridData data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    CLabel nameLabel = getWidgetFactory().createCLabel(composite, Messages.UI_LABEL_KIND);
    nameLabel.setLayoutData(data);

    // ------------------------------------------------------------------
    // NameText
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    modelGroupCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
    modelGroupCombo.setLayoutData(data);
    modelGroupCombo.addSelectionListener(this);
    modelGroupCombo.setItems(modelGroupComboValues);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(modelGroupCombo,
    		XSDEditorCSHelpIds.GENERAL_TAB__MODELGROUP__KIND);


    // ------------------------------------------------------------------
    // min property
    // ------------------------------------------------------------------
    getWidgetFactory().createCLabel(composite, Messages.UI_LABEL_MINOCCURS);
    
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;

    minCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
    minCombo.setLayoutData(data);
    minCombo.add("0"); //$NON-NLS-1$
    minCombo.add("1"); //$NON-NLS-1$
    applyAllListeners(minCombo);
    minCombo.addSelectionListener(this);
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(minCombo,
    		XSDEditorCSHelpIds.GENERAL_TAB__MODELGROUP__MIN_OCCURENCE);

    // ------------------------------------------------------------------
    // max property
    // ------------------------------------------------------------------
    getWidgetFactory().createCLabel(composite, Messages.UI_LABEL_MAXOCCURS);

    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;

    maxCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
    maxCombo.setLayoutData(data);
    maxCombo.add("0"); //$NON-NLS-1$
    maxCombo.add("1"); //$NON-NLS-1$
    maxCombo.add("unbounded"); //$NON-NLS-1$
    applyAllListeners(maxCombo);
    maxCombo.addSelectionListener(this);
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(maxCombo,
    		XSDEditorCSHelpIds.GENERAL_TAB__MODELGROUP__MAX_OCCURENCE);
  }

  
  public void refresh()
  {
    super.refresh();

    if (isReadOnly)
    {
      composite.setEnabled(false);
    }
    else
    {
      composite.setEnabled(true);
    }

    setListenerEnabled(false);

    if (input != null)
    {
      if (input instanceof XSDModelGroup)
      {
        XSDModelGroup particle = (XSDModelGroup)input;
        String modelType = particle.getCompositor().getName();
        modelGroupCombo.setText(modelType);
        
        minCombo.setEnabled(!(particle.eContainer() instanceof XSDModelGroupDefinition));
        maxCombo.setEnabled(!(particle.eContainer() instanceof XSDModelGroupDefinition));
      }
    }
    
    refreshMinMax();

    setListenerEnabled(true);
  }
  
  public void doWidgetSelected(SelectionEvent e)
  {
    XSDModelGroup particle = (XSDModelGroup)input;
    if (e.widget == modelGroupCombo)
    {
      XSDCompositor newValue = XSDCompositor.get(modelGroupCombo.getText());
      UpdateContentModelCommand command = new UpdateContentModelCommand(org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_ACTION_CHANGE_CONTENT_MODEL, particle, newValue);
      getCommandStack().execute(command);
    }
    super.doWidgetSelected(e);
  }
  
  public void dispose()
  {
    if (minCombo != null && !minCombo.isDisposed())
      minCombo.removeSelectionListener(this);
    if (maxCombo != null && !maxCombo.isDisposed())
      maxCombo.removeSelectionListener(this);
    super.dispose();
  }
}
