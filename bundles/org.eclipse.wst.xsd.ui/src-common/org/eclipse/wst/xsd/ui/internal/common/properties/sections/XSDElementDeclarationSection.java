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

import org.apache.xerces.util.XMLChar;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.adt.edit.ComponentReferenceEditManager;
import org.eclipse.wst.xsd.ui.internal.adt.edit.IComponentDialog;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateNameCommand;
import org.eclipse.wst.xsd.ui.internal.dialogs.NewTypeDialog;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.wst.xsd.ui.internal.editor.XSDElementReferenceEditManager;
import org.eclipse.wst.xsd.ui.internal.editor.XSDTypeReferenceEditManager;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDSearchListDialogDelegate;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class XSDElementDeclarationSection extends MultiplicitySection
{
  protected Text nameText;
  protected CCombo typeCombo;
  protected CCombo componentNameCombo;
  boolean isElementReference;
  protected String typeName = ""; //$NON-NLS-1$

  private XSDTypeDefinition typeDefinition;

  public XSDElementDeclarationSection()
  {
    super();
  }

  /**
   * Contents of the property tab
   */
  public void createContents(Composite parent)
  {
    composite = getWidgetFactory().createFlatFormComposite(parent);
    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();

    String typeLabel = Messages.UI_LABEL_TYPE; //$NON-NLS-1$

    GridData data = new GridData();

    GridLayout gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginBottom = 0;
    gridLayout.numColumns = 3;
    composite.setLayout(gridLayout);
    
      // ------------------------------------------------------------------
      // NameLabel
      // ------------------------------------------------------------------

      data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
      data.grabExcessHorizontalSpace = false;
      CLabel nameLabel = factory.createCLabel(composite, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_NAME);
      nameLabel.setLayoutData(data);

      // ------------------------------------------------------------------
      // NameText
      // ------------------------------------------------------------------
      data = new GridData();
      data.grabExcessHorizontalSpace = true;
      data.horizontalAlignment = GridData.FILL;
      nameText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
      nameText.setLayoutData(data);
      nameText.setEnabled(!isElementReference);
      applyAllListeners(nameText);   
      PlatformUI.getWorkbench().getHelpSystem().setHelp(nameText,
        		XSDEditorCSHelpIds.GENERAL_TAB__ELEMENT__NAME);
     
      // ------------------------------------------------------------------
      // Refactor/rename hyperlink 
      // ------------------------------------------------------------------
      if (!hideHyperLink)
      {
        createRenameHyperlink(composite);
        setRenameHyperlinkEnabled(!isElementReference);
      }
      else
      {
        getWidgetFactory().createCLabel(composite, "");
      }

    // ------------------------------------------------------------------
    // Ref Label
    // ------------------------------------------------------------------
    if (isElementReference)
    {
      data = new GridData();
      data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
      data.grabExcessHorizontalSpace = false;
      CLabel refLabel = getWidgetFactory().createCLabel(composite, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_REFERENCE);
      refLabel.setLayoutData(data);

      // ------------------------------------------------------------------
      // Ref Combo
      // ------------------------------------------------------------------

      data = new GridData();
      data.grabExcessHorizontalSpace = true;
      data.horizontalAlignment = GridData.FILL;

      componentNameCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
      componentNameCombo.addSelectionListener(this);
      componentNameCombo.addListener(SWT.Traverse, this);
      componentNameCombo.setLayoutData(data);
      
      PlatformUI.getWorkbench().getHelpSystem().setHelp(componentNameCombo,
      		XSDEditorCSHelpIds.GENERAL_TAB__ELEMENT__REFERENCE);

      // dummy
      getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$
    }

    // ------------------------------------------------------------------
    // typeLabel
    // ------------------------------------------------------------------
    getWidgetFactory().createCLabel(composite, typeLabel);

    // ------------------------------------------------------------------
    // typeCombo
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;

    typeCombo = getWidgetFactory().createCCombo(composite); //$NON-NLS-1$
    typeCombo.setEditable(false);
    typeCombo.setLayoutData(data);
    typeCombo.addSelectionListener(this);
    typeCombo.addListener(SWT.Traverse, this);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(typeCombo,
    		XSDEditorCSHelpIds.GENERAL_TAB__ELEMENT__TYPE);
    
    // ------------------------------------------------------------------
    // DummyLabel
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$

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
    		XSDEditorCSHelpIds.GENERAL_TAB__ELEMENT__MIN_OCCURENCE);    

    // ------------------------------------------------------------------
    // DummyLabel
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$

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
    		XSDEditorCSHelpIds.GENERAL_TAB__ELEMENT__MAX_OCCURENCE);    
  }

  public void setInput(IWorkbenchPart part, ISelection selection)
  {
    super.setInput(part, selection);
    setListenerEnabled(false);
    init();
    relayout();
    
    setListenerEnabled(true);
  }
  
  protected void init()
  {
    if (input instanceof XSDElementDeclaration)
    {
      XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) input;
      isElementReference = xsdElementDeclaration.isElementDeclarationReference();

      hideHyperLink = !xsdElementDeclaration.isGlobal() || isElementReference;
      typeDefinition = xsdElementDeclaration.getResolvedElementDeclaration().getTypeDefinition();
    }
  }
  
  private void fillTypesCombo()
  {
    IEditorPart editor = getActiveEditor();
    ComponentReferenceEditManager manager = (ComponentReferenceEditManager)editor.getAdapter(XSDTypeReferenceEditManager.class);
    if (manager != null)
    {
      ComponentSpecification[] items = manager.getQuickPicks();

      typeCombo.removeAll();
      typeCombo.add(Messages._UI_COMBO_BROWSE);
      typeCombo.add(Messages._UI_COMBO_NEW);
      for (int i = 0; i < items.length; i++)
      {
        typeCombo.add(items[i].getName());
      }
      // Add the current Type of this element if needed
      XSDElementDeclaration namedComponent = ((XSDElementDeclaration) input).getResolvedElementDeclaration();
      XSDTypeDefinition td = namedComponent.getType();
      if (td != null)
      {
        String currentTypeName = td.getQName(xsdSchema);
        if (currentTypeName == null) // anonymous type
          currentTypeName = "**Anonymous**";
        ComponentSpecification ret = getComponentSpecFromQuickPickForValue(currentTypeName, manager);
        if (ret == null && currentTypeName != null) // not in quickPick
        {
          typeCombo.add(currentTypeName);
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
  
  /*
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
   */
  public void refresh()
  {
    setListenerEnabled(false);

    super.refresh();

    XSDElementDeclaration xsdElementDeclaration = ((XSDElementDeclaration) input).getResolvedElementDeclaration();

    // refresh name
    nameText.setText(""); //$NON-NLS-1$
    typeCombo.setText(""); //$NON-NLS-1$
    String name = xsdElementDeclaration.getName();
    if (name != null)
    {
      nameText.setText(name);
    }
    
    if (isElementReference)
    {
      refreshRefCombo();
    }

    // refresh type
    if (input != null)
    {
      if (input instanceof XSDElementDeclaration)
      {
        boolean isAnonymous = xsdElementDeclaration.getAnonymousTypeDefinition() != null;
        //XSDTypeDefinition typeDef = XSDUtils.getResolvedType(xsdElementDeclaration);
        XSDTypeDefinition typeDef = xsdElementDeclaration.getResolvedElementDeclaration().getTypeDefinition();
         
        if (typeDef != null)
          typeName = typeDef.getQName(xsdSchema);

        if (typeName == null)
        {
          typeName = ""; //$NON-NLS-1$
        }

        fillTypesCombo();
        if (isAnonymous)
        {
        	typeCombo.setText("**Anonymous**");
          typeName = "**Anonymous**";
        }
        else
        {
          if (typeDefinition != null)
          {
            typeCombo.setText(typeName);
          }
          else
          {
            typeCombo.setText(Messages.UI_NO_TYPE); //$NON-NLS-1$
          }
        }

        maxCombo.setEnabled(!xsdElementDeclaration.isGlobal() || isElementReference);
        minCombo.setEnabled(!xsdElementDeclaration.isGlobal() || isElementReference);
      }
    }

    // refresh min max
    refreshMinMax();

    setListenerEnabled(true);

  }

  public boolean shouldUseExtraSpace()
  {
    return false;
  }

  public void doWidgetDefaultSelected(SelectionEvent e)
  {
    if (e.widget == typeCombo)
    {
      String selection = typeCombo.getText();
      if (shouldPerformComboSelection(SWT.DefaultSelection, selection))
        handleWidgetSelection(e);
    } else if (e.widget == componentNameCombo)
    {
      String selection = componentNameCombo.getText();
      if (shouldPerformComboSelection(SWT.DefaultSelection, selection))
        handleWidgetSelection(e);
    } else
    {
      handleWidgetSelection(e);
    }
  }

  public void doWidgetSelected(SelectionEvent e)
  {
    if (e.widget == typeCombo)
    {
      String selection = typeCombo.getText();
      if (shouldPerformComboSelection(SWT.Selection, selection))
        handleWidgetSelection(e);
    } else if (e.widget == componentNameCombo)
    {
      String selection = componentNameCombo.getText();
      if (shouldPerformComboSelection(SWT.Selection, selection))
        handleWidgetSelection(e);
    } else
    {
      handleWidgetSelection(e);
    }
  }

  private void handleWidgetSelection(SelectionEvent e)
  {
    if (e.widget == typeCombo)
    {
      IEditorPart editor = getActiveEditor();
      if (editor == null) return;
      ComponentReferenceEditManager manager = (ComponentReferenceEditManager)editor.getAdapter(XSDTypeReferenceEditManager.class);
      String selection = typeCombo.getText();
      ComponentSpecification newValue;
      IComponentDialog dialog= null;
      if ( selection.equals(Messages._UI_COMBO_BROWSE))
      {
        dialog = manager.getBrowseDialog();
        ((XSDSearchListDialogDelegate) dialog).showComplexTypes(true);
      }
      else if ( selection.equals(Messages._UI_COMBO_NEW))
      {
        dialog = manager.getNewDialog();
        ((NewTypeDialog) dialog).allowComplexType(true);
      }

      if (dialog != null)
      {
        if (dialog.createAndOpen() == Window.OK)
        {
          newValue = dialog.getSelectedComponent();
          XSDElementDeclaration elementDeclaration = ((XSDElementDeclaration) input).getResolvedElementDeclaration();
          manager.modifyComponentReference(elementDeclaration, newValue);
        }
        else
        {
          typeCombo.setText(typeName );
        }
      }
      else //use the value from selected quickPick item
      {
        newValue = getComponentSpecFromQuickPickForValue(selection, manager);
        if (newValue != null)
          manager.modifyComponentReference(input, newValue);
      }
    }
    else if (e.widget == componentNameCombo)
    {
      IEditorPart editor = getActiveEditor();
      if (editor == null) return;
      ComponentReferenceEditManager manager = (ComponentReferenceEditManager)editor.getAdapter(XSDElementReferenceEditManager.class);

      String selection = componentNameCombo.getText();
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
          componentNameCombo.setText("");
        }
      }
      else //use the value from selected quickPick item
      {
        newValue = getComponentSpecFromQuickPickForValue(selection, manager);
        if (newValue != null)
          manager.modifyComponentReference(input, newValue);
      }
    }
    else if (e.widget == maxCombo)
    {
      updateMaxAttribute();
    }
    else if (e.widget == minCombo)
    {
      updateMinAttribute();
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

  public void doHandleEvent(Event event)
  {
    if (event.type == SWT.Traverse) {
      if (event.detail == SWT.TRAVERSE_ARROW_NEXT || event.detail == SWT.TRAVERSE_ARROW_PREVIOUS) {
        isTraversing = true;
        return;
      }
    }
    super.doHandleEvent(event);
    if (event.widget == nameText)
    {
      String newValue = nameText.getText().trim();
      if (input instanceof XSDElementDeclaration)
      {
        XSDElementDeclaration namedComponent = ((XSDElementDeclaration) input).getResolvedElementDeclaration();
        if (!validateSection())
          return;

        Command command = null;

        // Make sure an actual name change has taken place
        String oldName = namedComponent.getName();
        if (!newValue.equals(oldName))
        {
          command = new UpdateNameCommand(org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_ACTION_RENAME, namedComponent, newValue);
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
      setErrorMessage(org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_ERROR_INVALID_NAME);
      return false;
    }

    return true;
  }

  public void dispose()
  {
    if (componentNameCombo != null && !componentNameCombo.isDisposed())
    {
      componentNameCombo.removeSelectionListener(this);
      componentNameCombo.removeListener(SWT.Traverse, this);
    }
    if (minCombo != null && !minCombo.isDisposed())
      minCombo.removeSelectionListener(this);
    if (maxCombo != null && !maxCombo.isDisposed())
      maxCombo.removeSelectionListener(this);
    if (typeCombo != null && !typeCombo.isDisposed())
    {
      typeCombo.removeSelectionListener(this);
      typeCombo.removeListener(SWT.Traverse, this);
    }
    if (nameText != null && !nameText.isDisposed())
      removeListeners(nameText);
    super.dispose();
  }

  protected void refreshRefCombo()
  {
    componentNameCombo.setText(""); //$NON-NLS-1$
    fillElementsCombo();
  }
  
  private void fillElementsCombo()
  {
    IEditorPart editor = getActiveEditor();
    ComponentReferenceEditManager manager = (ComponentReferenceEditManager)editor.getAdapter(XSDElementReferenceEditManager.class);    
    if (manager != null)
    {
      componentNameCombo.removeAll();
      componentNameCombo.add(Messages._UI_COMBO_BROWSE);
      componentNameCombo.add(Messages._UI_COMBO_NEW);
      ComponentSpecification[] quickPicks = manager.getQuickPicks();
      if (quickPicks != null)
      {
        for (int i = 0; i < quickPicks.length; i++)
        {
          ComponentSpecification componentSpecification = quickPicks[i];
          componentNameCombo.add(componentSpecification.getName());
        }
      }
      ComponentSpecification[] history = manager.getHistory();
      if (history != null)
      {
        for (int i = 0; i < history.length; i++)
        {
          ComponentSpecification componentSpecification = history[i];
          componentNameCombo.add(componentSpecification.getName());
        }
      }
    }
    XSDElementDeclaration namedComponent = (XSDElementDeclaration) input;
    Element element = namedComponent.getElement();
    
    if (element != null)
    {
      String attrValue = element.getAttribute(XSDConstants.REF_ATTRIBUTE);
      if (attrValue == null)
      {
        attrValue = ""; //$NON-NLS-1$
      }
      ComponentSpecification ret = getComponentSpecFromQuickPickForValue(attrValue, manager);
      if (ret == null)
      {
        componentNameCombo.add(attrValue);
      }
      componentNameCombo.setText(attrValue);
    }
  }
}
