/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.ui.tests;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wst.sse.core.internal.builder.StructuredDocumentBuilder;


/**
 * @author nitin
 * 
 * A view to hang actions off of to execute arbitrary code at arbitrary times.
 */
public class ActionTestView extends ViewPart {

	class RegisterBuilderAction extends Action {
		public RegisterBuilderAction() {
			super("Register SDMB");
			setToolTipText("Register Structured Document Builder");
		}

		public void run() {
			super.run();
			StructuredDocumentBuilder.add(new NullProgressMonitor(), ResourcesPlugin.getWorkspace().getRoot(), null);
		}
	}

	class RegisterBuilderActionWithContext extends Action {
		public RegisterBuilderActionWithContext() {
			super("Register SDMB w/UI");
			setToolTipText("Register Structured Document Builder with UI Context");
		}

		public void run() {
			super.run();
			StructuredDocumentBuilder.add(new NullProgressMonitor(), ResourcesPlugin.getWorkspace().getRoot(), fControl);
		}
	}

	Control fControl = null;

	private List createActions() {
		List actions = new ArrayList();

		actions.add(new RegisterBuilderAction());
		actions.add(new RegisterBuilderActionWithContext());

		return actions;
	}

	/**
	 * @return
	 */
	private List createContribututions() {
		List actions = new ArrayList();
		return actions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		ITextViewer text = new TextViewer(parent, SWT.READ_ONLY);
		text.setDocument(new Document());
		fControl = text.getTextWidget();
		text.getDocument().set("Use either the toolbar or the menu to run your actions");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IViewPart#init(org.eclipse.ui.IViewSite)
	 */
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		List actions = createActions();
		for (int i = 0; i < actions.size(); i++) {
			site.getActionBars().getToolBarManager().add((IAction) actions.get(i));
			site.getActionBars().getMenuManager().add((IAction) actions.get(i));
		}
		List contributions = createContribututions();
		for (int i = 0; i < contributions.size(); i++) {
			site.getActionBars().getToolBarManager().add((IContributionItem) contributions.get(i));
			site.getActionBars().getMenuManager().add((IContributionItem) contributions.get(i));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		if (fControl != null && !fControl.isDisposed()) {
			fControl.setFocus();
		}
	}
}