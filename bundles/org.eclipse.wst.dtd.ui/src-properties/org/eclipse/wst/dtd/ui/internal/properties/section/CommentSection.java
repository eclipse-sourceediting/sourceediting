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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.dtd.core.internal.Comment;

public class CommentSection extends AbstractSection {
	private Text commentText;

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory) {
		super.createControls(parent, factory);

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		commentText = getWidgetFactory().createText(composite, "", SWT.MULTI | SWT.NONE); //$NON-NLS-1$
		commentText.addListener(SWT.Modify, CommentSection.this);

		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		commentText.setLayoutData(data);
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh() {
		setListenerEnabled(false);
		commentText.setEnabled(true);
		Object input = getInput();
		commentText.setText(""); //$NON-NLS-1$
		if (input != null) {
			if (input instanceof Comment)
				commentText.setText(((Comment) input).getText());
		}
		setListenerEnabled(true);
	}


	public void doHandleEvent(Event event) {
		Object input = getInput();
		if (input != null) {
			String newValue = commentText.getText();
			if (newValue.length() > 0 && input instanceof Comment) {
				Comment comment = (Comment) input;
				comment.setText(newValue);
			}
		}

	}

	public boolean shouldUseExtraSpace() {
		return true;
	}
}
