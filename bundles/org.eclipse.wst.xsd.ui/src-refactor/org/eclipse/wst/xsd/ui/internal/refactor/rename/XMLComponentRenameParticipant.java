/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor.rename;

import java.util.Iterator;
import java.util.List;

//import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
//import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.wst.common.core.search.SearchMatch;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;
import org.eclipse.wst.xsd.ui.internal.refactor.TextChangeManager;
import org.eclipse.wst.xsd.ui.internal.refactor.util.TextChangeCompatibility;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Node;

public class XMLComponentRenameParticipant extends RenameParticipant {
	
	protected SearchMatch match;

	protected TextChangeManager changeManager;
	protected List matches;

    
    
	protected boolean initialize(Object element) {
		
		if(getArguments() instanceof ComponentRenameArguments){
			// changeManger is passed in from the RenameComponentProcessor to collect all the changes
			changeManager = ((ComponentRenameArguments)getArguments()).getChangeManager();
		}
		
		return false;
	}
	
	public String getName() {
		return "XML Component Rename Participant";
	}

	public RefactoringStatus checkConditions(IProgressMonitor monitor,
			CheckConditionsContext context) throws OperationCanceledException {
		return null;
	}
	
	public TextChangeManager getChangeManager(){
		
		if(changeManager == null){
				changeManager = new TextChangeManager(false);
		}
		return changeManager;
		
	}
	
//	private RefactoringStatus createRenameChanges(final IProgressMonitor monitor) throws CoreException {
//		Assert.isNotNull(monitor);
//		final RefactoringStatus status= new RefactoringStatus();
//		try {
//			monitor.beginTask("RefactoringMessages.RenameComponentRefactoring_searching", 1); 
//			createRenameChanges(new SubProgressMonitor(monitor, 1));
//			//updateChangeManager(new SubProgressMonitor(monitor, 1), status);
//		} finally {
//			monitor.done();
//		}
//		return status;
//	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		for (Iterator iter = matches.iterator(); iter.hasNext();) {
			SearchMatch match = (SearchMatch) iter.next();
			TextChange textChange = getChangeManager().get(match.getFile());
			String newName = getArguments().getNewName();
			String qualifier = "";
			if(getArguments() instanceof ComponentRenameArguments){
				qualifier = ((ComponentRenameArguments)getArguments()).getQualifier();
			}
			if(match.getObject() instanceof Node){
				Node node = (Node)match.getObject();
				if(node instanceof IDOMAttr){
					IDOMAttr attr = (IDOMAttr)node;
					IDOMElement element = (IDOMElement)attr.getOwnerElement() ;
					newName = getNewQName(element, qualifier, newName);
				}
				newName = RenameComponentProcessor.quoteString(newName);
			}
			
			ReplaceEdit replaceEdit = new ReplaceEdit(match.getOffset(), match.getLength(), newName );
			String editName = RefactoringMessages.getString("RenameComponentProcessor.Component_Refactoring_update_reference");
			TextChangeCompatibility.addTextEdit(textChange, editName, replaceEdit);
   		}
		// don't create any change now, all the changes are in changeManger variable and will be combined in RenameComponentProcessor.postCreateChange method
		return null;
	}
	
	private static String getNewQName(Node node, String targetNamespace, String newName) {
		StringBuffer sb = new StringBuffer();
		if (newName != null) {
			String prefix = XSDConstants.lookupQualifier(node, targetNamespace);
			if (prefix != null && prefix.length() > 0) {
				sb.append(prefix);
				sb.append(":");
				sb.append(newName);
			} else {
				sb.append(newName);
			}
		} else {
			sb.append(newName);
		}

		return sb.toString();
	}

  public void setChangeManager(TextChangeManager changeManager)
  {
    this.changeManager = changeManager;
  }

}
