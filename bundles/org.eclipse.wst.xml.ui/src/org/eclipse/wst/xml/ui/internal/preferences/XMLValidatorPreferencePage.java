/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     David Carver - STAR - [205989] - [validation] validate XML after XInclude resolution
 *******************************************************************************/

package org.eclipse.wst.xml.ui.internal.preferences;


import org.eclipse.core.runtime.Preferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractPreferencePage;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.eclipse.wst.xml.ui.internal.editor.IHelpContextIds;


public class XMLValidatorPreferencePage extends AbstractPreferencePage {
  private Combo fIndicateNoGrammar;

  private Button fHonourAllSchemaLocations;

  private Button fUseXinclude;
 
  private static final String[] SEVERITIES = {XMLUIMessages.Indicate_no_grammar_specified_severities_error, XMLUIMessages.Indicate_no_grammar_specified_severities_warning, XMLUIMessages.Indicate_no_grammar_specified_severities_ignore};

  protected Control createContents(Composite parent) {
    Composite composite = (Composite)super.createContents(parent);
    PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.XML_PREFWEBX_VALIDATOR_HELPID);
    createContentsForValidatingGroup(composite);

    setSize(composite);
    loadPreferences();

    return composite;
  }

  protected void createContentsForValidatingGroup(Composite parent) {
    Group validatingGroup = createGroup(parent, 2);
    ((GridLayout)validatingGroup.getLayout()).makeColumnsEqualWidth = false;
    validatingGroup.setText(XMLUIMessages.Validating_files);

    if (fIndicateNoGrammar == null) {
      createLabel(validatingGroup, XMLUIMessages.Indicate_no_grammar_specified);
      fIndicateNoGrammar = createCombo(validatingGroup, SEVERITIES);
    }
    if (fUseXinclude == null) {
      fUseXinclude = createCheckBox(validatingGroup, XMLUIMessages.Use_XInclude);
      ((GridData)fUseXinclude.getLayoutData()).horizontalSpan = 2;
    }
    if (fHonourAllSchemaLocations == null) {
      fHonourAllSchemaLocations = createCheckBox(validatingGroup, XMLUIMessages.Honour_all_schema_locations);
      ((GridData)fHonourAllSchemaLocations.getLayoutData()).horizontalSpan = 2;
    }
  }

  /**
   * @param parent 
   * @return
   */
  private Combo createCombo(Composite parent, String[] items) {
    Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
    combo.setItems(items);

    //GridData
    GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
    combo.setLayoutData(data);

    return combo;
  }

  protected void initializeValues() {
    initializeValuesForValidatingGroup();
  }

  protected void initializeValuesForValidatingGroup() {
    Preferences modelPreferences = getModelPreferences();
    int indicateNoGrammarButtonSelected = modelPreferences.getInt(XMLCorePreferenceNames.INDICATE_NO_GRAMMAR);
    boolean useXIncludeButtonSelected = modelPreferences.getBoolean(XMLCorePreferenceNames.USE_XINCLUDE);

    if (fIndicateNoGrammar != null) {
      fIndicateNoGrammar.select(2 - indicateNoGrammarButtonSelected);
      fIndicateNoGrammar.setText(SEVERITIES[2 - indicateNoGrammarButtonSelected]);
    }
    if (fUseXinclude != null) {
      fUseXinclude.setSelection(useXIncludeButtonSelected);
    }

    boolean honourAllSelected = modelPreferences.getBoolean(XMLCorePreferenceNames.HONOUR_ALL_SCHEMA_LOCATIONS);
    if (fHonourAllSchemaLocations != null) {
      fHonourAllSchemaLocations.setSelection(honourAllSelected);
    }
  }

  protected void performDefaultsForValidatingGroup() {
    Preferences modelPreferences = getModelPreferences();
    int indicateNoGrammarButtonSelected = modelPreferences.getDefaultInt(XMLCorePreferenceNames.INDICATE_NO_GRAMMAR);
    boolean useXIncludeButtonSelected = modelPreferences.getDefaultBoolean(XMLCorePreferenceNames.USE_XINCLUDE);

    if (fIndicateNoGrammar != null) {
      fIndicateNoGrammar.setSelection(new Point(indicateNoGrammarButtonSelected, 2 - indicateNoGrammarButtonSelected));
      fIndicateNoGrammar.setText(SEVERITIES[indicateNoGrammarButtonSelected]);
    }
    if (fUseXinclude != null) {
      fUseXinclude.setSelection(useXIncludeButtonSelected);
    }

    boolean honourAllButtonSelected = modelPreferences.getDefaultBoolean(XMLCorePreferenceNames.HONOUR_ALL_SCHEMA_LOCATIONS);
    if (fHonourAllSchemaLocations != null) {
      fHonourAllSchemaLocations.setSelection(honourAllButtonSelected);
    }
  }

  protected void storeValuesForValidatingGroup()
  {
    Preferences modelPreferences = getModelPreferences();
    if (fIndicateNoGrammar != null) {
      int warnNoGrammarButtonSelected = 2 - fIndicateNoGrammar.getSelectionIndex();
      modelPreferences.setValue(XMLCorePreferenceNames.INDICATE_NO_GRAMMAR, warnNoGrammarButtonSelected);
    }
    if (fUseXinclude != null) {
      boolean useXIncludeButtonSelected = fUseXinclude.getSelection();
      modelPreferences.setValue(XMLCorePreferenceNames.USE_XINCLUDE, useXIncludeButtonSelected);
    }
    if (fHonourAllSchemaLocations != null) {
      boolean honourAllButtonSelected = fHonourAllSchemaLocations.getSelection();
      modelPreferences.setValue(XMLCorePreferenceNames.HONOUR_ALL_SCHEMA_LOCATIONS, honourAllButtonSelected);
    }
  }
  
  protected void storeValues() {
    storeValuesForValidatingGroup();
  }

  protected void performDefaults() {
    performDefaultsForValidatingGroup();
    super.performDefaults();
  }
  
  protected Preferences getModelPreferences() {
    return XMLCorePlugin.getDefault().getPluginPreferences();
  }  
  
  protected void doSavePreferenceStore() {
      XMLCorePlugin.getDefault().savePluginPreferences(); // model
  }

  public boolean performOk() {
    boolean result = super.performOk();

    doSavePreferenceStore();

    return result;
  }
}
