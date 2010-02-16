/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.design.editparts;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IActionProvider;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IFeedbackHandler;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.KeyBoardAccessibilityEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.design.figures.IFigureFactory;
import org.eclipse.wst.xsd.ui.internal.adt.editor.CommonMultiPageEditor;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObjectListener;

public abstract class BaseEditPart extends AbstractGraphicalEditPart implements IActionProvider, IADTObjectListener, IFeedbackHandler
{
  protected static final String[] EMPTY_ACTION_ARRAY = {};
  protected boolean isSelected = false;
  protected boolean hasFocus = false;
  protected static boolean isHighContrast = Display.getDefault().getHighContrast();
  protected AccessibleEditPart accessiblePart;
  
  public IFigureFactory getFigureFactory()
  {
    EditPartFactory factory = getViewer().getEditPartFactory();
    Assert.isTrue(factory instanceof IFigureFactory, "EditPartFactory must be an instanceof of IFigureFactory");     //$NON-NLS-1$
    return (IFigureFactory)factory; 
  }
  
  public String[] getActions(Object object)
  {
    Object model = getModel();
    if (model instanceof IActionProvider)
    {
      return ((IActionProvider)model).getActions(object);
    }  
    return EMPTY_ACTION_ARRAY;
  }
  
  protected void addActionsToList(List list, IAction[] actions)
  {
    for (int i = 0; i < actions.length; i++)
    {
      list.add(actions[i]);
    }  
  }
  
  public void activate()
  {
    super.activate();
    Object model = getModel();
    if (model instanceof IADTObject)
    {
      IADTObject object = (IADTObject)model;
      object.registerListener(this);
    }
    
    if (getZoomManager() != null)
      getZoomManager().addZoomListener(zoomListener);

  }
  
  public void deactivate()
  {
    try
    {
    Object model = getModel();
    if (model instanceof IADTObject)
    {
      IADTObject object = (IADTObject)model;
      object.unregisterListener(this);
    }   
    
    if (getZoomManager() != null)
      getZoomManager().removeZoomListener(zoomListener);    
    }
    finally
    {
      super.deactivate();
    }  
  }  
  
  public void propertyChanged(Object object, String property)
  {
    refresh();
  }
  
  public void refresh() {
    
    boolean doUpdateDesign = doUpdateDesign();
    if (doUpdateDesign)
    {
      super.refresh();
    }
  }

  public void addFeedback()
  {
    isSelected = true;
    refreshVisuals();
  }

  public void removeFeedback()
  {
    isSelected = false;
    refreshVisuals();
  }
  
  public ZoomManager getZoomManager()
  {
    return ((ScalableRootEditPart)getRoot()).getZoomManager();
  }
  
  public Rectangle getZoomedBounds(Rectangle r)
  {
    double factor = getZoomManager().getZoom();
    int x = (int)Math.round(r.x * factor);
    int y = (int)Math.round(r.y * factor);
    int width = (int)Math.round(r.width * factor);
    int height = (int)Math.round(r.height * factor);

    return new Rectangle(x, y, width, height);
  }
  
  private ZoomListener zoomListener = new ZoomListener()
  {
    public void zoomChanged(double zoom)
    {
      handleZoomChanged();
    }
  };

  protected void handleZoomChanged()
  {
    refreshVisuals();
  }

  public IEditorPart getEditorPart()
  {
    IEditorPart editorPart = null;
    IWorkbench workbench = PlatformUI.getWorkbench();
    if (workbench != null)
    {
      IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
      if (workbenchWindow != null)
      {
        if (workbenchWindow.getActivePage() != null)
        {
          editorPart = workbenchWindow.getActivePage().getActiveEditor();
        }
      }
    }
//    Assert.isNotNull(editorPart);
    return editorPart;
  }
  
  protected void createEditPolicies()
  {
    installEditPolicy(KeyBoardAccessibilityEditPolicy.KEY, new KeyBoardAccessibilityEditPolicy()
    {      
      public EditPart getRelativeEditPart(EditPart editPart, int direction)
      {
        return doGetRelativeEditPart(editPart, direction);  
      }           
    });        
  }
  
  
  public EditPart doGetRelativeEditPart(EditPart editPart, int direction)
  {   
    return null;      
  }
  
  protected boolean isFileReadOnly()
  {
    IWorkbench workbench = PlatformUI.getWorkbench();
    if (workbench != null)
    {
      IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
      if (workbenchWindow != null)
      {
        IWorkbenchPage page = workbenchWindow.getActivePage();
        if (page != null)
        {
          IEditorPart editor = page.getActiveEditor();
          if (editor != null)
          {
            IEditorInput editorInput = editor.getEditorInput();
            if (!(editorInput instanceof IFileEditorInput || editorInput instanceof FileStoreEditorInput))
            {
              return true;
            }
          }
        }
      }
    }
    return false;
  }
  
  // For https://bugs.eclipse.org/bugs/show_bug.cgi?id=218281
  // Don't want to refresh the design when changes are made in the source view.
  protected boolean doUpdateDesign()
  {
    IWorkbench workbench = PlatformUI.getWorkbench();
    if (workbench != null)
    {
      IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
      if (workbenchWindow != null)
      {
        IWorkbenchPage page = workbenchWindow.getActivePage();
        if (page != null)
        {
          IEditorPart editorPart = page.getActiveEditor();
          if (editorPart instanceof CommonMultiPageEditor)
          {
            CommonMultiPageEditor editor = (CommonMultiPageEditor) editorPart;
            GraphicalViewer viewer = (GraphicalViewer)editor.getAdapter(GraphicalViewer.class);
            // Need to ensure this is the same editor we are working with since the active editor may not be 
            // the current, eg. at startup, there can be another XSD Editor open on the source page, so we could end
            // up not populating the design view initally
            if (getViewer() == viewer)
            {
              // If source page is active, don't update the design
              return !editor.isSourcePageActive();
            }
          }
        }
      }
    }
    return true;
  }
  
  /**
   * Try to get the italic font for the current font
   * @param font
   * @return
   */
  protected Font getItalicFont(Font font)
  {
    if (font != null && !font.isDisposed())
    {
      FontData[] fd = font.getFontData();
      if (fd.length > 0)
      {
        fd[0].setStyle(fd[0].getStyle() | SWT.ITALIC);
        return new Font(font.getDevice(), fd);
      }
    }
    return font;
  }
  protected AccessibleEditPart getAccessibleEditPart() {
		
	  if (accessiblePart ==null)
	  {
		  accessiblePart = new AccessibleGraphicalEditPart(){
		
			public void getName(AccessibleEvent e) {		 
				e.result = getReaderText();
			}
		};
	  }
	  return accessiblePart;
	}
public String getReaderText()
{
	  return "";
}
}
