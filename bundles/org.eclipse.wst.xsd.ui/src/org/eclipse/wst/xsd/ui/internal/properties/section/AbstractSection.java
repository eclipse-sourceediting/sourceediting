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
package org.eclipse.wst.xsd.ui.internal.properties.section;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.wst.common.ui.properties.internal.provisional.ISection;
import org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetPage;
import org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xml.core.NameValidator;
import org.eclipse.wst.xml.core.document.XMLNode;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.actions.CreateElementAction;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class AbstractSection implements ISection, IPropertyChangeListener, Listener, SelectionListener
{
	private TabbedPropertySheetWidgetFactory factory;
	protected IWorkbenchPart part;
	protected ISelection selection;
	protected Object input;
  protected boolean doRefresh = true;
	XSDSchema xsdSchema;
  protected boolean isReadOnly = false;
  private IStatusLineManager statusLine;
  protected Composite composite;
  protected int rightMarginSpace;
  protected int tableMinimumWidth = 50;
 
  /**
   * 
   */
  public AbstractSection()
  {
    super();
  }
  
  public void createControls(Composite parent,	TabbedPropertySheetPage tabbedPropertySheetPage)
  {
    createControls(parent, tabbedPropertySheetPage.getWidgetFactory());
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
   */
  public void createControls(Composite parent, TabbedPropertySheetWidgetFactory aFactory)
  {
		this.factory = aFactory;
    GC gc = new GC(parent);
    Point extent = gc.textExtent("  ...  ");
    rightMarginSpace = extent.x;
    gc.dispose();
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISection#setInput(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
   */
  public void setInput(IWorkbenchPart part, ISelection selection)
  {
		Assert.isTrue(selection instanceof IStructuredSelection);
		this.part = part;
		this.selection = selection;
		Object input = ((IStructuredSelection)selection).getFirstElement();
    this.input = input;
    
    if (input instanceof XSDConcreteComponent)
    {
      xsdSchema = ((XSDConcreteComponent)input).getSchema();
      
      Element element = ((XSDConcreteComponent)input).getElement();
      if (element instanceof XMLNode)
      {
        isReadOnly = false;
      }
      else
      {
        isReadOnly = true;
      }
    }
    statusLine = getStatusLine();
    clearErrorMessage();

//		refresh();
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISection#aboutToBeShown()
   */
  public void aboutToBeShown()
  {
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISection#aboutToBeHidden()
   */
  public void aboutToBeHidden()
  {
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISection#dispose()
   */
  public void dispose()
  {
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISection#getMinimumHeight()
   */
  public int getMinimumHeight()
  {
    return SWT.DEFAULT;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISection#shouldUseExtraSpace()
   */
  public boolean shouldUseExtraSpace()
  {
    return false;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISection#refresh()
   */
  public void refresh()
  {
  }

  public Object getInput()
  {
    if (input instanceof Element)
    {
      input = xsdSchema.getCorrespondingComponent((Element)input);
    }
    return input;
  }
  
  public XSDSchema getSchema()
  {
    return xsdSchema;
  }
  
	/**
	 * Get the widget factory.
	 * @return the widget factory.
	 */
	public TabbedPropertySheetWidgetFactory getWidgetFactory() {
		return factory;
	}

	public void propertyChange(PropertyChangeEvent event)
	{
    refresh();
	}

	
  public void doWidgetDefaultSelected(SelectionEvent e)
  {}
  
  public void doWidgetSelected(SelectionEvent e)
  {}
  
  /**
   * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(SelectionEvent)
   */
  public void widgetDefaultSelected(SelectionEvent e)
  {
    if (isListenerEnabled() &&
        getInput() != null &&
        !isInDoHandle &&
        !isReadOnly) 
    {
      isInDoHandle = true;
      doWidgetDefaultSelected(e);
      isInDoHandle = false;
    }
    
  }

  /**
   * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
   */
  public void widgetSelected(SelectionEvent e)
  {
    if (isListenerEnabled() &&
        getInput() != null &&
        !isInDoHandle &&
        !isReadOnly) 
    {
      isInDoHandle = true;
      doWidgetSelected(e);
      isInDoHandle = false;
    }
    
  }
  
  boolean listenerEnabled = true;
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

  public void handleEvent(Event event)
  {
    if (isListenerEnabled() && !isInDoHandle && !isReadOnly) 
    {
      isInDoHandle = true;
      startDelayedEvent(event);
      isInDoHandle = false;
    } // end of if ()
  }
  
  public void doHandleEvent(Event event)
  {
    
  }
  
  protected DelayedEvent delayedTask;
  
  protected void startDelayedEvent(Event e)
  {
    if (delayedTask == null ||
      delayedTask.getEvent() == null)
    {
      delayedTask = new DelayedEvent();
      delayedTask.setEvent(e);
      Display.getDefault().timerExec(500,delayedTask);
    }
    else
    {
      Event delayedEvent = delayedTask.getEvent();
      
      if (e.widget == delayedEvent.widget &&
        e.type == delayedEvent.type)
      {
        // same event, just different data, delay new event
        delayedTask.setEvent(null);
      }
      delayedTask = new DelayedEvent();
      delayedTask.setEvent(e);
      Display.getDefault().timerExec(500,delayedTask);
    }
  }
  
  class DelayedEvent implements Runnable
  {
    protected Event event;
    
    /*
     * @see Runnable#run()
     */
    public void run()
    {
      if (event != null)
      {
        isInDoHandle = true;
        doHandleEvent(event);
        isInDoHandle = false;
        event = null;
      }
    }
    
    /**
     * Gets the event.
     * @return Returns a Event
     */
    public Event getEvent()
    {
      return event;
    }

    /**
     * Sets the event.
     * @param event The event to set
     */
    public void setEvent(Event event)
    {
      this.event = event;
    }

  }

  boolean isInDoHandle;
  /**
   * Get the value of isInDoHandle.
   * @return value of isInDoHandle.
   */
  public boolean isInDoHandle() 
  {
    return isInDoHandle;
  }

  
  protected IEditorPart getActiveEditor()
  {
    IWorkbench workbench = XSDEditorPlugin.getPlugin().getWorkbench();
    IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
    IEditorPart editorPart = workbenchWindow.getActivePage().getActiveEditor();
//    IEditorPart editorPart = part.getSite().getWorkbenchWindow().getActivePage().getActiveEditor();

    return editorPart;
  }

  static protected IStatusLineManager getStatusLineManager(IEditorPart editorPart)
  { 
    IStatusLineManager result = null;
    try
    {                       
      EditorActionBarContributor contributor = (EditorActionBarContributor)editorPart.getEditorSite().getActionBarContributor();
      result = contributor.getActionBars().getStatusLineManager();
    }
    catch (Exception e)
    {
    }  
    return result;
  }

  protected XSDDOMHelper domHelper = new XSDDOMHelper();
  /**
   * Gets the domHelper.
   * @return Returns a XSDDomHelper
   */
  public XSDDOMHelper getDomHelper()
  {
    return domHelper;
  }
  
  public DocumentImpl getDocument(Element element)
  {
    return (DocumentImpl) element.getOwnerDocument();
  }

  public void beginRecording(String description, Element element)
  {
    getDocument(element).getModel().beginRecording(this, description);
  }
  
  public void endRecording(Element element)
  {
    DocumentImpl doc = (DocumentImpl) getDocument(element);
    
    doc.getModel().endRecording(this);    
  }

  protected boolean validateName(String name)
  {
    try
    {
      return NameValidator.isValid(name);
    }
    catch (Exception e)
    {
      return false;
    }
  }

  // TODO
  protected boolean validateLanguage(String lang)
  {
    return true;
  }

  // TODO
  protected boolean validatePrefix(String prefix)
  {
    return true;
  }

  
  protected Action getNewElementAction(String elementTag, String label, List attributes, Element parent, Node relativeNode)
  {
    CreateElementAction action = new CreateElementAction(label);
    action.setElementTag(elementTag);
    action.setAttributes(attributes);
    action.setParentNode(parent);
    action.setRelativeNode(relativeNode);
    action.setXSDSchema(getSchema());
    action.setSelectionProvider(null);
    return action;
  }

  public void setErrorMessage(String message)
  {
    if (statusLine != null)
    {
      statusLine.setErrorMessage(message);
      statusLine.update(true);
    }
  }

  public void clearErrorMessage()
  {
    if (statusLine != null)
    {
      statusLine.setErrorMessage(null);
      statusLine.update(false);
    }
  }

  EditorActionBarContributor contributor;
  protected IStatusLineManager getStatusLine()
  {
//    IWorkbench workbench = XSDEditorPlugin.getPlugin().getWorkbench();
//    IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
    IEditorPart editorPart = part.getSite().getWorkbenchWindow().getActivePage().getActiveEditor();
    
    if (statusLine == null)
    {
      try
      {                       
        contributor = (EditorActionBarContributor)editorPart.getEditorSite().getActionBarContributor();
        statusLine = contributor.getActionBars().getStatusLineManager();
      }
      catch (Exception e)
      {
      }  
    }
    return statusLine;
  }
  
  /**
   * Get the standard label width when labels for sections line up on the left
   * hand side of the composite. We line up to a fixed position, but if a
   * string is wider than the fixed position, then we use that widest string.
   * 
   * @param parent
   *            The parent composite used to create a GC.
   * @param labels
   *            The list of labels.
   * @return the standard label width.
   */
  protected int getStandardLabelWidth(Composite parent, String[] labels) {
    int standardLabelWidth = 100;   // STANDARD_LABEL_WIDTH;
    GC gc = new GC(parent);
    int indent = gc.textExtent("XXX").x; //$NON-NLS-1$
    for (int i = 0; i < labels.length; i++) {
      int width = gc.textExtent(labels[i]).x;
      if (width + indent > standardLabelWidth) {
        standardLabelWidth = width + indent;
      }
    }
    gc.dispose();
    return standardLabelWidth;
  }

}
