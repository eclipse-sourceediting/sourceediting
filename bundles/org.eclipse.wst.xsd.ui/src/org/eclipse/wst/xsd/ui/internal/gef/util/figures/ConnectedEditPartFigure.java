/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.gef.util.figures;
           
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

              
public class ConnectedEditPartFigure extends ContainerFigure implements IConnectedEditPartFigure
{                 
  protected EditPart editPart;
  protected boolean childConnectionsEnabled = true;
  protected List connectedFigures = new ArrayList();
  protected int connectionType = RIGHT_CONNECTION;
    
  public ConnectedEditPartFigure(EditPart editPart)
  {
    this.editPart = editPart;    
  }     

  public void setChildConnectionsEnabled(boolean enabled)
  {
    childConnectionsEnabled = enabled;
  }          

  protected IConnectedEditPartFigure getParentGraphNodeFigure()
  {
    IConnectedEditPartFigure result = null;
    for (EditPart parentEditPart = editPart.getParent(); parentEditPart != null; parentEditPart = parentEditPart.getParent())
    {                                                              
      IFigure figure = ((AbstractGraphicalEditPart)parentEditPart).getFigure();    
      if (figure instanceof IConnectedEditPartFigure)
      {                                                            
        IConnectedEditPartFigure graphNodeFigure = (IConnectedEditPartFigure)figure;
        if (graphNodeFigure.getConnectionFigure() != null)
        {
          result = graphNodeFigure;
          break;
        }
      }          
    }          
    return result;
  }

  public void addNotify()                     
  {           
    super.addNotify();
    if (getConnectionFigure() != null)
    {
      IConnectedEditPartFigure parentGraphNodeFigure = getParentGraphNodeFigure();
      if (parentGraphNodeFigure != null)
      {   
        parentGraphNodeFigure.addConnectedFigure(this);
      }   
    }
  }   

  public void removeNotify()
  {
    super.removeNotify();
    if (getConnectionFigure() != null)
    {
      IConnectedEditPartFigure parentGraphNodeFigure = getParentGraphNodeFigure();
      if (parentGraphNodeFigure != null)
      {   
        parentGraphNodeFigure.removeConnectedFigure(this);
      }   
    }
  }   

  public void addConnectedFigure(IConnectedEditPartFigure figure)
  {   
    if (childConnectionsEnabled) 
    {  
      // this test is required since we sometimes receive the 'addNotify' call twice
      //
      if (!connectedFigures.contains(figure))
      {
        connectedFigures.add(figure);
      }
    }
  }

  public void removeConnectedFigure(IConnectedEditPartFigure figure)
  {
    if (childConnectionsEnabled) 
    { 
      connectedFigures.remove(figure);
    }
  }    
                    
  public IFigure getSelectionFigure()
  {
    return this;
  }

  public IFigure getConnectionFigure()
  {
    return this;
  }

  public List getConnectedFigures(int type)
  {
    List list = new ArrayList();
    for (Iterator i = connectedFigures.iterator(); i.hasNext(); )
    {           
      IConnectedEditPartFigure figure = (IConnectedEditPartFigure)i.next();
      //if (type == 0 || type == figure.getConnectionType())
      {
        list.add(figure);
      }      
    }
    return list;
  }                               

  public int getConnectionType()
  {
    return connectionType;
  }

  public void setConnectionType(int type)
  {
    connectionType = type;
  }
}
