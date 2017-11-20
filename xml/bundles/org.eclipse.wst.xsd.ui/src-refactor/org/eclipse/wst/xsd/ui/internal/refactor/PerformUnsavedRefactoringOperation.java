/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;

public class PerformUnsavedRefactoringOperation implements IWorkspaceRunnable 
{ 
  private ProcessorBasedRefactoring refactoring;
  
  public PerformUnsavedRefactoringOperation(ProcessorBasedRefactoring refactoring)
  {
    this.refactoring = refactoring;
  }
  
  public void run(IProgressMonitor pm)
  {
    if (pm == null)
    {
      pm = new NullProgressMonitor();
    }  
    try
    { 
      refactoring.checkAllConditions(pm);
      Change change = refactoring.createChange(pm);   
      if (change instanceof CompositeChange)
      {
        CompositeChange compositeChange = (CompositeChange)change;
        setSaveMode(compositeChange);              
      }  
      change.perform(pm);         
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void setSaveMode(CompositeChange composite)
  {
    Change[] children = composite.getChildren();
    for (int i = 0; i < children.length; i++)
    {
      Change child = children[i];
      if (child instanceof TextFileChange)
      {
        ((TextFileChange)child).setSaveMode(TextFileChange.LEAVE_DIRTY);
      } 
      else if (child instanceof CompositeChange)
      {
        setSaveMode((CompositeChange)child);
      }  
    }  
  }
}  