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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.wizards.RegexWizard;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;


public class PatternPropertySource
  extends BasePropertySource
  implements IPropertySource
{
  /**
   * 
   */
  public PatternPropertySource()
  {
    super();
//    expressionField.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_PATTERN"));    
//    WorkbenchHelp.setHelp(expressionField, XSDEditorContextIds.XSDE_PATTERN_VALUE);
//    WorkbenchHelp.setHelp(activateWizardButton, XSDEditorContextIds.XSDE_PATTERN_REGULAR);
//    activateWizardButton.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_REGEX_WIZARD_BUTTON"));
  }
  /**
   * @param viewer
   * @param xsdSchema
   */
  public PatternPropertySource(Viewer viewer, XSDSchema xsdSchema)
  {
    super(viewer, xsdSchema);
  }
  /**
   * @param xsdSchema
   */
  public PatternPropertySource(XSDSchema xsdSchema)
  {
    super(xsdSchema);
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
   */
  public Object getEditableValue()
  {
    return null;
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
   */
  public IPropertyDescriptor[] getPropertyDescriptors()
  {
    List list = new ArrayList();
    // Create a descriptor and set a category

    PatternTextPropertyDescriptor patternDescriptor =
    new PatternTextPropertyDescriptor(
        XSDConstants.VALUE_ATTRIBUTE, 
        XSDConstants.VALUE_ATTRIBUTE);
    list.add(patternDescriptor);
    
    IPropertyDescriptor[] result = new IPropertyDescriptor[list.size()];
    list.toArray(result);
    return result;
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
   */
  public Object getPropertyValue(Object id)
  {
    Object result = null;
    if (id instanceof String)
    {
      result = element.getAttribute((String) id);
    }
    if (result == null)
    {
      result = "";
    }
    return result;
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
   */
  public boolean isPropertySet(Object id)
  {
    return false;
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
   */
  public void resetPropertyValue(Object id)
  {
  }
  /* (non-Javadoc)
   * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
   */
  public void setPropertyValue(Object id, Object value)
  {
    if (value == null)
    {
      value = "";
    }
    if (value instanceof String)
    {
      if (((String) id).equals(XSDConstants.VALUE_ATTRIBUTE))
      { 
        beginRecording(XSDEditorPlugin.getXSDString("_UI_PATTERN_VALUE_CHANGE"), element);
        element.setAttribute(XSDConstants.VALUE_ATTRIBUTE, (String)value);
        endRecording(element);
      }
    }
    Runnable delayedUpdate = new Runnable()
    {
      public void run()
      {
        if (viewer != null)
          viewer.refresh();
      }
    };
    Display.getCurrent().asyncExec(delayedUpdate);
    
  }

  public class PatternTextPropertyDescriptor extends PropertyDescriptor
  {
    /**
     * @param id
     * @param displayName
     */
    public PatternTextPropertyDescriptor(Object id, String displayName)
    {
      super(id, displayName);
    }
    
    public CellEditor createPropertyEditor(Composite parent)
    {
      // CellEditor editor = new PatternTextCellEditor(parent);
      CellEditor editor = new PatternDialogCellEditor(parent);
      if (getValidator() != null)
        editor.setValidator(getValidator());
      return editor;
    }
  }   

  public class PatternDialogCellEditor extends DialogCellEditor {

    /**
     * Creates a new Font dialog cell editor parented under the given control.
     * The cell editor value is <code>null</code> initially, and has no 
     * validator.
     *
     * @param parent the parent control
     */
    protected PatternDialogCellEditor(Composite parent) {
      super(parent);
    }

    /**
     * @see org.eclipse.jface.viewers.DialogCellEditor#openDialogBox(Control)
     */
    protected Object openDialogBox(Control cellEditorWindow)
    {
      String initialValue = element.getAttribute(XSDConstants.VALUE_ATTRIBUTE);
      if (initialValue == null)
      {
        initialValue = "";
      }
      RegexWizard wizard = new RegexWizard(initialValue);
      Shell shell = Display.getCurrent().getActiveShell();
      WizardDialog wizardDialog = new WizardDialog(shell, wizard);
      wizardDialog.create();
      
      String value = (String)getValue();

      int result = wizardDialog.open();

      if (result == Window.OK)
      {
        return wizard.getPattern();
      }
      return value;
    }

  }
  
//  class PatternTextCellEditor extends OptionsTextCellEditor
//  {
//    protected Button fixedButton, defaultButton;
//    
//    public PatternTextCellEditor(Composite parent)
//    {
//      super(parent);
//    }
//
//    protected Control createControl(Composite parent)
//    {
//      isTextReadOnly = false;
//      return super.createControl(parent);
//    }
//
//    protected void openDialog()
//    {
//      RegexWizard wizard = new RegexWizard(element.getAttribute(XSDConstants.VALUE_ATTRIBUTE));
//      Shell shell = Display.getCurrent().getActiveShell();
//      WizardDialog wizardDialog = new WizardDialog(shell, wizard);
//      wizardDialog.create();
//      
//      dialog = wizardDialog.getShell();
//      Display display = dialog.getDisplay();
//      dialog.addShellListener(new ShellAdapter()
//      {
//        public void shellDeactivated(ShellEvent e)
//        {
//          cancel();
//        }
//      });
//
//      int result = wizardDialog.open();
//
//      if (result == Window.OK)
//      {
//        fText.setText(wizard.getPattern());
//        applyEditorValueAndDeactivate();
//      }
//      
//    }
//
//    protected void cancel()
//    {
//      super.cancel();
//    }
//
//    void applyEditorValueAndDeactivate()
//    {
//      String value = fText.getText();
//      doSetValue(value);
//      fireApplyEditorValue();
//      deactivate();
//    }
//    
//    protected Object doGetValue()
//    { 
//      fValue = fText.getText();
//      return fText.getText();
//    }
//
//  }
}
