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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.ui.internal.DTDPropertiesMessages;

public class NameSection extends AbstractSection {
	private final String NAME = DTDPropertiesMessages._UI_LABEL_NAME;
	private Text nameText;
	private FontMetrics fFontMetrics;

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
		// Create label first then attach other control to it
		CLabel nameLabel = getWidgetFactory().createCLabel(composite, NAME);
		initializeFontMetrics(nameLabel);
		int labelWidth = getLabelWidth(nameLabel.getText());
		data = new FormData(labelWidth, SWT.DEFAULT);
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(0, 0);
		nameLabel.setLayoutData(data);

		nameText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(nameLabel, -ITabbedPropertyConstants.HSPACE);
		data.right = new FormAttachment(100);
		data.top = new FormAttachment(nameLabel, 0, SWT.CENTER);
		nameText.setLayoutData(data);
		nameText.addListener(SWT.Modify, this);

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
		nameText.setText(""); //$NON-NLS-1$
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

	/**
	 * Initilize font metrics
	 * 
	 * @param control
	 */
	private void initializeFontMetrics(Control control) {
		GC gc = new GC(control);
		gc.setFont(control.getFont());
		fFontMetrics = gc.getFontMetrics();
		gc.dispose();
	}

	/**
	 * Determine appropriate label width
	 * 
	 * @param labelText
	 * @return
	 */
	private int getLabelWidth(String labelText) {
		int labelWidth = 98;

		int pixels = Dialog.convertWidthInCharsToPixels(fFontMetrics, labelText.length() + 5);
		labelWidth = Math.max(pixels, labelWidth);
		return labelWidth;
	}
}