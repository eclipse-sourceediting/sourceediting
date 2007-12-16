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
package org.eclipse.wst.xsl.internal.debug.ui.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.StylesheetViewer;

public class MoveUpAction extends AbstractStylesheetAction
{
	public MoveUpAction(StylesheetViewer viewer)
	{
		super(ActionMessages.MoveUpAction_Text, viewer);
	}

	@Override
	public void run()
	{
		List targets = getOrderedSelection();
		if (targets.isEmpty())
		{
			return;
		}
		int top = 0;
		int index = 0;
		List list = getEntriesAsList();
		Iterator entries = targets.iterator();
		while (entries.hasNext())
		{
			Object target = entries.next();
			index = list.indexOf(target);
			if (index > top)
			{
				top = index - 1;
				Object temp = list.get(top);
				list.set(top, target);
				list.set(index, temp);
			}
			top = index;
		}
		setEntries(list);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection)
	{
		if (selection.isEmpty())
		{
			return false;
		}
		return getViewer().updateSelection(getActionType(), selection) && !isIndexSelected(selection, 0);
	}

	@Override
	protected int getActionType()
	{
		return MOVE;
	}
}
