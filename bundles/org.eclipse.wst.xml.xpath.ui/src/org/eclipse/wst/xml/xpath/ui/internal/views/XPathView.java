/*******************************************************************************
 * Copyright (c) 2008, 2010 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - bug 261588 - Add Edit Namespace support for XPath view
 *     David Carver (Intalio) - bug 246110 - Clean up XPath UI.
 *     David Carver (Intalio) - bug 271288 - Set ContextNode for XPath Evaluation
 *     Jesper Steen Moller - bug 313992 - XPath evaluation does not show atomics
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.ui.internal.views;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.xpath.core.util.XSLTXPathHelper;
import org.eclipse.wst.xml.xpath.ui.internal.Messages;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathView extends ViewPart {

	private boolean isFiringSelection = false;
	private IPartListener2 partListener2 = new XPathPartListener();
	private ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (part == getSite().getPage().getActiveEditor()) {
				if (selection instanceof IStructuredSelection) {
					currentSelection = (IStructuredSelection) selection;
					if (!selection.isEmpty()
							&& (currentSelection.getFirstElement() instanceof Node)) {
						recalculateLocation((Node) currentSelection
								.getFirstElement());
						boolean isLinked = xpathViewActions
								.isLinkedWithEditor(treeViewer);
						if (isLinked) {
							treeViewer.setSelection(currentSelection, true);
						}
					} else {
						recalculateLocation(null);
					}
				}
			}
		}
	};
	private TreeViewer treeViewer;
	private IEditorPart activeEditor;
	private Text text;
	// private JFaceNodeContentProviderXPath contentProvider;
	private XPathComputer xpathComputer;
	private Text locationText;
	private XPathViewActions xpathViewActions = new XPathViewActions();
	private IPostSelectionProvider selectionProvider = new SelectionProvider();
	private String location = ""; //$NON-NLS-1$
	private String message;
	private boolean expressionValid = true;
	private Integer currentSheet = Integer.valueOf(0);
	private Map<Integer, String> sheetMap;
	private IStructuredSelection currentSelection;
	private Node contextNode = null;

	public void createPartControl(Composite parent) {
		Composite parentComp = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(1, false);
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		parentComp.setLayout(gl);
		GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1);
		parentComp.setLayoutData(gd);

		Composite comp = new Composite(parentComp, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		comp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

		Label label = new Label(comp, SWT.NONE);
		label.setText(Messages.XPathView_1);

		this.text = new Text(comp, SWT.BORDER);

		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		text.setLayoutData(gd);
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				recomputeXPath();
			}
		});

		this.locationText = new Text(comp, SWT.READ_ONLY | SWT.FULL_SELECTION);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		locationText.setLayoutData(gd);

		this.treeViewer = new TreeViewer(parentComp, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		treeViewer.getControl().setLayoutData(gd);
		treeViewer.setLabelProvider(new JFaceNodeLabelProviderXPath());
		treeViewer.setContentProvider(new JFaceNodeContentProviderXPath());
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				if (getSite().getPage().getActivePart() == XPathView.this) {
					handleTreeSelection((IStructuredSelection) event
							.getSelection(), false);
				}
			}
		});
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				handleTreeSelection(
						(IStructuredSelection) event.getSelection(), true);
			}

		});

		final CTabFolder folder = new CTabFolder(parentComp, SWT.BOTTOM
				| SWT.FLAT);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.heightHint = 0;
		folder.setLayoutData(gd);
		folder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				CTabItem item = (CTabItem) e.item;
				sheetMap.put(currentSheet, text.getText());
				pageChange(folder.indexOf(item));
			}
		});

		for (int i = 0; i < 5; i++) {
			CTabItem item = new CTabItem(folder, SWT.NONE, i);
			// item.setControl(comp);
			item.setText(Messages.XPathView_2 + (i + 1));
		}
		folder.setSelection(currentSheet);
		pageChange(currentSheet);

		this.xpathComputer = new XPathComputer(this);
		createMenu();
		createToolbar();
		createContextMenu();

		getSite().setSelectionProvider(selectionProvider);

		initEditorListener();
	}

	private void handleTreeSelection(IStructuredSelection selection,
			boolean reveal) {
		if (activeEditor != null) {
			isFiringSelection = true;
			if (selection.getFirstElement() != null) {
				Object element = selection.getFirstElement();
				if (element instanceof IDOMNode) {
					IDOMNode node = (IDOMNode) element;
					ITextEditor textEditor = (ITextEditor) activeEditor
							.getAdapter(ITextEditor.class);

					if (textEditor != null) {
						getSite().getWorkbenchWindow().getActivePage()
								.bringToTop(textEditor);
						if (reveal) {
							textEditor
									.selectAndReveal(node.getStartOffset(),
											node.getEndOffset()
													- node.getStartOffset());
						} else {
							textEditor.setHighlightRange(node.getStartOffset(),
									0, true);
						}
					}
				}
			}
			isFiringSelection = false;
		}
	}

	private void createContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				xpathViewActions.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, treeViewer);
	}

	private void pageChange(int index) {
		currentSheet = index;
		String exp = sheetMap.get(index);
		if (exp != null) {
			text.setText(exp);
		} else {
			text.setText("/"); //$NON-NLS-1$
		}
	}

	private void createMenu() {
		IMenuManager mgr = getViewSite().getActionBars().getMenuManager();
		IAction[] items = xpathViewActions.createMenuContributions(treeViewer);
		for (int i = 0; i < items.length; i++) {
			IAction item = items[i];
			mgr.add(item);
		}
	}

	private void createToolbar() {
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		IAction[] items = xpathViewActions
				.createToolbarContributions(treeViewer);
		for (int i = 0; i < items.length; i++) {
			IAction item = items[i];
			mgr.add(item);
		}
	}

	private void recalculateLocation(Node selected) {
		this.location = XSLTXPathHelper.calculateXPathToNode(selected);
		contextNode = selected;
		updateLocationText();
	}

	private void recomputeXPath() {
		if (activeEditor != null) {
			boolean valid = expressionValid;
			try {
				if (contextNode != null) {
					xpathComputer.setSelectedNode(contextNode);
				}
				xpathComputer.setText(text.getText());
				xpathComputer.compute();
				valid = true;
			} catch (XPathExpressionException e) {
				valid = false;
				if (e.getCause() != null) {
					message = e.getCause().getMessage();
				} else {
					message = "Invalid XPath expression"; //$NON-NLS-1$
				}
			}
			if (expressionValid != valid) {
				expressionValid = valid;
				updateLocationText();
			}
		}
	}

	protected void xpathRecomputed(final NodeList nodeList) {
		if (getSite() == null) return;
		Shell shell = getSite().getShell();
		if (shell == null) return;
		Display display = shell.getDisplay();
		if (display == null) return;
		display.asyncExec(new Runnable() {
			public void run() {
				Control refreshControl = treeViewer.getControl();
				if ((refreshControl != null) && !refreshControl.isDisposed()) {
					refreshControl.setRedraw(false);
					treeViewer.setInput(nodeList);
					treeViewer.setSelection(currentSelection, true);
					refreshControl.setRedraw(true);
				}
			}
		});
	}

	private void updateLocationText() {
		if (expressionValid) {
			locationText.setText(Messages.XPathView_0 + location);
			locationText.setForeground(null);
		} else {
			locationText.setText(message);
			locationText.setForeground(JFaceColors.getErrorText(locationText
					.getDisplay()));
		}
	}

	private void initEditorListener() {
		getSite().getPage().addPartListener(partListener2);
		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(selectionListener);
		editorActivated(getSite().getPage().getActiveEditor());
	}

	public void setFocus() {
		text.setFocus();
	}

	public void dispose() {
		getSite().getPage().removePartListener(partListener2);
		getSite().getWorkbenchWindow().getSelectionService()
				.removeSelectionListener(selectionListener);
		xpathComputer.dispose();
		super.dispose();
	}

	private void editorActivated(IWorkbenchPart part) {
		if ((part != activeEditor) && (part instanceof IEditorPart)) {
			IEditorPart editor = (IEditorPart) part;
			IStructuredModel model = getEditorModel(editor);
			if (model != null) {
				activeEditor = editor;
				xpathComputer.setModel(model);
				xpathComputer.setSelectedNode((Document) model
						.getAdapter(Document.class));
			}
			recomputeXPath();
		}
	}

	private void editorClosed(IWorkbenchPart part) {
		if (part == activeEditor) {
			treeViewer.setInput(null);
			locationText.setText("");
			activeEditor = null;
		}
	}

	private IStructuredModel getEditorModel(IEditorPart editor) {
		return (IStructuredModel) editor.getAdapter(IStructuredModel.class);
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class key) {
		Object adapter = null;
		if (key.equals(IShowInTarget.class) && (treeViewer != null)) {
			adapter = new IShowInTarget() {
				public boolean show(ShowInContext context) {
					treeViewer.setSelection(context.getSelection());
					return treeViewer.getSelection().equals(
							context.getSelection());
				}
			};
		} else if (key.equals(IShowInSource.class) && (activeEditor != null)) {
			adapter = new IShowInSource() {
				public ShowInContext getShowInContext() {
					return new ShowInContext(activeEditor.getEditorInput(),
							activeEditor.getEditorSite().getSelectionProvider()
									.getSelection());
				}
			};
		} else if (key.equals(IShowInTargetList.class)
				&& (activeEditor != null)) {
			adapter = activeEditor.getAdapter(key);
		}
		if (adapter == null) {
			adapter = super.getAdapter(key);
		}
		return adapter;
	}

	public void saveState(IMemento memento) {
		sheetMap.put(currentSheet, text.getText());
		memento.putInteger("CurrentSheet", currentSheet); //$NON-NLS-1$
		for (Map.Entry<Integer, String> entry : sheetMap.entrySet()) {
			IMemento child = memento.createChild("Sheet"); //$NON-NLS-1$
			child.putInteger("Index", entry.getKey()); //$NON-NLS-1$
			child.putString("XPath", entry.getValue()); //$NON-NLS-1$
		}
		boolean link = xpathViewActions.isLinkWithEditor();
		memento.putBoolean("LinkWithEditor", link);
		super.saveState(memento);
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		sheetMap = new HashMap<Integer, String>();
		if (memento != null) {
			IMemento[] sheets = memento.getChildren("Sheet"); //$NON-NLS-1$
			if (sheets != null) {
				currentSheet = memento.getInteger("CurrentSheet"); //$NON-NLS-1$
				for (IMemento sheet : sheets) {
					sheetMap
							.put(
									sheet.getInteger("Index"), sheet.getString("XPath")); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			Boolean linkWithEditor = memento.getBoolean("LinkWithEditor");
			boolean link = false;
			if (linkWithEditor != null) {
				link = linkWithEditor.booleanValue();
			}
			xpathViewActions.setLinkWithEditor(link);
		}
		if (currentSheet == null) {
			currentSheet = 0;
		}
		super.init(site, memento);
	}

	private class XPathPartListener implements IPartListener2 {
		public void partActivated(IWorkbenchPartReference partRef) {
			editorActivated(partRef.getPart(false));
		}

		public void partBroughtToTop(IWorkbenchPartReference partRef) {
		}

		public void partInputChanged(IWorkbenchPartReference partRef) {
		}

		public void partOpened(IWorkbenchPartReference partRef) {
		}

		public void partVisible(IWorkbenchPartReference partRef) {
		}

		public void partClosed(IWorkbenchPartReference partRef) {
			editorClosed(partRef.getPart(false));
		}

		public void partDeactivated(IWorkbenchPartReference partRef) {
		}

		public void partHidden(IWorkbenchPartReference partRef) {
		}
	}

	private class SelectionProvider implements IPostSelectionProvider {
		private class PostSelectionChangedListener implements
				ISelectionChangedListener {
			public void selectionChanged(SelectionChangedEvent event) {
				if (!isFiringSelection()) {
					fireSelectionChanged(event, postListeners);
				}
			}
		}

		private class SelectionChangedListener implements
				ISelectionChangedListener {
			public void selectionChanged(SelectionChangedEvent event) {
				if (!isFiringSelection()) {
					fireSelectionChanged(event, listeners);
				}
			}
		}

		private ListenerList listeners = new ListenerList();
		private ListenerList postListeners = new ListenerList();
		private ISelectionChangedListener postSelectionChangedListener = new PostSelectionChangedListener();
		private ISelectionChangedListener selectionChangedListener = new SelectionChangedListener();

		public void addPostSelectionChangedListener(
				ISelectionChangedListener listener) {
			postListeners.add(listener);
		}

		public void addSelectionChangedListener(
				ISelectionChangedListener listener) {
			listeners.add(listener);
		}

		public void fireSelectionChanged(final SelectionChangedEvent event,
				ListenerList listenerList) {
			isFiringSelection = true;
			Object[] listeners = listenerList.getListeners();
			for (int i = 0; i < listeners.length; ++i) {
				final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
				SafeRunner.run(new SafeRunnable() {
					public void run() {
						l.selectionChanged(event);
					}
				});
			}
			isFiringSelection = false;
		}

		public ISelection getSelection() {
			if (treeViewer != null) {
				return treeViewer.getSelection();
			}
			return StructuredSelection.EMPTY;
		}

		public boolean isFiringSelection() {
			return isFiringSelection;
		}

		public void removePostSelectionChangedListener(
				ISelectionChangedListener listener) {
			postListeners.remove(listener);
		}

		public void removeSelectionChangedListener(
				ISelectionChangedListener listener) {
			listeners.remove(listener);
		}

		public void setSelection(ISelection selection) {
			if (!isFiringSelection) {
				treeViewer.setSelection(selection);
			}
		}
	}
}
