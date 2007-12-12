/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.editor;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.xsd.XSDSchema;

public class ADTFileStoreEditorInput extends FileStoreEditorInput implements IADTEditorInput
{
  private XSDSchema schema;
  private String editorName;

  public ADTFileStoreEditorInput(IFileStore fileStore, XSDSchema xsdSchema)
  {
    super(fileStore);
    this.schema = xsdSchema;
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
