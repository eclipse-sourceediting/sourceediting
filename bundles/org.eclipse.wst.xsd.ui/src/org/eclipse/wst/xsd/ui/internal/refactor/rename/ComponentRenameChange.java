/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsd.ui.internal.refactor.rename;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.text.Assert;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDSwitch;


/**
 * @author ebelisar
 *
 */
public class ComponentRenameChange extends Change {
	
	private Map fChanges;
	private String fNewName;

	private String fOldName;

	private XSDNamedComponent fNamedComponent;

	public ComponentRenameChange(XSDNamedComponent component, String oldName, String newName) {
		Assert.isNotNull(newName, "new name"); //$NON-NLS-1$
		Assert.isNotNull(oldName, "old name"); //$NON-NLS-1$

		fNamedComponent = component;
		fOldName= oldName;
		fNewName= newName;
	}
	
//	public static Change[] createChangesFor(XSDNamedComponent component, String newName) {
//		// TODO: P1 implement search of XSD files	
//		XSDSearchSupport support = XSDSearchSupport.getInstance();
//		RefactorSearchRequestor requestor = new RefactorSearchRequestor(component, newName);
//		support.searchRunnable(component, IXSDSearchConstants.WORKSPACE_SCOPE, requestor);
//
//		return requestor.getChanges();
//
//	}

	protected Change createUndoChange() {
		return new ComponentRenameChange(fNamedComponent, getNewName(), getOldName());
	}
	
	protected void doRename(IProgressMonitor pm) throws CoreException {
		// TODO P1 change temporary rename of XSD model components 
		performModify(getNewName());
	}
	
	public void performModify(final String value)
	  {
	    if (value.length() > 0)
	    {
	      DelayedRenameRunnable runnable = new DelayedRenameRunnable(fNamedComponent, value);
	      Display.getCurrent().asyncExec(runnable);
	    }
	  }      

	  protected static class DelayedRenameRunnable implements Runnable
	  {
	    protected XSDNamedComponent component;
	    protected String name;

	    public DelayedRenameRunnable(XSDNamedComponent component, String name)
	    {                                                               
	      this.component = component;
	      this.name = name;
	    }                                                              

	    public void run()
	    {                      
	      component.updateElement(true);
	      XSDSwitch xsdSwitch = new XSDSwitch()
	      {                   
	        public Object caseXSDTypeDefinition(XSDTypeDefinition object)
	        {
	          new GlobalSimpleOrComplexTypeRenamer(object, name).visitSchema(object.getSchema());
	          return null;
	        } 
	      
	        public Object caseXSDElementDeclaration(XSDElementDeclaration object)
	        {           
	          if (object.isGlobal())
	          {
	            new GlobalElementRenamer(object, name).visitSchema(object.getSchema());
	          }
	          return null;
	        }
	      
	        public Object caseXSDModelGroupDefinition(XSDModelGroupDefinition object)
	        {
	          new GlobalGroupRenamer(object, name).visitSchema(object.getSchema());
	          return null;
	        }
	      };
	      xsdSwitch.doSwitch(component); 
	      component.setName(name);
	     
	    }
	  }
	
	public TextChange getChange(IFile file) {
		TextChange result= (TextChange)fChanges.get(file);
		if (result == null) {
			result= new TextFileChange(file.getName(), file);
			fChanges.put(file, result);
		}
		return result;
	}
	
	public String getName() {
		return RefactoringMessages.getFormattedString("XSDComponentRenameChange.name", new String[]{getOldName(), getNewName()}); //$NON-NLS-1$
	}

	public final Change perform(IProgressMonitor pm) throws CoreException {
		try {
			pm.beginTask(RefactoringMessages.getString("XSDComponentRenameChange.Renaming"), 1); //$NON-NLS-1$
			Change result= createUndoChange();
			doRename(new SubProgressMonitor(pm, 1));
			return result;
		} finally {
			pm.done();
		}
	}

	/**
	 * Gets the newName.
	 * 
	 * @return Returns a String
	 */
	protected String getNewName() {
		return fNewName;
	}

	/**
	 * Gets the oldName
	 * 
	 * @return Returns a String
	 */
	protected String getOldName() {
		return fOldName;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#getModifiedElement()
	 */
	public Object getModifiedElement() {
		// TODO Auto-generated method stub
		return fNamedComponent;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#initializeValidationData(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void initializeValidationData(IProgressMonitor pm) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#isValid(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		// TODO implement change validation
		return new RefactoringStatus();
	}
}
