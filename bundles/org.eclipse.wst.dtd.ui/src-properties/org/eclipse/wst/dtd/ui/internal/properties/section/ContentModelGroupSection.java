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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.dtd.core.internal.CMGroupNode;
import org.eclipse.wst.dtd.ui.internal.DTDPropertiesMessages;

public class ContentModelGroupSection extends AbstractSection {
	private final String SEQUENCE = DTDPropertiesMessages._UI_SEQUENCE;
	private final String CHOICE = DTDPropertiesMessages._UI_CHOICE;
	private final String MODEL_GROUP = DTDPropertiesMessages._UI_LABEL_MODEL_GROUP;

	private CCombo modelGroupCombo;
	private String[] modelGroupComboValues = {SEQUENCE, CHOICE};

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory) {
		super.createControls(parent, factory);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);

		// Create label first then attach other control to it
		CLabel cLabel = getWidgetFactory().createCLabel(composite, MODEL_GROUP);

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=141106
		Point p = cLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
		int labelWidth = Math.max(p.x, 98);
		FormData data = new FormData(labelWidth, SWT.DEFAULT);
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(0, 0);
		cLabel.setLayoutData(data);

		modelGroupCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
		data = new FormData();
		data.left = new FormAttachment(cLabel, -ITabbedPropertyConstants.HSPACE);
		data.right = new FormAttachment(100);
		data.top = new FormAttachment(cLabel, 0, SWT.CENTER);
		modelGroupCombo.setLayoutData(data);
		modelGroupCombo.addSelectionListener(this);
		modelGroupCombo.setItems(modelGroupComboValues);
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh() {
		setListenerEnabled(false);
		Object input = getInput();
		if (input != null) {
			if (input instanceof CMGroupNode) {
				CMGroupNode node = (CMGroupNode) input;
				char modelType = node.getConnector();
				if (CMGroupNode.CHOICE == modelType)
					modelGroupCombo.setText(CHOICE);
				else if (CMGroupNode.SEQUENCE == modelType)
					modelGroupCombo.setText(SEQUENCE);
			}
		}
		setListenerEnabled(true);
	}

	public void widgetSelected(SelectionEvent e) {
		if (e.widget == modelGroupCombo) {
			Object input = getInput();
			if (input instanceof CMGroupNode) {
				CMGroupNode node = (CMGroupNode) input;
				if (CHOICE.equals(modelGroupCombo.getText()))
					node.setConnector(CMGroupNode.CHOICE);
				else if (SEQUENCE.equals(modelGroupCombo.getText()))
					node.setConnector(CMGroupNode.SEQUENCE);
			}
		}
	}
}
