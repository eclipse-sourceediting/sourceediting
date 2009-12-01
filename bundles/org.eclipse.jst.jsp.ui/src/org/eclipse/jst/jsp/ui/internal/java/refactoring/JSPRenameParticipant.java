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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jst.jsp.core.internal.java.search.JSPSearchScope;
import org.eclipse.jst.jsp.core.internal.java.search.JSPSearchSupport;
import org.eclipse.jst.jsp.ui.internal.JSPUIMessages;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.ISharableParticipant;
import org.eclipse.ltk.core.refactoring.participants.RefactoringArguments;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.text.edits.TextEdit;

/**
 * Abstract {@link ISharableParticipant} {@link RenameParticipant} for editing JSP documents
 */
public abstract class JSPRenameParticipant extends RenameParticipant implements ISharableParticipant {
	/**
	 * The name of this participant.
	 * Should be initialized by implementers in {@link #initialize(Object)}
	 */
	protected String fName;
	
	/**
	 * A map of {@link IJavaElement} names to pairs of {@link IJavaElement}s
	 * and their associated {@link RenameArguments} that have been added to
	 * this {@link ISharableParticipant}.
	 * 
	 * key: {@link String} - Element name<br/>
	 * value: {@link ElementAndArgumentsPair}
	 */
	private Map fElementAndArgumentPairs;
	
	/**
	 * When new changes are being safely created {@link #getTextChange(Object)}
	 * is called first to check for existing {@link TextChange}s, but those
	 * results do not usually include the changes that have been created thus far
	 * locally by this {@link RenameParticipant}.  This is to keep track of those
	 * changes so the overridden version of {@link #getTextChange(Object)}s will take
	 * these local {@link TextChange}s into account.
	 */
	private Map fLocalTextChanges;
	
	/**
	 * Groups an {@link IJavaElement} with its associated {@link RenameArguments}
	 * that have been added to this {@link ISharableParticipant}
	 */
	private class ElementAndArgumentsPair {
		protected IJavaElement fElement;
		protected RenameArguments fArgs;
		
		public ElementAndArgumentsPair(IJavaElement element, RenameArguments args) {
			this.fElement = element;
			this.fArgs = args;
		}
	}

	/**
	 * <p>Do local initialization. This is done here instead of in an implementation of
	 * {@link RefactoringParticipant#initialize(java.lang.Object)} because implementers
	 * of this class are not expected to have to call super when they implement
	 * {@link RefactoringParticipant#initialize(java.lang.Object)}</p>
	 * 
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#initialize(
	 * 	org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor, java.lang.Object,
	 * 	org.eclipse.ltk.core.refactoring.participants.RefactoringArguments)
	 */
	public boolean initialize(RefactoringProcessor processor, Object element,
			RefactoringArguments arguments) {
		
		this.fElementAndArgumentPairs = new HashMap();
		this.addElement(element, arguments);
		this.fLocalTextChanges = new HashMap();
		this.fName = ""; //$NON-NLS-1$
		
		return super.initialize(processor, element, arguments);
	}

	/**
	 * <p>When an element is added to this {@link ISharableParticipant} it must be
	 * a {@link IJavaElement} and be a legal element type ({@link #isLegalElementType(IJavaElement)}
	 * and the given arguments must be {@link RenameArguments}.  Also the new <code>element</code>
	 * will not be added if and {@link IJavaElement} of that name has already been added to
	 * this {@link ISharableParticipant}.  This protects against elements being added more
	 * then once.</p>
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.ISharableParticipant#addElement(java.lang.Object,
	 * 	org.eclipse.ltk.core.refactoring.participants.RefactoringArguments)
	 */
	public void addElement(Object element, RefactoringArguments arguments) {
		if(element instanceof IJavaElement &&
				isLegalElementType((IJavaElement)element) &&
				arguments instanceof RenameArguments) {
			
			//don't add elements that have already been added
			String elementName = ((IJavaElement)element).getElementName();
			if(!this.fElementAndArgumentPairs.containsKey(elementName)) {
				this.fElementAndArgumentPairs.put(elementName,
						new ElementAndArgumentsPair((IJavaElement)element, (RenameArguments)arguments));
			}
		}
	}
	
	/**
	 * <p>As of now the conditions are always {@link RefactoringStatus#OK}</p>
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#checkConditions(
	 * 	org.eclipse.core.runtime.IProgressMonitor, org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
	 */
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) {
		return new RefactoringStatus();
	}
	
	/**
	 * 
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#createChange(
	 * 	org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Change createChange(IProgressMonitor pm) throws CoreException {
		this.getTextChange(""); //$NON-NLS-1$
		
		//create one multi change to contain all new created changes
		CompositeChange multiChange = new CompositeChange(JSPUIMessages.JSP_changes);
		
		//for each element get the changes for it and add it to the multi change
		Iterator iter = fElementAndArgumentPairs.values().iterator();
		while(iter.hasNext()) {
			ElementAndArgumentsPair elemArgsPair = (ElementAndArgumentsPair)iter.next();
			Change[] changes = createChangesFor(elemArgsPair.fElement, elemArgsPair.fArgs.getNewName());
			
			/* add all new text changes to the local list of text changes so that
			 * future iterations through the while loop will be aware of already
			 * existing changes
			 */
			for(int i = 0; i < changes.length; ++i) {
				if(changes[i] instanceof TextChange) {
					fLocalTextChanges.put(((TextChange)changes[i]).getModifiedElement(), changes[i]);
				}
			}
			
			if(changes.length > 0) {
				multiChange.addAll(changes);
			}
		}

		//unless there are actually new changes return null
		Change result = null;
		if(multiChange.getChildren().length > 0) {
			result = multiChange;
		}
		
		return result;
	}
	
	/**
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#getName()
	 */
	public String getName() {
		return fName;
	}
	
	/**
	 * <p>Overridden to include locally created {@link TextChange}s that have not yet be returned by
	 * {@link #createChange(IProgressMonitor)}.</p>
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#getTextChange(java.lang.Object)
	 */
	public TextChange getTextChange(Object element) {
		TextChange existingChange = (TextChange)fLocalTextChanges.get(element);
		if(existingChange == null) {
			existingChange = super.getTextChange(element);
		}
		
		return existingChange;
	}
	
	/**
	 * 
	 * @param element the {@link IJavaElement} to create new changes for
	 * @param newName the new name of the given {@link IJavaElement}
	 * 
	 * @return any newly created {@link Change}s.  It is important to note
	 * that while no NEW {@link Change}s maybe returned it is possible that
	 * new {@link TextEdit}s will still added to existing {@link Change}s.
	 */
	protected Change[] createChangesFor(IJavaElement element, String newName) {
		Change[] changes;
		BasicRefactorSearchRequestor requestor = getSearchRequestor(element, newName);
		if(requestor != null) {
			JSPSearchSupport support = JSPSearchSupport.getInstance();
			support.searchRunnable(element, new JSPSearchScope(), requestor);
			changes = requestor.getChanges(this);
		} else {
			changes = new Change[0];
		}
		
		return changes;
	}
	
	/**
	 * <p>Should be implemented to return the {@link BasicRefactorSearchRequestor} associated
	 * with the implementers {@link JSPRenameParticipantParticipant}.</p>
	 * 
	 * @param element the {@link IJavaElement} to create the {@link BasicRefactorSearchRequestor} from
	 * @param newName the new name of the {@link IJavaElement} to use when
	 * creating the {@link BasicRefactorSearchRequestor}
	 * 
	 * @return a new {@link BasicRefactorSearchRequestor} based on the given parameters
	 */
	protected abstract BasicRefactorSearchRequestor getSearchRequestor(IJavaElement element, String newName);
	
	/**
	 * @param element check that this {@link IJavaElement} is of the type the
	 * implementers {@link JSPRenameParticipant} is configured to deal with.
	 * 
	 * @return <code>true</code> if the given {@link IJavaElement} is of a type
	 * the implementers {@link JSPRenameParticipant} is configured to deal with,
	 * <code>false</code> otherwise.
	 */
	protected abstract boolean isLegalElementType(IJavaElement element);
}
