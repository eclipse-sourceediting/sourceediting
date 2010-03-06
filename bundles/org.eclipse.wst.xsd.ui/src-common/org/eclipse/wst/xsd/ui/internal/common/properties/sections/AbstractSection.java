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
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.SubContributionManager;
import org.eclipse.jface.action.SubStatusLineManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;

public abstract class AbstractSection extends AbstractPropertySection implements SelectionListener, Listener
{
  protected Composite composite;
  protected PaintListener painter;
  protected XSDSchema xsdSchema;
  protected Object input;
  protected boolean isReadOnly;
  protected boolean listenerEnabled = true;
  protected boolean isSimple;
  protected CustomListener customListener = new CustomListener();
  protected IEditorPart owningEditor;
  private IStatusLineManager statusLine;
  protected boolean isTraversing = false;
  
  public static final Image ICON_ERROR = XSDEditorPlugin.getDefault().getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
  
  public AbstractSection()
  {
    super();
  }
  
  public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage)
  {
    super.createControls(parent, aTabbedPropertySheetPage);
    isSimple = getIsSimple();
    createContents(parent);
  }
  
  protected abstract void createContents(Composite parent);

    protected PaintListener createPainter() {
        return new PaintListener() {

            public void paintControl(PaintEvent e) {
//                Rectangle bounds = composite.getClientArea();
                GC gc = e.gc;

                gc.setForeground(gc.getBackground());
                gc.setBackground(getWidgetFactory().getColors().getColor(
                    FormColors.TB_BG));

//                gc.fillGradientRectangle(4 + bounds.width / 2, 0,
//                    bounds.width / 2 - 9, bounds.height, false);

                gc.setForeground(getWidgetFactory().getColors().getColor(
                    FormColors.TB_BORDER));
//                gc.drawLine(bounds.width - 5, 0, bounds.width - 5,
//                    bounds.height);
            }

        };

    }
    
    public void dispose()
    {
        if (composite != null && ! composite.isDisposed() && painter != null)
            composite.removePaintListener(painter);
        
        super.dispose();
    }

    public void setInput(IWorkbenchPart part, ISelection selection)
    {
        super.setInput(part, selection);
        isSimple = getIsSimple();
        Object input = ((IStructuredSelection)selection).getFirstElement();
        this.input = input;
        
        if (input instanceof XSDConcreteComponent)
        {
          xsdSchema = ((XSDConcreteComponent)input).getSchema();
        }
        
        // set owning editor of this section
        if (part!=null)
        {
            if (part instanceof IEditorPart)
            {
                owningEditor = (IEditorPart)part;
            }
            else
            {
                owningEditor = part.getSite().getWorkbenchWindow().getActivePage().getActiveEditor();
            }
        }
        if (xsdSchema == owningEditor.getAdapter(XSDSchema.class))
        {
          isReadOnly = false;
        }
        else
        {
          isReadOnly = true;
        }

        IEditorInput editorInput = owningEditor.getEditorInput();
        if (!(editorInput instanceof IFileEditorInput || editorInput instanceof FileStoreEditorInput))
        {
          isReadOnly = true;
        }
    }

    public void refresh()
    {
      super.refresh();
      composite.setEnabled(!isReadOnly);
    }

    public void applyAllListeners(Control control)
    {
      control.addListener(SWT.Modify, customListener);
      control.addListener(SWT.FocusOut, customListener);
      control.addListener(SWT.KeyDown, customListener);
    }
    
    public void applyModifyListeners(Control control)
    {
      control.addListener(SWT.Modify, customListener);
      control.addListener(SWT.FocusOut, customListener);
    }

    public void applyKeyListener(Control control)
    {
      control.addListener(SWT.KeyDown, customListener);
    }

    public void removeListeners(Control control)
    {
      control.removeListener(SWT.Modify, customListener);
      control.removeListener(SWT.FocusOut, customListener);
      control.removeListener(SWT.KeyDown, customListener);
    }
    
    public void doWidgetDefaultSelected(SelectionEvent e)
    {}
    
    public void doWidgetSelected(SelectionEvent e)
    {}

    public void widgetSelected(SelectionEvent e)
    {
      if (isListenerEnabled() &&
          input != null &&
          !isReadOnly) 
      {
        doWidgetSelected(e);
      }
    }

    public void widgetDefaultSelected(SelectionEvent e)
    {
      if (isListenerEnabled() &&
          input != null &&
          !isReadOnly) 
      {
        doWidgetDefaultSelected(e);
      }
    }

    /**
     * Get the value of listenerEnabled.
     * @return value of listenerEnabled.
     */
    public boolean isListenerEnabled() 
    {
      return listenerEnabled;
    }
    
    /**
     * Set the value of listenerEnabled.
     * @param v  Value to assign to listenerEnabled.
     */
    public void setListenerEnabled(boolean  v) 
    {
      this.listenerEnabled = v;
    }

    /**
     * Sent when an event that the receiver has registered for occurs.
     *
     * @param event the event which occurred
     */
    public void handleEvent(Event event)
    {
      if (isListenerEnabled() && !isReadOnly) 
      {
        doHandleEvent(event);
      }
    }

    /**
     * Subclasses should override
     * @param event
     */
    protected void doHandleEvent(Event event)
    {
    }

    protected IEditorPart getActiveEditor()
    {
      IWorkbench workbench = PlatformUI.getWorkbench();
      IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
      IEditorPart editorPart = workbenchWindow.getActivePage().getActiveEditor();
      this.owningEditor = editorPart;
      return editorPart;
    }
    
    public CommandStack getCommandStack()
    {
      Object commandStack = owningEditor.getAdapter(CommandStack.class); 
          
      if (commandStack==null)
          return null;
      else
          return (CommandStack)commandStack;
    }
    
    public boolean getIsSimple()
    {
      return false;
    }
    
    
    
    /**
     * Intended to display error messages.
     * @return
     */
    private IStatusLineManager getStatusLineManager()
    {
      if (statusLine==null && getPart()!=null)
      {
        if(getPart().getSite() instanceof IEditorSite)
          statusLine = ((IEditorSite)getPart().getSite()).getActionBars().getStatusLineManager();
        else if (getPart().getSite() instanceof IViewSite)
          statusLine = ((IViewSite)getPart().getSite()).getActionBars().getStatusLineManager();
        
        /* 
         * We must manually set the visibility of the status line since the action bars are from the editor
         * which means the status line only shows up when the editor is in focus (by default).
         * Note only a SubStatusLineManager can set the visibility.
         */
        if (statusLine instanceof SubStatusLineManager)
          ((SubStatusLineManager)statusLine).setVisible(true);
      }
      
      return statusLine;
    }

    /**
     * Display an error message in the status line.
     * Call setErrorMessage(null) to clear the status line.
     * @param text 
     */
    public void setErrorMessage(String text)
    {
      IStatusLineManager statusLine = getStatusLineManager();

      if (statusLine!=null)
      {
        if (text==null || text.length()<1)
          statusLine.setErrorMessage(null);
        else
          statusLine.setErrorMessage(ICON_ERROR, text);

        // ensure our message gets displayed
        if (statusLine instanceof SubContributionManager)
          ((SubContributionManager)statusLine).setVisible(true);
        
        statusLine.update(true);
      }
    }

    
    protected EObject getModel()
    {
      return (XSDComponent)input;
    }

    
    class CustomListener implements Listener
    {
      boolean handlingEvent = false;
      public void handleEvent(Event event)
      {
        if (isListenerEnabled() && !isReadOnly) 
        {
          switch (event.type)
          {
            case SWT.KeyDown :
            {
              if (event.character == SWT.CR)
              {
                if (!handlingEvent)
                {
                  handlingEvent = true;
                  doHandleEvent(event);
                  handlingEvent = false;
                }
              }
              break;
            }
            case SWT.FocusOut :
            {
              if (!handlingEvent)
              {
                handlingEvent = true;
                doHandleEvent(event);
                handlingEvent = false;
              }
              break;
            }
          }
        }
      }
    }
    
    protected boolean shouldPerformComboSelection(int eventType, Object selectedItem)
    {
      // if traversing through combobox, don't automatically pop up
      // the browse and new dialog boxes
      boolean wasTraversing = isTraversing;
      if (isTraversing)
        isTraversing = false;

      // we only care about default selecting (hitting enter in combobox)
      // for browse.. and new.. otherwise, selection event will be fired
      if (eventType == SWT.DefaultSelection)
      {
        if (selectedItem instanceof String && ((Messages._UI_COMBO_BROWSE.equals(selectedItem) || Messages._UI_COMBO_NEW.equals(selectedItem))))
          return true;
        return false;
      }

      // if was traversing and got selection event, do nothing if it's 
      // browse.. or new..
      if (wasTraversing && selectedItem instanceof String)
      {
        if (Messages._UI_COMBO_BROWSE.equals(selectedItem) || Messages._UI_COMBO_NEW.equals(selectedItem))
        {
          return false;
        }
      }
      return true;
    }
}
