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
package org.eclipse.wst.xsd.ui.internal.graph.editparts;
                          
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.graph.XSDGraphUtil;
import org.eclipse.wst.xsd.ui.internal.graph.figures.ContainerFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.GraphNodeFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.RepeatableGraphNodeFigure;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDWildcard;
              

public class WildcardEditPart extends RepeatableGraphNodeEditPart
{
  protected Label label;

                             
  public XSDParticle getXSDParticle()
  {                    
    Object o = getXSDWildcard().getContainer();
    return (o instanceof XSDParticle) ? (XSDParticle)o : null;
  }


  public XSDWildcard getXSDWildcard()
  {
    return (XSDWildcard)getModel();
  }

   
  protected GraphNodeFigure createGraphNodeFigure()
  {
    RepeatableGraphNodeFigure figure = new RepeatableGraphNodeFigure();                                                        
    figure.setConnected(true);
    figure.getOutlinedArea().setFill(true);

    label = new Label();    
    label.setText(XSDEditorPlugin.getXSDString("_UI_ANY_ELEMENT")); 
    label.setBorder(new MarginBorder(0, 6, 0, 4));
    label.setFont(mediumBoldFont);        

    figure.getIconArea().add(label);     

    return figure;
  }  
 
      
  protected void refreshVisuals()
  {     
    ContainerFigure rectangle = graphNodeFigure.getOutlinedArea();
    if (XSDGraphUtil.isEditable(getModel()))
    {
      rectangle.setBorder(new LineBorder(2));
      rectangle.setBackgroundColor(elementBackgroundColor);
      rectangle.setForegroundColor(isSelected ? ColorConstants.black : elementBorderColor);

      label.setBackgroundColor(elementBackgroundColor);
      label.setForegroundColor(elementLabelColor);
    }
    else
    {            
      rectangle.setBorder(new LineBorder(readOnlyBorderColor, 2));
      rectangle.setBackgroundColor(readOnlyBackgroundColor);
      rectangle.setForegroundColor(isSelected ? ColorConstants.black : readOnlyBorderColor);
   
      label.setBackgroundColor(readOnlyBackgroundColor);
      label.setForegroundColor(readOnlyBorderColor);
    } 

    refreshOccurenceLabel(getXSDParticle());
  }      
}
