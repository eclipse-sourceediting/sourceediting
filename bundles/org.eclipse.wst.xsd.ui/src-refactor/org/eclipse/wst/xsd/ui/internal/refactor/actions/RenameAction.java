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
package org.eclipse.wst.xsd.ui.internal.refactor.actions;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.refactor.wizard.RefactoringWizardMessages;



/**
* Renames a XML Schema element or workbench resource.
* <p>
* Action is applicable to selections containing elements of type
* <code></code> or <code>IResource</code>.
* 
* <p>
* This class may be instantiated; it is not intended to be subclassed.
* </p>

*/
public class RenameAction extends SelectionDispatchAction  {

	private SelectionDispatchAction renameComponentAction;
	private SelectionDispatchAction renameResourceAction;
	
	
	public RenameAction(ISelection selection) {
		super(selection);
		setText(RefactoringWizardMessages.RenameAction_text); 
		renameResourceAction= new RenameResourceAction(selection);
		renameResourceAction.setText(getText());
		
	}
	public RenameAction(ISelection selection, Object model) {
		super(selection);
		setText(RefactoringWizardMessages.RenameAction_text);
		renameComponentAction= new RenameComponentAction(selection, model);
		renameComponentAction.setText(getText());
		renameResourceAction= new RenameResourceAction(selection);
		renameResourceAction.setText(getText());
		
	}
	

	
	/*
	 * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		renameComponentAction.selectionChanged(event);
		if (renameResourceAction != null)
			renameResourceAction.selectionChanged(event);
		setEnabled(computeEnabledState());		
	}

	/*
	 * @see SelectionDispatchAction#update(ISelection)
	 */
	public void update(ISelection selection) {
		if(renameComponentAction != null){
			renameComponentAction.update(selection);
		}
		if (renameResourceAction != null)
			renameResourceAction.update(selection);
		setEnabled(computeEnabledState());		
	}
	
	private boolean computeEnabledState(){
		if (renameResourceAction != null) {
			return renameComponentAction.isEnabled() || renameResourceAction.isEnabled();
		} else {
			return renameComponentAction.isEnabled();
		}
	}
	
	public void run(IStructuredSelection selection) {
		if (renameComponentAction != null && renameComponentAction.isEnabled())
			renameComponentAction.run(selection);
		if (renameResourceAction != null && renameResourceAction.isEnabled())
			renameResourceAction.run(selection);
	}

	public void run(ITextSelection selection) {
		if (renameComponentAction != null && renameComponentAction.canRun())
			renameComponentAction.run(selection);
		else
			MessageDialog.openInformation(XSDEditorPlugin.getShell(), RefactoringWizardMessages.RenameAction_rename, RefactoringWizardMessages.RenameAction_unavailable);  
	}
	public void run(ISelection selection) {
	    if(selection == null){
	    	super.run();
	    }
	    else{
	    	super.run(selection);
	    }
		
	}
	public final void setRenameComponentAction(
			SelectionDispatchAction renameComponentAction)
	{
		this.renameComponentAction = renameComponentAction;
	}
}
