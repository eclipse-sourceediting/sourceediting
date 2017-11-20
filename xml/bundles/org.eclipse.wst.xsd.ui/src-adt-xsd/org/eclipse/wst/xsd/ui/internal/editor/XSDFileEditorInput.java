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
package org.eclipse.wst.xsd.ui.internal.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.xsd.ui.internal.adt.editor.IADTEditorInput;
import org.eclipse.xsd.XSDSchema;

public class XSDFileEditorInput extends FileEditorInput implements IADTEditorInput
{
  private IFile file;
  private XSDSchema schema;
  private String editorName;

  public XSDFileEditorInput(IFile file, XSDSchema schema) {
    super(file);
    if (file == null) {
      throw new IllegalArgumentException();
    }
    this.file = file;
    this.schema = schema;
    editorName = file.getName();
  }

  public IFile getFile()
  {
    return file;
  }

  public XSDSchema getSchema()
  {
    return schema;
  }

  public void setEditorName(String name)
  {
    editorName = name;
  }
 
  public String getName()
  {
    if (editorName != null)
    {
      return editorName;
    }
    return super.getName();
  }
  
  public String getToolTipText()
  {
    if (schema != null)
    {
      String ns = schema.getTargetNamespace();
      if (ns != null && ns.length() > 0)
        return Messages._UI_LABEL_TARGET_NAMESPACE + ns;
      else
        return Messages._UI_LABEL_NO_NAMESPACE;
    }
    return super.getToolTipText();
  }
}
