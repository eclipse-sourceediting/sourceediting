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
package org.eclipse.wst.xsd.ui.internal.properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.wst.xsd.ui.internal.XSDEditorContextIds;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.ViewUtility;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;


public class FixedOrDefaultTextPropertyDescriptor extends PropertyDescriptor
{
  protected static String choice = "";
  Element element;
  /**
   * @param id
   * @param displayName
   */
  public FixedOrDefaultTextPropertyDescriptor(Object id, String displayName, Element element)
  {
    super(id, displayName);
    this.element = element;
  }
  
  public CellEditor createPropertyEditor(Composite parent)
  {
    CellEditor editor = new FixedOrDefaultTextCellEditor(parent);
    if (getValidator() != null)
      editor.setValidator(getValidator());
    return editor;
  }

//    public void setChoice(String newChoice)
//    {
//      choice = newChoice;
//    }

  public Object getId()
  {
    Attr fixedAttr = element.getAttributeNode(XSDConstants.FIXED_ATTRIBUTE);
    Attr defaultAttr = element.getAttributeNode(XSDConstants.DEFAULT_ATTRIBUTE);
    if (fixedAttr != null)
    {
      choice = "fixed";
    }
    else if (defaultAttr != null) // what if both attributes were specified?  Use default...
    {
      choice = "default";
    }
    else
    {
      choice = "";
    }

    if (choice.equals("fixed"))
     {
      return "fixed";
    }
    else if (choice.equals("default"))
     {
      return "default";
    }
    else
     {
      return super.getId();
    }
  }
  
  public String getDisplayName()
  {
    Attr fixedAttr = element.getAttributeNode(XSDConstants.FIXED_ATTRIBUTE);
    Attr defaultAttr = element.getAttributeNode(XSDConstants.DEFAULT_ATTRIBUTE);
    if (fixedAttr != null)
    {
      choice = "fixed";
    }
    else if (defaultAttr != null) // what if both attributes were specified?  Use default...
     {
      choice = "default";
    }
    else
    {
      choice = "fixed/default";
    }

    if (choice.equals("fixed"))
     {
      return "fixed";
    }
    else if (choice.equals("default"))
     {
      return "default";
    }
    else
     {
      return super.getDisplayName();
    }
  }

  class FixedOrDefaultTextCellEditor extends DialogCellEditor
  {
    public FixedOrDefaultTextCellEditor(Composite parent)
    {
      super(parent);
    }

    protected Object openDialogBox(Control cellEditorWindow)
    {
	    Shell shell = Display.getCurrent().getActiveShell();
	    
	    FixedOrDefaultDialog dialog = new FixedOrDefaultDialog(shell);

	    dialog.setBlockOnOpen(true);
	    dialog.create();
	    
	    String value = (String)getValue();
	
	    int result = dialog.open();
	    
	    if (result == Window.OK)
	    {
	      String newValue = dialog.getValue();
        fireApplyEditorValue();
	    }
	    deactivate();
      return null;
	  }
  }
  
  class FixedOrDefaultDialog extends Dialog implements SelectionListener
  {
    private int FIXED = 0;
    private int DEFAULT = 1;
    private int type;
    private int value;
    protected Button fixedButton, defaultButton;
    protected Text valueField;
    protected String valueString = "";
    
    public FixedOrDefaultDialog(Shell shell)
    {
      super(shell);
    }

    protected void configureShell(Shell shell)
    {
      super.configureShell(shell);
    }

    protected void buttonPressed(int buttonId)
    {
      if (buttonId == Dialog.OK)
      {
        valueString = valueField.getText();
        applyEditorValueAndDeactivate();
      }
      super.buttonPressed(buttonId);
    }

    public String getValue() { return valueString; }
    public String getType() { return type == FIXED? "fixed" : "default"; }

    //
    // Create the controls
    //
    public Control createDialogArea(Composite parent)
    {
      Composite client = (Composite)super.createDialogArea(parent);
      getShell().setText(XSDEditorPlugin.getXSDString("_UI_LABEL_FIXEDORDEFAULT_VALUE"));

      GridLayout gl = new GridLayout(1, true);
//      gl.marginHeight = 0;
//      gl.marginWidth = 0;
//      gl.horizontalSpacing = 0;
//      gl.verticalSpacing = 0;
      client.setLayout(gl);

      GridData gd = new GridData();
      gd.grabExcessHorizontalSpace = true;
      gd.grabExcessVerticalSpace = true;
      gd.horizontalAlignment = GridData.FILL;
      gd.verticalAlignment = GridData.FILL;
      gd.horizontalIndent = 0;
      client.setLayoutData(gd);
      
//      isTextReadOnly = false;

      fixedButton = ViewUtility.createRadioButton(client, XSDEditorPlugin.getXSDString("_UI_FIXED"));
//    WorkbenchHelp.setHelp(fixedButton, XSDEditorContextIds.XSDE_ELEMENT_FIXED);
      
      defaultButton = ViewUtility.createRadioButton(client, XSDEditorPlugin.getXSDString("_UI_DEFAULT"));
//    WorkbenchHelp.setHelp(defaultButton, XSDEditorContextIds.XSDE_ELEMENT_DEFAULT);

      valueField = ViewUtility.createTextField(client, 30);
      
//    WorkbenchHelp.setHelp(valueField, XSDEditorContextIds.XSDE_ELEMENT_VALUE);
//    valueField.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_ELEMENT_VALUE"));

      WorkbenchHelp.setHelp(fixedButton, XSDEditorContextIds.XSDE_ATTRIBUTE_FIXED);
      WorkbenchHelp.setHelp(defaultButton, XSDEditorContextIds.XSDE_ATTRIBUTE_DEFAULT);
      // WorkbenchHelp.setHelp(valueField, XSDEditorContextIds.XSDE_ATTRIBUTE_VALUE);
      
      Attr fixedAttr = element.getAttributeNode(XSDConstants.FIXED_ATTRIBUTE);
      Attr defaultAttr = element.getAttributeNode(XSDConstants.DEFAULT_ATTRIBUTE);

      if (fixedAttr != null)
      {
        fixedButton.setSelection(true);
        defaultButton.setSelection(false);
        choice = "fixed";
        type = FIXED;
        valueField.setText(element.getAttribute("fixed"));
        valueField.setFocus();
        valueField.selectAll();
      }
      if (defaultAttr != null) // what if both attributes were specified?  Use default...
       {
        fixedButton.setSelection(false);
        defaultButton.setSelection(true);
        choice = "default";
        type = DEFAULT;
        valueField.setText(element.getAttribute("default"));
        valueField.setFocus();
        valueField.selectAll();
      }

      fixedButton.addSelectionListener(this);
      defaultButton.addSelectionListener(this);
      return client;
    }

    void applyEditorValueAndDeactivate()
    {
      String value = valueField.getText();
      if (value != null && value.length() > 0)
      {
        choice = type == FIXED? "fixed" : "default";
      }
      if (value != null && value.length() > 0)
      {
        if (choice.equals("fixed"))
        {
          element.removeAttribute(XSDConstants.DEFAULT_ATTRIBUTE);
          element.setAttribute(XSDConstants.FIXED_ATTRIBUTE, value);
        }
        else if (choice.equals("default"))
        {
          element.removeAttribute(XSDConstants.FIXED_ATTRIBUTE);
          element.setAttribute(XSDConstants.DEFAULT_ATTRIBUTE, value);
        }
      }
      if (value.equals(""))
      {
        choice = "";
        element.removeAttribute(XSDConstants.DEFAULT_ATTRIBUTE);
        element.removeAttribute(XSDConstants.FIXED_ATTRIBUTE);
      }
    }

    public void widgetSelected(SelectionEvent e)
    {
      if (e.widget == fixedButton && fixedButton.getSelection())
      {
        type = FIXED;
        choice = "fixed";
      }
      else if (e.widget == defaultButton && defaultButton.getSelection())
      {
        type = DEFAULT;
        choice = "default";
      }
    }

  	public void widgetDefaultSelected(SelectionEvent e)
    {
    }

  }
  
  
}
