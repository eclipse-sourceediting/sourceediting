/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.preferences.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.preferences.JSPCorePreferenceNames;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.editor.IHelpContextIds;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ScrolledPageContent;

public class JSPValidationPreferencePage extends AbstractValidationSettingsPage {

	/**
	 * 
	 */
	private static final String PREFERENCE_NODE_QUALIFIER = JSPCorePlugin.getDefault().getBundle().getSymbolicName();

	private static final String SETTINGS_SECTION_NAME = "JSPValidationSeverities";//$NON-NLS-1$

	private static final int[] SEVERITIES = {ValidationMessage.ERROR, ValidationMessage.WARNING, ValidationMessage.IGNORE};

	// Should equal org.eclipse.jdt.internal.ui.preferences.ProblemSeveritiesPreferencePage.PREF_ID
	public static final String JAVA_SEVERITY_PREFERENCE_PAGE = "org.eclipse.jdt.ui.preferences.ProblemSeveritiesPreferencePage";
	// Should equal org.eclipse.jdt.internal.ui.preferences.ProblemSeveritiesPreferencePage.PROP_ID
	public static final String JAVA_SEVERITY_PROPERTY_PAGE = "org.eclipse.jdt.ui.propertyPages.ProblemSeveritiesPreferencePage";
	// Should equal org.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage.DATA_NO_LINK
	public static final String DATA_NO_LINK= "PropertyAndPreferencePage.nolink"; //$NON-NLS-1$

	private PixelConverter fPixelConverter;
	private Button fValidateFragments;

	private boolean fOriginalValidateFragments;

	public JSPValidationPreferencePage() {
		super();
	}

	/**
	 * @param parent
	 * @param text
	 * @return
	 */
	private Button createCheckBox(Composite parent, String text) {
		Button c = new Button(parent, SWT.CHECK);
		c.setText(text);
		c.setLayoutData(GridDataFactory.fillDefaults().create());
		return c;
	}

	protected Control createCommonContents(Composite parent) {
		final Composite page = new Composite(parent, SWT.NULL);

		// GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		page.setLayout(layout);

		fPixelConverter = new PixelConverter(parent);
		
		Group filesGroup = new Group(page, SWT.NONE);
		filesGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		filesGroup.setLayout(new GridLayout(1, false));
		filesGroup.setText(JSPUIMessages.JSPFilesPreferencePage_0);
		createFilesSection(filesGroup);

		// spacer
//		new Label(page, SWT.NONE).setLayoutData(GridDataFactory.fillDefaults().create());

		Group severitiesGroup = new Group(page, SWT.NONE);
		severitiesGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		severitiesGroup.setLayout(new GridLayout(1, false));
		severitiesGroup.setText(JSPUIMessages.JSPValidationPreferencePage_0);
		final Composite content = createValidationSection(severitiesGroup);

		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.heightHint = fPixelConverter.convertHeightInCharsToPixels(20);
		content.setLayoutData(gridData);

		return page;
	}

	/**
	 * @param fragmentGroup
	 */
	private void createFilesSection(Group fragmentGroup) {
		fValidateFragments = createCheckBox(fragmentGroup, JSPUIMessages.JSPFilesPreferencePage_1);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(fValidateFragments, IHelpContextIds.JSP_PREFWEBX_FILES_HELPID);
		IScopeContext[] contexts = createPreferenceScopes();
		fOriginalValidateFragments = contexts[0].getNode(getPreferenceNodeQualifier()).getBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, contexts[1].getNode(getPreferenceNodeQualifier()).getBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, true));
		fValidateFragments.setSelection(fOriginalValidateFragments);
	}

	private Composite createValidationSection(Composite page) {
		int nColumns = 3;

		final ScrolledPageContent spContent = new ScrolledPageContent(page);

		Composite composite = spContent.getBody();

		GridLayout layout = new GridLayout(nColumns, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		Label description = new Label(composite, SWT.NONE);
		description.setText(JSPUIMessages.Validation_description);
		description.setFont(page.getFont());

		String[] errorWarningIgnoreLabels = new String[]{JSPUIMessages.Validation_Error, JSPUIMessages.Validation_Warning, JSPUIMessages.Validation_Ignore};
		Composite section;

		// begin directives section
		section = createStyleSectionWithContentComposite(composite, JSPUIMessages.VALIDATION_HEADER_DIRECTIVE, nColumns);
		addComboBox(section, JSPUIMessages.VALIDATION_DIRECTIVE_TAGLIB_UNRESOLVABLE_URI_OR_TAGDIR, JSPCorePreferenceNames.VALIDATION_DIRECTIVE_TAGLIB_UNRESOLVABLE_URI_OR_TAGDIR, SEVERITIES, errorWarningIgnoreLabels, 0);
		addComboBox(section, JSPUIMessages.VALIDATION_DIRECTIVE_TAGLIB_DUPLICATE_PREFIXES_DIFFERENT_URIS, JSPCorePreferenceNames.VALIDATION_DIRECTIVE_TAGLIB_DUPLICATE_PREFIXES_DIFFERENT_URIS, SEVERITIES, errorWarningIgnoreLabels, 0);
		addComboBox(section, JSPUIMessages.VALIDATION_DIRECTIVE_TAGLIB_DUPLICATE_PREFIXES_SAME_URIS, JSPCorePreferenceNames.VALIDATION_DIRECTIVE_TAGLIB_DUPLICATE_PREFIXES_SAME_URIS, SEVERITIES, errorWarningIgnoreLabels, 0);
		addComboBox(section, JSPUIMessages.VALIDATION_DIRECTIVE_TAGLIB_MISSING_PREFIX, JSPCorePreferenceNames.VALIDATION_DIRECTIVE_TAGLIB_MISSING_PREFIX, SEVERITIES, errorWarningIgnoreLabels, 0);
		addComboBox(section, JSPUIMessages.VALIDATION_DIRECTIVE_TAGLIB_MISSING_URI_OR_TAGDIR, JSPCorePreferenceNames.VALIDATION_DIRECTIVE_TAGLIB_MISSING_URI_OR_TAGDIR, SEVERITIES, errorWarningIgnoreLabels, 0);
		addComboBox(section, JSPUIMessages.VALIDATION_DIRECTIVE_INCLUDE_FILE_NOT_FOUND, JSPCorePreferenceNames.VALIDATION_DIRECTIVE_INCLUDE_FILE_NOT_FOUND, SEVERITIES, errorWarningIgnoreLabels, 0);
		addComboBox(section, JSPUIMessages.VALIDATION_DIRECTIVE_INCLUDE_NO_FILE_SPECIFIED, JSPCorePreferenceNames.VALIDATION_DIRECTIVE_INCLUDE_NO_FILE_SPECIFIED, SEVERITIES, errorWarningIgnoreLabels, 0);
//		addComboBox(section, JSPUIMessages.VALIDATION_DIRECTIVE_PAGE_SUPERCLASS_NOT_FOUND, JSPCorePreferenceNames.VALIDATION_DIRECTIVE_PAGE_SUPERCLASS_NOT_FOUND, SEVERITIES, errorWarningIgnoreLabels, 0);
		// end directives section

		// begin custom actions section
		section = createStyleSectionWithContentComposite(composite, JSPUIMessages.VALIDATION_HEADER_CUSTOM_ACTIONS, nColumns);
		addComboBox(section, JSPUIMessages.VALIDATION_ACTIONS_SEVERITY_MISSING_REQUIRED_ATTRIBUTE, JSPCorePreferenceNames.VALIDATION_ACTIONS_SEVERITY_MISSING_REQUIRED_ATTRIBUTE, SEVERITIES, errorWarningIgnoreLabels, 0);
		addComboBox(section, JSPUIMessages.VALIDATION_ACTIONS_SEVERITY_UNKNOWN_ATTRIBUTE, JSPCorePreferenceNames.VALIDATION_ACTIONS_SEVERITY_UNKNOWN_ATTRIBUTE, SEVERITIES, errorWarningIgnoreLabels, 0);
		addComboBox(section, JSPUIMessages.VALIDATION_ACTIONS_SEVERITY_UNEXPECTED_RTEXPRVALUE, JSPCorePreferenceNames.VALIDATION_ACTIONS_SEVERITY_UNEXPECTED_RTEXPRVALUE, SEVERITIES, errorWarningIgnoreLabels, 0);
		addComboBox(section, JSPUIMessages.VALIDATION_ACTIONS_SEVERITY_NON_EMPTY_INLINE_TAG, JSPCorePreferenceNames.VALIDATION_ACTIONS_SEVERITY_NON_EMPTY_INLINE_TAG, SEVERITIES, errorWarningIgnoreLabels, 0);
		addComboBox(section, JSPUIMessages.VALIDATION_TRANSLATION_TEI_VALIDATION_MESSAGE, JSPCorePreferenceNames.VALIDATION_TRANSLATION_TEI_VALIDATION_MESSAGE, SEVERITIES, errorWarningIgnoreLabels, 0);
		addComboBox(section, JSPUIMessages.VALIDATION_TRANSLATION_TEI_CLASS_NOT_FOUND, JSPCorePreferenceNames.VALIDATION_TRANSLATION_TEI_CLASS_NOT_FOUND, SEVERITIES, errorWarningIgnoreLabels, 0);
		addComboBox(section, JSPUIMessages.VALIDATION_TRANSLATION_TEI_CLASS_NOT_INSTANTIATED, JSPCorePreferenceNames.VALIDATION_TRANSLATION_TEI_CLASS_NOT_INSTANTIATED, SEVERITIES, errorWarningIgnoreLabels, 0);
		addComboBox(section, JSPUIMessages.VALIDATION_TRANSLATION_TEI_CLASS_RUNTIME_EXCEPTION, JSPCorePreferenceNames.VALIDATION_TRANSLATION_TEI_CLASS_RUNTIME_EXCEPTION, SEVERITIES, errorWarningIgnoreLabels, 0);
		addComboBox(section, JSPUIMessages.VALIDATION_TRANSLATION_TAG_HANDLER_CLASS_NOT_FOUND, JSPCorePreferenceNames.VALIDATION_TRANSLATION_TAG_HANDLER_CLASS_NOT_FOUND, SEVERITIES, errorWarningIgnoreLabels, 0);
		// end custom actions section

		// begin standard actions section
		section = createStyleSectionWithContentComposite(composite, JSPUIMessages.VALIDATION_HEADER_STANDARD_ACTIONS, nColumns);
		addComboBox(section, JSPUIMessages.VALIDATION_TRANSLATION_USEBEAN_INVALID_ID, JSPCorePreferenceNames.VALIDATION_TRANSLATION_USEBEAN_INVALID_ID, SEVERITIES, errorWarningIgnoreLabels, 0);
		addComboBox(section, JSPUIMessages.VALIDATION_TRANSLATION_USBEAN_MISSING_TYPE_INFO, JSPCorePreferenceNames.VALIDATION_TRANSLATION_USBEAN_MISSING_TYPE_INFO, SEVERITIES, errorWarningIgnoreLabels, 0);
		addComboBox(section, JSPUIMessages.VALIDATION_TRANSLATION_USEBEAN_AMBIGUOUS_TYPE_INFO, JSPCorePreferenceNames.VALIDATION_TRANSLATION_USEBEAN_AMBIGUOUS_TYPE_INFO, SEVERITIES, errorWarningIgnoreLabels, 0);
		// end standard actions section

		// begin Java severity override section
		section = createStyleSectionWithContentComposite(composite, JSPUIMessages.VALIDATION_HEADER_JAVA, nColumns);
		if (getProject() == null) {
			new PreferenceLinkArea(section, SWT.WRAP | SWT.MULTI | SWT.LEFT_TO_RIGHT, JAVA_SEVERITY_PREFERENCE_PAGE, JSPUIMessages.VALIDATION_JAVA_NOTICE, (IWorkbenchPreferenceContainer) getContainer(), null).getControl().setLayoutData(GridDataFactory.fillDefaults().span(2, 1).indent(0, 0).hint(150, SWT.DEFAULT).create());
		}
		else {
			final Map data = new HashMap();
			data.put(DATA_NO_LINK, Boolean.TRUE);
			Link link= new Link(section, SWT.WRAP | SWT.MULTI | SWT.LEFT_TO_RIGHT);
			link.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).indent(0, 0).hint(150, SWT.DEFAULT).create());
			link.setText(JSPUIMessages.VALIDATION_JAVA_NOTICE);
			link.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					IWorkbenchPreferenceContainer container= (IWorkbenchPreferenceContainer)getContainer();
					container.openPage(JAVA_SEVERITY_PROPERTY_PAGE, data);
				}
			});
			//new PreferenceLinkArea(section, SWT.WRAP | SWT.MULTI | SWT.LEFT_TO_RIGHT, JAVA_SEVERITY_PROPERTY_PAGE, JSPUIMessages.VALIDATION_JAVA_NOTICE, (IWorkbenchPreferenceContainer) getContainer(), data).getControl().setLayoutData(GridDataFactory.fillDefaults().span(2, 1).indent(0, 0).hint(150, SWT.DEFAULT).create());
			// open in same shell?
			// PreferencesUtil.createPropertyDialogOn(getShell(), getProject(), JAVA_SEVERITY_PROPERTY_PAGE, new String[] { JAVA_SEVERITY_PROPERTY_PAGE }, data).open();
		}
		int sectionIndent = convertWidthInCharsToPixels(2);
		addComboBox(section, JSPUIMessages.VALIDATION_JAVA_LOCAL_VARIABLE_NEVER_USED, JSPCorePreferenceNames.VALIDATION_JAVA_LOCAL_VARIABLE_NEVER_USED, SEVERITIES, errorWarningIgnoreLabels, sectionIndent);
		addComboBox(section, JSPUIMessages.VALIDATION_JAVA_ARGUMENT_IS_NEVER_USED, JSPCorePreferenceNames.VALIDATION_JAVA_ARGUMENT_IS_NEVER_USED, SEVERITIES, errorWarningIgnoreLabels, sectionIndent);
		addComboBox(section, JSPUIMessages.VALIDATION_JAVA_NULL_LOCAL_VARIABLE_REFERENCE, JSPCorePreferenceNames.VALIDATION_JAVA_NULL_LOCAL_VARIABLE_REFERENCE, SEVERITIES, errorWarningIgnoreLabels, sectionIndent);
		addComboBox(section, JSPUIMessages.VALIDATION_JAVA_POTENTIAL_NULL_LOCAL_VARIABLE_REFERENCE, JSPCorePreferenceNames.VALIDATION_JAVA_POTENTIAL_NULL_LOCAL_VARIABLE_REFERENCE, SEVERITIES, errorWarningIgnoreLabels, sectionIndent);
		addComboBox(section, JSPUIMessages.VALIDATION_JAVA_UNUSED_IMPORT, JSPCorePreferenceNames.VALIDATION_JAVA_UNUSED_IMPORT, SEVERITIES, errorWarningIgnoreLabels, sectionIndent);
		// end Java severity override section

		// begin EL section
		section = createStyleSectionWithContentComposite(composite, JSPUIMessages.VALIDATION_HEADER_EL, nColumns);
		addComboBox(section, JSPUIMessages.VALIDATION_EL_SYNTAX, JSPCorePreferenceNames.VALIDATION_EL_SYNTAX, SEVERITIES, errorWarningIgnoreLabels, 0);
		addComboBox(section, JSPUIMessages.VALIDATION_EL_LEXER, JSPCorePreferenceNames.VALIDATION_EL_LEXER, SEVERITIES, errorWarningIgnoreLabels, 0);
		// end EL section

		restoreSectionExpansionStates(getDialogSettings().getSection(SETTINGS_SECTION_NAME));

		return spContent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	public void dispose() {
		storeSectionExpansionStates(getDialogSettings().addNewSection(SETTINGS_SECTION_NAME));
		super.dispose();
	}

	protected IDialogSettings getDialogSettings() {
		return JSPUIPlugin.getDefault().getDialogSettings();
	}

	protected String getPreferenceNodeQualifier() {
		return PREFERENCE_NODE_QUALIFIER;
	}

	protected String getPreferencePageID() {
		return "org.eclipse.jst.jsp.ui.preferences.validation";//$NON-NLS-1$
	}

	protected String getProjectSettingsKey() {
		return JSPCorePreferenceNames.VALIDATION_USE_PROJECT_SETTINGS;
	}

	protected String getPropertyPageID() {
		return "org.eclipse.jst.jsp.ui.propertyPage.project.validation";//$NON-NLS-1$
	}

	public void init(IWorkbench workbench) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		resetSeverities();

		IEclipsePreferences defaultContext = new DefaultScope().getNode(getPreferenceNodeQualifier());
		boolean validateFragments = defaultContext.getBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, true);
		fValidateFragments.setSelection(validateFragments);

		super.performDefaults();
	}

	protected boolean shouldRevalidateOnSettingsChange() {
		return fOriginalValidateFragments != fValidateFragments.getSelection() || super.shouldRevalidateOnSettingsChange();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jst.jsp.ui.internal.preferences.ui.AbstractValidationSettingsPage#storeValues()
	 */
	protected void storeValues() {
		super.storeValues();

		IScopeContext[] contexts = createPreferenceScopes();
		boolean validateFragments = fValidateFragments.getSelection();
		contexts[0].getNode(getPreferenceNodeQualifier()).putBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, validateFragments);
	}
}
