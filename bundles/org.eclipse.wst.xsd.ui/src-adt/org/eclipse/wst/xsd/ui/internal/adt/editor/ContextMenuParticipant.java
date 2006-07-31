package org.eclipse.wst.xsd.ui.internal.adt.editor;

import org.eclipse.jface.action.IMenuManager;

public class ContextMenuParticipant
{  
  public boolean isApplicable(Object object, String actionId)
  {
    return true;
  }
  
  public void contributeActions(Object object, IMenuManager menu)
  {    
  }
}
