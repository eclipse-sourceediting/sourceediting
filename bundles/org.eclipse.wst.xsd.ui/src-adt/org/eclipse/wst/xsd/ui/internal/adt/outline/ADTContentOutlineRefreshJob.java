/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.outline;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.adapters.CategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;


/**
 * This job holds a queue of updates (affected nodes) for the XSD editor's
 * outline view. When a new request comes in, the current run is cancelled, 
 * the new request is added to the queue, then the job is re-scheduled.
 * This class is loosely based on RefreshStructureJob.
 */
class ADTContentOutlineRefreshJob extends Job
{
  /**
   * The delay time in milliseconds.
   */
  private static final long UPDATE_DELAY = 300;

  private final List nodesToRefresh = new ArrayList(5);

  private final TreeViewer viewer;

  public ADTContentOutlineRefreshJob(Viewer viewer)
  {
    super("Refreshing XSD outline"); //$NON-NLS-1$
    setPriority(Job.LONG);
    setSystem(true);
    this.viewer = (TreeViewer)viewer;
  }

  private synchronized void addRefreshRequest(IADTObject adtObject)
  {
    if (nodesToRefresh.contains(adtObject))
    {
      return;
    }

    nodesToRefresh.add(adtObject);
  }

  protected void canceling()
  {
    nodesToRefresh.clear();
    super.canceling();
  }

  private void doRefresh(final IADTObject adtObject)
  {
    final Display display = PlatformUI.getWorkbench().getDisplay();
    display.asyncExec(new Runnable()
      {
        public void run()
        {
          boolean isValidViewer = viewer != null && !viewer.getControl().isDisposed();
          if (isValidViewer)
          {
            viewer.refresh(adtObject);

            // Needlessly revealing the category nodes causes a lot of UI flicker.
            
            if (!(adtObject instanceof CategoryAdapter))
            {
              viewer.reveal(adtObject);
            }
          }
        }
      });
  }

  private synchronized IADTObject[] getNodesToRefresh()
  {
    IADTObject[] toRefresh = new IADTObject [nodesToRefresh.size()];
    nodesToRefresh.toArray(toRefresh);
    nodesToRefresh.clear();

    return toRefresh;
  }

  public void refresh(IADTObject adtObject)
  {
    if (adtObject == null)
    {
      return;
    }

    addRefreshRequest(adtObject);

    schedule(UPDATE_DELAY);
  }

  protected IStatus run(IProgressMonitor monitor)
  {
    IStatus status = Status.OK_STATUS;
    try
    {
      performRefreshes(monitor);
    }
    finally
    {
      monitor.done();
    }
    return status;
  }

  private void performRefreshes(IProgressMonitor monitor)
  {
    IADTObject[] nodes = getNodesToRefresh();

    for (int index = 0; index < nodes.length; index++)
    {
      if (monitor.isCanceled())
      {
        throw new OperationCanceledException();
      }
      IADTObject node = nodes[index];
      doRefresh(node);
    }
  }
}