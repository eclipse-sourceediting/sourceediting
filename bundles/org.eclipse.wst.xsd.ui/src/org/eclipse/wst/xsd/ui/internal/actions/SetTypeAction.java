package org.eclipse.wst.xsd.ui.internal.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.xsd.ui.internal.commands.SetTypeCommand;
import org.eclipse.xsd.XSDConcreteComponent;

public class SetTypeAction extends AbstractAction
{
  int typeKind;
  SetTypeCommand command;
  
  public SetTypeAction(String text, ImageDescriptor image, XSDConcreteComponent xsdConcreteComponent)
  {
    super(text, image, xsdConcreteComponent);
    command = new SetTypeCommand(xsdConcreteComponent);
  }
  
  public SetTypeAction(String text, XSDConcreteComponent xsdConcreteComponent)
  {
    super(text, xsdConcreteComponent);
    command = new SetTypeCommand(xsdConcreteComponent);
  }
  
  public void setTypeKind(int type)
  {
    this.typeKind = type;
    command.setTypeKind(type);
  }
  
  public void run()
  {
    beginRecording(getText());
    command.run();
    endRecording();
  }

}
