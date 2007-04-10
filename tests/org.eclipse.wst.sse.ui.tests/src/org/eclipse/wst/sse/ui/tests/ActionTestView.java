/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;


/**
 * @author nitin
 * 
 * A view to hang actions off of to execute arbitrary code at arbitrary times.
 */
public class ActionTestView extends ViewPart {

	private class ComponentViewer extends Action {
		public void run() {
			super.run();
			if (fSelection != null && !fSelection.isEmpty() && fSelection instanceof IStructuredSelection) {
				IStructuredSelection selection = (IStructuredSelection) fSelection;
				if (selection.getFirstElement() instanceof IResource) {
					IResource resource = (IResource) selection.getFirstElement();
					IVirtualResource[] virtualResources = ComponentCore.createResources(resource.getProject());
					// Only return results for Flexible projects
					if (virtualResources != null) {
						for (int i = 0; i < virtualResources.length; i++) {
							System.out.println(virtualResources[i].getComponent().getRootFolder().getWorkspaceRelativePath());
						}
					}
				}
			}
		}
	}

	class EmptyTextSetter extends Action {
		public EmptyTextSetter() {
			super("Set Text Editor text to empty");
			setToolTipText("Set Text Editor text to empty using set() API");
		}

		public void run() {
			super.run();
			IEditorPart editor = getViewSite().getPage().getActiveEditor();
			ITextEditor textEditor = null;
			if (editor instanceof ITextEditor)
				textEditor = (ITextEditor) editor;
			else
				textEditor = (ITextEditor) editor.getAdapter(ITextEditor.class);
			if (textEditor != null) {
				IDocument document = textEditor.getDocumentProvider().getDocument(editor.getEditorInput());
				document.set("");
			}
			else {
				print("Error getting IDocument.\n");
			}
		}
	}

	Control fControl = null;

	ISelection fSelection;
	private ISelectionListener fSelectionListener;

	private List createActions() {
		List actions = new ArrayList();

		actions.add(new EmptyTextSetter());
		actions.add(new ComponentViewer());
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
		text.getDocument().set("Use either the toolbar or the menu to run your actions\n\n");
	}

	private ISelectionListener getSelectionListener() {
		if (fSelectionListener == null) {
			fSelectionListener = new ISelectionListener() {
				public void selectionChanged(IWorkbenchPart part, ISelection selection) {
					fSelection = selection;
				}
			};
		}
		return fSelectionListener;
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
		site.getWorkbenchWindow().getSelectionService().addPostSelectionListener(getSelectionListener());
	}

	void print(String s) {
		((StyledText) fControl).append(s);
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
