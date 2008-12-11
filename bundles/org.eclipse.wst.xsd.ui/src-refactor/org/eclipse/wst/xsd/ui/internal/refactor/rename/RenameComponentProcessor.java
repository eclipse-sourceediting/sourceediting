/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
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

//import com.ibm.icu.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.text.Position;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.ParticipantManager;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.ltk.core.refactoring.participants.RenameProcessor;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.wst.common.core.search.SearchEngine;
import org.eclipse.wst.common.core.search.SearchMatch;
import org.eclipse.wst.common.core.search.pattern.QualifiedName;
import org.eclipse.wst.common.core.search.pattern.SearchPattern;
import org.eclipse.wst.common.core.search.scope.SearchScope;
import org.eclipse.wst.common.core.search.scope.SelectionSearchScope;
import org.eclipse.wst.common.core.search.scope.WorkspaceSearchScope;
import org.eclipse.wst.common.core.search.util.CollectingSearchRequestor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.search.XMLComponentDeclarationPattern;
import org.eclipse.wst.xml.core.internal.search.XMLComponentReferencePattern;
import org.eclipse.wst.xsd.ui.internal.refactor.Checks;
import org.eclipse.wst.xsd.ui.internal.refactor.INameUpdating;
import org.eclipse.wst.xsd.ui.internal.refactor.IReferenceUpdating;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringComponent;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;
import org.eclipse.wst.xsd.ui.internal.refactor.TextChangeManager;
import org.eclipse.wst.xsd.ui.internal.refactor.util.TextChangeCompatibility;



public class RenameComponentProcessor extends RenameProcessor implements INameUpdating, IReferenceUpdating {
	public static final String IDENTIFIER = "org.eclipse.wst.xml.refactor.renameComponentProcessor"; //$NON-NLS-1$
    private boolean singleFileOnly = false;
    
	public static String quoteString(String value) {
		value = value == null ? "" : value;

		StringBuffer sb = new StringBuffer();
		if (!value.startsWith("\"")) {
			sb.append("\"");
		}
		sb.append(value);
		if (!value.endsWith("\"")) {
			sb.append("\"");
		}
		return sb.toString();
	}

	private TextChangeManager changeManager;

	private String newName;

	private RefactoringComponent selectedComponent;

	private boolean updateReferences = true;

	private Map references = new HashMap();

	public RenameComponentProcessor(RefactoringComponent selectedComponent) {
		this.selectedComponent = selectedComponent;
	}

	public RenameComponentProcessor(RefactoringComponent selectedComponent, String newName) {
		this(selectedComponent, newName, false);
	}
    
    public RenameComponentProcessor(RefactoringComponent selectedComponent, String newName, boolean singleFileOnly) {
        this.newName = newName;
        this.selectedComponent = selectedComponent;
        this.singleFileOnly = singleFileOnly;
    }    

	private void addDeclarationUpdate(TextChangeManager manager) throws CoreException {
		String fileStr = selectedComponent.getElement().getModel().getBaseLocation();
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileStr));
		addDeclarationUpdate(manager, file);
	}

	final void addDeclarationUpdate(TextChangeManager manager, IFile file) throws CoreException {

		String componentName = selectedComponent.getName();
		String componentNamespace = selectedComponent.getNamespaceURI();
		QualifiedName elementQName = new QualifiedName(componentNamespace, componentName);
		QualifiedName typeQName = selectedComponent.getTypeQName();



		SearchScope scope = new WorkspaceSearchScope();
		if (file != null) {
			scope = new SelectionSearchScope(new IResource[]{file});
		}
		CollectingSearchRequestor requestor = new CollectingSearchRequestor();
		SearchPattern pattern = new XMLComponentDeclarationPattern(file, elementQName, typeQName);
		SearchEngine searchEngine = new SearchEngine();
        HashMap map = new HashMap();
        if (singleFileOnly)
        {
          map.put("searchDirtyContent", Boolean.TRUE);
        }  
		searchEngine.search(pattern, requestor, scope, map, new NullProgressMonitor());
		List results = requestor.getResults();
		
		// more than one declaration found, so use offset as additional check
		Position offsetPosition = null;
		if (results.size() > 1) {
		    IDOMElement selectedElement = selectedComponent.getElement();
		    if (selectedElement != null) {
		    	int startOffset = selectedElement.getStartOffset();
		    	offsetPosition = new Position(startOffset, (selectedElement.getEndOffset() - startOffset));
		    }
		}
		
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			SearchMatch match = (SearchMatch) iter.next();
			if (match != null) {
				boolean addTextChange = true;
				// additional check to verify correct declaration is changed
				if (offsetPosition != null) {
					addTextChange = offsetPosition.overlapsWith(match.getOffset(), match.getLength());  
				}
				if (addTextChange) {
					TextChange textChange = manager.get(match.getFile());
					String newName = getNewElementName();
					newName = quoteString(newName);
	
					ReplaceEdit replaceEdit = new ReplaceEdit(match.getOffset(), match.getLength(), newName);
					String editName = RefactoringMessages.getString("RenameComponentProcessor.Component_Refactoring_update_declatation");
					TextChangeCompatibility.addTextEdit(textChange, editName, replaceEdit);
				}
			}
		}
	}

	void addOccurrences(TextChangeManager manager, IProgressMonitor pm, RefactoringStatus status) throws CoreException {

		String fileStr = selectedComponent.getElement().getModel().getBaseLocation();
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileStr));

		String componentName = selectedComponent.getName();
		String componentNamespace = selectedComponent.getNamespaceURI();
		QualifiedName elementQName = new QualifiedName(componentNamespace, componentName);
		QualifiedName typeQName = selectedComponent.getTypeQName();

		SearchEngine searchEngine = new SearchEngine();
        SearchScope scope = null;
        HashMap map = new HashMap();
        if (singleFileOnly)
        {
          map.put("searchDirtyContent", Boolean.TRUE);
          scope = new SelectionSearchScope(new IResource[]{file});
        }
        else
        {  
          scope = new WorkspaceSearchScope();
        } 
		SortingSearchRequestor requestor = new SortingSearchRequestor();
		SearchPattern pattern = new XMLComponentReferencePattern(file, elementQName, typeQName);
        
		searchEngine.search(pattern, requestor, scope, map, new NullProgressMonitor());
		references = requestor.getResults();
		// for (Iterator iter = references.iterator(); iter.hasNext();) {
		// SearchMatch match = (SearchMatch) iter.next();

		// TextChange textChange = manager.get(match.getFile());
		// String newName = getNewElementName();
		// if(match.getObject() instanceof Node){
		// Node node = (Node)match.getObject();
		// if(node instanceof IDOMAttr){
		// IDOMAttr attr = (IDOMAttr)node;
		// IDOMElement element = (IDOMElement)attr.getOwnerElement() ;
		// newName = getNewQName(element, componentNamespace, newName);
		// }
		// newName = quoteString(newName);
		// }
		//				
		// ReplaceEdit replaceEdit = new ReplaceEdit(match.getOffset(),
		// match.getLength(), newName );
		// String editName =
		// RefactoringMessages.getString("RenameComponentProcessor.Component_Refactoring_update_reference");
		// TextChangeCompatibility.addTextEdit(textChange, editName,
		// replaceEdit);

		// }
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.internal.corext.refactoring.tagging.ITextUpdating#canEnableTextUpdating()
	 */
	public boolean canEnableTextUpdating() {
		return true;
	}

	public boolean canEnableUpdateReferences() {
		return true;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#checkFinalConditions(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
	 */
	public RefactoringStatus checkFinalConditions(IProgressMonitor monitor, CheckConditionsContext context) throws CoreException, OperationCanceledException {
		Assert.isNotNull(monitor);
		Assert.isNotNull(context);
		final RefactoringStatus status = new RefactoringStatus();
		try {
			monitor.beginTask("", 2); //$NON-NLS-1$
			monitor.setTaskName(RefactoringMessages.getString("RenameComponentRefactoring_checking")); //$NON-NLS-1$
			status.merge(checkNewElementName(getNewElementName()));
			monitor.worked(1);
			monitor.setTaskName(RefactoringMessages.getString("RenameComponentRefactoring_searching")); //$NON-NLS-1$
			status.merge(createRenameChanges(new SubProgressMonitor(monitor, 1)));
		}
		finally {
			monitor.done();
		}
		return status;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#checkInitialConditions(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		// TODO add code to check initial conditions for component rename
		Assert.isNotNull(pm);
		try {
			return new RefactoringStatus();
		}
		finally {
			pm.done();
		}

	}

	public final RefactoringStatus checkNewElementName(final String name) {
		Assert.isNotNull(name);
		final RefactoringStatus result = Checks.checkName(name);
		result.merge(Checks.checkComponentName(name));
		if (Checks.isAlreadyNamed(selectedComponent, name))
			result.addFatalError(RefactoringMessages.getString("RenameComponentRefactoring_another_name")); //$NON-NLS-1$
		return result;
	}

	private Object[] computeDerivedElements() {

		Object[] elements = getElements();
		// Object[] results = new Object[elements.length];
		// for(int i=0; i< elements.length; i++){
		// RefactoringComponent component = (RefactoringComponent)elements[i];
		// results[i] = component.getAdaptee();
		//			
		// }
		return elements;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#createChange(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		// don't create any change now, all the changes are in changeManger
		// variable and will be combined in postCreateChange method
		return null;
	}

	private TextChangeManager updateChangeManager(IProgressMonitor pm, RefactoringStatus status) throws CoreException {
		TextChangeManager manager = getChangeManager();
		// only one declaration gets updated
		addDeclarationUpdate(manager);
		if (getUpdateReferences()) {
			addOccurrences(manager, pm, status);
		}
		return manager;
	}

	private RefactoringStatus createRenameChanges(final IProgressMonitor monitor) throws CoreException {
		Assert.isNotNull(monitor);
		final RefactoringStatus status = new RefactoringStatus();
		try {
			monitor.beginTask(RefactoringMessages.getString("RenameComponentRefactoring_searching"), 1); //$NON-NLS-1$
			updateChangeManager(new SubProgressMonitor(monitor, 1), status);
		}
		finally {
			monitor.done();
		}
		return status;
	}

	public TextChangeManager getChangeManager() {

		if (changeManager == null) {
			changeManager = new TextChangeManager(false);
		}
		return changeManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xsd.internal.refactoring.rename.XSDRenameProcessor#getAffectedProjectNatures()
	 */
	protected String[] getAffectedProjectNatures() throws CoreException {
		// TODO: find project natures of the files that are going to be
		// refactored
		return new String[]{"org.eclipse.jdt.core.javanature"};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.internal.corext.refactoring.tagging.ITextUpdating#getCurrentElementName()
	 */
	public String getCurrentElementName() {
		//
		return selectedComponent.getName();
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getElements()
	 */
	public Object[] getElements() {
		Object model = selectedComponent.getModelObject();
		if (model != null) {
			return new Object[]{model};
		}
		return new Object[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getIdentifier()
	 */
	public String getIdentifier() {
		return IDENTIFIER;
	}

	public String getNewElementName() {
		return newName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getProcessorName()
	 */
	public String getProcessorName() {
		return RefactoringMessages.getFormattedString("RenameComponentRefactoring.name", //$NON-NLS-1$
					new String[]{selectedComponent.getNamespaceURI() + ":" + selectedComponent.getName(), newName});

	}


	public boolean getUpdateReferences() {
		return updateReferences;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#isApplicable()
	 */
	public boolean isApplicable() throws CoreException {
		if (selectedComponent == null)
			return false;
		// TODO implement isApplicable logic for the named component,
		// verify how it is different from other condition checks
		// if (fNamedComponent.isAnonymous())
		// return false;
		// if (! Checks.isAvailable(fType))
		// return false;
		// if (isSpecialCase(fType))
		// return false;
		return true;
	}

	protected void loadDerivedParticipants(RefactoringStatus status, List result, Object[] derivedElements, ComponentRenameArguments arguments, String[] natures, SharableParticipants shared) throws CoreException {
		if (derivedElements != null) {
			for (int i = 0; i < derivedElements.length; i++) {
				RenameParticipant[] participants = ParticipantManager.loadRenameParticipants(status, this, derivedElements[i], arguments, natures, shared);
				result.addAll(Arrays.asList(participants));
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xsd.internal.refactoring.rename.XSDRenameProcessor#loadDerivedParticipants(org.eclipse.ltk.core.refactoring.RefactoringStatus,
	 *      java.util.List, java.lang.String[],
	 *      org.eclipse.ltk.core.refactoring.participants.SharableParticipants)
	 */
	protected void loadDerivedParticipants(RefactoringStatus status, List result, String[] natures, SharableParticipants shared) throws CoreException {
		ComponentRenameArguments arguments = new ComponentRenameArguments(getNewElementName(), getUpdateReferences());
		arguments.setMatches(references);
		arguments.setQualifier(selectedComponent.getNamespaceURI());
		// pass in changeManger to the participants so that it can collect all
		// changes/per files
		arguments.setChangeManager(getChangeManager());
		loadDerivedParticipants(status, result, computeDerivedElements(), arguments, natures, shared);
	}

	protected void loadElementParticipants(RefactoringStatus status, List result, RenameArguments arguments, String[] natures, SharableParticipants shared) throws CoreException {
		Object[] elements = new Object[0];// getElements();
		for (int i = 0; i < elements.length; i++) {
			result.addAll(Arrays.asList(ParticipantManager.loadRenameParticipants(status, this, elements[i], arguments, natures, shared)));
		}
	}


	public final RefactoringParticipant[] loadParticipants(RefactoringStatus status, SharableParticipants sharedParticipants) throws CoreException {
		RenameArguments arguments = new RenameArguments(getNewElementName(), getUpdateReferences());
		String[] natures = getAffectedProjectNatures();
		List result = new ArrayList();
		loadElementParticipants(status, result, arguments, natures, sharedParticipants);
		loadDerivedParticipants(status, result, natures, sharedParticipants);
		for (Iterator i = result.iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof XMLComponentRenameParticipant) {
				XMLComponentRenameParticipant p = (XMLComponentRenameParticipant) o;
				// getChangeManager()
				p.setChangeManager(getChangeManager());
			}
		}

		return (RefactoringParticipant[]) result.toArray(new RefactoringParticipant[result.size()]);
	}

	public void setNewElementName(String newName) {
		Assert.isNotNull(newName);
		this.newName = newName;
	}

	public void setUpdateReferences(boolean update) {
		updateReferences = update;

	}

	public Change postCreateChange(Change[] participantChanges, IProgressMonitor pm) throws CoreException, OperationCanceledException {
		Assert.isNotNull(pm);
		try {
			String changeName = RefactoringMessages.getString("RenameComponentProcessor.Component_Refactoring_updates");
			TextChange[] changes = changeManager.getAllChanges();
//			Comparator c = new Comparator() {
//				public int compare(Object o1, Object o2) {
//					TextFileChange c1 = (TextFileChange) o1;
//					TextFileChange c2 = (TextFileChange) o2;
//					return Collator.getInstance().compare(c1.getFile().getFullPath(), c2.getFile().getFullPath());
//				}
//			};
			if (changes.length > 0) {
				// Arrays.sort(changes, c);
				CompositeChange compositeChange = new CompositeChange("!" + changeName, changes);
				compositeChange.markAsSynthetic();
				return compositeChange;
			}
			else {
				return null;
			}

		}
		finally {
			pm.done();
		}
	}



}
