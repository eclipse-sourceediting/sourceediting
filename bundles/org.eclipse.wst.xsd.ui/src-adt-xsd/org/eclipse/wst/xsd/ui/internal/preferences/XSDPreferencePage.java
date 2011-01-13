/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
David Carver, Standards for Technology in Automotive Retail, bug 1147033     
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorContextIds;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.ViewUtility;

public class XSDPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, Listener 
{
  Text indentTextField;
  String indentString;
  Text schemaNsPrefixField;
  Text defaultTargetNamespaceText;
  Button qualifyXSDLanguage;

  /**
   * Creates preference page controls on demand.
   *   @param parent  the parent for the preference page
   */
  protected Control createContents(final Composite parent)
  {
    WorkbenchHelp.setHelp(parent, XSDEditorContextIds.XSDP_PREFERENCE_PAGE);

    Group group = createGroup(parent, 2);   
    group.setText(Messages._UI_TEXT_XSD_NAMESPACE_PREFIX);

    qualifyXSDLanguage = ViewUtility.createCheckBox(group, Messages._UI_QUALIFY_XSD);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(qualifyXSDLanguage,
    		XSDEditorCSHelpIds.XMLSCHEMAFILES_PREFERENCES__QUALIFY_XMLSCHEMA_LANGUAGE_CONSTRUCTS); 
    ViewUtility.createLabel(group, " ");

    createLabel(group, Messages._UI_TEXT_XSD_DEFAULT_PREFIX);
    schemaNsPrefixField = createTextField(group);
    schemaNsPrefixField.addKeyListener(new KeyAdapter()
    {
      public void keyPressed(KeyEvent e)
      {
        setValid(true);
      }      
    });
    PlatformUI.getWorkbench().getHelpSystem().setHelp(schemaNsPrefixField,
    		XSDEditorCSHelpIds.XMLSCHEMAFILES_PREFERENCES__XML_SCHEMA_LANGUAGE_CONSTRUCTS_PREFIX); 
    
    createLabel(group, Messages._UI_TEXT_XSD_DEFAULT_TARGET_NAMESPACE);
    defaultTargetNamespaceText = createTextField(group);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(defaultTargetNamespaceText,
    		XSDEditorCSHelpIds.XMLSCHEMAFILES_PREFERENCES__DEFAULT_TARGETNAMESPACE); 
    
    
    initializeValues();

    applyDialogFont(parent);

    return new Composite(parent, SWT.NULL);
  }

  private Group createGroup(Composite parent, int numColumns) 
  {
    Group group = new Group(parent, SWT.NULL);

    GridLayout layout = new GridLayout();
    layout.numColumns = numColumns;
    group.setLayout(layout);

    GridData data = new GridData();
    data.verticalAlignment = GridData.FILL;
    data.horizontalAlignment = GridData.FILL;
    data.grabExcessHorizontalSpace = true;
    group.setLayoutData(data);
    
    return group;
  }

  private Text createTextField(Composite parent) 
  {
     Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
     GridData data = new GridData();
     data.verticalAlignment = GridData.FILL;
     data.horizontalAlignment = GridData.FILL;
     data.grabExcessHorizontalSpace = true;
     text.setLayoutData(data);

     return text;
  }

  private Label createLabel(Composite parent, String text) 
  {
    Label label = new Label(parent, SWT.LEFT);
    label.setText(text);
    
    GridData data = new GridData();
    data.verticalAlignment = GridData.CENTER;
    data.horizontalAlignment = GridData.FILL;
    label.setLayoutData(data);
    
    return label;
  }
  
  /**
   * Does anything necessary because the default button has been pressed.
   */
  protected void performDefaults() 
  {
    super.performDefaults();
    initializeDefaults();
    checkValues();
  }

  /**
   * Do anything necessary because the OK button has been pressed.
   *  @return whether it is okay to close the preference page
   */
  public boolean performOk() 
  {
    if (checkValues())
    {
      storeValues();    
      return true;
    }
    return false;
  }

  protected void performApply()
  {
    if (checkValues())
    {
      storeValues();    
    }
  }

  /**
   * Handles events generated by controls on this page.
   *   @param e  the event to handle
   */
  public void handleEvent(Event e) 
  {
  }

  /**
   * @see IWorkbenchPreferencePage
   */
  public void init(IWorkbench workbench)
  { 
  }

  /** 
   * The indent is stored in the preference store associated with the XML Schema Model
   */
  public IPreferenceStore getPreferenceStore()
  {
    return XSDEditorPlugin.getPlugin().getPreferenceStore();
  }

  /**
   * Initializes states of the controls using default values
   * in the preference store.
   */
  private void initializeDefaults() 
  {
    schemaNsPrefixField.setText(getPreferenceStore().getDefaultString(XSDEditorPlugin.CONST_XSD_DEFAULT_PREFIX_TEXT));
    qualifyXSDLanguage.setSelection(getPreferenceStore().getDefaultBoolean(XSDEditorPlugin.CONST_XSD_LANGUAGE_QUALIFY));
    defaultTargetNamespaceText.setText(getPreferenceStore().getDefaultString(XSDEditorPlugin.CONST_DEFAULT_TARGET_NAMESPACE));
  }

  /**
   * Initializes states of the controls from the preference store.
   */
  private void initializeValues() 
  {
    IPreferenceStore store = getPreferenceStore();
    schemaNsPrefixField.setText(store.getString(XSDEditorPlugin.CONST_XSD_DEFAULT_PREFIX_TEXT));
    qualifyXSDLanguage.setSelection(store.getBoolean(XSDEditorPlugin.CONST_XSD_LANGUAGE_QUALIFY));
    defaultTargetNamespaceText.setText(store.getString(XSDEditorPlugin.CONST_DEFAULT_TARGET_NAMESPACE));
  }

  /**
   * Stores the values of the controls back to the preference store.
   */
  private void storeValues() 
  {
    IPreferenceStore store = getPreferenceStore();

    store.setValue(XSDEditorPlugin.CONST_XSD_DEFAULT_PREFIX_TEXT, getXMLSchemaPrefix());
    store.setValue(XSDEditorPlugin.CONST_XSD_LANGUAGE_QUALIFY, getQualify());
    store.setValue(XSDEditorPlugin.CONST_DEFAULT_TARGET_NAMESPACE, getXMLSchemaTargetNamespace());
      
    XSDEditorPlugin.getPlugin().savePluginPreferences();
  }

  public String getXMLSchemaPrefix()
  {
    String prefix = schemaNsPrefixField.getText();
    if (prefix == null || prefix.equals("")) 
    {
      return "xsd";
    }
    return prefix;
  }

  public boolean getQualify()
  {
    return qualifyXSDLanguage.getSelection();
  }
  
  /**
   * Get the xml schema default target namespace
   */
  public String getXMLSchemaTargetNamespace()
  {
  	String targetNamespace = defaultTargetNamespaceText.getText();
    if (targetNamespace == null || targetNamespace.equals("")) 
    {
      return XSDEditorPlugin.DEFAULT_TARGET_NAMESPACE;
    }
    return targetNamespace;
  }
  
  public boolean checkValues()
  {
// KCPort TODO    String errorMessage = ValidateHelper.checkXMLName(schemaNsPrefixField.getText());
	 String errorMessage = null;

    if (errorMessage == null || errorMessage.length() == 0)
    {
      setErrorMessage(null);
      setValid(true);
      return true;
    }
    else
    {
      setErrorMessage(Messages._ERROR_LABEL_INVALID_PREFIX);
      setValid(false);
      return false;
    }
  }
}
