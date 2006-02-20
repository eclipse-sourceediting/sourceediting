/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertyConstants;
import org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.dtd.core.internal.Attribute;
import org.eclipse.wst.dtd.ui.internal.DTDPropertiesMessages;

public class TypeSection extends AbstractSection {
	private final String TYPE = DTDPropertiesMessages._UI_LABEL_TYPE;

	private CCombo typeCombo;
	private String[] typeComboValues = {Attribute.CDATA, Attribute.ID, Attribute.IDREF, Attribute.IDREFS, Attribute.ENTITY, Attribute.ENTITIES, Attribute.NMTOKEN, Attribute.NMTOKENS, Attribute.ENUMERATED_NAME, Attribute.ENUMERATED_NOTATION};

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory) {
		super.createControls(parent, factory);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);

		typeCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
		data = new FormData();
		data.left = new FormAttachment(0, 100);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, 0);
		typeCombo.setLayoutData(data);
		typeCombo.addSelectionListener(this);
		typeCombo.setItems(typeComboValues);

		CLabel cLabel = getWidgetFactory().createCLabel(composite, TYPE);
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(typeCombo, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(typeCombo, 0, SWT.CENTER);
		cLabel.setLayoutData(data);

	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh() {
		Object input = getInput();

		if (input != null) {
			if (input instanceof Attribute)
				typeCombo.setText(((Attribute) input).getType());
		}

	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
		if (e.widget == typeCombo) {
			Object input = getInput();
			if (input instanceof Attribute) {
				Attribute attribute = (Attribute) input;
				attribute.setType(typeCombo.getText());
			}
		}
	}

	public boolean shouldUseExtraSpace() {
		return false;
	}

}