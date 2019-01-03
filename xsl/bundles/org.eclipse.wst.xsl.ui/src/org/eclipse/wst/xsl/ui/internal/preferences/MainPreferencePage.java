/*******************************************************************************
 * Copyright (c) 2007, 2009 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver - STAR - bug 213853 - Moved main preference page out of
 *                                        debug ui and into XSL ui.
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wst.xsl.ui.internal.Messages;

/**
 * 
 *
 */
public class MainPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
	/**
	 * MainPreferncePage is the constructor for the XSL Preference Page
	 */
	public MainPreferencePage()
	{
		super();
		noDefaultAndApplyButton();
	}

	/** 
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
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
		label.setText(Messages.MainPreferencePage);
		
		applyDialogFont(ancestor);
		return ancestor;
	}

	/** 
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk()
	{
		return super.performOk();
	}
}
