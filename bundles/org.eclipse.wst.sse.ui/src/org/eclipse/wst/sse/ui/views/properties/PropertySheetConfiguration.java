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
package org.eclipse.wst.sse.ui.views.properties;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.wst.sse.ui.extension.IExtendedConfiguration;


/**
 * Configuration class for Property Sheet Pages.  Not finalized.
 * @author Nitin Dahyabhai
 */

public class PropertySheetConfiguration implements IExtendedConfiguration {

	private class NullPropertySource implements IPropertySource {
		private final IPropertyDescriptor[] descriptors = new IPropertyDescriptor[0];

		/* (non-Javadoc)
		 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
		 */
		public Object getEditableValue() {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
		 */
		public IPropertyDescriptor[] getPropertyDescriptors() {
			return descriptors;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
		 */
		public Object getPropertyValue(Object id) {
			return ""; //$NON-NLS-1$
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
		 */
		public boolean isPropertySet(Object id) {
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
		 */
		public void resetPropertyValue(Object id) {
			// do nothing
		}

		/* (non-Javadoc)
		 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
		 */
		public void setPropertyValue(Object id, Object value) {
			// do nothing
		}
	}

	private class NullPropertySourceProvider implements IPropertySourceProvider {
		private IPropertySource fNullPropertySource = null;

		/* (non-Javadoc)
		 * @see org.eclipse.ui.views.properties.IPropertySourceProvider#getPropertySource(java.lang.Object)
		 */
		public IPropertySource getPropertySource(Object object) {
			if (fNullPropertySource == null)
				fNullPropertySource = new NullPropertySource();
			return fNullPropertySource;
		}
	}

	public static final String ID = "propertysheetconfiguration"; //$NON-NLS-1$

	private String fDeclaringID;

	private IEditorPart fEditor;
	protected IPropertySourceProvider fPropertySourceProvider = null;

	public PropertySheetConfiguration() {
		super();
	}

	public void addContributions(IMenuManager menuManager, IToolBarManager toolBarManager, IStatusLineManager statusLineManager) {
		// do nothing
	}

	/**
	 * @return
	 */
	protected IPropertySourceProvider createPropertySourceProvider() {
		return new NullPropertySourceProvider();
	}

	/**
	 * @return Returns the declaringID.
	 */
	public String getDeclaringID() {
		return fDeclaringID;
	}

	/**
	 * @return Returns the editor.
	 */
	public IEditorPart getEditor() {
		return fEditor;
	}

	/**
	 * Returns the correct IPropertySourceProvider
	 */
	public IPropertySourceProvider getPropertySourceProvider() {
		if (fPropertySourceProvider == null)
			fPropertySourceProvider = createPropertySourceProvider();
		return fPropertySourceProvider;
	}

	/**
	 * Allows for filteration of selection before being sent to the viewer
	 * @param selectingPart - may be null
	 * @param selection
	 * @return
	 */
	public ISelection getSelection(IWorkbenchPart selectingPart, ISelection selection) {
		return selection;
	}

	public void removeContributions(IMenuManager menuManager, IToolBarManager toolBarManager, IStatusLineManager statusLineManager) {
		// do nothing
	}

	/* (non-Javadoc)
	 */
	public void setDeclaringID(String declaringID) {
		fDeclaringID = declaringID;
	}

	/**
	 * @param editor The editor to set.
	 */
	public void setEditor(IEditorPart editor) {
		fEditor = editor;
	}

	public void unconfigure() {
		// do nothing
	}
}
