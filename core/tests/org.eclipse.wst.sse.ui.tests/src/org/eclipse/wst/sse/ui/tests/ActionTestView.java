/*******************************************************************************
 * Copyright (c) 2004, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.ui.tests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
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
 *         A view to hang actions off of to execute arbitrary code at
 *         arbitrary times.
 */
public class ActionTestView extends ViewPart {

	private static final String ACTIONTEXT = "actiontext";
	private static final String DEFAULT_ACTION_TEXT = "Use either the toolbar or the menu to run your actions\n\nWrite new lines with file paths and hit enter to open them.\n\n";


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
				textEditor = editor.getAdapter(ITextEditor.class);
			if (textEditor != null) {
				IDocument document = textEditor.getDocumentProvider().getDocument(editor.getEditorInput());
				document.set("");
			}
			else {
				print("Error getting IDocument.\n");
			}
		}
	}

	StyledText fControl = null;

	ISelection fSelection;
	private ISelectionListener fSelectionListener;
	private IDocument fDocument = new Document();

	private List<Action> createActions() {
		List<Action> actions = new ArrayList<>();

		actions.add(new EmptyTextSetter());
		actions.add(new ComponentViewer());
		return actions;
	}

	/**
	 * @return
	 */
	private List<IContributionItem> createContribututions() {
		List<IContributionItem> actions = new ArrayList<>();
		return actions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createPartControl(Composite parent) {
		ITextViewer text = new TextViewer(parent, SWT.MULTI);
		text.setDocument(fDocument);
		fControl = text.getTextWidget();
		fControl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent keyEvent) {
				if (keyEvent.character == '\r') {
					int offset = fControl.getCaretOffset();
					try {
						IRegion lineInformation = text.getDocument().getLineInformationOfOffset(offset - 1);
						String path = text.getDocument().get(lineInformation.getOffset(), lineInformation.getLength());
						if (path.trim().length() > 0) {
							Event event = new Event();
							event.type = SWT.OpenDocument;
							event.text = path.trim();
							event.widget = fControl;
							Method m = parent.getDisplay().getClass().getDeclaredMethod("sendEvent", new Class[]{int.class, Event.class});
							m.setAccessible(true);
							m.invoke(parent.getDisplay(), event.type, event);
						}
					}
					catch (BadLocationException e) {
						Logger.logException(e);
						e.printStackTrace();
					}
					catch (NoSuchMethodException e) {
						Logger.logException(e);
						e.printStackTrace();
					}
					catch (SecurityException e) {
						Logger.logException(e);
						e.printStackTrace();
					}
					catch (IllegalAccessException e) {
						Logger.logException(e);
						e.printStackTrace();
					}
					catch (IllegalArgumentException e) {
						Logger.logException(e);
						e.printStackTrace();
					}
					catch (InvocationTargetException e) {
						Logger.logException(e);
						e.printStackTrace();
					}
				}
				super.keyReleased(keyEvent);
			}
		});
	}

	@Override
	public void saveState(IMemento memento) {
		memento.putString(ACTIONTEXT, fDocument.get());
		super.saveState(memento);
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

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site);
		String contents = memento.getString(ACTIONTEXT);
		if (contents == null || contents.length() == 0) {
			contents = DEFAULT_ACTION_TEXT;
		}
		fDocument.set(contents);
		List<Action> actions = createActions();
		for (int i = 0; i < actions.size(); i++) {
			site.getActionBars().getToolBarManager().add(actions.get(i));
			site.getActionBars().getMenuManager().add(actions.get(i));
		}
		List<IContributionItem> contributions = createContribututions();
		for (int i = 0; i < contributions.size(); i++) {
			site.getActionBars().getToolBarManager().add(contributions.get(i));
			site.getActionBars().getMenuManager().add(contributions.get(i));
		}
		site.getWorkbenchWindow().getSelectionService().addPostSelectionListener(getSelectionListener());
	}

	void print(String s) {
		fControl.append(s);
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
