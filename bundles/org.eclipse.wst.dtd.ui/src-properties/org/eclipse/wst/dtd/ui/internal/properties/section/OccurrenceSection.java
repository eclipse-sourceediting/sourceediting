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
import org.eclipse.wst.dtd.core.internal.CMRepeatableNode;
import org.eclipse.wst.dtd.ui.internal.DTDPropertiesMessages;

public class OccurrenceSection extends AbstractSection {
	private final String ONCE = DTDPropertiesMessages._UI_ONCE;
	private final String ONE_OR_MORE = DTDPropertiesMessages._UI_ONE_OR_MORE;
	private final String OPTIONAL = DTDPropertiesMessages._UI_OPTIONAL;
	private final String ZERO_OR_MORE = DTDPropertiesMessages._UI_ZERO_OR_MORE;
	private final String OCCURENCE = DTDPropertiesMessages._UI_LABEL_OCCURRENCE;

	private CCombo occurrenceCombo;
	private String[] occurrenceComboValues = {ONCE, ONE_OR_MORE, OPTIONAL, ZERO_OR_MORE};

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory) {
		super.createControls(parent, factory);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);

		occurrenceCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
		FormData data = new FormData();
		data.left = new FormAttachment(0, 100);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, 0);
		occurrenceCombo.setLayoutData(data);
		occurrenceCombo.addSelectionListener(this);
		occurrenceCombo.setItems(occurrenceComboValues);

		CLabel usageLabel = getWidgetFactory().createCLabel(composite, OCCURENCE);
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(occurrenceCombo, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(occurrenceCombo, 0, SWT.CENTER);
		usageLabel.setLayoutData(data);
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh() {
		setListenerEnabled(false);
		Object input = getInput();
		if (input instanceof CMRepeatableNode) {
			char occurence = ((CMRepeatableNode) input).getOccurrence();
			if (CMRepeatableNode.ONCE == occurence)
				occurrenceCombo.setText(ONCE);
			else if (CMRepeatableNode.ONE_OR_MORE == occurence)
				occurrenceCombo.setText(ONE_OR_MORE);
			else if (CMRepeatableNode.OPTIONAL == occurence)
				occurrenceCombo.setText(OPTIONAL);
			else if (CMRepeatableNode.ZERO_OR_MORE == occurence)
				occurrenceCombo.setText(ZERO_OR_MORE);
			else
				occurrenceCombo.setText(ONCE);
		}
		setListenerEnabled(true);
	}

	public void widgetSelected(SelectionEvent e) {
		Object input = getInput();
		if (input instanceof CMRepeatableNode) {
			setListenerEnabled(false);
			CMRepeatableNode node = (CMRepeatableNode) input;
			String occurrence = occurrenceCombo.getText();
			if (ONCE.equals(occurrence))
				node.setOccurrence(CMRepeatableNode.ONCE);
			else if (ONE_OR_MORE.equals(occurrence))
				node.setOccurrence(CMRepeatableNode.ONE_OR_MORE);
			else if (OPTIONAL.equals(occurrence))
				node.setOccurrence(CMRepeatableNode.OPTIONAL);
			else if (ZERO_OR_MORE.equals(occurrence))
				node.setOccurrence(CMRepeatableNode.ZERO_OR_MORE);
			setListenerEnabled(true);
		}
	}

}