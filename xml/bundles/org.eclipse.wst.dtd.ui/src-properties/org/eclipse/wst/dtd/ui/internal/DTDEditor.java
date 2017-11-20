/*******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.internal;

import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.wst.dtd.ui.internal.preferences.DTDUIPreferenceNames;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * A DTD Editor subclass StructuredTextEditor, required to supply a complete
 * replacement for the property sheet page. When a better solution is found,
 * this class will be removed.
 */
public class DTDEditor extends StructuredTextEditor {
	class DTDPropertySheetPageContributor implements ITabbedPropertySheetPageContributor {
		DTDPropertySheetPageContributor() {
			super();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor#getContributorId()
		 */
		public String getContributorId() {
			return getEditorSite().getId();
		}
	}

	TabbedPropertySheetPage fPropertySheetPage;

	public DTDEditor() {
		super();
	}

	/*
	 * @see IAdaptable#getAdapter(Class)
	 */
	public Object getAdapter(Class required) {
		if (IPropertySheetPage.class.equals(required)) {
			if (fPropertySheetPage == null) {
				fPropertySheetPage = new DTDTabbedPropertySheetPage(new DTDPropertySheetPageContributor());
				/*
				 * Add the property sheet page as a direct post selection
				 * listener so standard cursor navigation triggers a selection
				 * notification. The default tabbed property sheet does not
				 * listen to post selection.
				 */
				((IPostSelectionProvider) getSelectionProvider()).addPostSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						if (fPropertySheetPage != null && !fPropertySheetPage.getControl().isDisposed()) {
							fPropertySheetPage.selectionChanged(DTDEditor.this, getSelectionProvider().getSelection());
						}
					}
				});
			}
			return fPropertySheetPage;
		}
		return super.getAdapter(required);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.StructuredTextEditor#init(org.eclipse.ui.IEditorSite,
	 *      org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		super.init(site, editorInput);

		if (DTDUIPlugin.getDefault().getPreferenceStore().getBoolean(DTDUIPreferenceNames.ACTIVATE_PROPERTIES)) {
			IWorkbenchWindow dw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = dw.getActivePage();
			try {
				if (page != null) {
					page.showView(IPageLayout.ID_PROP_SHEET);
				}
			}
			catch (PartInitException e) {
				Logger.logException(e);
			}
		}
	}
}