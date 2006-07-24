/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.actions;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.DirectEditAction;
import org.eclipse.gef.ui.parts.AbstractEditPartViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;


public class BaseDirectEditAction extends DirectEditAction {
	protected ISelectionProvider provider;
	
	/**
	 * Same as {@link #DirectEditAction(IWorkbenchPart)}.
	 * @param editor the editor
	 */
	public BaseDirectEditAction(IEditorPart editor) {
		super((IWorkbenchPart)editor);
	}

	/**
	 * Constructs a DirectEditAction using the specified part.
	 * @param part the workbench part
	 */
	public BaseDirectEditAction(IWorkbenchPart part) {
		super(part);
	}
	
	  /* (non-Javadoc)
	   * @see org.eclipse.gef.ui.actions.SelectionAction#getSelection()
	   */
	  protected ISelection getSelection()
	  {
	    // always get selection from selection provider first
		  if (provider!=null) {
			  Object selection = provider.getSelection();
			  if (selection instanceof StructuredSelection) {
				  Object object = ((StructuredSelection) selection).getFirstElement();
				  if (object instanceof XSDBaseAdapter) {
					  // We need to return an EditPart as the selection.
					  IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
					  Object graphicalViewer = editor.getAdapter(GraphicalViewer.class);
					  if (graphicalViewer instanceof AbstractEditPartViewer) {
						  AbstractEditPartViewer viewer = (AbstractEditPartViewer) graphicalViewer;
						  EditPart editPart = (EditPart)viewer.getEditPartRegistry().get(object);
						  return new StructuredSelection(editPart);
					  }
				  }
			  }
	    }
	    
	    return super.getSelection();
	  }
	
	  protected boolean calculateEnabled() {
		  Object selection = ((IStructuredSelection) getSelection()).getFirstElement();

		  if (selection instanceof XSDBaseAdapter) {
			  return  !((XSDBaseAdapter) selection).isReadOnly();
		  }
		  
		  return true;
	  }
	  
	  /* (non-Javadoc)
	   * @see org.eclipse.gef.ui.actions.SelectionAction#setSelectionProvider(org.eclipse.jface.viewers.ISelectionProvider)
	   */
	  public void setSelectionProvider(ISelectionProvider provider)
	  {
	    super.setSelectionProvider(provider);
	    this.provider = provider;
	  }
}
