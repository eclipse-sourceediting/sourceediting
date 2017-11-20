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
package org.eclipse.wst.xsd.ui.internal.search.actions;

import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.CompoundContributionItem;

// TODO should be in common.ui
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
      Action dummyAction = new Action("XSDSeachActionGroup_no_refactoring_available") //TODO wrong string here ??
      {
        // dummy inner class; no methods
      };
      dummyAction.setEnabled(false);
      contribList.add(new ActionContributionItem(dummyAction));
    }
    return (IContributionItem[]) contribList.toArray(new IContributionItem[contribList.size()]);        
  }
}
