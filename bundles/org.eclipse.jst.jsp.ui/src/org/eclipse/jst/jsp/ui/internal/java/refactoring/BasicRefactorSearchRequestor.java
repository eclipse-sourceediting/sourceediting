/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.java.refactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.SearchDocument;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.core.internal.java.search.JSPSearchSupport;
import org.eclipse.jst.jsp.core.internal.java.search.JavaSearchDocumentDelegate;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.osgi.util.NLS;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditGroup;

/**
 * <p>After a search is run with this {@link SearchRequestor} {@link #getChanges(RefactoringParticipant)}
 * can be called to get any new {@link Change}s that need to be created as a result of the search.  If
 * {@link Change}s are already existing for the documents found then new {@link Change}s will not be
 * created for them, but the needed {@link TextEdit}s will be added to the existing {@link Change}s.</p>
 */
public class BasicRefactorSearchRequestor extends SearchRequestor {
	/** The type being renamed (the old type)*/
	IJavaElement fElement = null;
	/** The new name of the type being renamed*/
	private String fNewName = ""; //$NON-NLS-1$
	/** maps a JSPSearchDocument path -> MultiTextEdit for the java file*/
	private HashMap fSearchDocPath2JavaEditMap = null;
	
	public BasicRefactorSearchRequestor(IJavaElement element, String newName) {
		this.fNewName = newName;
		this.fElement = element;
		this.fSearchDocPath2JavaEditMap = new HashMap();
	}
	
	public IJavaElement getElement() {
		return this.fElement;
	}

	/**
	 * @return the new name for the Type
	 */
	public String getNewName() {
		return this.fNewName;
	}
	
	/**
	 * @see org.eclipse.jdt.core.search.SearchRequestor#acceptSearchMatch(org.eclipse.jdt.core.search.SearchMatch)
	 */
	public void acceptSearchMatch(SearchMatch javaMatch) throws CoreException {
		
		String matchDocumentPath = javaMatch.getResource().getFullPath().toString();
		SearchDocument searchDoc = JSPSearchSupport.getInstance().getSearchDocument(matchDocumentPath);
	
		if (searchDoc != null && searchDoc instanceof JavaSearchDocumentDelegate) {
	
			String renameText = getRenameText((JavaSearchDocumentDelegate)searchDoc, javaMatch);
			
			//if rename text is null then don't create an edit for it
			if(renameText != null) {
				// add it for the correct document
				addJavaEdit(searchDoc.getPath(), new ReplaceEdit(javaMatch.getOffset(), javaMatch.getLength(), renameText));
			}
		}
	}
	
	/**
	 * @param searchDoc
	 * @return the rename text or <code>null</code> if no edit should be created for the given match.
	 * Such a case would be a match where nothing needs to be edited.
	 */
	protected String getRenameText(JavaSearchDocumentDelegate searchDoc, SearchMatch javaMatch) {
		return getNewName();
	}

	/**
	 * Adds to the multi edit for a give java document.
	 * @param javaDocument
	 * @param javaEdit
	 */
	private void addJavaEdit(String searchDocPath, ReplaceEdit javaEdit) {
		
		Object o = this.fSearchDocPath2JavaEditMap.get(searchDocPath);
		if(o != null) {

			MultiTextEdit multi = (MultiTextEdit)o;
			multi.addChild(javaEdit);
		}
		else {
			// use a multi edit so doc position offsets get updated automatically
			// when adding multiple child edits
			MultiTextEdit multi = new MultiTextEdit();
			multi.addChild(javaEdit);
			this.fSearchDocPath2JavaEditMap.put(searchDocPath, multi);
		}
	}
	
	/**
	 * <p>This function is not safe because it does not check for existing {@link Change}s that
	 * new {@link Change}s created by this method may conflict with.  These conflicts can
	 * cause indeterminate results when applied to documents.  Long story short, don't
	 * use this method any more.</p>
	 * 
	 * @return all JSP changes for the search matches for the given Type, they may conflict
	 * with already existing {@link Change}s
	 * 
	 * @see #getChanges(RefactoringParticipant)
	 * 
	 * @deprecated
	 */
	public Change[] getChanges() {
		
		JSPSearchSupport support = JSPSearchSupport.getInstance();
		List changes = new ArrayList();
		Iterator keys = fSearchDocPath2JavaEditMap.keySet().iterator();
		String searchDocPath = null;
		SearchDocument delegate = null;
		
		while(keys.hasNext()) {
			// create on the fly
			searchDocPath = (String)keys.next();
			MultiTextEdit javaEdit = (MultiTextEdit)fSearchDocPath2JavaEditMap.get(searchDocPath);
			delegate = support.getSearchDocument(searchDocPath);
			
			if(delegate != null && delegate instanceof JavaSearchDocumentDelegate) {
				JavaSearchDocumentDelegate javaDelegate = (JavaSearchDocumentDelegate)delegate;
				changes.add(createChange(javaDelegate, javaDelegate.getJspTranslation().getJspEdit(javaEdit)));
			}
		}
		return (Change[])changes.toArray(new Change[changes.size()]);
	}
	
	/**
	 * Gets new {@link Change}s created as a result of this {@link SearchRequestor}.
	 * Any existing {@link TextChange}s that had new edits added to them will not be
	 * returned.
	 * 
	 * @param participant {@link RefactoringParticipant} to determine if there are already existing
	 * {@link TextChange}s for the documents that this {@link SearchRequestor} found.
	 * If existing
	 * {@link TextChange}s are found then they will be used for any new edits, else new {@link TextChange}s
	 * will be created.
	 * 
	 * @return Any new {@link TextChange}s created by this {@link SearchRequestor}.  If edits were
	 * added to existing {@link TextChange}s then those existing {@link TextChange}s will not be
	 * returned in this array.
	 */
	public Change[] getChanges(RefactoringParticipant participant) {
		
		JSPSearchSupport support = JSPSearchSupport.getInstance();
		List changes = new ArrayList();
		Iterator keys = fSearchDocPath2JavaEditMap.keySet().iterator();
		String searchDocPath = null;
		SearchDocument delegate = null;
		
		while(keys.hasNext()) {
			// create on the fly
			searchDocPath = (String)keys.next();
			MultiTextEdit javaEdit = (MultiTextEdit)fSearchDocPath2JavaEditMap.get(searchDocPath);
			delegate = support.getSearchDocument(searchDocPath);
			
			if(delegate != null && delegate instanceof JavaSearchDocumentDelegate) {
				JavaSearchDocumentDelegate javaDelegate = (JavaSearchDocumentDelegate)delegate;
				Change change = createChange(javaDelegate, javaDelegate.getJspTranslation().getJspEdit(javaEdit), participant);
				changes.add(change);
			}
		}
		return (Change[])changes.toArray(new Change[changes.size()]);
	}
	
	/**
	 * <p>This method is not safe because it does not take into consideration already existing
	 * {@link Change}s and thus conflicts could occur that when applied create indeterminate
	 * results in the target documents</p>
	 * 
	 * @see #createChange(JavaSearchDocumentDelegate, TextEdit, RefactoringParticipant)
	 * 
	 * @deprecated
	 */
	private Change createChange(JavaSearchDocumentDelegate searchDoc, TextEdit edit) {
		
		IDocument doc = searchDoc.getJspTranslation().getJspDocument();
		String file = searchDoc.getFile().getName();
		String description = getDescription();
		try {
			// document lines are 0 based
			String lineNumber = Integer.toString(doc.getLineOfOffset(edit.getOffset()) + 1);
			description += " " + NLS.bind(JSPUIMessages.BasicRefactorSearchRequestor_1, new String[]{file, lineNumber}); //$NON-NLS-1$
		} 
		catch (BadLocationException e) {
			Logger.logException(e);
		}
		return new JSPRenameChange(searchDoc.getFile(), doc, edit, description);
	}
	
	/**
	 * </p>If a {@link TextChange} does not already exist for the given {@link JavaSearchDocumentDelegate}
	 * then a new one will be created with the given {@link TextEdit}.  Otherwise the given {@link TextEdit}
	 * will be added to a new group and added to the existing change and <code>null</code> will be returned.</p>
	 * 
	 * @param searchDoc the {@link JavaSearchDocumentDelegate} that the <code>edit</code> will be applied to
	 * @param edit the {@link TextEdit} that needs to be added to a new {@link TextChange} or appended to an
	 * existing one
	 * @param participant the {@link RefactoringParticipant} that knows about the existing {@link TextChange}s
	 * @return a new {@link Change} if there was not one already existing for the document in question,
	 * else <code>null</code>
	 */
	private Change createChange(JavaSearchDocumentDelegate searchDoc, TextEdit edit, RefactoringParticipant participant) {
		IDocument doc = searchDoc.getJspTranslation().getJspDocument();
		String description = getDescription();
		
		TextChange existingChange = participant.getTextChange(searchDoc.getFile());
		TextChange change = null;
		if(existingChange != null) {
			try {
				existingChange.addEdit(edit);
			}catch (MalformedTreeException e) {
				Logger.logException("MalformedTreeException while adding edit " + //$NON-NLS-1$
						edit + " to existing change " + change, e); //$NON-NLS-1$
			}
			
			TextEditGroup group = new TextEditGroup(description, edit);
			existingChange.addTextEditGroup(group);
		} else {
			change = new JSPRenameChange(searchDoc.getFile(), doc, edit, searchDoc.getFile().getName());
			TextEditGroup group = new TextEditGroup(description, edit);
			change.addTextEditGroup(group);
		}
		
		return change; 
	}
	
	// https://w3.opensource.ibm.com/bugzilla/show_bug.cgi?id=3205
	// only relevant for IType refactorings
	protected boolean isFullyQualified(String matchText) {
		if(getElement() instanceof IType) {
			String pkg = ((IType)getElement()).getPackageFragment().getElementName();
			return matchText.startsWith(pkg);
		}
		return false;
	}

	/**
	 * Subclasses should override to better describe the change.
	 * @return
	 */
	protected String getDescription() {
		return ""; //$NON-NLS-1$
	}
}
