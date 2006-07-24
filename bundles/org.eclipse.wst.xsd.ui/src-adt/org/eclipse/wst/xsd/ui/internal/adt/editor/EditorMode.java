package org.eclipse.wst.xsd.ui.internal.adt.editor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.jface.viewers.IContentProvider;

public abstract class EditorMode implements IAdaptable
{
  public abstract String getId();
  
  public abstract String getDisplayName();
  
  public abstract EditPartFactory getEditPartFactory();
  
  public abstract IContentProvider getOutlineProvider();
  
  public ContextMenuParticipant getContextMenuParticipant()
  {
    return null;
  }
  
  public Object getAdapter(Class adapter)
  {
    return null;
  }
}
