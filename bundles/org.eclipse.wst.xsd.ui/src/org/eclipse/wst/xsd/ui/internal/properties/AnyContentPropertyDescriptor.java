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
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AnyContentPropertyDescriptor extends PropertyDescriptor
{
  Element element;
  /**
   * @param id
   * @param displayName
   */
  public AnyContentPropertyDescriptor(Object id, String displayName, Element element)
  {
    super(id, displayName);
    this.element = element;
  }
  
  public CellEditor createPropertyEditor(Composite parent)
  {
    CellEditor editor = new AnyContentDialogCellEditor(parent);
    if (getValidator() != null)
      editor.setValidator(getValidator());
    return editor;
  }

  public class AnyContentDialogCellEditor extends DialogCellEditor {

    /**
     * Creates a new Font dialog cell editor parented under the given control.
     * The cell editor value is <code>null</code> initially, and has no 
     * validator.
     *
     * @param parent the parent control
     */
    protected AnyContentDialogCellEditor(Composite parent) {
      super(parent);
    }

    /**
     * @see org.eclipse.jface.viewers.DialogCellEditor#openDialogBox(Control)
     */
    protected Object openDialogBox(Control cellEditorWindow)
    {
      Shell shell = Display.getCurrent().getActiveShell();
      
      AnyContentDialog dialog = new AnyContentDialog(shell);
      dialog.setBlockOnOpen(true);
      dialog.create();
      
      String value = (String)getValue();

      int result = dialog.open();

      if (result == Window.OK)
      {
        return dialog.getComment();
      }
      return value;
    }

  }

  public class AnyContentDialog extends org.eclipse.jface.dialogs.Dialog
  {
    protected Text commentField;
    protected Button okButton, cancelButton;
    private String comment;
    
    public AnyContentDialog(Shell shell)
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
        comment = commentField.getText();
      }
      super.buttonPressed(buttonId);
    }

    public String getComment() { return comment; }

    //
    // Create the controls
    //
    public Control createDialogArea(Composite parent)
    {
      Composite client = (Composite)super.createDialogArea(parent);
      getShell().setText(element.getLocalName());
      
      commentField = ViewUtility.createMultiTextField(client, 400, 200, true);
      
      WorkbenchHelp.setHelp(commentField, XSDEditorContextIds.XSDE_ANNOTATION_COMMENT);
      commentField.setToolTipText(XSDEditorPlugin.getXSDString("_UI_TOOLTIP_COMMENT")); //$NON-NLS-1$

      String initialString = (String)getInitialContent();
      commentField.setText(initialString);
      return client;
    }

    private Object getInitialContent()
    {
      Object result = null;
      if (element.hasChildNodes())
       {
        // if the element is Text
        Node node = element.getFirstChild();
        if (node instanceof CharacterData)
         {
          result = ((CharacterData)node).getData();
        }
      }
      else
       {
        result = ""; //$NON-NLS-1$
      }
      return result;
    }
  }
}
