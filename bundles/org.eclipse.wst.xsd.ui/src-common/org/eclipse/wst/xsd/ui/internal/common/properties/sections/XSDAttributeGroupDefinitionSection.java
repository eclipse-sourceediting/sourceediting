/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.util.XMLChar;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateNameCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDDirectivesManager;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class XSDAttributeGroupDefinitionSection extends RefactoringSection
{
  protected Text nameText;
  protected CCombo refCombo;
  boolean isReference;

  public XSDAttributeGroupDefinitionSection()
  {
    super();
  }

  protected void createContents(Composite parent)
  {
    composite = getWidgetFactory().createFlatFormComposite(parent);

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    composite.setLayout(gridLayout);

    if (isReference)
    {
      // ------------------------------------------------------------------
      // Ref Label
      // ------------------------------------------------------------------
      GridData data = new GridData();
      data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
      data.grabExcessHorizontalSpace = false;
      CLabel refLabel = getWidgetFactory().createCLabel(composite, XSDConstants.REF_ATTRIBUTE + ":");  //$NON-NLS-1$
      refLabel.setLayoutData(data);

      // ------------------------------------------------------------------
      // Ref Combo
      // ------------------------------------------------------------------

      data = new GridData();
      data.grabExcessHorizontalSpace = true;
      data.horizontalAlignment = GridData.FILL;

      refCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
      refCombo.addSelectionListener(this);
      refCombo.setLayoutData(data);
      
      PlatformUI.getWorkbench().getHelpSystem().setHelp(refCombo,
      		XSDEditorCSHelpIds.GENERAL_TAB__ATTRIBUTEGROUP_REFS__REF);
    }
    else
    {
      // ------------------------------------------------------------------
      // NameLabel
      // ------------------------------------------------------------------
      GridData data = new GridData();
      data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
      data.grabExcessHorizontalSpace = false;
      CLabel nameLabel = getWidgetFactory().createCLabel(composite, Messages._UI_LABEL_NAME);
      nameLabel.setLayoutData(data);

      // ------------------------------------------------------------------
      // NameText
      // ------------------------------------------------------------------
      data = new GridData();
      data.grabExcessHorizontalSpace = true;
      data.horizontalAlignment = GridData.FILL;
      nameText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
      nameText.setLayoutData(data);
      applyAllListeners(nameText);
      
      PlatformUI.getWorkbench().getHelpSystem().setHelp(nameText,
      		XSDEditorCSHelpIds.GENERAL_TAB__ATTRIBUTEGROUP__NAME);

      // ------------------------------------------------------------------
      // Refactor/rename hyperlink 
      // ------------------------------------------------------------------      
      if (!hideHyperLink)
      {
        createRenameHyperlink(composite);
        setRenameHyperlinkEnabled(!isReference);
      }
      else
      {
        getWidgetFactory().createCLabel(composite, "");
      }
    }
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

    XSDNamedComponent namedComponent = (XSDNamedComponent)input;
    
    if (isReference)
    {
      Element element = namedComponent.getElement();
      if (element != null)
      {
        String attrValue = element.getAttribute(XSDConstants.REF_ATTRIBUTE);
        if (attrValue == null)
        {
          attrValue = ""; //$NON-NLS-1$
        }
        refCombo.setText(attrValue);
      }
    }
    else
    {
      // refresh name
      nameText.setText(""); //$NON-NLS-1$

      String name = namedComponent.getName();
      if (name != null)
      {
        nameText.setText(name);
      }
    }
    setListenerEnabled(true);
  }

  public void setInput(IWorkbenchPart part, ISelection selection)
  {
    super.setInput(part, selection);
    init();
    relayout();
    
    if (isReference)
    {
      TypesHelper helper = new TypesHelper(xsdSchema);
      List items = new ArrayList();
      items = helper.getGlobalAttributeGroups();
      items.add(0, ""); //$NON-NLS-1$
      refCombo.setItems((String [])items.toArray(new String[0]));
    }
  }
  
  protected void relayout()
  {
    Composite parentComposite = composite.getParent();
    parentComposite.getParent().setRedraw(false);

    if (parentComposite != null && !parentComposite.isDisposed())
    {
      Control[] children = parentComposite.getChildren();
      for (int i = 0; i < children.length; i++)
      {
        children[i].dispose();
      }
    }

    // Now initialize the new handler
    createContents(parentComposite);
    parentComposite.getParent().layout(true, true);

    // Now turn painting back on
    parentComposite.getParent().setRedraw(true);
    refresh();
  }
  
  protected void init()
  {
    if (input instanceof XSDAttributeGroupDefinition)
    {
      XSDAttributeGroupDefinition group = (XSDAttributeGroupDefinition) input;
      isReference = group.isAttributeGroupDefinitionReference();
      hideHyperLink = isReference;
    }
  }
  
  public void doHandleEvent(Event event)
  {
    super.doHandleEvent(event);
    if (event.widget == nameText)
    {
      String newValue = nameText.getText().trim();
      if (input instanceof XSDNamedComponent)
      {
        XSDNamedComponent namedComponent = (XSDNamedComponent) input;
        if (!validateSection())
          return;

        Command command = null;

        // Make sure an actual name change has taken place
        String oldName = namedComponent.getName();
        if (!newValue.equals(oldName))
        {
          command = new UpdateNameCommand(Messages._UI_ACTION_RENAME, namedComponent, newValue);
        }

        if (command != null && getCommandStack() != null)
        {
          getCommandStack().execute(command);
        }
        // doReferentialIntegrityCheck(namedComponent, newValue);
      }
    }
  }

  protected boolean validateSection()
  {
    if (nameText == null || nameText.isDisposed())
      return true;

    setErrorMessage(null);

    String name = nameText.getText().trim();

    // validate against NCName
    if (name.length() < 1 || !XMLChar.isValidNCName(name))
    {
      setErrorMessage(Messages._UI_ERROR_INVALID_NAME);
      return false;
    }

    return true;
  }

  public void doWidgetSelected(SelectionEvent e)
  {
    if (e.widget == refCombo)
    {
      String newValue = refCombo.getText();
      if (input instanceof XSDNamedComponent)
      {
        XSDNamedComponent namedComponent = (XSDNamedComponent)input;
        Element element = namedComponent.getElement();

        if (namedComponent instanceof XSDAttributeGroupDefinition)
        {
          element.setAttribute(XSDConstants.REF_ATTRIBUTE, newValue);
          XSDDirectivesManager.removeUnusedXSDImports(namedComponent.getSchema());
        }
      }
    }
    super.doWidgetSelected(e);
  }


  public void dispose()
  {
    if (nameText != null && !nameText.isDisposed())
      removeListeners(nameText);
    super.dispose();
  }

}
