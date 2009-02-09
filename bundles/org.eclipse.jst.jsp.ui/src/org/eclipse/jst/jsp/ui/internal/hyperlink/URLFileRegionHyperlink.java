/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.hyperlink;

import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * Hyperlink for URLs (opens in read-only mode)
 */
class URLFileRegionHyperlink implements IHyperlink {
	// copies of this class exist in:
	// org.eclipse.wst.xml.ui.internal.hyperlink
	// org.eclipse.wst.html.ui.internal.hyperlink
	// org.eclipse.jst.jsp.ui.internal.hyperlink

	static class StorageEditorInput implements IStorageEditorInput {
		IStorage fStorage = null;

		StorageEditorInput(IStorage storage) {
			fStorage = storage;
		}

		public IStorage getStorage() throws CoreException {
			return fStorage;
		}

		public boolean exists() {
			return fStorage != null;
		}

		public boolean equals(Object obj) {
			if (obj instanceof StorageEditorInput) {
				return fStorage.equals(((StorageEditorInput) obj).fStorage);
			}
			return super.equals(obj);
		}

		public ImageDescriptor getImageDescriptor() {
			return null;
		}

		public String getName() {
			return fStorage.getName();
		}

		public IPersistableElement getPersistable() {
			return null;
		}

		public String getToolTipText() {
			return fStorage.getFullPath() != null ? fStorage.getFullPath().toString() : fStorage.getName();
		}

		public Object getAdapter(Class adapter) {
			return null;
		}
	}

	static class URLStorage implements IStorage {
		URL fURL = null;

		URLStorage(URL url) {
			fURL = url;
		}

		public boolean equals(Object obj) {
			if (obj instanceof URLStorage) {
				return fURL.equals(((URLStorage) obj).fURL);
			}
			return super.equals(obj);
		}

		public InputStream getContents() throws CoreException {
			InputStream stream = null;
			try {
				stream = fURL.openStream();
			}
			catch (Exception e) {
				throw new CoreException(new Status(IStatus.ERROR, JSPUIPlugin.getDefault().getBundle().getSymbolicName(), IStatus.ERROR, fURL.toString(), e));
			}
			return stream;
		}

		public IPath getFullPath() {
			return new Path(fURL.toString());
		}

		public String getName() {
			return new Path(fURL.getFile()).lastSegment();
		}

		public boolean isReadOnly() {
			return true;
		}

		public Object getAdapter(Class adapter) {
			return null;
		}

	}

	private IRegion fRegion;
	private URL fURL;
	private int fSearchType;
	private String fSearchName;

	public URLFileRegionHyperlink(IRegion region, int searchType, String searchName, URL url) {
		fRegion = region;
		fURL = url;
		fSearchName = searchName;
		fSearchType = searchType;
	}

	public IRegion getHyperlinkRegion() {
		return fRegion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getTypeLabel()
	 */
	public String getTypeLabel() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		return NLS.bind(JSPUIMessages.Open, fURL.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.hyperlink.IHyperlink#open()
	 */
	public void open() {
		if (fURL != null) {
			IEditorInput input = new StorageEditorInput(new URLStorage(fURL));
			IEditorDescriptor descriptor;
			try {
				descriptor = IDE.getEditorDescriptor(input.getName(), true);
				if (descriptor != null) {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IEditorPart openedEditor = IDE.openEditor(page, input, descriptor.getId(), true);
					IRegion definitionRegion = findDefinitionRegion(new URLStorage(fURL));
					if (definitionRegion != null) {
						openedEditor.getSite().getSelectionProvider().setSelection(new TextSelection(definitionRegion.getOffset(), definitionRegion.getLength()));
					}
				}
			}
			catch (PartInitException e) {
				Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
			}
		}
	}

	/**
	 * @return
	 */
	private IRegion findDefinitionRegion(IStorage storage) {
		IRegion definition = null;
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getModelForRead(storage.getName(), storage.getContents(), null);
		}
		catch (Exception e) {
			Logger.logException(e);
		}
		if (model != null) {
			if (model instanceof IDOMModel) {
				definition = TaglibHyperlinkDetector.findDefinition((IDOMModel) model, fSearchName, fSearchType);
			}
			model.releaseFromRead();
		}
		return definition;
	}
}
