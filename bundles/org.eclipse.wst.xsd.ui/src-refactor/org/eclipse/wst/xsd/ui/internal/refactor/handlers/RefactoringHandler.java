/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsd.ui.internal.refactor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xsd.XSDSchema;

public abstract class RefactoringHandler extends AbstractHandler
{
  public Object execute(ExecutionEvent event) throws ExecutionException {
    
    IWorkbenchWindow workbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
    if (workbenchWindow == null) return null;

    IWorkbenchPage activePage = workbenchWindow.getActivePage();
    if (activePage == null) return null;

    IEditorPart editor = activePage.getActiveEditor();
    if (editor == null) return null;
    XSDSchema schema = (XSDSchema)editor.getAdapter(XSDSchema.class);
     
    ISelection selection = activePage.getSelection();
    
    return doExecute(selection, schema);
  }

  protected abstract Object doExecute(ISelection selection, XSDSchema schema);
}
