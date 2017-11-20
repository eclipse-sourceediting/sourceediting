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

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wst.xsl.launching.config.LaunchAttribute;
import org.eclipse.wst.xsl.launching.config.LaunchTransform;

public class ParametersContentProvider implements IStructuredContentProvider {
	private LaunchTransform stylesheetEntry;
	private TableViewer parameterViewer;

	public Object[] getElements(Object inputElement) {
		if (stylesheetEntry == null)
			return new Object[0];
		return stylesheetEntry.getParameters().toArray();
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		stylesheetEntry = (LaunchTransform) newInput;
		parameterViewer = (TableViewer) viewer;
	}

	public void dispose() {
	}

	public LaunchAttribute[] getParameters() {
		return stylesheetEntry.getParameters().toArray(new LaunchAttribute[0]);
	}

	public void removeParameters(LaunchAttribute[] entries) {
		for (LaunchAttribute parameter : entries) {
			stylesheetEntry.getParameters().remove(parameter);
		}
		TableItem[] items = parameterViewer.getTable().getItems();
		List<LaunchAttribute> entryList = Arrays.asList(entries);
		Object sel = null;
		for (int i = items.length - 1; i >= 0; i--) {
			TableItem item = items[i];
			if (!entryList.contains(item.getData())) {
				sel = item.getData();
				break;
			}
		}
		parameterViewer.remove(entries);

		if (sel != null)
			parameterViewer.setSelection(new StructuredSelection(sel), true);
	}

	public void addParameter(LaunchAttribute parameter) {
		stylesheetEntry.getParameters().add(parameter);
		parameterViewer.add(parameter);
		parameterViewer.setSelection(new StructuredSelection(parameter), true);
		// parameterViewer.refresh();
	}
}
