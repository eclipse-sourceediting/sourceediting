/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Schneider, david.schneider@unisys.com - [142500] WTP properties pages fonts don't follow Eclipse preferences     
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.contentproperties.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.wst.jsdt.web.core.internal.contentproperties.JSPFContentProperties;
import org.eclipse.wst.jsdt.web.ui.internal.JSPUIMessages;
import org.eclipse.wst.jsdt.web.ui.internal.Logger;
import org.eclipse.wst.jsdt.web.ui.internal.editor.IHelpContextIds;

/**
 * JSP Fragment Property Page
 */
public class JSPFContentSettingsPropertyPage extends PropertyPage {
	private static final String SELECT_NONE = JSPUIMessages.JSPFContentSettingsPropertyPage_0;

	// TODO: Figure out what to do with these strings/variables
	private String[] fLanguages = { SELECT_NONE, "java", //$NON-NLS-1$
			"javascript" }; //$NON-NLS-1$
	private String[] fContentTypes = { SELECT_NONE, "application/xhtml+xml", //$NON-NLS-1$
			"application/xml", //$NON-NLS-1$
			"text/html", //$NON-NLS-1$
			"text/xml", //$NON-NLS-1$
			"text/css" }; //$NON-NLS-1$

	private class ButtonListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			boolean specific = fSpecificSetting.getSelection();
			updateButtons(specific);
		}
	}

	private Combo fLanguageCombo;
	private Combo fContentTypeCombo;
	private Button fValidateFragments;
	Button fSpecificSetting;
	private SelectionListener fSpecificListener;

	public JSPFContentSettingsPropertyPage() {
		super();
		setDescription(JSPUIMessages.JSPFContentSettingsPropertyPage_1);
	}

	private Composite createComposite(Composite parent, int numColumns) {
		Composite composite = new Composite(parent, SWT.NULL);

		// GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		composite.setLayout(layout);

		// GridData
		GridData data = new GridData(GridData.FILL, GridData.FILL, true, false);
		data.horizontalIndent = 0;
		composite.setLayoutData(data);

		return composite;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite propertyPage = createComposite(parent, 2);

		// fragment language control
		Text languageLabel = new Text(propertyPage, SWT.READ_ONLY);
		languageLabel.setText(JSPUIMessages.JSPFContentSettingsPropertyPage_2);
		fLanguageCombo = new Combo(propertyPage, SWT.NONE);
		GridData data = new GridData(GridData.FILL, GridData.FILL, true, false);
		data.horizontalIndent = 0;
		fLanguageCombo.setLayoutData(data);
		fLanguageCombo.setItems(fLanguages);

		// fragment content type control
		Text contentTypeLabel = new Text(propertyPage, SWT.READ_ONLY);
		contentTypeLabel
				.setText(JSPUIMessages.JSPFContentSettingsPropertyPage_3);
		fContentTypeCombo = new Combo(propertyPage, SWT.NONE);
		data = new GridData(GridData.FILL, GridData.FILL, true, false);
		data.horizontalIndent = 0;
		fContentTypeCombo.setLayoutData(data);
		fContentTypeCombo.setItems(fContentTypes);

		// create separator
		Label label = new Label(propertyPage, SWT.SEPARATOR | SWT.HORIZONTAL);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		data.verticalSpan = 8;
		label.setLayoutData(data);

		// specific setting
		fSpecificSetting = new Button(propertyPage, SWT.CHECK | SWT.LEFT);
		fSpecificSetting
				.setText(JSPUIMessages.JSPFContentSettingsPropertyPage_4);
		data = new GridData(GridData.FILL, GridData.FILL, true, false);
		data.horizontalSpan = 2;
		fSpecificSetting.setLayoutData(data);

		fSpecificListener = new ButtonListener();
		fSpecificSetting.addSelectionListener(fSpecificListener);

		// validate file
		fValidateFragments = new Button(propertyPage, SWT.CHECK | SWT.LEFT);
		fValidateFragments.setText(JSPUIMessages.JSPFilesPreferencePage_1);
		data = new GridData(GridData.FILL, GridData.FILL, true, false);
		data.horizontalSpan = 2;
		data.horizontalIndent = 5;
		fValidateFragments.setLayoutData(data);

		initializeValues();

		PlatformUI.getWorkbench().getHelpSystem().setHelp(propertyPage,
				IHelpContextIds.JSP_FRAGMENT_HELPID);
		Dialog.applyDialogFont(parent);
		return propertyPage;
	}

	void updateButtons(boolean enabled) {
		fSpecificSetting.setSelection(enabled);
		fValidateFragments.setEnabled(enabled);
	}

	/**
	 * Get the resource this properties page is for
	 * 
	 * @return IResource for this properties page or null if there is no
	 *         IResource
	 */
	private IResource getResource() {
		IResource resource = null;
		if (getElement() instanceof IResource) {
			resource = (IResource) getElement();
		}
		return resource;
	}

	private void initializeValues() {
		String language = JSPFContentProperties.getProperty(
				JSPFContentProperties.JSPLANGUAGE, getResource(), false);
		if (language == null || language.length() == 0) {
			// if null, use none
			language = SELECT_NONE;
		}
		/*
		 * If item is already part of combo, select it. Otherwise, add to the
		 * combobox.
		 */
		int index = fLanguageCombo.indexOf(language);
		if (index > -1) {
			fLanguageCombo.select(index);
		} else {
			fLanguageCombo.setText(language);
		}

		String contentType = JSPFContentProperties.getProperty(
				JSPFContentProperties.JSPCONTENTTYPE, getResource(), false);
		if (contentType == null || contentType.length() == 0) {
			// if null, use none
			contentType = SELECT_NONE;
		}
		/*
		 * If item is already part of combo, select it. Otherwise, add to the
		 * combobox.
		 */
		index = fContentTypeCombo.indexOf(contentType);
		if (index > -1) {
			fContentTypeCombo.select(index);
		} else {
			fContentTypeCombo.setText(contentType);
		}

		String validate = JSPFContentProperties.getProperty(
				JSPFContentProperties.VALIDATE_FRAGMENTS, getResource(), false);
		String validate2 = JSPFContentProperties.getProperty(
				JSPFContentProperties.VALIDATE_FRAGMENTS, getResource(), true);
		if (validate == null || validate.length() == 0) {
			updateButtons(false);
			fValidateFragments.setSelection(Boolean.valueOf(validate2)
					.booleanValue());
		} else {
			updateButtons(true);
			fValidateFragments.setSelection(Boolean.valueOf(validate)
					.booleanValue());
		}
	}

	@Override
	protected void performDefaults() {
		int index = fLanguageCombo.indexOf(SELECT_NONE);
		if (index > -1) {
			fLanguageCombo.select(index);
		}

		index = fContentTypeCombo.indexOf(SELECT_NONE);
		if (index > -1) {
			fContentTypeCombo.select(index);
		}

		updateButtons(false);
		fValidateFragments.setSelection(true);

		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		try {
			// save the fragment language
			String language = fLanguageCombo.getText();
			if (language == null || language.length() == 0
					|| language.equalsIgnoreCase(SELECT_NONE)) {
				// if none, use null
				language = null;
			}
			JSPFContentProperties.setProperty(
					JSPFContentProperties.JSPLANGUAGE, getResource(), language);

			// save fragment content type
			String contentType = fContentTypeCombo.getText();
			if (contentType == null || contentType.length() == 0
					|| contentType.equalsIgnoreCase(SELECT_NONE)) {
				// if none, use null
				contentType = null;
			}
			JSPFContentProperties.setProperty(
					JSPFContentProperties.JSPCONTENTTYPE, getResource(),
					contentType);

			String validate = null;
			if (fSpecificSetting.getSelection()) {
				validate = Boolean.toString(fValidateFragments.getSelection());
			}
			JSPFContentProperties.setProperty(
					JSPFContentProperties.VALIDATE_FRAGMENTS, getResource(),
					validate);
		} catch (CoreException e) {
			// maybe in future, let user know there was a problem saving file
			Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
		}

		return super.performOk();
	}
}
