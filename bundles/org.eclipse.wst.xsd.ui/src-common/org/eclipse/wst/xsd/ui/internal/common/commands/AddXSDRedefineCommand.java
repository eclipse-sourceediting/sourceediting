package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;

public class AddXSDRedefineCommand extends AddXSDSchemaDirectiveCommand
{
  public AddXSDRedefineCommand(String label, XSDSchema schema)
  {
    super(label);
    this.xsdSchema = schema;
  }

  public void execute()
  {
    XSDRedefine xsdRedefine = XSDFactory.eINSTANCE.createXSDRedefine();
    xsdRedefine.setSchemaLocation("");
    xsdSchema.getContents().add(findNextPositionToInsert(), xsdRedefine);
    addedXSDConcreteComponent = xsdRedefine;
    formatChild(xsdSchema.getElement());
  }
  
}
