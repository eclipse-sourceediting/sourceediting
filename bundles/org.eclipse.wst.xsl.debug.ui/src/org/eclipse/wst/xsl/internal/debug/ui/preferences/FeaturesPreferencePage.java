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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wst.xsl.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.launching.IFeature;
import org.eclipse.wst.xsl.launching.IProcessorType;
import org.eclipse.wst.xsl.launching.XSLTRuntime;

public class FeaturesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
	private final Map<IProcessorType, Map<String, String>> typeFeatureMap = new HashMap<IProcessorType, Map<String, String>>();
	private ComboViewer cViewer;
	private FeaturesBlock featuresBlock;

	public FeaturesPreferencePage()
	{
		super();
		// only used when page is shown programatically
		setTitle("Processor Features");
		setDescription("Set default values for installed processor types");
	}

	@Override
	protected Control createContents(Composite parent)
	{
		parent = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);

		Label label = new Label(parent, SWT.NULL);
		GridData gd = new GridData(SWT.NONE, SWT.CENTER, false, false);
		label.setText("Processor Type: ");
		label.setLayoutData(gd);

		Combo combo = new Combo(parent, SWT.READ_ONLY | SWT.SINGLE);
		gd = new GridData(SWT.NONE, SWT.CENTER, false, false);
		combo.setLayoutData(gd);

		cViewer = new ComboViewer(combo);
		cViewer.setContentProvider(new IStructuredContentProvider()
		{

			private IProcessorType[] types;

			public Object[] getElements(Object inputElement)
			{
				return types;
			}

			public void dispose()
			{
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
			{
				types = (IProcessorType[]) newInput;
			}
		});
		cViewer.setLabelProvider(new LabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				IProcessorType type = (IProcessorType) element;
				return type.getLabel();
			}
		});
		cViewer.setSorter(new ViewerSorter()
		{
			@Override
			public int compare(Viewer viewer, Object e1, Object e2)
			{
				IProcessorType type1 = (IProcessorType) e1;
				IProcessorType type2 = (IProcessorType) e2;
				return type1.getLabel().compareTo(type2.getLabel());
			}
		});
		cViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				IProcessorType type = (IProcessorType) selection.getFirstElement();
				if (type != null)
				{
					featuresBlock.setFeatureValues((Map<String, String>) typeFeatureMap.get(type));
					featuresBlock.setInput(type.getFeatures());
				}
				else
				{
					featuresBlock.setFeatureValues(new HashMap<String, String>());
					featuresBlock.setInput(new IFeature[0]);
				}
			}
		});
		cViewer.addFilter(new ViewerFilter()
		{

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element)
			{
				IProcessorType type = (IProcessorType) element;
				return !type.equals(XSLTRuntime.getProcessorType(XSLTRuntime.JRE_DEFAULT_PROCESSOR_TYPE_ID));
			}

		});

		featuresBlock = new FeaturesBlock(this);
		featuresBlock.createControl(parent);

		setInput();

		return parent;
	}

	private void setInput()
	{
		IProcessorType[] types = XSLTRuntime.getProcessorTypes();
		for (IProcessorType type : types)
		{
			typeFeatureMap.put(type, type.getFeatureValues());
		}
		cViewer.setInput(XSLTRuntime.getProcessorTypes());
		cViewer.setSelection(new StructuredSelection(XSLTRuntime.getProcessorTypesExclJREDefault()[0]), true);
	}

	public void init(IWorkbench workbench)
	{
	}
	
	@Override
	protected void performDefaults()
	{
		IProcessorType[] types = XSLTRuntime.getProcessorTypes();
		for (IProcessorType type : types)
		{
			typeFeatureMap.put(type, new HashMap<String,String>());
		}
		cViewer.setInput(XSLTRuntime.getProcessorTypes());
		cViewer.setSelection(new StructuredSelection(XSLTRuntime.getProcessorTypesExclJREDefault()[0]), true);
		
		super.performDefaults();
	}

	@Override
	public boolean performOk()
	{
		featuresBlock.saveColumnSettings();
		final boolean[] ok = new boolean[1];
		try
		{
			IRunnableWithProgress runnable = new IRunnableWithProgress()
			{
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
				{
					XSLTRuntime.saveFeaturePreferences(typeFeatureMap,monitor);
					ok[0] = !monitor.isCanceled();
				}
			};
			XSLDebugUIPlugin.getDefault().getWorkbench().getProgressService().busyCursorWhile(runnable);
		}
		catch (InvocationTargetException e)
		{
			XSLDebugUIPlugin.log(e);
		}
		catch (InterruptedException e)
		{
			XSLDebugUIPlugin.log(e);
		}
		return ok[0];
	}
}
