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
package org.eclipse.wst.xsd.ui.internal.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.ViewUtility;


public class XSDLocationChoicePage extends WizardPage 
{
  protected Button radioButton1;
  protected Button radioButton2;
    
  public XSDLocationChoicePage()
  {
    super("XSDLocationChoicePage");

    this.setTitle(XSDEditorPlugin.getXSDString("_UI_WIZARD_INCLUDE_FILE_TITLE"));
    this.setDescription(XSDEditorPlugin.getXSDString("_UI_WIZARD_INCLUDE_FILE_DESC"));
  }
    
  public boolean isPageComplete()
  {
    return true;
  }
    
  public void createControl(Composite parent)
  {
    Composite base = new Composite(parent, SWT.NONE);
    base.setLayout(new GridLayout());
      
    ViewUtility.createLabel(base, XSDEditorPlugin.getXSDString("_UI_LABEL_INCLUDE_URL_FILE"));
    Composite radioButtonsGroup = ViewUtility.createComposite(base, 1, true);

    radioButton1 = ViewUtility.createRadioButton(radioButtonsGroup, 
                                                 XSDEditorPlugin.getXSDString("_UI_RADIO_FILE"));
      
    radioButton2 = ViewUtility.createRadioButton(radioButtonsGroup,
                                                 XSDEditorPlugin.getXSDString("_UI_RADIO_URL"));

    radioButton1.setSelection(true);

    setControl(base);
  }

  // actions on finish
  public boolean performFinish()
  {
    return true;
  }

  public boolean isURL()
  {
    return radioButton2.getSelection();
  }
}
