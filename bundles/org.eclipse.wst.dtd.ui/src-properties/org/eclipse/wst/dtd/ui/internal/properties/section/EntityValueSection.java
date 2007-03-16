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
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.dtd.core.internal.Entity;
import org.eclipse.wst.dtd.ui.internal.DTDPropertiesMessages;

public class EntityValueSection extends AbstractSection {
	private final String VALUE = DTDPropertiesMessages._UI_LABEL_ENTITY_VALUE;
	private final String PUBLIC_ID = DTDPropertiesMessages._UI_LABEL_PUBLIC_ID;
	private final String SYSTEM_ID = DTDPropertiesMessages._UI_LABEL_SYSTEM_ID;

	private Text valueText;
	private Text publicIdText;
	private Text systemIdText;

	private CLabel valueLabel;
	private CLabel publicIdLabel;
	private CLabel systemIdLabel;

	public void doHandleEvent(Event event) {
		if (event.widget == valueText) {
			Object input = getInput();
			String newValue = valueText.getText();
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

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory) {
		super.createControls(parent, factory);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);

		// Create label first then attach other control to it
		valueLabel = getWidgetFactory().createCLabel(composite, VALUE);

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=141106
		Point p = valueLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
		int labelWidth = Math.max(p.x, 98);
		FormData data = new FormData(labelWidth, SWT.DEFAULT);
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(0, 0);
		valueLabel.setLayoutData(data);

		// Entity Value
		valueText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(valueLabel, -ITabbedPropertyConstants.HSPACE);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(valueLabel, 0, SWT.CENTER);
		valueText.setLayoutData(data);
		valueText.addListener(SWT.Modify, this);

		// Create label first then attach other control to it
		publicIdLabel = getWidgetFactory().createCLabel(composite, PUBLIC_ID);

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=141106
		p = publicIdLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
		labelWidth = Math.max(p.x, 98);
		data = new FormData(labelWidth, SWT.DEFAULT);
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(valueLabel, +ITabbedPropertyConstants.VSPACE);
		publicIdLabel.setLayoutData(data);

		// Public ID
		publicIdText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(publicIdLabel, -ITabbedPropertyConstants.HSPACE);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(publicIdLabel, 0, SWT.CENTER);
		publicIdText.setLayoutData(data);
		publicIdText.addListener(SWT.Modify, this);

		// Create label first then attach other control to it
		systemIdLabel = getWidgetFactory().createCLabel(composite, SYSTEM_ID);

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=141106
		p = systemIdLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
		labelWidth = Math.max(p.x, 98);
		data = new FormData(labelWidth, SWT.DEFAULT);
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(publicIdLabel, +ITabbedPropertyConstants.VSPACE);
		systemIdLabel.setLayoutData(data);

		// System ID
		systemIdText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(systemIdLabel, -ITabbedPropertyConstants.HSPACE);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(systemIdLabel, 0, SWT.CENTER);
		systemIdText.setLayoutData(data);
		systemIdText.addListener(SWT.Modify, this);
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh() {
		setListenerEnabled(false);
		Object input = getInput();
		if (isExternalEntity()) {
			valueLabel.setVisible(false);
			valueText.setVisible(false);
			publicIdLabel.setVisible(true);
			publicIdText.setVisible(true);
			systemIdLabel.setVisible(true);
			systemIdText.setVisible(true);

			publicIdText.setText(""); //$NON-NLS-1$
			systemIdText.setText(""); //$NON-NLS-1$
			if (input != null && input instanceof Entity) {
				Entity entity = (Entity) input;
				publicIdText.setText((entity).getPublicID());
				systemIdText.setText((entity).getSystemID());
			}
		}
		else {
			valueLabel.setVisible(true);
			valueText.setVisible(true);
			publicIdLabel.setVisible(false);
			publicIdText.setVisible(false);
			systemIdLabel.setVisible(false);
			systemIdText.setVisible(false);

			valueText.setText(""); //$NON-NLS-1$
			if (input != null && input instanceof Entity)
				valueText.setText(((Entity) input).getValue());
		}
		setListenerEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISection#shouldUseExtraSpace()
	 */
	public boolean shouldUseExtraSpace() {
		return false;
	}

	private boolean isExternalEntity() {
		return EntityTypeSection.isExternalEntity;
	}
}