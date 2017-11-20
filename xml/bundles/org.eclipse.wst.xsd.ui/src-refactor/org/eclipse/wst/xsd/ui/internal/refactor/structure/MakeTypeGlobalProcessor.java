/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor.structure;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.ParticipantManager;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.eclipse.ltk.core.refactoring.participants.RenameProcessor;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.eclipse.wst.xsd.ui.internal.refactor.INameUpdating;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;
import org.eclipse.xsd.XSDTypeDefinition;

public class MakeTypeGlobalProcessor extends RenameProcessor implements INameUpdating{
	
	private XSDTypeDefinition fTypeComponent;
	private String fNewElementName;

	public static final String IDENTIFIER= "org.eclipse.wst.ui.xsd.makeTypeGlobalProcessor"; //$NON-NLS-1$

	//private QualifiedNameSearchResult fNameSearchResult;
	
	public MakeTypeGlobalProcessor(XSDTypeDefinition element, String newName) {
		fTypeComponent= element;
		fNewElementName = newName;
		
	}
	
	public XSDTypeDefinition getTypeComponent() {
		return fTypeComponent;
	}

	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.tagging.ITextUpdating#canEnableTextUpdating()
	 */
	public boolean canEnableTextUpdating() {
		return true;
	}

	protected String[] getAffectedProjectNatures() throws CoreException {
		//TODO: find project natures of the files that are going to be refactored
		return new String[0];
	}
	
	protected void loadDerivedParticipants(RefactoringStatus status,
			List result, String[] natures, SharableParticipants shared)
			throws CoreException {
		// TODO: provide a way to load rename participants
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#checkFinalConditions(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
	 */
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws CoreException,
			OperationCanceledException {
		// TODO add code to check final conditions for component rename
		return new RefactoringStatus();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#checkInitialConditions(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
//		 TODO add code to check initial conditions for component rename
		return new RefactoringStatus();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#createChange(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		// TODO P1 add change creation
//		Change[] changes = XSDComponentRenameChange.createChangesFor(this.fNamedComponent, getNewElementName());
//		CompositeChange multiChange = null; 
//			if(changes.length > 0)
//				multiChange  = new CompositeChange("XSD component rename participant changes", changes); //$NON-NLS-1$ TODO: externalize string
//		return multiChange;
		
//		computeNameMatches(pm);	
//		Change[] changes = fNameSearchResult.getAllChanges();
//		return new CompositeChange("XSD file rename participant changes", changes); //TODO: externalize string
		pm.beginTask("", 1); //$NON-NLS-1$
		try{
			return new MakeTypeGlobalChange(fTypeComponent, getNewElementName());
		} finally{
			pm.done();
		}	 
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getElements()
	 */
	public Object[] getElements() {
		
		return new Object[] {fTypeComponent};
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getIdentifier()
	 */
	public String getIdentifier() {
		return IDENTIFIER;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getProcessorName()
	 */
	public String getProcessorName() {
		return RefactoringMessages.getFormattedString(
				"MakeLocalTypeGlobalRefactoring.name",  //$NON-NLS-1$
				new String[]{getNewElementName()});

	}
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#isApplicable()
	 */
	public boolean isApplicable() throws CoreException {
		if (fTypeComponent == null)
			return false;
		// TODO implement isApplicable logic for the named component, 
		// verify how it is different from other condition checks
//		if (fNamedComponent.isAnonymous())
//			return false;
//		if (! Checks.isAvailable(fType))
//			return false;
//		if (isSpecialCase(fType))
//			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.tagging.INameUpdating#checkNewElementName(java.lang.String)
	 */
	public RefactoringStatus checkNewElementName(String newName){
		Assert.isNotNull(newName, "new name"); //$NON-NLS-1$
		// TODO: implement new name checking
//		RefactoringStatus result = Checks.checkTypeName(newName);
//		if (Checks.isAlreadyNamed(fType, newName))
//			result.addFatalError(RefactoringCoreMessages.getString("RenameTypeRefactoring.choose_another_name"));	 //$NON-NLS-1$
		return new RefactoringStatus();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.tagging.INameUpdating#getNewElement()
	 */
	public Object getNewElement() throws CoreException {
		// TODO implement this method, it's used for updating selection on new element
		return null;
	}
	
//	private void computeNameMatches(IProgressMonitor pm) throws CoreException {
//	
//	    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
//	    try {
//			URL fileURL = Platform.resolve(new URL(fNamedComponent.getSchema().getSchemaLocation()));
//			IFile file = workspaceRoot.getFileForLocation(new Path(fileURL.getPath()));
//			if (fNameSearchResult == null)
//				fNameSearchResult= new QualifiedNameSearchResult();
//			QualifiedNameFinder.process(fNameSearchResult, getNamedComponent().getName(),  
//				getNewElementName(), 
//				"*.xsd", file.getProject(), pm);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public final RefactoringParticipant[] loadParticipants(RefactoringStatus status, SharableParticipants sharedParticipants) throws CoreException {
		RenameArguments arguments= new RenameArguments(getNewElementName(), true);
		String[] natures= getAffectedProjectNatures();
		List result= new ArrayList();
		loadElementParticipants(status, result, arguments, natures, sharedParticipants);
		loadDerivedParticipants(status, result, natures, sharedParticipants);
		return (RefactoringParticipant[])result.toArray(new RefactoringParticipant[result.size()]);
	}
	
	protected void loadElementParticipants(RefactoringStatus status, List result, RenameArguments arguments, String[] natures, SharableParticipants shared) throws CoreException {
		Object[] elements= getElements();
		for (int i= 0; i < elements.length; i++) {
			result.addAll(Arrays.asList(ParticipantManager.loadRenameParticipants(status, 
				this,  elements[i],
				arguments, natures, shared)));
		}
	}
	
	
	public void setNewElementName(String newName) {
		
		fNewElementName= newName;
	}

	public String getNewElementName() {
		return fNewElementName;
	}

	public String getCurrentElementName() {
		// TODO Auto-generated method stub
		return fNewElementName;
	}
	
	
}
