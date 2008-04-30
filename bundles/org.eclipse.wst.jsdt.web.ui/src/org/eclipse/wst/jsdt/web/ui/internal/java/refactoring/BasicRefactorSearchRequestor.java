/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.java.refactoring;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.osgi.util.NLS;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.search.SearchDocument;
import org.eclipse.wst.jsdt.core.search.SearchMatch;
import org.eclipse.wst.jsdt.core.search.SearchRequestor;
import org.eclipse.wst.jsdt.web.core.javascript.search.JSDTSearchDocumentDelegate;
import org.eclipse.wst.jsdt.web.core.javascript.search.JsSearchSupport;
import org.eclipse.wst.jsdt.web.ui.internal.JsUIMessages;
import org.eclipse.wst.jsdt.web.ui.internal.Logger;
import org.eclipse.wst.sse.core.internal.document.DocumentReader;
import org.eclipse.wst.sse.core.internal.encoding.CodedStreamCreator;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*
 * @author pavery
 */
public class BasicRefactorSearchRequestor extends SearchRequestor {
	/**
	 * Change class that wraps a text edit on the jsp document
	 */
	private class RenameChange extends DocumentChange {
		private String fDescription = JsUIMessages.BasicRefactorSearchRequestor_0;
		private TextEdit fEdit = null;
		private IDocument fJSPDoc = null;
		private IFile fJSPFile = null;
		
		public RenameChange(IFile jspFile, IDocument jspDoc, TextEdit edit, String description) {
			super(JsUIMessages.BasicRefactorSearchRequestor_6, jspDoc);
			this.fEdit = edit;
			this.fJSPFile = jspFile;
			this.fJSPDoc = jspDoc;
			this.fDescription = description;
		}
		
		
		public Object getModifiedElement() {
			return getElement();
		}
		
		
		public String getName() {
			return this.fDescription;
		}
		
		
		public IDocument getPreviewDocument(IProgressMonitor pm) throws CoreException {
			IDocument copyDoc = new Document(fJSPDoc.get());
			try {
				fEdit.apply(copyDoc);
			} catch (MalformedTreeException e) {
				// ignore
			} catch (BadLocationException e) {
				// ignore
			}
			return copyDoc;
		}
		
		/**
		 * Checks if a document is open in an editor
		 * 
		 * @param jspDoc
		 * @return
		 */
		private boolean isOpenInEditor(IDocument jspDoc) {
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
						editor = references[j].getEditor(true);
						// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=3764
						// use adapter to get ITextEditor (for things like
						// page designer)
						o = editor.getAdapter(ITextEditor.class);
						if (o != null && o instanceof ITextEditor) {
							doc = ((ITextEditor) o).getDocumentProvider().getDocument(editor.getEditorInput());
							if (doc != null && doc.equals(jspDoc)) {
								return true;
							}
						}
					}
				}
			}
			return false;
		}
		
		
		public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException {
			return new RefactoringStatus();
		}
		
		
		public Change perform(IProgressMonitor pm) throws CoreException {
			RenameChange undoChange = null;
			try {
				if (!isOpenInEditor(this.fJSPDoc)) {
					// apply edit to JSP doc AND save model
					undoChange = new RenameChange(this.fJSPFile, this.fJSPDoc, this.fEdit.apply(fJSPDoc), this.fDescription);
					saveFile(this.fJSPFile, this.fJSPDoc);
				} else {
					// just apply edit to JSP document
					undoChange = new RenameChange(this.fJSPFile, this.fJSPDoc, this.fEdit.apply(fJSPDoc), this.fDescription);
				}
			} catch (MalformedTreeException e) {
				Logger.logException(e);
			} catch (BadLocationException e) {
				Logger.logException(e);
			}
			return undoChange;
		}
		
		/**
		 * Performed in an operation since it modifies resources in the
		 * workspace
		 * 
		 * @param jspDoc
		 * @throws CoreException
		 */
		private void saveFile(IFile jspFile, IDocument jspDoc) {
			SaveJspFileOp op = new SaveJspFileOp(jspFile, jspDoc);
			try {
				op.run(JsSearchSupport.getInstance().getProgressMonitor());
			} catch (InvocationTargetException e) {
				Logger.logException(e);
			} catch (InterruptedException e) {
				Logger.logException(e);
			}
		}
	}
	// end inner class SaveJspFileOp
	/**
	 * Workspace operation to perform save on model for updated documents.
	 * Should only be done on models not open in an editor.
	 */
	private class SaveJspFileOp extends WorkspaceModifyOperation {
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
				if (this.fJSPFile.exists()) {
					this.fJSPFile.setContents(codedStream, true, true, null);
				} else {
					this.fJSPFile.create(codedStream, false, null);
				}
			} catch (CoreException e) {
				Logger.logException(e);
			} catch (IOException e) {
				Logger.logException(e);
			} finally {
				try {
					if (codedByteStream != null) {
						codedByteStream.close();
					}
					if (codedStream != null) {
						codedStream.close();
					}
				} catch (IOException e) {
					// unlikely
				}
			}
		}
	}
	// end inner class RenameChange
	/** The type being renamed (the old type) */
	IJavaScriptElement fElement = null;
	/** The new name of the type being renamed */
	private String fNewName = ""; //$NON-NLS-1$
	/** maps a JSPSearchDocument path -> MultiTextEdit for the java file */
	private HashMap fSearchDocPath2JavaEditMap = null;
	
	public BasicRefactorSearchRequestor(IJavaScriptElement element, String newName) {
		this.fNewName = newName;
		this.fElement = element;
		this.fSearchDocPath2JavaEditMap = new HashMap();
	}
	
	/**
	 * @see org.eclipse.wst.jsdt.core.search.SearchRequestor#acceptSearchMatch(org.eclipse.wst.jsdt.core.search.SearchMatch)
	 */
	
	public void acceptSearchMatch(SearchMatch javaMatch) throws CoreException {
		String matchDocumentPath = javaMatch.getResource().getFullPath().toString();
		SearchDocument searchDoc = JsSearchSupport.getInstance().getSearchDocument(matchDocumentPath);
		if (searchDoc != null && searchDoc instanceof JSDTSearchDocumentDelegate) {
			String renameText = getRenameText((JSDTSearchDocumentDelegate) searchDoc, javaMatch);
			// add it for the correct document
			addJavaEdit(searchDoc.getPath(), new ReplaceEdit(javaMatch.getOffset(), javaMatch.getLength(), renameText));
		}
	}
	
	/**
	 * Adds to the multi edit for a give java document.
	 * 
	 * @param javaDocument
	 * @param javaEdit
	 */
	private void addJavaEdit(String searchDocPath, ReplaceEdit javaEdit) {
		Object o = this.fSearchDocPath2JavaEditMap.get(searchDocPath);
		if (o != null) {
			MultiTextEdit multi = (MultiTextEdit) o;
			multi.addChild(javaEdit);
		} else {
			// use a multi edit so doc position offsets get updated
			// automatically
			// when adding multiple child edits
			MultiTextEdit multi = new MultiTextEdit();
			multi.addChild(javaEdit);
			this.fSearchDocPath2JavaEditMap.put(searchDocPath, multi);
		}
	}
	
	private Change createChange(JSDTSearchDocumentDelegate searchDoc, TextEdit edit) {
		IDocument doc = searchDoc.getJspTranslation().getHtmlDocument();
		String file = searchDoc.getFile().getName();
		String description = getDescription();
		try {
			// document lines are 0 based
			String lineNumber = Integer.toString(doc.getLineOfOffset(edit.getOffset()) + 1);
			description += " " + NLS.bind(JsUIMessages.BasicRefactorSearchRequestor_1, new String[] { file, lineNumber }); //$NON-NLS-1$
		} catch (BadLocationException e) {
			Logger.logException(e);
		}
		return new RenameChange(searchDoc.getFile(), doc, edit, description);
	}
	
	/**
	 * 
	 * @return all JSP changes for the search matches for the given Type
	 */
	public Change[] getChanges() {
		JsSearchSupport support = JsSearchSupport.getInstance();
		List changes = new ArrayList();
		Iterator keys = fSearchDocPath2JavaEditMap.keySet().iterator();
		String searchDocPath = null;
		SearchDocument delegate = null;
		while (keys.hasNext()) {
			// create on the fly
			searchDocPath = (String) keys.next();
			MultiTextEdit javaEdit = (MultiTextEdit) fSearchDocPath2JavaEditMap.get(searchDocPath);
			delegate = support.getSearchDocument(searchDocPath);
			if (delegate != null && delegate instanceof JSDTSearchDocumentDelegate) {
				JSDTSearchDocumentDelegate javaDelegate = (JSDTSearchDocumentDelegate) delegate;
				changes.add(createChange(javaDelegate, javaEdit));
			}
		}
		return (Change[]) changes.toArray(new Change[changes.size()]);
	}
	
	/**
	 * Subclasses should override to better describe the change.
	 * 
	 * @return
	 */
	protected String getDescription() {
		return ""; //$NON-NLS-1$
	}
	
	public IJavaScriptElement getElement() {
		return this.fElement;
	}
	
	/**
	 * @return the new name for the Type
	 */
	public String getNewName() {
		return this.fNewName;
	}
	
	/**
	 * @param searchDoc
	 * @return
	 */
	protected String getRenameText(JSDTSearchDocumentDelegate searchDoc, SearchMatch javaMatch) {
		return getNewName();
	}
}
