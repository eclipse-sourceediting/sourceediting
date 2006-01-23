package org.eclipse.wst.xsd.ui.internal.search.actions;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;

//org.eclipse.wst.xsd.ui.internal.search.actions.XSDSearchGroupActionDelegate
public class XSDSearchReferencesGroupActionDelegate extends BaseGroupActionDelegate
{
    protected void fillMenu(Menu menu) {
      try
      {
        if (fSelection == null) {
            return;
        }  
        if (workbenchPart instanceof IEditorPart)
        {            
          ReferencesSearchGroup referencesGroup = new ReferencesSearchGroup((IEditorPart)workbenchPart);
          XSDSearchGroupSubMenu subMenu = new XSDSearchGroupSubMenu(referencesGroup);
          subMenu.fill(menu, -1);
        }  
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }  
}
