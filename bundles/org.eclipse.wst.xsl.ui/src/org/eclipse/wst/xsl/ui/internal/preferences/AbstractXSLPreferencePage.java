/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractPreferencePage;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ScrolledPageContent;
import org.eclipse.wst.xsl.core.internal.XSLCorePlugin;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;

/**
 * Augments the SSE <code>AbstractPreferencePage</code> with support for expandable composites (twisties) and combos.
 * 
 * @author Doug Satchwell
 * @see org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractPreferencePage
 */
public abstract class AbstractXSLPreferencePage extends AbstractPreferencePage
{
	private final String SETTINGS_EXPANDED = getClass().getCanonicalName()+".SETTINGS_EXPANDED"; //$NON-NLS-1$
	private final String SETTINGS_SECTION_NAME = getClass().getCanonicalName()+".SETTINGS_SECTION_NAME";//$NON-NLS-1$
	private List<ExpandableComposite> fExpandables = new ArrayList<ExpandableComposite>();

	@Override
	protected final Control createContents(Composite parent)
	{
		createCommonContents(parent);
		loadPreferences();
		restoreSectionExpansionStates(getDialogSettings().getSection(SETTINGS_SECTION_NAME));
		return parent;
	}

	/**
	 * Called by createContents and must be implemented.
	 * 
	 * @param parent
	 * @return the composite
	 */
	protected abstract Composite createCommonContents(Composite parent);

	protected Composite createTwistie(Composite parent, String label, int cols)
	{
		ExpandableComposite excomposite = new ExpandableComposite(parent, SWT.NONE, ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT);
		excomposite.setText(label);
		excomposite.setExpanded(false);
		excomposite.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		excomposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, cols, 1));
		excomposite.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(ExpansionEvent e)
			{
				expandedStateChanged((ExpandableComposite) e.getSource());
			}
		});
		fExpandables.add(excomposite);
		makeScrollableCompositeAware(excomposite);

		Composite twistieCient = new Composite(excomposite, SWT.NONE);
		excomposite.setClient(twistieCient);

		GridLayout layout = new GridLayout(2, false);
		layout.marginRight = 5;
		twistieCient.setLayout(layout);

		GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		twistieCient.setLayoutData(gd);

		return twistieCient;
	}

	private final void expandedStateChanged(ExpandableComposite expandable)
	{
		ScrolledPageContent parentScrolledComposite = getParentScrolledComposite(expandable);
		if (parentScrolledComposite != null)
		{
			parentScrolledComposite.reflow(true);
		}
	}

	private void makeScrollableCompositeAware(Control control)
	{
		ScrolledPageContent parentScrolledComposite = getParentScrolledComposite(control);
		if (parentScrolledComposite != null)
		{
			parentScrolledComposite.adaptChild(control);
		}
	}

	private ScrolledPageContent getParentScrolledComposite(Control control)
	{
		Control parent = control.getParent();
		while (!(parent instanceof ScrolledPageContent) && parent != null)
		{
			parent = parent.getParent();
		}
		if (parent instanceof ScrolledPageContent)
		{
			return (ScrolledPageContent) parent;
		}
		return null;
	}

	protected Combo createCombo(Composite parent, String label, String prefKey)
	{
		GridData gd = new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1);

		Label labelControl = new Label(parent, SWT.LEFT);
		labelControl.setFont(JFaceResources.getDialogFont());
		labelControl.setText(label);
		labelControl.setLayoutData(gd);

		Combo combo = createDropDownBox(parent);
		combo.addSelectionListener(this);
		combo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		return combo;
	}

	private void storeSectionExpansionStates(IDialogSettings section)
	{
		for (int i = 0; i < fExpandables.size(); i++)
		{
			ExpandableComposite comp = fExpandables.get(i);
			section.put(SETTINGS_EXPANDED + String.valueOf(i), comp.isExpanded());
		}
	}

	private IDialogSettings getDialogSettings()
	{
		return XSLUIPlugin.getDefault().getDialogSettings();
	}

	/**
	 * Saves the expansion states before calling super.
	 * 
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	@Override
	public void dispose()
	{
		storeSectionExpansionStates(getDialogSettings().addNewSection(SETTINGS_SECTION_NAME));
		super.dispose();
	}

	private void restoreSectionExpansionStates(IDialogSettings settings)
	{
		for (int i = 0; i < fExpandables.size(); i++)
		{
			ExpandableComposite excomposite = fExpandables.get(i);
			if (settings == null)
			{
				excomposite.setExpanded(i == 0); // only expand the first node by default
			}
			else
			{
				excomposite.setExpanded(settings.getBoolean(SETTINGS_EXPANDED + String.valueOf(i)));
			}
		}
	}

	@Override
	protected Preferences getModelPreferences()
	{
		return XSLCorePlugin.getDefault().getPluginPreferences();
	}

	/**
	 * Save the preferences.
	 * 
	 * @see org.eclipse.wst.sse.ui.internal.preferences.ui.AbstractPreferencePage#performOk()
	 */
	@Override
	public boolean performOk()
	{
		boolean ok = super.performOk();
		if (ok)
			XSLCorePlugin.getDefault().savePluginPreferences();
		return ok;
	}
}
