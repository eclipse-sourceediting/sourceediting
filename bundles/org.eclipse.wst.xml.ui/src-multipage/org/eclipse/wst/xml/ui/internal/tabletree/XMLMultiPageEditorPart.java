/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.xml.ui.internal.tabletree;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageEditorSite;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.ui.internal.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.IXMLPreferenceNames;
import org.eclipse.wst.xml.ui.internal.Logger;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.provisional.StructuredTextEditorXML;

public class XMLMultiPageEditorPart extends MultiPageEditorPart {

	/**
	 * Internal part activation listener
	 */
	class PartListener extends ShellAdapter implements IPartListener {
		private IWorkbenchPart fActivePart;
		private boolean fIsHandlingActivation = false;

		private void handleActivation() {

			if (fIsHandlingActivation)
				return;

			if (fActivePart == XMLMultiPageEditorPart.this) {
				fIsHandlingActivation = true;
				try {
					safelySanityCheckState();
				}
				finally {
					fIsHandlingActivation = false;
				}
			}
		}

		/**
		 * @see IPartListener#partActivated(IWorkbenchPart)
		 */
		public void partActivated(IWorkbenchPart part) {
			fActivePart = part;
			handleActivation();
		}

		/**
		 * @see IPartListener#partBroughtToTop(IWorkbenchPart)
		 */
		public void partBroughtToTop(IWorkbenchPart part) {
		}

		/**
		 * @see IPartListener#partClosed(IWorkbenchPart)
		 */
		public void partClosed(IWorkbenchPart part) {
		}

		/**
		 * @see IPartListener#partDeactivated(IWorkbenchPart)
		 */
		public void partDeactivated(IWorkbenchPart part) {
			fActivePart = null;
		}

		/**
		 * @see IPartListener#partOpened(IWorkbenchPart)
		 */
		public void partOpened(IWorkbenchPart part) {
		}

		/*
		 * @see ShellListener#shellActivated(ShellEvent)
		 */
		public void shellActivated(ShellEvent e) {
			handleActivation();
		}
	}

	/**
	 * Internal IPropertyListener
	 */
	class PropertyListener implements IPropertyListener {
		public void propertyChanged(Object source, int propId) {
			switch (propId) {
				// had to implement input changed "listener" so that
				// StructuredTextEditor could tell it containing editor that
				// the input has change, when a 'resource moved' event is
				// found.
				case IEditorPart.PROP_INPUT :
				case IEditorPart.PROP_DIRTY : {
					if (source == getTextEditor()) {
						if (getTextEditor().getEditorInput() != getEditorInput()) {
							setInput(getTextEditor().getEditorInput());
							/*
							 * title should always change when input changes.
							 * create runnable for following post call
							 */
							Runnable runnable = new Runnable() {
								public void run() {
									_firePropertyChange(IWorkbenchPart.PROP_TITLE);
								}
							};
							/*
							 * Update is just to post things on the display
							 * queue (thread). We have to do this to get the
							 * dirty property to get updated after other
							 * things on the queue are executed.
							 */
							postOnDisplayQue(runnable);
						}
					}
					break;
				}
				case IWorkbenchPart.PROP_TITLE : {
					// update the input if the title is changed
					if (source == getTextEditor()) {
						if (getTextEditor().getEditorInput() != getEditorInput()) {
							setInput(getTextEditor().getEditorInput());
						}
					}
					break;
				}
				default : {
					// propagate changes. Is this needed? Answer: Yes.
					if (source == getTextEditor()) {
						firePropertyChange(propId);
					}
					break;
				}
			}

		}
	}

	class TextInputListener implements ITextInputListener {
		public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
		}

		public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
			if (fDesignViewer != null && newInput != null)
				fDesignViewer.setModel(getModel());
		}
	}

	/** The design page index. */
	private int fDesignPageIndex;

	/** The design viewer */
	private IDesignViewer fDesignViewer;

	private PartListener fPartListener;

	IPropertyListener fPropertyListener = null;

	/** The source page index. */
	private int fSourcePageIndex;

	/** The text editor. */
	private StructuredTextEditor fTextEditor;

	/**
	 * StructuredTextMultiPageEditorPart constructor comment.
	 */
	public XMLMultiPageEditorPart() {
		super();
	}

	/*
	 * This method is just to make firePropertyChanged accessbible from some
	 * (anonomous) inner classes.
	 */
	void _firePropertyChange(int property) {
		super.firePropertyChange(property);
	}

	/**
	 * Adds the source page of the multi-page editor.
	 */
	private void addSourcePage() throws PartInitException {
		try {
			fSourcePageIndex = addPage(fTextEditor, getEditorInput());
			setPageText(fSourcePageIndex, XMLEditorMessages.XMLMultiPageEditorPart_0);
			// the update's critical, to get viewer selection manager and
			// highlighting to work
			fTextEditor.update();

			firePropertyChange(PROP_TITLE);

			// Changes to the Text Viewer's document instance should also
			// force an
			// input refresh
			fTextEditor.getTextViewer().addTextInputListener(new TextInputListener());
		}
		catch (PartInitException exception) {
			// dispose editor
			dispose();
			Logger.logException(exception);
			throw new SourceEditingRuntimeException(exception, XMLEditorMessages.An_error_has_occurred_when1_ERROR_);
		}
	}

	/**
	 * Connects the design viewer with the viewer selection manager. Should be
	 * done after createSourcePage() is done because we need to get the
	 * ViewerSelectionManager from the TextEditor. setModel is also done here
	 * because getModel() needs to reference the TextEditor.
	 */
	private void connectDesignPage() {
		if (fDesignViewer != null) {
			fDesignViewer.setViewerSelectionManager(fTextEditor.getViewerSelectionManager());
			fDesignViewer.setModel(getModel());
		}
	}

	/**
	 * Create and Add the Design Page using a registered factory
	 * 
	 */
	private void createAndAddDesignPage() {
		IDesignViewer tableTreeViewer = createDesignPage();

		fDesignViewer = tableTreeViewer;
		// note: By adding the design page as a Control instead of an
		// IEditorPart, page switches will indicate
		// a "null" active editor when the design page is made active
		fDesignPageIndex = addPage(tableTreeViewer.getControl());
		setPageText(fDesignPageIndex, tableTreeViewer.getTitle());
	}

	protected IDesignViewer createDesignPage() {
		XMLTableTreeViewer tableTreeViewer = new XMLTableTreeViewer(getContainer());
		// Set the default infopop for XML design viewer.
		XMLUIPlugin.getInstance().getWorkbench().getHelpSystem().setHelp(tableTreeViewer.getControl(), XMLTableTreeHelpContextIds.XML_DESIGN_VIEW_HELPID);
		return tableTreeViewer;
	}

	/**
	 * Creates the pages of this multi-page editor.
	 * <p>
	 * Subclasses of <code>MultiPageEditor</code> must implement this
	 * method.
	 * </p>
	 */
	protected void createPages() {
		try {
			// source page MUST be created before design page, now
			createSourcePage();
			createAndAddDesignPage();
			addSourcePage();
			connectDesignPage();

			int activePageIndex = getPreferenceStore().getInt(IXMLPreferenceNames.LAST_ACTIVE_PAGE);
			if (activePageIndex >= 0 && activePageIndex < getPageCount()) {
				setActivePage(activePageIndex);
			}
		}
		catch (PartInitException e) {
			Logger.logException(e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createSite(org.eclipse.ui.IEditorPart)
	 */
	protected IEditorSite createSite(IEditorPart editor) {
		IEditorSite site = null;
		if (editor == fTextEditor) {
			site = new MultiPageEditorSite(this, editor) {
				/**
				 * @see org.eclipse.ui.part.MultiPageEditorSite#getActionBarContributor()
				 */
				public IEditorActionBarContributor getActionBarContributor() {
					IEditorActionBarContributor contributor = super.getActionBarContributor();
					IEditorActionBarContributor multiContributor = XMLMultiPageEditorPart.this.getEditorSite().getActionBarContributor();
					if (multiContributor instanceof XMLMultiPageEditorActionBarContributor) {
						contributor = ((XMLMultiPageEditorActionBarContributor) multiContributor).sourceViewerActionContributor;
					}
					return contributor;
				}
			};
		}
		else {
			site = super.createSite(editor);
		}
		return site;
	}

	/**
	 * Creates the source page of the multi-page editor.
	 */
	protected void createSourcePage() throws PartInitException {
		fTextEditor = createTextEditor();
		fTextEditor.setEditorPart(this);

		if (fPropertyListener == null) {
			fPropertyListener = new PropertyListener();
		}
		fTextEditor.addPropertyListener(fPropertyListener);
	}

	/**
	 * Method createTextEditor.
	 * 
	 * @return StructuredTextEditor
	 */
	private StructuredTextEditor createTextEditor() {
		return new StructuredTextEditorXML();
	}

	private void disconnectDesignPage() {
		if (fDesignViewer != null) {
			fDesignViewer.setModel(null);
			fDesignViewer.setViewerSelectionManager(null);
		}
	}

	public void dispose() {
		Logger.trace("Source Editor", "StructuredTextMultiPageEditorPart::dispose entry"); //$NON-NLS-1$ //$NON-NLS-2$

		disconnectDesignPage();

		IWorkbenchWindow window = getSite().getWorkbenchWindow();
		window.getPartService().removePartListener(fPartListener);
		window.getShell().removeShellListener(fPartListener);
		if (fTextEditor != null && fPropertyListener != null) {
			fTextEditor.removePropertyListener(fPropertyListener);
		}

		// moved to last when added window ... seems like
		// we'd be in danger of losing some data, like site,
		// or something.
		super.dispose();

		Logger.trace("Source Editor", "StructuredTextMultiPageEditorPart::dispose exit"); //$NON-NLS-1$ //$NON-NLS-2$

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		fTextEditor.doSave(monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
		fTextEditor.doSaveAs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class key) {
		Object result = null;
		if (key == IDesignViewer.class) {
			result = fDesignViewer;

		}
		else if (key.equals(IGotoMarker.class)) {
			result = new IGotoMarker() {
				public void gotoMarker(IMarker marker) {
					XMLMultiPageEditorPart.this.gotoMarker(marker);
				}
			};
		}
		else {
			// DMW: I'm bullet-proofing this because
			// its been reported (on IBM WSAD 4.03 version) a null pointer
			// sometimes
			// happens here on startup, when an editor has been left
			// open when workbench shutdown.
			if (fTextEditor != null) {
				result = fTextEditor.getAdapter(key);
			}
		}
		return result;
	}

	private IStructuredModel getModel() {
		IStructuredModel model = null;
		if (fTextEditor != null)
			model = fTextEditor.getModel();
		return model;
	}

	private IPreferenceStore getPreferenceStore() {
		return XMLUIPlugin.getDefault().getPreferenceStore();
	}

	StructuredTextEditor getTextEditor() {
		return fTextEditor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#getTitle()
	 */
	public String getTitle() {
		String title = null;
		if (getTextEditor() == null) {
			if (getEditorInput() != null) {
				title = getEditorInput().getName();
			}
		}
		else {
			title = getTextEditor().getTitle();
		}
		if (title == null) {
			title = getPartName();
		}
		return title;
	}

	void gotoMarker(IMarker marker) {
		setActivePage(fSourcePageIndex);
		IDE.gotoMarker(fTextEditor, marker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite,
	 *      org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		try {
			super.init(site, input);
			if (fPartListener == null) {
				fPartListener = new PartListener();
			}
			// we want to listen for our own activation
			IWorkbenchWindow window = getSite().getWorkbenchWindow();
			window.getPartService().addPartListener(fPartListener);
			window.getShell().addShellListener(fPartListener);
		}
		catch (Exception e) {
			Logger.logException("exception initializing " + getClass().getName(), e);
		}
		setPartName(input.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return fTextEditor != null && fTextEditor.isSaveAsAllowed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isSaveOnCloseNeeded()
	 */
	public boolean isSaveOnCloseNeeded() {
		// overriding super class since it does a lowly isDirty!
		if (fTextEditor != null)
			return fTextEditor.isSaveOnCloseNeeded();
		return isDirty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#pageChange(int)
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		saveLastActivePageIndex(newPageIndex);
	}

	/**
	 * Posts the update code "behind" the running operation.
	 */
	void postOnDisplayQue(Runnable runnable) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
		if (windows != null && windows.length > 0) {
			Display display = windows[0].getShell().getDisplay();
			display.asyncExec(runnable);
		}
		else
			runnable.run();
	}


	void safelySanityCheckState() {
		// If we're called before editor is created, simply ignore since we
		// delegate this function to our embedded TextEditor
		if (getTextEditor() != null) {
			getTextEditor().safelySanityCheckState(getEditorInput());
		}
	}

	private void saveLastActivePageIndex(int newPageIndex) {
		// save the last active page index to preference manager
		getPreferenceStore().setValue(IXMLPreferenceNames.LAST_ACTIVE_PAGE, newPageIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	protected void setInput(IEditorInput input) {
		// If driven from the Source page, it's "model" may not be up to date
		// with the input just yet. We'll rely on later notification from the
		// TextViewer to set us straight
		super.setInput(input);
		if (fDesignViewer != null)
			fDesignViewer.setModel(getModel());
		setPartName(input.getName());
	}
}