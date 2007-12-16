/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui.tabs.formatter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.wst.xsl.internal.debug.ui.actions.ControlAccessibleListener;

public class XSLFOComboBlock
{
	private Composite fControl;
	private Button noneButton;
	private Button alternateButton;
	private Combo alternateCombo;
	private Button manageButton;

	public void createControl(Composite ancestor)
	{
		Font font = ancestor.getFont();
		Composite comp = new Composite(ancestor, SWT.NONE);
		GridLayout layout = new GridLayout();
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp.setFont(font);
		fControl = comp;

		Group group = new Group(comp, SWT.NULL);
		layout = new GridLayout();
		layout.numColumns = 3;
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setFont(font);

		GridData data;
		group.setText(RendererTabMessages.XSLFOComboBlock_XSLFOGroupTitle);

		noneButton = new Button(group, SWT.RADIO);
		noneButton.setText(RendererTabMessages.XSLFOComboBlock_None);
		noneButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (noneButton.getSelection())
				{
					// setUseDefault();
					// setStatus(OK_STATUS);
					// firePropertyChange();
				}
			}
		});
		data = new GridData();
		data.horizontalSpan = 3;
		noneButton.setLayoutData(data);
		noneButton.setFont(font);

		alternateButton = new Button(group, SWT.RADIO);
		alternateButton.setText(RendererTabMessages.XSLFOComboBlock_SpecificXSLFOProcessor);
		alternateButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (alternateButton.getSelection())
				{
					// fCombo.setEnabled(true);
					// if (fCombo.getText().length() == 0 && !fVMs.isEmpty()) {
					// fCombo.select(0);
					// }
					// if (fVMs.isEmpty()) {
					// setError(OutputTabMessages.ProcessorsComboBlock_0);
					// } else {
					// setStatus(OK_STATUS);
					// }
					// firePropertyChange();
				}
			}
		});

		alternateButton.setFont(font);
		data = new GridData(GridData.BEGINNING);
		alternateButton.setLayoutData(data);

		alternateCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		alternateCombo.setFont(font);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		alternateCombo.setLayoutData(data);
		ControlAccessibleListener.addListener(alternateCombo, alternateButton.getText());

		alternateCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// firePropertyChange();
			}
		});

		manageButton = new Button(group, SWT.PUSH);
		manageButton.setText(RendererTabMessages.XSLFOComboBlock_ManageXSLFOProcessor);
		manageButton.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event event)
			{
				// IPreferencePage page = new ProcessorsPreferencePage();
				// showPrefPage("org.eclipse.wst.xslt.launching.ui.preferences.ProcessorPreferencePage",
				// page);
			}
		});
	}

	public Control getControl()
	{
		return fControl;
	}
}
