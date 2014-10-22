/*******************************************************************************
 * Copyright (c) 2008, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.ui.internal.preferences.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.html.ui.internal.HTMLUIMessages;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.html.ui.internal.Logger;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractValidationSettingsPage;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ScrolledPageContent;
import org.eclipse.wst.sse.ui.internal.provisional.preferences.CommonEditorPreferenceNames;
import org.osgi.service.prefs.BackingStoreException;

public class HTMLValidationPreferencePage extends AbstractValidationSettingsPage {
	
	public static final String PROPERTY_PAGE_ID = "org.eclipse.wst.html.ui.propertyPage.project.validation";

	public static final String PREFERENCE_PAGE_ID = "org.eclipse.wst.html.ui.preferences.validation";

	private static final int[] SEVERITIES = {ValidationMessage.ERROR, ValidationMessage.WARNING, ValidationMessage.IGNORE};
	
	private static final String SETTINGS_SECTION_NAME = "HTMLValidationSeverities";//$NON-NLS-1$

	private class BooleanData {
		private String fKey;
		private boolean fValue;
		boolean originalValue = false; 
		
		public BooleanData(String key) {
			fKey = key;
		}
		
		public String getKey() {
			return fKey;
		}
		
		/**
		 * Sets enablement for the attribute names ignorance
		 * 
		 * @param severity the severity level
		 */
		public void setValue(boolean value) {
			fValue = value;
		}
		
		/**
		 * Returns the value for the attribute names ignorance
		 * 
		 * @return
		 */
		public boolean getValue() {
			return fValue;
		}
		
		boolean isChanged() {
			return (originalValue != fValue);
		}
	}
	
	private class TextData {
		private String fKey;
		private String fValue;
		String originalValue = ""; //$NON-NLS-1$
		
		public TextData(String key) {
			fKey = key;
		}
		
		public String getKey() {
			return fKey;
		}
		
		/**
		 * Sets the ignored attribute names pattern
		 * 
		 * @param severity the severity level
		 */
		public void setValue(String value) {
			fValue = value;
		}
		
		/**
		 * Returns non-null value for the ignored attribute names pattern
		 * 
		 * @return
		 */
		public String getValue() {
			return fValue != null ? fValue : ""; //$NON-NLS-1$
		}
		
		boolean isChanged() {
			return !originalValue.equalsIgnoreCase(fValue);
		}
	}
	
	public HTMLValidationPreferencePage() {
		super();
		fPreferencesService = Platform.getPreferencesService();
	}
	
	private PixelConverter fPixelConverter;
	private Button fIgnoreElementNames;
	private Label fIgnoredElementNamesLabel;
	private Text fIgnoredElementNames;
	private Button fIgnoreAttributeNames;
	private Label fIgnoredAttributeNamesLabel;
	private Text fIgnoredAttributeNames;
	private IPreferencesService fPreferencesService = null;
	
	private boolean fUseElementsOriginOverrides = false;
	private boolean fIgnoreElementNamesOriginOverride = HTMLCorePreferenceNames.IGNORE_ELEMENT_NAMES_DEFAULT;
	private String  fIgnoredElementNamesOriginOverride = HTMLCorePreferenceNames.ELEMENT_NAMES_TO_IGNORE_DEFAULT;

	private boolean fUseAttributesOriginOverrides = false;
	private boolean fIgnoreAttributeNamesOriginOverride = HTMLCorePreferenceNames.IGNORE_ATTRIBUTE_NAMES_DEFAULT;
	private String  fIgnoredAttributeNamesOriginOverride = HTMLCorePreferenceNames.ATTRIBUTE_NAMES_TO_IGNORE_DEFAULT;

	public void overrideIgnoredElementsOriginValues(boolean enableIgnore, String elementNames) {
		fIgnoreElementNamesOriginOverride = enableIgnore;
		fIgnoredElementNamesOriginOverride = elementNames;
		fUseElementsOriginOverrides = true;
		
		if (fIgnoreElementNames != null) {
			BooleanData data = (BooleanData)fIgnoreElementNames.getData();
			if (data != null)
				data.originalValue = fIgnoreElementNamesOriginOverride;
		}
		if (fIgnoredElementNames != null) {
			TextData data = (TextData)fIgnoredElementNames.getData();
			if (data != null)
				data.originalValue = fIgnoredElementNamesOriginOverride;
		}
	}

	/**
	 * Overrides the origin values for Ignored Attribute Names
	 * 
	 * @deprecated Use overrideIgnoredAttributesOriginValues(boolean, String)
	 * 
	 * @param enableIgnore
	 * @param attributeNames
	 */
	public void overrideOriginValues(boolean enableIgnore, String attributeNames) {
		this.overrideIgnoredAttributesOriginValues(enableIgnore, attributeNames);
	}
	
	public void overrideIgnoredAttributesOriginValues(boolean enableIgnore, String attributeNames) {
		fIgnoreAttributeNamesOriginOverride = enableIgnore;
		fIgnoredAttributeNamesOriginOverride = attributeNames;
		fUseAttributesOriginOverrides = true;
		
		if (fIgnoreAttributeNames != null) {
			BooleanData data = (BooleanData)fIgnoreAttributeNames.getData();
			if (data != null)
				data.originalValue = fIgnoreAttributeNamesOriginOverride;
		}
		if (fIgnoredAttributeNames != null) {
			TextData data = (TextData)fIgnoredAttributeNames.getData();
			if (data != null)
				data.originalValue = fIgnoredAttributeNamesOriginOverride;
		}
	}

	protected Control createCommonContents(Composite parent) {
		final Composite page = new Composite(parent, SWT.NULL);
		
		//GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		page.setLayout(layout);
		
		fPixelConverter = new PixelConverter(parent);
		
		final Composite content = createValidationSection(page);

		GridData gridData= new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.heightHint = fPixelConverter.convertHeightInCharsToPixels(20);
		content.setLayoutData(gridData);
		
		return page;
	}
	
	private Composite createValidationSection(Composite page) {
		int nColumns = 3;
		
		final ScrolledPageContent spContent = new ScrolledPageContent(page);
		
		Composite composite = spContent.getBody();
		
		GridLayout layout= new GridLayout(nColumns, false);
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		composite.setLayout(layout);
		
		// Ignored Element Names Pattern
		BooleanData ignoreData = new BooleanData(HTMLCorePreferenceNames.IGNORE_ELEMENT_NAMES);
		fIgnoreElementNames = new Button(composite, SWT.CHECK);
		fIgnoreElementNames.setData(ignoreData);
		fIgnoreElementNames.setFont(page.getFont());
		fIgnoreElementNames.setText(HTMLUIMessages.IgnoreElementNames);
		fIgnoreElementNames.setEnabled(true);
		
		boolean ignoreElementNamesIsSelected = fPreferencesService.getBoolean(getPreferenceNodeQualifier(), 
				ignoreData.getKey(), HTMLCorePreferenceNames.IGNORE_ELEMENT_NAMES_DEFAULT, createPreferenceScopes());
		ignoreData.setValue(ignoreElementNamesIsSelected);
		ignoreData.originalValue = fUseElementsOriginOverrides ? fIgnoreElementNamesOriginOverride : ignoreElementNamesIsSelected;
		
		fIgnoreElementNames.setSelection(ignoreData.getValue());
		fIgnoreElementNames.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				controlChanged(e.widget);
			}
			public void widgetSelected(SelectionEvent e) {
				controlChanged(e.widget);
			}
		});
		fIgnoreElementNames.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		
		fIgnoredElementNamesLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
		fIgnoredElementNamesLabel.setFont(composite.getFont());
		fIgnoredElementNamesLabel.setEnabled(ignoreData.getValue());
		fIgnoredElementNamesLabel.setText(HTMLUIMessages.IgnoreElementNamesPattern);
		fIgnoredElementNamesLabel.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false, 3, 1));
		setHorizontalIndent(fIgnoredElementNamesLabel, 20);

		TextData data = new TextData(HTMLCorePreferenceNames.ELEMENT_NAMES_TO_IGNORE);
		fIgnoredElementNames = new Text(composite, SWT.SINGLE | SWT.BORDER);
		fIgnoredElementNames.setData(data);
		fIgnoredElementNames.setTextLimit(500);
		fIgnoredElementNames.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		setHorizontalIndent(fIgnoredElementNames, 20);
		setWidthHint(fIgnoredElementNames, convertWidthInCharsToPixels(65));
		String ignoredElementNames = fPreferencesService.getString(getPreferenceNodeQualifier(), data.getKey(), HTMLCorePreferenceNames.ELEMENT_NAMES_TO_IGNORE_DEFAULT, createPreferenceScopes());
		data.setValue(ignoredElementNames);
		data.originalValue = fUseElementsOriginOverrides ? fIgnoredElementNamesOriginOverride : ignoredElementNames;
		fIgnoredElementNames.setText(data.getValue());
		
		fIgnoredElementNames.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				if (verifyIgnoredNames(fIgnoredElementNames.getText().trim())) {
					controlChanged(e.widget);
				}
			}
		});
		controlChanged(fIgnoreElementNames);

		// Ignored Attribute Names Pattern
		ignoreData = new BooleanData(HTMLCorePreferenceNames.IGNORE_ATTRIBUTE_NAMES);
		fIgnoreAttributeNames = new Button(composite, SWT.CHECK);
		fIgnoreAttributeNames.setData(ignoreData);
		fIgnoreAttributeNames.setFont(page.getFont());
		fIgnoreAttributeNames.setText(HTMLUIMessages.IgnoreAttributeNames);
		fIgnoreAttributeNames.setEnabled(true);
		
		boolean ignoreAttributeNamesIsSelected = fPreferencesService.getBoolean(getPreferenceNodeQualifier(), 
				ignoreData.getKey(), HTMLCorePreferenceNames.IGNORE_ATTRIBUTE_NAMES_DEFAULT, createPreferenceScopes());
		ignoreData.setValue(ignoreAttributeNamesIsSelected);
		ignoreData.originalValue = fUseAttributesOriginOverrides ? fIgnoreAttributeNamesOriginOverride : ignoreAttributeNamesIsSelected;
		
		fIgnoreAttributeNames.setSelection(ignoreData.getValue());
		fIgnoreAttributeNames.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				controlChanged(e.widget);
			}
			public void widgetSelected(SelectionEvent e) {
				controlChanged(e.widget);
			}
		});
		fIgnoreAttributeNames.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		
		fIgnoredAttributeNamesLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
		fIgnoredAttributeNamesLabel.setFont(composite.getFont());
		fIgnoredAttributeNamesLabel.setEnabled(ignoreData.getValue());
		fIgnoredAttributeNamesLabel.setText(HTMLUIMessages.IgnoreAttributeNamesPattern);
		fIgnoredAttributeNamesLabel.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false, 3, 1));
		setHorizontalIndent(fIgnoredAttributeNamesLabel, 20);

		data = new TextData(HTMLCorePreferenceNames.ATTRIBUTE_NAMES_TO_IGNORE);
		fIgnoredAttributeNames = new Text(composite, SWT.SINGLE | SWT.BORDER);
		fIgnoredAttributeNames.setData(data);
		fIgnoredAttributeNames.setTextLimit(500);
		fIgnoredAttributeNames.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		setHorizontalIndent(fIgnoredAttributeNames, 20);
		setWidthHint(fIgnoredAttributeNames, convertWidthInCharsToPixels(65));
		String ignoredAttributeNames = fPreferencesService.getString(getPreferenceNodeQualifier(), data.getKey(), HTMLCorePreferenceNames.ATTRIBUTE_NAMES_TO_IGNORE_DEFAULT, createPreferenceScopes());
		data.setValue(ignoredAttributeNames);
		data.originalValue = fUseAttributesOriginOverrides ? fIgnoredAttributeNamesOriginOverride : ignoredAttributeNames;
		fIgnoredAttributeNames.setText(data.getValue());
		
		fIgnoredAttributeNames.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				if (verifyIgnoredNames(fIgnoredAttributeNames.getText().trim())) {
					controlChanged(e.widget);
				}
			}
		});
		controlChanged(fIgnoreAttributeNames);

		Label description = new Label(composite, SWT.NONE);
		description.setText(HTMLUIMessages.Validation_description);
		description.setFont(page.getFont());

		
		ExpandableComposite ec;
		Composite inner;
		String label;
		
		String[] errorWarningIgnoreLabel = new String[] { HTMLUIMessages.Validation_Error, HTMLUIMessages.Validation_Warning, HTMLUIMessages.Validation_Ignore };
		
		// Element section
		
		ec = createStyleSection(composite, HTMLUIMessages.Expandable_label_elements, nColumns);
		
		inner = new Composite(ec, SWT.NONE);
		inner.setFont(composite.getFont());
		inner.setLayout(new GridLayout(nColumns, false));
		ec.setClient(inner);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_8;
		addComboBox(inner, label, HTMLCorePreferenceNames.ELEM_UNKNOWN_NAME, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_9;
		addComboBox(inner, label, HTMLCorePreferenceNames.ELEM_INVALID_NAME, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_10;
		addComboBox(inner, label, HTMLCorePreferenceNames.ELEM_START_INVALID_CASE, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_11;
		addComboBox(inner, label, HTMLCorePreferenceNames.ELEM_END_INVALID_CASE, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_12;
		addComboBox(inner, label, HTMLCorePreferenceNames.ELEM_MISSING_START, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_13;
		addComboBox(inner, label, HTMLCorePreferenceNames.ELEM_MISSING_END, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_14;
		addComboBox(inner, label, HTMLCorePreferenceNames.ELEM_UNNECESSARY_END, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_15;
		addComboBox(inner, label, HTMLCorePreferenceNames.ELEM_INVALID_DIRECTIVE, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_16;
		addComboBox(inner, label, HTMLCorePreferenceNames.ELEM_INVALID_CONTENT, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_17;
		addComboBox(inner, label, HTMLCorePreferenceNames.ELEM_DUPLICATE, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_18;
		addComboBox(inner, label, HTMLCorePreferenceNames.ELEM_COEXISTENCE, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_19;
		addComboBox(inner, label, HTMLCorePreferenceNames.ELEM_UNCLOSED_START_TAG, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_20;
		addComboBox(inner, label, HTMLCorePreferenceNames.ELEM_UNCLOSED_END_TAG, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_21;
		addComboBox(inner, label, HTMLCorePreferenceNames.ELEM_INVALID_EMPTY_TAG, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_36;
		addComboBox(inner, label, HTMLCorePreferenceNames.ELEM_INVALID_TEXT, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		// End Element Section
		
		// The Attribute validation section
		
		ec = createStyleSection(composite, HTMLUIMessages.Expandable_label_attributes, nColumns);
		inner = new Composite(ec, SWT.NONE);
		inner.setFont(composite.getFont());
		inner.setLayout(new GridLayout(nColumns, false));
		ec.setClient(inner);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_0;
		addComboBox(inner, label, HTMLCorePreferenceNames.ATTRIBUTE_UNDEFINED_NAME, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_1;
		addComboBox(inner, label, HTMLCorePreferenceNames.ATTRIBUTE_UNDEFINED_VALUE, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_2;
		addComboBox(inner, label, HTMLCorePreferenceNames.ATTRIBUTE_NAME_MISMATCH, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_3;
		addComboBox(inner, label, HTMLCorePreferenceNames.ATTRIBUTE_INVALID_NAME, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_4;
		addComboBox(inner, label, HTMLCorePreferenceNames.ATTRIBUTE_INVALID_VALUE, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_5;
		addComboBox(inner, label, HTMLCorePreferenceNames.ATTRIBUTE_DUPLICATE, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_6;
		addComboBox(inner, label, HTMLCorePreferenceNames.ATTRIBUTE_VALUE_MISMATCH, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_7;
		addComboBox(inner, label, HTMLCorePreferenceNames.ATTRIBUTE_VALUE_UNCLOSED, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_37;
		addComboBox(inner, label, HTMLCorePreferenceNames.ATTRIBUTE_VALUE_EQUALS_MISSING, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_35;
		 addComboBox(inner, label, HTMLCorePreferenceNames.ATTRIBUTE_VALUE_RESOURCE_NOT_FOUND, SEVERITIES, errorWarningIgnoreLabel, 0);

		// End Attribute section
		
		
		// Document Type
		ec = createStyleSection(composite, HTMLUIMessages.Expandable_label_document_type, nColumns);
		
		inner = new Composite(ec, SWT.NONE);
		inner.setFont(composite.getFont());
		inner.setLayout(new GridLayout(nColumns, false));
		ec.setClient(inner);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_22;
		addComboBox(inner, label, HTMLCorePreferenceNames.DOC_DUPLICATE, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_23;
		addComboBox(inner, label, HTMLCorePreferenceNames.DOC_INVALID_CONTENT, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_24;
		addComboBox(inner, label, HTMLCorePreferenceNames.DOC_DOCTYPE_UNCLOSED, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		// End Document Type
		
		// Comments
		ec = createStyleSection(composite, HTMLUIMessages.Expandable_label_comment, nColumns);
		
		inner = new Composite(ec, SWT.NONE);
		inner.setFont(composite.getFont());
		inner.setLayout(new GridLayout(nColumns, false));
		ec.setClient(inner);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_27;
		addComboBox(inner, label, HTMLCorePreferenceNames.COMMENT_INVALID_CONTENT, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_28;
		addComboBox(inner, label, HTMLCorePreferenceNames.COMMENT_UNCLOSED, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		// End Comments
		
		
		// CDATA Sections
		ec = createStyleSection(composite, HTMLUIMessages.Expandable_label_cdata, nColumns);
		
		inner = new Composite(ec, SWT.NONE);
		inner.setFont(composite.getFont());
		inner.setLayout(new GridLayout(nColumns, false));
		ec.setClient(inner);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_29;
		addComboBox(inner, label, HTMLCorePreferenceNames.CDATA_INVALID_CONTENT, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_30;
		addComboBox(inner, label, HTMLCorePreferenceNames.CDATA_UNCLOSED, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		// End CDATA Sections
		
		// Processing Instructions
		ec = createStyleSection(composite, HTMLUIMessages.Expandable_label_pi, nColumns);
		
		inner = new Composite(ec, SWT.NONE);
		inner.setFont(composite.getFont());
		inner.setLayout(new GridLayout(nColumns, false));
		ec.setClient(inner);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_31;
		addComboBox(inner, label, HTMLCorePreferenceNames.PI_INVALID_CONTENT, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_32;
		addComboBox(inner, label, HTMLCorePreferenceNames.PI_UNCLOSED, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		// End Processing Instructions
		
		// Entity References
		ec = createStyleSection(composite, HTMLUIMessages.Expandable_label_entity_ref, nColumns);
		
		inner = new Composite(ec, SWT.NONE);
		inner.setFont(composite.getFont());
		inner.setLayout(new GridLayout(nColumns, false));
		ec.setClient(inner);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_33;
		addComboBox(inner, label, HTMLCorePreferenceNames.REF_INVALID_CONTENT, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_34;
		addComboBox(inner, label, HTMLCorePreferenceNames.REF_UNDEFINED, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		
		// End Entity References
		
		
		// Text Content
		ec = createStyleSection(composite, HTMLUIMessages.Expandable_label_text, nColumns);
		
		inner = new Composite(ec, SWT.NONE);
		inner.setFont(composite.getFont());
		inner.setLayout(new GridLayout(nColumns, false));
		ec.setClient(inner);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_25;
		addComboBox(inner, label, HTMLCorePreferenceNames.TEXT_INVALID_CONTENT, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		label = HTMLUIMessages.HTMLValidationPreferencePage_26;
		addComboBox(inner, label, HTMLCorePreferenceNames.TEXT_INVALID_CHAR, SEVERITIES, errorWarningIgnoreLabel, 0);
		
		// End Text Content
		
		restoreSectionExpansionStates(getDialogSettings().getSection(SETTINGS_SECTION_NAME));
		
		return spContent;
	}

	private void setHorizontalIndent(Control control, int indent) {
		Object ld= control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData) ld).horizontalIndent= indent;
		}
	}
	
	private void setWidthHint(Control control, int widthHint) {
		Object ld= control.getLayoutData();
		if (ld instanceof GridData) {
			((GridData)ld).widthHint= widthHint;
		}
	}
	
	private boolean verifyIgnoredNames(String value) {
		if (value.length() == 0)
			return true;

		String[] names = value.split(","); //$NON-NLS-1$
		boolean valid = true;
		for (int i = 0; valid && names != null && i < names.length; i++) {
			String name = names[i] == null ? null : names[i].trim();
			if (name != null && name.length() > 0) {
				for (int j = 0; valid && j < name.length(); j++) {
					if (!Character.isJavaIdentifierPart(name.charAt(j)) &&
							'-' != name.charAt(j) && '_' != name.charAt(j) &&
							'*' != name.charAt(j) && '?' != name.charAt(j))
						valid = false;
				}
			}
		}
		
		if (!valid) {
			setErrorMessage(NLS.bind(HTMLUIMessages.BadIgnoreAttributeNamesPattern, value));
			setValid(false);
		} else {
			setErrorMessage(null);
			setValid(true);
		}

		return valid;
	}
	
	protected void controlChanged(Widget widget) {
		if (widget instanceof Text) {
			TextData data= (TextData) widget.getData();
			data.setValue(((Text)widget).getText());
		} else if (widget instanceof Button) {
			BooleanData data = (BooleanData) widget.getData();
			if (data != null) {
				data.setValue(((Button)widget).getSelection());
				if (fIgnoreElementNames == widget) {
					fIgnoredElementNamesLabel.setEnabled(data.getValue());
					fIgnoredElementNames.setEnabled(data.getValue());
					if (data.getValue()) {
						fIgnoredElementNames.setFocus();
					}
				} else if (fIgnoreAttributeNames == widget) {
					fIgnoredAttributeNamesLabel.setEnabled(data.getValue());
					fIgnoredAttributeNames.setEnabled(data.getValue());
					if (data.getValue()) {
						fIgnoredAttributeNames.setFocus();
					}
				}
			}
		} else {
			super.controlChanged(widget);
		}
	}

	/**
	 * Returns true in case of the Attribute Names to ignore preferences is changed
	 * causing the full validation to be requested.
	 */
	protected boolean shouldRevalidateOnSettingsChange() {
		TextData data = (TextData)fIgnoredElementNames.getData();
		if (data.isChanged())
			return true;
		
		BooleanData ignoreData = (BooleanData)fIgnoreElementNames.getData();
		if (ignoreData.isChanged())
			return true;
		
		data = (TextData)fIgnoredAttributeNames.getData();
		if (data.isChanged())
			return true;
		
		ignoreData = (BooleanData)fIgnoreAttributeNames.getData();
		if (ignoreData.isChanged())
			return true;
		
		return super.shouldRevalidateOnSettingsChange();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractSettingsPage#storeValues()
	 */
	protected void storeValues() {
		IScopeContext[] contexts = createPreferenceScopes();

		BooleanData ignoreData = (BooleanData)fIgnoreElementNames.getData();
		contexts[0].getNode(getPreferenceNodeQualifier()).putBoolean(ignoreData.getKey(), ignoreData.getValue()); 
		ignoreData.originalValue = ignoreData.getValue();

		TextData data = (TextData)fIgnoredElementNames.getData();
		contexts[0].getNode(getPreferenceNodeQualifier()).put(data.getKey(), data.getValue()); 
		data.originalValue = data.getValue();

		ignoreData = (BooleanData)fIgnoreAttributeNames.getData();
		contexts[0].getNode(getPreferenceNodeQualifier()).putBoolean(ignoreData.getKey(), ignoreData.getValue()); 
		ignoreData.originalValue = ignoreData.getValue();

		data = (TextData)fIgnoredAttributeNames.getData();
		contexts[0].getNode(getPreferenceNodeQualifier()).put(data.getKey(), data.getValue()); 
		data.originalValue = data.getValue();

		for(int i = 0; i < contexts.length; i++) {
			try {
				contexts[i].getNode(getPreferenceNodeQualifier()).flush();
			} catch (BackingStoreException e) {
				Logger.logException(e);
			}
		}

		super.storeValues();
		
		forceReconciling();
	}
	
	private void forceReconciling() {
		IPreferenceStore store = SSEUIPlugin.getDefault().getPreferenceStore();
		boolean value = store.getBoolean(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS);
		store.setValue(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS, !value);
		store.setValue(CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS, value);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		resetIgnoreNamesPatterns();
		resetSeverities();
		super.performDefaults();
	}
	
	protected void resetIgnoreNamesPatterns() {
		IEclipsePreferences defaultContext = new DefaultScope().getNode(getPreferenceNodeQualifier());

		BooleanData ignoreData = (BooleanData)fIgnoreElementNames.getData();
		boolean ignoreElementNames = defaultContext.getBoolean(ignoreData.getKey(), HTMLCorePreferenceNames.IGNORE_ELEMENT_NAMES_DEFAULT);
		ignoreData.setValue(ignoreElementNames);
		fIgnoreElementNames.setSelection(ignoreData.getValue());

		TextData data = (TextData)fIgnoredElementNames.getData();
		String ignoredElementNames = defaultContext.get(data.getKey(), HTMLCorePreferenceNames.ELEMENT_NAMES_TO_IGNORE_DEFAULT);
		data.setValue(ignoredElementNames);
		fIgnoredElementNames.setText(data.getValue());

		ignoreData = (BooleanData)fIgnoreAttributeNames.getData();
		boolean ignoreAttributeNames = defaultContext.getBoolean(ignoreData.getKey(), HTMLCorePreferenceNames.IGNORE_ATTRIBUTE_NAMES_DEFAULT);
		ignoreData.setValue(ignoreAttributeNames);
		fIgnoreAttributeNames.setSelection(ignoreData.getValue());

		data = (TextData)fIgnoredAttributeNames.getData();
		String ignoredAttributeNames = defaultContext.get(data.getKey(), HTMLCorePreferenceNames.ATTRIBUTE_NAMES_TO_IGNORE_DEFAULT);
		data.setValue(ignoredAttributeNames);
		fIgnoredAttributeNames.setText(data.getValue());
		
		controlChanged(fIgnoreAttributeNames);
	}
	
	
	protected IDialogSettings getDialogSettings() {
		return HTMLUIPlugin.getDefault().getDialogSettings();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	public void dispose() {
		storeSectionExpansionStates(getDialogSettings().addNewSection(SETTINGS_SECTION_NAME));
		super.dispose();
	}
	
	protected String getQualifier() {
		return HTMLCorePlugin.getDefault().getBundle().getSymbolicName();
	}
	
	protected String getPreferenceNodeQualifier() {
		return HTMLCorePlugin.getDefault().getBundle().getSymbolicName();
	}

	protected String getPreferencePageID() {
		return PREFERENCE_PAGE_ID;
	}

	protected String getProjectSettingsKey() {
		return HTMLCorePreferenceNames.USE_PROJECT_SETTINGS;
	}

	protected String getPropertyPageID() {
		return PROPERTY_PAGE_ID;
	}

	public void init(IWorkbench workbench) {
	}
}
