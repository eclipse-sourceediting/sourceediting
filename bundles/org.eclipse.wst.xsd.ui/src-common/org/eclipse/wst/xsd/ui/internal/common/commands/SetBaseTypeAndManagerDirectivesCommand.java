package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.core.resources.IFile;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDTypeDefinition;

public class SetBaseTypeAndManagerDirectivesCommand extends UpdateTypeReferenceAndManageDirectivesCommand
{

  public SetBaseTypeAndManagerDirectivesCommand(XSDConcreteComponent concreteComponent, String componentName, String componentNamespace, IFile file)
  {
    super(concreteComponent, componentName, componentNamespace, file);
  }

  public void execute()
  {
    try
    {
      beginRecording(concreteComponent.getElement());
      XSDComponent td = computeComponent();
      if (td != null && td instanceof XSDTypeDefinition)
      {
        SetBaseTypeCommand command = new SetBaseTypeCommand(concreteComponent, (XSDTypeDefinition) td);
        command.execute();
      }
    }
    catch (Exception e)
    {

    }
    finally
    {
      endRecording();
    }
  }
}
