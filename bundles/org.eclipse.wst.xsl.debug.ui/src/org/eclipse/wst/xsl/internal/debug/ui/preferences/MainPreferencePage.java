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
package org.eclipse.wst.xsl.internal.debug.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class MainPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
	public MainPreferencePage()
	{
		super();
		noDefaultAndApplyButton();
	}

	public void init(IWorkbench workbench)
	{
	}

	@Override
	protected Control createContents(Composite ancestor)
	{
		initializeDialogUnits(ancestor);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		ancestor.setLayout(layout);

		Label label = new Label(ancestor, SWT.NONE);
		label.setText("Expand the tree to set preferences ");

		// Label label = new Label(ancestor,SWT.NONE);
		// label.setText("Resolve URI's relative to:");
		//		
		// Button radio1 = new Button(ancestor,SWT.RADIO);
		// radio1.setText("working directory");
		//		
		// Button radio2 = new Button(ancestor,SWT.RADIO);
		// radio2.setText("stylesheet");
		//		
		// Button radio3 = new Button(ancestor,SWT.RADIO);
		// radio3.setText("input file");
		//
		// Button radio4 = new Button(ancestor,SWT.RADIO);
		// radio4.setText("specific location");
		//
		// label = new Label(ancestor,SWT.NONE);
		// label.setText("Default output directory:");
		//		
		// radio1 = new Button(ancestor,SWT.RADIO);
		// radio1.setText("temp user.dir");
		//		
		// radio2 = new Button(ancestor,SWT.RADIO);
		// radio2.setText("same as working directory");
		//		
		// radio3 = new Button(ancestor,SWT.RADIO);
		// radio3.setText("specific location");

		// TODO PlatformUI.getWorkbench().getHelpSystem().setHelp...

		applyDialogFont(ancestor);
		return ancestor;
	}

	@Override
	public boolean performOk()
	{
		return super.performOk();
	}
}
