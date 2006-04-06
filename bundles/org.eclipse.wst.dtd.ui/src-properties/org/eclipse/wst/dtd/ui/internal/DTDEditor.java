/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
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

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySheetPageContributor;
import org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetPage;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * A DTD Editor subclass StructuredTextEditor, required to supply a complete
 * replacement for the property sheet page. When a better solution is found,
 * this class will be removed.
 */
public class DTDEditor extends StructuredTextEditor {
	private class DTDPropertySheetPageContributor implements ITabbedPropertySheetPageContributor {
		/**
		 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySheetPageContributor#getContributorId()
		 */
		public String getContributorId() {
			return getEditorSite().getId();
		}

		/**
		 * @return String[]
		 */
		public String[] getPropertyCategories() {
			return new String[]{DTDPropertiesMessages.DTDEditor_0, DTDPropertiesMessages.DTDEditor_1, DTDPropertiesMessages.DTDEditor_2, DTDPropertiesMessages.DTDEditor_3};
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
				fPropertySheetPage = new TabbedPropertySheetPage(new DTDPropertySheetPageContributor());
			}
			return fPropertySheetPage;
		}
		return super.getAdapter(required);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite,
	 *      org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		super.init(site, editorInput);

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