package org.eclipse.wst.xsd.ui.internal.actions;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.commands.RenameCommand;
import org.eclipse.xsd.XSDNamedComponent;


public class GraphRenameAction extends Action
{
  protected RenameCommand command;
  
  public GraphRenameAction(XSDNamedComponent namedComponent, GraphicalEditPart editPart)
  {
    command = new RenameCommand(namedComponent, editPart);
    setText(XSDEditorPlugin.getXSDString("_UI_LABEL_RENAME"));
  }

  public void run()
  {
    command.run();
  }
}
