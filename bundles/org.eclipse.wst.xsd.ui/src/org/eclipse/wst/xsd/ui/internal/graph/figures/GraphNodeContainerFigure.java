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
package org.eclipse.wst.xsd.ui.internal.graph.figures;
            
              

public class GraphNodeContainerFigure extends ContainerFigure
{               
  public ExpandableGraphNodeFigure expandableGraphNodeFigure;  

  public GraphNodeContainerFigure(ExpandableGraphNodeFigure expandableGraphNodeFigure)
  {                  
    super();
    this.expandableGraphNodeFigure = expandableGraphNodeFigure;
  }     

  public boolean isExpanded()
  {
    return expandableGraphNodeFigure.isExpanded();
  }
}
