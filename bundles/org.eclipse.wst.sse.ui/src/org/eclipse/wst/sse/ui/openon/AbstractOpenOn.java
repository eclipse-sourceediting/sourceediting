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
package org.eclipse.wst.sse.ui.openon;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.util.PathHelper;
import org.eclipse.wst.sse.core.util.URIResolver;
import org.eclipse.wst.sse.ui.Logger;
import org.eclipse.wst.sse.ui.extensions.openon.IOpenOn;
import org.eclipse.wst.sse.ui.internal.openon.ExternalFileEditorInput;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.sse.ui.util.PlatformStatusLineUtil;


/**
 * This action class retrieves the link/file selected by the cursor and
 * attempts to open the link/file in the default editor or web browser
 */
abstract public class AbstractOpenOn implements IOpenOn {
	protected final String FILE_PROTOCOL = "file:/";//$NON-NLS-1$
	private final String HTTP_PROTOCOL = "http://";//$NON-NLS-1$
	protected final String CANNOT_OPEN = ResourceHandler.getString("AbstractOpenOn.0"); //$NON-NLS-1$
	private IDocument fDocument;	// document currention associated with open on

	/**
	 * Opens the appropriate editor for fileString
	 * @param fileString
	 */
	protected void openFileInEditor(String fileString) {
		IEditorPart editor = null;
		if (fileString != null) {
			// open web browser if this is a web address
			String temp = fileString.toLowerCase();
			if (temp.startsWith(HTTP_PROTOCOL)) {
				Program.launch(fileString); // launches web browser/executable associated with uri
				return;
			}
			// chop off the file protocol
			if (temp.startsWith(FILE_PROTOCOL)) {
				fileString = fileString.substring(FILE_PROTOCOL.length());
			}

			// try to locate the file in the workspace and return an IFile if found
			IFile file = getFile(fileString);
			if (file != null) {
				// file exists in workspace
				editor = openFileInEditor(file);
			} else {
				// file does not exist in workspace
				editor = openExternalFile(fileString);
			}
		}
		// no editor was opened
		if (editor == null) {
			openFileFailed();
		}
	}

	/**
	 * Returns an IFile from the given uri if possible, null if cannot find file from uri.
	 * @param fileString file system path
	 * @return returns IFile if fileString exists in the workspace
	 */
	protected IFile getFile(String fileString) {
		if (fileString != null) {
			IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(new Path(fileString));
			for (int i = 0; i < files.length; i++)
				if (files[i].exists())
					return files[i];
		}
		return null;
	}

	/**
	 * Opens the IFile, input in its default editor, if possible, and returns the editor
	 * opened.
	 * Possible reasons for failure: input cannot be found, input does not exist in 
	 * workbench, editor cannot be opened.
	 * 
	 * @return IEditorPart editor opened or null if input == null or does not exist, 
	 * external editor was opened, editor could not be opened
	 */
	protected IEditorPart openFileInEditor(IFile input) {
		if (input != null && input.exists()) {
			try {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				return IDE.openEditor(page, input, true);
			}
			catch (PartInitException pie) {
				Logger.log(Logger.WARNING_DEBUG, pie.getMessage(), pie);
			}
		}
		return null;
	}
	
	/**
	 * Try to open the external file, fileString in its default editor
	 * @param fileString
	 * @return IEditorPart editor opened or null if editor could not be opened
	 */
	protected IEditorPart openExternalFile(String fileString) {
		// file does not exist in workspace so try to open using system editor
		File file = new File(fileString);
		IEditorInput input = new ExternalFileEditorInput(file);
		String editorId = getEditorId(fileString);
		
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			return page.openEditor(input, editorId, true);
		}
		catch (PartInitException pie) {
			Logger.log(Logger.WARNING_DEBUG, pie.getMessage(), pie);
		}
		return null;
	}
	
	/**
	 * Determines the editor associated with the given file name
	 * @param filename
	 * @return editor id of the editor associated with the given file name
	 */
	private String getEditorId(String filename) {
		IWorkbench workbench= PlatformUI.getWorkbench();
		IEditorRegistry editorRegistry= workbench.getEditorRegistry();
		IEditorDescriptor descriptor= editorRegistry.getDefaultEditor(filename);
		if (descriptor != null)
			return descriptor.getId();
		return EditorsUI.DEFAULT_TEXT_EDITOR_ID;
	}

	/**
	 * Notifies user that open on selection action could not successfully open
	 * the selection (writes message on status bar and beeps)
	 */
	protected void openFileFailed() {
		PlatformStatusLineUtil.displayErrorMessage(CANNOT_OPEN);
		PlatformStatusLineUtil.addOneTimeClearListener();
	}

	/**
	 * @deprecated this method has moved up to DefaultOpenOnHTML - TODO remove in C5
	 */
	protected String resolveURI(String uriString) {
		// future_TODO: should use the new common extensible URI resolver when clients start implementing it
		String resolvedURI = uriString;

		if (uriString != null) {
			IStructuredModel sModel = getModelManager().getExistingModelForRead(getDocument());
			if (sModel != null) {
				URIResolver resolver = sModel.getResolver();
				resolvedURI = resolver != null ? resolver.getLocationByURI(uriString, true) : uriString;

				sModel.releaseFromRead();
			}
			// special adjustment for file protocol
			if (uriString.startsWith(FILE_PROTOCOL)) {
				PathHelper.removeLeadingSeparator(resolvedURI);
			}
		}
		return resolvedURI;
	}

	protected IModelManager getModelManager() {
		IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		return plugin.getModelManager();
	}
	
	/**
	 * @deprecated use getOpenRegion(IDocument, int) instead TODO remove in C5
	 */
	public IRegion getOpenOnRegion(ITextViewer viewer, int offset) {
		if (viewer != null) {
			return getOpenOnRegion(viewer.getDocument(), offset);
		}
		return null;
	}

	/**
	 * @deprecated use openOn(IDocument, IRegion) instead TODO remove in C5
	 */
	public void openOn(ITextViewer viewer, IRegion region) {
		if (viewer != null) {
			openOn(viewer.getDocument(), region);
		}
	}

	/**
	 * @deprecated use getDocument() instead TODO remove in C5
	 */
	public ITextViewer getTextViewer() {
		return null;
	}

	/**
	 * @deprecated use setDocument instead TODO remove in C5
	 */
	public void setTextViewer(ITextViewer viewer) {
	}

	/*
	 *  (non-Javadoc)
	 */
	public IRegion getOpenOnRegion(IDocument doc, int offset) {
		IRegion region;
		// set the document for this action
		setDocument(doc);
		region = doGetOpenOnRegion(offset);
		// reset the document back to null for this action
		setDocument(null);
		return region;
	}
	
	/*
	 *  (non-Javadoc)
	 */
	public void openOn(IDocument doc, IRegion region) {
		// set the document for this action
		setDocument(doc);
		// if no region was given this action fails
		if (region == null)
			openFileFailed();
		else
			doOpenOn(region);
		// reset the document back to null for this action
		setDocument(null);
	}
	
	/**
	 * Returns the current document associated with open on
	 * @return IDocument
	 */
	public IDocument getDocument() {
		return fDocument;
	}
	
	/**
	 * Sets current document associated with open on
	 * @param document
	 */
	public void setDocument(IDocument document) {
		fDocument = document;
	}
	
	abstract protected IRegion doGetOpenOnRegion(int offset);

	abstract protected void doOpenOn(IRegion region);
}
