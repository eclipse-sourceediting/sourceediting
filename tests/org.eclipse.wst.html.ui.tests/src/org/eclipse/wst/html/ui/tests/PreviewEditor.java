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
package org.eclipse.wst.html.ui.tests;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.html.ui.StructuredTextEditorHTML;
import org.eclipse.wst.sse.ui.extension.IExtendedSimpleEditor;

/**
 * A simple multi-page editor embedding a Text Editor and the SWT Browser
 * widget synchronized on page switch. The editor should implement
 * IExtendedSimpleEditor and IReusableEditor.
 * 
 * The Text editor class used is the SSE HTML source editor.
 * 
 * @author nitin
 */
public class PreviewEditor extends MultiPageEditorPart implements ITextEditor, IExtendedSimpleEditor {
	private Control fPreviewControl = null;
	private ITextEditor fSourcePage = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ITextEditor#close(boolean)
	 */
	public void close(boolean save) {
		getSourcePage().close(save);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
	 */
	protected void createPages() {
		fSourcePage = createSourcePage();
		try {
			addPage(fSourcePage, getEditorInput());
		}
		catch (PartInitException e) {
			if (getPageCount() > 0) {
				removePage(0);
			}
		}

		fPreviewControl = createPreviewControl();
		addPage(fPreviewControl);

		setPageText(0, "Source");
		setPageText(1, "Preview");
	}

	/**
	 * @return
	 */
	private Control createPreviewControl() {
		return new Browser(getContainer(), SWT.READ_ONLY);
	}

	/**
	 * @return
	 */
	private ITextEditor createSourcePage() {
		return new StructuredTextEditorHTML();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ITextEditor#doRevertToSaved()
	 */
	public void doRevertToSaved() {
		getSourcePage().doRevertToSaved();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		getSourcePage().doSave(monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
		getSourcePage().doSaveAs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ITextEditor#getAction(java.lang.String)
	 */
	public IAction getAction(String actionId) {
		return getSourcePage().getAction(actionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return getSourcePage().getAdapter(adapter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.extension.IExtendedSimpleEditor#getCaretPosition()
	 */
	public int getCaretPosition() {
		if (getSourcePage() instanceof IExtendedSimpleEditor) {
			return ((IExtendedSimpleEditor) getSourcePage()).getCaretPosition();
		}
		ITextSelection selection = (ITextSelection) getSourcePage().getSelectionProvider().getSelection();
		if (selection != null) {
			return selection.getOffset();
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.extension.IExtendedSimpleEditor#getDocument()
	 */
	public IDocument getDocument() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ITextEditor#getDocumentProvider()
	 */
	public IDocumentProvider getDocumentProvider() {
		return getSourcePage().getDocumentProvider();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.extension.IExtendedSimpleEditor#getEditorPart()
	 */
	public IEditorPart getEditorPart() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ITextEditor#getHighlightRange()
	 */
	public IRegion getHighlightRange() {
		return getSourcePage().getHighlightRange();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ITextEditor#getSelectionProvider()
	 */
	public ISelectionProvider getSelectionProvider() {
		return getSourcePage().getSelectionProvider();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.extension.IExtendedSimpleEditor#getSelectionRange()
	 */
	public Point getSelectionRange() {
		if (getSourcePage() instanceof IExtendedSimpleEditor) {
			return ((IExtendedSimpleEditor) getSourcePage()).getSelectionRange();
		}
		ITextSelection selection = (ITextSelection) getSourcePage().getSelectionProvider().getSelection();
		if (selection != null) {
			return new Point(selection.getOffset(), selection.getOffset() + selection.getLength());
		}
		return new Point(0, 0);
	}

	protected ITextEditor getSourcePage() {
		return fSourcePage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite,
	 *      org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
	}

	protected void interruptPreview() {
		((Browser) fPreviewControl).stop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ITextEditor#isEditable()
	 */
	public boolean isEditable() {
		return getSourcePage().isEditable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return getSourcePage().isSaveAsAllowed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.MultiPageEditorPart#pageChange(int)
	 */
	protected void pageChange(int newPageIndex) {
		if (getControl(newPageIndex) == fPreviewControl) {
			updatePreviewContent();
		}
		super.pageChange(newPageIndex);
		if (getControl(newPageIndex) == fPreviewControl) {
			getEditorSite().getActionBarContributor().setActiveEditor(this);
			updatePreviewContent();
		}
		else {
			interruptPreview();
			getEditorSite().getActionBarContributor().setActiveEditor(fSourcePage);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ITextEditor#removeActionActivationCode(java.lang.String)
	 */
	public void removeActionActivationCode(String actionId) {
		getSourcePage().removeActionActivationCode(actionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ITextEditor#resetHighlightRange()
	 */
	public void resetHighlightRange() {
		getSourcePage().resetHighlightRange();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ITextEditor#selectAndReveal(int, int)
	 */
	public void selectAndReveal(int offset, int length) {
		getSourcePage().selectAndReveal(offset, length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ITextEditor#setAction(java.lang.String,
	 *      org.eclipse.jface.action.IAction)
	 */
	public void setAction(String actionID, IAction action) {
		getSourcePage().setAction(actionID, action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ITextEditor#setActionActivationCode(java.lang.String,
	 *      char, int, int)
	 */
	public void setActionActivationCode(String actionId, char activationCharacter, int activationKeyCode, int activationStateMask) {
		setActionActivationCode(actionId, activationCharacter, activationKeyCode, activationStateMask);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ITextEditor#setHighlightRange(int, int,
	 *      boolean)
	 */
	public void setHighlightRange(int offset, int length, boolean moveCursor) {
		getSourcePage().setHighlightRange(offset, length, moveCursor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		if (fSourcePage != null) {
			((IReusableEditor) fSourcePage).setInput(input);
		}
		if (fPreviewControl != null && getControl(getActivePage()) == fPreviewControl) {
			interruptPreview();
			updatePreviewContent();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ITextEditor#showHighlightRangeOnly(boolean)
	 */
	public void showHighlightRangeOnly(boolean showHighlightRangeOnly) {
		getSourcePage().showHighlightRangeOnly(showHighlightRangeOnly);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ITextEditor#showsHighlightRangeOnly()
	 */
	public boolean showsHighlightRangeOnly() {
		return getSourcePage().showsHighlightRangeOnly();
	}

	/**
	 * Update the contents of the Preview page
	 */
	protected void updatePreviewContent() {
		// TODO: Add a base href so that images show up
		boolean rendered = ((Browser) fPreviewControl).setText(getSourcePage().getDocumentProvider().getDocument(getSourcePage().getEditorInput()).get());
		if (!rendered)
			System.out.println("Failure rendering");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.extension.IExtendedSimpleEditor#validateEdit(org.eclipse.swt.widgets.Shell)
	 */
	public IStatus validateEdit(Shell context) {
		if (getSourcePage() instanceof IExtendedSimpleEditor) {
			return ((IExtendedSimpleEditor) getSourcePage()).validateEdit(context);
		}
		return new Status(IStatus.OK, SSEForHTMLTestsPlugin.getDefault().getDescriptor().getUniqueIdentifier(), IStatus.OK, "", null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#getTitle()
	 */
	public String getTitle() {
		String title = super.getTitle();
		if (title == null && getEditorInput() != null) {
			title = getEditorInput().getName();
		}
		return title;
	}
}