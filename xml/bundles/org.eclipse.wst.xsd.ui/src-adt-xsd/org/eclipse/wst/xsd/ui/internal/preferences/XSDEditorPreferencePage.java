/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsd.ui.internal.preferences;


import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractPreferencePage;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorContextIds;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;


public class XSDEditorPreferencePage extends AbstractPreferencePage
{
  private static final String XML_EDITOR_PREFERENCE_PAGE_ID = "org.eclipse.wst.sse.ui.preferences.xml.source"; //$NON-NLS-1$

  private Button removeUnusedImports;

  private Button automaticallyOpenSchemaLocationDialog;

  protected Control createContents(Composite parent)
  {
    final Composite composite = super.createComposite(parent, 1);
    IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
    helpSystem.setHelp(parent, XSDEditorContextIds.XSDP_EDITOR_PREFERENCE_PAGE);

    new PreferenceLinkArea(composite, SWT.WRAP | SWT.MULTI, XML_EDITOR_PREFERENCE_PAGE_ID, Messages._UI_XML_TEXT_EDITOR_PREFS_LINK,
      (IWorkbenchPreferenceContainer)getContainer(), null).getControl().setLayoutData(GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).create());
    new Label(composite, SWT.NONE).setLayoutData(GridDataFactory.swtDefaults().create());

    createContentsForImportCleanup(composite);

    setSize(composite);
    loadPreferences();

    return composite;
  }

  private boolean getRemoveImportSetting()
  {
    return removeUnusedImports.getSelection();
  }

  private boolean getAutomaticallyOpenSchemaLocationDialogSetting()
  {
    return automaticallyOpenSchemaLocationDialog.getSelection();
  }

  protected void initializeValues()
  {
    IPreferenceStore store = getPreferenceStore();
    removeUnusedImports.setSelection(store.getBoolean(XSDEditorPlugin.CONST_XSD_IMPORT_CLEANUP));
    automaticallyOpenSchemaLocationDialog.setSelection(store.getBoolean(XSDEditorPlugin.CONST_XSD_AUTO_OPEN_SCHEMA_LOCATION_DIALOG));
  }

  protected void performDefaults()
  {
    IPreferenceStore preferenceStore = getPreferenceStore();
    removeUnusedImports.setSelection(preferenceStore.getDefaultBoolean(XSDEditorPlugin.CONST_XSD_IMPORT_CLEANUP));
    automaticallyOpenSchemaLocationDialog.setSelection(preferenceStore.getDefaultBoolean(XSDEditorPlugin.CONST_XSD_AUTO_OPEN_SCHEMA_LOCATION_DIALOG));
    super.performDefaults();
  }

  protected void storeValues()
  {
    saveValuesForImportsCleanup();
  }

  /**
   * Stores the values of the controls back to the preference store.
   */
  private void saveValuesForImportsCleanup()
  {
    IPreferenceStore store = getPreferenceStore();

    store.setValue(XSDEditorPlugin.CONST_XSD_IMPORT_CLEANUP, getRemoveImportSetting());
    store.setValue(XSDEditorPlugin.CONST_XSD_AUTO_OPEN_SCHEMA_LOCATION_DIALOG, getAutomaticallyOpenSchemaLocationDialogSetting());
  }

  /** 
   * The indent is stored in the preference store associated with the XML Schema Model
   */
  public IPreferenceStore getPreferenceStore()
  {
    return XSDEditorPlugin.getPlugin().getPreferenceStore();
  }

  private void createContentsForImportCleanup(Composite parent)
  {
    Group unusedImportGroup = createGroup(parent, 1);
    unusedImportGroup.setText(Messages._UI_GRAPH_DIRECTIVES);

    //GridData
    GridData data = new GridData(SWT.FILL);
    data.verticalAlignment = SWT.CENTER;
    data.horizontalAlignment = SWT.FILL;

    if (removeUnusedImports == null)
    {
      removeUnusedImports = new Button(unusedImportGroup, SWT.CHECK | SWT.LEFT);
      removeUnusedImports.setText(Messages._UI_TEXT_ENABLE_AUTO_IMPORT_CLEANUP);
      removeUnusedImports.setLayoutData(data);

      PlatformUI.getWorkbench().getHelpSystem().setHelp(removeUnusedImports, XSDEditorCSHelpIds.XMLSCHEMAFILES_PREFERENCES__IMPORT_CLEANUP);

      automaticallyOpenSchemaLocationDialog = new Button(unusedImportGroup, SWT.CHECK | SWT.LEFT);
      automaticallyOpenSchemaLocationDialog.setText(Messages._UI_TEXT_ENABLE_AUTO_OPEN_SCHEMA_DIALOG);
      automaticallyOpenSchemaLocationDialog.setLayoutData(GridDataFactory.copyData(data));
    }
  }

  protected void doSavePreferenceStore()
  {
    XSDEditorPlugin.getDefault().savePluginPreferences(); // model
  }

  public boolean performOk()
  {
    boolean result = super.performOk();

    doSavePreferenceStore();

    return result;
  }
}
