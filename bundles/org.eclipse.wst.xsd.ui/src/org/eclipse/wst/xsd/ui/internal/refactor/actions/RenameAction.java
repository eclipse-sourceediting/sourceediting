
package org.eclipse.wst.xsd.ui.internal.refactor.actions;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;
import org.eclipse.xsd.XSDSchema;


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

	private RenameComponentAction fRenameXSDElement;
	private RenameResourceAction fRenameResource;
	
	
	public RenameAction(ISelectionProvider selectionProvider, XSDSchema schema) {
		super(selectionProvider);
		setText(RefactoringMessages.getString("RenameAction.text")); //$NON-NLS-1$
		fRenameXSDElement= new RenameComponentAction(selectionProvider, schema);
		fRenameXSDElement.setText(getText());
		fRenameResource= new RenameResourceAction(selectionProvider);
		fRenameResource.setText(getText());
		
	}
	
	/*
	 * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		fRenameXSDElement.selectionChanged(event);
		if (fRenameResource != null)
			fRenameResource.selectionChanged(event);
		setEnabled(computeEnabledState());		
	}

	/*
	 * @see SelectionDispatchAction#update(ISelection)
	 */
	public void update(ISelection selection) {
		if(fRenameXSDElement != null){
			fRenameXSDElement.update(selection);
		}
		if (fRenameResource != null)
			fRenameResource.update(selection);
		setEnabled(computeEnabledState());		
	}
	
	private boolean computeEnabledState(){
		if (fRenameResource != null) {
			return fRenameXSDElement.isEnabled() || fRenameResource.isEnabled();
		} else {
			return fRenameXSDElement.isEnabled();
		}
	}
	
	public void run(IStructuredSelection selection) {
		if (fRenameXSDElement.isEnabled())
			fRenameXSDElement.run(selection);
		if (fRenameResource != null && fRenameResource.isEnabled())
			fRenameResource.run(selection);
	}

	public void run(ITextSelection selection) {
		if (fRenameXSDElement.canRun())
			fRenameXSDElement.run(selection);
		else
			MessageDialog.openInformation(XSDEditorPlugin.getShell(), RefactoringMessages.getString("RenameAction.rename"), RefactoringMessages.getString("RenameAction.unavailable"));  //$NON-NLS-1$ //$NON-NLS-2$
	}
	public void run(ISelection selection) {
	    if(selection == null){
	    	super.run();
	    }
	    else{
	    	super.run(selection);
	    }
		
	}
}
