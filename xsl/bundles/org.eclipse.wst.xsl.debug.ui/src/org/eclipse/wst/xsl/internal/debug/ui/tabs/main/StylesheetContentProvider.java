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

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.xsl.launching.config.LaunchPipeline;
import org.eclipse.wst.xsl.launching.config.LaunchTransform;

public class StylesheetContentProvider implements IStructuredContentProvider {
	private TableViewer tableViewer;
	private LaunchPipeline pipeline;

	public StylesheetContentProvider() {
	}

	public Object[] getElements(Object inputElement) {
		return pipeline.getTransformDefs().toArray(new LaunchTransform[0]);
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		tableViewer = (TableViewer) viewer;
		pipeline = (LaunchPipeline) newInput;
	}

	public void dispose() {
	}

	public void setEntries(LaunchTransform[] transforms) {
		pipeline.setTransformDefs(new ArrayList<LaunchTransform>(Arrays
				.asList(transforms)));
		tableViewer.refresh();
	}

	public void addEntries(LaunchTransform[] res, Object beforeElement) {
		for (LaunchTransform transform : res) {
			pipeline.addTransformDef(transform);
		}
		tableViewer.add(res);
		// select the first new one
		tableViewer.setSelection(new StructuredSelection(res[0]), true);
	}

	public void removeEntries(LaunchTransform[] res) {
		for (LaunchTransform transform : res) {
			pipeline.removeTransformDef(transform);
		}
		tableViewer.refresh();
	}

}
