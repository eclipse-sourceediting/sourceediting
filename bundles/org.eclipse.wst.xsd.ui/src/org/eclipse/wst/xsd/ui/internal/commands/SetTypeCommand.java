package org.eclipse.wst.xsd.ui.internal.commands;

import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.XSDSetTypeDialog;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;

public class SetTypeCommand extends AbstractCommand
{
  protected int typeKind = XSDConstants.COMPLEXTYPE_ELEMENT;
  
  public SetTypeCommand(XSDConcreteComponent parent)
  {
    super(parent);
  }
  
  public void setTypeKind(int type)
  {
    this.typeKind = type;
  }

  public void run()
  {
    XSDConcreteComponent parent = getParent();
    if (parent instanceof XSDElementDeclaration)
    {
      XSDElementDeclaration element = (XSDElementDeclaration)parent;
      XSDSchema schema = element.getSchema();
      if (typeKind == XSDConstants.COMPLEXTYPE_ELEMENT)
      {
        AddComplexTypeDefinitionCommand ctCommand = new AddComplexTypeDefinitionCommand(element, null);
//        ctCommand.run();
        AddModelGroupCommand sequenceCommand = new AddModelGroupCommand(element, XSDCompositor.SEQUENCE_LITERAL);
        sequenceCommand.run();
      }
      else if (typeKind == XSDConstants.SIMPLETYPE_ELEMENT)
      {
        AddSimpleTypeDefinitionCommand stCommand = new AddSimpleTypeDefinitionCommand(element, null);
        stCommand.run();
        XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)stCommand.getModelObject();
        XSDSimpleTypeDefinition base = schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(), "string");
        st.setBaseTypeDefinition(base);
      }
      else
      {
        XSDSetTypeDialog setTypeDialog = new XSDSetTypeDialog(Display.getDefault().getActiveShell(), element.getElement(), "Set Type", schema);
        setTypeDialog.create();
        setTypeDialog.open();
        int rc = setTypeDialog.getReturnCode();
      }
      formatChild(element.getElement());
    }

  }

  protected boolean adopt(XSDConcreteComponent model)
  {
    return false;
  }

}
