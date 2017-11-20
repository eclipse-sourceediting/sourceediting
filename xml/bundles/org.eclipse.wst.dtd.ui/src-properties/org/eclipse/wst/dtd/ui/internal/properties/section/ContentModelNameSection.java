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

import java.util.Iterator;

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
import org.eclipse.wst.dtd.core.internal.CMBasicNode;
import org.eclipse.wst.dtd.core.internal.CMNode;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.Element;
import org.eclipse.wst.dtd.core.internal.Entity;
import org.eclipse.wst.dtd.ui.internal.DTDPropertiesMessages;

public class ContentModelNameSection extends AbstractSection {
	private final String CONTENT_MODEL = DTDPropertiesMessages._UI_LABEL_CONTENT_MODEL;

	private CCombo typeCombo;
	private String[] typeComboValues = {CMNode.PCDATA};

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory) {
		super.createControls(parent, factory);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);

		// Create label first then attach other control to it
		CLabel cLabel = getWidgetFactory().createCLabel(composite, CONTENT_MODEL);

		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=141106
		Point p = cLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, false);
		int labelWidth = Math.max(p.x, 98);
		FormData data = new FormData(labelWidth, SWT.DEFAULT);
		data.left = new FormAttachment(0, 0);
		data.top = new FormAttachment(0, 0);
		cLabel.setLayoutData(data);

		typeCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT | SWT.READ_ONLY);
		data = new FormData();
		data.left = new FormAttachment(cLabel, -ITabbedPropertyConstants.HSPACE);
		data.right = new FormAttachment(100);
		data.top = new FormAttachment(cLabel, 0, SWT.CENTER);
		typeCombo.setLayoutData(data);
		typeCombo.addSelectionListener(this);
		typeCombo.setItems(typeComboValues);
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh() {
		Object input = getInput();

		if (input != null) {
			if (input instanceof CMBasicNode) {
				typeCombo.removeAll();
				typeCombo.add(CMNode.PCDATA);
				// NodeList list =
				// ((CMBasicNode)fInput).getDTDFile().getElementsAndParameterEntityReferences();
				Iterator iterator = ((CMBasicNode) input).getDTDFile().getNodes().iterator();
				boolean isForRootContent = ((CMBasicNode) input).isRootElementContent();
				while (iterator.hasNext()) {
					DTDNode node = (DTDNode) iterator.next();
					if (!isForRootContent && node instanceof Element)
						typeCombo.add(node.getName());
					if (node instanceof Entity && ((Entity) node).isParameterEntity())
						typeCombo.add("%" + node.getName() + ";"); //$NON-NLS-1$ //$NON-NLS-2$
				}

				typeCombo.setText(((CMBasicNode) input).getName());
			}
		}
	}

	public void widgetSelected(SelectionEvent e) {
		if (e.widget == typeCombo) {
			Object input = getInput();
			if (input instanceof CMBasicNode) {
				CMBasicNode node = (CMBasicNode) input;
				String selected = typeCombo.getText();
				// if (CMNode.PCDATA.equals(selected))
				node.setName(selected);
			}
		}
	}

	public boolean shouldUseExtraSpace() {
		return false;
	}
}