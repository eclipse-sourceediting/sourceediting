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
package org.eclipse.wst.xsl.internal.debug.ui.tabs.resolver;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class URIResolverBlock2 extends AbstractLaunchConfigurationTab
{
	private Button customRadio;
	private Text customText;
	private final ResolverType[] resolverTypes = new ResolverType[]
	{ new ResolverType(Messages.getString("URIResolverBlock2.0"), Messages.getString("URIResolverBlock2.1")), new ResolverType("org.eclipse.wst.some.resolver.type.1", Messages.getString("URIResolverBlock2.3")) }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	private Button defaultRadio;
	private Combo combo;

	public void createControl(Composite parent)
	{
		Font font = parent.getFont();

		Group group = new Group(parent, SWT.NONE);
		group.setText(getName());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);
		GridLayout layout = new GridLayout(2, false);
		group.setLayout(layout);
		group.setFont(font);

		setControl(group);

		defaultRadio = new Button(group, SWT.RADIO);
		defaultRadio.setText(Messages.getString("URIResolverBlock2.4")); //$NON-NLS-1$
		defaultRadio.setFont(font);
		defaultRadio.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				customText.setEnabled(false);
				combo.setEnabled(true);
				updateLaunchConfigurationDialog();
			}
		});

		combo = new Combo(group, SWT.READ_ONLY);
		ComboViewer cViewer = new ComboViewer(combo);
		cViewer.setContentProvider(new IStructuredContentProvider()
		{

			public Object[] getElements(Object inputElement)
			{
				return resolverTypes;
			}

			public void dispose()
			{
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
			{
			}
		});
		cViewer.setLabelProvider(new LabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				ResolverType type = (ResolverType) element;
				return type.getDescription();
			}
		});
		cViewer.setInput(resolverTypes);
		cViewer.setSelection(new StructuredSelection(resolverTypes[0]));
		cViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				updateLaunchConfigurationDialog();
			}
		});

		customRadio = new Button(group, SWT.RADIO);
		customRadio.setText(Messages.getString("URIResolverBlock2.5")); //$NON-NLS-1$
		gd = new GridData(GridData.FILL_HORIZONTAL);
		customRadio.setLayoutData(gd);
		gd.horizontalSpan = 2;
		customRadio.setFont(font);
		customRadio.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				customText.setEnabled(true);
				combo.setEnabled(false);
				updateLaunchConfigurationDialog();
			}
		});

		Composite textComp = new Composite(group, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.horizontalIndent = 10;
		textComp.setLayoutData(gd);
		layout = new GridLayout(2, false);
		layout.marginWidth = 2;
		textComp.setLayout(layout);
		textComp.setFont(font);

		Label label = new Label(textComp, SWT.NONE);
		label.setText(Messages.getString("URIResolverBlock2.6")); //$NON-NLS-1$

		customText = new Text(textComp, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		customText.setLayoutData(gd);
		customText.setFont(font);
		customText.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				updateLaunchConfigurationDialog();
			}
		});
	}

	public String getName()
	{
		return Messages.getString("URIResolverBlock2.7"); //$NON-NLS-1$
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig)
	{
		String text = customText.getText();
		if (customRadio.getSelection() && (text == null || text.length() == 0))
		{
			setErrorMessage(Messages.getString("URIResolverBlock2.8")); //$NON-NLS-1$
			return false;
		}
		return super.isValid(launchConfig);
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{

	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
	}

	private static class ResolverType
	{
		private final String className;
		private final String description;

		public ResolverType(String className, String description)
		{
			this.className = className;
			this.description = description;
		}

		public String getClassName()
		{
			return className;
		}

		public String getDescription()
		{
			return description;
		}
	}
}
