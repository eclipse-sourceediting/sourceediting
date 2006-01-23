package org.eclipse.wst.xsd.ui.internal.search.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorPart;

public class FindAction extends Action implements ISelectionChangedListener
{
  IEditorPart editor;
  
  protected FindAction(IEditorPart editor)
  {
    this.editor = editor;
  }
  
  public void selectionChanged(SelectionChangedEvent event)
  {
    // TODO Auto-generated method stub
    
  }
}
