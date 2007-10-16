/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.design;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.KeyBoardAccessibilityEditPolicy;
import org.eclipse.wst.xsd.ui.internal.common.actions.OpenInNewEditor;

/**
 * This key handler is designed to be re-used by both the WSDL and XSD editor
 */
public class BaseGraphicalViewerKeyHandler extends GraphicalViewerKeyHandler
{
  public BaseGraphicalViewerKeyHandler(GraphicalViewer viewer)
  {
    super(viewer);
  }

  public boolean keyPressed(KeyEvent event)
  {
    int direction = -1;
    boolean isAltDown = (event.stateMask & SWT.ALT) != 0;
    boolean isCtrlDown = (event.stateMask & SWT.CTRL) != 0;
    switch (event.keyCode)
    {
      case SWT.ARROW_LEFT : {
        direction = PositionConstants.WEST;
        break;
      }
      case SWT.ARROW_RIGHT : {
        direction = PositionConstants.EAST;
        break;
      }
      case SWT.ARROW_UP : {
        direction = isAltDown ? KeyBoardAccessibilityEditPolicy.OUT_TO_PARENT : PositionConstants.NORTH;
        break;
      }
      case SWT.ARROW_DOWN : {
    	 direction = isAltDown ? KeyBoardAccessibilityEditPolicy.IN_TO_FIRST_CHILD : PositionConstants.SOUTH;       
        break;
      }
    }
    
    if (direction != -1)
    {
      GraphicalEditPart focusEditPart = getFocusEditPart();
      KeyBoardAccessibilityEditPolicy policy = (KeyBoardAccessibilityEditPolicy)focusEditPart.getEditPolicy(KeyBoardAccessibilityEditPolicy.KEY);
      
      if (policy != null)          
      {
        EditPart target = policy.getRelativeEditPart(focusEditPart, direction);
        if (target != null)
        {
        	if(isCtrlDown) {

        		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        			 Object keyboardDrag = editor.getAdapter(IKeyboardDrag.class);
        			 if (keyboardDrag instanceof IKeyboardDrag) {
        				 ((IKeyboardDrag) keyboardDrag).performKeyboardDrag(focusEditPart, direction);
        				 return true;
        		 }
        	}
        	else {
                navigateTo(target, event);
                return true;
        	}
        }          
      }         
    }
    
    switch (event.keyCode)
    {
      case SWT.PAGE_DOWN :
      {  
        if (scrollPage(event, PositionConstants.SOUTH))
          return true;
      }  
      case SWT.PAGE_UP :
      {  
        if (scrollPage(event, PositionConstants.NORTH))
          return true;
      }
      case SWT.F3 :
      case SWT.CR :
      {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
        IEditorPart editorPart = workbenchWindow.getActivePage().getActiveEditor();
        ActionRegistry registry = (ActionRegistry) editorPart.getAdapter(ActionRegistry.class);
        if (registry != null)
        {
          IAction action = registry.getAction(OpenInNewEditor.ID);
          if (action != null)
          action.run();
        }
      }
      /*
      case SWT.F5 :
      {
        IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        if (part != null)
        {
          EditorModeManager manager = (EditorModeManager)part.getAdapter(EditorModeManager.class);
          EditorMode[] modes = manager.getModes();
          EditorMode mode = manager.getCurrentMode();
          List list = Arrays.asList(modes);
          int index = list.indexOf(mode);
          int nextIndex = index + 1;
          if (nextIndex < modes.length)
          {
            mode = (EditorMode)list.get(nextIndex);
          }  
          else
          {
            mode = (EditorMode)list.get(0);            
          }
          if (mode != manager.getCurrentMode())
          {  
            manager.setCurrentMode(mode);
          }  
        }  
        return true;
      }*/      
    }
    return super.keyPressed(event);
  }

  private boolean scrollPage(KeyEvent event, int direction)
  {
    if (!(getViewer().getControl() instanceof FigureCanvas))
      return false;
    FigureCanvas figCanvas = (FigureCanvas) getViewer().getControl();
    Point loc = figCanvas.getViewport().getViewLocation();
    Rectangle area = figCanvas.getViewport().getClientArea(Rectangle.SINGLETON).scale(.8);
    if (direction == PositionConstants.NORTH)
    {
      figCanvas.scrollToY(loc.y - area.height);
    }
    else
    {
      figCanvas.scrollToY(loc.y + area.height);
    }
    return true;
  }
}