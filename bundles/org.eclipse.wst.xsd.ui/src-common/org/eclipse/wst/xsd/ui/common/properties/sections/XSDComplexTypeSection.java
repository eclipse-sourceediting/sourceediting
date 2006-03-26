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
package org.eclipse.wst.xsd.ui.common.properties.sections;

import org.apache.xerces.util.XMLChar;
import org.eclipse.core.resources.IFile;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.xsd.editor.Messages;
import org.eclipse.wst.xsd.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.common.commands.UpdateNameCommand;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDDerivationMethod;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class XSDComplexTypeSection extends AbstractSection implements SelectionListener
{
  protected Text nameText;
  protected Text baseTypeCombo;
  protected CCombo derivedByCombo;
  protected Button button;
  private String derivedByChoicesComboValues[] = { "", XSDConstants.RESTRICTION_ELEMENT_TAG, XSDConstants.EXTENSION_ELEMENT_TAG };

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
    CLabel nameLabel = getWidgetFactory().createCLabel(composite, "Name:"); //$NON-NLS-1$
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
    // BaseTypeLabel
    // ------------------------------------------------------------------

    getWidgetFactory().createCLabel(composite, Messages.UI_LABEL_INHERIT_FROM); //$NON-NLS-1$

    // ------------------------------------------------------------------
    // BaseTypeCombo
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;

    baseTypeCombo = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
    baseTypeCombo.setEditable(false);
    // baseTypeCombo.addListener(SWT.Modify, this);
    baseTypeCombo.setLayoutData(data);

    // ------------------------------------------------------------------
    // BaseTypeButton
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;

    button = getWidgetFactory().createButton(composite, "", SWT.PUSH);
    button.setImage(XSDEditorPlugin.getXSDImage("icons/browsebutton.gif"));
    button.addSelectionListener(this);
    button.setLayoutData(data);

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
    // derivedByCombo.addSelectionListener(this);

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

    try
    {
      nameText.setText("");
      baseTypeCombo.setText(""); //$NON-NLS-1$

      if (input instanceof XSDComplexTypeDefinition)
      {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) input;
        String name = complexType.getName();
        if (name == null)
          name = "";

        boolean isAnonymousType = name.equals("") ? true : false;
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
            baseType = "";
          }
        }
        baseTypeCombo.setText(baseType);

        derivedByCombo.setText(""); //$NON-NLS-1$
        int derivationMethod = complexType.getDerivationMethod().getValue();
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
    finally
    {
      setListenerEnabled(true);
    }
  }

  /**
   * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
   */
  public void widgetSelected(SelectionEvent e)
  {
    XSDComplexTypeDefinition xsdComplexType = (XSDComplexTypeDefinition) input;
    Element ctElement = xsdComplexType.getElement();
    if (e.widget == button)
    {
      Shell shell = Display.getCurrent().getActiveShell();
      Element element = null;
      if (xsdComplexType.getContent() != null)
      {
        element = xsdComplexType.getContent().getElement();
      }

      // SimpleContentBaseTypeOptionsDialog dialog = new
      // SimpleContentBaseTypeOptionsDialog(shell, element, BASE_TYPE_ID,
      // xsdComplexType.getSchema());
      // dialog.setBlockOnOpen(true);
      // dialog.create();
      // int result = dialog.open();

      IFile currentIFile = ((IFileEditorInput) getActiveEditor().getEditorInput()).getFile();

      XSDSchema schema = xsdComplexType.getSchema();
      
      // issuec (cs) need to move the common.ui's selection dialog
      /*
      XSDComponentSelectionProvider provider = new XSDComponentSelectionProvider(currentIFile, schema);
      XSDComponentSelectionDialog dialog = new XSDComponentSelectionDialog(shell, Messages.UI_LABEL_SET_TYPE, provider);
      provider.setDialog(dialog);
      dialog.setBlockOnOpen(true);
      dialog.create();
      int result = dialog.open();

      if (result == Window.OK)
      {
        XMLComponentSpecification spec = dialog.getSelection();
        XSDSetTypeHelper helper = new XSDSetTypeHelper(currentIFile, schema);
        helper.addImportIfNecessary(element, spec);

        String typeString = helper.getPrefixedTypeName(spec);

        String derivedBy = "";
        int derivationMethod = xsdComplexType.getDerivationMethod().getValue();
        if (derivationMethod == XSDDerivationMethod.EXTENSION)
        {
          derivedBy = XSDConstants.EXTENSION_ELEMENT_TAG;
        }
        else if (derivationMethod == XSDDerivationMethod.RESTRICTION)
        {
          derivedBy = XSDConstants.RESTRICTION_ELEMENT_TAG;
        }

        SetBaseTypeAction setBaseTypeAction = new SetBaseTypeAction("_UI_LABEL_SET_BASE_TYPE"); //$NON-NLS-1$
        setBaseTypeAction.setXSDSchema(xsdSchema);
        setBaseTypeAction.setComplexTypeElement(ctElement);
        setBaseTypeAction.setType(typeString);
        setBaseTypeAction.setDerivedBy(derivedBy);
        setBaseTypeAction.performAction();

      }
      refresh();
      // }
      // else if (e.widget == derivedByCombo)
      // {
      // Element contentModelElement =
      // getDomHelper().getContentModelFromParent(ctElement);
      // String baseType = getDomHelper().getBaseType(contentModelElement);
      // beginRecording(XSDEditorPlugin.getXSDString("_UI_DERIVEDBY_CHANGE"),
      // ctElement); //$NON-NLS-1$
      // if (contentModelElement != null)
      // {
      // getDomHelper().changeDerivedByType(contentModelElement,
      // derivedByCombo.getText(), baseType);
      // }
      // endRecording(ctElement);       
       */
    }
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
  }

  public void doHandleEvent(Event event)
  {
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
          command = new UpdateNameCommand("Rename", namedComponent, newValue);
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
      setErrorMessage("Error Invalid Name");
      return false;
    }

    return true;
  }

}
