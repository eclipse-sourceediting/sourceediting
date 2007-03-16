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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.dtd.core.internal.Entity;
import org.eclipse.wst.dtd.ui.internal.DTDPropertiesMessages;

public class EntityTypeSection extends AbstractSection {
	private final String ENTITY_TYPE = DTDPropertiesMessages._UI_LABEL_ENTITY_TYPE;
	private final String EXTERNAL_ENTITY = DTDPropertiesMessages._UI_LABEL_EXTERNAL_ENTITY;
	private final String PARAMETER = DTDPropertiesMessages._UI_LABEL_PARAMETER_ENTITY;
	private final String GENERAL = DTDPropertiesMessages._UI_LABEL_GENERAL_ENTITY;

	private CCombo typeCombo;
	private String[] typeComboValues = {PARAMETER, GENERAL};
	private Button checkBox;

	public static boolean isExternalEntity = false;

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory) {
		super.createControls(parent, factory);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);

		// Create label first then attach other control to it
		CLabel cLabel = getWidgetFactory().createCLabel(composite, ENTITY_TYPE);

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=141106
		Point p = cLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
		int labelWidth = Math.max(p.x, 98);
		FormData data = new FormData(labelWidth, SWT.DEFAULT);
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(0, 0);
		cLabel.setLayoutData(data);

		typeCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
		data = new FormData();
		data.left = new FormAttachment(cLabel, -ITabbedPropertyConstants.HSPACE);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(cLabel, 0, SWT.CENTER);
		typeCombo.setLayoutData(data);
		typeCombo.addSelectionListener(this);
		typeCombo.setItems(typeComboValues);

		checkBox = getWidgetFactory().createButton(composite, EXTERNAL_ENTITY, SWT.CHECK);

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=141106
		p = checkBox.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
		labelWidth = Math.max(p.x, 98);
		data = new FormData(labelWidth, SWT.DEFAULT);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(95, 0);
		data.top = new FormAttachment(cLabel, +ITabbedPropertyConstants.VSPACE);
		checkBox.setLayoutData(data);
		checkBox.addSelectionListener(this);
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh() {
		setListenerEnabled(false);
		Object input = getInput();

		if (input != null) {
			if (input instanceof Entity) {
				Entity entity = (Entity) input;
				if (entity.isParameterEntity())
					typeCombo.setText(PARAMETER);
				else
					typeCombo.setText(GENERAL);

				if (entity.isExternalEntity()) {
					checkBox.setSelection(true);
					isExternalEntity = true;
				}
				else {
					checkBox.setSelection(false);
					isExternalEntity = false;
				}
			}
		}
		setListenerEnabled(true);
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
					isExternalEntity = true;
				}
				else {
					node.setExternalEntity(false);
					isExternalEntity = false;
				}
			}
		}
	}

	public boolean shouldUseExtraSpace() {
		return false;
	}
}