/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jst.jsp.ui.tests;

import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.wst.sse.core.internal.util.DocumentInputStream;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * @author nitin
 * 
 */
public class JSPTranslationEditorInput implements IStorageEditorInput {
	private class JSPTranslationStorage implements IStorage {
		/**
		 * 
		 */
		public JSPTranslationStorage(IDOMModel jspModel) {
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
			return new DocumentInputStream(getTranslationAdapter().getJSPTranslation().getJavaDocument());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IStorage#getFullPath()
		 */
		public IPath getFullPath() {
			return new Path(getTranslationAdapter().getJSPTranslation().getJavaPath());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IStorage#getName()
		 */
		public String getName() {
//			return getTranslationAdapter().getJSPTranslation().getCompilationUnit().getElementName();
			return new Path(fModel.getBaseLocation()).lastSegment() + ".java";
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

	IDOMModel fModel;

	private JSPTranslationStorage fStorage;

	/**
	 * 
	 */
	public JSPTranslationEditorInput(IDOMModel model) {
		fStorage = new JSPTranslationStorage(model);
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

	JSPTranslationAdapter getTranslationAdapter() {
		JSPTranslationAdapter adapter = (JSPTranslationAdapter) fModel.getDocument().getAdapterFor(IJSPTranslation.class);
		return adapter;
	}


}
