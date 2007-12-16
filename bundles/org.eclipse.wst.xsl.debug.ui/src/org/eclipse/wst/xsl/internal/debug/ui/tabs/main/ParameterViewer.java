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
package org.eclipse.wst.xsl.internal.debug.ui.tabs.main;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.wst.xsl.internal.debug.ui.actions.AbstractStylesheetAction;
import org.eclipse.wst.xsl.launching.config.LaunchAttribute;

public class ParameterViewer extends TableViewer
{
	private final ListenerList fListeners = new ListenerList();

	public ParameterViewer(Table table)
	{
		super(table);

		table.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent event)
			{
				// TODO - removal
				// if (updateSelection(AbstractStylesheetAction.REMOVE,
				// (IStructuredSelection)getSelection()) && event.character ==
				// SWT.DEL && event.stateMask == 0)
				// {
				// List selection = getSelectionFromWidget();
				// getStylesheetContentProvider().removeEntries((IStylesheetEntry[])selection.toArray(new
				// IStylesheetEntry[0]));
				// notifyChanged();
				// }
			}
		});
	}

	private ParametersContentProvider getParametersContentProvider()
	{
		return (ParametersContentProvider) super.getContentProvider();
	}

	public Shell getShell()
	{
		return getControl().getShell();
	}

	public boolean isEnabled()
	{
		return true;
	}

	public boolean updateSelection(int actionType, IStructuredSelection selection)
	{
		switch (actionType)
		{
			case AbstractStylesheetAction.ADD:
				return true;
			case AbstractStylesheetAction.REMOVE:
			case AbstractStylesheetAction.MOVE:
			default:
				break;
		}
		return selection.size() > 0;
	}

	public IStructuredSelection getSelectedEntries()
	{
		IStructuredSelection selection = (IStructuredSelection) getSelection();
		return selection;
	}

	public void addParameter(LaunchAttribute parameter)
	{
		getParametersContentProvider().addParameter(parameter);
		notifyChanged();
	}

	public void removeEntries(LaunchAttribute[] entries)
	{
		getParametersContentProvider().removeParameters(entries);
		notifyChanged();
	}

	public LaunchAttribute[] getParameters()
	{
		return getParametersContentProvider().getParameters();
	}

	public void addParametersChangedListener(IParametersChangedListener listener)
	{
		fListeners.add(listener);
	}

	public void removeParametersChangedListener(IParametersChangedListener listener)
	{
		fListeners.remove(listener);
	}

	private void notifyChanged()
	{
		Object[] listeners = fListeners.getListeners();
		for (Object element : listeners)
		{
			((IParametersChangedListener) element).parametersChanged(this);
		}
	}

}
