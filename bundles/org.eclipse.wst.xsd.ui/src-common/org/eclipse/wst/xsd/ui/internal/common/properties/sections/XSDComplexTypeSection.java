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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.ui.internal.search.dialogs.ComponentSpecification;
import org.eclipse.wst.xsd.ui.internal.adt.edit.ComponentReferenceEditManager;
import org.eclipse.wst.xsd.ui.internal.adt.edit.IComponentDialog;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateComplexTypeDerivationBy;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateNameCommand;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDComplexTypeBaseTypeEditManager;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDDerivationMethod;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;

public class XSDComplexTypeSection extends RefactoringSection implements SelectionListener
{
  protected Text nameText;
  protected CCombo baseTypeCombo;
  protected CCombo derivedByCombo;
  private String derivedByChoicesComboValues[] = { "", XSDConstants.RESTRICTION_ELEMENT_TAG, XSDConstants.EXTENSION_ELEMENT_TAG }; //$NON-NLS-1$

  public XSDComplexTypeSection()
  {
    super();
  }

  /**
   * Contents of the property tab
   * 
   * NameLabel NameText DummyLabel BaseTypeLabel BaseTypeCombo BaseTypeButton
   * DerivedByLabel DerivedByCombo
   */
  public void createContents(Composite parent)
  {
    composite = getWidgetFactory().createFlatFormComposite(parent);

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    composite.setLayout(gridLayout);

    // ------------------------------------------------------------------
    // NameLabel
    // ------------------------------------------------------------------
    GridData data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    CLabel nameLabel = getWidgetFactory().createCLabel(composite, Messages.UI_LABEL_NAME);
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
    		XSDEditorCSHelpIds.GENERAL_TAB__COMPLEX_TYPE__NAME);

    // ------------------------------------------------------------------
    // Refactor/rename hyperlink 
    // ------------------------------------------------------------------
    createRenameHyperlink(composite);
    
    // ------------------------------------------------------------------
    // BaseTypeLabel
    // ------------------------------------------------------------------

    getWidgetFactory().createCLabel(composite, Messages.UI_LABEL_INHERIT_FROM); //$NON-NLS-1$

    // ------------------------------------------------------------------
    // BaseTypeCombo
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;

    baseTypeCombo = getWidgetFactory().createCCombo(composite);
    baseTypeCombo.setEditable(false);
    baseTypeCombo.setLayoutData(data);
    baseTypeCombo.addSelectionListener(this);
    baseTypeCombo.addListener(SWT.Traverse, this);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(baseTypeCombo,
    		XSDEditorCSHelpIds.GENERAL_TAB__COMPLEX_TYPE__INHERIT_FROM);


    // ------------------------------------------------------------------
    // Spacer label
    // ------------------------------------------------------------------
    getWidgetFactory().createCLabel(composite, "");

    // ------------------------------------------------------------------
    // DerivedByLabel
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;

    CLabel derivedByLabel = getWidgetFactory().createCLabel(composite, Messages.UI_LABEL_INHERIT_BY); //$NON-NLS-1$
    derivedByLabel.setLayoutData(data);

    // ------------------------------------------------------------------
    // DerivedByCombo
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;

    derivedByCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
    derivedByCombo.setLayoutData(data);
    derivedByCombo.setItems(derivedByChoicesComboValues);
    derivedByCombo.addSelectionListener(this);
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(derivedByCombo,
    		XSDEditorCSHelpIds.GENERAL_TAB__COMPLEX_TYPE__INHERIT_BY);

    // ------------------------------------------------------------------
    // Spacer label
    // ------------------------------------------------------------------
    getWidgetFactory().createCLabel(composite, "");
  }

  /*
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
   */
  public void refresh()
  {
    super.refresh();
    if (Display.getCurrent() == null)
      return;

    setListenerEnabled(false);
    showLink(!hideHyperLink);

    try
    {
      nameText.setText(""); //$NON-NLS-1$
      baseTypeCombo.setText(""); //$NON-NLS-1$
      fillTypesCombo();

      if (input instanceof XSDComplexTypeDefinition)
      {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) input;
        String name = complexType.getName();
        if (name == null)
          name = ""; //$NON-NLS-1$

        boolean isAnonymousType = name.equals("") ? true : false; //$NON-NLS-1$
        if (isAnonymousType)
        {
          nameText.setText("**anonymous**"); //$NON-NLS-1$
          nameText.setEditable(false);
        }
        else
        {
          nameText.setText(name);
          nameText.setEditable(true);
        }

        XSDTypeDefinition baseTypeDefinition = complexType.getBaseTypeDefinition();
        String baseType = ""; //$NON-NLS-1$
        if (baseTypeDefinition != null)
        {
          baseType = baseTypeDefinition.getName();
          if (baseType == null)
          {
            baseType = ""; //$NON-NLS-1$
          }
          else if (baseType.equals("anyType"))
          {
            baseType = ""; //$NON-NLS-1$
          }
        }
        baseTypeCombo.setText(baseType);

        derivedByCombo.setText(""); //$NON-NLS-1$
        int derivationMethod = complexType.getDerivationMethod().getValue();
        XSDDOMHelper domHelper = new XSDDOMHelper();
        if(domHelper.getDerivedByElementFromComplexType(complexType.getElement()) != null) {
	        if (derivationMethod == XSDDerivationMethod.EXTENSION)
	        {
	          derivedByCombo.setText(XSDConstants.EXTENSION_ELEMENT_TAG);
	        }
	        else if (derivationMethod == XSDDerivationMethod.RESTRICTION)
	        {
	          derivedByCombo.setText(XSDConstants.RESTRICTION_ELEMENT_TAG);
	        }
        }
      }

    }
    finally
    {
      setListenerEnabled(true);
    }
  }

  public void doWidgetDefaultSelected(SelectionEvent e)
  {
    if (e.widget == baseTypeCombo)
    {
      String selection = baseTypeCombo.getText();
      if (shouldPerformComboSelection(SWT.DefaultSelection, selection))
        handleWidgetSelection(e);
    } else
    {
      handleWidgetSelection(e);
    }
  }

  public void doWidgetSelected(SelectionEvent e)
  {
    if (e.widget == baseTypeCombo)
    {
      String selection = baseTypeCombo.getText();
      if (shouldPerformComboSelection(SWT.Selection, selection))
        handleWidgetSelection(e);
    } else
    {
      handleWidgetSelection(e);
    }
  }
  
  private void handleWidgetSelection(SelectionEvent e)
  {
    if (e.widget == baseTypeCombo)
    {
      IEditorPart editor = getActiveEditor();
      if (editor == null) return;
      ComponentReferenceEditManager manager = (ComponentReferenceEditManager)editor.getAdapter(XSDComplexTypeBaseTypeEditManager.class);

      String selection = baseTypeCombo.getText();
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
          refresh();
        }
      }
    }
    else if (e.widget == derivedByCombo)
    {
      XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) input;
      String value = derivedByCombo.getText();
      Command command = new UpdateComplexTypeDerivationBy(complexType, value);

      if (getCommandStack() != null)
      {
        getCommandStack().execute(command);
      }
    }
    super.doWidgetSelected(e);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISection#shouldUseExtraSpace()
   */
  public boolean shouldUseExtraSpace()
  {
    return false;
  }

  public void dispose()
  {
    super.dispose();
    if (baseTypeCombo != null && !baseTypeCombo.isDisposed())
      baseTypeCombo.removeListener(SWT.Traverse, this);
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
      if (!nameText.getEditable())
        return;

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
  
  public void setInput(IWorkbenchPart part, ISelection selection)
  {
    super.setInput(part, selection);
    setListenerEnabled(false);
    if (input instanceof XSDComplexTypeDefinition)
    {
    	XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) input;
      hideHyperLink = !(complexType.getContainer() instanceof XSDSchema);
      
    }    
    
    setListenerEnabled(true);
  }
  
  private void fillTypesCombo()
  {
    baseTypeCombo.removeAll();
    baseTypeCombo.add(Messages._UI_COMBO_BROWSE);
    baseTypeCombo.add(Messages._UI_COMBO_NEW);
    // Add the current Type of this attribute if needed
    XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) input;
    XSDTypeDefinition baseType = complexType.getBaseType();
    if (baseType != null && baseType.getQName() != null)
    {
      String currentTypeName = baseType.getQName(xsdSchema); //no prefix
      if (currentTypeName != null && !currentTypeName.equals("anyType"))
        baseTypeCombo.add(currentTypeName);
    }
  }
}
