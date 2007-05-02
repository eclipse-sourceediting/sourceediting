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
package org.eclipse.wst.xsd.ui.internal.widgets;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.ViewUtility;


/**
 * Dialog to help define a list of enumerations
 * for a join. This might be replaced once we know how to
 * initiate a drag tracker
 */

public class EnumerationsDialog extends org.eclipse.jface.dialogs.Dialog
{
  public EnumerationsDialog(Shell shell)
  {
    super(shell);
  }

  protected void configureShell(Shell shell)
  {
    super.configureShell(shell);
    shell.setText(XSDEditorPlugin.getXSDString("_UI_ENUMERATIONS_DIALOG_TITLE"));
  }

  protected void buttonPressed(int buttonId)
  {
    if (buttonId == Window.OK)
    {
      text = textField.getText();
      delimiter = delimiterField.getText();
      isPreserve = preserveWhitespace.getSelection();
    }
    super.buttonPressed(buttonId);
  }

  private String text, delimiter;
  private boolean isPreserve;
  public String getText() { return text; }
  public String getDelimiter() { return delimiter; }
  public boolean isPreserveWhitespace() { return isPreserve; }

  private Text textField;
  private Button preserveWhitespace;
  private Combo delimiterField;
  //
  // Create the controls
  //
  public Control createDialogArea(Composite parent)
  {
    Control[] tabOrder = new Control[3];
  	int tabIndex = 0;
    Composite client = (Composite)super.createDialogArea(parent);
    GridLayout layout = (GridLayout)client.getLayout();
    layout.numColumns = 2;
    client.setLayout(layout); 

    textField = ViewUtility.createWrappedMultiTextField(client, 400, 20, true);
    GridData gd = (GridData) textField.getLayoutData();
    gd.horizontalSpan = 2;
    tabOrder[tabIndex++] = textField;
    PlatformUI.getWorkbench().getHelpSystem().setHelp(textField, XSDEditorCSHelpIds.ADD_ENUMERATIONS__NO_NAME);

    ViewUtility.createLabel(client, XSDEditorPlugin.getXSDString("_UI_LABEL_DELIMITER_CHAR"));
    delimiterField = ViewUtility.createComboBox(client, false);
    gd = (GridData) delimiterField.getLayoutData();
    gd.grabExcessHorizontalSpace = false;
    gd.horizontalAlignment = GridData.BEGINNING;
    gd.widthHint = 30;
    tabOrder[tabIndex++] = delimiterField;
    PlatformUI.getWorkbench().getHelpSystem().setHelp(delimiterField, XSDEditorCSHelpIds.ADD_ENUMERATIONS__DELIMITER_CHARS);

    // add default delimiters
    delimiterField.add(":");
    delimiterField.add(",");
    delimiterField.add(" ");
    // set the current one to be ','
    delimiterField.setText(",");

    preserveWhitespace = ViewUtility.createCheckBox(client, XSDEditorPlugin.getXSDString("_UI_LABEL_PRESERVE_WHITESPACE"));
    gd = (GridData) preserveWhitespace.getLayoutData();
    gd.horizontalSpan = 2;
    tabOrder[tabIndex++] = preserveWhitespace;
    PlatformUI.getWorkbench().getHelpSystem().setHelp(preserveWhitespace, XSDEditorCSHelpIds.ADD_ENUMERATIONS__PRESERVE_LEAD_AND_TRAIL_WHITESPACES);
    
    client.setTabList(tabOrder);

    return client;
  }
}
