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

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.sse.ui.extensions.breakpoint.IExtendedStorageEditorInput;


/**
 * 
 * @author nitin
 * 
 * A view to assist in testing out ExtendedStorageEditorInput handling.
 * In-progress.
 */
public class ExtendedStorageEditorInputView extends ViewPart {

	class AddInputAction extends Action {
		public AddInputAction() {
			super("Add");
		}

		public void run() {
			super.run();
			FileDialog dlg = new FileDialog(getListViewer().getControl().getShell());
			String fileName = dlg.open();
			if (fileName != null) {
				fInputs.add(new FileStorageEditorInput(new File(fileName)));
				getListViewer().refresh(true);
			}
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
			} catch (FileNotFoundException e) {
				contents = new ByteArrayInputStream(new byte[0]);
			} catch (IOException e) {
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
			return new Path(fFile.getAbsolutePath());
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

	class FileStorageEditorInputLabelProvider extends LabelProvider {
		public String getText(Object element) {
			String text = super.getText(element);
			Assert.isTrue(element instanceof FileStorageEditorInput);
			try {
				text = ((FileStorageEditorInput) element).getStorage().getFullPath().toString();
				if (((FileStorageEditorInput) element).isDirty()) {
					text = "*" + text;
				}
			} catch (CoreException e) {
			}
			return text;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object element) {
			return null;
		}
	}

	class FileStorageEditorInput implements IExtendedStorageEditorInput {
		List fElementStateListeners = new Vector(0);
		boolean fIsDirty = false;
		FileBackedStorage fStorage = null;

		File getFile() {
			return fStorage.fFile;
		}

		FileStorageEditorInput(File file) {
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
				((IElementStateListener) fElementStateListeners.get(i)).elementContentAboutToBeReplaced(FileStorageEditorInput.this);
			}
		}

		void elementContentReplaced() {
			for (int i = 0; i < fElementStateListeners.size(); i++) {
				((IElementStateListener) fElementStateListeners.get(i)).elementContentReplaced(FileStorageEditorInput.this);
			}
		}

		void elementDeleted() {
			for (int i = 0; i < fElementStateListeners.size(); i++) {
				((IElementStateListener) fElementStateListeners.get(i)).elementDeleted(FileStorageEditorInput.this);
			}
		}

		void elementDirtyStateChanged(boolean dirty) {
			setDirty(dirty);
			for (int i = 0; i < fElementStateListeners.size(); i++) {
				((IElementStateListener) fElementStateListeners.get(i)).elementDirtyStateChanged(FileStorageEditorInput.this, dirty);
			}
		}

		void elementMoved(Object oldElement, Object newElement) {
			for (int i = 0; i < fElementStateListeners.size(); i++) {
				((IElementStateListener) fElementStateListeners.get(i)).elementMoved(FileStorageEditorInput.this, FileStorageEditorInput.this);
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
			return fStorage.getFullPath().toString();
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

	class InputChangeDirtyStateAction extends Action {
		public InputChangeDirtyStateAction() {
			super("Toggle dirty flag");
		}

		public void run() {
			super.run();
			FileStorageEditorInput[] inputs = getSelectedInputs();
			for (int i = 0; i < inputs.length; i++) {
				inputs[i].elementDirtyStateChanged(!inputs[i].isDirty());
			}
			getListViewer().refresh(true);
		}

	}

	class InputDeleteAction extends Action {
		public InputDeleteAction() {
			super("Delete Input");
		}

		public void run() {
			super.run();
			FileStorageEditorInput[] inputs = getSelectedInputs();
			for (int i = 0; i < inputs.length; i++) {
				inputs[i].elementDeleted();
			}
			for (int i = 0; i < inputs.length; i++) {
				fInputs.remove(inputs[i]);
			}
			getListViewer().refresh();
		}
	}

	class InputMoveAction extends Action {
		public InputMoveAction() {
			super("Move Input");
		}

		public void run() {
			super.run();
			FileStorageEditorInput[] inputs = getSelectedInputs();
			for (int i = 0; i < inputs.length; i++) {
				inputs[i].elementMoved(inputs[i], inputs[i]);
			}
		}
	}

	class InputOpenAction extends Action {
		public InputOpenAction() {
			super("Open");
		}

		public void run() {
			super.run();
			FileStorageEditorInput[] inputs = getSelectedInputs();
			for (int i = 0; i < inputs.length; i++) {
				try {
					getSite().getWorkbenchWindow().getActivePage().openEditor(inputs[i], getEditorId(inputs[i].getName()));
				} catch (PartInitException e) {
					openError(getSite().getWorkbenchWindow().getActivePage().getWorkbenchWindow().getShell(), "OpenSystemEditorAction.dialogTitle", e.getMessage(), e);
				}
			}
		}
	}

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
		} else {
			// Open a regular error dialog since there is no
			// extra information to display
			MessageDialog.openError(parent, title, message);
		}
	}


	class InputReplaceContentsAction extends Action {
		public InputReplaceContentsAction() {
			super("Replace Input's Contents");
		}

		public void run() {
			super.run();
			FileStorageEditorInput[] inputs = getSelectedInputs();
			for (int i = 0; i < inputs.length; i++) {
				inputs[i].elementContentAboutToBeReplaced();
				inputs[i].elementContentReplaced();
			}
		}
	}

	class RemoveInputAction extends Action {
		public RemoveInputAction() {
			super("Remove");
		}

		public void run() {
			super.run();
			FileStorageEditorInput[] inputs = getSelectedInputs();
			for (int i = 0; i < inputs.length; i++) {
				fInputs.remove(inputs[i]);
			}
			getListViewer().refresh();
		}
	}

	ListViewer fInputList = null;

	List fInputs = new ArrayList(0);

	class DoubleClickListener implements IDoubleClickListener {
		public void doubleClick(DoubleClickEvent event) {
			new InputOpenAction().run();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		fInputList = new ListViewer(parent, SWT.MULTI);
		fInputList.setContentProvider(new ArrayContentProvider());
		fInputList.setLabelProvider(new FileStorageEditorInputLabelProvider());
		fInputList.addDoubleClickListener(new DoubleClickListener());

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
			fInputs.add(new FileStorageEditorInput(new File(paths[i])));
		}

		fInputList.setInput(fInputs);
	}


	String getEditorId(String filename) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IEditorRegistry editorRegistry = workbench.getEditorRegistry();
		IEditorDescriptor descriptor = editorRegistry.getDefaultEditor(filename);
		if (descriptor != null)
			return descriptor.getId();
		return EditorsUI.DEFAULT_TEXT_EDITOR_ID;
	}

	ListViewer getListViewer() {
		return fInputList;
	}

	FileStorageEditorInput[] getSelectedInputs() {
		ISelection selection = getListViewer().getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			if (sel.isEmpty()) {
				return new FileStorageEditorInput[0];
			} else {
				Object[] arr = sel.toArray();
				FileStorageEditorInput[] inputs = new FileStorageEditorInput[arr.length];
				System.arraycopy(arr, 0, inputs, 0, inputs.length);
				return inputs;
			}
		}
		return new FileStorageEditorInput[0];
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
				String path = ((FileStorageEditorInput) fInputs.get(i)).getFile().getCanonicalPath();
				paths.add(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		SSETestsPlugin.getDefault().getPluginPreferences().setValue(getInputsPreferenceName(), StringUtils.pack((String[]) paths.toArray(new String[0])));
		SSETestsPlugin.getDefault().savePluginPreferences();
	}

	/**
	 * @return
	 */
	String getInputsPreferenceName() {
		return "ExtendedStorageEditorInputView:inputs";
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IViewPart#init(org.eclipse.ui.IViewSite)
	 */
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		site.getActionBars().getToolBarManager().add(new AddInputAction());
		site.getActionBars().getToolBarManager().add(new RemoveInputAction());

		site.getActionBars().getMenuManager().add(new InputOpenAction());
		site.getActionBars().getMenuManager().add(new Separator());
		site.getActionBars().getMenuManager().add(new InputMoveAction());
		site.getActionBars().getMenuManager().add(new InputChangeDirtyStateAction());
		site.getActionBars().getMenuManager().add(new InputDeleteAction());
		site.getActionBars().getMenuManager().add(new InputReplaceContentsAction());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		getListViewer().getControl().setFocus();
	}
}