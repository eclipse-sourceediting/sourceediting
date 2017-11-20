/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver - STAR - added nillable advanced tab option, bug 209356
 *******************************************************************************/

package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.adt.edit.ComponentReferenceEditManager;
import org.eclipse.wst.xsd.ui.internal.adt.edit.IComponentDialog;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateAttributeValueCommand;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDSubstitutionGroupEditManager;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class XSDElementDeclarationAdvancedSection extends AbstractSection
{
  private static final String EMPTY = ""; //$NON-NLS-1$
  private static final String FALSE = "false"; //$NON-NLS-1$
  private static final String TRUE = "true"; //$NON-NLS-1$
  protected CCombo blockCombo;
  protected CCombo finalCombo;
  protected CCombo abstractCombo;
  protected CCombo substGroupCombo;
  protected CCombo nillableCombo;

  private String blockValues[] = { EMPTY, "#" + XSDConstants.ALL_ELEMENT_TAG, //$NON-NLS-1$
      XSDConstants.EXTENSION_ELEMENT_TAG, XSDConstants.RESTRICTION_ELEMENT_TAG,
      "substitution" }; //$NON-NLS-1$

  private String finalValues[] = { EMPTY, "#" + XSDConstants.ALL_ELEMENT_TAG, XSDConstants.EXTENSION_ELEMENT_TAG, //$NON-NLS-1$
      XSDConstants.RESTRICTION_ELEMENT_TAG }; //$NON-NLS-1$

  private String booleanValues[] = { EMPTY, TRUE, FALSE }; 

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

    CLabel abstractLabel = getWidgetFactory().createCLabel(composite, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_ABSTRACT);
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

    CLabel blockLabel = getWidgetFactory().createCLabel(composite, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_BLOCK);
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

    CLabel finalLabel = getWidgetFactory().createCLabel(composite, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_FINAL);
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
    // Substitution Group Label
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;

    CLabel subGroupLabel = getWidgetFactory().createCLabel(composite, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_SUBSTITUTION_GROUP);
    subGroupLabel.setLayoutData(data);

    // ------------------------------------------------------------------
    // Substitution Group Combo
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    substGroupCombo = getWidgetFactory().createCCombo(composite);
    substGroupCombo.setLayoutData(data);
    substGroupCombo.setEditable(true);
    substGroupCombo.addSelectionListener(this);
    substGroupCombo.addListener(SWT.Traverse, this);
    applyAllListeners(substGroupCombo);
    
    // ------------------------------------------------------------------
    // Nillable Label
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;

    CLabel nillableLabel = getWidgetFactory().createCLabel(composite, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_NILLABLE);
    nillableLabel.setLayoutData(data);

    // ------------------------------------------------------------------
    // NillableCombo
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    nillableCombo = getWidgetFactory().createCCombo(composite);
    nillableCombo.setLayoutData(data);
    nillableCombo.setEditable(false);

    nillableCombo.setItems(booleanValues);
    nillableCombo.addSelectionListener(this);
    
  }

  public void doHandleEvent(Event e)
  {
    if (e.widget == substGroupCombo)
    {
      if (e.type == SWT.Traverse) {
        if (e.detail == SWT.TRAVERSE_ARROW_NEXT || e.detail == SWT.TRAVERSE_ARROW_PREVIOUS)
        {
          isTraversing = true;
          return;
        }
      }
		
      XSDElementDeclaration eleDec = (XSDElementDeclaration) input;
      String value = substGroupCombo.getText();
      String oldValue = eleDec.getElement().getAttribute(XSDConstants.SUBSTITUTIONGROUP_ATTRIBUTE);
      if (oldValue == null)
        oldValue = EMPTY;
      if (value.equals(oldValue))
        return;

      UpdateAttributeValueCommand command = new UpdateAttributeValueCommand(eleDec.getElement(), XSDConstants.SUBSTITUTIONGROUP_ATTRIBUTE, value, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_SUBSTITUTION_GROUP);
      command.setDeleteIfEmpty(true);
      getCommandStack().execute(command);
    }
  }

  public void doWidgetDefaultSelected(SelectionEvent e)
  {
    if (e.widget == substGroupCombo)
    {
      String selection = substGroupCombo.getText();
      if (shouldPerformComboSelection(SWT.DefaultSelection, selection))
        handleWidgetSelection(e);
    } else
    {
      handleWidgetSelection(e);
    }
  }

  public void doWidgetSelected(SelectionEvent e)
  {
    if (e.widget == substGroupCombo)
    {
      String selection = substGroupCombo.getText();
      if (shouldPerformComboSelection(SWT.Selection, selection))
        handleWidgetSelection(e);
    } else
    {
      handleWidgetSelection(e);
    }
  }

  private void handleWidgetSelection(SelectionEvent e)
  {
    if (e.widget == blockCombo)
    {
      XSDElementDeclaration eleDec = (XSDElementDeclaration) input;
      String value = blockCombo.getText();

      UpdateAttributeValueCommand command = new UpdateAttributeValueCommand(eleDec.getElement(), XSDConstants.BLOCK_ATTRIBUTE, value, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_BLOCK);
      command.setDeleteIfEmpty(true);
      getCommandStack().execute(command);
    }
    else if (e.widget == finalCombo)
    {
      XSDElementDeclaration eleDec = (XSDElementDeclaration) input;
      String value = finalCombo.getText();

      UpdateAttributeValueCommand command = new UpdateAttributeValueCommand(eleDec.getElement(), XSDConstants.FINAL_ATTRIBUTE, value, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_FINAL);
      command.setDeleteIfEmpty(true);
      getCommandStack().execute(command);
    }
    else if (e.widget == abstractCombo)
    {
      XSDElementDeclaration eleDec = (XSDElementDeclaration) input;
      String value = abstractCombo.getText();
      UpdateAttributeValueCommand command = new UpdateAttributeValueCommand(eleDec.getElement(), XSDConstants.ABSTRACT_ATTRIBUTE, value, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_ABSTRACT);
      command.setDeleteIfEmpty(true);
      getCommandStack().execute(command);
    }
    else if (e.widget == substGroupCombo)
    {
      IEditorPart editor = getActiveEditor();
      if (editor == null) return;
      ComponentReferenceEditManager manager = (ComponentReferenceEditManager)editor.getAdapter(XSDSubstitutionGroupEditManager.class);

      String selection = substGroupCombo.getText();
      ComponentSpecification newValue;
      IComponentDialog dialog= null;
      if ( selection.equals(Messages._UI_COMBO_BROWSE))
      {
        dialog = manager.getBrowseDialog();
      }
      else if ( selection.equals(Messages._UI_COMBO_NEW))
      {
        dialog = manager.getNewDialog();
      }

      if (dialog != null)
      {
        if (dialog.createAndOpen() == Window.OK)
        {
          newValue = dialog.getSelectedComponent();
          manager.modifyComponentReference(input, newValue);
        }
        else
        {
          substGroupCombo.setText("");
        }
      }
      else //use the value from selected quickPick item
      {
        newValue = getComponentSpecFromQuickPickForValue(selection, manager);
        if (newValue != null)
          manager.modifyComponentReference(input, newValue);
      }
    }
    else if (e.widget == nillableCombo)
    {
        XSDElementDeclaration eleDec = (XSDElementDeclaration) input;
        String value = nillableCombo.getText();

        UpdateAttributeValueCommand command = new UpdateAttributeValueCommand(eleDec.getElement(), XSDConstants.NILLABLE_ATTRIBUTE, value, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_NILLABLE);
        command.setDeleteIfEmpty(true);
        getCommandStack().execute(command);
    }
  }

  public void refresh()
  {
    super.refresh();
    fillSubstitutionGroupCombo();
    setListenerEnabled(false);
    try
    {
      if (input instanceof XSDElementDeclaration)
      {
        XSDElementDeclaration eleDec = (XSDElementDeclaration) input;

        composite.setEnabled(!isReadOnly);
        if (eleDec.getContainer() instanceof XSDSchema) // global element
        {
          abstractCombo.setEnabled(true);
          finalCombo.setEnabled(true);
          substGroupCombo.setEnabled(true);
          nillableCombo.setEnabled(true);
        }
        else
        {
          abstractCombo.setEnabled(false);
          finalCombo.setEnabled(false);
          substGroupCombo.setEnabled(false);
          
          // Nillable when used in a local element declaration can't be set on
          // on elements that use @ref.
       	  nillableCombo.setEnabled(!eleDec.isElementDeclarationReference());
        }

        Element element = eleDec.getElement();
        
        if (element.hasAttribute(XSDConstants.BLOCK_ATTRIBUTE))
        {
          String blockAttValue = element.getAttribute(XSDConstants.BLOCK_ATTRIBUTE);
          blockCombo.setText(blockAttValue);
        }
        else
        {
          blockCombo.setText(EMPTY);
        }
        // We should show the value of the attribute regardless if it is invalid
        // ie. as the user starts typing 'true' in the source, the properties view
        // should show the value dynamically
        if (element.hasAttribute(XSDConstants.NILLABLE_ATTRIBUTE))
        {
          String attrValue = element.getAttribute(XSDConstants.NILLABLE_ATTRIBUTE);
          nillableCombo.setText(attrValue);
        }
        else
        {
          nillableCombo.setText(EMPTY);
        }
  
        if (element.hasAttribute(XSDConstants.FINAL_ATTRIBUTE))
        {
        	String finalAttValue = element.getAttribute(XSDConstants.FINAL_ATTRIBUTE);
            finalCombo.setText(finalAttValue);
        }
        else
        {
          finalCombo.setText(EMPTY);
        }

        if (element.hasAttribute(XSDConstants.ABSTRACT_ATTRIBUTE))
        {
          abstractCombo.setText(element.getAttribute(XSDConstants.ABSTRACT_ATTRIBUTE));
        }
        else
          abstractCombo.setText(EMPTY);

        if (element.hasAttribute(XSDConstants.SUBSTITUTIONGROUP_ATTRIBUTE))
        {
          substGroupCombo.setText(element.getAttribute(XSDConstants.SUBSTITUTIONGROUP_ATTRIBUTE));
        }
        else
        {
          substGroupCombo.setText(EMPTY);
        }
      }
    }
    catch (Exception e)
    {
    }
    setListenerEnabled(true);
  }
  
  private void fillSubstitutionGroupCombo()
  {
    IEditorPart editor = getActiveEditor();
    ComponentReferenceEditManager manager = (ComponentReferenceEditManager)editor.getAdapter(XSDSubstitutionGroupEditManager.class);
    if (manager != null)
    {
      ComponentSpecification[] items = manager.getQuickPicks();

      substGroupCombo.removeAll();
      substGroupCombo.add(Messages._UI_COMBO_BROWSE);
      substGroupCombo.add(Messages._UI_COMBO_NEW);
      for (int i = 0; i < items.length; i++)
      {
        substGroupCombo.add(items[i].getName());
      }
      // Add the current substitution group if needed
      XSDElementDeclaration namedComponent = ((XSDElementDeclaration) input).getSubstitutionGroupAffiliation();
      if (namedComponent != null)
      {
        ComponentSpecification ret = getComponentSpecFromQuickPickForValue(namedComponent.getName(), manager);
        if (ret == null)
        {
          substGroupCombo.add(namedComponent.getQName(xsdSchema));
        }
      }
    }
  }
  
  private ComponentSpecification getComponentSpecFromQuickPickForValue(String value, ComponentReferenceEditManager editManager)
  {
    if (editManager != null)
    {  
      ComponentSpecification[] quickPicks = editManager.getQuickPicks();
      if (quickPicks != null)
      {
        for (int i=0; i < quickPicks.length; i++)
        {
          ComponentSpecification componentSpecification = quickPicks[i];
          if (value != null && value.equals(componentSpecification.getName()))
          {
            return componentSpecification;
          }                
        }  
      }
    }
    return null;
  }
  
  public void dispose()
  {
  	if (substGroupCombo != null && !substGroupCombo.isDisposed())
  	{
		substGroupCombo.removeListener(SWT.Traverse, this);
	}
	super.dispose();
  }

}
