/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.ui.tests;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.breakpoint.IExtendedStorageEditorInput;


/**
 * @author nitin
 * 
 * A view to assist in testing out StructuredTextEditor's IEditorInput
 * handling.
 * 
 * Permanently in-progress.
 */
public class ExtendedStorageEditorInputView extends ViewPart {
	/**
	 * Adds an IFileEditorInput to the list of inputs with which to test
	 */
	class AddFileInputAction extends Action {
		public AddFileInputAction() {
			super("Add FileEditorInput");
		}

		public void run() {
			super.run();
			ResourceSelectionDialog dlg = new ResourceSelectionDialog(fInputList.getControl().getShell(), ResourcesPlugin.getWorkspace().getRoot(), "Choose");
			int retval = dlg.open();
			if (retval == Window.OK) {
				Object[] files = dlg.getResult();
				for (int i = 0; i < files.length; i++) {
					fInputs.add(new FileEditorInput((IFile) files[i]));
				}
				fInputList.refresh(true);
			}
		}
	}

	/**
	 * Adds an IStorageEditorInput to the list of inputs with which to test
	 */
	class AddStorageInputAction extends Action {
		public AddStorageInputAction() {
			super("Add StorageEditorInput");
		}

		public void run() {
			super.run();
			FileDialog dlg = new FileDialog(fInputList.getControl().getShell());
			String fileName = dlg.open();
			if (fileName != null) {
				fInputs.add(new FileBasedStorageEditorInput(new File(fileName)));
				fInputList.refresh(true);
			}
		}
	}

	/**
	 * Handler for double-click 
	 */
	class DoubleClickListener implements IDoubleClickListener {
		public void doubleClick(DoubleClickEvent event) {
			new InputOpenAction().run();
		}
	}

	class EditorInputLabelProvider implements ITableLabelProvider {
		ILabelProvider baseProvider = new WorkbenchLabelProvider();

		public void addListener(ILabelProviderListener listener) {
			// not implemented
		}

		public void dispose() {
			// no need
		}

		public Image getColumnImage(Object element, int columnIndex) {
			if (element instanceof IFileEditorInput && columnIndex == 0) {
				return baseProvider.getImage(((IFileEditorInput) element).getFile());
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			IEditorInput input = (IEditorInput) element;
			String text = null;
			switch (columnIndex) {
				case 0 :
					try {
						if (element instanceof FileBasedStorageEditorInput) {
							IPath fullpath = ((FileBasedStorageEditorInput) element).getStorage().getFullPath();
							text = fullpath != null ? fullpath.toString() : ((FileBasedStorageEditorInput) element).getName();
							if (((FileBasedStorageEditorInput) element).isDirty()) {
								text = "*" + text;
							}
						}
						else if (element instanceof IFileEditorInput) {
							text = ((IFileEditorInput) element).getFile().getFullPath().toString();
						}
					}
					catch (CoreException e) {
						e.printStackTrace();
					}

					break;
				case 1 :
					if (element instanceof FileBasedStorageEditorInput) {
						text = "FileBasedStorageEditorInput";
					}
					else if (element instanceof IFileEditorInput) {
						text = "FileEditorInput";
					}
					else {
						text = input.getClass().getName();
					}
					break;
			}
			if (text == null)
				text = "";
			return text;
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			// not implemented
		}
	}


	class FileBackedStorage implements IStorage {
		File fFile = null;

		FileBackedStorage(File file) {
			super();
			fFile = file;
		}

		boolean exists() {
			return fFile.exists();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
		 */
		public Object getAdapter(Class adapter) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IStorage#getContents()
		 */
		public InputStream getContents() throws CoreException {
			InputStream contents = null;
			try {
				contents = new FileInputStream(fFile);
				ByteBuffer buffer = ByteBuffer.allocate((int) fFile.length());
				byte[] bytes = new byte[2048];
				while (contents.available() > 0) {
					int count = contents.read(bytes);
					buffer.put(bytes, 0, count);
				}
				contents.close();

				contents = new ByteArrayInputStream(buffer.array());
			}
			catch (FileNotFoundException e) {
				contents = new ByteArrayInputStream(new byte[0]);
			}
			catch (IOException e) {
				contents = new ByteArrayInputStream(new byte[0]);
			}
			return contents;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IStorage#getFullPath()
		 */
		public IPath getFullPath() {
			if (provideIStorageFullPath) {
				return new Path(fFile.getAbsolutePath());
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IStorage#getName()
		 */
		public String getName() {
			return fFile.getName();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IStorage#isReadOnly()
		 */
		public boolean isReadOnly() {
			return true;
		}
	}

	class FileBasedStorageEditorInput implements IExtendedStorageEditorInput {
		List fElementStateListeners = new Vector(0);
		boolean fIsDirty = false;
		FileBackedStorage fStorage = null;

		FileBasedStorageEditorInput(File file) {
			fStorage = new FileBackedStorage(file);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.ui.extensions.breakpoint.IExtendedStorageEditorInput#addElementStateListener(org.eclipse.ui.texteditor.IElementStateListener)
		 */
		public void addElementStateListener(IElementStateListener listener) {
			fElementStateListeners.add(listener);
		}

		void elementContentAboutToBeReplaced() {
			for (int i = 0; i < fElementStateListeners.size(); i++) {
				((IElementStateListener) fElementStateListeners.get(i)).elementContentAboutToBeReplaced(FileBasedStorageEditorInput.this);
			}
		}

		void elementContentReplaced() {
			for (int i = 0; i < fElementStateListeners.size(); i++) {
				((IElementStateListener) fElementStateListeners.get(i)).elementContentReplaced(FileBasedStorageEditorInput.this);
			}
		}

		void elementDeleted() {
			for (int i = 0; i < fElementStateListeners.size(); i++) {
				((IElementStateListener) fElementStateListeners.get(i)).elementDeleted(FileBasedStorageEditorInput.this);
			}
		}

		void elementDirtyStateChanged(boolean dirty) {
			setDirty(dirty);
			for (int i = 0; i < fElementStateListeners.size(); i++) {
				((IElementStateListener) fElementStateListeners.get(i)).elementDirtyStateChanged(FileBasedStorageEditorInput.this, dirty);
			}
		}

		void elementMoved(Object oldElement, Object newElement) {
			for (int i = 0; i < fElementStateListeners.size(); i++) {
				((IElementStateListener) fElementStateListeners.get(i)).elementMoved(FileBasedStorageEditorInput.this, FileBasedStorageEditorInput.this);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.IEditorInput#exists()
		 */
		public boolean exists() {
			return fStorage.exists();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
		 */
		public Object getAdapter(Class adapter) {
			return null;
		}

		File getFile() {
			return fStorage.fFile;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
		 */
		public ImageDescriptor getImageDescriptor() {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.IEditorInput#getName()
		 */
		public String getName() {
			return fStorage.getName();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.IEditorInput#getPersistable()
		 */
		public IPersistableElement getPersistable() {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.IStorageEditorInput#getStorage()
		 */
		public IStorage getStorage() throws CoreException {
			return fStorage;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.IEditorInput#getToolTipText()
		 */
		public String getToolTipText() {
			return fStorage.getFullPath() != null ? fStorage.getFullPath().toString() : fStorage.getName();
		}

		boolean isDirty() {
			return fIsDirty;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.wst.sse.ui.extensions.breakpoint.IExtendedStorageEditorInput#removeElementStateListener(org.eclipse.ui.texteditor.IElementStateListener)
		 */
		public void removeElementStateListener(IElementStateListener listener) {
			fElementStateListeners.remove(listener);
		}

		void setDirty(boolean dirty) {
			fIsDirty = dirty;
		}
	}

	class InputChangeDirtyStateAction extends Action implements IUpdate {
		public InputChangeDirtyStateAction() {
			super("Toggle dirty flag");
		}

		public void run() {
			super.run();
			IEditorInput[] inputs = getSelectedInputs();
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i] instanceof FileBasedStorageEditorInput) {
					((FileBasedStorageEditorInput) inputs[i]).elementDirtyStateChanged(!((FileBasedStorageEditorInput) inputs[i]).isDirty());
				}
			}
			fInputList.refresh(true);
		}

		public void update() {
			setEnabled(fSelectedElement != null && fSelectedElement instanceof FileBasedStorageEditorInput);
		}
	}

	class InputDeleteAction extends Action implements IUpdate {
		public InputDeleteAction() {
			super("Delete Input");
		}

		public void run() {
			super.run();
			IEditorInput[] inputs = getSelectedInputs();
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i] instanceof FileBasedStorageEditorInput) {
					((FileBasedStorageEditorInput) inputs[i]).elementDeleted();
				}
			}
			for (int i = 0; i < inputs.length; i++) {
				fInputs.remove(inputs[i]);
			}
			fInputList.refresh();
		}

		public void update() {
			setEnabled(fSelectedElement != null && fSelectedElement instanceof FileBasedStorageEditorInput);
		}
	}

	class InputMoveAction extends Action implements IUpdate {
		public InputMoveAction() {
			super("Move Input");
		}

		public void run() {
			super.run();
			IEditorInput[] inputs = getSelectedInputs();
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i] instanceof FileBasedStorageEditorInput) {
					((FileBasedStorageEditorInput) inputs[i]).elementMoved(inputs[i], inputs[i]);
				}
			}
		}

		public void update() {
			setEnabled(fSelectedElement != null && fSelectedElement instanceof FileBasedStorageEditorInput);
		}
	}

	class InputOpenAction extends Action {
		public InputOpenAction() {
			super("Open");
		}

		public void run() {
			super.run();
			IEditorInput[] inputs = getSelectedInputs();
			for (int i = 0; i < inputs.length; i++) {
				try {
					getSite().getWorkbenchWindow().getActivePage().openEditor(inputs[i], getEditorId(inputs[i]));
				}
				catch (PartInitException e) {
					openError(getSite().getWorkbenchWindow().getActivePage().getWorkbenchWindow().getShell(), "OpenSystemEditorAction.dialogTitle", e.getMessage(), e);
				}
			}
		}
	}

	class InputReplaceContentsAction extends Action implements IUpdate {
		public InputReplaceContentsAction() {
			super("Replace Input's Contents");
		}

		public void run() {
			super.run();
			IEditorInput[] inputs = getSelectedInputs();
			for (int i = 0; i < inputs.length; i++) {
				((FileBasedStorageEditorInput) inputs[i]).elementContentAboutToBeReplaced();
				((FileBasedStorageEditorInput) inputs[i]).elementContentReplaced();
			}
		}

		public void update() {
			setEnabled(fSelectedElement != null && fSelectedElement instanceof FileBasedStorageEditorInput);
		}
	}

	class RemoveInputAction extends Action {
		public RemoveInputAction() {
			super("Remove");
		}

		public void run() {
			super.run();
			IEditorInput[] inputs = getSelectedInputs();
			for (int i = 0; i < inputs.length; i++) {
				fInputs.remove(inputs[i]);
			}
			fInputList.refresh();
		}
	}

	class ReuseEditorAction extends Action implements IUpdate {
		public ReuseEditorAction() {
			super("Reuse Editor");
		}

		public void run() {
			super.run();
			IEditorInput[] inputs = getSelectedInputs();
			for (int i = 0; i < inputs.length; i++) {
				IEditorPart editor = getSite().getWorkbenchWindow().getActivePage().getActiveEditor();
				if (editor instanceof IReusableEditor) {
					getSite().getWorkbenchWindow().getActivePage().reuseEditor(((IReusableEditor) editor), inputs[i]);
				}
				else {
					SSEUIPlugin.getDefault().getWorkbench().getDisplay().beep();
				}
			}
		}

		public void update() {
			boolean enable = true;
			try {
				enable = fSelectedElement != null && getSite().getWorkbenchWindow().getActivePage().getActiveEditor() instanceof IReusableEditor;
			}
			catch (Exception e) {
				enable = true;
			}
			setEnabled(enable);
		}
	}


	public static final boolean provideIStorageFullPath = true;

	/**
	 * Open an error style dialog for PartInitException by including any extra
	 * information from the nested CoreException if present.
	 */
	public static void openError(Shell parent, String title, String message, PartInitException exception) {
		// Check for a nested CoreException
		CoreException nestedException = null;
		IStatus status = exception.getStatus();
		if (status != null && status.getException() instanceof CoreException)
			nestedException = (CoreException) status.getException();

		if (nestedException != null) {
			// Open an error dialog and include the extra
			// status information from the nested CoreException
			ErrorDialog.openError(parent, title, message, nestedException.getStatus());
		}
		else {
			// Open a regular error dialog since there is no
			// extra information to display
			MessageDialog.openError(parent, title, message);
		}
	}

	private List actions = null;

	TableViewer fInputList = null;

	List fInputs = new ArrayList(0);

	Object fSelectedElement = null;


	public ExtendedStorageEditorInputView() {
		super();
		actions = new ArrayList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		fInputList = new TableViewer(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		fInputList.setContentProvider(new ArrayContentProvider());
		fInputList.setLabelProvider(new EditorInputLabelProvider());
		fInputList.addDoubleClickListener(new DoubleClickListener());
		fInputList.getTable().setHeaderVisible(true);
		fInputList.getTable().setLinesVisible(true);
		String[] columns = new String[]{"Path", "Type"};
		fInputList.setLabelProvider(new EditorInputLabelProvider());


		TableLayout tlayout = new TableLayout();
		CellEditor[] cellEditors = new CellEditor[5];
		for (int i = 0; i < columns.length; i++) {
			tlayout.addColumnData(new ColumnWeightData(1));
			TableColumn tc = new TableColumn(fInputList.getTable(), SWT.NONE);
			tc.setText(columns[i]);
			tc.setResizable(true);
			tc.setWidth(Display.getCurrent().getBounds().width / 14);
		}
		fInputList.setCellEditors(cellEditors);
		fInputList.setColumnProperties(columns);

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		fInputList.getTable().setLayoutData(gd);

		MenuManager menuManager = new MenuManager("#popup"); //$NON-NLS-1$
		menuManager.setRemoveAllWhenShown(false);

		menuManager.add(new InputOpenAction());
		menuManager.add(new Separator());
		menuManager.add(new InputMoveAction());
		menuManager.add(new InputChangeDirtyStateAction());
		menuManager.add(new InputDeleteAction());
		menuManager.add(new InputReplaceContentsAction());
		menuManager.add(new Separator());
		menuManager.add(new RemoveInputAction());
		Menu menu = menuManager.createContextMenu(fInputList.getControl());
		fInputList.getControl().setMenu(menu);


		SSETestsPlugin.getDefault().getPluginPreferences().setDefault(getInputsPreferenceName(), "");
		String paths[] = StringUtils.unpack(SSETestsPlugin.getDefault().getPluginPreferences().getString(getInputsPreferenceName()));
		for (int i = 0; i < paths.length; i++) {
			if (paths[i].startsWith("S!")) {
				fInputs.add(new FileBasedStorageEditorInput(new File(paths[i].substring(2))));
				menuManager.add(new OpenWithMenu(getSite().getPage(), null));
			}
			else if (paths[i].startsWith("F!")) {
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(paths[i].substring(2)));
				fInputs.add(new FileEditorInput(file));
				menuManager.add(new OpenWithMenu(getSite().getPage(), file));
			}
		}

		fInputList.setInput(fInputs);
		fInputList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = ((IStructuredSelection) event.getSelection());
				fSelectedElement = sel.getFirstElement();
			}
		});
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		List paths = new ArrayList(0);
		for (int i = 0; i < fInputs.size(); i++) {
			try {
				Object input = fInputs.get(i);
				String path = null;
				if (input instanceof FileBasedStorageEditorInput) {
					path = "S!" + ((FileBasedStorageEditorInput) input).getFile().getCanonicalPath();
				}
				else if (input instanceof IFileEditorInput) {
					path = "F!" + ((IFileEditorInput) input).getFile().getFullPath().toString();
				}
				if (path != null) {
					paths.add(path);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		SSETestsPlugin.getDefault().getPluginPreferences().setValue(getInputsPreferenceName(), StringUtils.pack((String[]) paths.toArray(new String[0])));
		SSETestsPlugin.getDefault().savePluginPreferences();
	}

	String getEditorId(IEditorInput input) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IEditorRegistry editorRegistry = workbench.getEditorRegistry();
		IContentType[] types = null;
		String editorID = null;
		if (input instanceof IStorageEditorInput) {
			InputStream inputStream = null;
			try {
				inputStream = ((IStorageEditorInput) input).getStorage().getContents();
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
			try {
				types = Platform.getContentTypeManager().findContentTypesFor(inputStream, input.getName());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		IEditorDescriptor descriptor = editorRegistry.getDefaultEditor(input.getName(), types[0]);
		if (descriptor != null) {
			editorID = descriptor.getId();
		}
		if (editorID == null) {
			editorID = EditorsUI.DEFAULT_TEXT_EDITOR_ID;
		}
		return editorID;
	}

	/**
	 * @return
	 */
	String getInputsPreferenceName() {
		return "ExtendedStorageEditorInputView:inputs";
	}

	IEditorInput[] getSelectedInputs() {
		ISelection selection = fInputList.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			if (sel.isEmpty()) {
				return new IEditorInput[0];
			}
			Object[] arr = sel.toArray();
			IEditorInput[] inputs = new IEditorInput[arr.length];
			System.arraycopy(arr, 0, inputs, 0, inputs.length);
			return inputs;
		}
		return new IEditorInput[0];
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IViewPart#init(org.eclipse.ui.IViewSite)
	 */
	public void init(IViewSite site) throws PartInitException {
		super.init(site);

		site.getActionBars().getToolBarManager().add(rememberAction(new AddStorageInputAction()));
		site.getActionBars().getToolBarManager().add(rememberAction(new AddFileInputAction()));
		site.getActionBars().getToolBarManager().add(rememberAction(new ReuseEditorAction()));
		site.getActionBars().getToolBarManager().add(rememberAction(new RemoveInputAction()));

		site.getActionBars().getMenuManager().add(rememberAction(new InputOpenAction()));
		site.getActionBars().getMenuManager().add(rememberAction(new ReuseEditorAction()));
		site.getActionBars().getMenuManager().add(new Separator());
		site.getActionBars().getMenuManager().add(rememberAction(new InputMoveAction()));
		site.getActionBars().getMenuManager().add(rememberAction(new InputChangeDirtyStateAction()));
		site.getActionBars().getMenuManager().add(rememberAction(new InputDeleteAction()));
		site.getActionBars().getMenuManager().add(rememberAction(new InputReplaceContentsAction()));
	}

	IAction rememberAction(IAction action) {
		actions.add(action);
		return action;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		fInputList.getControl().setFocus();
	}


	protected void updateEnablement() {
		for (int i = 0; i < actions.size(); i++) {
			Object action = actions.get(i);
			if (action instanceof IUpdate) {
				((IUpdate) action).update();
			}
		}
	}
}
