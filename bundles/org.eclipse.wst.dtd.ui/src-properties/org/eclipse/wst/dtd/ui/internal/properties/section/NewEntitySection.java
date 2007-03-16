/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.internal.properties.section;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.common.ui.internal.viewers.ResourceFilter;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.dtd.core.internal.Entity;
import org.eclipse.wst.dtd.ui.internal.DTDPropertiesMessages;
import org.eclipse.wst.dtd.ui.internal.DTDUIPlugin;

public class NewEntitySection extends AbstractSection {
	private final String NAME = DTDPropertiesMessages._UI_LABEL_NAME;
	private final String ENTITY_TYPE = DTDPropertiesMessages._UI_LABEL_ENTITY_TYPE;
	private final String EXTERNAL_ENTITY = DTDPropertiesMessages._UI_LABEL_EXTERNAL_ENTITY;
	private final String PARAMETER = DTDPropertiesMessages._UI_LABEL_PARAMETER_ENTITY;
	private final String GENERAL = DTDPropertiesMessages._UI_LABEL_GENERAL_ENTITY;
	private final String VALUE = DTDPropertiesMessages._UI_LABEL_ENTITY_VALUE;
	private final String PUBLIC_ID = DTDPropertiesMessages._UI_LABEL_PUBLIC_ID;
	private final String SYSTEM_ID = DTDPropertiesMessages._UI_LABEL_SYSTEM_ID;

	private Text systemIdText;
	private Text publicIdText;
	private Text nameText;
	private Button wizardButton;

	private Text entityValueText;
	private Button checkBox;

	private CCombo typeCombo;
	private String[] typeComboValues = {PARAMETER, GENERAL};
	private PageBook pageBook;

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory) {
		super.createControls(parent, factory);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);

		createEntityCommonComposite(composite);

		pageBook = new PageBook(composite, SWT.FLAT);
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(entityCommonComposite, 0);
		data.bottom = new FormAttachment(100, 0);
		pageBook.setLayoutData(data);

		createExternalEntityComposite(pageBook);
		createInternalEntityComposite(pageBook);

		pageBook.showPage(externalEntityComposite);
	}

	private Composite entityCommonComposite;

	private Composite createEntityCommonComposite(Composite parent) {
		entityCommonComposite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, 0);
		entityCommonComposite.setLayoutData(data);

		// Create label first then attach other control to it
		CLabel nameLabel = getWidgetFactory().createCLabel(entityCommonComposite, NAME); //$NON-NLS-1$

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=141106
		Point p = nameLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
		int labelWidth = Math.max(p.x, 98);
		data = new FormData(labelWidth, SWT.DEFAULT);
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(0, 0);
		nameLabel.setLayoutData(data);

		nameText = getWidgetFactory().createText(entityCommonComposite, "", SWT.NONE); //$NON-NLS-1$    
		data = new FormData();
		data.left = new FormAttachment(nameLabel, -ITabbedPropertyConstants.HSPACE);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(nameLabel, 0, SWT.CENTER);
		nameText.setLayoutData(data);
		nameText.addListener(SWT.Modify, this);

		// Create label first then attach other control to it
		CLabel cLabel = getWidgetFactory().createCLabel(entityCommonComposite, ENTITY_TYPE);

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=141106
		p = cLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
		labelWidth = Math.max(p.x, 98);
		data = new FormData(labelWidth, SWT.DEFAULT);
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(nameLabel, +ITabbedPropertyConstants.VSPACE);
		cLabel.setLayoutData(data);

		// Create Checkbox
		checkBox = getWidgetFactory().createButton(entityCommonComposite, EXTERNAL_ENTITY, SWT.CHECK); //$NON-NLS-1$
		data = new FormData();
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(cLabel, 0, SWT.CENTER);
		checkBox.setLayoutData(data);
		checkBox.addSelectionListener(this);

		// Create CCombo
		typeCombo = getWidgetFactory().createCCombo(entityCommonComposite, SWT.FLAT | SWT.READ_ONLY);
		data = new FormData();
		data.left = new FormAttachment(cLabel, -ITabbedPropertyConstants.HSPACE - 2);
		data.right = new FormAttachment(checkBox, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(cLabel, 0, SWT.CENTER);
		typeCombo.setLayoutData(data);
		typeCombo.addSelectionListener(this);
		typeCombo.setItems(typeComboValues);
		typeCombo.setText(PARAMETER);

		return entityCommonComposite;
	}

	private Composite internalEntityComposite;

	private Composite createInternalEntityComposite(Composite parent) {
		internalEntityComposite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(entityCommonComposite, -ITabbedPropertyConstants.VSPACE);
		internalEntityComposite.setLayoutData(data);

		// Create label first then attach other control to it
		CLabel entityValueLabel = getWidgetFactory().createCLabel(internalEntityComposite, VALUE); //$NON-NLS-1$

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=141106
		Point p = entityValueLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
		int labelWidth = Math.max(p.x, 98);
		data = new FormData(labelWidth, SWT.DEFAULT);
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(0, 0);
		entityValueLabel.setLayoutData(data);

		entityValueText = getWidgetFactory().createText(internalEntityComposite, "", SWT.NONE); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(entityValueLabel, -ITabbedPropertyConstants.HSPACE);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(entityValueLabel, 0, SWT.CENTER);
		entityValueText.setLayoutData(data);
		entityValueText.setEditable(true);
		entityValueText.addListener(SWT.Modify, this);

		return internalEntityComposite;
	}

	private Composite externalEntityComposite;

	private Composite createExternalEntityComposite(Composite parent) {
		externalEntityComposite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(entityCommonComposite, -ITabbedPropertyConstants.VSPACE);
		externalEntityComposite.setLayoutData(data);

		// Create label first then attach other control to it
		CLabel publicIdLabel = getWidgetFactory().createCLabel(externalEntityComposite, PUBLIC_ID); //$NON-NLS-1$

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=141106
		Point p = publicIdLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
		int labelWidth = Math.max(p.x, 98);
		data = new FormData(labelWidth, SWT.DEFAULT);
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(0, 0);
		publicIdLabel.setLayoutData(data);

		publicIdText = getWidgetFactory().createText(externalEntityComposite, "", SWT.NONE); //$NON-NLS-1$
		publicIdText.setEditable(true);
		data = new FormData();
		data.left = new FormAttachment(publicIdLabel, -ITabbedPropertyConstants.HSPACE);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(publicIdLabel, 0, SWT.CENTER);
		publicIdText.setLayoutData(data);
		publicIdText.addListener(SWT.Modify, this);

		// Create label first then attach other control to it
		// Create System ID Label
		CLabel systemIdLabel = getWidgetFactory().createCLabel(externalEntityComposite, SYSTEM_ID); //$NON-NLS-1$

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=141106
		p = systemIdLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
		labelWidth = Math.max(p.x, 98);
		data = new FormData(labelWidth, SWT.DEFAULT);
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(publicIdLabel, +ITabbedPropertyConstants.VSPACE);
		systemIdLabel.setLayoutData(data);

		// Create Wizard Button
		wizardButton = getWidgetFactory().createButton(externalEntityComposite, "", SWT.NONE); //$NON-NLS-1$
		wizardButton.setImage(AbstractUIPlugin.imageDescriptorFromPlugin(DTDUIPlugin.getDefault().getBundle().getSymbolicName(), "icons/browsebutton.gif").createImage()); //$NON-NLS-1$
		data = new FormData();
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(systemIdLabel, 0, SWT.CENTER);
		wizardButton.setLayoutData(data);
		wizardButton.addSelectionListener(this);

		// Create System ID Text
		systemIdText = getWidgetFactory().createText(externalEntityComposite, "", SWT.NONE); //$NON-NLS-1$
		// systemIdText.setEditable(false);
		data = new FormData();
		data.left = new FormAttachment(systemIdLabel, -ITabbedPropertyConstants.HSPACE);
		data.right = new FormAttachment(wizardButton, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(systemIdLabel, 0, SWT.CENTER);
		systemIdText.setLayoutData(data);
		systemIdText.addListener(SWT.Modify, this);

		return externalEntityComposite;
	}

	public void widgetSelected(SelectionEvent e) {
		if (e.widget == typeCombo) {
			Object input = getInput();
			if (input instanceof Entity) {
				Entity node = (Entity) input;
				String selected = typeCombo.getText();
				if (PARAMETER.equals(selected))
					node.setParameterEntity(true);
				else
					node.setParameterEntity(false);
			}
		}
		else if (e.widget == checkBox) {
			Object input = getInput();
			if (input instanceof Entity) {
				Entity node = (Entity) input;
				boolean selected = checkBox.getSelection();
				if (selected) {
					node.setExternalEntity(true);
					pageBook.showPage(externalEntityComposite);
				}
				else {
					node.setExternalEntity(false);
					pageBook.showPage(internalEntityComposite);
				}
			}
		}
		else if (e.widget == wizardButton) {
			Shell shell = Display.getCurrent().getActiveShell();
			IFile currentIFile = ((IFileEditorInput) getActiveEditor().getEditorInput()).getFile();
			ViewerFilter filter = new ResourceFilter(new String[]{".dtd"}, new IFile[]{currentIFile}, null); //$NON-NLS-1$

			DTDSelectIncludeFileWizard fileSelectWizard = new DTDSelectIncludeFileWizard(DTDPropertiesMessages._UI_FILEDIALOG_SELECT_DTD, DTDPropertiesMessages._UI_FILEDIALOG_SELECT_DTD_DESC, filter, (IStructuredSelection) fSelection);

			WizardDialog wizardDialog = new WizardDialog(shell, fileSelectWizard);
			wizardDialog.create();
			wizardDialog.setBlockOnOpen(true);
			int result = wizardDialog.open();
			if (result == Window.OK) {
				String value = systemIdText.getText();
				IFile selectedIFile = fileSelectWizard.getResultFile();
				String dtdFileString = value;
				if (selectedIFile != null) {
					dtdFileString = URIHelper.getRelativeURI(selectedIFile.getFullPath(), currentIFile.getFullPath());
				}
				systemIdText.setText(dtdFileString);
			}
		}

	}

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh() {
		setListenerEnabled(false);
		Object input = getInput();
		nameText.setText(""); //$NON-NLS-1$
		if (input != null) {
			if (input instanceof Entity) {
				Entity entity = (Entity) input;
				nameText.setText(entity.getName());

				if (entity.isParameterEntity())
					typeCombo.setText(PARAMETER);
				else
					typeCombo.setText(GENERAL);

				if (entity.isExternalEntity()) {
					checkBox.setSelection(true);
					pageBook.showPage(externalEntityComposite);
					publicIdText.setText(entity.getPublicID());
					systemIdText.setText(entity.getSystemID());
				}
				else {
					checkBox.setSelection(false);
					pageBook.showPage(internalEntityComposite);
					entityValueText.setText(entity.getValue());
				}
			}
		}
		setListenerEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISection#shouldUseExtraSpace()
	 */
	public boolean shouldUseExtraSpace() {
		return true;
	}

	public void doHandleEvent(Event event) {
		if (event.widget == nameText) {
			Object input = getInput();
			String newValue = nameText.getText();
			if (newValue.length() > 0 && input instanceof Entity) {
				Entity entity = (Entity) input;
				entity.setName(newValue);
			}
		}
		else if (event.widget == entityValueText) {
			Object input = getInput();
			String newValue = entityValueText.getText();
			if (input instanceof Entity) {
				Entity entity = (Entity) input;
				entity.setValue(newValue);
			}
		}
		else if (event.widget == publicIdText) {
			Object input = getInput();
			String newValue = publicIdText.getText();
			if (input instanceof Entity) {
				Entity entity = (Entity) input;
				entity.setPublicID(newValue);
			}
		}
		else if (event.widget == systemIdText) {
			Object input = getInput();
			String newValue = systemIdText.getText();
			if (input instanceof Entity) {
				Entity entity = (Entity) input;
				entity.setSystemID(newValue);
			}
		}

	}
}
