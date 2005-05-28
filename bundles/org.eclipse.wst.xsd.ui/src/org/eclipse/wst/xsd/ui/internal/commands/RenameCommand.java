package org.eclipse.wst.xsd.ui.internal.commands;

import org.eclipse.draw2d.Label;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.wst.xsd.ui.internal.graph.editparts.ComplexTypeDefinitionEditPart;
import org.eclipse.wst.xsd.ui.internal.graph.editparts.ElementDeclarationEditPart;
import org.eclipse.wst.xsd.ui.internal.graph.editparts.ModelGroupDefinitionEditPart;
import org.eclipse.wst.xsd.ui.internal.graph.editparts.TopLevelComponentEditPart;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.SimpleDirectEditPolicy;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDNamedComponent;


/**
 * @author kchong
 *
 * <a href="mailto:kchong@ca.ibm.com">kchong@ca.ibm.com</a>
 *
 */
public class RenameCommand
{
  protected SimpleDirectEditPolicy simpleDirectEditPolicy = new SimpleDirectEditPolicy();
  Label label = new Label();
  XSDNamedComponent namedComponent;
  GraphicalEditPart editPart;
  
  public RenameCommand(XSDNamedComponent namedComponent, GraphicalEditPart editPart)
  {
    this.namedComponent = namedComponent;
    this.editPart = editPart;
  }

  public void run()
  {
    if (editPart instanceof ElementDeclarationEditPart)
    {
      ElementDeclarationEditPart elementDeclarationEditPart = (ElementDeclarationEditPart)editPart;
      elementDeclarationEditPart.doEditName();
    }
    else if (editPart instanceof TopLevelComponentEditPart)
    {
      TopLevelComponentEditPart topLevelEditPart = (TopLevelComponentEditPart)editPart;
      topLevelEditPart.doEditName();
    }
    else if (editPart instanceof ComplexTypeDefinitionEditPart)
    {
      ((ComplexTypeDefinitionEditPart)editPart).doEditName();
    }
    else if (editPart instanceof ModelGroupDefinitionEditPart)
    {
      ((ModelGroupDefinitionEditPart)editPart).doEditName();
    }
  }

  protected boolean adopt(XSDConcreteComponent model)
  {
    return false;
  }

}
