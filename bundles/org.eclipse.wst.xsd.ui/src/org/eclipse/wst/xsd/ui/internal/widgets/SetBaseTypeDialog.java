/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.widgets;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.ViewUtility;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;



public class SetBaseTypeDialog extends Dialog implements SelectionListener
{
  protected Combo baseTypeCombo;
  protected Combo derivedByCombo;
  protected XSDSchema xsdSchema;
  protected Element element;  // the complex type element
  private String type = "";
  private String derivedByString = "";
  
  /**
   * Constructor for SetBaseTypeDialog.
   * @param arg0
   */
  public SetBaseTypeDialog(Shell arg0, XSDSchema xsdSchema, Element element)
  {
    super(arg0);
    this.xsdSchema = xsdSchema;
    this.element = element;
  }

  protected void configureShell(Shell shell)
  {
    super.configureShell(shell);
    shell.setText(XSDEditorPlugin.getXSDString("_UI_LABEL_SET_BASE_TYPE"));
  }


  protected void buttonPressed(int buttonId)
  {
    if (buttonId == Dialog.OK)
    {
      type = baseTypeCombo.getText();
      derivedByString = derivedByCombo.getText();
    }
    super.buttonPressed(buttonId);
  }
  
  public String getBaseType()
  {
    return type;
  }

  public String getDerivedBy()
  {
    return derivedByString;
  }
  
  public void setCurrentBaseType(String type)
  {
    this.type = type;
  }
  
  public void setCurrentDerivedBy(String derivedByString)
  {
    this.derivedByString = derivedByString;
  }

  //
  // Create the controls
  //
  public Control createDialogArea(Composite parent)
  {
    Composite nameComposite = (Composite)super.createDialogArea(parent);
    GridLayout layout = (GridLayout)nameComposite.getLayout();
    layout.numColumns = 2;
    nameComposite.setLayout(layout); 

    ViewUtility utility = new ViewUtility();

    ViewUtility.createLabel(nameComposite, XSDEditorPlugin.getXSDString("_UI_LABEL_BASE_TYPE_WITH_COLON"));
    baseTypeCombo = ViewUtility.createComboBox(nameComposite, true); // readonly
    baseTypeCombo.addSelectionListener(this);
    
    ViewUtility.createLabel(nameComposite, XSDEditorPlugin.getXSDString("_UI_LABEL_DERIVED_BY")); 
    derivedByCombo = ViewUtility.createComboBox(nameComposite, true); // readonly

    derivedByCombo.add(XSDConstants.EXTENSION_ELEMENT_TAG);
    derivedByCombo.add(XSDConstants.RESTRICTION_ELEMENT_TAG);
    derivedByCombo.setText(derivedByString);
    initializeBaseTypeCombo();

    if (type.equals(""))
    {
      derivedByCombo.setText("");
      derivedByCombo.setEnabled(false);
    }
        
    return nameComposite;
  }

  private void initializeBaseTypeCombo()
  {
    ArrayList list = new ArrayList();
    TypesHelper helper = new TypesHelper(xsdSchema);

    String prefix = helper.getPrefix(xsdSchema.getTargetNamespace(), true);

    list.add("");
    list.addAll(helper.getBuiltInTypeNamesList());
    list.addAll(helper.getUserSimpleTypeNamesList());
    list.addAll(helper.getUserComplexTypeNamesList());

    // remove the current CT from the list
    list.remove(prefix + element.getAttribute("name"));

    baseTypeCombo.removeAll();
    for (int i = 0; i < list.size(); i++)
    {
      baseTypeCombo.add(list.get(i).toString());
    }
    baseTypeCombo.setText(type);
    handleBaseTypeComboChange();
  }
  
  private void handleBaseTypeComboChange()
  {
    String tempChoice = baseTypeCombo.getText();
    TypesHelper helper = new TypesHelper(xsdSchema);
    if (helper.getBuiltInTypeNamesList().contains(tempChoice) ||
        helper.getUserSimpleTypeNamesList().contains(tempChoice))
    {
      derivedByCombo.setText(XSDConstants.EXTENSION_ELEMENT_TAG);
      derivedByCombo.setEnabled(false);
    }
    else if (helper.getUserComplexTypeNamesList().contains(tempChoice))
    {
      derivedByCombo.setEnabled(true); 
    }
    else
    {
      derivedByCombo.setText("");
      derivedByCombo.setEnabled(false); 
    }
  }

  public void widgetSelected(SelectionEvent e)
  {
    if (e.widget == baseTypeCombo)
    {
      handleBaseTypeComboChange();
    }
    
  }

  public void widgetDefaultSelected(SelectionEvent e)
  {
    
  }
}
