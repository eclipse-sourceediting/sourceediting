package org.eclipse.wst.xsd.ui.internal.search.actions;

import java.util.List;
import org.eclipse.ui.actions.ActionGroup;

public abstract class SearchGroup extends ActionGroup
{
  public abstract void fillActions(List list);
}
