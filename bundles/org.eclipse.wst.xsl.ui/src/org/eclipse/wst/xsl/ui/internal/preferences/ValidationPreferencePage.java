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
package org.eclipse.wst.xsl.ui.internal.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wst.xsl.core.internal.ValidationPreferences;
import org.eclipse.wst.xsl.core.internal.XSLCorePlugin;

public class ValidationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
	private Text maxErrorsText;

	public ValidationPreferencePage()
	{
		super();
		// only used when page is shown programatically
		setTitle("XSL Validation");
		setDescription("Configure validation preferences");
	}

	@Override
	protected Control createContents(Composite parent)
	{
		parent = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);

		Label label = new Label(parent, SWT.NULL);
		GridData gd = new GridData(SWT.NONE, SWT.CENTER, false, false);
		label.setText("Maximum number of errors reported per stylesheet:"); //$NON-NLS-1$
		label.setLayoutData(gd);

		maxErrorsText = new Text(parent, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		maxErrorsText.setLayoutData(gd);

		setInput();

		return parent;
	}

	private void setInput()
	{
		int missingParamPref = getInt(ValidationPreferences.MAX_ERRORS);
		maxErrorsText.setText(String.valueOf(missingParamPref));
	}
	
	public void init(IWorkbench workbench)
	{
	}
	
	@Override
	protected void performDefaults()
	{
		super.performDefaults();
	}
	
	private int getInt(String pref)
	{
		return XSLCorePlugin.getDefault().getPluginPreferences().getInt(pref);
	}

	private void setValue(String pref, int val)
	{
		XSLCorePlugin.getDefault().getPluginPreferences().setValue(pref, val);
	}

	@Override
	public boolean performOk()
	{
		String maxErrorsString = maxErrorsText.getText();
		int maxErrors;
		try
		{
			maxErrors = Integer.parseInt(maxErrorsString);
		}
		catch (NumberFormatException e)
		{
			maxErrors = getInt(ValidationPreferences.MAX_ERRORS);
		}
		setValue(ValidationPreferences.MAX_ERRORS, maxErrors);
		
		XSLCorePlugin.getDefault().savePluginPreferences();
		return true;
	}
}
