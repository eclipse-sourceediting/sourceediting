
package org.eclipse.wst.xsd.ui.internal.refactor.actions;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.wst.xsd.ui.internal.XSDEditor;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringMessages;


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
	private XSDEditor fEditor;
		
	/**
	 * Creates a new <code>RenameAction</code>. The action requires
	 * that the selection provided by the site's selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site the site providing context information for this action
	 */
	public RenameAction(IWorkbenchSite site) {
		super(site);
		setText(RefactoringMessages.getString("RenameAction.text")); //$NON-NLS-1$
		fRenameXSDElement= new RenameComponentAction(site);
		fRenameXSDElement.setText(getText());
		fRenameResource= new RenameResourceAction(site);
		fRenameResource.setText(getText());
		IEditorPart editorPart = site.getPage().getActiveEditor();
		if(editorPart instanceof XSDEditor){
			fEditor = (XSDEditor)editorPart;
		}
		//TODO set help context ids
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
			MessageDialog.openInformation(fEditor.getEditorSite().getShell(), RefactoringMessages.getString("RenameAction.rename"), RefactoringMessages.getString("RenameAction.unavailable"));  //$NON-NLS-1$ //$NON-NLS-2$
	}
	public void run(ISelection selection) {
	    if(selection == null){
	    	ISelection editorSelection = fEditor.getSelectionManager().getSelection();
	    	super.dispatchRun(editorSelection);
	    }
	    else{
	    	super.run(selection);
	    }
		
	}
}
