/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.ui.internal.editorviewer;



import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.view.events.INodeSelectionListener;
import org.eclipse.wst.sse.ui.view.events.NodeSelectionChangedEvent;
import org.eclipse.wst.sse.ui.views.contentoutline.StructuredTextEditorContentOutlinePage;
import org.w3c.dom.Attr;


/**
 * TODO remove in C5 or earlier
 * @deprecated use the StructuredTextViewer directly if only using for read-only mode.  
 * otherwise, use the StructuredTextEditorViewer in qev plugin 
 * (removing in C5 or earlier)
 */
public class StructuredTextEditorViewer extends Viewer {

	protected void hook2(IEditorPart editor) {
		if (fHighlightRangeListener == null)
			fHighlightRangeListener = new NodeRangeSelectionListener();

		fContentOutlinePage = ((IContentOutlinePage) editor.getAdapter(IContentOutlinePage.class));
		if (fContentOutlinePage != null && fContentOutlinePage instanceof StructuredTextEditorContentOutlinePage) {
			((StructuredTextEditorContentOutlinePage) fContentOutlinePage).getViewerSelectionManager().addNodeSelectionListener(fHighlightRangeListener);

			if (!fContentOutlinePage.getSelection().isEmpty() && fContentOutlinePage.getSelection() instanceof IStructuredSelection) {
				getTextEditor().showHighlightRangeOnly(true);
				Object[] nodes = ((IStructuredSelection) fContentOutlinePage.getSelection()).toArray();
				IndexedRegion startNode = (IndexedRegion) nodes[0];
				IndexedRegion endNode = (IndexedRegion) nodes[nodes.length - 1];

				if (startNode instanceof Attr)
					startNode = (IndexedRegion) ((Attr) startNode).getOwnerElement();
				if (endNode instanceof Attr)
					endNode = (IndexedRegion) ((Attr) endNode).getOwnerElement();

				int start = startNode.getStartOffset();
				int end = endNode.getEndOffset();
				getTextEditor().resetHighlightRange();
				getTextEditor().setHighlightRange(start, end - start, true);

				IDocument document = getTextEditor().getDocumentProvider().getDocument(getEditorInput());
				if (document instanceof IStructuredDocument) {
					((IStructuredDocument) document).makeReadOnly(start, end - start);
					refresh();
				}

			}
		}
	}

	protected StructuredTextEditor fTextEditor = null;
	protected IContentOutlinePage fContentOutlinePage = null;

	private String fDefaultContentTypeID = null;
	protected INodeSelectionListener fHighlightRangeListener = null;

	private Composite parent = null;
	protected Composite container = null;

	protected boolean fEditable = true;
	protected IEditorInput fInput = null;
	protected int fStyle = SWT.NONE;

	/**
	 * Unhooks the editor when in follow mode and the target editing part is
	 * disposed.
	 */
	protected class UnhookOnDisposeListener implements DisposeListener {
		public void widgetDisposed(DisposeEvent e) {
			unhook();
		}
	}

	/**
	 * Sets the editor's highlighting text range to the text range indicated by the
	 * selected Nodes.
	 */
	protected class NodeRangeSelectionListener implements INodeSelectionListener {
		public void nodeSelectionChanged(NodeSelectionChangedEvent event) {
			if (!event.getSelectedNodes().isEmpty()) {
				IndexedRegion startNode = (IndexedRegion) event.getSelectedNodes().get(0);
				IndexedRegion endNode = (IndexedRegion) event.getSelectedNodes().get(event.getSelectedNodes().size() - 1);

				if (startNode instanceof Attr)
					startNode = (IndexedRegion) ((Attr) startNode).getOwnerElement();
				if (endNode instanceof Attr)
					endNode = (IndexedRegion) ((Attr) endNode).getOwnerElement();

				int start = startNode.getStartOffset();
				int end = endNode.getEndOffset();

				getTextEditor().resetHighlightRange();
				getTextEditor().setHighlightRange(start, end - start, true);
			}
			else {
				getTextEditor().resetHighlightRange();
			}
		}
	}

	public static class ViewEditorInput implements IEditorInput {

		private IStructuredModel fStructuredModel = null;

		public ViewEditorInput(IStructuredModel model) {
			super();
			fStructuredModel = model;
		}

		public boolean exists() {
			return fStructuredModel != null;
		}

		public ImageDescriptor getImageDescriptor() {
			return null;
		}

		public String getName() {
			if (exists())
				return fStructuredModel.getId();
			return "org.eclipse.wst.sse.ui.internal-view"; //$NON-NLS-1$
		}

		public IPersistableElement getPersistable() {
			return null;
		}

		public String getToolTipText() {
			return getStructuredModel().getBaseLocation();
		}

		public Object getAdapter(Class arg0) {
			return null;
		}

		public IStructuredModel getStructuredModel() {
			return fStructuredModel;
		}

		public void setStructuredModel(IStructuredModel structuredModel) {
			fStructuredModel = structuredModel;
		}
	}

	public static class ViewerEditorSite implements IEditorSite {

		protected IWorkbenchPartSite fSite = null;

		public ViewerEditorSite(IWorkbenchPartSite site) {
			fSite = site;
		}

		public IEditorActionBarContributor getActionBarContributor() {
			return null;
		}

		public IKeyBindingService getKeyBindingService() {
			return fSite.getKeyBindingService();
		}

		public String getId() {
			return fSite.getId();
		}

		public String getPluginId() {
			return fSite.getPluginId();
		}

		public String getRegisteredName() {
			return fSite.getRegisteredName();
		}

		public void registerContextMenu(String menuId, MenuManager menuManager, ISelectionProvider selectionProvider) {
			fSite.registerContextMenu(menuId, menuManager, selectionProvider);
		}

		public void registerContextMenu(MenuManager menuManager, ISelectionProvider selectionProvider) {
			fSite.registerContextMenu(menuManager, selectionProvider);
		}

		public IWorkbenchPage getPage() {
			return fSite.getPage();
		}

		public ISelectionProvider getSelectionProvider() {
			return fSite.getSelectionProvider();
		}

		public Shell getShell() {
			return fSite.getShell();
		}

		public IWorkbenchWindow getWorkbenchWindow() {
			return fSite.getWorkbenchWindow();
		}

		public void setSelectionProvider(ISelectionProvider provider) {
		}

		/**
		 * @see org.eclipse.ui.IEditorSite#getActionBars()
		 */
		public IActionBars getActionBars() {
			if (fSite instanceof IViewSite)
				return ((IViewSite) fSite).getActionBars();
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
		 */
		public Object getAdapter(Class adapter) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	/**
	 * Method StructuredTextEditorViewer.
	 * @param parent
	 */
	public StructuredTextEditorViewer(Composite parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * Method StructuredTextEditorViewer.
	 * @param parent
	 * @param style
	 */
	public StructuredTextEditorViewer(Composite parent, int style) {
		super();
		this.parent = parent;
		fTextEditor = createTextEditor();
		fStyle = style;
	}

	/**
	 * Method StructuredTextEditorViewer.
	 * @param parent
	 * @param style
	 * @param editor
	 */
	public StructuredTextEditorViewer(Composite parent, int style, StructuredTextEditor editor) {
		super();
		this.parent = parent;
		fTextEditor = editor;
		fStyle = style;
	}

	protected StructuredTextEditor createTextEditor() {
		StructuredTextEditor editor = new StructuredTextEditor();
		return editor;
	}

	/**
	 * If possible, retrieves the IEditorPart's StructuredTextEditorContentOutlinePage
	 * and displays the text content indicated by its Selection
	 * @param editor
	 */
	public void followSelection(IEditorPart editor) {
		setInput(editor.getEditorInput());
		hook(editor);
	}

	public void followSelection2(IEditorPart editor) {
		setInput(editor.getEditorInput());
		hook2(editor);
	}

	/**
	 * @see org.eclipse.jface.viewers.Viewer#getControl()
	 */
	public Control getControl() {
		return container;
	}

	public String getDefaultContentTypeID() {
		return fDefaultContentTypeID;
	}

	public IEditorInput getEditorInput() {
		return fInput;
	}

	/**
	 * @see org.eclipse.jface.viewers.IInputProvider#getInput()
	 */
	public Object getInput() {
		return getEditorInput();
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		return getTextEditor().getTextViewer().getSelection();
	}

	/**
	 * Returns the textEditor.
	 * @return StructuredTextEditor
	 */
	public StructuredTextEditor getTextEditor() {
		return fTextEditor;
	}

	protected void hook(IEditorPart editor) {
		if (fHighlightRangeListener == null)
			fHighlightRangeListener = new NodeRangeSelectionListener();

		fContentOutlinePage = ((IContentOutlinePage) editor.getAdapter(IContentOutlinePage.class));
		if (fContentOutlinePage != null && fContentOutlinePage instanceof StructuredTextEditorContentOutlinePage) {
			((StructuredTextEditorContentOutlinePage) fContentOutlinePage).getViewerSelectionManager().addNodeSelectionListener(fHighlightRangeListener);

			if (!fContentOutlinePage.getSelection().isEmpty() && fContentOutlinePage.getSelection() instanceof IStructuredSelection) {
				getTextEditor().showHighlightRangeOnly(true);
				Object[] nodes = ((IStructuredSelection) fContentOutlinePage.getSelection()).toArray();
				IndexedRegion startNode = (IndexedRegion) nodes[0];
				IndexedRegion endNode = (IndexedRegion) nodes[nodes.length - 1];

				if (startNode instanceof Attr)
					startNode = (IndexedRegion) ((Attr) startNode).getOwnerElement();
				if (endNode instanceof Attr)
					endNode = (IndexedRegion) ((Attr) endNode).getOwnerElement();

				int start = startNode.getStartOffset();
				int end = endNode.getEndOffset();
				getTextEditor().resetHighlightRange();
				getTextEditor().setHighlightRange(start, end - start, true);
			}
		}
	}

	/**
	 * Method init, for testing.
	 * @param site
	 */
	public void init(IWorkbenchPartSite site, IEditorInput input) {
		try {
			//			setInput(input);
			//			getTextEditor().init(new ViewerEditorSite(site), getEditorInput());
			getTextEditor().init(new ViewerEditorSite(site), input);
		}
		catch (PartInitException e) {
			e.printStackTrace();
		}
		container = new Composite(parent, fStyle);
		FillLayout fill = new FillLayout();
		container.setLayout(fill);

		getTextEditor().createPartControl(container);
		// part of the specific DnD support, even though it is itself an IEditorPart
		// TODO: 5.1W3 - should update the StructuredTextEditor to handle this functionality itself
		getTextEditor().setEditorPart(getTextEditor());
		getTextEditor().getTextViewer().getTextWidget().addDisposeListener(new UnhookOnDisposeListener());
	}

	/**
	 * @see org.eclipse.jface.viewers.Viewer#refresh()
	 */
	public void refresh() {
		getTextEditor().getTextViewer().refresh();
	}

	/**
	 * Sets the documentProvider.
	 * @param documentProvider The documentProvider to set
	 */
	public void setDocumentProvider(IDocumentProvider documentProvider) {
		getTextEditor().initializeDocumentProvider(documentProvider);
	}

	public void setEditable(boolean editable) {
		fEditable = editable;
	}

	/**
	 * Sets the viewer's input - expects a IStructuredModel or IEditorInput
	 * @see org.eclipse.jface.viewers.Viewer#setInput(Object)
	 */
	public void setInput(Object input) {
		//		getTextEditor().getTextViewer().getTextWidget().setEnabled(input != null);
		unhook();
		if (input instanceof IStructuredModel) {
			fInput = new ViewEditorInput((IStructuredModel) input);
			getTextEditor().setInput(fInput);
			updateEditor();
		}
		else if (input instanceof IEditorInput) {
			//			if (input instanceof IFileEditorInput) {
			//				setDocumentProvider(FileModelProvider.getInstance());
			//			}
			fInput = (IEditorInput) input;
			getTextEditor().setInput(fInput);
			updateEditor();
		}
		else
			throw new UnsupportedOperationException("setInput: unsupported type"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.viewers.Viewer#setSelection(ISelection, boolean)
	 */
	public void setSelection(ISelection selection, boolean reveal) {
		getTextEditor().getTextViewer().setSelection(selection, reveal);
	}

	/**
	 * @deprecated - leave for users
	 * 
	 * Show content from the beginning of the line on which the offset
	 * "start" is located for "length" number of characters.
	 */
	public void show(int start, int length) {
		getTextEditor().showHighlightRangeOnly(true);
		getTextEditor().resetHighlightRange();
		getTextEditor().setHighlightRange(start, length, true);
	}

	/**
	 * @deprecated - leave for users
	 * 
	 * Show all content.
	 */
	public void showAll() {
		getTextEditor().resetHighlightRange();
		getTextEditor().showHighlightRangeOnly(false);
	}

	/**
	 * cease showing just the current selection in the StructuredTextEditorContentOutlinePage
	 */
	public void stopFollowingSelection() {
		unhook();
	}

	protected void unhook() {
		if (fContentOutlinePage != null && fContentOutlinePage instanceof StructuredTextEditorContentOutlinePage) {
			((StructuredTextEditorContentOutlinePage) fContentOutlinePage).getViewerSelectionManager().removeNodeSelectionListener(fHighlightRangeListener);
			if (getTextEditor().getTextViewer() != null) {
				showAll();
			}
			fContentOutlinePage = null;
		}
	}

	protected void updateEditor() {
		if (getTextEditor().getTextViewer() != null && fInput != null) {
			getTextEditor().getTextViewer().setEditable(fEditable);
		}
	}

	public void dispose() {
		getTextEditor().dispose();
	}

	/**
	 * @param string
	 */
	public void setDefaultContentTypeID(String string) {
		fDefaultContentTypeID = string;
	}

}
