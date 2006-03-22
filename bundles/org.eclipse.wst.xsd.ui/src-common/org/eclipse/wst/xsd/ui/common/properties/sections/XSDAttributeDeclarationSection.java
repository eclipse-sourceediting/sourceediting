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
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.xsd.editor.Messages;
import org.eclipse.wst.xsd.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.common.commands.UpdateNameCommand;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class XSDAttributeDeclarationSection extends AbstractSection
{
  protected Text nameText;
  protected Text typeCombo;
  protected Button button;
  static Image browseIcon = XSDEditorPlugin.getImageDescriptor("icons/browsebutton.gif").createImage();
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
    // typeLabel
    // ------------------------------------------------------------------
    getWidgetFactory().createCLabel(composite, typeLabel); //$NON-NLS-1$

    // ------------------------------------------------------------------
    // typeCombo
    // ------------------------------------------------------------------
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;

    typeCombo = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
    typeCombo.setEditable(false);
    // baseTypeCombo.addListener(SWT.Modify, this);
    typeCombo.setLayoutData(data);

    // ------------------------------------------------------------------
    // BaseTypeButton
    // ------------------------------------------------------------------
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;

    button = getWidgetFactory().createButton(composite, "", SWT.PUSH);
    button.setImage(browseIcon);
    button.addSelectionListener(this);
    button.setLayoutData(data);
  }

  /*
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
   */
  public void refresh()
  {
    super.refresh();

    setListenerEnabled(false);

    // refresh name

    nameText.setText("");
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
      Element element = null;
      if (input instanceof XSDAttributeDeclaration)
      {
        XSDAttributeDeclaration xsdAttribute = ((XSDAttributeDeclaration) input).getResolvedAttributeDeclaration();
        isAttributeReference = ((XSDAttributeDeclaration)input).isAttributeDeclarationReference();
        element = xsdAttribute.getElement();
        XSDTypeDefinition typeDef = xsdAttribute.getTypeDefinition();
        boolean isAnonymous = xsdAttribute.getAnonymousTypeDefinition() != null;

        if (isAnonymous)
        {
          typeCombo.setText("**anonymous**"); //$NON-NLS-1$
        }
        else
        {
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
          command = new UpdateNameCommand("Rename", namedComponent, newValue);
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
      setErrorMessage("Error Invalid Name");
      return false;
    }

    return true;
  }

}
