/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.xml.internal.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageEditorSite;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.xml.core.XMLPreferenceNames;
import org.eclipse.wst.xml.ui.StructuredTextEditorXML;
import org.eclipse.wst.xml.ui.internal.Logger;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLTableTreeHelpContextIds;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLTableTreeViewer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class XMLMultiPageEditorPart extends MultiPageEditorPart implements IPropertyListener {

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
				} finally {
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

	/** The source page index. */
	private int fSourcePageIndex;
	/** The text editor. */
	private StructuredTextEditor fTextEditor;

	private PartListener partListener;

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
	protected void _firePropertyChange(int property) {
		super.firePropertyChange(property);
	}

	/**
	 * Adds the source page of the multi-page editor.
	 */
	protected void addSourcePage() throws PartInitException {
		try {
			fSourcePageIndex = addPage(fTextEditor, getEditorInput());
			setPageText(fSourcePageIndex, org.eclipse.wst.xml.ui.nls2.ResourceHandler.getString("XMLMultiPageEditorPart.0")); //$NON-NLS-1$
			// the update's critical, to get viewer selection manager and
			// highlighting to work
			fTextEditor.update();

			firePropertyChange(PROP_TITLE);

			// Changes to the Text Viewer's document instance should also
			// force an
			// input refresh
			fTextEditor.getTextViewer().addTextInputListener(new TextInputListener());
		} catch (PartInitException exception) {
			// dispose editor
			dispose();
			Logger.logException(exception);
			throw new SourceEditingRuntimeException(exception, ResourceHandler.getString("An_error_has_occurred_when1_ERROR_")); //$NON-NLS-1$
		}
	}

	/**
	 * Connects the design viewer with the viewer selection manager. Should be
	 * done after createSourcePage() is done because we need to get the
	 * ViewerSelectionManager from the TextEditor. setModel is also done here
	 * because getModel() needs to reference the TextEditor.
	 */
	protected void connectDesignPage() {
		if (fDesignViewer != null) {
			fDesignViewer.setViewerSelectionManager(fTextEditor.getViewerSelectionManager());
			fDesignViewer.setModel(getModel());
		}
	}

	/**
	 * Create and Add the Design Page using a registered factory
	 * 
	 */
	protected void createAndAddDesignPage() {
		XMLTableTreeViewer tableTreeViewer = new XMLTableTreeViewer(getContainer());
		//  Set the default infopop for XML design viewer.
		WorkbenchHelp.setHelp(tableTreeViewer.getControl(), XMLTableTreeHelpContextIds.XML_DESIGN_VIEW_HELPID);

		fDesignViewer = tableTreeViewer;
		// note: By adding the design page as a Control instead of an
		// IEditorPart, page switches will indicate
		// a "null" active editor when the design page is made active
		fDesignPageIndex = addPage(tableTreeViewer.getControl());
		setPageText(fDesignPageIndex, tableTreeViewer.getTitle());
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

			setActivePage();

			// future_TODO: add a catch block here for any exception the
			// design
			// page throws and convert it into a more informative message.
		} catch (PartInitException e) {
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
		} else {
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

		// Set the SourceViewerConfiguration now so the text editor won't use
		// the default configuration first
		// and switch to the StructuredTextViewerConfiguration later.
		// DMW removed setSourceViewerConfiguration 3/26/2003 since added
		// createPartControl to our text editor.
		// fTextEditor.setSourceViewerConfiguration();
		fTextEditor.addPropertyListener(this);
	}

	/**
	 * Method createTextEditor.
	 * 
	 * @return StructuredTextEditor
	 */
	protected StructuredTextEditor createTextEditor() {
		return new StructuredTextEditorXML();
	}

	protected void disconnectDesignPage() {
		if (fDesignViewer != null) {
			fDesignViewer.setModel(null);
			fDesignViewer.setViewerSelectionManager(null);
		}
	}

	public void dispose() {
		Logger.trace("Source Editor", "StructuredTextMultiPageEditorPart::dispose entry"); //$NON-NLS-1$ //$NON-NLS-2$

		disconnectDesignPage();

		IWorkbenchWindow window = getSite().getWorkbenchWindow();
		window.getPartService().removePartListener(partListener);
		window.getShell().removeShellListener(partListener);

		getSite().getPage().removePartListener(partListener);
		if (fTextEditor != null) {
			fTextEditor.removePropertyListener(this);
		}

		// moved to last when added window ... seems like
		// we'd be in danger of losing some data, like site,
		// or something.
		super.dispose();

		Logger.trace("Source Editor", "StructuredTextMultiPageEditorPart::dispose exit"); //$NON-NLS-1$ //$NON-NLS-2$

	}

	/*
	 * (non-Javadoc) Saves the contents of this editor. <p> Subclasses must
	 * override this method to implement the open-save-close lifecycle for an
	 * editor. For greater details, see <code> IEditorPart </code></p>
	 * 
	 * @see IEditorPart
	 */
	public void doSave(IProgressMonitor monitor) {
		fTextEditor.doSave(monitor);
		//		// this is a temporary way to force validation.
		//		// when the validator is a workbench builder, the following lines
		// can be removed
		//		if (fDesignViewer != null)
		//			fDesignViewer.saveOccurred();

	}

	/*
	 * (non-Javadoc) Saves the contents of this editor to another object. <p>
	 * Subclasses must override this method to implement the open-save-close
	 * lifecycle for an editor. For greater details, see <code> IEditorPart
	 * </code></p>
	 * 
	 * @see IEditorPart
	 */
	public void doSaveAs() {
		fTextEditor.doSaveAs();
		// 253619
		// following used to be executed here, but is
		// now called "back" from text editor (since
		// mulitiple paths to the performSaveAs in StructuredTextEditor.
		//doSaveAsForStructuredTextMulitPagePart();
	}

	private void editorInputIsAcceptable(IEditorInput input) throws PartInitException {
		if (input instanceof IFileEditorInput) {
			// verify that it can be opened
			CoreException[] coreExceptionArray = new CoreException[1];
			if (fileDoesNotExist((IFileEditorInput) input, coreExceptionArray)) {
				CoreException coreException = coreExceptionArray[0];
				if (coreException.getStatus().getCode() == IResourceStatus.FAILED_READ_LOCAL) {
					// I'm assuming this is always 'does not exist'
					// we'll refresh local go mimic behavior of default
					// editor, where the
					// troublesome file is refreshed (and will cause it to
					// 'disappear' from Navigator.
					try {
						((IFileEditorInput) input).getFile().refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
					} catch (CoreException ce) {
						// very unlikely
						Logger.logException(ce);
					}
					throw new PartInitException(ResourceHandler.getString("23concat_EXC_", (new Object[]{input.getName()}))); //$NON-NLS-1$
					//$NON-NLS-1$ = "Resource {0} does not exist."
				} else {
					throw new PartInitException(ResourceHandler.getString("32concat_EXC_", (new Object[]{input.getName()}))); //$NON-NLS-1$
					//$NON-NLS-1$ = "Editor could not be open on {0}"
				}
			}
		} else if (input instanceof IStorageEditorInput) {
			InputStream contents = null;
			try {
				contents = ((IStorageEditorInput) input).getStorage().getContents();
			} catch (CoreException noStorageExc) {
			}
			if (contents == null) {
				throw new PartInitException(ResourceHandler.getString("32concat_EXC_", (new Object[]{input.getName()}))); //$NON-NLS-1$
			} else {
				try {
					contents.close();
				} catch (IOException e) {
				}
			}
		}
	}

	//	void doSaveAsForStructuredTextMulitPagePart() {
	//		setPageText(getActivePage(), fTextEditor.getTitle());
	//		setInput(fTextEditor.getEditorInput());
	//		if (fDesignViewer != null) {
	//			//fDesignViewer.setEditorInput(fTextEditor.getEditorInput());
	//			fDesignViewer.setModel(getModel());
	//			fDesignViewer.saveAsOccurred();
	//		}
	//		// even though we've set title etc., several times already!
	//		// only now is all prepared for it.
	//		firePropertyChange(IWorkbenchPart.PROP_TITLE);
	//		firePropertyChange(PROP_DIRTY);
	//	}
	/*
	 * (non-Javadoc) Initializes the editor part with a site and input. <p>
	 * Subclasses of <code> EditorPart </code> must implement this method.
	 * Within the implementation subclasses should verify that the input type
	 * is acceptable and then save the site and input. Here is sample code:
	 * </p><pre> if (!(input instanceof IFileEditorInput)) throw new
	 * PartInitException("Invalid Input: Must be IFileEditorInput");
	 * setSite(site); setInput(editorInput); </pre>
	 */
	protected boolean fileDoesNotExist(IFileEditorInput input, Throwable[] coreException) {
		boolean result = false;
		InputStream inStream = null;
		if ((!(input.exists())) || (!(input.getFile().exists()))) {
			result = true;
		} else {
			try {
				inStream = input.getFile().getContents(true);
			} catch (CoreException e) {
				// very likely to be file not found
				result = true;
				coreException[0] = e;
			} finally {
				if (input != null) {
					try {
						if (inStream != null) {
							inStream.close();
						}
					} catch (IOException e) {
						Logger.logException(e);
					}
				}
			}
		}
		return result;
	}

	public Object getAdapter(Class key) {
		Object result = null;
		if (key == IDesignViewer.class) {
			result = fDesignViewer;
		} else {
			// DMW: I'm bullet-proofing this because
			// its been reported (on 4.03 version) a null pointer sometimes
			// happens here on startup, when an editor has been left
			// open when workbench shutdown.
			if (fTextEditor != null) {
				result = fTextEditor.getAdapter(key);
			}
		}
		return result;
	}

	/**
	 * IExtendedMarkupEditor method
	 */
	public Node getCaretNode() {
		if (getTextEditor() == null)
			return null;

		return getTextEditor().getCaretNode();
	}

	/**
	 * IExtendedSimpleEditor method
	 */
	public int getCaretPosition() {
		if (getTextEditor() == null)
			return -1;

		return getTextEditor().getCaretPosition();
	}

	/**
	 * IExtendedSimpleEditor method
	 */
	public IDocument getDocument() {
		if (getTextEditor() == null)
			return null;

		return getTextEditor().getDocument();
	}

	/**
	 * IExtendedMarkupEditor method
	 */
	public Document getDOMDocument() {
		if (getTextEditor() == null)
			return null;

		return getTextEditor().getDOMDocument();
	}

	/**
	 * IExtendedSimpleEditor method
	 */
	public IEditorPart getEditorPart() {
		return this;
	}

	protected IStructuredModel getModel() {
		IStructuredModel model = null;
		if (fTextEditor != null)
			model = fTextEditor.getModel();
		return model;
	}

	protected IPreferenceStore getPreferenceStore() {
		return XMLUIPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * IExtendedMarkupEditor method
	 */
	public List getSelectedNodes() {
		if (getTextEditor() == null)
			return null;
		return getTextEditor().getSelectedNodes();
	}

	/**
	 * IExtendedSimpleEditor method
	 */
	public Point getSelectionRange() {
		if (getTextEditor() == null)
			return new Point(-1, -1);

		return getTextEditor().getSelectionRange();
	}

	public StructuredTextEditor getTextEditor() {
		return fTextEditor;
	}

	/*
	 * (non-Javadoc) Method declared on IWorkbenchPart.
	 */
	public String getTitle() {
		String title = null;
		if (getTextEditor() == null) {
			if (getEditorInput() != null) {
				title = getEditorInput().getName();
			}
		} else {
			title = getTextEditor().getTitle();
		}
		if (title == null) {
			title = getPartName();
		}
		return title;
	}

	/*
	 * (non-Javadoc) Sets the cursor and selection state for this editor to
	 * the passage defined by the given marker. <p> Subclasses may override.
	 * For greater details, see <code> IEditorPart </code></p>
	 * 
	 * @see IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		// (pa) 20020217 this was null when opening an editor that was
		// already open
		if (fTextEditor != null) {
			IGotoMarker markerGotoer = (IGotoMarker) fTextEditor.getAdapter(IGotoMarker.class);
			markerGotoer.gotoMarker(marker);
		}
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		editorInputIsAcceptable(input);
		try {
			super.init(site, input);
			if (partListener == null) {
				partListener = new PartListener();
			}
			//getSite().getPage().addPartListener(partListner);
			// we want to listen for our own activation
			IWorkbenchWindow window = getSite().getWorkbenchWindow();
			window.getPartService().addPartListener(partListener);
			window.getShell().addShellListener(partListener);
		} catch (Exception e) {
			if (e instanceof SourceEditingRuntimeException) {
				Throwable t = ((SourceEditingRuntimeException) e).getOriginalException();
				if (t instanceof IOException) {
					System.out.println(t);
					// file not found
				}
			}
		}
		setPartName(input.getName());
	}

	/*
	 * (non-Javadoc) Returns whether the "save as" operation is supported by
	 * this editor. <p> Subclasses must override this method to implement the
	 * open-save-close lifecycle for an editor. For greater details, see
	 * <code> IEditorPart </code></p>
	 * 
	 * @see IEditorPart
	 */
	public boolean isSaveAsAllowed() {
		return fTextEditor != null && fTextEditor.isSaveAsAllowed();
	}

	/*
	 * (non-Javadoc) Returns whether the contents of this editor should be
	 * saved when the editor is closed. <p> This method returns <code> true
	 * </code> if and only if the editor is dirty ( <code> isDirty </code> ).
	 * </p>
	 */
	public boolean isSaveOnCloseNeeded() {
		// overriding super class since it does a lowly isDirty!
		if (fTextEditor != null)
			return fTextEditor.isSaveOnCloseNeeded();
		return isDirty();
	}

	/**
	 * Notifies this multi-page editor that the page with the given id has
	 * been activated. This method is called when the user selects a different
	 * tab.
	 * 
	 * @param newPageIndex
	 *            the index of the activated page
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);

		saveLastActivePageIndex(newPageIndex);
	}

	/**
	 * Posts the update code "behind" the running operation.
	 */
	protected void postOnDisplayQue(Runnable runnable) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
		if (windows != null && windows.length > 0) {
			Display display = windows[0].getShell().getDisplay();
			display.asyncExec(runnable);
		} else
			runnable.run();
	}

	/**
	 * Indicates that a property has changed.
	 * 
	 * @param source
	 *            the object whose property has changed
	 * @param propId
	 *            the id of the property which has changed; property ids are
	 *            generally defined as constants on the source class
	 */
	public void propertyChanged(Object source, int propId) {
		switch (propId) {
			// had to implement input changed "listener" so that
			// strucutedText could tell it containing editor that
			// the input has change, when a 'resource moved' event is
			// found.
			case IEditorPart.PROP_INPUT :
			case IEditorPart.PROP_DIRTY : {
				if (source == fTextEditor) {
					if (fTextEditor.getEditorInput() != getEditorInput()) {
						setInput(fTextEditor.getEditorInput());
						// title should always change when input changes.
						// create runnable for following post call
						Runnable runnable = new Runnable() {
							public void run() {
								_firePropertyChange(IWorkbenchPart.PROP_TITLE);
							}
						};
						// Update is just to post things on the display queue
						// (thread). We have to do this to get the dirty
						// property to get updated after other things on the
						// queue are executed.
						postOnDisplayQue(runnable);
					}
				}
				break;
			}
			case IWorkbenchPart.PROP_TITLE : {
				// update the input if the title is changed
				if (source == fTextEditor) {
					if (fTextEditor.getEditorInput() != getEditorInput()) {
						setInput(fTextEditor.getEditorInput());
					}
				}
				break;
			}
			default : {
				// propagate changes. Is this needed? Answer: Yes.
				if (source == fTextEditor) {
					firePropertyChange(propId);
				}
				break;
			}
		}

	}

	protected void safelySanityCheckState() {
		// If we're called before editor is created, simply ignore since we
		// delegate this function to our embedded TextEditor
		if (getTextEditor() == null)
			return;

		getTextEditor().safelySanityCheckState(getEditorInput());

	}

	protected void saveLastActivePageIndex(int newPageIndex) {
		// save the last active page index to preference manager
		getPreferenceStore().setValue(XMLPreferenceNames.LAST_ACTIVE_PAGE, newPageIndex);
	}

	/**
	 * Sets the currently active page.
	 */
	protected void setActivePage() {
		// retrieve the last active page index from preference manager
		int activePageIndex = getPreferenceStore().getInt(XMLPreferenceNames.LAST_ACTIVE_PAGE);

		// We check this range since someone could hand edit the XML
		// preference file to an invalid value ... which I know from
		// experience :( ... if they do, we'll reset to default and continue
		// rather than throw an assertion error in the setActivePage(int)
		// method.
		if (activePageIndex < 0 || activePageIndex >= getPageCount()) {
			activePageIndex = fDesignPageIndex;
		}
		setActivePage(activePageIndex);
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

	/**
	 * IExtendedMarkupEditor method
	 */
	public IStatus validateEdit(Shell context) {
		if (getTextEditor() == null)
			return new Status(IStatus.ERROR, XMLUIPlugin.ID, IStatus.INFO, "", null); //$NON-NLS-1$

		return getTextEditor().validateEdit(context);
	}

}