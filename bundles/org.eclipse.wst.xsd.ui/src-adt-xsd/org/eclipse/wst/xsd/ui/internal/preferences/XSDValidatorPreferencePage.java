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


import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.layout.GridDataFactory;
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
import org.eclipse.wst.xsd.core.internal.XSDCorePlugin;
import org.eclipse.wst.xsd.core.internal.preferences.XSDCorePreferenceNames;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorContextIds;


public class XSDValidatorPreferencePage extends AbstractPreferencePage
{
  private static final String XML_VALIDATOR_PREFERENCE_PAGE_ID = "org.eclipse.wst.sse.ui.preferences.xml.validation"; //$NON-NLS-1$

  private Button fullSchemaConformance = null;

  protected Control createContents(Composite parent)
  {
    final Composite composite = super.createComposite(parent, 1);
    IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();

    helpSystem.setHelp(parent, XSDEditorContextIds.XSDP_VALIDATOR_PREFERENCE_PAGE);

    new PreferenceLinkArea(composite, SWT.WRAP | SWT.MULTI, XML_VALIDATOR_PREFERENCE_PAGE_ID, Messages._UI_XML_VALIDATOR_PREFS_LINK,
      (IWorkbenchPreferenceContainer)getContainer(), null).getControl().setLayoutData(GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).create());
    new Label(composite, SWT.NONE).setLayoutData(GridDataFactory.swtDefaults().create());

    createContentsForValidatingGroup(composite);

    setSize(composite);
    loadPreferences();

    return composite;
  }

  private void createContentsForValidatingGroup(Composite parent)
  {
    Group validatingGroup = createGroup(parent, 1);
    validatingGroup.setText(Messages._UI_VALIDATING_FILES);

    GridData data = new GridData(SWT.FILL);
    data.verticalAlignment = SWT.CENTER;
    data.horizontalAlignment = SWT.FILL;

    if (fullSchemaConformance == null)
    {
      fullSchemaConformance = new Button(validatingGroup, SWT.CHECK | SWT.LEFT);
      fullSchemaConformance.setLayoutData(GridDataFactory.copyData(data));
      fullSchemaConformance.setText(Messages._UI_FULL_CONFORMANCE);
    }
  }

  protected void performDefaults()
  {
    fullSchemaConformance.setSelection(getModelPreferences().getDefaultBoolean(XSDCorePreferenceNames.FULL_SCHEMA_CONFORMANCE));
    super.performDefaults();
  }

  protected void initializeValues()
  {
    initializeValuesForValidatingGroup();
    super.initializeValues();
  }

  protected void initializeValuesForValidatingGroup()
  {
    fullSchemaConformance.setSelection(getModelPreferences().getBoolean(XSDCorePreferenceNames.FULL_SCHEMA_CONFORMANCE));
  }

  protected Preferences getModelPreferences()
  {
    return XSDCorePlugin.getDefault().getPluginPreferences();
  }

  protected void storeValuesForValidatingGroup()
  {
    if (fullSchemaConformance != null)
    {
      boolean fullSchemaConformanceSelected = fullSchemaConformance.getSelection();
      getModelPreferences().setValue(XSDCorePreferenceNames.FULL_SCHEMA_CONFORMANCE, fullSchemaConformanceSelected);
    }
  }

  protected void storeValues()
  {
    storeValuesForValidatingGroup();
  }

  protected void doSavePreferenceStore()
  {
    XSDCorePlugin.getDefault().savePluginPreferences(); // model
  }

  public boolean performOk()
  {
    boolean result = super.performOk();

    doSavePreferenceStore();

    return result;
  }
}
