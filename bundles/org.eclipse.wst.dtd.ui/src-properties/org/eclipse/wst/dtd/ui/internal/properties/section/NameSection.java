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
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.ui.internal.DTDPropertiesMessages;

public class NameSection extends AbstractSection {
	private final String NAME = DTDPropertiesMessages._UI_LABEL_NAME;
	private Text nameText;

	public void doHandleEvent(Event event) {
		if (event.widget == nameText) {
			Object input = getInput();
			String newValue = nameText.getText();
			if (newValue.length() > 0 && input instanceof DTDNode) {
				DTDNode dtdNode = (DTDNode) input;
				dtdNode.setName(newValue);
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

		nameText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 100);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, 0);
		nameText.setLayoutData(data);
		nameText.addListener(SWT.Modify, this);

		CLabel nameLabel = getWidgetFactory().createCLabel(composite, NAME);
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(nameText, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(nameText, 0, SWT.CENTER);
		nameLabel.setLayoutData(data);

		// listener.startListeningForEnter(nameText);
		// listener.startListeningTo(nameText);
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh() {
		setListenerEnabled(false);
		nameText.setEnabled(true);
		Object input = getInput();
		nameText.setText("");
		if (input != null) {
			if (input instanceof DTDNode)
				nameText.setText(((DTDNode) input).getName());
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