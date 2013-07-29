/*******************************************************************************
 * Copyright (c) 2006, 2013 IBM Corporation and others.
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.dtd.core.internal.Attribute;
import org.eclipse.wst.dtd.ui.internal.DTDPropertiesMessages;

public class AttributeDefaultSection extends AbstractSection {

	private final String IMPLIED = Attribute.IMPLIED;
	private final String REQUIRED = Attribute.REQUIRED;
	private final String FIXED = Attribute.FIXED;

	private CCombo usageCombo;
	private String[] usageComboValues = {IMPLIED, REQUIRED, FIXED, DTDPropertiesMessages._UI_DEFAULT};
	private Text defaultValueText;
	private CLabel defaultValueLabel;

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory) {
		super.createControls(parent, factory);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);

		// Create label first then attach other control to it
		CLabel usageLabel = getWidgetFactory().createCLabel(composite, DTDPropertiesMessages._UI_LABEL_USAGE);
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=141106
		Point p = usageLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
		int labelWidth = Math.max(p.x, 98);
		FormData data = new FormData(labelWidth, SWT.DEFAULT);
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(0, 0);
		usageLabel.setLayoutData(data);

		usageCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
		data = new FormData();
		data.left = new FormAttachment(usageLabel, -ITabbedPropertyConstants.HSPACE);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(usageLabel, 0, SWT.CENTER);
		usageCombo.setLayoutData(data);
		usageCombo.addSelectionListener(this);
		usageCombo.setItems(usageComboValues);

		// Create label first then attach other control to it
		defaultValueLabel = getWidgetFactory().createCLabel(composite, DTDPropertiesMessages._UI_LABEL_DEFAULT_VALUE);

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=141106
		p = defaultValueLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
		labelWidth = Math.max(p.x, 98);
		data = new FormData(labelWidth, SWT.DEFAULT);
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(usageLabel, +ITabbedPropertyConstants.VSPACE);
		defaultValueLabel.setLayoutData(data);

		defaultValueText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(defaultValueLabel, -ITabbedPropertyConstants.HSPACE);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(defaultValueLabel, 0, SWT.CENTER);
		defaultValueText.setLayoutData(data);
		defaultValueText.addListener(SWT.Modify, this);
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh() {
		setListenerEnabled(false);
		Object input = getInput();
		if (input != null) {
			if (input instanceof Attribute) {
				String kind = ((Attribute) input).getDefaultKind();
				if ("".equals(kind)) //$NON-NLS-1$
					usageCombo.setText(DTDPropertiesMessages._UI_DEFAULT);
				else
					usageCombo.setText(kind);

				if ("".equals(kind) || FIXED.equals(kind)) { //$NON-NLS-1$
					defaultValueLabel.setVisible(true);
					defaultValueText.setVisible(true);
					defaultValueText.setEnabled(true);
					defaultValueText.setText(((Attribute) input).getDefaultValue());
				}
				else {
					defaultValueText.setText(""); //$NON-NLS-1$
					//defaultValueLabel.setVisible(false);
					//defaultValueText.setVisible(false);
					defaultValueText.setEnabled(false);
				}
			}
		}
		setListenerEnabled(true);
	}

	public void widgetSelected(SelectionEvent e) {
		if (e.widget == usageCombo) {
			Object input = getInput();
			if (input instanceof Attribute) {
				String usage = usageCombo.getText();
				Attribute attribute = (Attribute) input;
				if (usage.equals(DTDPropertiesMessages._UI_DEFAULT))
					attribute.setDefaultKind(""); //$NON-NLS-1$
				else
					attribute.setDefaultKind(usage);

				if (DTDPropertiesMessages._UI_DEFAULT.equals(usage) || FIXED.equals(usage)) {
					defaultValueLabel.setVisible(true);
					defaultValueText.setVisible(true);
					defaultValueText.setEnabled(true);
				}
				else {
					defaultValueLabel.setVisible(false);
					defaultValueText.setVisible(false);
					defaultValueText.setEnabled(false);
				}
			}
		}
	}

	public void doHandleEvent(Event event) {
		Object input = getInput();
		if (input instanceof Attribute) {
			if (event.widget == defaultValueText) {
				String newValue = defaultValueText.getText();
				((Attribute) input).setDefaultValue(newValue, usageCombo.getText().equals(FIXED));
			}
		}
	}
}