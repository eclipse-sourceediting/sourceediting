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
package org.eclipse.wst.xsd.ui.internal.refactor.rename;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.common.util.URI;
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
import org.eclipse.wst.xsd.ui.internal.refactor.INameUpdating;
import org.eclipse.wst.xsd.ui.internal.refactor.IReferenceUpdating;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringComponent;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;
import org.eclipse.wst.xsd.ui.internal.refactor.TextChangeManager;
import org.eclipse.xsd.XSDSchema;


public class RenameTargetNamespaceProcessor extends RenameProcessor implements INameUpdating, IReferenceUpdating
{
	private String newNamespace;
	private boolean updateReferences = true;
	private TextChangeManager changeManager;
	private XSDSchema model;


	public static final String IDENTIFIER = "org.eclipse.wst.ui.xsd.renameComponentProcessor"; //$NON-NLS-1$

	public RenameTargetNamespaceProcessor(XSDSchema model, String newName)
	{
		this.model = model;
		this.newNamespace = newName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.internal.corext.refactoring.tagging.ITextUpdating#canEnableTextUpdating()
	 */
	public boolean canEnableTextUpdating()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.internal.corext.refactoring.tagging.ITextUpdating#getCurrentElementName()
	 */
	public String getCurrentElementName()
	{
		//
		return model.getTargetNamespace();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xsd.internal.refactoring.rename.XSDRenameProcessor#getAffectedProjectNatures()
	 */
	protected String[] getAffectedProjectNatures() throws CoreException
	{
		// TODO: find project natures of the files that are going to be
		// refactored
		return new String[]{"org.eclipse.jdt.core.javanature"};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xsd.internal.refactoring.rename.XSDRenameProcessor#loadDerivedParticipants(org.eclipse.ltk.core.refactoring.RefactoringStatus,
	 *      java.util.List, java.lang.String[],
	 *      org.eclipse.ltk.core.refactoring.participants.SharableParticipants)
	 */
	protected void loadDerivedParticipants(RefactoringStatus status,
			List result, String[] natures, SharableParticipants shared)
			throws CoreException
	{
		String newCUName= getNewElementName(); //$NON-NLS-1$
		RenameArguments arguments= new RenameArguments(newCUName, getUpdateReferences());
		loadDerivedParticipants(status, result, 
			computeDerivedElements(), arguments, 
			 natures, shared);
	}
	
	protected void loadDerivedParticipants(RefactoringStatus status, List result, Object[] derivedElements, 
			RenameArguments arguments,  String[] natures, SharableParticipants shared) throws CoreException {
		if (derivedElements != null) {
			for (int i= 0; i < derivedElements.length; i++) {
				RenameParticipant[] participants= ParticipantManager.loadRenameParticipants(status, 
					this, derivedElements[i], 
					arguments, natures, shared);
				result.addAll(Arrays.asList(participants));
			}
		}
		
	}
	
	private Object[] computeDerivedElements() {

		Object[] elements = getElements();
		Object[] results = new Object[elements.length];
		for(int i=0; i< elements.length; i++){
			RefactoringComponent component = (RefactoringComponent)elements[i];
			results[i] = component.getModelObject();
			
		}
		return results;
		
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#checkFinalConditions(org.eclipse.core.runtime.IProgressMonitor,
	 *      org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
	 */
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws CoreException,
			OperationCanceledException
	{
		try
		{
			RefactoringStatus result = new RefactoringStatus();
			pm.beginTask("", 9); //$NON-NLS-1$
			changeManager = createChangeManager(new SubProgressMonitor(pm, 1),
					result);
			return result;
		} finally
		{
			pm.done();
		}
		

	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#checkInitialConditions(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException
	{
		// TODO add code to check initial conditions for component rename
		return new RefactoringStatus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#createChange(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException
	{
		try
		{
			String changeName = RefactoringMessages.getString("RenameComponentProcessor.Component_Refactoring_updates");
			return new CompositeChange(changeName, changeManager.getAllChanges());
		} finally
		{
			pm.done();
		}

		// Change[] changes = ComponentRenameChange.createChangesFor(
		// this.fNamedComponent, getNewElementName());
		//
		// if (changes.length > 0)
		// {
		// CompositeChange multiChange = null;
		// multiChange = new CompositeChange(
		// "XSD component rename participant changes", changes); //$NON-NLS-1$
		// TODO: externalize string
		// return multiChange;
		// } else
		// {
		//
		// return new ComponentRenameChange(
		// fNamedComponent,
		// fNamedComponent.getName(),
		// getNewElementName(),
		// fNamedComponent.getSchema());
		// }
		
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getElements()
	 */
	public Object[] getElements()
	{
		return new Object[] { model };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getIdentifier()
	 */
	public String getIdentifier()
	{
		return IDENTIFIER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getProcessorName()
	 */
	public String getProcessorName()
	{
		return RefactoringMessages.getFormattedString(
				"RenameComponentRefactoring.name", //$NON-NLS-1$
				new String[]
				{
						getCurrentElementName(),
						getNewElementName() });

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#isApplicable()
	 */
	public boolean isApplicable() throws CoreException
	{
		if (getModel() == null)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.internal.corext.refactoring.tagging.INameUpdating#checkNewElementName(java.lang.String)
	 */
	public RefactoringStatus checkNewElementName(String newName)
	{
		Assert.isNotNull(newName, "new name"); //$NON-NLS-1$
		// TODO: implement new name checking
		// RefactoringStatus result = Checks.checkTypeName(newName);
		// if (Checks.isAlreadyNamed(fType, newName))
		// result.addFatalError(RefactoringCoreMessages.getString("RenameTypeRefactoring.choose_another_name"));
		// //$NON-NLS-1$
		return new RefactoringStatus();
	}

	public final RefactoringParticipant[] loadParticipants(
			RefactoringStatus status, SharableParticipants sharedParticipants)
			throws CoreException
	{
		RenameArguments arguments = new RenameArguments(getNewElementName(),
				true);
		String[] natures = getAffectedProjectNatures();
		List result = new ArrayList();
		loadElementParticipants(status, result, arguments, natures,
				sharedParticipants);
		loadDerivedParticipants(status, result, natures, sharedParticipants);
		return (RefactoringParticipant[]) result
				.toArray(new RefactoringParticipant[result.size()]);
	}

	protected void loadElementParticipants(RefactoringStatus status,
			List result, RenameArguments arguments, String[] natures,
			SharableParticipants shared) throws CoreException
	{
		Object[] elements = getElements();
		for (int i = 0; i < elements.length; i++)
		{
			result.addAll(Arrays.asList(ParticipantManager
					.loadRenameParticipants(status, this, elements[i],
							arguments, natures, shared)));
		}
	}
	
	private TextChangeManager createChangeManager(IProgressMonitor pm,
			RefactoringStatus status) throws CoreException
	{
		TextChangeManager manager = new TextChangeManager(false);
		// only one declaration gets updated
		addDeclarationUpdate(manager);
		return manager;
	}

	private void addDeclarationUpdate(TextChangeManager manager)

	{
		String fileStr = getModel().getSchemaLocation();
		URI uri = URI.createPlatformResourceURI(fileStr);
		try
		{
			URL url = new URL(uri.toString());
			url = Platform.resolve(url);
			if(url != null){
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				IFile file = root.getFileForLocation(new Path(url.getFile()));
				if(file != null ){
					TextChange change = manager.get(file);
					addDeclarationUpdate(change);
				}
			}
		} catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	

	final void addDeclarationUpdate(TextChange change) 
	{
//		String editName = RefactoringMessages.getString("RenameComponentProcessor.Component_Refactoring_update_declatation");;
//	  
//		NamedComponentRenamer renamer = new NamedComponentRenamer(
//				selectedComponent.getElement(), newNamespace);
//		renamer.renameComponent();
//		List textEdits = renamer.getTextEdits();
//		for (int j = 0; j < textEdits.size(); j++)
//		{
//			ReplaceEdit replaceEdit = (ReplaceEdit) textEdits
//					.get(j);
//			TextChangeCompatibility.addTextEdit(change,
//					editName, replaceEdit);
//		}
	}

	public void setNewElementName(String newName)
	{
		this.newNamespace = newName;
	}

	public String getNewElementName()
	{
		return newNamespace;
	}

	

	public boolean canEnableUpdateReferences()
	{
		return true;
	}

	public boolean getUpdateReferences()
	{
		return updateReferences;
	}

	public void setUpdateReferences(boolean update)
	{
		updateReferences = update;
		
	}

	public final TextChangeManager getChangeManager()
	{
		return changeManager;
	}

	public final XSDSchema getModel()
	{
		return model;
	}
	
}
