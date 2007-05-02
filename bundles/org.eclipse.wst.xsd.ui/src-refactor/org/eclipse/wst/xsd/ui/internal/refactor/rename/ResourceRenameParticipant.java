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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;
import org.eclipse.wst.xsd.ui.internal.refactor.TextChangeManager;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;

/**
 * This rename participant creates text changes for the references of the XSD and WSDL files
 */
public class ResourceRenameParticipant extends RenameParticipant {
	
//	private IFile file = null;
	private TextChangeManager changeManager;

	
	private static String XSD_CONTENT_TYPE_ID = "org.eclipse.wst.xsd.core.xsdsource";
	private static String WSDL_CONTENT_TYPE_ID = "org.eclipse.wst.wsdl.wsdlsource";


	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#initialize(java.lang.Object)
	 */
	protected boolean initialize(Object element) {
		if(element instanceof IFile) {
			// check if file has XSD or WSDL content
			IFile aFile = (IFile) element;
			try {
				IContentDescription description = aFile.getContentDescription();
				if ( description == null )
  				return false;
				IContentType contentType = description.getContentType();
				if(contentType != null){
					if(XSD_CONTENT_TYPE_ID.equals(contentType.getId()) ||
							WSDL_CONTENT_TYPE_ID.equals(contentType.getId())){
//						file = aFile;
						return true;
					}
				}
			} catch (CoreException e) {
				return false;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#getName()
	 */
	public String getName() {
		return RefactoringMessages.getString("ResourceRenameParticipant.compositeChangeName");
	}
	
//	private IPath getNewFilePath() {
//		
//		IPath oldPath = file.getRawLocation();
//		IPath newPath = oldPath.removeLastSegments(1).append(getArguments().getNewName());
//		return newPath;
//	}

	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException
	{
		RefactoringStatus result = new RefactoringStatus();
		try
		{
			pm.beginTask("", 9); //$NON-NLS-1$
			changeManager = createChangeManager(new SubProgressMonitor(pm, 1),
					result);
			
		} catch(CoreException e){
			result.addFatalError(e.toString());
		}
		finally
		{
			pm.done();
		}
		return result;

	}
	
	

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException
	{
		try
		{
			String changeName = RefactoringMessages.getString("RenameResourceChange.rename_resource_reference_change");
			TextChange[] changes =  changeManager.getAllChanges();
			if(changes.length > 0){
				return new CompositeChange(changeName, changes);
			}
			else{
				return null;
			}
			
		} finally
		{
			pm.done();
		}

	}
	
	
	private TextChangeManager createChangeManager(IProgressMonitor pm,
			RefactoringStatus status) throws CoreException
	{
		TextChangeManager manager = new TextChangeManager(false);
		// only one declaration gets updated
		//addDeclarationUpdate(manager);
		if (getArguments().getUpdateReferences())
			addOccurrences(manager, pm, status);
		return manager;
	}



	void addOccurrences(TextChangeManager manager, IProgressMonitor pm,
			RefactoringStatus status) throws CoreException
	{
//		
//		Object[] occurrences = SearchTools.getFileDependencies(file);
//		pm.beginTask("", occurrences.length); //$NON-NLS-1$
//		
//		for (int i = 0; i < occurrences.length; i++)
//		{
//			Object object = occurrences[i];
//
//			if (object instanceof SearchResultGroup)
//			{
//				SearchResultGroup searchResult = (SearchResultGroup) object;
//				if (searchResult == null)
//					continue;
//				
//				IFile referencingFile = (IFile)searchResult.getResource();
//					
//				resourceSet = new ResourceSetImpl();
//				// for each result file create XSD model and get component from that model
//				resourceSet.getAdapterFactories().add(
//						new XSDSchemaLocationResolverAdapterFactory());
//				URI uri = URI.createFileURI(referencingFile.getLocation().toPortableString());
//				try
//				{
//					XSDSchema schema = XSDFactory.eINSTANCE.createXSDSchema();
//					IStructuredModel structuredModel = StructuredModelManager.getModelManager().getModelForRead(referencingFile);
//					IDOMModel domModel = (IDOMModel) structuredModel;
//					Resource resource = new XSDResourceImpl();
//					resource.setURI(uri);
//					schema = XSDFactory.eINSTANCE.createXSDSchema();
//					resource.getContents().add(schema);
//					resourceSet.getResources().add(resource);
//					schema.setElement(domModel.getDocument().getDocumentElement());
//					// get target namespace 
//					String stringPath = file.getLocation().toString();
//					String targetNamespace = XMLQuickScan.getTargetNamespace(stringPath);
//					targetNamespace = targetNamespace == null ? "" : targetNamespace;
//
//					List textEdits = new ArrayList();
//					SearchMatch[] matches = searchResult.getSearchResults();
//					
//					for (int j = 0; j < matches.length; j++) {
//						SearchMatch match = matches[j];
//						
//						FileReferenceRenamer renamer = new FileReferenceRenamer(
//								match.getAttrValue(), targetNamespace, getNewFilePath().toString(), schema);
//						renamer.visitSchema(schema);
//					    textEdits.addAll(renamer.getTextEdits());
//					}
//					
//					
//					if(!textEdits.isEmpty()){
//						TextChange textChange = manager.get(referencingFile);
//						for (int j = 0; j < textEdits.size(); j++)
//						{
//							ReplaceEdit replaceEdit = (ReplaceEdit) textEdits
//									.get(j);
//							String editName = RefactoringMessages.getString("ResourceRenameParticipant.File_Rename_update_reference");
//							TextChangeCompatibility.addTextEdit(textChange,
//									editName, replaceEdit);
//						}
//					}
//					
//				} catch (Exception e)
//				{
//					e.printStackTrace();
//				} finally
//				{
//
//				}
//			}
//		}
	}
	
	
	public  class ReferenceLocationFinder
	{
		protected XSDNamedComponent component;
		protected String name;
		protected XSDSchema referencingSchema;
		protected List results = new ArrayList();

		public ReferenceLocationFinder(XSDNamedComponent component,
				String name, XSDSchema referencingSchema)
		{
			this.component = component;
			this.name = name;
			this.referencingSchema = referencingSchema;
		}

		public void run()
		{
			
			//XSDSwitch xsdSwitch = new XSDSwitch()
//			{
//				public Object caseXSDTypeDefinition(XSDTypeDefinition object)
//				{
//					GlobalTypeReferenceRenamer renamer = new GlobalTypeReferenceRenamer(
//							object.getName(), object.getTargetNamespace(), name, referencingSchema);
//					renamer.visitSchema(referencingSchema);
//					results.addAll(renamer.getTextEdits());
//					return null;
//				}
//
//				public Object caseXSDElementDeclaration(
//						XSDElementDeclaration object)
//				{
//					if (object.isGlobal())
//					{
//						GlobalElementRenamer renamer = new GlobalElementRenamer(
//								object.getName(), object.getTargetNamespace(), name, referencingSchema);
//						renamer.visitSchema(referencingSchema);
//						results.addAll(renamer.getTextEdits());
//					}
//					return null;
//				}
//
//				public Object caseXSDModelGroupDefinition(
//						XSDModelGroupDefinition object)
//				{
//					GlobalGroupRenamer renamer = new GlobalGroupRenamer(
//							object.getName(), object.getTargetNamespace(), name, referencingSchema);
//					renamer.visitSchema(referencingSchema);
//					return null;
//				}
//			};
			//xsdSwitch.doSwitch(component);
//			component.setName(name);
//			try
//			{
//				referencingSchema.eResource().save(new HashMap());
//			} catch (IOException e)
//			{
//				e.printStackTrace();
//			}

		}

		public final List getResults()
		{
			return results;
		}
	}
	
	



}
