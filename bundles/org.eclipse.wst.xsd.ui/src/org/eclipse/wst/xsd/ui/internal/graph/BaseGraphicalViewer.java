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
package org.eclipse.wst.xsd.ui.internal.graph;

import java.util.Iterator;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.wst.xsd.ui.internal.XSDEditor;
import org.eclipse.wst.xsd.ui.internal.XSDMenuListener;
import org.eclipse.wst.xsd.ui.internal.gef.util.figures.ConnectionRenderingFigure;
import org.eclipse.wst.xsd.ui.internal.gef.util.figures.IConnectionRenderingViewer;
import org.eclipse.wst.xsd.ui.internal.graph.figures.CenterLayout;
import org.eclipse.xsd.XSDConcreteComponent;


/**
 * @author ernest
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class BaseGraphicalViewer extends ScrollingGraphicalViewer implements IConnectionRenderingViewer
{
  protected FigureCanvasKeyboardHandler figureCanvasKeyboardHandler;

  protected EditDomain editDomain;

  protected boolean isInputEnabled = true;

  protected boolean isSelectionEnabled = true;

  protected ISelectionProvider menuSelectionProvider;

  protected GraphContextMenuProvider menuProvider;

  protected XSDEditor editor;

  protected XSDConcreteComponent input;

  protected ConnectionRenderingFigure connectionRenderingFigure;

  public BaseGraphicalViewer(XSDEditor editor, ISelectionProvider selectionProvider)
  {
    super();
    this.editor = editor;
    menuSelectionProvider = selectionProvider;
  }
  
  public ConnectionRenderingFigure getConnectionRenderingFigure()
  {
    return connectionRenderingFigure;
  }

  public void setInputEnabled(boolean enabled)
  {
    isInputEnabled = enabled;
  }

  public void setSelectionEnabled(boolean enabled)
  {
    isSelectionEnabled = enabled;
  }

  public XSDMenuListener getMenuListener()
  {
    return menuProvider.getMenuListener();
  }

  protected static Color white = null;
  protected void hookControl()
  {                     
    super.hookControl();
  
    if (white == null)
    {
      white = new Color(getControl().getDisplay(), 255, 255, 255);
    }
    getControl().setBackground(white); 
  
    editDomain = new DefaultEditDomain(null);
    ((DefaultEditDomain)editDomain).setDefaultTool(new SelectionTool());
    editDomain.loadDefaultTool();
    editDomain.addViewer(this);
  
    //jvh - gef port - moved this from below so it is available when adding context menu below
    menuProvider = new GraphContextMenuProvider(this, menuSelectionProvider, editor.getXSDTextEditor());
    setContextMenu(menuProvider);
  
    // add context menu to the graph
    MenuManager manager = new MenuManager();
    manager.addMenuListener(getMenuListener());  //jvh - gef port
    manager.setRemoveAllWhenShown(true);
    Menu menu = manager.createContextMenu(getControl());
    getControl().setMenu(menu);
    
    KeyAdapter keyListener = new KeyAdapter()
    {
      /**
       * @see org.eclipse.swt.events.KeyAdapter#keyReleased(KeyEvent)
       */
      public void keyReleased(KeyEvent e)
      {
        if (e.character == SWT.DEL)
        {
          getMenuListener().getDeleteAction().run();
        }
      }
    };
  
    setKeyHandler(new XSDGraphicalViewerKeyHandler(this).setParent(new KeyHandler()));
    
//    getControl().addKeyListener(keyListener);

    figureCanvasKeyboardHandler = new FigureCanvasKeyboardHandler();
    getFigureCanvas().addKeyListener(figureCanvasKeyboardHandler);
    
  	getRootEditPart().activate();		
                       
  	ScalableRootEditPart graphicalRootEditPart = (ScalableRootEditPart)getRootEditPart();
     
    // set the layout for the primary layer so that the children are always centered
    //
    graphicalRootEditPart.getLayer(LayerConstants.PRIMARY_LAYER).setLayoutManager(new CenterLayout());
  
    // add the ConnectionRenderingFigure which is responsible for drawing all of the lines in the view
    //                       
    IFigure figure = graphicalRootEditPart.getLayer(LayerConstants.HANDLE_LAYER);
    figure.setLayoutManager(new StackLayout());          
    connectionRenderingFigure = new ConnectionRenderingFigure(graphicalRootEditPart.getLayer(LayerConstants.PRIMARY_LAYER));      
    figure.add(connectionRenderingFigure);
  
    figure.validate();      
  }

  public XSDConcreteComponent getInput()
  {
    return input;
  }

  protected EditPart getEditPart(EditPart parent, Object object)
  {
    EditPart result = null;
    for (Iterator i = parent.getChildren().iterator(); i.hasNext(); )
    {
      EditPart editPart = (EditPart)i.next();
      if (editPart.getModel() == object)
      {  
        result = editPart;
        break;
      }
    }             
  
    if (result == null)
    { 
      for (Iterator i = parent.getChildren().iterator(); i.hasNext(); )
      {
        EditPart editPart = getEditPart((EditPart)i.next(), object);
        if (editPart != null)
        {
          result = editPart;
          break;
        }
      }            
    }
  
    return result;
  }
  
  public abstract void setInput(XSDConcreteComponent comp);
  
  public abstract void setSelection(XSDConcreteComponent comp);
  
  
}
