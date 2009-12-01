/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.java.refactoring;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.core.internal.java.search.JSPSearchSupport;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.internal.document.DocumentReader;
import org.eclipse.wst.sse.core.internal.encoding.CodedStreamCreator;

/**
 * {@link DocumentChange} implementation for JSP Documents
 */
public class JSPRenameChange extends DocumentChange {

	/**
	 * The JSP file this {@link Change} will change
	 */
	protected IFile fJSPFile = null;
	
	/**
	 * The description of this change
	 */
	private String fDescription;
	
	/**
	 * Create a new {@link JSPRenameChange}
	 * 
	 * @param jspFile
	 * @param jspDoc
	 * @param edit
	 * @param description
	 */
	public JSPRenameChange(IFile jspFile, IDocument jspDoc, TextEdit edit, String description) {
		super(JSPUIMessages.BasicRefactorSearchRequestor_6, jspDoc);
		MultiTextEdit parentEdit = new MultiTextEdit();
		parentEdit.addChild(edit);
		super.setEdit(parentEdit);
		this.fJSPFile = jspFile;
		this.fDescription = description;
	}
	
	/**
	 * Create a new {@link JSPRenameChange} by shallow copying the given
	 * original {@link JSPRenameChange}.
	 * 
	 * @param originalChange the {@link JSPRenameChange} to shallow copy to create
	 * a new {@link JSPRenameChange}
	 */
	public JSPRenameChange(JSPRenameChange originalChange) {
		super(JSPUIMessages.BasicRefactorSearchRequestor_6, originalChange.getJSPDoc());
		super.setEdit(originalChange.getEdit());
		this.fJSPFile = originalChange.fJSPFile;
		this.fDescription = originalChange.fDescription;
	}
	
	/**
	 * <p>Currently will always be {@link RefactoringStatus#OK}</p>
	 * 
	 * @see org.eclipse.ltk.core.refactoring.DocumentChange#isValid(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public RefactoringStatus isValid(IProgressMonitor pm)throws CoreException {
		return new RefactoringStatus();
	}
	
	/**
	 * @see org.eclipse.ltk.core.refactoring.TextChange#getPreviewDocument(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IDocument getPreviewDocument(IProgressMonitor pm) throws CoreException {
		IDocument copyDoc = new Document(this.getJSPDoc().get());
		try {
			super.getEdit().apply(copyDoc);
		}
		catch (MalformedTreeException e) {
			// ignore
		}
		catch (BadLocationException e) {
			// ignore
		}
		return copyDoc;
	}
	
	/**
	 * Performs this change and returns a {@link JSPRenameUndoChange} to undo the change.
	 * 
	 * @return a {@link JSPRenameUndoChange} to undo this performed {@link Change}
	 * @see org.eclipse.ltk.core.refactoring.TextChange#perform(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Change perform(IProgressMonitor pm) throws CoreException {
		Change undoChange = null;
		try {
			//apply edit
			undoChange = super.perform(pm);
			undoChange = new JSPRenameUndoChange(this, undoChange);
			
			//save the model
			saveJSPFile(this.fJSPFile, this.getJSPDoc());
			
		} catch (MalformedTreeException e) {
			Logger.logException(e);
		}
		return undoChange;
	}

	/**
	 * @see org.eclipse.ltk.core.refactoring.TextEditBasedChange#getName()
	 */
	public String getName() {
		return this.fDescription;
	}
	
	/**
	 * <p>The modified element is the JSP {@link IFile} that this {@link Change}
	 * changes.</p>
	 * 
	 * @see org.eclipse.ltk.core.refactoring.DocumentChange#getModifiedElement()
	 */
	public Object getModifiedElement() {
		return this.fJSPFile;
	}
	
	/**
	 * <p>Convenience method to get the JSP {@link IDocument} that this {@link Change}
	 * edits.</p>
	 * 
	 * @return the JSP {@link IDocument} that this {@link Change} edits
	 */
	protected IDocument getJSPDoc() {
		IDocument doc = null;
		try {
			doc = this.acquireDocument(null);
		} catch(CoreException e) {
			//ignore, DocumentChange.acquireDocument will never throw it
		}
		
		return doc;
	}
	
	/**
	 * <p>Saves a JSP file.  If the file is not open in an editor then modifies the file directly, else
	 * if the file is open an editor then run the save method on the open editor.</p>
	 * 
	 * @param jspFile the {@link IFile} to save
	 * @param jspDoc the {@link IDocument} with the new content for the given {@link IFile}
	 */
	protected static void saveJSPFile(IFile jspFile, IDocument jspDoc) {
		//if not open then save model
		final ITextEditor editor = findOpenEditor(jspDoc);
		try {
			/* if no open editor then save the document to the file
			 * else save the open editor
			 */
			if(editor == null) {
				SaveJspFileOp op  = new SaveJspFileOp(jspFile, jspDoc);
				op.run(JSPSearchSupport.getInstance().getProgressMonitor());
			} else {
				//editor save must be done on UI thread
				IRunnableWithProgress runnable= new IRunnableWithProgress() {
					public void run(IProgressMonitor pm) throws InterruptedException {
						editor.doSave(pm);
					}
				};
				PlatformUI.getWorkbench().getProgressService().runInUI(editor.getSite().getWorkbenchWindow(), runnable, null);
			}
		} catch (InvocationTargetException e) {
			Logger.logException(e);
		} catch (InterruptedException e) {
			Logger.logException(e);
		}
	}
	
	/**
	 * <p>Checks if a document is open in an editor and returns it if it is</p>
	 * 
	 * @param jspDoc check to see if this {@link IDocument} is currently open in an editor
	 * @return the open {@link ITextEditor} associated with the given {@link IDocument} or
	 * <code>null</code> if none can be found.
	 */
	private static ITextEditor findOpenEditor(IDocument jspDoc) {
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		IWorkbenchWindow w = null;
		for (int i = 0; i < windows.length; i++) {

			w = windows[i];
			IWorkbenchPage page = w.getActivePage();
			if (page != null) {

				IEditorReference[] references = page.getEditorReferences();
				IEditorPart editor = null;
				Object o = null;
				IDocument doc = null;
				for (int j = 0; j < references.length; j++) {

					editor = references[j].getEditor(false);
					// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=3764
					// use adapter to get ITextEditor (for things like
					// page designer)
					o = editor.getAdapter(ITextEditor.class);
					if (o != null && o instanceof ITextEditor) {

						doc = ((ITextEditor) o).getDocumentProvider().getDocument(editor.getEditorInput());
						if (doc != null && doc.equals(jspDoc)) {
							return (ITextEditor) o;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Workspace operation to perform save on model for updated documents.
	 * Should only be done on models not open in an editor.
	 */
	private static class SaveJspFileOp extends WorkspaceModifyOperation {
		
		private IDocument fJSPDoc = null;
		private IFile fJSPFile = null;
		
		public SaveJspFileOp(IFile jspFile, IDocument jspDoc) {
			this.fJSPDoc = jspDoc;
			this.fJSPFile = jspFile;
		}
		
		protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
			
			// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=3765
			// save file w/ no intermediate model creation
			
			CodedStreamCreator codedStreamCreator = new CodedStreamCreator();
			Reader reader = new DocumentReader(this.fJSPDoc);
			codedStreamCreator.set(this.fJSPFile, reader);
			
			ByteArrayOutputStream codedByteStream = null;
			InputStream codedStream = null;
			try {
				codedByteStream = codedStreamCreator.getCodedByteArrayOutputStream();
				codedStream = new ByteArrayInputStream(codedByteStream.toByteArray());
				if (this.fJSPFile.exists())
					this.fJSPFile.setContents(codedStream, true, true, null);
				else
					this.fJSPFile.create(codedStream, false, null);
				
			} catch (CoreException e) {
				Logger.logException(e);
			} catch (IOException e) {
				Logger.logException(e);
			}
			finally {
				try {
					if(codedByteStream != null)
						codedByteStream.close();
					if(codedStream != null)
						codedStream.close();
				}
				catch (IOException e){
					// unlikely
				}
			}
		}
	}
}