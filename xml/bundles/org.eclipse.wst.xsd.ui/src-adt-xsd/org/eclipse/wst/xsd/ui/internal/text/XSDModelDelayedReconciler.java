/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.text;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.wst.xsd.ui.internal.editor.InternalXSDMultiPageEditor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Element;


/**
 * Provides  delayed reconciliation between the SSE DOM and the XSD EMF model. 
 * Changes in the DOM are queued and processed by a UI job. When a new request 
 * comes in, the current run is cancelled, the new request is added to the queue, 
 * then the job is re-scheduled.
 */
public class XSDModelDelayedReconciler
{
  /**
   * The model reconciler job.
   */
  private ReconcilerJob reconcilerJob;

  /**
   * The time in milliseconds to delay updating the EMF model.
   */
  private static final int DELAY = 300;

  /**
   * The elements to reconcile.
   */
  private List elementsToReconcile = new ArrayList();

  /**
   * Determines if the delayed reconciler should kick in.
   */
  public boolean shouldDelay(XSDSchema schema)
  {
    boolean shouldDelay = false;

    // The delayed reconciler should not be used when the editor is in graphical editing mode.

    IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
    IEditorPart editorPart = workbenchPage != null ? workbenchPage.getActiveEditor() : null;
    if (editorPart != null && editorPart instanceof InternalXSDMultiPageEditor)
    {
      InternalXSDMultiPageEditor xsdEditor = (InternalXSDMultiPageEditor)editorPart;
      shouldDelay = xsdEditor.isSourcePageActive();
    }

    return shouldDelay;
  }

  /**
   * Updates the XSD EMF component corresponding to the DOM element.
   * 
   * @param element the changed element.
   * @param schema the containing schema.
   */
  public void elementChanged(Element element, XSDSchema schema)
  {
    synchronized (elementsToReconcile)
    {
      // The number of elements should be small so a linear search should be fine.

      if (!elementsToReconcile.contains(element))
      {
        elementsToReconcile.add(element);
      }

      if (reconcilerJob == null)
      {
        reconcilerJob = new ReconcilerJob(schema);
      }
      
      reconcilerJob.schedule(DELAY);
    }
  }

  /**
   * A UI job used to reconcile the XSD EMF model with the associated SSE DOM.
   */
  class ReconcilerJob extends UIJob
  {
    /**
     * The target schema.
     */
    private XSDSchema schema;

    /**
     * The number of times allowed to wake up and do nothing.
     */
    private static final int MAX_INACTIVE_COUNT = 10;

    /**
     * The job will terminate once this counter reaches MAX_INACTIVE_COUNT. 
     */
    private int timesAwakeAndIdle = 0;

    /**
     * Constructs the reconciler job and configures some of its properties.
     */
    public ReconcilerJob(XSDSchema schema)
    {
      super("Reconciling the XSD EMF model"); //$NON-NLS-1$
      setSystem(true);
      setPriority(Job.LONG);
      this.schema = schema;
    }

    public IStatus runInUIThread(IProgressMonitor monitor)
    {
      if (monitor.isCanceled())
      {
        return Status.CANCEL_STATUS;
      }

      Element[] elements = null;
      
      synchronized (elementsToReconcile)
      {
        if (!elementsToReconcile.isEmpty())
        {
          elements = new Element[elementsToReconcile.size()];
          elementsToReconcile.toArray(elements);
          elementsToReconcile.clear();
        }
        else
        {
          if (shouldTerminate())
          {
            reconcilerJob = null;
            return Status.CANCEL_STATUS;
          }
        }
      }

      reconcile(elements);

      schedule(DELAY);

      return Status.OK_STATUS;
    }

    private void reconcile(Element[] modifiedElements)
    {
      if (modifiedElements != null)
      {
        for (int index = 0; index < modifiedElements.length; index++)
        {
          Element modifiedElement = modifiedElements[index];

          reconcile(modifiedElement);
        }
      }
    }

    private void reconcile(Element modifiedElement)
    {
      if (modifiedElement != null)
      {
        XSDConcreteComponent concreteComponent = schema.getCorrespondingComponent(modifiedElement);
        concreteComponent.elementChanged(modifiedElement);
      }
    }
    
    private boolean shouldTerminate()
    {
      return timesAwakeAndIdle++ == MAX_INACTIVE_COUNT;
    }
  }
}