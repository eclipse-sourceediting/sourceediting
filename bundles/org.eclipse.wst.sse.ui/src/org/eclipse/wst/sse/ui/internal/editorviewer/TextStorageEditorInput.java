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

import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.wst.sse.core.util.DocumentInputStream;

/**
 * TODO remove in C5 or earlier
 * @deprecated removing in C5 or earlier
 */
public class TextStorageEditorInput implements IStorageEditorInput {

	private class TextStorage implements IStorage {
		public Object getAdapter(Class adapter) {
			return null;
		}

		public InputStream getContents() throws CoreException {
			return new DocumentInputStream(fDocument);
		}

		public IPath getFullPath() {
			return fPath;
		}

		public String getName() {
			return getFullPath().toString();
		}

		public boolean isReadOnly() {
			return false;
		}
	}

	protected IDocument fDocument;
	protected IPath fPath;
	protected TextStorage fStorage;


	public TextStorageEditorInput(String path) {
		super();
		fPath = new Path(path);
		fStorage = new TextStorage();
		fDocument = new Document();
	}

	public boolean exists() {
		return true;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public IDocument getDocument() {
		return fDocument;
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

	public IStorage getStorage() throws CoreException {
		return fStorage;
	}

	public String getToolTipText() {
		return getName();
	}
}
