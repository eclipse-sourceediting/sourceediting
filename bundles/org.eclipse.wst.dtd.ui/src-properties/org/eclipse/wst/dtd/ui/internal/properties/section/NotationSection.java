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
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertyConstants;
import org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.dtd.core.internal.Notation;
import org.eclipse.wst.dtd.ui.internal.DTDPropertiesMessages;

public class NotationSection extends AbstractSection {
	private final String PUBLIC_ID = DTDPropertiesMessages._UI_LABEL_PUBLIC_ID;
	private final String SYSTEM_ID = DTDPropertiesMessages._UI_LABEL_SYSTEM_ID;

	private Text publicIdText;
	private Text systemIdText;

	private CLabel publicIdLabel;
	private CLabel systemIdLabel;

	public void doHandleEvent(Event event) {
		if (event.widget == publicIdText) {
			Object input = getInput();
			String newValue = publicIdText.getText();
			if (input instanceof Notation) {
				Notation notation = (Notation) input;
				notation.setPublicID(newValue);
			}
		}
		else if (event.widget == systemIdText) {
			Object input = getInput();
			String newValue = systemIdText.getText();
			if (input instanceof Notation) {
				Notation notation = (Notation) input;
				notation.setSystemID(newValue);
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
		FormData data;

		// Public ID
		publicIdText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 100);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, 0);
		publicIdText.setLayoutData(data);
		publicIdText.addListener(SWT.Modify, this);

		publicIdLabel = getWidgetFactory().createCLabel(composite, PUBLIC_ID);
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(publicIdText, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(publicIdText, 0, SWT.CENTER);
		publicIdLabel.setLayoutData(data);

		// System ID
		systemIdText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 100);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(publicIdText, +ITabbedPropertyConstants.VSPACE);
		systemIdText.setLayoutData(data);
		systemIdText.addListener(SWT.Modify, this);

		systemIdLabel = getWidgetFactory().createCLabel(composite, SYSTEM_ID);
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(systemIdText, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(systemIdText, 0, SWT.CENTER);
		systemIdLabel.setLayoutData(data);
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh() {
		setListenerEnabled(false);
		Object input = getInput();
		publicIdText.setText("");
		systemIdText.setText("");

		if (input != null && input instanceof Notation) {
			Notation notation = (Notation) input;
			publicIdText.setText((notation).getPublicID());
			systemIdText.setText((notation).getSystemID());
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

}