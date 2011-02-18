/*******************************************************************************
 * Copyright (c) 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.internal.properties.section;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.common.ui.internal.viewers.SelectSingleFilePage;
import org.eclipse.wst.dtd.ui.internal.DTDUIPlugin;
import org.eclipse.wst.dtd.ui.internal.editor.DTDEditorPluginImages;

/**
 * Extend the base wizard to select a file from the project or outside the
 * workbench and add error handling
 */
class DTDSelectIncludeFileWizard extends Wizard implements INewWizard {
	DTDSelectSingleFilePage filePage;

	IFile resultFile;

	public DTDSelectIncludeFileWizard(String title, String desc, ViewerFilter filter, IStructuredSelection selection) {
		super();
		setWindowTitle(title);
		setDefaultPageImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(DTDUIPlugin.getDefault().getBundle().getSymbolicName(), DTDEditorPluginImages.IMG_WIZBAN_NEWDTDFILE));

		// Select File Page
		filePage = new DTDSelectSingleFilePage(PlatformUI.getWorkbench(), selection, true);
		filePage.setTitle(title);
		filePage.setDescription(desc);
		filePage.addFilter(filter);
	}

	public void init(IWorkbench aWorkbench, IStructuredSelection aSelection) {
	}

	public void addPages() {
		addPage(filePage);
	}

	public boolean canFinish() {
		return filePage.isPageComplete();
	}

	public boolean performFinish() {
		resultFile = filePage.getFile();
		return true;
	}

	public IFile getResultFile() {
		return resultFile;
	}

	/**
	 * Select DTD File
	 */
	class DTDSelectSingleFilePage extends SelectSingleFilePage {
		public DTDSelectSingleFilePage(IWorkbench w, IStructuredSelection selection, boolean isFileMandatory) {
			super(w, selection, isFileMandatory);
		}

		public boolean isPageComplete() {
			return super.isPageComplete();
		}
	}
}
