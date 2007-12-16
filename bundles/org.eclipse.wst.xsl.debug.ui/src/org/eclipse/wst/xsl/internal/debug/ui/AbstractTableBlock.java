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
package org.eclipse.wst.xsl.internal.debug.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Table;

public abstract class AbstractTableBlock extends AbstractLaunchConfigurationTab
{
	private int fSortColumn;

	protected abstract Table getTable();

	protected abstract IDialogSettings getDialogSettings();

	protected abstract String getQualifier();

	protected void setSortColumn(int column)
	{
		fSortColumn = column;
	}

	/**
	 * Persist table settings into the give dialog store, prefixed with the
	 * given key.
	 */
	public void saveColumnSettings()
	{
		int columnCount = getTable().getColumnCount();
		for (int i = 0; i < columnCount; i++)
		{
			getDialogSettings().put(getQualifier() + ".columnWidth" + i, getTable().getColumn(i).getWidth());
		}
		getDialogSettings().put(getQualifier() + ".sortColumn", fSortColumn);
	}

	/**
	 * Restore table settings from the given dialog store using the given key.
	 * 
	 * @param widths
	 */
	public void restoreColumnSettings()
	{
		getTable().layout(true);
		restoreColumnWidths(getDialogSettings(), getQualifier());
		int col = 0;
		try
		{
			col = getDialogSettings().getInt(getQualifier() + ".sortColumn");
		}
		catch (NumberFormatException e)
		{
			col = 1;
		}
		setSortColumn(col);
	}

	private void restoreColumnWidths(IDialogSettings settings, String qualifier)
	{
		int columnCount = getTable().getColumnCount();
		for (int i = 0; i < columnCount; i++)
		{
			int width = -1;
			try
			{
				width = settings.getInt(qualifier + ".columnWidth" + i);
			}
			catch (NumberFormatException e)
			{
			}

			if (width > 0)
				getTable().getColumn(i).setWidth(width);
		}
	}

	@Override
	public void dispose()
	{
		if (getTable() != null && !getTable().isDisposed())
			saveColumnSettings();
		super.dispose();
	}
}
