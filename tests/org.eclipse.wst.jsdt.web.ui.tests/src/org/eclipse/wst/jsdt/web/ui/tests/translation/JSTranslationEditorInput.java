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

package org.eclipse.wst.jsdt.web.ui.tests.translation;

import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Document;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.sse.core.internal.util.DocumentInputStream;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * @author nitin
 * 
 */
public class JSTranslationEditorInput implements IStorageEditorInput {
	private class JSTranslationStorage implements IStorage {
		/**
		 * 
		 */
		public JSTranslationStorage(IDOMModel jspModel) {
			fModel = jspModel;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
		 */
		public Object getAdapter(Class adapter) {
			return fModel.getAdapter(adapter);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IStorage#getContents()
		 */
		public InputStream getContents() throws CoreException {
			return new DocumentInputStream(new Document(getTranslationAdapter().getJsTranslation(false).getJsText()));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IStorage#getFullPath()
		 */
		public IPath getFullPath() {
			return new Path(getTranslationAdapter().getJsTranslation(false).getJavaPath());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IStorage#getName()
		 */
		public String getName() {
			return new Path(fModel.getBaseLocation()).lastSegment() + ".js";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IStorage#isReadOnly()
		 */
		public boolean isReadOnly() {
			return false;
		}
	}

	IDOMModel fModel;

	private JSTranslationStorage fStorage;

	/**
	 * 
	 */
	public JSTranslationEditorInput(IDOMModel model) {
		fStorage = new JSTranslationStorage(model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return fModel.getAdapter(adapter);
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
		return fModel.getBaseLocation();
	}

	JsTranslationAdapter getTranslationAdapter() {
		JsTranslationAdapter adapter = (JsTranslationAdapter) fModel.getDocument().getAdapterFor(IJsTranslation.class);
		return adapter;
	}


}
