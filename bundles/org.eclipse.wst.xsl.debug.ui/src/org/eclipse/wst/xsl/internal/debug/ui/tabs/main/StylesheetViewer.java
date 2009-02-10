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

import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.xsl.internal.debug.ui.actions.AbstractStylesheetAction;
import org.eclipse.wst.xsl.launching.config.LaunchTransform;

public class StylesheetViewer extends TableViewer
{
	private final ListenerList listenerList = new ListenerList();

	public StylesheetViewer(Composite parent)
	{
		super(parent);

		getTable().addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent event)
			{
				if (updateSelection(AbstractStylesheetAction.REMOVE, (IStructuredSelection) getSelection()) && event.character == SWT.DEL && event.stateMask == 0)
				{
					List<?> selection = getSelectionFromWidget();
					getStylesheetContentProvider().removeEntries(selection.toArray(new LaunchTransform[0]));
					notifyChanged();
				}
			}
		});
	}

	private StylesheetContentProvider getStylesheetContentProvider()
	{
		return (StylesheetContentProvider) super.getContentProvider();
	}

	public void setEntries(LaunchTransform[] transforms)
	{
		getStylesheetContentProvider().setEntries(transforms);
		notifyChanged();
	}

	public LaunchTransform[] getEntries()
	{
		return (LaunchTransform[]) getStylesheetContentProvider().getElements(null);
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

	public void addTransforms(LaunchTransform[] res)
	{
		IStructuredSelection sel = (IStructuredSelection) getSelection();
		Object beforeElement = sel.getFirstElement();
		if (getEntries().length > 1 && beforeElement == null)
			beforeElement = getEntries()[getEntries().length - 1];
		getStylesheetContentProvider().addEntries(res, beforeElement);
		notifyChanged();
	}

	public void removeEntries(LaunchTransform[] entries)
	{
		getStylesheetContentProvider().removeEntries(entries);
		notifyChanged();
	}

	public void addEntriesChangedListener(IStylesheetEntriesChangedListener listener)
	{
		listenerList.add(listener);
	}

	public void removeEntriesChangedListener(IStylesheetEntriesChangedListener listener)
	{
		listenerList.remove(listener);
	}

	private void notifyChanged()
	{
		Object[] listeners = listenerList.getListeners();
		for (Object element : listeners)
		{
			((IStylesheetEntriesChangedListener) element).entriesChanged(this);
		}
	}

}
