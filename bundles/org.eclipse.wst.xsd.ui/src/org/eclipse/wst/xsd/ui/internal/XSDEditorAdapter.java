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
package org.eclipse.wst.xsd.ui.internal;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;

public interface XSDEditorAdapter
{
  public IFile getFileResource();

  public IDocument getEditorIDocument();

  public void createTasksInTaskList(ArrayList messages);

  public void resetInformationTasks();
}
