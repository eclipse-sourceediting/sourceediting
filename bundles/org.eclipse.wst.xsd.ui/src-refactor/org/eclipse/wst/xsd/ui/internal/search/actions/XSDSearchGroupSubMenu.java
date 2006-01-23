package org.eclipse.wst.xsd.ui.internal.search.actions;

import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.CompoundContributionItem;

public class XSDSearchGroupSubMenu extends CompoundContributionItem
{
  SearchGroup searchActionGroup;

  public XSDSearchGroupSubMenu(SearchGroup refactorMenuGroup)
  {
    super();
    searchActionGroup = refactorMenuGroup;
  }

  public XSDSearchGroupSubMenu(String id)
  {
    super(id);
  }

  protected IContributionItem[] getContributionItems()
  {
    ArrayList actionsList = new ArrayList();
    ArrayList contribList = new ArrayList();
    searchActionGroup.fillActions(actionsList);
    if (actionsList != null && !actionsList.isEmpty())
    {
      for (Iterator iter = actionsList.iterator(); iter.hasNext();)
      {
        Object o = iter.next();
        if (o instanceof IAction)
        {  
          IAction action = (IAction)o;
          contribList.add(new ActionContributionItem(action));
        }
        else if (o instanceof Separator)
        {
          Separator separator = (Separator)o;
          contribList.add(separator);
        }  
      }
    }
    else
    {
      Action dummyAction = new Action("XSDSeachActionGroup_no_refactoring_available")
      {
        // dummy inner class; no methods
      };
      dummyAction.setEnabled(false);
      contribList.add(new ActionContributionItem(dummyAction));
    }
    return (IContributionItem[]) contribList.toArray(new IContributionItem[contribList.size()]);        
  }
}
