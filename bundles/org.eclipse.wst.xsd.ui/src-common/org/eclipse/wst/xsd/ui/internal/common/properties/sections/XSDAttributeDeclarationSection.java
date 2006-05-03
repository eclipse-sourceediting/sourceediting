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

import org.apache.xerces.util.XMLChar;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.adt.edit.ComponentReferenceEditManager;
import org.eclipse.wst.xsd.ui.internal.adt.edit.IComponentDialog;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateNameCommand;
import org.eclipse.wst.xsd.ui.internal.dialogs.NewTypeDialog;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDTypeReferenceEditManager;
import org.eclipse.wst.xsd.ui.internal.editor.search.XSDSearchListDialogDelegate;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;

public class XSDAttributeDeclarationSection extends AbstractSection
{
  protected Text nameText;
  protected CCombo typeCombo;
  boolean isAttributeReference;
  
  public XSDAttributeDeclarationSection()
  {
    super();
  }

  protected void createContents(Composite parent)
  {
    composite = getWidgetFactory().createFlatFormComposite(parent);

    String typeLabel = Messages.UI_LABEL_TYPE; //$NON-NLS-1$

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    composite.setLayout(gridLayout);

    // ------------------------------------------------------------------
    // NameLabel
    // ------------------------------------------------------------------
    GridData data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    CLabel nameLabel = getWidgetFactory().createCLabel(composite, org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_NAME);
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

    // ------------------------------------------------------------------
    // DummyLabel
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    getWidgetFactory().createCLabel(composite, ""); //$NON-NLS-1$

    // ------------------------------------------------------------------
    // typeLabel
    // ------------------------------------------------------------------
    getWidgetFactory().createCLabel(composite, typeLabel); //$NON-NLS-1$

    // ------------------------------------------------------------------
    // typeCombo
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    typeCombo = getWidgetFactory().createCCombo(composite);
    typeCombo.setLayoutData(data);
    typeCombo.addSelectionListener(this);

  }

  private void fillTypesCombo()
  {
    IEditorPart editor = getActiveEditor();
    XSDTypeReferenceEditManager manager = (XSDTypeReferenceEditManager)editor.getAdapter(XSDTypeReferenceEditManager.class);    
    ComponentSpecification[] items = manager.getQuickPicks();
    
    typeCombo.removeAll();
    typeCombo.add(Messages._UI_ACTION_BROWSE);
    typeCombo.add(Messages._UI_ACTION_NEW);
    for (int i = 0; i < items.length; i++)
    {
      typeCombo.add(items[i].getName());
    }

    XSDAttributeDeclaration namedComponent = ((XSDAttributeDeclaration) input).getResolvedAttributeDeclaration();
    XSDTypeDefinition namedComponentType = namedComponent.getType();
    String currentTypeName = namedComponentType.getQName(xsdSchema); // no prefix
    ComponentSpecification ret = getComponentSpecFromQuickPickForValue(currentTypeName, manager);
    if (ret == null) //not in quickPick
      typeCombo.add(currentTypeName);
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
          if (value.equals(componentSpecification.getName()))
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
    super.refresh();

    setListenerEnabled(false);

    // refresh name

    nameText.setText(""); //$NON-NLS-1$
    if (input instanceof XSDAttributeDeclaration)
    {
      XSDAttributeDeclaration namedComponent = ((XSDAttributeDeclaration) input).getResolvedAttributeDeclaration();

      String name = namedComponent.getName();
      if (name != null)
      {
        nameText.setText(name);
      }
    }

    // refresh type

    typeCombo.setText(""); //$NON-NLS-1$
    if (input != null)
    {
      if (input instanceof XSDAttributeDeclaration)
      {
        XSDAttributeDeclaration xsdAttribute = ((XSDAttributeDeclaration) input).getResolvedAttributeDeclaration();
        isAttributeReference = ((XSDAttributeDeclaration)input).isAttributeDeclarationReference();
        XSDTypeDefinition typeDef = xsdAttribute.getTypeDefinition();
        boolean isAnonymous = xsdAttribute.getAnonymousTypeDefinition() != null;

        if (isAnonymous)
        {
          typeCombo.setText("**anonymous**"); //$NON-NLS-1$
        }
        else
        {
          fillTypesCombo();
          String typeName = ""; //$NON-NLS-1$
          if (typeDef != null)
          {
            typeName = typeDef.getQName(xsdSchema);
            if (typeName == null)
            {
              typeName = ""; //$NON-NLS-1$
            }
            typeCombo.setText(typeName);
          }
          else
          {
            typeCombo.setText(Messages.UI_NO_TYPE); //$NON-NLS-1$
          }
        }
      }
    }

    setListenerEnabled(true);
  }

  public boolean shouldUseExtraSpace()
  {
    return false;
  }
  
  public void doWidgetSelected(SelectionEvent e)
  {
    super.doWidgetSelected(e);
    if (e.widget == typeCombo)
    {
      IEditorPart editor = getActiveEditor();
      if (editor == null) return;
      ComponentReferenceEditManager manager = (ComponentReferenceEditManager)editor.getAdapter(XSDTypeReferenceEditManager.class);    

      String selection = typeCombo.getText();
      ComponentSpecification newValue;
      IComponentDialog dialog= null;
      if ( selection.equals(Messages._UI_ACTION_BROWSE))
      {
        dialog = manager.getBrowseDialog();
        ((XSDSearchListDialogDelegate) dialog).showComplexTypes(false);
      }
      else if ( selection.equals(Messages._UI_ACTION_NEW))
      {
        dialog = manager.getNewDialog();
        ((NewTypeDialog) dialog).allowComplexType(false);
      }

      if (dialog != null)
      {
        if (dialog.createAndOpen() == Window.OK)
        {
          newValue = dialog.getSelectedComponent();
          manager.modifyComponentReference(input, newValue);
        }
      }
      else //use the value from selected quickPick item
      {
        newValue = getComponentSpecFromQuickPickForValue(selection, manager);
        if (newValue != null)
          manager.modifyComponentReference(input, newValue);
      }
//      refresh();
    }
  }

  protected void doHandleEvent(Event event)
  {
    super.doHandleEvent(event);
    if (event.widget == nameText)
    {
      if (!nameText.getEditable())
        return;

      String newValue = nameText.getText().trim();
      if (input instanceof XSDAttributeDeclaration)
      {
        XSDAttributeDeclaration namedComponent = ((XSDAttributeDeclaration) input).getResolvedAttributeDeclaration();

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
        
        if (isAttributeReference)
        {
          XSDAttributeDeclaration attrRef = (XSDAttributeDeclaration)input;
          String qname = attrRef.getResolvedAttributeDeclaration().getQName();
          attrRef.getElement().setAttribute(XSDConstants.REF_ATTRIBUTE, qname);
          
//          TypesHelper helper = new TypesHelper(xsdSchema);
//          List items = new ArrayList();
//          items = helper.getGlobalElements();
//          items.add(0, "");
//          componentNameCombo.setItems((String [])items.toArray(new String[0]));
//
//          refreshRefCombo();
        }

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
    if (nameText != null && !nameText.isDisposed())
      removeListeners(nameText);
    if (typeCombo != null && !typeCombo.isDisposed())
      typeCombo.removeSelectionListener(this);
    super.dispose();
  }

}
