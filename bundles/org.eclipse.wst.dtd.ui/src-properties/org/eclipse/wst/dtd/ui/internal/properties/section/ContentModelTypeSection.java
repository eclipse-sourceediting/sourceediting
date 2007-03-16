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
import org.eclipse.wst.dtd.core.internal.CMGroupNode;
import org.eclipse.wst.dtd.core.internal.CMNode;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.Element;
import org.eclipse.wst.dtd.core.internal.Entity;
import org.eclipse.wst.dtd.ui.internal.DTDPropertiesMessages;

public class ContentModelTypeSection extends AbstractSection {
	private final String CONTENT_TYPE = DTDPropertiesMessages._UI_LABEL_CONTENT_TYPE;

	private CCombo typeCombo;
	private String[] typeComboValues = {CMNode.ANY, CMNode.EMPTY, CMNode.PCDATA, CMNode.CHILDREN, CMNode.MIXED};

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory) {
		super.createControls(parent, factory);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);

		// Create label first then attach other control to it
		CLabel cLabel = getWidgetFactory().createCLabel(composite, CONTENT_TYPE);

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
			if (input instanceof CMNode) {
				typeCombo.removeAll();
				typeCombo.add(CMNode.ANY);
				typeCombo.add(CMNode.EMPTY);
				typeCombo.add(CMNode.PCDATA);
				typeCombo.add(CMNode.CHILDREN);
				typeCombo.add(CMNode.MIXED);

				Iterator iterator = ((CMNode) input).getDTDFile().getNodes().iterator();
				String nodeName = null;
				while (iterator.hasNext()) {
					DTDNode node = (DTDNode) iterator.next();
					nodeName = node.getName();
					if (node instanceof Element && typeCombo.indexOf(nodeName) == -1)
						typeCombo.add(nodeName);
					else if (node instanceof Entity && ((Entity) node).isParameterEntity() && typeCombo.indexOf(nodeName) == -1)
						typeCombo.add("%" + nodeName + ";"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				if (input instanceof CMGroupNode)
					typeCombo.setText(((CMGroupNode) input).getType());
				else if (input instanceof CMBasicNode)
					typeCombo.setText(((CMBasicNode) input).getType());
			}
		} // end if (fInput != null)
	}

	public void widgetSelected(SelectionEvent e) {
		if (e.widget == typeCombo) {
			Object input = getInput();
			if (input instanceof CMGroupNode || input instanceof CMBasicNode) {
				CMNode node = (CMNode) input;
				String selected = typeCombo.getText();
				if (CMNode.MIXED.equals(selected))
					node.setMixedContent();
				else if (CMNode.CHILDREN.equals(selected))
					node.setChildrenContent(""); //$NON-NLS-1$
				else if (CMNode.EMPTY.equals(selected) || CMNode.ANY.equals(selected) || CMNode.PCDATA.equals(selected))
					node.setContent(selected);
				else
					node.setChildrenContent(selected);
			}
		}
	}

	public boolean shouldUseExtraSpace() {
		return false;
	}
}