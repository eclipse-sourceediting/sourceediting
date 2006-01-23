/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySheetPageContributor;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.internal.undo.IStructuredTextUndoManager;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.contentoutline.ConfigurableContentOutlinePage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsd.ui.internal.graph.XSDGraphViewer;
import org.eclipse.wst.xsd.ui.internal.properties.section.XSDTabbedPropertySheetPage;
import org.eclipse.wst.xsd.ui.internal.provider.CategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.provider.XSDAdapterFactoryLabelProvider;
import org.eclipse.wst.xsd.ui.internal.provider.XSDModelAdapterFactoryImpl;
import org.eclipse.wst.xsd.ui.internal.text.XSDModelAdapter;
import org.eclipse.wst.xsd.ui.internal.util.OpenOnSelectionHelper;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

// public class XSDEditor extends StructuredTextMultiPageEditorPart
public class XSDEditor extends XSDMultiPageEditorPart implements ITabbedPropertySheetPageContributor {
	protected StructuredTextEditor textEditor;
	IFile resourceFile;
	XSDSelectionManager xsdSelectionManager;
	XSDModelAdapter schemalNodeAdapter;

	private IStructuredModel result;

	/**
	 * Listener on SSE's outline page's selections that converts DOM
	 * selections into wsdl selections and notifies WSDL selection manager
	 */
	class OutlineTreeSelectionChangeListener implements ISelectionChangedListener, IDoubleClickListener {
		public OutlineTreeSelectionChangeListener() {
		}

		private ISelection getXSDSelection(ISelection selection) {
			ISelection sel = null;
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				Object o = structuredSelection.getFirstElement();
				if (o != null)
					sel = new StructuredSelection(o);
			}
			return sel;
		}

		/**
		 * Determines DOM node based on object (xsd node)
		 * 
		 * @param object
		 * @return
		 */
		private Object getObjectForOtherModel(Object object) {
			Node node = null;

			if (object instanceof Node) {
				node = (Node) object;
			}
			else if (object instanceof XSDComponent) {
				node = ((XSDComponent) object).getElement();
			}
			else if (object instanceof CategoryAdapter) {
				node = ((CategoryAdapter) object).getXSDSchema().getElement();
			}

			// the text editor can only accept sed nodes!
			//
			if (!(node instanceof IDOMNode)) {
				node = null;
			}
			return node;
		}

		public void doubleClick(DoubleClickEvent event) {
			/*
			 * Selection in outline tree changed so set outline tree's
			 * selection into editor's selection and say it came from outline
			 * tree
			 */
			if (getSelectionManager() != null && getSelectionManager().enableNotify) {
				ISelection selection = getXSDSelection(event.getSelection());
				if (selection != null) {
					getSelectionManager().setSelection(selection, fOutlinePage);
				}

				if (getTextEditor() != null && selection instanceof IStructuredSelection) {
					int start = -1;
					int length = 0;
					Object o = ((IStructuredSelection) selection).getFirstElement();
					if (o != null)
						o = getObjectForOtherModel(o);
					if (o instanceof IndexedRegion) {
						start = ((IndexedRegion) o).getStartOffset();
						length = ((IndexedRegion) o).getEndOffset() - start;
					}
					if (start > -1) {
						getTextEditor().selectAndReveal(start, length);
					}
				}
			}
		}

		public void selectionChanged(SelectionChangedEvent event) {
			/*
			 * Selection in outline tree changed so set outline tree's
			 * selection into editor's selection and say it came from outline
			 * tree
			 */
			if (getSelectionManager() != null) { // && getSelectionManager().enableNotify) {
				
				ISelection selection = getXSDSelection(event.getSelection());
				if (selection != null) {
					getSelectionManager().setSelection(selection, fOutlinePage);
				}
			}
		}
	}

	/**
	 * Listener on SSE's source editor's selections that converts DOM
	 * selections into xsd selections and notifies XSD selection manager
	 */
	private class SourceEditorSelectionListener implements ISelectionChangedListener {
		/**
		 * Determines XSD node based on object (DOM node)
		 * 
		 * @param object
		 * @return
		 */
		private Object getXSDNode(Object object) {
			// get the element node
			Element element = null;
			if (object instanceof Node) {
				Node node = (Node) object;
				if (node != null) {
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						element = (Element) node;
					}
					else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
						element = ((Attr) node).getOwnerElement();
					}
				}
			}
			Object o = element;
			if (element != null) {
				Object modelObject = getXSDSchema().getCorrespondingComponent(element);
				if (modelObject != null) {
					o = modelObject;
				}
			}
			return o;
		}

		public void selectionChanged(SelectionChangedEvent event) {
			ISelection selection = event.getSelection();
			if (selection instanceof IStructuredSelection) {
				List xsdSelections = new ArrayList();
				for (Iterator i = ((IStructuredSelection) selection).iterator(); i.hasNext();) {
					Object domNode = i.next();
					Object xsdNode = getXSDNode(domNode);
					if (xsdNode != null) {
						xsdSelections.add(xsdNode);
					}
				}

				if (!xsdSelections.isEmpty()) {
					StructuredSelection xsdSelection = new StructuredSelection(xsdSelections);
					getSelectionManager().setSelection(xsdSelection, getTextEditor().getSelectionProvider());
				}
			}
		}
	}

	/**
	 * Listener on XSD's selection manager's selections that converts XSD
	 * selections into DOM selections and notifies SSE's selection provider
	 */
	private class XSDSelectionManagerSelectionListener implements ISelectionChangedListener {
		/**
		 * Determines DOM node based on object (xsd node)
		 * 
		 * @param object
		 * @return
		 */
		private Object getObjectForOtherModel(Object object) {
			Node node = null;

			if (object instanceof Node) {
				node = (Node) object;
			}
			else if (object instanceof XSDComponent) {
				node = ((XSDComponent) object).getElement();
			}
			else if (object instanceof CategoryAdapter) {
				node = ((CategoryAdapter) object).getXSDSchema().getElement();
			}

			// the text editor can only accept sed nodes!
			//
			if (!(node instanceof IDOMNode)) {
				node = null;
			}
			return node;
		}

		public void selectionChanged(SelectionChangedEvent event) {
			// do not fire selection in source editor if selection event came
			// from source editor
			if (event.getSource() != getTextEditor().getSelectionProvider()) {
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					List otherModelObjectList = new ArrayList();
					for (Iterator i = ((IStructuredSelection) selection).iterator(); i.hasNext();) {
						Object modelObject = i.next();
						Object otherModelObject = getObjectForOtherModel(modelObject);
						if (otherModelObject != null) {
							otherModelObjectList.add(otherModelObject);
						}
					}
					if (!otherModelObjectList.isEmpty()) {
						StructuredSelection nodeSelection = new StructuredSelection(otherModelObjectList);
						getTextEditor().getSelectionProvider().setSelection(nodeSelection);
					}
				}
			}
		}
	}

	public XSDEditor() {
		super();
		xsdSelectionManager = new XSDSelectionManager();
	}

	InternalPartListener partListener = new InternalPartListener(this);

	// show outline view - defect 266116
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		super.init(site, editorInput);
		IWorkbenchWindow dw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = dw.getActivePage();
		getSite().getPage().addPartListener(partListener);
		try {
			if (page != null) {
				// page.showView("org.eclipse.ui.views.ContentOutline");
				page.showView("org.eclipse.ui.views.PropertySheet");
			}
		}
		catch (PartInitException e) {
			// e.printStackTrace();
		}
	}

	// For team support
	// protected PropertyDirtyChangeListener propertyChangeListener;

	/**
	 * Creates the pages of this multi-page editor.
	 * <p>
	 * Subclasses of <code>MultiPageEditor</code> must implement this
	 * method.
	 * </p>
	 */
	protected void createPages() {
		try {
			if (!loadFile())
				return;

			xsdModelAdapterFactory = XSDModelAdapterFactoryImpl.getInstance();
			adapterFactoryLabelProvider = new XSDAdapterFactoryLabelProvider(xsdModelAdapterFactory);

			// source page MUST be created before design page, now
			createSourcePage();

			addSourcePage();
			buildXSDModel();

			// comment this line out to hide the graph page
			// 
			createAndAddGraphPage();

			int pageIndexToShow = getDefaultPageTypeIndex();
			setActivePage(pageIndexToShow);

			openOnSelectionHelper = new OpenOnSelectionHelper(textEditor, getXSDSchema());
			// added selection listeners after setting selection to avoid
			// navigation exception
			ISelectionProvider provider = getTextEditor().getSelectionProvider();
			fSourceEditorSelectionListener = new SourceEditorSelectionListener();
			if (provider instanceof IPostSelectionProvider) {
				((IPostSelectionProvider) provider).addPostSelectionChangedListener(fSourceEditorSelectionListener);
			}
			else {
				provider.addSelectionChangedListener(fSourceEditorSelectionListener);
			}
			fXSDSelectionListener = new XSDSelectionManagerSelectionListener();
			getSelectionManager().addSelectionChangedListener(fXSDSelectionListener);
			
			addCommandStackListener();

			XSDEditorPlugin.getPlugin().getPreferenceStore().addPropertyChangeListener(preferenceStoreListener);
		}
		catch (PartInitException e) {
			// log for now, unless we find reason not to
			Logger.log(Logger.INFO, e.getMessage());
		}
	}


	public void buildXSDModel() {
		try {
			Document document = ((IDOMModel) getModel()).getDocument();

			boolean schemaNodeExists = document.getElementsByTagNameNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "schema").getLength() == 1;

			if (document.getChildNodes().getLength() == 0 || !schemaNodeExists) {
				createDefaultSchemaNode(document);
			}

			if (document instanceof INodeNotifier) {
				INodeNotifier notifier = (INodeNotifier) document;
				schemalNodeAdapter = (XSDModelAdapter) notifier.getAdapterFor(XSDModelAdapter.class);
				if (schemalNodeAdapter == null) {
					schemalNodeAdapter = new XSDModelAdapter();
					notifier.addAdapter(schemalNodeAdapter);
					schemalNodeAdapter.createSchema(document.getDocumentElement());
				}
			}
		}
		catch (Exception e) {
			// XSDEditorPlugin.getPlugin().getMsgLogger().write("Failed to
			// create Model");
			// XSDEditorPlugin.getPlugin().getMsgLogger().write(e);
			// e.printStackTrace();
		}



		// XSDResourceFactoryImpl.validate(xsdSchema,
		// input.getFile().getContents(true));
	}

	public String[] getPropertyCategories() {
		return new String[]{"general", "namespace", "other", "attributes", "documentation", "facets"}; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySheetPageContributor#getContributorId()
	 */
	public String getContributorId() {
		return "org.eclipse.wst.xsd.ui.internal.XSDEditor";
		// return getSite().getId();
	}

	protected CommandStackListener commandStackListener;

	protected void addCommandStackListener() {
		if (commandStackListener == null) {
			IStructuredTextUndoManager undoManager = getModel().getUndoManager();
			commandStackListener = new CommandStackListener() {
				/**
				 * @see org.eclipse.emf.common.command.CommandStackListener#commandStackChanged(EventObject)
				 */
				public void commandStackChanged(EventObject event) {
					Object obj = event.getSource();
					if (obj instanceof BasicCommandStack) {
						BasicCommandStack stack = (BasicCommandStack) obj;
						Command recentCommand = stack.getMostRecentCommand();
						Command redoCommand = stack.getRedoCommand();
						Command undoCommand = stack.getUndoCommand();
						if (recentCommand == redoCommand) {
							// there must have been an undo reset info tasks
							resetInformationTasks();
						}
					}
				}
			};

			// TODO WTP Port
			// undoManager.getCommandStack().addCommandStackListener(commandStackListener);

		}
	}

	protected void pageChange(int arg) {
		super.pageChange(arg);
	}

	protected void removeCommandStackListener() {
		if (commandStackListener != null) {
			IStructuredTextUndoManager undoManager = getModel().getUndoManager();
			// TODO WTP Port
			// undoManager.getCommandStack().removeCommandStackListener(commandStackListener);
		}
	}

	// This is from the IValidateEditEditor interface
	/*
	 * public void undoChange() { StructuredTextUndoManager undoManager =
	 * textEditor.getModel().getUndoManager(); undoManager.undo(); // Make the
	 * editor clean textEditor.getModel().setDirtyState(false); }
	 */

	private class PreferenceStoreListener implements IPropertyChangeListener {
		/**
		 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent event) {
		}
	}

	protected IPropertyChangeListener preferenceStoreListener = new PreferenceStoreListener();

	protected int getDefaultPageTypeIndex() {
		int pageIndex = sourcePageIndex;

		if (XSDEditorPlugin.getPlugin().getDefaultPage().equals(XSDEditorPlugin.GRAPH_PAGE)) {
			if (graphPageIndex != -1)
				pageIndex = graphPageIndex;
		}

		return pageIndex;
	}

	int currentPage = -1;

	public String getCurrentPageType() {
		// should update pref. for valid pages
		if (getActivePage() != -1) {
			currentPage = getActivePage();
		}
		if (currentPage == graphPageIndex) {
			return XSDEditorPlugin.GRAPH_PAGE;
		}
		else {
			return XSDEditorPlugin.SOURCE_PAGE;
		}
	}

	public Object getActivePart() {
		return getSite().getWorkbenchWindow().getActivePage().getActivePart();
	}

	public void dispose() {
		// some things in the configuration need to clean
		// up after themselves
		if (fOutlinePage != null) {
			if (fOutlinePage instanceof ConfigurableContentOutlinePage && fOutlineListener != null) {
				((ConfigurableContentOutlinePage) fOutlinePage).removeDoubleClickListener(fOutlineListener);
			}
			if (fOutlineListener != null) {
				fOutlinePage.removeSelectionChangedListener(fOutlineListener);
			}
		}
		ISelectionProvider provider = getTextEditor().getSelectionProvider();
		if (provider instanceof IPostSelectionProvider) {
			((IPostSelectionProvider) provider).removePostSelectionChangedListener(fSourceEditorSelectionListener);
		}
		else {
			provider.removeSelectionChangedListener(fSourceEditorSelectionListener);
		}
		getSelectionManager().removeSelectionChangedListener(fXSDSelectionListener);
		
		// propertyChangeListener.dispose();
		removeCommandStackListener();

		XSDEditorPlugin.getPlugin().setDefaultPage(getCurrentPageType());
		XSDEditorPlugin.getPlugin().getPreferenceStore().removePropertyChangeListener(preferenceStoreListener);

		getSite().getPage().removePartListener(partListener);

		// KB: Temporary solution for bug 99468
		IStructuredModel myModel = textEditor.getModel();
		if (myModel != null && myModel instanceof IStructuredDocumentListener)
			myModel.getStructuredDocument().removeDocumentChangingListener((IStructuredDocumentListener) myModel);

		textEditor = null;
		resourceFile = null;
		xsdSelectionManager = null;
		schemalNodeAdapter = null;
		result = null;
		partListener = null;
		commandStackListener = null;
		preferenceStoreListener = null;
		openOnSelectionHelper = null;
		graphViewer = null;

		super.dispose();

		// release the schema model
		//
		if (schemalNodeAdapter != null) {
			schemalNodeAdapter.clear();
			schemalNodeAdapter = null;
		}
	}

	protected boolean loadFile() {
		Object input = getEditorInput();

		if (input instanceof IFileEditorInput) {
			resourceFile = ((IFileEditorInput) input).getFile();
		}
		else if (input instanceof ILocationProvider) {
			IPath path = ((ILocationProvider) input).getPath(input);
			String ext = path.getFileExtension();
			if (ext != null && ext.equals("xsd")) {
				return true;
			}
			return false;
		}
		else {
			return false;
		}
		return true;
	}

	/**
	 * Method openOnGlobalReference. The comp argument is a resolved xsd
	 * schema object from another file. This is created and called from
	 * another schema model to allow F3 navigation to open a new editor and
	 * choose the referenced object within that editor context
	 * 
	 * @param comp
	 */
	public void openOnGlobalReference(XSDConcreteComponent comp) {
		openOnSelectionHelper.openOnGlobalReference(comp);
	}

	protected OpenOnSelectionHelper openOnSelectionHelper;

	public OpenOnSelectionHelper getOpenOnSelectionHelper() {
		return openOnSelectionHelper;
	}

	/**
	 * @see org.eclipse.wst.xsd.ui.internal.XSDMultiPageEditorPart#createTextEditor()
	 */
	protected StructuredTextEditor createTextEditor() {
		textEditor = new StructuredTextEditor();
		return textEditor;
	}

	/*
	 * @see StructuredTextMultiPageEditorPart#createSourcePage()
	 */
	protected void createSourcePage() throws PartInitException {
		super.createSourcePage();
	}

	int sourcePageIndex = -1;

	/**
	 * Adds the source page of the multi-page editor.
	 */
	protected void addSourcePage() throws PartInitException {

		sourcePageIndex = addPage(textEditor, getEditorInput());
		setPageText(sourcePageIndex, XSDEditorPlugin.getXSDString("_UI_TAB_SOURCE"));

		// the update's critical, to get viewer selection manager and
		// highlighting to work
		textEditor.update();
		firePropertyChange(PROP_TITLE);
	}

	int graphPageIndex = -1;
	XSDGraphViewer graphViewer;

	/**
	 * Creates the graph page and adds it to the multi-page editor.
	 */
	protected void createAndAddGraphPage() throws PartInitException {
		graphViewer = new XSDGraphViewer(this);
		graphViewer.setSchema(getXSDSchema());
		Control graphControl = graphViewer.createControl(getContainer());
		graphPageIndex = addPage(graphControl);
		setPageText(graphPageIndex, XSDEditorPlugin.getXSDString("_UI_TAB_GRAPH"));

		// graphViewer.setViewerSelectionManager(textEditor.getViewerSelectionManager());
		graphViewer.setSelectionManager(getSelectionManager());

		// this forces the editor to initially select the top level schema
		// object
		//
		if (getXSDSchema() != null) {
			getSelectionManager().setSelection(new StructuredSelection(getXSDSchema()));
		}
	}

	protected XSDModelAdapterFactoryImpl xsdModelAdapterFactory;
	protected XSDAdapterFactoryLabelProvider adapterFactoryLabelProvider;
	private IPropertySheetPage fPropertySheetPage;
	private IContentOutlinePage fOutlinePage;
	private OutlineTreeSelectionChangeListener fOutlineListener;
	private SourceEditorSelectionListener fSourceEditorSelectionListener;
	private XSDSelectionManagerSelectionListener fXSDSelectionListener;

	/*
	 * @see IAdaptable#getAdapter(Class)
	 */
	public Object getAdapter(Class key) {
		Object result = null;
		if (key == ISelectionProvider.class) {
			result = xsdSelectionManager;
		}
        else if (IPropertySheetPage.class.equals(key)) {
			fPropertySheetPage = new XSDTabbedPropertySheetPage(this);

			((XSDTabbedPropertySheetPage) fPropertySheetPage).setXSDModelAdapterFactory(xsdModelAdapterFactory);
			((XSDTabbedPropertySheetPage) fPropertySheetPage).setSelectionManager(getSelectionManager());
			((XSDTabbedPropertySheetPage) fPropertySheetPage).setXSDSchema(getXSDSchema());

			return fPropertySheetPage;
		}
		else if (IContentOutlinePage.class.equals(key)) {
			if (fOutlinePage == null || fOutlinePage.getControl() == null || fOutlinePage.getControl().isDisposed()) {
				IContentOutlinePage page = (IContentOutlinePage) super.getAdapter(key);
				if (page != null) {
					fOutlineListener = new OutlineTreeSelectionChangeListener();
					page.addSelectionChangedListener(fOutlineListener);
					if (page instanceof ConfigurableContentOutlinePage) {
						((ConfigurableContentOutlinePage) page).addDoubleClickListener(fOutlineListener);
					}
				}
				fOutlinePage = page;
				// XSDContentOutlinePage outlinePage = new
				// XSDContentOutlinePage(this);
				// XSDContentProvider xsdContentProvider = new
				// XSDContentProvider(xsdModelAdapterFactory);
				// xsdContentProvider.setXSDSchema(getXSDSchema());
				// outlinePage.setContentProvider(xsdContentProvider);
				// outlinePage.setLabelProvider(adapterFactoryLabelProvider);
				// outlinePage.setModel(getXSDSchema().getDocument());
				//
				// fOutlinePage = outlinePage;
			}
			return fOutlinePage;
		}
		else {
			result = super.getAdapter(key);
		}
		return result;
	}


	public XSDModelAdapterFactoryImpl getXSDModelAdapterFactory() {
		return xsdModelAdapterFactory;
	}

	public XSDAdapterFactoryLabelProvider getLabelProvider() {
		return adapterFactoryLabelProvider;
	}


	public XSDSelectionManager getSelectionManager() {
		return xsdSelectionManager;
	}

	/**
	 * @see org.eclipse.wst.xsd.ui.internal.XSDMultiPageEditorPart#doSaveAs()
	 */
	public void doSaveAs() {
		super.doSaveAs();
	}

	public void doSave(org.eclipse.core.runtime.IProgressMonitor monitor) {
		super.doSave(monitor);
	}

	public void reparseSchema() {
		// TODO cs : Are there no better ways to make the model
		// reload it's dependencies? This seems rather extreme.
		//
		Document document = ((IDOMModel) getModel()).getDocument();
		if (schemalNodeAdapter != null) {
			schemalNodeAdapter.createSchema(document.getDocumentElement());
		}
	}

	/**
	 * Returns the xsdSchema.
	 * 
	 * @return XSDSchema
	 */
	public XSDSchema getXSDSchema() {
		return schemalNodeAdapter != null ? schemalNodeAdapter.getSchema() : null;
	}


	/**
	 * Returns the resourceFile.
	 * 
	 * @return IFile
	 */
	public IFile getFileResource() {
		return resourceFile;
	}

	/**
	 * Get the IDocument from the text viewer
	 */
	public IDocument getEditorIDocument() {
		IDocument document = textEditor.getTextViewer().getDocument();
		return document;
	}

	/**
	 * Create ref integrity tasks in task list
	 */
	public void createTasksInTaskList(ArrayList messages) {
		// DisplayErrorInTaskList tasks = new
		// DisplayErrorInTaskList(getEditorIDocument(), getFileResource(),
		// messages);
		// tasks.run();
	}

	public void resetInformationTasks() {
		// DisplayErrorInTaskList.removeInfoMarkers(getFileResource());
	}

	public XSDGraphViewer getGraphViewer() {
		return graphViewer;
	}

	public IEditorPart getActiveEditorPage() {
		return getActiveEditor();
	}

	public StructuredTextEditor getTextEditor() {
		return textEditor;
	}

	class InternalPartListener implements IPartListener {
		XSDEditor editor;

		public InternalPartListener(XSDEditor editor) {
			this.editor = editor;
		}

		public void partActivated(IWorkbenchPart part) {
			if (part == editor) {
				ISelection selection = getSelectionManager().getSelection();
				if (selection != null) {
					if (getCurrentPageType().equals(XSDEditorPlugin.GRAPH_PAGE)) {
						getSelectionManager().selectionChanged(new SelectionChangedEvent(editor.getGraphViewer().getComponentViewer(), selection));
					}
					else if (getCurrentPageType().equals(XSDEditorPlugin.SOURCE_PAGE)) {
						getSelectionManager().setSelection(selection);
					}
				}
			}
		}

		public void partBroughtToTop(IWorkbenchPart part) {
		}

		public void partClosed(IWorkbenchPart part) {
		}

		public void partDeactivated(IWorkbenchPart part) {
		}

		public void partOpened(IWorkbenchPart part) {
		}
	}


	/**
	 * Method createDefaultSchemaNode. Should only be called to insert a
	 * schema node into an empty document
	 */
	public void createDefaultSchemaNode(Document document) {
		if (document.getChildNodes().getLength() == 0) {
			// if it is a completely empty file, then add the encoding and
			// version processing instruction
			// TODO String encoding = EncodingHelper.getDefaultEncodingTag();
			String encoding = "UTF-8";
			ProcessingInstruction instr = document.createProcessingInstruction("xml", "version=\"1.0\" encoding=\"" + encoding + "\"");
			document.appendChild(instr);
		}

		// Create a default schema tag now

		// String defaultPrefixForTargetNamespace =
		// getFileResource().getProjectRelativePath().removeFileExtension().lastSegment();
		String defaultPrefixForTargetNamespace = "tns";
		String prefixForSchemaNamespace = "";
		String schemaNamespaceAttribute = "xmlns";
		if (XSDEditorPlugin.getPlugin().isQualifyXMLSchemaLanguage()) {
			// Added this if check before disallowing blank prefixes in the
			// preferences...
			// Can take this out. See also NewXSDWizard
			if (XSDEditorPlugin.getPlugin().getXMLSchemaPrefix().trim().length() > 0) {
				prefixForSchemaNamespace = XSDEditorPlugin.getPlugin().getXMLSchemaPrefix() + ":";
				schemaNamespaceAttribute += ":" + XSDEditorPlugin.getPlugin().getXMLSchemaPrefix();
			}
		}

		document.appendChild(document.createTextNode("\n"));
		Element element = document.createElement(prefixForSchemaNamespace + XSDConstants.SCHEMA_ELEMENT_TAG);

		element.setAttribute(schemaNamespaceAttribute, "http://www.w3.org/2001/XMLSchema");

		String defaultTargetURI = XSDEditorPlugin.getPlugin().getXMLSchemaTargetNamespace();
		element.setAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE, defaultTargetURI);
		element.setAttribute("xmlns:" + defaultPrefixForTargetNamespace, defaultTargetURI);

		document.appendChild(element);
	}
}
